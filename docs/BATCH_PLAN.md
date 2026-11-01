# Wisdom Tower Post: batch plan

Written 1 Nov 2026 by Claude, after reading the code on `main` (commit `0c51399`). For any assistant that continues this work, and for Hiyab.

## 1. Goal

Make 10 to 20 posts from one template in one go. Paste a list of words, press one button, get a folder of finished images. Offline, no server, no new libraries.

This is the speed feature the app exists for (see UI_UX_PLAN.md section 1). Today every post needs the template opened, the words typed and Save pressed. Batch does all the typing in one place and all the saving in one press.

Decided with Hiyab on 1 Nov 2026: no MCP server, no Supabase, no network, no accounts. The app must not drift into a Canva copy. Only add what saves time on repeat posts.

## 2. Rules for any assistant working on this

1. Work on `main` only (CLAUDE.md). No branches, no pull requests.
2. Read the latest copy of every file from `main` before editing it.
3. Nobody here can compile. After each push say plainly: 'not compiled, please build'. Hiyab builds on GitHub: Actions, 'Build Wisdom Tower Post Release APK', Run workflow.
4. Push in small slices (section 8). Each slice must compile on its own and must not change how the Save dialog works today. Tick the slice in the Status list (section 9) in the same push.
5. No new dependency. Facts: Kotlin 2.0.21, AGP 8.7.3, Compose BOM 2024.12.01, minSdk 26, `material-icons-extended` is already there.
6. Do not change the post file format (`PostCodec`, version 4). Batch never writes drafts and never changes the open post.
7. Do not touch the templates or `PostRenderer`. Batch only calls them.
8. UI words are plain and short. Never show a color code. Tap targets 48 dp or more, labels 13 sp or more.
9. Never guess a name from this repo. Open the file and copy the real name.

## 3. What the code does today (read on 1 Nov 2026)

- `PostDoc` (PostModel.kt) is one post. `texts: Map<String, String>` holds the words by slot name. Everything else on the post (colors, photo, logo, fonts, per-box styles, dragged positions) is in the same object, so `doc.copy(texts = ...)` keeps the whole design.
- Slot names are in `PostSlots`: `eyebrow`, `headline`, `body`, `number`, `list`, `button`. `PostSlots.label(slot)` gives the name shown to the user: 'Small label', 'Headline', 'Message', 'Big number or code', 'List (one item per line)', 'Button text'.
- The editable slots of a template at a size are worked out in `PostStudioScreen` like this: take `postTemplate(doc.templateId).layout(doc.size).slots`, drop the ones that have a `literal`, keep the slot names, `distinct()`. That order is the order of the Words section.
- A list slot (`ListStyle` ROW, DOT, LETTER, NUMBER or CHECK) is one text with one item per line. ROW lines look like `Mon: Physics`.
- `PostExport.save(context, doc, brand, sizes, png, fourK)` (PostStore.kt) renders each size with `PostRenderer.render(context, doc.copy(size = size), brand, scale)`, writes it to Pictures/Wisdom Tower Post, recycles the bitmap, and returns `Saved(uris, overflow)`. `overflow` lists the slots whose text had to shrink.
- The file name is `WisdomTower_<size>[_4K]_<yyyyMMdd_HHmmss>.<ext>`. The stamp has only seconds, so two posts saved in the same second get the same name. On Android 10 and newer MediaStore renames the second one; on older Android the file is overwritten. Batch must fix this (slice B1).
- `PostStudio.kt` has a private `SaveDialog`: tick sizes, Standard or 4K, PNG or JPG, Save, Save and share. It runs the export inside `scope.launch { withContext(Dispatchers.Default) { ... } }`. It forgets its choices every time it opens.
- `PostApp.kt` holds `doc`, `brand`, `library` and a `screen` string ('studio', 'library', 'brand'). Each screen is wrapped in `PostTheme { ... }` and draws with `PostPalette`.
- The studio top row (`TitleRow` in PostStudio.kt) has bookmark, Library and Brand icon buttons. Batch gets its own icon button there.
- `PostRenderer.preview(context, doc, brand, width) { cancelled }` returns a bitmap and the overflow list. The studio uses it for the live preview.

