# Template redesign plan (urgent item 6)

Written 15 Nov 2026 by the assistant (Claude) with the owner, from the real code (`PostTemplates*.kt`, `PostRenderer.kt`, `PostEngine.kt`, `PostModel.kt`, `PostTextStyle.kt`, `docs/TEXT_STYLE.md`, `docs/URGENT_PLAN.md`) and the owner's phone screenshots of the editor. The assistant cannot compile. **Nothing in this file is built yet.**

Scope: every poster template EXCEPT the Chapter summaries (`study-*`, they are not touched). Standing rules stay: work on `main` only (CLAUDE.md), the logo header design is not touched (URGENT_PLAN decisions), no hex boxes (color circles only), no waves, rings, dot grids, stripes or gems.

---

## 0. INSTRUCTIONS FOR GOOGLE AI STUDIO (read this first)

The owner decided (15 Nov 2026): **do the whole plan in ONE run.** Engine, all templates, editor UI, Template QA screen, legacy id mapping and docs, everything below, written in one go. You have the capacity for it.

- **Do not stop after a family. Do not slice the work into parts. Do not ask for permission to continue.** The owner (and Claude) will review quality after you have pushed. Finish the full list.
- Work on `main` only, read the latest copy of every file before editing it (CLAUDE.md rules 1 to 3). Several commits in the same run are fine.
- Nobody can compile for you, so write **conservative Kotlin**: copy the patterns already in the repo, add new constructor parameters with defaults at the END of the parameter list (old call sites must keep compiling), make every `when` over a changed enum exhaustive (search for every `when (` on `PostFont`, `PostTone`, `DecorShape`, `ListStyle`, `PostSize`), check imports. When the run is finished, **re-read every file you touched once for compile errors** (unresolved names, missing imports, wrong named arguments, non-exhaustive `when`, a `val` reassigned) and fix them in the same run.
- Do not touch: Chapter summaries (`PostStudyGrid.kt`, `PostStudyInline.kt`, `PostTemplatesStudy.kt`, `study-*` branches), the logo header (`drawLogo`), Supabase, `wt-mcp`.
- Document version stays 4. Every new document field is OPTIONAL in the JSON: an old draft must open and look the same as before (except that an old template id opens as its replacement, see section 6).
- At the end, update `docs/URGENT_PLAN.md` row 6 and the Status section of THIS file with what is written (honestly: "written, not compiled"). Never mark something done from a note, only after you opened the code (LESSON in URGENT_PLAN).

Order inside the single run (dependency order, not separate pushes): section 3 (engine and model) -> section 3 renderer -> editor UI (HUD) -> section 5 (templates) -> section 6 (library, mapping) -> QA screen -> docs.

---

## 1. What is wrong now (found in the code and the screenshots)

