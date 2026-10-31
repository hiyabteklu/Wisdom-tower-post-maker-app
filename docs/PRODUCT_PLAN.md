# Wisdom Tower Post — Product Plan

> **Direction changed.** Scope, screens and phases are now defined in docs/IMPROVEMENT_PLAN.md. Where the two documents disagree, the improvement plan wins. The sections on a full layer editor, A4 and PDF below are no longer the goal.

## 1. Product definition

Wisdom Tower Post is a **personal graphic-design studio for Wisdom Tower Academy**, built to make daily Telegram and social-media publishing fast without sacrificing design quality. It is not a text editor with colored backgrounds and it is not intended for public release.

The goal is to create a post once, explore different layouts, fine-tune the design, and export a polished result while preserving the Academy's brand.

## 2. Product principles

1. **Design first:** the canvas and its visual composition are the main experience.
2. **One source of content:** changing templates or canvas formats should preserve editable content wherever possible.
3. **Real layer editing:** every text, image, shape and decorative element is an independently selectable object.
4. **Brand consistency without creative restriction:** saved brand defaults are available, but each layer can be customized.
5. **Offline and private:** no login, backend, analytics or internet requirement for normal editing and export.
6. **Non-destructive workflow:** autosave, undo/redo, duplicate designs and easy recovery from edits.
7. **Export must match preview:** the rendered file should be produced from the same design document and layout rules as the canvas preview.

## 3. Core editor requirements

### A. Canvas formats and resizing

Required presets:
- **Square social post:** 1:1, default export 1080 × 1080.
- **Vertical social story/reel:** 9:16, default export 1080 × 1920.
- **A4 page:** portrait and landscape, print-ready export resolution.
- Preserve the design when changing formats: keep layers editable and provide a clear fit/reflow choice rather than silently deleting or flattening elements.

Planned additional presets: 4:5 portrait feed, 16:9 landscape, and custom dimensions.

### B. Twenty launch templates

Provide at least 20 genuinely designed, editable starting templates—not merely 20 color swaps. Each template must use the same layer system and remain customizable.

Initial template families:
1. Quote / saying of the day (3)
2. Study motivation / student mindset (2)
3. Daily question / MCQ (2)
4. Exam announcement / countdown (2)
5. Study tip / short note (2)
6. Subject or course spotlight (2)
7. App feature / product promotion (2)
8. Reminder / schedule / deadline (2)
9. Achievement / result / milestone (1)
10. Minimal branded announcement (2)

**Launch acceptance:** 20 distinct layouts are available in a template gallery with category labels, previews and search/filtering. Selecting one applies its layout without erasing the current draft; the user can choose whether to replace the whole design or only its style where that makes sense.

### C. Layer system

Layer types:
- Text
- Imported image/photo
- Shape (rectangle, rounded rectangle, circle, line)
- Background color or gradient
- Brand logo and footer/social marks
- Decorative elements

Layer actions:
- Select, move, resize and rotate
- Change stacking order (forward/backward; bring to front/send to back)
- Rename, duplicate and delete
- Hide/show and lock/unlock
- Edit text and layer-specific properties
- Multi-select/grouping is a later enhancement if implementation complexity requires it

A visible layer list/panel is required. Selecting a layer in the list selects it on the canvas, and selecting an object on the canvas highlights its layer. The selected layer's controls must be context-sensitive.

### D. Typography controls

- Edit text directly and choose font family from bundled, license-safe fonts.
- Font size, weight, alignment, line spacing, letter spacing and text color.
- Text box width/height and positioning.
- Optional text background, outline or shadow as supported by the renderer.
- Text wrapping and overflow indicators; no silent clipping in exported files.

### E. Color and gradient editor

- A ready-to-use palette of **15 visible color swatches**.
- Custom color picker and HEX entry.
- Apply colors to text, shapes, borders and backgrounds.
- Solid, linear-gradient and radial-gradient background/fill options where supported.
- Gradient editor with editable start/end colors, additional color stops, direction/angle, and live preview.
- Save frequently used custom colors locally.
- Changing one layer's color must not unexpectedly recolor unrelated layers.

### F. Image import and editing

- Import images from the Android photo picker or device files.
- Support multiple image layers in one design.
- Crop, contain/cover, reposition, resize, rotate and adjust opacity.
- Allow shapes or gradient overlays to improve text readability.
- Show a useful placeholder/error state if a source image becomes unavailable.
- Keep imported images local; never upload them automatically.

### G. Brand kit

Local brand settings:
- Import and replace the actual Academy logo.
- Brand colors and custom palette.
- Preferred fonts.
- Footer/tagline and social handles/icons.
- Optional watermark and default placement.
- Templates use the brand kit by default but do not make it impossible to override a particular layer.

Until actual assets are provided, use clearly marked placeholders rather than pretending the placeholder is the real logo.

### H. Draft and project management