## 4. Step 0: build before adding anything

REDESIGN_PLAN.md says nothing since the engine push has been compiled. On 1 Nov 2026 Claude also pushed a change that is not compiled either (commit `0c51399`): new app icon, navy and turquoise theme, and a Gradle task in `app/build.gradle.kts` that copies `new_logo.png` from the repo root into the app resources as `launcher_logo`.

Before slice B1:

1. Run the build. If it fails, fix the build first. Nothing else.
2. If the failure is in the `prepare...LauncherLogo` task or in resource linking about `launcher_logo`: remove the `PrepareLauncherLogo` class and the `androidComponents` block from `app/build.gradle.kts`, and ask Hiyab to move `new_logo.png` to `app/src/main/res/drawable-nodpi/launcher_logo.png` (on github.com, edit the file name and type the folder path in front of it). Nothing else needs to change.
3. Install and look: the app opens, the icon is the new logo (black square, two white bars, turquoise dot), the screens are navy with turquoise buttons, and an old draft still opens.

## 5. The feature

### 5.1 What Hiyab sees

A new icon button in the studio top row opens the Batch screen. The batch is made from the post that is open right now: same template, colors, photo, logo, fonts and per-box styles. Only the words change from post to post.

```
< Batch                                   Clear
Editorial quote            (the open template)
[1:1] [4:5] [4:3] [9:16]   sizes to save, tick any
[PNG] [JPG]   [Standard] [4K]
Words:  ( ) One line = one post   ( ) Full blocks
Fill:   [Headline]         (one-line mode only)
Boxes in order: Small label, Headline, Message   (blocks mode only)
+----------------------------------------+
| text box, at least 8 lines             |
+----------------------------------------+
[Paste]  [Example]                  12 posts
Preview   [<]  Post 3 of 12  [>]
+--------+
|preview |    gold chip when text shrinks
+--------+
Notes (only when there are any)
[ Make 12 posts (36 files) ]
Saving 4 of 12                    [Cancel]
Saved 36 files to Pictures/Wisdom Tower Post/Batch 2026-11-01 14-30-12   [Share] [Close]
```

This screen scrolls up and down (like the studio). Everything on it is plain chips, one text box and buttons.

### 5.2 Two ways to write the list

**One line = one post.** The fastest way for quotes, tips, questions. Every non-blank line is one post. The line goes into the box chosen in 'Fill' (default Headline, or the first box if the template has no Headline). The other boxes keep the words of the open post, so a fixed label such as STUDY TIP stays on every post. Lines made only of dashes are ignored.

**Full blocks.** For posts with more than one box. Posts are separated by a line with three or more dashes and nothing else. Inside one post there are two ways to write, and a post uses the first way if it has any key line:

1. *Key lines.* A line is a key line when, after leading spaces, it starts with one of the key words below (capitals do not matter), then optional spaces, then a colon. The rest of that line is the value of that box. Lines after it that are not key lines continue the value on new lines (this is how a list is written). A key line with nothing after the colon starts the value on the next line. A key line with no value at all and no lines after it empties that box.
2. *Plain lines.* Blank lines are dropped. Line 1 goes to the first box, line 2 to the second, and so on, in the order shown on the screen under 'Boxes in order'. A line with only `-` skips that box (it keeps the words of the open post). If the template has a list box, the list box takes all the remaining lines. If there are more lines than boxes and no list box, the extra lines are added to the last box on new lines and the post gets a note.

In both ways: `{n}` anywhere in a value is replaced by the post number (1, 2, 3 ...). Values are trimmed. Boxes that are not mentioned keep the words of the open post.

Key words:

| Box | Accepted keys |
|---|---|
| eyebrow | label, eyebrow, small label |
| headline | headline, title |
| body | message, body |
| number | number, code, big number |
| list | list |
| button | button, button text |

