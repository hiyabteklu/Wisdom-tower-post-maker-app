# Wisdom Tower Post: redesign plan

Goal: one app that makes 10-20 consistent posts a day for TikTok, Instagram, Facebook, LinkedIn and Telegram, so Canva is not needed. 25 handpicked templates, each clearly different, covering photo posts, information posts and marketing posts.

## 1. What is wrong today (found by reading the code)

1. **There are 36 templates, but they share one recipe.** Gradient backdrop, then eyebrow + headline + body text, then decoration. PostLooks.kt adds rings, dot grids, stripes, glow circles and small shapes to almost every template, and PostRich.kt adds more when "Rich" is on (the default). That is why they look alike and cluttered.
2. **The sine wave.** DecorShape.WAVE is used by 5 templates (motivation-split, subject-cover, subject-badge, announce-plain, wave-announce).
3. **Dead code.** DIAMOND, TRIANGLE and HEXAGON draw nothing in PostRenderer.drawDecor (`-> Unit`), yet templates and PostLooks still place them.
4. **Photos are weak.** Only 4 templates have a photo slot (quote-photo, subject-cover, subject-badge, announce-photo). The only shapes are a rectangle and a pentagon. No rounded, arch, pill or stacked frames. No cut-out subject, so no "text behind the subject".
5. **Text cannot be dragged.** Slot positions are fixed fractions in SlotBox. Only the logo and icons can be dragged.
6. **The logo is unreliable.** It is drawn as a bare white image on whatever is behind it. On the many light templates it is almost invisible. It can sit left, center or right, and can also be dragged anywhere (logoCx / logoCy), so placement is not consistent.
7. **Old studio is still in the app.** DesignStudio.kt, DesignDocument.kt, DesignExport.kt (about 75 KB), reached from PostApp (`screen == "old"`) and the Brand > About button (`onOpenOldStudio`). Note: PostStudio.kt is the NEW editor. Do not delete it.
8. **Export is 1080 px.** Sizes are 1:1 1080x1080, 4:3 1440x1080, 9:16 1080x1920. Everything is stored as fractions, so a bigger export is a small change.
9. **UI is hard to read.** Section hints and control labels are 11 sp semibold muted grey-green on dark green; chips are 12 sp; the editor is one long scroll of 8 sections.

## 2. New design philosophy

Remove: rings, dot grids, stripes, waves, small diamonds/triangles/hexagons, the Clean/Rich switch, PostLooks.kt, PostRich.kt.

Use instead:
- **Real gradients.** Multi-stop linear gradients, plus soft radial "mesh" blends (2-3 large blurred color areas), plus a very light grain overlay so gradients do not band at 4K.
- **Hard-edged structure.** Color blocks, straight diagonal cuts, thin rules, big cropped type used as a graphic, cards with soft shadow.
- **Curated palettes per template** anchored on the brand green (deep forest, emerald, mint, gold, cream, ink). Brand colors still drive them; the per-post accent override still works.
- **Hierarchy through type size and contrast**, not decoration.

## 3. The 25 templates

Each is a different structure, not a recolor. Every one works at 1:1, 4:3 and 9:16 with its own layout per size.

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

The few current templates you already like are kept and rebuilt into this list (to be confirmed by you). Old template ids map to the nearest new one so saved drafts still open.

## 4. Features

- **Photo frames:** mask types ROUNDED, ARCH, PILL, CIRCLE, STACKED, with optional border and shadow. Zoom and pan stay.
- **Text behind subject:** layer order for these templates is background, back text, cut-out subject, front text, header, footer. A slot gets a layer (BACK or FRONT). The cut-out comes from a transparent PNG you import, or from an in-app "Cut out subject" button using on-device segmentation (to be tested on your phone; PNG import is the fallback).
- **Drag text in every template:** each slot gets a saved offset (and optional size). Tap a text block on the preview to select it, drag to move, "Reset position" to go back. Defaults stay exactly as designed. Saved in the post file (version 4); old drafts still load.
- **Header box (logo):** a fixed brand-gradient box flush to the top-right corner, with the white logo inside it, so it reads on any background. Same place on every template and every size. Remove the corner picker, free logo drag and logo-move mode. Keep a show/hide switch. On 9:16 it sits a little lower so Instagram and TikTok top buttons do not cover it.
- **4K export:** Standard (1080 base) or 4K: 1:1 2160x2160, 4:3 2880x2160, 9:16 2160x3840. PNG or JPG (JPG quality 95). Photos are decoded at full resolution for 4K. Note: TikTok and Instagram recompress to about 1080, so 4K matters most for LinkedIn, Telegram files and keeping masters.
- **Remove old studio:** delete the three Design*.kt files, the `old` route in PostApp, the Brand > About button, and any leftover storage keys.
- **UI:** bundled Plus Jakarta Sans / Outfit in the app UI, bold weights, body 15 sp minimum, labels 13 sp bold, lighter text colors (target 7:1 contrast on text, 4.5:1 minimum for secondary), 48 dp touch targets. Editor: Words open first, other sections collapsed.

## 5. Order of work

I cannot compile here, so each phase goes on branch `redesign/v2` as a pull request. You start the build through the API, install the APK and check.

- **Phase 0: cleanup.** Remove old studio. *Check:* app builds and Brand screen has no old studio entry.
- **Phase 1: foundation.** Header box, remove waves/rings/dots/stripes/gems and Clean/Rich, 4K export, UI theme and fonts. *Check:* logo readable on a light and a dark template; 4K files open at the right size.
- **Phase 2: engine.** Gradient/mesh/grain, photo masks, layered cut-out, slot offsets and drag, document v4. *Check:* drag a headline, save, reopen, export: same position.
- **Phase 3: templates in three batches** (photo 7, information 8, marketing + type 10). *Check:* every template renders at 3 sizes with long text and no overflow warning.
- **Phase 4: library and polish.** Category chips (Photo, Information, Marketing, Type), old-id mapping, final UI pass.

## 6. Questions for the owner

1. Which current templates do you consider usable (the fewer than 5)? They are the style reference.
2. OK to use on-device automatic cut-out, with PNG import as backup?
3. Add 4:5 (1080x1350, Instagram feed) next to 1:1, 4:3, 9:16?
4. Header box on 9:16: lower by about 9% to clear app buttons. OK?
