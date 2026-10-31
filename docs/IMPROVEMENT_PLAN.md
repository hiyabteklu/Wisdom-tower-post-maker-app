# Wisdom Tower Post: improvement plan

This plan replaces the Canva-style direction in PRODUCT_PLAN.md. That plan asked for a full layer editor, gradients, A4 and PDF. The goal was always something smaller: make a branded social post in under a minute, with the same logo and footer every time.

## 1. What the code does today (and why it feels like Canva)

Found by reading DesignStudio.kt, DesignDocument.kt and DesignExport.kt.

1. **One screen does everything.** DesignStudio.kt is a single scrolling column: name, save, open, new, canvas size, template strip, canvas, add buttons, logo button, layer stack, a panel with about 12 sliders for the selected layer, brand kit, background, gradient, export. The canvas scrolls out of view while you edit.
2. **Templates are not really 20 designs.** Every template builds the same 5 text layers (brand label, eyebrow, headline, message, footer). A `when(id)` block nudges their Y positions, and a few templates add one circle or stripe. Mostly they are color changes.
3. **Preview and export are two different renderers.** The screen draws with Compose Text (font size clamped to 8 to 60sp, line height 1.2). Export draws with StaticLayout at pixel size and cuts lines silently when the box is short. Long text will look different in the saved file than on screen.
4. **Footer icons are one imported picture.** There is no Telegram, Website or TikTok icon set and no link text. The logo is a normal image layer that can be dragged anywhere, so brand placement is not consistent.
5. **Wrong sizes.** The presets are 1:1, 9:16, 4:5, 16:9 and two A4 sizes. 4:3 is missing. PDF export exists and is not needed.
6. **Fragile storage.** Logo and icon strip are stored as picker URIs, which can stop working. Every drag writes the whole design to SharedPreferences. Undo history is built from a delayed effect, and changing the selected layer counts as an edit.
7. **Fonts are the Android system fonts** (sans, serif, mono). Nothing from the Academy brand.

## 2. New product shape

Template first, brand locked, three sizes.

- You pick a template, type your words, optionally add a photo, export. Nothing else is required.
- The logo and footer are brand components, not layers. They appear on every post in the same place, and you control them from one Brand screen.
- No free layer list, no rotation, no opacity sliders, no gradient editor on the main path. Anything advanced sits inside a collapsed "More" group.
- Three export sizes only: **1:1** (1080 x 1080), **4:3** (1440 x 1080), **9:16** (1080 x 1920). PNG or JPG. No PDF, no A4.

## 3. Screens (four, not one)

1. **Templates** (home). Grid of 20 live previews, category chips, size switch (1:1, 4:3, 9:16) at the top. Previews are drawn by the same renderer as export, using the Academy brand and sample text. Tap one to open it.
2. **Editor.** Canvas fixed at the top and never scrolls away. Under it, a panel with collapsed groups. Only one group is open at a time:
   - **Words**: one text box per slot in this template (eyebrow, headline, body, list).
   - **Look**: 6 brand colors, background style (solid, soft, photo), text size S / M / L.
   - **Photo**: add or replace, fill or fit, darken for readability.
   - **Header and footer**: switch logo or footer off for this post, pick logo corner.
   - **More** (closed by default): nudge position, change font, duplicate text box.
   Tapping a text area on the canvas opens Words with that box focused.
3. **Brand.** Logo (copied into app storage), brand colors, fonts, footer items. Footer items are chosen from a bundled icon set (Telegram, Website, TikTok, YouTube, Instagram, Facebook) with your own handle or link text next to each. Changing it here changes every new post and can be applied to the open one.
4. **Export sheet.** Tick one or all three sizes, choose PNG or JPG, then Save or Share. It warns about text that does not fit before saving.

The saved-designs list becomes a small "My posts" tab on the home screen with thumbnails.

## 4. Templates: 20 real layouts

Each template is data: a list of slots (eyebrow, headline, body, list, big number, photo) with a position per aspect ratio, plus a background style. The layout is redone for each ratio instead of stretched. Content is kept when you switch template or size, because it belongs to the slot, not to a layer.

