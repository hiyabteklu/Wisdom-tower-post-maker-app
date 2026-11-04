# DEVICE_MOCKUP_PLAN: phone-frame mockup template (9:16, safe areas)

Written 4 Nov 2026 from a chat with the owner. T1 to T4 and T7 were done the same day (code pushed, NOT compiled). The assistant cannot compile: after each push, ask the owner to build and test. Work on `main` only (CLAUDE.md). Read the latest file from main before editing.

## 1. Goal

The owner has raw phone screenshots. They are very tall (about 9:20) and look ugly posted as they are. He wants a template that takes a screenshot and puts it inside a phone frame on a 9:16 canvas (1080 x 1920) with proper safe areas, so a quick upload becomes a ready-to-post mockup. Used for ECE package screens, app screens and similar marketing posts.

## 2. What the code already gives us (read on 4 Nov 2026, commit fe3151d)

- A template is data: `PostTemplateSpec(id, name, family, samples, look, layout: (PostSize) -> TemplateLayout)` in `PostTemplates.kt`. The list `PostTemplates = PhotoTemplates + MorePhotoTemplates + SplitTemplates + DeviceTemplates + InfoTemplates` decides what the strip and Library show.
- `TemplateLayout` holds `base`, `decor: List<DecorBox>`, `slots: List<SlotBox>`, `photo: PostPhotoArea?`, `backdrop`, `clean`. All coordinates are canvas fractions (0 to 1).
- `PostPhotoArea(x, y, w, h, dim, mask)` is the photo window. `dim` is the dark layer on top, 0 to 255.
- `PostSize.STORY` is 1080 x 1920. Other sizes: `LANDSCAPE` 1440 x 1080 (4:3), `WIDE` 1920 x 1080. Any size without its own branch falls into the 1:1 branch.
- Best pattern to copy: `PostTemplatesPhoto2.kt` (Circle portrait, Testimonial). Its helpers are `private`, so `PostTemplatesDevice.kt` has its own copies with a `dv` prefix.
- Photo drawing is `PostRenderer.drawPhoto`: it scales the picture to cover the window (`scale = max(dest.w / bmp.w, dest.h / bmp.h) * zoom`) and crops. `doc.photoZoom` (minimum 1) and `doc.photoFocusX / photoFocusY` (0 to 1) move the crop. It never shrinks the picture to fit.
- A photo frame (`PhotoFrame.ROUNDED`, `ARCH`, `PILL`, `CIRCLE`, `STACKED`) only applies when the window is smaller than the canvas (`area.w < 0.99 && area.h < 0.99`). The path comes from `PostEngine.framePath(frame, rect, unit)`. `ROUNDED` uses a corner radius of `unit * 0.05` (54 px, `unit` = the shorter canvas side, 1080 on every size).
- `DecorBox.frameBack = 1` marks a shape as the backing of the photo, drawn in the chosen frame's shape. `overPhoto = true` draws a shape above the photo (use it for a notch pill or glass).
- Rules every template follows: `clean = true`, no `DIAMOND`, `TRIANGLE` or `HEXAGON` (the renderer draws nothing for them), own placement for 1:1, 4:3 and 9:16, text between 12% and 88% of the height, and on 9:16 text starts at 16% so the lowered logo box stays clear. Every slot a template uses needs a sample text.
- Header logo box sits top-right and is lowered on 9:16 (top at 8.5% of the height). From `PostRenderer.drawLogo` its height is about `1080 * 0.056 * logoScale * logoSize + 43` px, so it ends near 267 px (13.9% of 1920) at normal logo size. The footer line sits at 91.5% of the height, the footer chips at 93.5%.

## 3. Safe area (use the app's own rule, check on the phone)

Band for content on 9:16: 12% to 88% of the height = y 230 to 1690 px. Keep about 6 to 8% free left and right. Text starts at 16% (y 307). Platform-specific safe zones (Telegram, WhatsApp status, Instagram) were not verified from a source: ask the owner to judge on the phone.

## 4. Two ways to build it

**A. Template only (built first, 4 Nov 2026).** One new file `PostTemplatesDevice.kt`. The phone is a fixed 9:20 window made of `DecorBox` shapes (outer body, bezel, side buttons). The screenshot goes in the `PostPhotoArea`. Perfect for 9:20 screenshots. Phones with another shape lose a sliver at the edges, and the Photo tool's zoom and move sliders adjust it. No renderer change.

