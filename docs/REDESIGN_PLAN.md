# Wisdom Tower Post: redesign plan

Goal: one app that makes 10-20 consistent posts a day for TikTok, Instagram, Facebook, LinkedIn and Telegram, so Canva is not needed. 25 handpicked templates, each clearly different, covering photo posts, information posts and marketing posts.

**Working rule (owner's standing rule): all work is pushed straight to `main`. No branches, no pull requests, ever.**

## Decisions (owner approved)

- All recommendations below are accepted.
- On-device automatic subject cut-out, with transparent PNG import as backup.
- Add 4:5 (1080x1350, Instagram feed) next to 1:1, 4:3 and 9:16.
- 9:16 header box lowered about 8.5% to clear Instagram / TikTok top buttons.
- Keep at least one template from each existing family (Quote, Motivation, Question, Exam, Study tip, Subject, Promotion, Reminder, Achievement, Announcement, Social, Event, Fact), but every sine-wave / high-amplitude cut is gone for good, and the template is rebuilt around a real structure.
- Design bar: a template that is only "text on a gradient" does not ship. Each one needs a signature structure (photo frame, cut-out layering, floating card, title panel, giant type used as a graphic, split block) so it works as a repeatable brand format used many times a week.

## Status

- [x] Phase 0: old studio removed (DesignStudio, DesignDocument, DesignExport, route in PostApp, Brand > About button).
- [x] Header box: logo sits in a dark brand-gradient box flush to the top-right on every template and size (lowered on 9:16). Logo corner choice removed from the Brand screen.
- [ ] Remove waves, rings, dot grids, stripes, gems, Clean/Rich switch, PostLooks.kt, PostRich.kt (done together with the new templates so nothing is left flat).
- [ ] 4K export and 4:5 size.
- [ ] Drag text, photo frames, cut-out layering, new gradient engine.
- [ ] 25 new templates.
- [ ] UI pass (bold fonts, contrast) on the editor and library.

## 1. What was wrong (found by reading the code)

1. **There were 36 templates, but they shared one recipe.** Gradient backdrop, then eyebrow + headline + body text, then decoration. PostLooks.kt adds rings, dot grids, stripes, glow circles and small shapes to almost every template, and PostRich.kt adds more when "Rich" is on (the default).
2. **The sine wave.** DecorShape.WAVE is used by 5 templates (motivation-split, subject-cover, subject-badge, announce-plain, wave-announce).
3. **Dead code.** DIAMOND, TRIANGLE and HEXAGON draw nothing in PostRenderer.drawDecor (`-> Unit`), yet templates and PostLooks still place them.
4. **Photos are weak.** Only 4 templates have a photo slot. The only shapes are a rectangle and a pentagon. No rounded, arch, pill or stacked frames. No cut-out subject, so no "text behind the subject".
5. **Text cannot be dragged.** Slot positions are fixed fractions in SlotBox. Only icons could be dragged.
6. **The logo was unreliable.** A bare white image on whatever was behind it, nearly invisible on light templates, movable anywhere. (Fixed by the header box.)
7. **Old studio was still in the app.** (Removed.)
8. **Export is 1080 px.** Everything is stored as fractions, so a bigger export is a small change.
9. **UI is hard to read.** 11 sp semibold muted labels, 12 sp chips, one long scroll of 8 sections.

## 2. New design philosophy

Remove: rings, dot grids, stripes, waves, small diamonds/triangles/hexagons, the Clean/Rich switch, PostLooks.kt, PostRich.kt.

Use instead:
- **Real gradients.** Multi-stop linear gradients, plus soft radial "mesh" blends (2-3 large blurred color areas), plus a very light grain overlay so gradients do not band at 4K.
- **Hard-edged structure.** Color blocks, straight diagonal cuts, thin rules, big cropped type used as a graphic, cards with soft shadow.
- **Curated palettes per template** anchored on the brand green (deep forest, emerald, mint, gold, cream, ink). Brand colors still drive them; the per-post accent override still works.
- **Hierarchy through type size, layout and contrast**, not decoration.

## 3. The 25 templates

Each is a different structure, not a recolor. Every one works at 1:1, 4:5, 4:3 and 9:16 with its own layout per size.

**Photo (7)**
1. Arch Window: photo in an arch, text beside/below
2. Rounded Card: large rounded-corner photo, floating caption card
3. Stacked Frames: two offset frames, layered
4. Duotone Full-bleed: photo with brand gradient overlay, headline at the bottom
5. Behind the Subject: cut-out subject in the lower part, giant headline behind it, caption in front
6. Cutout Spotlight: cut-out on a glowing gradient, text behind and beside
7. Pill Portrait: stadium-shaped photo with halo gradient, name and title

**Information (8)**
8. Editorial Quote 9. Numbered Tip 10. Quiz, four options 11. Question + Answer 12. Checklist 13. Weekly Timetable 14. Definition 15. Big Stat

**Marketing (7)**
16. Feature Spotlight (dark) 17. Download Call to Action 18. Event Ticket 19. Event Poster (date block) 20. Notice (title panel + action strip) 21. Milestone 22. Follow Us (icon cards)

**Type-led (3)**
23. Aurora Statement 24. Giant Ink Type (stacked huge words) 25. Split Block (straight diagonal cut, two tones)

Old template ids map to the nearest new one so saved drafts still open.

## 4. Features

- **Photo frames:** mask types ROUNDED, ARCH, PILL, CIRCLE, STACKED, with optional border and shadow. Zoom and pan stay.
- **Text behind subject:** layer order for these templates is background, back text, cut-out subject, front text, header, footer. A slot gets a layer (BACK or FRONT). The cut-out comes from a transparent PNG you import, or from an in-app "Cut out subject" button using on-device segmentation.
- **Drag text in every template:** each slot gets a saved offset (and optional size). Tap a text block on the preview to select it, drag to move, "Reset position" to go back. Defaults stay exactly as designed. Saved in the post file (version 4); old drafts still load.
- **Header box (logo):** done. Fixed dark brand-gradient box flush to the top-right corner with the white logo inside, same place on every template and size.
- **4K export:** Standard (1080 base) or 4K: 1:1 2160x2160, 4:5 2160x2700, 4:3 2880x2160, 9:16 2160x3840. PNG or JPG (quality 95). TikTok and Instagram recompress to about 1080, so 4K matters most for LinkedIn, Telegram files and keeping masters.
- **UI:** bundled Plus Jakarta Sans / Outfit in the app UI, bold weights, body 15 sp minimum, labels 13 sp bold, lighter text colors (7:1 contrast target), 48 dp touch targets. Editor: Words open first, other sections collapsed.

## 5. Order of work (all on main)

Because nothing here can be compiled by the assistant, every push should be built by you (start the build through the API) and checked on the phone. Fix reports go back as the next push.

- **Phase 0:** cleanup. Done.
- **Phase 1:** header box (done), 4K + 4:5, UI theme and fonts.
- **Phase 2:** engine: gradient/mesh/grain, photo masks, layered cut-out, slot offsets and drag, document v4.
- **Phase 3:** templates in three batches (photo 7, information 8, marketing + type 10), replacing PostTemplates.kt, PostLooks.kt and PostRich.kt.
- **Phase 4:** library categories (Photo, Information, Marketing, Type), old-id mapping, final UI pass.