A key for a box the template does not have is ignored, with a note. A catch: inside a list, a line that begins with one of these key words and a colon would be read as a new key. Write that item differently.

At most 50 posts per run (`PostBatch.MAX_POSTS`). Extra posts are ignored, with a note.

### 5.3 Examples (the parser must give exactly these results)

**A. One line = one post, Fill = Headline.**

```
Small steps still move you forward.
Read the question twice.

---
Sleep is part of studying.
```

Result: 3 posts. Headline = each line. Label and Message keep the open post's words.

**B. Full blocks, plain lines, template with Small label, Headline, Message.**

```
STUDY TIP
Sleep before the exam
Eight hours beats an all-nighter.
---
-
Past papers first
Do last year's paper before new notes.
```

Result: 2 posts. Post 1: Small label 'STUDY TIP', Headline 'Sleep before the exam', Message 'Eight hours beats an all-nighter.' Post 2: Small label unchanged, Headline 'Past papers first', Message 'Do last year's paper before new notes.'

**C. Full blocks, key lines, a list.**

```
label: EXAM WEEK
headline: Physics checklist
list:
Formulas sheet
Past paper 2023
Calculator batteries
---
label: EXAM WEEK
headline: Chemistry checklist
list:
Periodic table
Lab notes
```

Result: 2 posts. Post 1 list value is the three lines joined with line breaks (no empty first line).

**D. A timetable list (ROW style).**

```
headline: Week {n}
list:
Mon: Physics
Tue: Chemistry
Wed: Biology
```

Result: 1 post. Headline 'Week 1'. The lines `Mon: Physics` and so on are NOT key lines (Mon is not a key word) and stay inside the list.

**E. Mixed.** A key line and plain lines in the same post: the post counts as a key-line post, and the plain lines before the first key line are ignored with a note ('Post 2: lines before the first label were ignored').

### 5.4 Edge cases

| Case | What happens |
|---|---|
| Empty box | Make posts is off, hint 'Paste or type your words' |
| Windows or old Mac line endings, tabs | Turned into plain line breaks and single spaces before parsing |
| Amharic or mixed text | Passed through untouched. Check on the phone that the preview draws it |
| A very long line | The renderer shrinks the text. The preview shows the gold 'Text shrunk' chip; B4 checks all posts at once; the final summary lists the posts that shrank |
| The open post has a photo | Every post uses that same photo. Say so in a small hint. Different photos per post is slice B5 |
| The app goes to the background in the middle of a batch | It may stop. The summary says how many files were saved. Running again makes a new folder |
| Storage full or the export throws | Stop, show 'Stopped at post N: <message>', keep what was saved |
| Cancel pressed | The post being drawn finishes, then it stops and shows the summary |
| Two batches in the same minute | The folder name has seconds, so no clash |
| A box emptied on purpose with an empty key line | Check on the phone that an empty box leaves no stray shape; if it does, note it in Status |

### 5.5 Files and folder

- One folder per batch: `Pictures/Wisdom Tower Post/Batch yyyy-MM-dd HH-mm-ss`. No colons or slashes in the folder name.
- File name: `WisdomTower_<size>[_4K]_<stamp>_<NN>.<ext>`. `NN` is the post number, zero padded to the width of the post count (01 to 12, or 01 to 20). The stamp is the same for the whole batch, so files sort together.
- Android 10 and newer: `MediaStore` with `RELATIVE_PATH` set to the folder. Older Android (minSdk is 26): the app's own pictures folder with the same sub-folder.

## 6. How to build it

### 6.1 New file `PostBatch.kt` (pure Kotlin, nothing from Android in the parser)

