# DEVICE_MOCKUP_PLAN: phone-frame mockup template (9:16, safe areas)

Written 4 Nov 2026 from a chat with the owner. Nothing in this file is built yet. The assistant cannot compile: after each push, ask the owner to build and test. Work on `main` only (CLAUDE.md). Read the latest file from main before editing.

## 1. Goal

The owner has raw phone screenshots. They are very tall (about 9:20) and look ugly posted as they are. He wants a template that takes a screenshot and puts it inside a phone frame on a 9:16 canvas (1080 x 1920) with proper safe areas, so a quick upload becomes a ready-to-post mockup. Used for ECE package screens, app screens and similar marketing posts.

## 2. What the code already gives us (read on 4 Nov 2026, commit fe3151d)

- A template is data: `PostTemplateSpec(id, name, family, samples, look, layout: (PostSize) -> TemplateLayout)` in `PostTemplates.kt`. The list `PostTemplates = PhotoTemplates + MorePhotoTemplates + SplitTemplates + InfoTemplates` decides what the strip and Library show.
- `TemplateLayout` holds `base`, `decor: List<DecorBox>`, `slots: List<SlotBox>`, `photo: PostPhotoArea?`, `backdrop`, `clean`. All coordinates are canvas fractions (0 to 1).
- `PostPhotoArea(x, y, w, h, dim, mask)` is the photo window. `dim` is the dark layer on top, 0 to 255.
- `PostSize.STORY` is 1080 x 1920. Other sizes: `LANDSCAPE` 1440 x 1080 (4:3), `WIDE` 1920 x 1080. Any size without its own branch falls into the 1:1 branch.
- Best pattern to copy: `PostTemplatesPhoto2.kt` (Circle portrait, Testimonial). Helpers there: `pqPick(square, wide, story)`, `pqText`, `pqBlock`, `pqRect`, `pqCircleBox`, `pqGlow`, `pqTemplate`. They are `private`, so a new file needs its own copies (or make them `internal` in a shared place).
- Photo drawing is `PostRenderer.drawPhoto`: it scales the picture to cover the window (`scale = max(dest.w / bmp.w, dest.h / bmp.h) * zoom`) and crops. `doc.photoZoom` (minimum 1) and `doc.photoFocusX / photoFocusY` (0 to 1) move the crop. It never shrinks the picture to fit.
- A photo frame (`PhotoFrame.ROUNDED`, `ARCH`, `PILL`, `CIRCLE`, `STACKED`) only applies when the window is smaller than the canvas (`area.w < 0.99 && area.h < 0.99`). The path comes from `PostEngine.framePath(frame, rect, unit)`. The corner radius used by `ROUNDED` is not read yet.
- `DecorBox.frameBack = 1` marks a shape as the backing of the photo, drawn in the chosen frame's shape. `overPhoto = true` draws a shape above the photo (use it for a notch pill or glass).
- Rules every template follows: `clean = true` (done by `pqTemplate`), no `DIAMOND`, `TRIANGLE` or `HEXAGON` (the renderer draws nothing for them), own placement for 1:1, 4:3 and 9:16, text between 12% and 88% of the height, and on 9:16 text starts at 16% so the lowered logo box stays clear. Every slot a template uses needs a sample text.
- Header logo box sits top-right and is lowered on 9:16. The footer owns the bottom edge. Their exact heights were not measured.

## 3. Safe area (use the app's own rule, check on the phone)

Band for content on 9:16: 12% to 88% of the height = y 230 to 1690 px. Keep about 6 to 8% free left and right. Text starts at 16% (y 307). Check against the logo box and the footer when testing. Platform-specific safe zones (Telegram, WhatsApp status, Instagram) were not verified from a source: ask the owner to judge on the phone.

## 4. Two ways to build it

**A. Template only (build first).** One new file `PostTemplatesDevice.kt`. The phone is a fixed 9:20 window made of `DecorBox` shapes (outer body, bezel, optional side buttons and notch pill). The screenshot goes in the `PostPhotoArea`. Perfect for 9:20 screenshots. Phones with another shape lose a sliver at the edges, and the Photo tool's zoom and move sliders adjust it. No renderer change.