| # | Family | Template |
|---|---|---|
| 1 | Quote | Centered quote |
| 2 | Quote | Quote with side bar |
| 3 | Quote | Quote on photo |
| 4 | Motivation | Bold statement |
| 5 | Motivation | Split color |
| 6 | Question | MCQ, four options |
| 7 | Question | Question with answer reveal |
| 8 | Exam | Exam date banner |
| 9 | Exam | Countdown number |
| 10 | Study tip | Numbered tip |
| 11 | Study tip | Note card |
| 12 | Subject | Subject cover, photo on top |
| 13 | Subject | Course code badge |
| 14 | Promotion | Feature highlight |
| 15 | Promotion | Download call to action |
| 16 | Reminder | Checklist |
| 17 | Reminder | Weekly schedule |
| 18 | Achievement | Milestone number |
| 19 | Announcement | Plain announcement |
| 20 | Announcement | Photo announcement |

The names can change. The rule is that two templates in the same family must differ in structure, not only in color.

## 5. Brand consistency rules

- Header: logo only, in one of three corners (left, center, right), size S / M / L. Default is the same on all 20 templates.
- Footer: one bar at the bottom with the icons and handles from the Brand screen, plus the website link. Same height and spacing on every template and every size.
- Colors: the palette is the brand colors only (primary, secondary, light, dark, plus 2 accents). A custom HEX field lives under More.
- Fonts: bundle Outfit and Plus Jakarta Sans, the same two used in the Academy app. Both are open licence.
- Logo source: the Academy app already has a 2000 x 2000 transparent logo.png (assets/brand/logo.png). Use it as the default so the first run already looks right.

## 6. Technical changes

1. **One renderer.** A single function draws a design onto an Android Canvas. The screen shows its bitmap, thumbnails use it at low resolution, export uses it at full size. This removes the preview/export mismatch.
2. **Document v2.** Replace the layer list with: template id, size, slot texts, style choices (colors, text size, background style), photo, and per-post brand switches. Keep the v1 reader for old drafts, convert what is possible, and drop the rest with one notice.
3. **Split the big file.** DesignStudio.kt becomes separate files for Templates, Editor, Brand and Export screens, plus a Templates data file and the renderer.
4. **Storage.** Copy imported logo and photos into app storage. Debounce autosave to about one second after the last change. Undo only tracks content and style changes, not selection.
5. **Text fitting.** Measure before drawing. If text does not fit, shrink to a minimum size, then show an overflow warning in the editor. Never cut lines silently.
6. **Language support.** Outfit and Plus Jakarta Sans have no Ge'ez letters. If you post in Amharic, add Noto Sans Ethiopic as a fallback in the renderer. See question 2 below.
7. **CI stays as it is.** The build runs only when started through the API. Do not add rules that run on push.

## 7. Order of work

Each phase ends with a debug APK you install and check on your phone. A phase is not done until its check passes.

- **Phase 1: foundation.** Document v2, one renderer, three sizes, bundled fonts, logo copied to storage. *Check:* one template exports identical to its preview at all three sizes, with long text.
- **Phase 2: brand.** Brand screen, locked header and footer, bundled icon set. *Check:* change a handle in Brand, see it on the canvas and in the export.
- **Phase 3: templates.** The 20 templates with slot layouts per ratio and generated thumbnails. *Check:* all 20 render at 3 sizes without overflow using sample text.
- **Phase 4: editor and navigation.** New Templates home, Editor with collapsed groups, My posts tab. *Check:* make a post from open-app to saved file in under a minute.
- **Phase 5: export sheet and cleanup.** Multi-size export, overflow warning, remove the old layer UI and PDF code. *Check:* export all three sizes in one tap, files open in Telegram.

## 8. Removed from the old plan

A4 and print export, PDF, layer panel as the main UI, rotation and opacity sliders, gradient editor, shape tools, 15-color swatch row on every layer, per-layer font choice on the main path, multi-project management beyond a simple list.

## 9. Questions to settle before Phase 1

1. Is 4:3 landscape (1440 x 1080) what you want, or portrait 3:4 (1080 x 1440)? The plan assumes landscape.
2. Do you post in Amharic as well as English? This decides the font work in section 6.
3. Which footer items do you want by default, and what are the exact handles and website link? The plan assumes Telegram, Website, TikTok and the live site address.
4. Should the logo always sit at the top, or do you want the three-corner choice?
