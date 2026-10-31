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
- [x] Engine, first push (not yet built or run, the assistant cannot compile): PostEngine.kt (photo frames ROUNDED / ARCH / PILL / CIRCLE / STACKED with border and shadow, mesh gradient blends AURORA / SPOTLIGHT / DUSK, grain, cut-out subject layer, text offsets), document version 4 in PostModel.kt, renderer order background > mesh > grain > shapes > photo > words set "behind" > cut-out > other words, and the studio section "Layers and style" with tap-and-drag for words and the subject on the preview.
- [x] Phase 3, batch 1 (photo templates, 7), pushed, not yet built or run: PostTemplatesPhoto.kt has Arch window, Rounded card, Stacked frames, Duotone full-bleed, Behind the subject, Cutout spotlight, Pill portrait (family "Photo", ids `photo-arch`, `photo-card`, `photo-stack`, `photo-duotone`, `photo-behind`, `photo-spotlight`, `photo-pill`). They are first in `PostTemplates`. Each has its own placement for 1:1, 4:3 and 9:16 (any other size falls into the 1:1 branch until it gets its own).
  - How it works: a template can carry a `TemplateLook` (photo frame, mesh, grain, words behind the subject, cut-out position). `PostDoc.withTemplate` applies it when the template is picked; the studio can still change each one. Old templates have no look and change nothing.
  - The new layouts are marked `clean`, so PostRich adds nothing behind them, and they skip PostLooks. They use no diamond / triangle / hexagon shapes (the renderer draws nothing for those).
  - Behind the subject and Cutout spotlight need a transparent PNG of the subject (Layers and style > cut-out). Without one they show only the giant headline on its glow.
  - Known small gap: the template gallery cards are built from the template's sample text only (PostEditorTabs), so they do not show the look (mesh, grain). The editor does.
- [x] Phase 3, batch 2 (information templates, 8), pushed, not yet built or run: PostTemplatesInfo.kt has Editorial quote, Numbered tip, Quiz (four options), Question and answer, Checklist, Weekly timetable, Definition, Big stat (family "Information", ids `info-quote`, `info-tip`, `info-quiz`, `info-qa`, `info-checklist`, `info-timetable`, `info-definition`, `info-stat`). They come right after the photo templates in `PostTemplates` (`PhotoTemplates + InfoTemplates + old list`).
  - Same rules as batch 1: marked `clean`, no photo slot (text only), own placement for 1:1, 4:3 and 9:16 (other sizes use the 1:1 branch, through a small `size.pick(square, wide, story)` helper), only rectangles, rounded blocks, discs and thin rules. On 9:16 content starts at 16% of the height so the lowered logo box stays clear.
  - The Q / A badges and the tick badge are a disc plus a fixed letter (`inBadge`). The letter is centered by an estimate of the font's metrics, so check on the phone that the letters sit in the middle of their discs (the tick uses the BOLD font and may need a nudge).
  - The list templates (Quiz, Checklist, Weekly timetable) draw their markers and rules themselves; the card behind the list is fixed, so a very long list shrinks its text instead of growing the card.
  - They use the same sample text as the old Question / Reminder / Study tip templates, so a post that switches between old and new keeps its words.
- [x] Phase 3, batch 3 (marketing and type templates, 10), pushed, not yet built or run: PostTemplatesMarketing.kt has Feature spotlight, Download call to action, Event ticket, Event poster, Notice, Milestone, Follow us (family "Marketing", ids `mkt-feature`, `mkt-download`, `mkt-ticket`, `mkt-poster`, `mkt-notice`, `mkt-milestone`, `mkt-follow`) and Aurora statement, Giant ink type, Split block (family "Type", ids `type-aurora`, `type-ink`, `type-split`). That completes the 25 new templates (7 photo + 8 information + 10 marketing and type).
  - Registration: `InfoTemplates` in PostTemplatesInfo.kt is now `InfoOnlyTemplates + MarketingTemplates`, so PostTemplates.kt did not have to be resent. Order in the list is photo, information, marketing, type, then the old templates. Phase 4 should move this into a proper `PostTemplates` line when the old list is removed.
  - Same rules as batches 1 and 2: marked `clean`, own placement for 1:1, 4:3 and 9:16 (other sizes use the 1:1 branch through `size.mkPick`), only rectangles, rounded blocks, discs, soft glows and thin rules (no rings, dots, stripes, waves or gems). On 9:16 content starts at 16% to 17% of the height.
  - Words inside a button, a tab or a date block (Download button, Notice tab and button, Poster date) are placed by `mkLine`, which centers one line on a point using an estimate of the font's metrics, like the badges in batch 2. Check on the phone that they sit in the middle of their shapes.
  - Event ticket: the ticket and its stub are two separate blocks with a thin line in the gap (no notches, because the page behind is a gradient). Milestone has a thin rule on each side of its label. Follow us uses the Brand icons as big GRID cards (`SocialArea`), so the small footer is left out there.
  - Split block: the plan said a diagonal cut, but the renderer only draws axis-aligned rectangles, so it uses a straight vertical cut on 1:1 and 4:3 and a straight horizontal cut on 9:16. The colored block stops at 88% of the height on 1:1 and 4:3 so the footer stays readable on the page.
  - Giant ink type expects a headline of short words (one word per line at that size). A long headline shrinks.