**B. Fit to the screenshot (build second, if cropping annoys the owner).** The layout gets a device area instead of a fixed window. At draw time the renderer reads the screenshot's width and height, fits the largest screen rectangle of that shape inside a maximum box, and draws the bezel around it. Needs a new field on `TemplateLayout` (for example `device: DeviceArea?`), a draw step in `PostRenderer`, and the editor preview to use the same path. The renderer is large, so read only `drawPhoto` and the call site before editing.

## 5. Numbers as built (1080 x 1920, screenshot ratio 9:20)

The geometry is computed in pixels by `dvPhone(size, cx, top, bottom)`: bezel 22 px on every side, screen exactly 9:20, body = screen + bezel. Screen corner = 54 px (the `ROUNDED` frame), body corner = 76 px (.0704 of the shorter side). Side buttons: one on the right, two on the left, 9 px wide, 6 px showing.

**device-hero ("Phone mockup"), 9:16:** body y 15% to 88% (288 to 1690 px), centred, about 655 x 1402 px; screen about 611 x 1358 px. Starts at 15%, not the 12% first planned, because the logo box ends near 14% and would cover the top right corner of the phone. One small eyebrow label at y 12.2%, left, level with the phone edge. 1:1, 4:5 and 4:3: phone y 17% to 88%, centred, label centred above it.

**device-headline ("Phone and headline"), 9:16:** eyebrow y 17.2%, headline y 20.5% to 28.7%, centred; body y 29.2% to 88% (560 to 1690 px), about 533 x 1130 px. 1:1 and 4:5: words left (x 7%, width 46%), phone right (centre x 73.5%, y 20% to 84%). 4:3 and 16:9: words left (width 40%), phone right (centre x 74%, y 16% to 86%).

Look: dark base, backdrop `GLOW` to `DEEP` (not `PAGE`: the renderer sets the page colour after the backdrop is built, so `PAGE` would read as white), soft accent glow behind the phone, body in the brand Dark colour with a shadow. No notch pill: Android screenshots already carry their own status bar. The template look sets `PhotoFrame.ROUNDED`; if the owner turns the frame off in the Photo tool the screen corners become square.

## 6. Tasks (tick each in the same push as the code)

- [x] T1. Read `PostEngine.framePath`: `ROUNDED` radius is `unit * 0.05` = 54 px. Decision: use it as the screen radius and size the body from it (54 + 22 bezel = 76 px) instead of adding a new field, so no renderer change.
- [x] T2. `PostTemplatesDevice.kt`: two templates, family "Photo", ids `device-hero` and `device-headline`, own 9:16, 1:1 and 4:3 placements, sample text for every slot. Pushed, NOT compiled.
- [x] T3. Registered in `PostTemplates` (now 38), doc comment and README count updated. Pushed, NOT compiled.
- [x] T4. Dark brand backdrop with a soft glow behind the phone, brand tones only. Pushed, NOT compiled.
- [ ] T5. Check the logo box and footer clearance on 9:16 on the phone. Adjust y values (hero top at 15%, headline text from 17.2%).
- [ ] T6. Brand kit: add both templates to `TEMPLATES.md` in `hiyabteklu/wisdom-tower-brand-kit` (boxes and limits per box). The same numbers must go into the Supabase table `wt_post.template_boxes`, which refuses unknown template ids at save. Only the owner or the dev agent changes that table: prepare the exact rows and ask.
- [x] T7. Checked 4 Nov 2026: CHAT_CONTROL_PLAN C4 "Photos in order" reuses Batch B5's multi-photo picker and is ticked (source pushed, build and phone check pending). So once built, picking many screenshots should give one mockup post per screenshot in order. Not tested.
- [ ] T8. Template QA: run Check all (Stress mode) on the new templates and send the report.
- [ ] T9. Optional, plan B: fit-to-screenshot device area (section 4).

## 7. Open questions for the owner

1. Hero only, headline variant only, or both? (Both are built.)
2. Light or dark phone body? (Dark is built.) Notch pill or none? (None is built.)
3. Should the post also carry a small brand label under the phone (for example the app name)? Footer items come from the Brand screen.
4. Is plan B wanted now, or after the first test of plan A?
5. Which phones do the screenshots come from (ratio 9:19.5, 9:20, 9:21)?

## 8. Status

- 4 Nov 2026: plan written. Same day: T1 to T4 and T7 done in code and docs (commits d273aa2, aa6d5bd and the one adding this file). Nothing compiled. Next: owner builds, opens the Phone mockup and Phone and headline templates with one tall and one short screenshot on 9:16, then T5, T6 and T8.