```kotlin
enum class BatchMode { ONE_LINE, BLOCKS }

data class BatchItem(
    val number: Int,                  // 1-based
    val texts: Map<String, String>,   // only the boxes this post sets
    val notes: List<String>           // plain-words problems for this post
)

data class BatchParse(val items: List<BatchItem>, val notes: List<String>)

object PostBatch {
    const val MAX_POSTS = 50
    fun parse(raw: String, mode: BatchMode, slots: List<String>, oneLineSlot: String): BatchParse
    fun apply(base: PostDoc, item: BatchItem): PostDoc   // base.copy(texts = base.texts + item.texts)
    fun exampleFor(slots: List<String>, mode: BatchMode): String
}
```

`slots` is the editable slots of the open template at the open size, in the order of the Words section (section 3). The studio computes this inline today; put the same few lines in one small shared function (for example `editableSlots(layout)`) and use it in both places, without changing what the studio does.

Parser steps:

1. Turn Windows and old Mac line endings into plain line breaks and tabs into one space. Split into lines.
2. ONE_LINE: every line that is not blank and not only dashes becomes one post with `oneLineSlot` set to the trimmed line. Go to step 7.
3. BLOCKS: split the lines into posts at every line that is only three or more dashes. Remove blank lines at the start and end of each post. Skip posts that end up empty.
4. A post with at least one key line (section 5.2) is read with key lines. Otherwise it is read with plain lines.
5. Key lines: walk the lines. A key line starts a new value for the box that key names (look it up in the key table, ignoring capitals). Any other line is added to the current value after a line break; if the current value is still empty, it simply becomes the value (no empty first line). Before the first key line, other lines are ignored with a note. A key for a box not in `slots` is ignored with a note, and the lines that follow it are ignored too.
6. Plain lines: drop blank lines. Go through `slots` in order and hand out lines. A line that is only `-` skips its box. If the current box is the list box, it takes all remaining lines (joined with line breaks) and the handing out stops. If lines are left after the last box, add them to the last box on new lines and add a note.
7. Replace `{n}` by the post number in every value, and trim every value. Cap the list at `MAX_POSTS` and add a note if posts were dropped. Return items and notes.

No parse step may throw. Any line is valid input.

### 6.2 Export change in `PostStore.kt` (PostExport)

Add one function and keep `save` behaving exactly as today:

```kotlin
fun saveOne(
    context: Context, doc: PostDoc, brand: PostBrand, sizes: List<PostSize>,
    png: Boolean, fourK: Boolean,
    stamp: String, tag: String = "", subfolder: String? = null
): Saved
```

- `save(...)` creates a stamp the way it does now and calls `saveOne` with no tag and no sub-folder. The Save dialog must give the same file names as before.
- `tag` is appended to the file name after the stamp (`_03`) when it is not empty.
- `writeImage` gets `subfolder: String?`. Android 10 and newer: `RELATIVE_PATH` is `Pictures/Wisdom Tower Post` plus `/` plus the sub-folder when there is one. Older: `File(folder, subfolder)` with `mkdirs()`.
- Keep the existing rules: one bitmap at a time, `recycle()` after writing, overflow collected per size.

### 6.3 New screen `PostBatchScreen.kt`

```kotlin
@Composable
fun PostBatchScreen(doc: PostDoc, brand: PostBrand, onBack: () -> Unit)
```