- Autosave edits locally and restore the latest draft on reopen.
- Save multiple named designs and duplicate a design before experimenting.
- Recent projects gallery with thumbnails, date and canvas size.
- Undo/redo history for editing actions.
- Explicit reset/new-design action with confirmation; never silently discard work.
- No cloud sync in the initial personal version.

### I. Preview and export

- WYSIWYG preview from the same design model used for export.
- JPG and PDF export required; PNG is a useful follow-on.
- Resolution depends on selected canvas: social presets export at their target pixel dimensions; A4 supports print-oriented resolution.
- Export quality option for raster formats.
- PDF page size/orientation must correspond to the selected canvas.
- Save to suitable device folders and confirm the actual save location.
- Check for out-of-bounds objects, text overflow and low-resolution images before export.
- Sharing is user-initiated through Android's share sheet; nothing is posted automatically.

## 4. Proposed app navigation

1. **Projects** — recent designs, create new, duplicate/open.
2. **Templates** — 20+ categorized design starters.
3. **Editor** — canvas, selection handles, layer panel, contextual property controls.
4. **Brand Kit** — logo, colors, fonts, footer and social identity.
5. **Export** — format, dimensions/quality, warnings, save/share.

On a phone, panels should use bottom sheets or tabs so the canvas remains usable. The editor should not force a desktop-sized side panel onto a narrow screen.

## 5. Architecture direction

Keep the Android app native and offline-first, but replace the current one-off preview drawing with a reusable design-document model.

Suggested modules:
- **Design model:** document size, background/fill and ordered layer objects.
- **Editor state/actions:** selected layer, transforms, undo/redo and template application.
- **Renderer:** one rendering path shared by canvas preview and high-resolution export.
- **Template catalog:** versioned template definitions with layer data and thumbnail previews.
- **Local storage:** serialized design documents, brand settings and recent-project metadata.
- **Media repository:** references to imported local images, with permission-safe URI handling.
- **Export service:** JPG/PDF generation from the shared renderer.

Use stable layer IDs and store layer properties as data, not as individually hard-coded Compose screens. Keep template content separate from app UI. Avoid adding cloud services or unnecessary dependencies.

## 6. Delivery phases and acceptance gates

### Phase 0 — Product plan and design system
- Agree on scope, navigation, document model and initial template categories.
- Define brand-kit fields and export targets.
- **Gate:** plan reviewed before further feature implementation.

### Phase 1 — Design document foundation
- Build the canvas model, reusable renderer, selection state and local project persistence.
- Add undo/redo and ensure drafts survive app restarts.
- **Gate:** a saved editable design reopens with the same layers and layout.

### Phase 2 — Real layer editor
- Add text, shape and image layers; selection, transform, reorder, hide/lock, duplicate and delete.
- Add layer list and contextual properties.
- **Gate:** create and edit a design without flattening it into one image.

### Phase 3 — Brand, colors and gradients
- Add 15-color palette, custom color/HEX picker, solid fills and editable gradients.
- Add local logo, fonts, footer and social-brand settings.
- **Gate:** brand defaults are reusable and individual layers remain independently editable.

### Phase 4 — Canvas formats and image tools
- Add square, 9:16 and A4 portrait/landscape.
- Add image import, crop/fit, opacity, rotation and overlays.
- **Gate:** changing format preserves layers and allows correction of the composition.

### Phase 5 — Twenty launch templates
- Create 20 distinct layouts across the defined families.
- Add gallery previews, categories and template application rules.
- **Gate:** template switching never silently destroys the user's draft.

### Phase 6 — Export, QA and personal release
- Add matching JPG/PDF export at target dimensions and print resolution.
- Test long text, missing images, gradient rendering, A4 page bounds, save locations and reopen behavior on a real Android device.
- Produce a debug APK only after the build passes.
- **Gate:** export is visually consistent with preview and the build is verified.

Phases can overlap when useful, but a later phase must not be called complete until its acceptance gate is tested.

## 7. Definition of done

The app is ready for daily personal use when:
- At least 20 distinct templates exist.
- Square, 9:16 and A4 canvases work.
- Text, image, shape and background layers are independently editable.
- A 15-color palette, custom colors and editable gradients work.
- The brand kit can be updated without editing source code.
- Projects autosave, reopen and support undo/redo.
- JPG and PDF exports match the canvas composition and target dimensions.
- Core editing and export work offline.
- The APK compiles and key workflows are tested on an Android device.

## 8. Explicitly out of scope for the first personal release

- Public accounts, subscriptions, ads or Play Store publishing.
- Cloud collaboration or automatic social posting.
- AI image generation or online asset libraries.
- A full desktop-grade vector editor.

These can be considered later, but they must not displace the core editable-layer, template, brand and export workflow.

## 9. Current prototype — not the finished product

The existing repository contains an early Compose scaffold, a few starter previews, local draft fields and an initial export implementation. It has **not** yet met the product definition above, and the APK build/export quality must be verified. The next implementation work should begin from this plan and establish the reusable design-document/layer architecture rather than continuing to add isolated controls to the prototype.