**B. Fit to the screenshot (build second, if cropping annoys the owner).** The layout gets a device area instead of a fixed window. At draw time the renderer reads the screenshot's width and height, fits the largest screen rectangle of that shape inside a maximum box, and draws the bezel around it. Needs a new field on `TemplateLayout` (for example `device: DeviceArea?`), a draw step in `PostRenderer`, and the editor preview to use the same path. The renderer is large, so read only `drawPhoto` and the call site before editing.

## 5. Starting numbers for 9:16 (1080 x 1920, screenshot ratio 9:20)

Phone outer corner radius about 90 px (radius .083 of the shorter side), screen radius about 68 px (.063), bezel 22 px. Tune on the phone.

**Variant 1, hero (no headline, optional small eyebrow label):**
- Outer body: x .185, y .120, w .631, h .760 (681 x 1460 px, y 230 to 1690).
- Screen window: x .205, y .131, w .590, h .738 (637 x 1416 px, exactly 9:20).

**Variant 2, with headline:**
- Eyebrow and headline in y .172 to .281 (about y 330 to 540), centered, clear of the logo box.
- Outer body: x .253, y .292, w .494, h .589 (533 x 1130 px, y 560 to 1690).
- Screen window: x .274, y .303, w .453, h .566 (489 x 1086 px).

**Other sizes (required by the app, keep simple):** 1:1 and 4:3 get a smaller centered phone with the same proportions. `pqPick(square, wide, story)` handles the three branches.

Notch pill: optional. Android screenshots already carry their own status bar, so a pill may look doubled. Make it a `DecorBox` with `overPhoto = true` and decide on the phone.

## 6. Tasks (tick each in the same push as the code)

- [ ] T1. Read `PostEngine.framePath` and check the `ROUNDED` radius against the screen radius above. If it does not match, add a radius to the template (a field on `PostPhotoArea` or a new `PhotoFrame.PHONE`) rather than faking it with shapes.
- [ ] T2. Create `PostTemplatesDevice.kt`: two templates, family "Photo" (or a new family if the strip supports it), ids `device-hero` and `device-headline`, 9:16 placements from section 5, simple 1:1 and 4:3 placements. Sample texts for every slot.
- [ ] T3. Register in `PostTemplates` (add the new list in `PostTemplates.kt`). Update the doc comment that says "The 36 templates" and the counts in README and `docs/` where they are stated.
- [ ] T4. Backdrop and look: a dark brand backdrop (for example `Backdrop(PostTone.DEEP, PostTone.PAGE, 150f)`) with a soft `pqGlow` behind the phone. Phone body dark, bezel darker. Use brand tones only.
- [ ] T5. Check the logo box and footer clearance on 9:16 on the phone. Adjust y values.
- [ ] T6. Brand kit: add both templates to `TEMPLATES.md` in `hiyabteklu/wisdom-tower-brand-kit` (boxes and limits per box). The same numbers must go into the Supabase table `wt_post.template_boxes`, which refuses unknown template ids at save. Only the owner or the dev agent changes that table: prepare the exact rows and ask.
- [ ] T7. Batch with screenshots: check whether BATCH_PLAN slice B5 ("Photos in order") and CHAT_CONTROL_PLAN C4 are built. With them, picking many screenshots gives one mockup post per screenshot in order. If they are not built, tell the owner.
- [ ] T8. Template QA: run Check all (Stress mode) on the new templates and send the report.
- [ ] T9. Optional, plan B: fit-to-screenshot device area (section 4).

## 7. Open questions for the owner

1. Hero only, headline variant only, or both?
2. Light or dark phone body? Notch pill or none?
3. Should the post also carry a small brand label under the phone (for example the app name)? Footer items come from the Brand screen.
4. Is plan B wanted now, or after the first test of plan A?
5. Which phones do the screenshots come from (ratio 9:19.5, 9:20, 9:21)?

## 8. Status

- 4 Nov 2026: plan written, nothing built. Owner continues in a new chat. Start with T1 and T2, then ask the owner to build and send a 9:16 screenshot with one tall and one short screenshot loaded.
