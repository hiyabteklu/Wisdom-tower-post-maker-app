# Wisdom Tower Post: UI and UX plan

Written 31 Oct 2026. This builds on IMPROVEMENT_PLAN.md (the renderer, brand kit and 33 templates now exist). Nothing here removes a feature. It changes where the features live, how they are reached, and what the app remembers.

## 1. Why this app exists

Canva Pro is on the phone, but it is slow on a weak connection and awkward to use with a thumb. This app has to beat it on four things:

1. **Fast.** Fully offline. A post from opening the app to a saved file in under a minute.
2. **Ready.** Every template looks finished the moment it opens. No empty states.
3. **Consistent.** Every template remembers how it was last left, and the brand (logo, footer, colors) is the same everywhere.
4. **Thumb-friendly.** No long vertical forms. No typing color codes.

## 2. What is wrong today

Found by reading PostStudio.kt, PostStore.kt, PostControls.kt, PostFooterEditor.kt and PostApp.kt.

1. **The editor is a long vertical form.** Below the preview there is one scrolling column: Words, Fonts, Look, Background image, Photo, Logo and footer, Social icons, Save. The Look card alone holds two swatch rows, two hex fields, three chips, three more hex fields and a slider. You scroll a lot and lose your place.
2. **Color codes.** `PostHexField` is used for accent, text color, background start and end, gradient end, and every footer icon color. Circles already exist next to them, so the typed code is the worst way to do it.
3. **Only one post is remembered.** `PostStore` keeps a single `doc`. Switching template copies your words across (`withTemplate`) and keeps everything else, so a template never comes back the way you left it.
4. **Thumbnails are rendered on every cold start.** The template strip uses an in-memory cache of 48 items. Each app launch re-renders the ones you scroll past.
5. **No way to mark favorites,** and no way to see which templates you have edited.
6. **No custom icons.** Footer icons are a fixed set. You cannot drop your own small graphic on a post.
7. **Dead code.** `MainActivity.kt` still holds the first prototype (`WisdomTowerPostApp`, four fake templates, PDF export). It is never shown. `PostApp` also still opens the old layer studio.

## 3. Rules the new UI must obey

These are the checks for every screen.

1. **One category is one horizontal row.** Buttons, options, swatches, chips, templates, icons: all slide sideways. Nothing stacks into a long vertical list.
2. **The editor never scrolls vertically.** The preview stays put. Only the Library home scrolls up and down, and only between rows.
3. **No color codes anywhere.** You see a circle, you tap it. Colors are stored as hex inside the file, and the UI never shows a hex.
4. **The bottom area never changes height** when you switch category, so the preview does not jump.
5. **Everything common is two taps or less:** change color, change size, swap template, save.
6. **Thumb zone.** Category tabs and options sit at the bottom. The preview sits at the top.
7. **Tap targets at least 48 dp.** Selected state is a ring, not a color change alone.
8. **Offline.** No network call anywhere. Fonts, icons and templates are bundled or stored on the phone.

## 4. Screens

### 4.1 Library (home)

A page of horizontal rows. Only the page itself scrolls vertically.

- **Continue** (templates you edited, newest first, with their own edited thumbnail)
- **Saved** (your bookmarked templates)
- One row per family: Quote, Motivation, Question, Exam, Study tip, Subject, Promotion, Reminder, Achievement, Announcement, Social, Event, Fact

A card shows the template at the current size. If you edited it, the thumbnail is **your edited version**, with a small dot. A bookmark icon sits on the card corner. The size switch (1:1, 4:3, 9:16) sits at the top.

Bookmarking moves a template into **Saved**. Removing the bookmark sends it back to its family row. (Default assumption, see question 1.)

### 4.2 Editor

```
+--------------------------------------+
| < Library   Template name   bookmark |  top bar: back, name, bookmark, reset, Save
|  [1:1] [4:3] [9:16]        reset  Save|
+--------------------------------------+
|                                      |
|            LIVE PREVIEW              |  about 45% of the screen, fixed
|        (tap a text to edit it)       |
|                                      |
+--------------------------------------+
|  ----o---------   Zoom               |  one shared slider, only when a control needs it
+--------------------------------------+
|  Words  Look  Photo  Icons  Logo ... |  category tabs, one row, slides sideways
+--------------------------------------+
|  [option] [option] [option] [option]>|  options for that category, one row, fixed height
+--------------------------------------+
```