- Wrap in `PostTheme { Scaffold(containerColor = PostPalette.Ink) { ... } }` like the other screens.
- State: the text, the mode, the Fill slot, the save options, the preview index, and for the run: `running`, `done`, `total`, a list of problems, the result.
- `slots` from the open doc (section 6.1). `parse` is computed with `remember(text, mode, slots, oneLineSlot)`.
- Preview: `PostBatch.apply(doc, items[index])` then `PostRenderer.preview(context, itemDoc, brand, 540) { !isActive }` inside `LaunchedEffect(itemDoc, brand)`, the same way `PreviewPane` does it. Show the gold 'Text shrunk to fit' chip from the overflow list like `PreviewPane` does.
- Make posts: a `scope.launch` on the main dispatcher that loops over the items one at a time. For each item: `ensureActive()`, then `withContext(Dispatchers.Default) { PostExport.saveOne(..., tag = number padded) }`, then update `done` on the main thread. Keep the `Job` so Cancel can call `cancel()`. Wrap each item in `runCatching`; on the first failure stop and show 'Stopped at post N: message'.
- One run uses one stamp and one sub-folder name, made when Make posts is pressed.
- Never render two bitmaps at the same time. A 4K 9:16 bitmap is about 33 MB.
- While running: `LocalView.current.keepScreenOn = true`, set back to false in a `DisposableEffect` when it stops or the screen closes. Show 'Keep the app open until it is done'. No foreground service in the first version.
- Result: files saved, posts that shrank text (post number, size, box names), and Share (`ACTION_SEND_MULTIPLE`, the same way `SaveDialog` does it) only when there are 30 files or fewer; otherwise say where the folder is.
- `SaveDialog` is private to PostStudio.kt. Do not refactor it in the first slices; copy the small chip rows. A shared options composable can come later, after the build works.

### 6.4 Wiring

- `PostApp.kt`: add a `when` branch `"batch"` next to `"library"` and `"brand"`, with `BackHandler { screen = "studio" }`, calling `PostBatchScreen(doc = doc, brand = brand, onBack = { screen = "studio" })`.
- `PostStudioScreen`: add `onOpenBatch: () -> Unit = {}` (a default, so nothing else breaks) and pass it to `TitleRow`, which gets one more `IconButton`. Use an icon that already exists in material-icons-extended, for example `Icons.Outlined.Layers`. Content description 'Batch'.

### 6.5 Remembering things (`PostBatchStore`, in PostBatch.kt or its own file)

- SharedPreferences file `wisdom_tower_post_batch`. Keys: the text per template id, the mode, the Fill slot per template id.
- Save the text about 500 ms after typing stops (the same debounce pattern as `PostApp`), so 20 typed lines survive a crash or a phone call.
- Clear button empties the text for this template.
- Save choices (sizes, PNG or JPG, Standard or 4K) are one small `SaveOptions` object stored by `PostStore` and used by both the Save dialog and the Batch screen. First launch: the open size, PNG, Standard (what the Save dialog does today).

## 7. Compile-safety rules for this repo

- Open the real file and copy names. `PostSmallLabel` is used in `SaveDialog` but not defined in PostStudio.kt: find where it lives before using it. `ControlHeading` and `SettingsSection` are private to PostStudio.kt and cannot be used from another file.
- Every new composable needs its imports written out. Check each one.
- A `when` on a string needs an `else` branch.
- Do not write to Compose state from a background thread: update state on the main thread.
- Strings with braces: `{n}` is plain text in Kotlin strings. A dollar sign starts a template, so write `${'$'}` if a dollar is ever needed.
- `resourceConfigurations` limits strings to English and Amharic; use plain Kotlin strings in code, not new resource files, for the UI words of this feature.

## 8. Slices (push in this order, one at a time)

**B0. Save choices stick.** `SaveOptions` and `PostStore.loadSaveOptions` / `saveSaveOptions`. `SaveDialog` starts from them and saves them when Save or Save and share is pressed. Check: tick 1:1 and 9:16, JPG, 4K, save, close, reopen: same choices. A fresh install shows today's defaults. No other change.

**B1. Parser and export, no screen.** `PostBatch.kt` (`parse`, `apply`, `exampleFor`, `editableSlots` helper used by the studio too) and `PostExport.saveOne` with the sub-folder and tag. Check: Save dialog file names are the same as before. Because nobody can run code, the assistant must write by hand the expected result of examples A to E (section 5.3) into this file under Status as a short trace, and compare it with what its code would do.

**B2. Screen with one-line mode and Make posts.** `PostBatchScreen.kt`, the icon in the studio, the route in `PostApp`. Text box, Paste, Example, save options, Fill, preview with the stepper, Make posts, progress, Cancel, result. Blocks mode can be a disabled chip that says 'soon'.

