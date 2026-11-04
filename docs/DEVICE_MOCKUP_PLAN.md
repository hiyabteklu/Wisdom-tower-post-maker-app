# DEVICE_MOCKUP_PLAN: phone-frame mockup template (9:16, safe areas)

Written 4 Nov 2026 from a chat with the owner. T1 to T4 and T7 were done the same day (code pushed, NOT compiled). The assistant cannot compile: after each push, ask the owner to build and test. Work on `main` only (CLAUDE.md). Read the latest file from main before editing.

## 1. Goal

The owner has raw phone screenshots. They are very tall and look ugly posted as they are. He wants a template that takes a screenshot and puts it inside a phone frame on a 9:16 canvas (1080 x 1920) with proper safe areas, so a quick upload becomes a ready-to-post mockup. Used for ECE package screens, app screens and similar marketing posts.

**Owner decisions, 4 Nov 2026:** the phone is a TECNO CAMON 30 (screenshots 1080 x 2436, ratio 0.4433, about 9:20.3). The mockup template itself must be clean like a product shot: NO logo box, NO footer or links, no text. A reference photo (dark phone on a charcoal gradient, thin bezel, two buttons on the right edge) was the style target.

## 2. What the code already gives us (read on 4 Nov 2026)

- A template is data: `PostTemplateSpec(id, name, family, samples, look, layout: (PostSize) -> TemplateLayout)` in `PostTemplates.kt`. The list `PostTemplates = PhotoTemplates + MorePhotoTemplates + SplitTemplates + DeviceTemplates + InfoTemplates` decides what the strip and Library show.
- `TemplateLayout` holds `base`, `decor`, `slots`, `photo`, `backdrop`, `clean` and (new, 4 Nov) `bare`. `bare = true` makes `PostRenderer.render` skip the logo box, the footer and the social block whatever the post's switches say. All coordinates are canvas fractions.
- `PostPhotoArea(x, y, w, h, dim, mask)` is the photo window. `dim` is the dark layer on top, 0 to 255.
- `PostSize.STORY` is 1080 x 1920. Other sizes: `LANDSCAPE` 1440 x 1080 (4:3), `WIDE` 1920 x 1080. Any size without its own branch falls into the 1:1 branch.
- Photo drawing is `PostRenderer.drawPhoto`: it scales the picture to cover the window and crops. `doc.photoZoom` (minimum 1) and `doc.photoFocusX / photoFocusY` move the crop. It never shrinks the picture to fit.
- A photo frame only applies when the window is smaller than the canvas. `PhotoFrame.ROUNDED` uses a corner radius of `unit * 0.05` (54 px; `unit` is the shorter canvas side, 1080 on every size).
- `DecorBox.frameBack = 1` marks a shape as the backing of the photo, drawn in the chosen frame's shape.
- Header logo box (when not bare): top at 8.5% of the height on 9:16, about `1080 * 0.056 * logoScale * logoSize + 43` px tall, so it ends near 13.9%. Footer line at 91.5%, chips at 93.5%.

## 3. Safe area

Band for text on 9:16: 12% to 88% of the height. Keep about 6 to 8% free left and right. Because the hero template is bare (no logo, no footer), its phone may use more of the height (9% to 91% on 9:16). Platform-specific safe zones (Telegram, WhatsApp status, Instagram) were not verified from a source: ask the owner to judge on the phone.

## 4. Two ways to build it

**A. Template only (built 4 Nov 2026).** `PostTemplatesDevice.kt`. The phone is a fixed window of the TECNO ratio made of `DecorBox` shapes. The screenshot goes in the `PostPhotoArea`. Perfect for 1080 x 2436 screenshots. Phones with another shape lose a sliver at the edges, and the Photo tool's zoom and move sliders adjust it.

**B. Fit to the screenshot (only if cropping annoys the owner).** The layout gets a device area instead of a fixed window; the renderer reads the screenshot size, fits the largest screen rectangle of that shape inside a maximum box and draws the bezel around it. Needs a new field on `TemplateLayout`, a draw step in `PostRenderer` and the editor preview on the same path.

## 5. Numbers as built

Geometry is computed in pixels by `dvPhone(size, cx, top, bottom)`: bezel 28 px, screen ratio 1080/2436, body = screen + bezel, a 3 px light rim around the body, screen corner 54 px (the `ROUNDED` frame), body corner 82 px. Two side buttons on the right edge. Backdrop `DEEP` to `DARK` (not `PAGE`: the renderer sets the page colour after the backdrop is built, so `PAGE` would read as white).

**device-hero ("Phone mockup"), bare:** no text slots, no logo, no footer, no glow. 9:16: body y 9% to 91% (about 729 x 1574 px), centred. Other sizes: y 8% to 92%, centred.

**device-headline ("Phone and headline"), not bare (keeps logo and footer):** 9:16: eyebrow y 17.2%, headline y 20.5% to 28.7%, centred; body y 29.2% to 88%. 1:1 and 4:5: words left, phone right (centre x 73.5%, y 20% to 84%). 4:3 and 16:9: words left, phone right (centre x 74%, y 16% to 86%). Soft accent glow behind the phone. To make it bare too, pass `bare = true` in `deviceHeadlineLayout`.

The template look sets `PhotoFrame.ROUNDED`; if the owner turns the frame off in the Photo tool the screen corners become square.

## 6. Tasks (tick each in the same push as the code)

- [x] T1. `ROUNDED` radius is `unit * 0.05` = 54 px: used as the screen radius, body sized from it. No new field.
- [x] T2. `PostTemplatesDevice.kt`: ids `device-hero` and `device-headline`, own 9:16, 1:1 and 4:3 placements. Pushed, NOT compiled.
- [x] T3. Registered in `PostTemplates` (38), doc comment and README count updated.
- [x] T4. Dark backdrop, brand tones only.
- [x] T4b. 4 Nov 2026: TECNO CAMON 30 ratio, bare hero (`TemplateLayout.bare`, two small edits in `PostRenderer.render`), thin rim, right-side buttons. Pushed, NOT compiled.
- [ ] T5. Check on the phone: hero phone fills the 9:16 canvas cleanly; headline variant clears the logo box and footer.
- [ ] T6. Brand kit: add both templates to `TEMPLATES.md` in `hiyabteklu/wisdom-tower-brand-kit` (boxes and limits per box; the hero has NO text boxes, the headline has eyebrow and headline). The same numbers must go into the Supabase table `wt_post.template_boxes`, which refuses unknown template ids at save. Only the owner or the dev agent changes that table: prepare the exact rows and ask.
- [x] T7. CHAT_CONTROL_PLAN C4 "Photos in order" reuses Batch B5's multi-photo picker and is ticked (source pushed, build and phone check pending). Not tested.
- [ ] T8. Template QA: run Check all (Stress mode) on the new templates and send the report.
- [ ] T9. Optional, plan B.

## 7. Open questions for the owner

1. Should the headline variant also be bare (no logo, no footer)?
2. Is the thin bezel and dark body right, or should the body look closer to the reference (purple-black rim)?
3. Are all screenshots 1080 x 2436?

## 8. Status

- 4 Nov 2026: plan written. Same day: T1 to T4, T4b and T7 done in code and docs. Nothing compiled. Next: owner builds, opens Phone mockup on 9:16 with a TECNO CAMON 30 screenshot, then T5, T6 and T8.