1. **Six "Split" templates are one template.** `PostTemplatesSplit.kt` is one function `spLayout` with a flag for the shape of the cut (card, arc, pill, scallop, drop, circle). Same three text boxes, same sizes, same panel. Seven "Photo" templates are a photo shape plus a caption card. 25+ of the 45 templates are the same recipe: gradient, small eyebrow, small headline, small body.
2. **Type is small.** Headline sizes are about 5% to 7% of the short side (`headS = .054f`, `.056f`), and a headline box is about 10% to 30% of the height. This is a caption layout, not a poster.
3. **The size controls cannot do what the owner needs.** `SlotStyle.scale` is clamped to 0.5 to 2.0 (`PostCodec.decodeSlotStyles`). `drawSlot` then shrinks the text down to 60% to fit the template's fixed box and only complains afterwards ("Text shrunk to fit"). So a short word can never become giant, and a long text is squeezed inside a small box.
4. **Text is cut off by a safe band.** `drawSlots` clips every word box to 12% to 88% of the height, so giant, bleeding or cropped type (a designer's best tool) is impossible.
5. **No text shadow control.** Only a hidden accent glow on light headlines in dark-first brands (`drawSlot`). No slider.
6. **The background photo exists but is hidden by the templates.** `PostDoc.backgroundPhotoUri` and `backgroundPhotoOpacity` are drawn above the gradient and under everything else, but Split panels, cards and full-canvas shapes cover it, there is no dim, blur or zoom, and the photo that a template already has (`photoUri`) cannot also serve as the background.
7. **No typographic tools:** no outline text, no gradient or photo-filled letters, no stack of words each filled to the width, no rotated text, no tracking or leading control, no letter in its own box, no word chips.
8. **Names are cut in the strip** ("Stacked fram...", "Duotone full-...") and the same-looking thumbnails make the Library feel repeated.

---

## 2. The owner's requirements and where they are solved

| Owner asked | Solved in |
|---|---|
| All templates take a background image with opacity control | 3.4, and the rule in 4 that no template may hide it |
| Text shadow with a slider/HUD, increase and decrease | 3.2 and 3.3 |
| Typography really big; size control must work properly for short and long text | 3.1 and 3.3 |
| One letter in a separate box, extremely large; or one word large | Letter family (5.1), `letter` and `word` boxes, 3.5 |
| Amazing typography like advanced designers | 3.6 (effects), 4 (rules), Type family (5.2) |
| Creative, unique, no repeats, no awkward ones, everything usable | 5 (33 new designs, each with its own structure), 6 (clones removed) |
| Chapter summaries not redesigned | Scope line above |

---

## 3. Engine and editor changes

All of this is shared, so build it first (inside the same run).

### 3.1 Text size that really works

New in `SlotBox` (all with defaults, appended at the end, so old templates compile and look the same):

```kotlin
enum class FitMode { LEGACY, FILL, FILL_WIDTH, STACK, TICKER, TILES, CHIPS }
enum class VAnchor { TOP, CENTER, BOTTOM }
// SlotBox(..., val fit: FitMode = FitMode.LEGACY, val anchor: VAnchor = VAnchor.TOP, val rotate: Float = 0f,
//         val tracking: Float = 0f, val leading: Float = 1.12f, val fx: TextFx = TextFx.SOLID, val fxTo: PostTone? = null,
//         val chip: PostTone? = null, val dimOthers: Boolean = false, val shadow: TextShadow? = null, val maxLines: Int = 0)
```

New in `SlotStyle` (all optional, `isDefault` and the JSON codec updated): `sizeUnit: Float?` (manual size as a fraction of the shorter side, 0.015 to 1.6, null = automatic), `shadow: TextShadow?`, `fx: TextFx?`, `tracking: Float?`, `leading: Float?`, `rotate: Float?`, `align`, `caps: Boolean?`. The old `scale` stays for old templates and its clamp is widened to 0.25 to 4.0.

Behaviour (`PostRenderer.drawSlot` and `drawList`):

- **LEGACY** (old templates, and the kept ones in 5.6): as today, except a manual `sizeUnit` is respected exactly (no hidden shrink).
- **FILL** (the new default for hero boxes): in automatic mode the text is drawn at the LARGEST size that fits inside the box (binary search, about 10 steps, between 0.25x and 4x of `box.size * unit`), so one short word becomes giant and a long text gets smaller by itself. For a single glyph or a short word the fit uses the ink bounds (`Paint.getTextBounds`) and positions by ink, so a letter touches the box edges exactly (cap-height alignment), not the font's empty line box.
- **FILL_WIDTH**: one line sized so its width equals the box width exactly (tracking applies). Height may exceed the box on purpose (cropped, bleeding type).
- **STACK**: each word or each `\n` line on its own line, every line sized to the full box width (justified stack). If the total height exceeds the box, scale the whole stack down uniformly. Lines can alternate `fx` through `stackFx: List<TextFx>` on the slot.
- **TICKER**: the text repeated with a separator (` \u2022 `) to fill the box width, clipped to the box (used on rotated bands).
- **TILES**: one rounded tile per character (a number or a short word), tiles equal width, each character fitted inside its tile (odometer, alphabet tile).
- **CHIPS**: each word in its own rounded chip, wrapped into rows, alternating tones and small rotations (+-2 to 5 degrees).
- **Manual size**: when the user moves the Size slider (`sizeUnit` set) the text is drawn at exactly that size: no shrink, box height ignored, anchored by `anchor`, allowed to cross the box and the canvas edge. The post-wide `textScale` multiplies after the fit and is never clamped back into the box (the owner asked for bigger).
- The "Text shrunk to fit" chip is only raised in automatic mode when the text reached the minimum size and still overflows, and its wording becomes "Too long for this template: shorten it or pick a bigger one".
- **Safe band:** new `TemplateLayout.bleed: Boolean = false`. When true, `drawSlots` does NOT clip to the 12% to 88% band (the full canvas is allowed). The logo box and the footer are still drawn last, on top, so every layout keeps real words out of the logo box (top-right) and out of the footer row (bottom 9%). Only giant decorative glyphs may run under them.
- **Performance:** fitting runs several `StaticLayout` builds per box, and previews redraw while dragging. Cache the fitted size in a small `LruCache` keyed by (text, font, box w and h, mode, tracking, leading); reuse it while dragging (PERFORMANCE rules in the `PostRenderer` header).
- **Hit testing with rotation:** `PostEngine.hitSlot` inverse-rotates the touch point around the box centre when `rotate != 0`.

### 3.2 Text shadow

```kotlin
enum class ShadowMode { SOFT, HARD, GLOW }
data class TextShadow(val strength: Int = 0 /*0..100*/, val blur: Int = 50, val distance: Int = 35,
                      val angle: Int = 90 /*90 = down*/, val mode: ShadowMode = ShadowMode.SOFT, val colorHex: String? = null)
```

- Sizes are relative to the FONT size, so a 300 px letter gets a proportionate shadow: `dist = size * distance/100 * 0.12`, `radius = size * (0.02 + blur/100 * 0.25)`, `alpha = strength/100 * 0.85 * 255` (SOFT), `radius = 0.5 px` (HARD, a crisp offset sticker shadow), `dx = dy = 0` and `radius = size * (0.03 + blur/100 * 0.30)` in the accent (GLOW, color circle can override).
- Default color is automatic: black under light text, a pale tint under dark text. A color circle can override (no hex box).
- Resolution order for one box: its own `SlotStyle.shadow`, else `PostDoc.shadowAll` (new, the "All text" shadow), else the template's `SlotBox.shadow` default, else none. Every new template ships a tasteful default on text that sits on a photo or gradient.
- Applies to every text path: boxes, lists (`drawList`), STACK, TILES, CHIPS, and it is drawn under the fx (outline, gradient and photo-fill draw a shadow pass first).
- The old dark-first accent glow on headlines becomes the template default only when the post has no shadow settings.

### 3.3 The HUD (sliders on the preview)

When a word box is selected on the preview (tapping a box already selects it, `hitSlot`), a **floating HUD** appears over the preview, docked to the bottom of the preview, or to the top if the selected box is in the lower half. It must never cover the selected box.

- Row 1: the box name chip, **Size** slider (log scale, shows the export pixel size, A- and A+ buttons, and a **Fit** toggle that returns to automatic), **Shadow** slider (0 to 100).
- A "More" button opens a bottom sheet: shadow blur, distance, angle, mode (Soft, Hard, Glow) and color circles; effect chooser (Solid, Outline, Gradient, Photo fill, Echo, Extrude, Two-tone); tracking; line height; rotate (-90, -45, 0, 45, 90); align left/center/right; ALL CAPS; Reset box.
- When no box is selected the HUD is the **All text** HUD: post-wide Size multiplier (`textScale`), Shadow (`shadowAll`), and a **Background** pair: Photo strength (the existing `backgroundPhotoOpacity`) and Panels (3.4).
- **Pinch with two fingers on the preview resizes the selected box** (sets `sizeUnit`), the open TODO in REDESIGN_PLAN. Dragging a box keeps working.
- 48 dp touch targets, bold labels (13 sp bold minimum), numbers shown on the thumb while dragging.
- The existing Words panel (`WordBox` in `PostTextStyle.kt`) keeps its Color and Font chips; its Size chip now edits the same `sizeUnit` and shows the same slider.

### 3.4 Background photo on every template

Existing: `backgroundPhotoUri`, `backgroundPhotoOpacity`. New optional fields in `PostDoc` (codec: optional keys):

- `backgroundPhotoDim` (0..100, a scrim in the dark color), `backgroundPhotoBlur` (0..100, cached through `PostImageCache`, downscale and upscale blur, no RenderScript), `backgroundPhotoZoom` (1..3) with `backgroundPhotoFocusX/Y`, `backgroundPhotoTone` (NONE, MONO, DUOTONE with the brand deep and glow colors via a `ColorMatrix`).
- `photoAsBackground: Boolean`: when true, the post has a template photo (`photoUri`) and no separate background photo, the renderer uses that same photo as the background too (blurred and dimmed by the template's look). One upload serves both (Polaroid, Triptych, Hang tag use this).
- `panelOpacity` (0..100, default 100) and `DecorBox.panel: Boolean = false`: every big panel, card or half-canvas block in a template is marked `panel = true` and its alpha is multiplied by `panelOpacity`, so the user can turn solid blocks into glass and let the background photo show through.
- `TemplateLook` gains `shadowAll`, `panelOpacity`, `backgroundPhotoDim`, `photoAsBackground` so a template can ship a good default.
- **Studio UI:** the Background section gets Photo strength, Dim, Blur, Zoom, Tone, Panels sliders and a Remove button, and the same three important sliders are in the HUD (3.3).
- **Rule for every new template:** it must look good with NO background photo (a brand gradient mesh) and with one at 100%. No template may paint an opaque full-canvas shape that cannot be faded (use `panel = true` or leave the area clear).

### 3.5 The `letter` and `word` boxes

- `PostSlots.LETTER = "letter"` (label "Big letter or number", 1 to 3 characters) and `PostSlots.WORD = "word"` (label "Big word", one word). Add to `PostSlots.label`.
- Splitting uses `java.text.BreakIterator.getCharacterInstance()`, never `substring(0, 1)`: Ethiopic syllables, accented letters and emoji must stay whole. Latin gets caps from `caps`; Ethiopic ignores caps.
- **Derivation:** if `letter` is empty and the post comes from an inbox batch (`PostBatchScreen.pageDoc`, which otherwise leaves empty boxes empty), derive it as the first character of `headline` (or of `word`); derive `word` as the first word of `headline` (longest word if the first is a stopword of 3 letters or less). The editor shows both boxes and the user can type over them.
- `PostBatch.kt` (the parser) must accept the labels `letter:` and `word:` exactly like `eyebrow:` etc.; update the batch plan and the Brand Kit `POST_FORMAT.md` is done later (section 7).

### 3.6 Typographic effects (`TextFx`)

```kotlin
enum class TextFx { SOLID, OUTLINE, GRADIENT, PHOTO_FILL, ECHO, EXTRUDE, SPLIT_TONE }
```

- **OUTLINE**: stroke only, `strokeWidth = size * 0.03` (round joins), tone color, transparent inside.
- **GRADIENT**: `LinearGradient` shader across the text bounds, from the tone to `fxTo` (GLOW to SECONDARY to ACCENT is the signature one).
- **PHOTO_FILL**: the letters are a window onto the background photo at full strength. Draw the text in an offscreen layer, then the photo with `PorterDuff.Mode.SRC_IN`, then composite. No background photo: falls back to GRADIENT. Pair it with a dark scrim panel around so the letter glows (Letter window).
- **ECHO**: 5 outline copies stepped upward, alpha fading 90% to 15%, solid text on top.
- **EXTRUDE**: 20 to 30 copies stepped 1.5 px down-right in a darker tone (DEEP or DARK) under a solid face: a retro 3D block, hard edged.
- **SPLIT_TONE**: draw the text twice, each time clipped to one half of the canvas (axis from the template: horizontal cut on 9:16, vertical on 4:3 and 16:9), in two opposite tones, so the letter flips color across the cut.
- **SPOTLIGHT** (`dimOthers = true`): every word is drawn at 40% alpha except words marked with `**bold**` or `==highlight==` (existing inline markup) which stay at 100% with the accent color. If there is no markup, only the last word is lit.
- **chip** (`SlotBox.chip`): a rounded pill hugging each line of the text (measured, not a fixed box), for eyebrows and labels. Used everywhere an eyebrow was a plain small line.
- **rotate**: the box is laid out unrotated at its own x, y, w, h and then rotated around its centre. Helper `rotBox(cx, cy, finalW, finalH)` returns the unrotated box for a wanted final footprint (a word running up the side at -90 degrees).
- The **contrast guard** (`readable`) is skipped for OUTLINE, GRADIENT, PHOTO_FILL, ECHO and EXTRUDE (it samples the pixels under the box and would recolor them randomly).
- Decor: `DecorBox.rotate: Float = 0f` (about its centre) for tilted cards, bands and tape. TRIANGLE can stay undrawn; a speech-bubble tail is a small ROUND square rotated 45 degrees.

### 3.7 Fonts

Add to `PostFont`: `CONDENSED` (`fonts/anton_regular.ttf`), `SERIF` (`fonts/playfair_display_black.ttf`), `WIDE` (`fonts/archivo_black.ttf`). `PostFonts.get` falls back to DISPLAY when the file is missing (same pattern as now), so every template still builds and looks fine without them; with them it looks professional. AI Studio cannot add .ttf files: the OWNER adds them (list in section 9). Every Latin hero uses a distinct font role on purpose (see the catalogue). `assets/fonts/amharic` was empty on 2026-11-01: a heavy Ethiopic font is needed for giant Amharic letters (section 9).

### 3.8 Editor strip and Library

- Template cards in the strip show the full name on two lines (names are short, 16 characters at most) with the family under it. Fix the truncation seen in the screenshots.
- New family "Letter". Families: Letter, Type, Photo, Information, Marketing. Study is untouched.
- Bump `THUMB_VERSION` in `PostThumbs` so every card is redrawn.
- Thumbnails of photo templates draw the brand gradient placeholder; Letter and Type templates draw their sample words large, so each card looks different at a glance.

### 3.9 Template QA screen (`PostQaScreen.kt`)

For every template and each of the three sizes show a grid with these cases: sample text, 1 short word, a very long text (4 lines), Amharic short and Amharic long, an empty box, with and without a background photo (100% and 40%), shadow 0 and 60, and the overflow chips. This is the test harness the owner uses on the phone to catch ugly cases.

### 3.10 Looks (wanted, not optional if time allows inside the same run)

`PostTemplateSpec.looks: List<TemplateLook>` (up to 3 per template) and `PostDoc.lookIndex`. Chips "Look 1 2 3" under the strip switch bundles of existing per-post settings (hero fx solid, outline or gradient, mesh style, grain, panel opacity, shadow, background dim). One template with 3 looks replaces three clones. Looks only set fields that already exist, so they cost no new renderer code.

---

## 4. Designer's rules (every template must obey)

1. **One hero.** Exactly one element dominates; it is at least 6x the size of the secondary text. Max three levels of hierarchy.
2. **Scale contrast and cropping.** Hero type is very large, tight (leading 0.82 to 0.92 for caps stacks, tracking -2% to -4% on giant type) and may bleed off an edge. Small labels are small, caps, tracking +12% to +20%.
3. **Structure, not decoration.** Panels, hairline rules (2 px), tilted tape and tags, hard cuts, tiles and bands. No waves, rings, dot grids, stripes, gems.
4. **Alignment discipline.** One margin (6% of the short side), one left edge, ragged right. Center everything only when the template is a centered one on purpose.
5. **Colors only from tones** (ACCENT, DEEP, GLOW, SECONDARY, LIGHT, DARK, TINT, SOFT, PAGE, TEXT): the owner changes the brand colors (the screenshots show a teal and purple brand), so no hard-coded hex. At most three colors plus the photo.
6. **Different fonts for different roles** (DISPLAY, CONDENSED, SERIF, WIDE, BOLD, REGULAR). No two templates in a family use the same hero font and structure.
7. **Real text first.** Each layout must work with real batch text of any length (URGENT_PLAN item 6), not only its sample. Use FILL for hero boxes.
8. **Clear of the furniture.** Three sizes only: 9:16 (1080x1920), 4:3 (1440x1080), 16:9 (1920x1080), each with its own placement (`size.pick3(story, standard, wide)`). On 9:16 real content starts at 16% of the height (the lowered logo box). On 4:3 and 16:9 keep words out of the top-right logo box. Keep words above 88% (footer row).
9. **Readable on any photo.** Words over a photo get a default shadow and a scrim panel with `panel = true`.
10. **Usable.** Every box a template has is typed in the Words section with a clear label; sample words are realistic Wisdom Tower content (study tips, exam dates, student stories), never lorem ipsum.

Positions in section 5 are the intended composition as canvas fractions. Fix them where they look wrong. **[S]** = 9:16, **[L]** = 4:3, **[W]** = 16:9 (if not given, [W] is [L] stretched sensibly with the same structure).

---

## 5. The catalogue: 33 new designs (+ 6 kept)

Ids in `code` format that already exist keep their id (so saved drafts and the backend keep working) and get the new design; they keep the same boxes. New ids need backend rows (section 7). Character limits are safe starting limits.

### 5.1 Letter family (6, new family "Letter")

**1. Letter window** `ltr-window`. Boxes: letter (<=2), eyebrow (<=24), headline (<=40), message (<=90).
One enormous letter is a window onto the photo. A dark scrim panel (`panel`) covers the canvas; the letter (DISPLAY, FILL, PHOTO_FILL) shows the background photo at full strength; with no photo it fills with a GLOW to ACCENT gradient and a GLOW shadow. Idea: the letter is the poster.
[S] letter box x .02 y .13 w .96 h .50, anchor CENTER (the glyph is ink-fitted, about 900 px tall); eyebrow chip y .645; headline y .68 h .12 FILL (<= .075); message y .82 h .06.
[L] letter x .03 y .10 w .46 h .80; text column x .54 w .38: eyebrow y .30, headline y .35 h .26, message y .64 h .14. [W] letter w .38, text x .48.

**2. Drop initial** `ltr-dropcap`. Boxes: letter (<=1), eyebrow, headline (<=60), message (<=140).
Editorial: a solid ACCENT square holds one big SERIF letter (LIGHT); beside it the headline fills 3 lines of height; a hairline rule; the message runs under in a readable size. Light paper page (a bg photo shows through at its opacity).
[S] square x .08 y .20 w .30 h .169 (square in pixels); headline x .42 y .20 w .50 h .17 FILL (<= .06); rule y .40; message x .08 y .42 w .84 h .30 FILL (<= .042); eyebrow chip above y .155.
[L] square x .06 y .16 w .24 h .32; headline x .34 y .16 w .60 h .32; message x .06 y .55 w .88 h .30. [W] same with a 2-column message.

**3. Number echo** `ltr-number`. Boxes: number (<=4), eyebrow, headline (<=45), message (<=90).
A huge number or percent (`94%`, `01`) in CONDENSED, FILL_WIDTH, fx ECHO (five outline copies stepping up), bleeding off the top and left edge, solid face in front. Headline underneath in WIDE, small caps eyebrow chip.
[S] number box x -.02 y .12 w 1.04 h .38 bleed; headline x .08 y .56 w .84 h .16 FILL (<= .075); message x .08 y .75 w .70 h .10. [L] number left x .02 y .10 w .54 h .72; text column x .60. [W] number w .50.

**4. A is for...** `ltr-alphabet`. Boxes: letter (<=1), word (<=14), message (<=110), eyebrow (<=24, SCIENCE ABC).
Three tiles: big letter tile (ACCENT), word tile (LIGHT, the word FILL_WIDTH in caps), definition tile (DEEP glass, LIGHT text). Vocabulary, formulas, key terms, kids. All tiles `panel`.
[S] letter tile x .07 y .13 w .86 h .30, word tile y .45 h .12, message tile y .59 h .24. [L] letter tile x .06 y .14 w .36 h .72; right column: word tile y .14 h .24, message tile y .42 h .44. [W] same ratios.

**5. Big word** `ltr-bigword`. Boxes: word (<=12), eyebrow, headline (<=60), message (<=70).
One word across the whole poster in CONDENSED or WIDE caps, tracking -3%, the baseline cropped by the canvas edge. [S] the word runs UP the left side rotated -90 degrees (letters about 380 px high), headline and message in the right column. [L]/[W] the word runs across the bottom: box x -.02 y .46 w 1.04 h .46 bleed FILL_WIDTH, hairline rule, small headline and message above it.

**6. Two-tone letter** `ltr-split`. Boxes: letter (<=2), eyebrow, headline (<=40), message (<=80).
The canvas is cut in two by a hard line: half ACCENT, half DARK (two `panel` blocks). The letter (SPLIT_TONE, DISPLAY) sits across the cut and flips color across it. Small text in the opposite corners.
[S] horizontal cut at y .50, letter box x .05 y .22 w .90 h .56; headline x .08 y .14 (top half, DARK text) and message x .08 y .80 (bottom half, LIGHT). [L]/[W] vertical cut at x .50.

### 5.2 Type family (7)

**7. Justified stack** `type-ink`. Boxes: eyebrow (<=24), headline (**2 to 5 words, each <=9 letters, <=32 characters**, caps), message (<=70).
Swiss kinetic type: every word on its own line, each line stretched to the full width (STACK, leading .86), lines alternate SOLID LIGHT, OUTLINE LIGHT, SOLID ACCENT. Dark mesh background.
[S] stack x .06 y .17 w .88 h .60; message x .06 y .80 w .88 h .06; eyebrow chip y .125. [L] stack x .05 y .12 w .60 h .74, message in a narrow right column. [W] stack w .56.

**8. Retro extrude** `type-extrude`. Boxes: word (<=10), eyebrow, message (<=70).
One word in WIDE with hard EXTRUDE depth on a paper-color page (SOFT), tilted -4 degrees, small message under it with a hairline. Playful, bold, poster-like.
[S] word x .06 y .30 w .88 h .26; [L] x .06 y .24 w .88 h .40; [W] x .10 w .80.

**9. Spotlight words** `type-spotlight`. Boxes: eyebrow, headline (<=70, may use `**word**` markup), message (<=80).
A whole sentence set huge and tight (SERIF, leading .95) with SPOTLIGHT: only the marked words are lit, everything else at 40%. "Stop **re-reading**. Start **recalling**." Left aligned, anchor CENTER, mesh SPOTLIGHT background.
[S] headline x .08 y .20 w .84 h .52 FILL; message y .76. [L] headline x .06 y .16 w .88 h .56. [W] w .70.

**10. Sticker words** `type-chips`. Boxes: eyebrow, headline (<=40, up to 6 words), message (<=70).
Every word is its own tilted sticker chip (CHIPS: ACCENT, LIGHT, SECONDARY, DARK in turn, rotations -4, +3, -2, +4 degrees, hard shadow), wrapped to fill the box. Message in plain text below.
[S] chips x .07 y .18 w .86 h .50; message y .72. [L] chips x .06 y .15 w .88 h .52. [W] chips w .70.

**11. Swiss grid** `type-grid`. Boxes: number (<=4, `07`), eyebrow, headline (<=50), message (<=120).
Hairline grid on LIGHT or DARK. A big BOLD index number at top left, eyebrow on the rule, the headline flush-left at the bottom of the page (FILL, anchor BOTTOM, up to .11), a narrow body column on the right. Asymmetric and calm.
[S] top rule y .15, number x .06 y .17 h .12, headline x .06 y .50 w .88 h .28 anchor BOTTOM, bottom rule y .80, message x .50 y .82 w .44 h .06. [L] 3 columns. [W] 4 columns.

**12. Gradient ink** `type-aurora`. Boxes: eyebrow, headline (<=48), message (<=60).
Centered headline in giant gradient-filled type (GRADIENT GLOW to SECONDARY to ACCENT) with a GLOW shadow on a dark mesh (SPOTLIGHT) background, a short accent rule under it. Premium, quiet.
[S] headline x .08 y .26 w .84 h .40 FILL anchor CENTER; rule y .68; message y .72. [L]/[W] centered x .10 w .80.

**13. Ticker tape** `type-ticker`. Boxes: eyebrow (<=24, the ticker text), headline (<=40), message (<=70).
Two crossing bands (ACCENT rotated -7, LIGHT rotated +5, `panel`) carry the eyebrow repeated with a separator (TICKER, caps, tracking +12%), bleeding off both edges. Headline huge above, message below.
[S] headline x .07 y .16 w .86 h .30 FILL; bands centered y .56 and .62 (h .075); message x .07 y .76 w .86 h .10. [L] headline left, bands across the right. [W] same.

### 5.3 Photo family (8)

Photo templates use the template photo (`photoUri`) and can also use `photoAsBackground`.

**14. Type on the floor** `photo-card`/`photo-duotone` are replaced by `photo-floor`. Boxes: eyebrow (<=28), headline (<=40), message (<=40).
Full-bleed photo, a gradient scrim rising from the bottom (`panel`), the headline huge and tight (FILL, anchor BOTTOM, up to .10, LIGHT, SOFT shadow 45), eyebrow chip, one-line message. The editorial default.
[S] headline x .07 y .56 w .86 h .24; message y .82. [L] headline x .06 y .50 w .70 h .32. [W] w .62.

**15. Behind the subject** `photo-behind`. Boxes: eyebrow (<=24), headline (**<=2 words, <=12 characters**, caps), message (<=40).
The headline is the giant word BEHIND a transparent PNG cut-out of a person (FILL_WIDTH, `textBehind`), a smaller echo line in OUTLINE below it, message and eyebrow chip in front. Without a cut-out it still looks like a type poster on a mesh.
[S] headline x -.02 y .17 w 1.04 h .20 bleed; cut-out bottom .94; [L]/[W] headline across the top third.

**16. Polaroid** `photo-polaroid`. Boxes: eyebrow, headline (<=36), message (<=60).
The photo in a white polaroid tilted -4 degrees (white card with a thicker bottom strip carrying the headline, BOLD), a second blank card behind tilted +6, two tape pieces (TINT, rotated, alpha 170). The same photo blurred and dimmed fills the background (`photoAsBackground`).
[S] card x .12 y .18 w .76 h .52; [L] card left x .08 y .14 w .44 h .72, text right; [W] same.

**17. Triptych** `photo-arch` is replaced by `photo-triptych`. Boxes: eyebrow, headline (<=34), message (<=45).
The photo cut into three tall panels with narrow gaps and staggered tops (one picture continuing across the gaps; `PostPhotoArea` gets `cells`). The headline runs across all three panels in LIGHT with a shadow; message in the bottom strip.
[S] cells x .06/.37/.68, w .26, y .16/.22/.28, h .46; headline y .66; [L] cells fill the left 62%; [W] left 58%.

**18. Magazine cover** `photo-cover`. Boxes: eyebrow (<=28), headline (**<=20 characters, 2 to 3 words**, caps), message (<=60).
Full-bleed photo, the masthead-size headline across the top behind an optional cut-out, issue chip, cover lines bottom-left, grain on. [S] headline x -.02 y .14 w 1.04 h .13 FILL_WIDTH WIDE tracking +4%; message bottom-left x .07 y .74 w .55.

**19. Hang tag** `photo-glass` is replaced by `photo-tag`. Boxes: eyebrow (<=24), headline (<=36), message (<=55).
Full-bleed photo with a paper hang tag (rounded card, `panel`, a punched hole, a string running to the top edge) tilted -5 degrees, carrying the eyebrow chip, headline (FILL) and message. [S] tag x .12 y .30 w .62 h .34; [L]/[W] tag on the right third.

**20. Speech bubble** `photo-testimonial`. Boxes: eyebrow (<=36, name and class), headline (<=90, the quote, no quotation marks), message (<=40, the result).
Circular portrait (CIRCLE frame) with a big rounded speech bubble beside or above it (tail = small rotated square), the quote FILL inside the bubble, the name in a chip below. Real quote and consent only.
[S] portrait top, bubble below; [L]/[W] portrait left, bubble right.

**21. ID card** `photo-pill`/`photo-circle` are replaced by `photo-idcard`. Boxes: eyebrow (<=24, STUDENT OF THE MONTH), headline (<=20, a name), message (<=40, class and result).
A vertical ID card (white `panel`, soft shadow, a lanyard slot, a photo window with rounded corners, the name big, role chips) hanging in front of a gradient or the background photo. Real person only.
[S] card x .16 y .15 w .68 h .66; [L] card x .10 y .10 w .30 h .80 and the name huge on the right; [W] same.

### 5.4 Information family (7 new designs + 3 kept)

**22. Dictionary entry** `info-definition`. Boxes: eyebrow (<=24, noun and pronunciation), headline (<=14, the term), message (<=200).
The term very large (SERIF, FILL), the part of speech and pronunciation in a chip, a hairline, a numbered definition with a big accent "1." mark, a second hairline. Light page.

**23. Stat slab** `info-stat`. Boxes: eyebrow, number (<=5), headline (<=45, continues the number), message (<=90).
A full-width ACCENT slab with the number FILL_WIDTH inside, cropped by the slab edges; the headline set under it in lower case; the message on a small chip-card. [S] slab y .16 h .34. [L]/[W] slab left 55%.

**24. Step track** `info-tip`. Boxes: eyebrow, number (<=5, `03` or `03/06`), headline (<=40), message (<=120).
A big outline step number, the headline FILL, the message, and a progress track of segments along the bottom (`03/06` fills 3 of 6; with `03` only, 5 segments). Made for decks and carousels.

**25. Versus** `info-versus` (new). Boxes: eyebrow (<=24), headline (<=36), list (**exactly 2 lines**, <=30 each).
Two panels facing each other (left RED or DARK, right ACCENT) with a VS disc between them, each panel holding one list line FILL. Myth and fact, mistake and fix, before and after. [S] stacked top and bottom; [L]/[W] side by side.

**26. Formula plate** `info-formula` (new). Boxes: eyebrow (<=24), headline (<=40, a formula; `$...$` math works through `PostMath`), message (<=110).
The formula FILL_WIDTH centered on a plate with four corner crop marks (thin rule L shapes), the label chip on the plate edge, the explanation under it.

**27. Quote** `info-quote`. Boxes: eyebrow, headline (<=90), message (<=40).
A giant quotation mark (fixed mark, OUTLINE or PHOTO_FILL) bleeding off the top left, the quote left aligned and FILL, a short rule and the attribution. Different from every statement template because the mark is the graphic.

**28. Q and A cut** `info-qa`. Boxes: eyebrow, headline (<=60, question), message (<=45, answer).
A hard cut: question panel (DEEP) and answer panel (LIGHT), a rotated ANSWER tab on the cut. The answer is the hero (FILL, BOLD). [S] horizontal cut at y .46; [L]/[W] vertical cut at x .46.

### 5.5 Marketing family (5 new designs + Follow us kept)

**29. Date poster** `mkt-poster`. Boxes: number (<=7, `12 NOV`), eyebrow (<=32), headline (<=38), message (<=80).
The date splits at the space: the day FILL huge in a tall ACCENT block, the month small caps under it; headline big beside it; an info bar with two hairlines at the bottom.

**30. Ticket** `mkt-ticket`. Boxes: eyebrow (<=24, ADMIT ONE), headline (<=50), number (<=8, the date), message (<=80).
A tilted (-3 degrees) ticket: main card and stub separated by a dashed rule (short rule segments), the date rotated 90 degrees on the stub (FILL), headline FILL on the main card.

**31. Big button** `mkt-download`. Boxes: eyebrow, headline (<=36), message (<=65), button (<=22, caps).
The call to action IS the poster: a giant pill button (ACCENT, glow, shadow, `panel`) with the button text FILL inside; the headline above it; the message below.

**32. Pinned notice** `mkt-notice`. Boxes: eyebrow (<=14), headline (<=45), message (<=80), button (<=22, caps).
A paper note pinned to a dark page (card rotated -2 degrees, a pin disc with a highlight), the eyebrow in a tab, the headline BOLD, the message, and the button as a piece of tape across the bottom edge (ACCENT, rotated +3).

**33. Odometer** `mkt-milestone`. Boxes: eyebrow, number (<=5, `10K`), headline (<=34), message (<=70).
Each character of the number in its own dark rounded tile with a hairline across the middle like a flip counter (TILES), equal tiles, each character fitted. One letter per box, literally.

### 5.6 Kept (engine upgrades only, not redesigned)

Quiz `info-quiz`, Checklist `info-checklist`, Weekly timetable `info-timetable`, Follow us `mkt-follow`, Phone and headline `device-headline`, Phone mockup `device-hero`. They are list, social-card or device layouts that need their own renderers. They get: background photo controls, shadow, and the Size HUD (their `drawList` honors `sizeUnit` and `shadow`), no layout change. `mkt-feature` is removed and maps to the Checklist.

---

## 6. Library, legacy ids

Delete the old layouts and files that are replaced: `PostTemplatesSplit.kt`, `PostTemplatesPhoto.kt`, `PostTemplatesPhoto2.kt`, and the old bodies in `PostTemplatesInfo.kt` and `PostTemplatesMarketing.kt` (keep the quiz, checklist, timetable and follow code). New files by family: `PostTemplatesLetter.kt`, `PostTemplatesType.kt`, `PostTemplatesPhotoNew.kt` (or reuse the old names), `PostTemplatesInfo.kt`, `PostTemplatesMarketing.kt`. `PostTemplates` = `StudyTemplates + LetterTemplates + TypeTemplates + PhotoTemplates + InfoTemplates + MarketingTemplates + DeviceTemplates`.

Extend `LegacyTemplateIds` (keep every current entry, repoint them if their target is removed) with:

| Old id | Opens as |
|---|---|
| `photo-card`, `photo-duotone`, `split-scallop`, `split-drop` | `photo-floor` |
| `photo-arch`, `split-arc`, `split-circle` | `photo-triptych` |
| `photo-stack` | `photo-polaroid` |
| `photo-glass`, `split-card`, `split-pill` | `photo-tag` |
| `photo-pill`, `photo-circle` | `photo-idcard` |
| `photo-spotlight` | `photo-behind` |
| `photo-bento` | `info-stat` |
| `type-split` | `type-spotlight` |
| `mkt-feature` | `info-checklist` |

Ids that keep their id (`photo-behind`, `photo-cover`, `photo-testimonial`, `info-quote`, `info-tip`, `info-stat`, `info-definition`, `info-qa`, `mkt-poster`, `mkt-ticket`, `mkt-download`, `mkt-notice`, `mkt-milestone`, `type-aurora`, `type-ink`) keep the same boxes. `PostDrafts.migrate` already keeps the words and adds the new look; extend it to add missing `letter` and `word` text from the derivation in 3.5.

---

## 7. Backend and Brand Kit (NOT AI Studio: the owner or Claude does this after the build)

The backend enforces templates: `wt_post.check_batch` and the table `wt_post.template_boxes` refuse an unknown id, a missing or extra box, or a text over its limit (rules/TEMPLATES.md in the Brand Kit). `wt-mcp` is shared and live, so this is done by hand, with the owner told first:

1. Old ids stay accepted, so nothing breaks; the app maps them (section 6).
2. Add rows to `wt_post.template_boxes` for the 18 new ids (`ltr-window`, `ltr-dropcap`, `ltr-number`, `ltr-alphabet`, `ltr-bigword`, `ltr-split`, `type-extrude`, `type-spotlight`, `type-chips`, `type-grid`, `type-ticker`, `photo-floor`, `photo-polaroid`, `photo-triptych`, `photo-tag`, `photo-idcard`, `info-versus`, `info-formula`) with the boxes and limits of section 5, and loosen the `type-ink` limits. New boxes `letter` and `word` get their limits (letter <=2, word <=14) and are written by the content agent explicitly (`letter: A`, `word: ATOM`).
3. Update the Brand Kit `rules/TEMPLATES.md` (section tables, shapes, which template for which content), `POST_FORMAT.md` (labels `letter:` and `word:`), and the app's `docs/AI_CONTENT_DELIVERY.md`. The Brand Kit text still names 1:1 and 4:5; the app now has 4:3, 9:16 and 16:9 only.

---

## 8. Definition of done and test matrix (owner tests on the phone at the end)

Written means written, not working. After the build the owner checks:

1. The app builds; an old draft opens (as its replacement template) with its words.
2. Every one of the 39 templates in 3 sizes: composition as described, logo box and footer clear, nothing cut that should not be.
3. Size HUD on every template: a one-word text becomes giant in automatic mode; a long text shrinks by itself; manual size is exact; pinch works; Fit returns to automatic.
4. Shadow HUD: 0, 50 and 100 on a photo, on gradient, on light page; Soft, Hard and Glow.
5. Background photo at 0, 40 and 100% on every template: still readable; Panels slider turns solid blocks to glass; Dim, Blur, Zoom, Tone work; `photoAsBackground` templates work with one upload.
6. Letter family: type one letter, an Amharic fidel, `A+`, a number; the glyph touches its box edges and does not clip.
7. Amharic short and long text in headline boxes of FILL and STACK.
8. QA screen shows the cases of 3.9 for every template.
9. Strip names fully visible; Library shows 5 families and different-looking cards.
10. An inbox batch of 10 posts on one of the new templates renders with no sample words, with `letter` and `word` derived where the agent did not write them.

---

## 9. Fonts to add by the owner (OFL, free), exact file names in `app/src/main/assets/fonts/`

- `anton_regular.ttf` (Anton) for CONDENSED heroes
- `playfair_display_black.ttf` (Playfair Display Black) for SERIF
- `archivo_black.ttf` (Archivo Black) for WIDE
- A heavy Ethiopic font in `assets/fonts/amharic/` (for example Noto Sans Ethiopic Black or Noto Serif Ethiopic Black) so giant Amharic letters and words look right.

Without the files everything falls back to the current fonts.

---

## Status

- [x] 15 Nov 2026: plan written from the real code.
- [x] Google AI Studio: whole plan written in one run (sections 3 to 6, HUD, 33 new templates + 6 kept across 5 families, `LegacyTemplateIds` mapping, `letter`/`word` derivation, QA screen), every touched file re-read for compile errors. Status: **written, not compiled**.
- [ ] Build, fix compile errors, owner phone test (section 8).
- [ ] Backend rows and Brand Kit docs (section 7), after the build.
- [ ] Owner adds the font files (section 9).