- [ ] Engine, still to do: on-device automatic cut-out (needs an ML dependency, so a transparent PNG import is what exists now), pinch to resize words.
- [ ] Remove waves, rings, dot grids, stripes, gems, Clean/Rich switch, PostLooks.kt, PostRich.kt, and the old template list in PostTemplates.kt (all 25 new templates now exist, so this is next). Map old template ids to the nearest new one so saved drafts still open.
- [ ] 4K export and 4:5 size (done with the template rewrite, because every old layout has an exhaustive `when (size)` that has to learn the new size; the new photo, information and marketing templates already use if / else or `pick`, so they will not break).
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

**Photo (7)** (batch 1, pushed)
1. Arch Window: photo in an arch, text beside/below
2. Rounded Card: large rounded-corner photo, floating caption card
3. Stacked Frames: two offset frames, layered
4. Duotone Full-bleed: photo with brand gradient overlay, headline at the bottom
5. Behind the Subject: cut-out subject in the lower part, giant headline behind it, caption in front
6. Cutout Spotlight: cut-out on a glowing gradient, text behind and beside
7. Pill Portrait: stadium-shaped photo with halo gradient, name and title

**Information (8)** (batch 2, pushed)
8. Editorial Quote 9. Numbered Tip 10. Quiz, four options 11. Question + Answer 12. Checklist 13. Weekly Timetable 14. Definition 15. Big Stat

**Marketing (7)** (batch 3, pushed)
16. Feature Spotlight (dark) 17. Download Call to Action 18. Event Ticket 19. Event Poster (date block) 20. Notice (title panel + action strip) 21. Milestone 22. Follow Us (icon cards)

**Type-led (3)** (batch 3, pushed)
23. Aurora Statement 24. Giant Ink Type (stacked huge words) 25. Split Block (straight cut, two tones; vertical on 1:1 and 4:3, horizontal on 9:16)

Old template ids map to the nearest new one so saved drafts still open.

## 4. Features

- **Photo frames:** mask types ROUNDED, ARCH, PILL, CIRCLE, STACKED, with optional border and shadow. Zoom and pan stay. (Engine done; the photo templates now set them through their look.)
- **Text behind subject:** layer order for these templates is background, back text, cut-out subject, front text, header, footer. A slot gets a layer (BACK or FRONT). The cut-out comes from a transparent PNG you import, or from an in-app "Cut out subject" button using on-device segmentation. (Layering and PNG import done; the automatic button is still to do.) Shapes drawn before the cut-out cannot sit on top of the subject, so a caption in front of the subject must have enough contrast on its own, or sit below it.
- **Drag text in every template:** each slot gets a saved offset. Tap a text block on the preview to select it, drag to move, "Put words back" to go back. Defaults stay exactly as designed. Saved in the post file (version 4, offsets keyed by template and size); old drafts still load. (Done.)
- **Header box (logo):** done. Fixed dark brand-gradient box flush to the top-right corner with the white logo inside, same place on every template and size.
- **4K export:** Standard (1080 base) or 4K: 1:1 2160x2160, 4:5 2160x2700, 4:3 2880x2160, 9:16 2160x3840. PNG or JPG (quality 95). TikTok and Instagram recompress to about 1080, so 4K matters most for LinkedIn, Telegram files and keeping masters.
- **UI:** bundled Plus Jakarta Sans / Outfit in the app UI, bold weights, body 15 sp minimum, labels 13 sp bold, lighter text colors (7:1 contrast target), 48 dp touch targets. Editor: Words open first, other sections collapsed.

## 5. Order of work (all on main)

Because nothing here can be compiled by the assistant, every push should be built by you (start the build through the API) and checked on the phone. Fix reports go back as the next push.

- **Phase 0:** cleanup. Done.
- **Phase 1:** header box (done), 4K + 4:5, UI theme and fonts.
- **Phase 2:** engine: gradient/mesh/grain, photo masks, layered cut-out, slot offsets and drag, document v4. First push done, waiting for a build and a phone check.
- **Phase 3:** templates in three batches (photo 7, information 8, marketing + type 10), replacing PostTemplates.kt, PostLooks.kt and PostRich.kt, together with 4K and 4:5. All three batches are pushed and waiting for a build and a phone check. What is left of this phase is the removal of the old list, PostLooks.kt and PostRich.kt, with the 4:5 size and 4K export.
- **Phase 4:** library categories (Photo, Information, Marketing, Type), old-id mapping, final UI pass.