**B3. Full blocks.** Key lines, plain lines, `-` skip, `{n}`, the notes list, remembered text and mode (`PostBatchStore`), Clear.

**B4. Check and polish.** A 'Check text fit' button that renders small previews (360 px wide, one after another, off the main thread) and lists the posts that shrink ('Post 4: Headline'). Share the result. Keep the screen on. Tidy wording.

**B5. Photos in order (optional).** Pick several photos with `ActivityResultContracts.OpenMultipleDocuments`, copy each with `PostStore.importPhoto`, give them to the posts in order (`photoUri`). Only offered when the template has a photo area. Delete the copied photos after the run unless the open post uses them.

## 9. Status

- [ ] Step 0: build passes, icon and theme checked on the phone
- [ ] B0 Save choices stick
- [ ] B1 Parser and export
- [ ] B2 Screen, one-line mode, Make posts
- [ ] B3 Full blocks, notes, remembered text
- [ ] B4 Check text fit, share, polish
- [ ] B5 Photos in order (optional)

Notes from assistants go below this line, newest first, one or two lines each.

## 10. Phone test (Hiyab)

After B2 and again after B3:

1. Open a template, tap the Batch icon. Paste example A. The count says 3 posts. Step through the preview: each shows its own headline and the same label.
2. Tick two sizes, press Make posts. The count of files is posts x sizes. Progress moves. The summary names a folder.
3. Open the gallery folder: files are in order (01, 02, 03), none missing, none overwritten, sizes right.
4. Make a post with a very long line. The preview shows the gold chip. The summary lists that post.
5. Press Cancel in a big run: it stops after the current post and the summary says how many were saved.
6. Type three lines, leave the app, come back: the text is still there (after B3).
7. Paste Amharic words: the preview draws them with the Amharic font.
8. Go back to the studio: the open post is exactly as it was, and no draft changed.

## 11. Review checklist (for Claude, or anyone checking the work)

The implementing assistant answers after every push with: files changed and one line why for each, what is not done, anything it was unsure about, and the sentence 'not compiled, please build'. The reviewer checks:

1. Only the files the slice needs were touched. Templates, renderer and `PostCodec` untouched.
2. `build.gradle.kts` has no new dependency.
3. Every name used exists in the repo (open the files). Every import is present. Every `when` has `else`.
4. `PostExport.save` still gives the same file names as before. `saveOne` adds the tag and sub-folder only when given.
5. The parser never throws, handles empty input, handles `---` at the start and end, and gives the results in section 5.3 for A to E. Trace each by hand.
6. The batch never changes `doc` in `PostApp`, never writes a draft, never calls `PostDrafts`.
7. One bitmap at a time. `recycle()` after each write. Cancel is checked between posts.
8. State is updated on the main thread. `keepScreenOn` is reset when the run ends or the screen closes.
9. UI words are plain, no color codes, 48 dp targets, no network or internet permission added.
10. The Status list in this file is ticked and the push is on `main`.

## 12. Prompt to paste into ChatGPT (or any assistant)

```
You are continuing work on a private Android app (Kotlin, Jetpack Compose) in the GitHub repo hiyabteklu/Wisdom-tower-post. Read CLAUDE.md and docs/BATCH_PLAN.md first. Then do exactly one slice from section 8: the first one that is not ticked in section 9. Work on main only. You cannot compile, so say so. Do not add dependencies. Open every file you edit from main first and keep every name exactly as in the repo. When done, push, tick the slice in docs/BATCH_PLAN.md, and answer with the report described in section 11. Do not start the next slice. If you cannot read the repo, ask Hiyab to paste these files: PostModel.kt, PostStore.kt, PostApp.kt, PostStudio.kt, PostTemplates.kt, PostTheme.kt.
```

## 13. Later (not now)

- A different template per post (a `template:` key line).
- Saved word lists that can be reused.
- A text file with the words of every post, for captions.
- 4:5 layouts of their own (already listed in REDESIGN_PLAN.md).