- **Words:** one chip per text slot of this template (Label, Headline, Message, List, Number, Button). Each chip shows the current text. Tap a chip, or tap the text on the preview, and one field opens above the keyboard. The preview shrinks to stay visible. Also here: **Bring my words**, which copies the words from the last template you edited (this keeps today's "words stay when you switch" feature, now as a choice).
- **Look:** Style (Clean or Rich), Text size (S, M, L), Color pairs, Main color, Text color, Background (Template, Solid, Gradient). Each of these is a row of circles or chips. Gradient asks for From and To as circle rows, and Direction as eight arrow chips.
- **Photo:** Pick, Replace, Remove, then Zoom, Left-right, Up-down, Darkness. Plus Background image with Opacity. See the shared slider below.
- **Icons:** your icon library (section 6).
- **Logo:** Show, Corner, Size S/M/L, Replace, Move on preview.
- **Footer:** Show, Style (Line or Pill), Icon look (Plain, Badge, Official), Icon size, and each footer item as a chip you tap to edit its text and color circle.
- **Font:** English and Amharic font chips, plus Add font from phone.

**The shared slider.** Today there are about ten sliders spread over the page. Instead, controls like Zoom, Left-right, Up-down, Darkness, Opacity and Icon size are chips in the row. Tapping one makes the single slider above the tabs control that value. Every slider feature stays, none of them stack.

**Direct handling on the preview.** Drag the logo (already works). Drag and pinch an icon. Photo gets pinch-zoom and drag as a later bonus, with the chips as the fallback.

### 4.3 Brand

Same rule: horizontal categories (Logo, Colors, Fonts, Footer, Icons, About). Brand colors are picked from circle rows, not typed. Brand edits apply to every template that has no color of its own.

### 4.4 Save

A bottom sheet from the Save button. One row of sizes (tick one or all three), one row of format (PNG, JPG), then Save and Save and share. The overflow warning ("text had to shrink in Headline") stays.

## 5. Every template remembers its last edit

This is the center of the plan.

**What is saved per template:** words, colors, background, text size, Clean or Rich, photo and its zoom and position, background image, fonts, icons placed on it, logo and footer switches, and the size you last used it at.

**What is not saved per template:** brand settings (logo file, corner, brand colors, footer items). Those are global, so changing the brand updates every template at once. A template that still uses "brand color" (no override) follows the brand automatically.

**Behavior**

- Open a template: it appears exactly as you last left it. First time ever: its sample text in brand colors.
- Autosave about one second after the last change. No Save button for drafts.
- **Reset** (top bar): back to the template's factory look with brand colors and sample words. It shows a snackbar with **Undo** for 8 seconds. No confirmation dialog.
- A dot on the card marks "edited". Reset removes the dot.
- Photos and icons are copied into app storage, so drafts never break. Reset releases the files that nothing else uses.
- Switching size keeps the same draft. The size is part of the draft.

**Storage**

- One file per template: `filesDir/drafts/<templateId>.json`, written with the existing `PostCodec` plus the new icons list.
- `filesDir/library.json`: bookmarked ids, last-opened template, last-edited times.
- `brand.json` stays as is (moved out of SharedPreferences only if needed).
- First launch after the update: the current single `doc` becomes the draft of its template, so nothing is lost.

## 6. Icon library (your own icons)

Goal: drop a small graphic on a post, size it, done. Not a graphics editor.

- **Add:** Icons tab, first tile is **+ Add**. Pick one or many files from the phone. They are copied into app storage and show up in a row called **My icons**.
- **Use:** tap an icon. It appears in the center at a sensible size.
- **Resize and move:** pinch on the preview, or choose **Size** and use the shared slider. Drag to move.
- **Change your mind:** **Remove** and **Front or back** are the only other actions. Maximum 6 icons per post.
- **Match color:** a toggle that tints a single-color icon with a circle you pick, so a black icon can match the brand. Off by default.
- **Starter shapes:** a small bundled pack of flat geometric shapes (diamond, triangle, hexagon, ring, plus, arrow, check) so there is something to use before you upload anything. No sparkles.
- **File types:** PNG, WebP and JPG at first. SVG is a possible later addition (question 3).
- **Rendering:** icons are drawn by `PostRenderer`, so the preview and the exported file match. They are saved in the template's draft.
- **Footer icons stay separate.** The brand footer icons (Telegram, Website, TikTok and so on) are not part of this library.

## 7. Colors without codes

- Remove all `PostHexField` use in `PostStudio.kt` and `PostFooterEditor.kt`, and any hex field in the Brand screen.
- Palette rows (each row is one horizontal strip): **Brand** (the four brand colors), **Pairs** (the existing two-tone pairs), **Bright**, **Soft**, **Dark**, **Neutral**. About 8 circles each.
- Footer item color, brand colors, text color, gradient From and To, and icon tint all use the same circle rows.
- A circle that means "use the brand color" is always first and is marked, so you can return to the brand in one tap.
- Stored values stay hex strings in the JSON, so old drafts keep working.

## 8. Speed

1. Open directly into the last template you edited, with the Library one tap away (question 2).
2. **Thumbnail disk cache.** Library cards are rendered once per (template, size, brand, draft version) and saved as small WebP files. The next launch shows them instantly. A card is re-rendered only when its draft or the brand changes.
3. **Two-speed preview.** While you drag a slider, an icon or the logo, render at about 540 px wide. Render at 960 px about 150 ms after you stop. Export always uses full size.
4. Render off the main thread (already done) and drop stale renders when a newer one starts.
5. Keep fonts, icons and templates bundled. Check that the manifest has no INTERNET permission.

## 9. Where every existing feature goes

Nothing is dropped. Everything on the left has a place on the right.

| Feature today | New place |
|---|---|
| Size chips 1:1, 4:3, 9:16 | Top bar of the Editor and Library |
| Template strip | Library rows, plus a Saved row; quick swap stays inside the Editor via the back arrow |
| Words (one text field per slot) | Words tab, one chip per slot, one field above the keyboard |
| "Your words stay when you switch template" | **Bring my words** button |
| Fonts (English, Amharic, add from phone) | Font tab and Brand |
| Clean or Rich decoration | Look tab, Style chips |
| Text size S, M, L | Look tab |
| Color pairs | Look tab, Pairs row |
| Main color, text color | Look tab, circle rows (no hex) |
| Background: Template, Solid, Gradient, angle | Look tab; circle rows plus eight direction chips |
| Background image and opacity | Photo tab; opacity on the shared slider |
| Template photo: pick, zoom, move, darkness | Photo tab; sliders become chips on the shared slider |
| Show logo, corner, size, replace, move | Logo tab and Brand |
| Show footer, footer style, icon look, icon size | Footer tab and Brand |
| Footer items: kind, label, color | Footer tab, one chip per item; color by circle |
| Social poster icons | Same data, edited in Footer tab |
| Overflow warning on the preview | Stays on the preview |
| Save: sizes, PNG/JPG, share | Save sheet |
| Old layer studio | Behind Brand > About until the new editor is accepted, then removed (question 5) |
| (new) Per-template memory, Reset | Editor top bar, Library |
| (new) Bookmarks and Saved row | Library, Editor top bar |
| (new) Icon library | Icons tab |

## 10. Order of work

Each phase ends with a debug APK. A phase is done only when its check passes on your phone. CI stays as it is: the build runs only when started through the API, so do not add rules that run on push. The assistant cannot compile or run the app, so the build and test come from you before the next phase starts.

**Phase 1: memory.**
Per-template draft files, `library.json`, migration of the single doc, Reset with Undo, edited dot data.
*Check:* edit templates A and B, switch back and forth, kill the app, reopen. Each is exactly as left. Reset returns to factory and Undo brings the edit back. An old install keeps its current post.

**Phase 2: horizontal editor.**
New Editor layout: fixed preview, shared slider, category tabs, one-row options. All current controls moved over using the table in section 9. Remove the vertical scroll.
*Check:* go down the table in section 9. Every row is reachable. No vertical scroll in the Editor. Switching tabs does not move the preview.

**Phase 3: colors by circle.**
Palette rows, "use brand" circle, gradient From, To and eight direction chips. Remove all hex fields.
*Check:* search the screens for any `#` prompt: none. Old drafts with typed colors still render.

**Phase 4: Library.**
Library home with Continue, Saved and family rows, bookmark button, edited dot, disk thumbnail cache, open into last template.
*Check:* second launch shows the Library thumbnails with no blank cards. Bookmark a template: it moves to Saved. Un-bookmark: it returns.

**Phase 5: icons.**
Import, My icons row, place, pinch and drag, Size on the shared slider, Remove, Front or back, Match color, starter shapes, saved in drafts.
*Check:* add 3 icons, place two on a template, leave and return: they are where you left them. The exported PNG matches the preview.

**Phase 6: speed and cleanup.**
Two-speed preview, stale render drop, delete the dead prototype in `MainActivity.kt`, remove or hide the old layer studio, offline check.
*Check:* dragging an icon feels smooth on your phone. The manifest has no INTERNET permission.

## 11. Questions to settle (defaults are assumed)

1. **Bookmarked templates:** the plan moves them out of their family row into Saved. Do you want them to also stay in their family row?
2. **Opening the app:** the plan opens straight into the last template you edited. Do you prefer opening on the Library?
3. **Icon files:** the plan accepts PNG, WebP and JPG. Do you also need SVG files? That adds a small offline library.
4. **Reset:** the plan resets words, colors, photo and icons of that one template, and never touches the brand. Is that right?
5. **Old layer studio:** remove it after Phase 2, or keep it hidden in Brand > About for a while?

## 12. Out of scope

Custom color picker or eyedropper, layers panel, rotation, text effects, PDF or A4 export, cloud sync, accounts. If you ever need an exact color, add it later as a visual picker, never as typed text.
