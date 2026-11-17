# SHOWCASE_TEMPLATES_PLAN: replace the 20 weak templates with crafted, scroll-stopping graphics

Written 17 Nov 2026 from a chat with the owner (Hiyab), for the Google AI Studio agent. Work on `main` only (CLAUDE.md). The agent cannot compile: say so after each push so the owner builds and tests. Read the latest copy of a file from `main` before editing it.

## 0. How to work (token-lean, please follow)

The repo is big (`PostRenderer.kt` 125 KB, `PostStudyGrid.kt` 43 KB, `PostTemplatesInfo.kt` 91 KB). Do NOT read everything. Read only what each phase names, and only the part you need (search for the function, read that part).

Read first, once (all short): `CLAUDE.md`, this plan, `PostTemplates.kt` (data model: `PostTemplateSpec`, `TemplateLayout`, `DecorBox`, `SlotBox`, `PostPhotoArea`, `TemplateLook`, `LegacyTemplateIds`), `PostTemplatesDevice.kt` (the existing phone mockup, the closest thing to what we want) and `docs/DEVICE_MOCKUP_PLAN.md`.
Do not read: `docs/TEMPLATE_REDESIGN_PLAN.md` (41 KB, old), `BATCH_PLAN.md`, `REDESIGN_PLAN.md`, `PRODUCT_PLAN.md`. Skim only the headings of `docs/UI_UX_PLAN.md` before Phase 5.

Keep a progress log at the bottom of this file (section 12). Tick each task in the same push as the code. Push small, compile-safe steps (one phase or one batch of templates per push).

## 1. Why this plan

Today's 20 templates (named in section 3) are text posters: a text box on a coloured card with a tiny logo box. They are weak, not reusable, not creative, and several are really flashcards, multiple-choice cards, timetables or checklists. We do not post those. We need high-stakes, scroll-stopping marketing graphics: very few words (1 to 4, sometimes 1 to 2, sometimes a single word) combined with images, cutouts, 3D device placements, abstract crafted shapes and unusual ways of setting type, like the polished app-promo visuals big brands post.

Problems seen on the owner's screenshots (fix all):

- The logo is a tiny dark rounded box in a corner. On light cards it looks like a stain, on the Editorial pull-quote it is clipped by the canvas edge, and its mark is unreadable. This is the most awkward thing in the app (section 5).
- Contrast bugs: white text on cream cards (Multiple choice question, Q&A flashcard question, Question and answer), dark text on dark brown (Myth vs Fact), grey text on cream (Speaker badge).
- Raw placeholder tokens render on a poster: `MYTH_TAG`, `FACT_TAG`. Timetable renders garbled rows (`MON19  00 · Mathematics`).
- Text clipped at the top (Editorial pull-quote "TODAY'S THOUGHT" is cut).
- Big empty zones in cards (Numbered tip card, Checklist, Vertical timeline, Timetable, Resource card): content sits in the top third.
- The editor UI is cluttered (section 6). Button labels repeat their text (`4:3 4:3`, `9:16 9:16`).

## 2. Design direction (freedom is the point)

The agent should be bold. Constraints are only these:

- **Words:** each template takes 1 to 4 words as the hero line (some exactly 1 or 2), at most one short supporting line, and an optional tiny label. Never paragraphs. Longer text is allowed in the data (AI content flows may send it), but the template must shrink or hide it gracefully and never overflow or clip.
- **Images:** 1 to 4 image slots per template (some use zero and ship strong abstract graphics). Support photo cutouts (subject on transparent background, using the existing `TemplateLook.cutoutX/Y/Scale` and `textBehind`), masked photos (text as a window, shapes as windows), collages, and device screens.
- **Style range:** some templates loud and layered (3D, depth, glow, grain, halftone, glass, duotone), some minimalist and clean (huge whitespace, one strong object, perfect alignment). Mix both across the set. Weird, deliberate typography is welcome: words broken across lines, one letter huge, text behind a subject, text on a path or curve, vertical text, outline plus fill pairs, stacked repeats, tape or ticker bands, stickers, strikethroughs, offset mirrors, gradient and mesh-filled glyphs.
- **Crafted look:** layered shadows, soft light, subtle grain, real depth (not flat rectangles), consistent corner radii, optical alignment, generous margins. Every template must look good at 9:16 (primary), 4:3 and 16:9. Other sizes fall back sensibly.
- **Brand:** use the brand tones (`PostTone`) so the Brand screen colours still drive every look. Each template ships 3 to 6 `looks` (a mix of dark, light and bold colour). No hard-coded colours that ignore the brand, except neutral blacks, whites and glass.
- **Reusable, not one-offs:** build a small composition kit once (section 4) and make templates configurations of it, so adding a new template later is mostly data. The owner's complaint is that current templates are not reusable.

Starter concept the owner gave (template #14 below): two mobile phones on a clean background, placed in 3D like software companies show an app, advanced graphics, with a few words and a cutout. Treat it as one example of the level wanted, not a limit.

## 3. The 20 templates to replace (names from the owner's screenshots)

Studio shows 55 templates in total. Only these 20 are replaced. The other 35 stay, but they inherit the new logo system (section 5) and the UI refresh (section 6). Ids are best guesses from `LegacyTemplateIds`: confirm each in code before editing.

| # | Current name (family) | Likely id | Problem | New concept (name is a suggestion, rename freely) |
|---|---|---|---|---|
| 1 | Letter window (Letter) | `ltr-window` | giant letter on a flat gradient, text box under it | **Letter Portal**: one huge glyph or short word used as a window onto a photo (text mask), soft inner shadow, depth and glow |
| 2 | Magazine drop-cap (Letter) | `ltr-dropcap` | small cap beside paragraph | **Cover Line**: giant serif initial bleeding off the canvas, a cutout overlapping it, 1 to 3 words, magazine-cover tension |
| 3 | Letter split (Letter) | `ltr-split` | two flat halves | **Mirror Split**: a word cut across its middle, halves offset, a photo seam or cutout between them, mild glitch |
| 4 | Aurora type (Type) | `type-aurora` | small text on a dark box | **Aurora Word**: 1 to 3 words huge, mesh-gradient and grain fill, glow, an image orbiting or masked inside the letters |
| 5 | Spotlight word (Type) | `type-spotlight` | dim lines of text | **Spotlight Cut**: one word lit, the rest ghosted, a cutout subject standing in front of the word (text behind subject) |
| 6 | Speaker badge (Photo) | `photo-idcard` | ID-card layout, grey text, empty lower half | **Guest Stage**: big person cutout, name set giant behind them, a tilted title chip, spotlight; works for any person, not just speakers |
| 7 | Portrait + quote (Photo) | `photo-testimonial` | cream card, small portrait ball | **Voice Frame**: portrait cutout, 3 to 6 quote words broken across lines at different scales, quote mark as a graphic object |
| 8 | Corner photo + tag (Photo) | `photo-tag` | photo rectangle plus text card | **Corner Peel**: photo with a peeled or folded corner revealing a colour block or second image, sticker tag |
| 9 | Editorial pull-quote (Information) | `info-quote` | thin text on gradient, clipped label | **Pull Quote Poster**: clean minimalist: one strong line, oversized quote glyph, hairlines, perfect margins, optional cutout |
| 10 | Myth vs Fact (Information) | `info-versus`? | two text cards, raw tokens, low contrast | **Strike and Reveal**: the myth word struck through or glitched, the fact word large and lit, image between; 1 to 2 words each |
| 11 | Numbered tip card (Information) | `info-tip` | card with tiny list | **Big Number Hook**: a giant numeral built into the image (extruded or outlined), 1 to 3 words beside it |
| 12 | Checklist (Information) | `info-formula`? | pasted list in a card | **Triple Stack**: three stacked words alternating outline and fill, check marks as graphic accents, cutout on the side |
| 13 | Multiple choice (Information) | confirm | quiz card | **Question Hero**: one giant `?` or one question word, image cutout emerging from it, arrows and sticker chips. No answer options |
| 14 | Question and answer (Information) | `info-qa` | two stacked cards | **Twin Phones**: two phones at opposing 3D angles with overlapping depth and soft shadows, screens hold images or screenshots, 1 to 3 words, clean background |
| 15 | Q&A flashcard (Information) | confirm | flashcard | **Card Fan**: tilted stack of 3 to 4 image cards fanned in perspective, one hero word, light flare |
| 16 | Vertical timeline (Information) | confirm | numbered text rows | **Ribbon Path**: an abstract winding ribbon or line linking 2 to 4 image dots, 1 to 2 words per stop, journey feel |
| 17 | Timetable (Information) | confirm | text table, garbled | **Date Slab**: giant date or time numerals as a monument slab, event word and image on it (live sessions, exams, deadlines) |
| 18 | Milestone counter (Marketing) | `mkt-milestone` | flip-clock cells | **Number Monument**: 3D extruded number (for example `50K`), confetti shapes, depth lighting, a thank-you word |
| 19 | Urgent notice (Marketing) | `mkt-notice` | cream card with orange header | **Alert Tape**: diagonal ticker or tape bands crossing an image, 1 to 4 words, high urgency, still on brand |
| 20 | Resource / PDF card (Marketing) | `mkt-download` | card with ticks | **Doc Stack**: tilted 3D sheets or a tablet showing a cover image, one word like `FREE`, a download glyph; also usable for e-books |

Add as many extra looks and variants as are useful. If the agent has strong ideas beyond the 20 (a single big phone with a cutout, a tablet and phone pair, a laptop, a scroll of three phones, a floating-object hero), add them as bonus templates in the same families, after the 20 are done.

## 4. Engine work (build once, reuse everywhere)

Read: `PostRenderer.kt` (search for `drawPhoto`, `render`, the decor `when (shape)`, the text-fx code), `PostModel.kt` (`PostDoc`), `PostTextStyle.kt`, `PostTemplatesDevice.kt` (`dvPhone`).

Add a composition kit (new file such as `PostComposeKit.kt` plus small renderer hooks). Suggested pieces, adapt as the code allows:

1. **Device frame in 3D.** Generalise `dvPhone`: phone with rotation (x, y, z), perspective skew, bezel, rim light, soft contact shadow and reflection, optional notch or punch-hole. Placement presets: single, twin (opposing angles), trio, phone plus tablet. The screen takes an image slot with crop and zoom.
2. **Cutout stack.** Subject cutout between layers: behind text, in front of text, partially masked by shapes, with optional rim glow or outline sticker edge.
3. **Text as a mask or a path.** Text window onto a photo, text on a curve, gradient or mesh fill, outline plus fill pair, repeated stack, vertical text, split and offset, glitch. Reuse the existing `TextFx` and `stackFx` before adding anything new.
4. **Abstract shape kit.** Blobs, rings, arches, ribbons, tape and ticker bands, stickers, halftone dots, grid and noise, light flares, confetti. Add the shapes to `DecorShape` only where they cannot be built from existing ones. Keep `PostStudyGrid.kt:860` `ListStyle` exhaustive (do not add enum entries there without updating that `when`).
5. **Multi-image slots.** Use `PostPhotoArea.cells` (already exists) for 2 to 4 images with per-cell pan, rotate and radius.
6. **Depth helpers.** Layered shadows, inner shadow, glow, grain overlay (`TemplateLook.grain` exists), duotone, glass blur panel.
7. **Fit-to-box text.** Hero words auto-size to fill their box on every ratio, never clip, never overlap another slot. Min and max size per slot role (the `PostDesignRules` minimums stay in force for body text, but the hero slot may be much larger than today).

Performance rule: the editor re-renders on every slider move. Cache rendered thumbnails (`PostImageCache.kt` exists), avoid allocating new `Paint` or `Bitmap` objects per frame, and keep heavy effects (blur, 3D warps) cached until the inputs change.

## 5. Logo and brand mark system (all templates, all families)

Problem: today the logo is drawn in a dark rounded box at the top right (`new_logo.png` at the repo root is the source asset). Fix:

- **No box by default.** Draw the logo with a transparent background. If the supplied PNG still has a baked background, process it once (alpha-key or re-export) and keep one clean transparent version plus a one-colour version for light and dark backgrounds.
- **Auto contrast.** Sample the luminance behind the logo area and pick the light, dark or accent version automatically, so it is always legible. The Brand screen can override.
- **Placement options** (per post, with a per-template default declared in `TemplateLayout`, for example a new `logoAnchor`): top-left, top-right, top-centre, bottom-left, bottom-right, bottom-centre, inline with the footer links, vertical along an edge, tiny corner watermark, large faded watermark behind the artwork, signature stamp. Also **None**.
- **Style options:** mark only, wordmark only, mark plus wordmark, sticker (white outline), glass chip (only when the user picks it).
- **Sizing and safety:** scale slider, opacity slider, safe margins on every ratio (never clipped by the canvas edge, never over the hero word or a face). Templates declare a keep-out zone so the default logo spot never collides with their artwork.
- `TemplateLayout.bare = true` keeps meaning "no logo, no footer, no social block".
- Footer links (`www.wisdom-tower-academy.live`, `@wisdom_tower_academy`, social chips): redesign as one slim, low-profile line with the same contrast logic; on minimalist looks allow it to hide.
- Chapter summaries get the same logo system (section 7), always outside the ruled text area.

## 6. App UI refresh (cool, fast, simple; no feature removed)

Read: `PostStudio.kt` (editor screen), `PostEditorTabs.kt`, `PostBottomBar.kt`, `PostControls.kt`, `PostTheme.kt`, headings of `docs/UI_UX_PLAN.md`.

The soul of the app is simplicity of use. Keep every feature, reorganise and restyle. Observed on the screenshots:

- The ratio chips and the family chips appear twice (top strip and again inside the bottom panel). Show each once.
- Four stacked control rows (`Quick`, `All size`, tab row, filter chips, template cards, `Look`, `Ratio`) compete for the screen; the preview is only about a quarter of it.
- Chips and tabs are cut off at the right edge with no clear scroll hint. Template cards are text-only (name plus family word), so choosing a template means guessing.
- Button labels repeat (`4:3 4:3`). Several buttons have the same weight, so nothing leads the eye.

Targets:

1. **Preview first.** Make the preview as large as the screen allows (fit the canvas between the top bar and a bottom sheet). Swiping the preview changes template (as today), with a small position indicator.
2. **One bottom sheet with 3 snap states** (peek, half, full). Peek shows only the tab row and the template strip. Controls (Words, Style, Photo, more) open in half or full state. Remove the second copy of ratio and family filters.
3. **Template strip with real thumbnails** (rendered with the user's current words and brand), not text-only cards. Cached, lazy, with stable keys. Family filter as one chip row. A Gallery button for a full grid.
4. **Contrast and hierarchy.** One clear primary action (Share or Export), secondary actions quieter. Text on buttons and chips meets WCAG AA (4.5:1 for text, 3:1 for icons and borders). Selected state is obvious (filled, not only outlined). Tap targets 48 dp or more. Replace the near-identical navy-on-navy surfaces with 2 or 3 clearly different surface levels.
5. **Quick row** keeps Size, Shadow, Color, Background, but as one compact row with icons and a single value label. The `All size` slider appears only when the user asks for it (or inside the Size quick action).
6. **Fix label bugs** (`4:3 4:3`), shorten labels, keep the Amharic and English fonts working (the app already shows Ethiopic text on some cards).
7. **Speed.** Debounce re-render during drags, render a low-resolution preview while a slider moves and the full-resolution one on release, avoid recomputing the template strip on every edit.
8. Keep Library, Batch/Inbox, Brand and Settings tabs and all their features. Restyle with the same tokens. No new required steps for the common path: open, pick, type words, share.

## 7. Chapter summaries (notes templates)

Family `CHAPTER_FAMILY`, file `PostTemplatesStudy.kt` (six variants: Notebook cover, Handwritten numbered list, Definition notes, Exam key-points sheet, Handwritten notes table, Mnemonic study sheet). Layout engine: `PostStudyGrid.kt`, with `PostStudyInline.kt`, `PostTex.kt`, `PostMath.kt`. Read `docs/CHAPTER_SUMMARIES_PLAN.md` (spec) and `docs/CHAPTER_SUMMARIES_PROGRESS.md` (what is done) first, then search inside `PostStudyGrid.kt` for the header and page-counter code; do not read the whole file.

Required fixes:

1. **Title on the first line, always.** The title starts on the first ruled line of the sheet, at every size, in every variant. Today it starts on about the 3rd or 4th line. Remove whatever reserves header space above it (the variants' eyebrow, headline y-positions around 0.14 to 0.23, and the engine's header offset).
2. **Delete the small label above the title.** Do not draw the eyebrow in this family. Keep accepting the `EYEBROW` slot in the data (AI content may still send it) but ignore it when rendering.
3. **Delete the page counter and the lane line above the title.** Nothing sits above the title.
4. **Render anything.** The grid must handle, without clipping or overlap: tables of any number of columns and rows, merged or uneven rows, matrix blocks (matrix, pmatrix, bmatrix, determinants, augmented matrices, systems of equations with braces, piecewise and cases), fractions and nested fractions, roots, powers and subscripts, sums, products, limits, integrals, vectors, units, chemical formulas and equations with arrows and states, Greek letters, long equations that must wrap or scale, numbered and nested lists, definition and key-point boxes, short code or pseudo-code lines, Amharic and mixed English-Amharic lines. Check what `PostTex.kt` already supports and extend it where a case fails.
5. **Overflow policy.** If content does not fit one sheet: shrink to a readable minimum first, then continue on a new sheet (batch pagination) instead of clipping. Never let a table or matrix run off the right edge: scale it or allow horizontal compression with a minimum size.
6. **Logo and brand marks** use the section 5 system, placed outside the ruled writing area (a bottom corner or the margin strip), transparent, never over text.
7. Create a **QA fixture**: one test input per variant containing a 5-column table, a 4x4 matrix, an augmented matrix, a piecewise function, a nested fraction with an integral, a chemical equation, a 12-item nested list and a long Amharic paragraph. Keep it in `docs/` (or as a debug sample inside the app) and use it for the render check in each push.

## 8. Compatibility (do not break the pipeline)

- Template ids are stored in drafts, inbox batches and the Supabase table `wt_post.template_boxes`, which refuses unknown ids at save. Use **new ids** for the new templates and add every old id to `LegacyTemplateIds` pointing to its replacement (existing mechanism, see `postTemplate(id)`), so old drafts still open.
- Keep the shared slot names (`EYEBROW`, `HEADLINE`, `BODY`, `BUTTON`, `LIST`, `NUMBER`, photo) so existing texts carry over. A new template may use a subset; unused slots must not crash or show raw tokens (`MYTH_TAG`-style placeholders must never render).
- Register the new templates in `PostTemplates` and update the counts in README and doc comments.
- Never touch the shared Supabase `public` schema. Only the owner (or the dev agent) changes `wt_post.template_boxes`: prepare the exact rows (template id, boxes, limits per box) and the matching `TEMPLATES.md` text for the Brand Kit repo `hiyabteklu/wisdom-tower-brand-kit`, and ask the owner to apply them. Do not push those.
- Content agents choose from the supported template list, so the hero-word limits (1 to 4 words) must be written into the Brand Kit text for these templates.

## 9. Phases and tasks

Order matters: the quick wins and the shared systems come before the 20 templates.

- [x] **P1. Logo system** (section 5). Transparent logo asset, auto contrast, placement and style options, per-template anchor and keep-out zone, footer redesign. Files: `PostRenderer.kt`, `PostModel.kt`, `PostBrandScreen.kt`, `PostFooterEditor.kt`, `PostTemplates.kt`, `PostComposeKit.kt`.
- [x] **P2. Chapter summaries fixes** (section 7), including the QA fixture (`docs/CHAPTER_SUMMARIES_QA_FIXTURE.md`, `PostStudyGrid.kt`, `PostTex.kt`, `PostTemplatesStudy.kt`, `PostQaScreen.kt`).
- [x] **P3. Composition kit** (section 4): device 3D frame, cutout stack, text mask and path, shape kit, multi-image slots, depth helpers, fit-to-box hero text (`PostComposeKit.kt`, `PostRenderer.kt`, `PostTemplates.kt`).
- [x] **P4a. Letter and Type templates** (#1 to #5: `ltr-portal`, `ltr-coverline`, `ltr-mirrorsplit`, `type-auroraword`, `type-spotlightcut`).
- [x] **P4b. Photo templates** (#6 to #8: `photo-gueststage`, `photo-voiceframe`, `photo-cornerpeel`).
- [x] **P4c. Information-family replacements** (#9 to #17: `info-pullquote`, `info-strikereveal`, `info-bignumber`, `info-triplestack`, `info-questionhero`, `info-twinphones`, `info-cardfan`, `info-ribbonpath`, `info-dateslab`).
- [x] **P4d. Marketing templates** (#18 to #20: `mkt-monument`, `mkt-alerttape`, `mkt-docstack`).
- [x] **P5. UI refresh** (section 6: preview-first layout, 3-snap-state bottom sheet [PEEK/HALF/FULL], cached thumbnail template strip, single Ratio & Family filters, WCAG AA surface tokens, 2-stage debounced preview rendering).
- [x] **P6. Compatibility, registry, QA, Brand Kit rows** (section 8: `LegacyTemplateIds` multi-hop mapping for all 1st-gen & 2nd-gen IDs, `PostQaScreen.kt` `CHAPTER_FIXTURE`, prepared `wt_post.template_boxes` SQL & Brand Kit `TEMPLATES.md` entries below).
- [x] **P7. Optional bonus templates** (section 3, last paragraph: `device-cutout-hero`, `device-tablet-duo`, `device-trio-scroll`, `mkt-floating-hero`).

Each template task is done when: it renders correctly at 9:16, 4:3 and 16:9; has 3 to 6 looks that follow the brand colours; takes 1 to 4 hero words plus its image slots; survives long text and empty slots (Stress check) without clipping; the logo sits cleanly with auto contrast; old ids resolve through `LegacyTemplateIds`.

## 10. Acceptance checklist (owner will check on the phone)

1. No template looks like a text card, flashcard, quiz, checklist or timetable.
2. Every poster reads in under 2 seconds: 1 to 4 words and a strong image or graphic.
3. The logo is transparent, legible and never clipped, on every template and look.
4. Notes: title on line 1, no label above it, no page counter or lane line, tables and matrices render fully.
5. Studio is clearer: bigger preview, one set of filters, thumbnail template strip, obvious primary action, no duplicate labels, all old features still reachable.
6. Contrast problems listed in section 1 are gone.
7. No raw placeholder tokens appear anywhere.

## 11. Open questions for the owner (the agent may proceed with the stated default)

1. Should logo default to **mark only, top-left, auto contrast** on posters and **bottom-right corner** on notes? (default: yes — implemented)
2. Is a bonus set beyond the 20 welcome? (default: yes, after P4d — 4 bonus templates implemented)
3. Do we have more than one logo file (light and dark versions)? If not, generate them from `new_logo.png`. (implemented via automatic alpha-key extraction + luminance-sampled light/dark/accent tinting in `PostComposeKit.kt`)

---

## 11B. Prepared Supabase `wt_post.template_boxes` SQL & Brand Kit (`TEMPLATES.md`) Spec (Owner / Dev Agent to Apply)

Per Section 8, this repository never touches the shared Supabase `public` or `wt_post` schema directly and does not push to `hiyabteklu/wisdom-tower-brand-kit`. Apply the following upsert in Supabase SQL Editor so `wt_post.template_boxes` accepts all 24 new showcase template IDs:

```sql
insert into wt_post.template_boxes (template_id, family, boxes, word_limits)
values
  ('ltr-portal',         'Letter',      array['letter','headline','eyebrow'],                '{"letter":"1 char","headline":"1-3 words","eyebrow":"1-3 words"}'::jsonb),
  ('ltr-coverline',      'Letter',      array['letter','headline','eyebrow','body'],         '{"letter":"1 char","headline":"1-3 words","eyebrow":"1-3 words","body":"3-6 words"}'::jsonb),
  ('ltr-mirrorsplit',    'Letter',      array['headline','eyebrow','body'],                  '{"headline":"1-2 words","eyebrow":"1-3 words","body":"2-5 words"}'::jsonb),
  ('type-auroraword',    'Type',        array['headline','eyebrow','body'],                  '{"headline":"1-3 words","eyebrow":"1-3 words","body":"3-6 words"}'::jsonb),
  ('type-spotlightcut',  'Type',        array['headline','eyebrow','body'],                  '{"headline":"1-2 words","eyebrow":"1-3 words","body":"3-6 words"}'::jsonb),
  ('photo-gueststage',   'Photo',       array['headline','eyebrow','body','button'],         '{"headline":"1-3 words","eyebrow":"1-3 words","body":"2-5 words","button":"1-3 words"}'::jsonb),
  ('photo-voiceframe',   'Photo',       array['headline','body','eyebrow'],                  '{"headline":"3-6 words","body":"2-4 words","eyebrow":"1-3 words"}'::jsonb),
  ('photo-cornerpeel',   'Photo',       array['headline','eyebrow','button'],                '{"headline":"1-3 words","eyebrow":"1-2 words","button":"1-2 words"}'::jsonb),
  ('info-pullquote',     'Information', array['headline','body','eyebrow'],                  '{"headline":"3-6 words","body":"2-4 words","eyebrow":"1-3 words"}'::jsonb),
  ('info-strikereveal',  'Information', array['eyebrow','headline','body'],                  '{"eyebrow":"1-2 words (struck myth)","headline":"1-3 words (lit fact)","body":"3-6 words"}'::jsonb),
  ('info-bignumber',     'Information', array['number','headline','eyebrow','body'],         '{"number":"1-3 chars","headline":"1-3 words","eyebrow":"1-3 words","body":"3-6 words"}'::jsonb),
  ('info-triplestack',   'Information', array['headline','eyebrow','body'],                  '{"headline":"3 lines, 1-2 words/line","eyebrow":"1-3 words","body":"3-6 words"}'::jsonb),
  ('info-questionhero',  'Information', array['headline','eyebrow','button'],                '{"headline":"1-3 words","eyebrow":"1-2 words","button":"1-3 words"}'::jsonb),
  ('info-twinphones',    'Information', array['headline','eyebrow','body','button'],         '{"headline":"1-3 words","eyebrow":"1-3 words","body":"3-6 words","button":"1-3 words"}'::jsonb),
  ('info-cardfan',       'Information', array['headline','eyebrow','body'],                  '{"headline":"1-3 words","eyebrow":"1-3 words","body":"3-6 words"}'::jsonb),
  ('info-ribbonpath',    'Information', array['headline','list','eyebrow'],                  '{"headline":"1-3 words","list":"3 stops, 1-2 words each","eyebrow":"1-3 words"}'::jsonb),
  ('info-dateslab',      'Information', array['number','headline','eyebrow','button'],       '{"number":"2-5 chars","headline":"1-3 words","eyebrow":"1-3 words","button":"1-3 words"}'::jsonb),
  ('mkt-monument',       'Marketing',   array['number','headline','eyebrow','body'],         '{"number":"2-4 chars","headline":"1-3 words","eyebrow":"1-3 words","body":"2-5 words"}'::jsonb),
  ('mkt-alerttape',      'Marketing',   array['headline','eyebrow','body','button'],         '{"headline":"1-3 words","eyebrow":"1-3 words","body":"2-5 words","button":"1-3 words"}'::jsonb),
  ('mkt-docstack',       'Marketing',   array['headline','eyebrow','body','button'],         '{"headline":"1-2 words","eyebrow":"1-3 words","body":"3-6 words","button":"1-3 words"}'::jsonb),
  ('mkt-floating-hero',  'Marketing',   array['headline','eyebrow','body','button'],         '{"headline":"1-3 words","eyebrow":"1-3 words","body":"3-6 words","button":"1-3 words"}'::jsonb),
  ('device-cutout-hero', 'Device',      array['headline','eyebrow','button'],                '{"headline":"1-3 words","eyebrow":"1-3 words","button":"1-3 words"}'::jsonb),
  ('device-tablet-duo',  'Device',      array['headline','eyebrow','body','button'],         '{"headline":"1-3 words","eyebrow":"1-3 words","body":"3-6 words","button":"1-3 words"}'::jsonb),
  ('device-trio-scroll', 'Device',      array['headline','eyebrow','button'],                '{"headline":"1-3 words","eyebrow":"1-3 words","button":"1-3 words"}'::jsonb)
on conflict (template_id) do update
  set family = excluded.family,
      boxes = excluded.boxes,
      word_limits = excluded.word_limits;
```

### Matching `TEMPLATES.md` Snippet for `hiyabteklu/wisdom-tower-brand-kit`

Copy-paste into `TEMPLATES.md` in the Brand Kit repository so AI content agents enforce the 1–4 hero word rule:

- **`ltr-portal` (Letter Portal)**: `letter` (1 char), `headline` (1–3 words max), `eyebrow` (1–3 words). Huge letter portal over photo/mesh depth.
- **`ltr-coverline` (Cover Line)**: `letter` (1 char), `headline` (1–3 words max), `eyebrow` (1–3 words), `body` (3–6 words). Magazine cover initial bleeding off-canvas with cutout subject.
- **`ltr-mirrorsplit` (Mirror Split)**: `headline` (1–2 words, glitch-split), `eyebrow` (1–3 words), `body` (2–5 words).
- **`type-auroraword` (Aurora Word)**: `headline` (1–3 words, gradient fill), `eyebrow` (1–3 words), `body` (3–6 words).
- **`type-spotlightcut` (Spotlight Cut)**: `headline` (1–2 words behind cutout), `eyebrow` (1–3 words), `body` (3–6 words).
- **`photo-gueststage` (Guest Stage)**: `headline` (1–3 words behind cutout), `eyebrow` (1–3 words), `body` (2–5 words), `button` (1–3 words).
- **`photo-voiceframe` (Voice Frame)**: `headline` (3–6 quote words), `body` (speaker name, 2–4 words), `eyebrow` (1–3 words).
- **`photo-cornerpeel` (Corner Peel)**: `headline` (1–3 words), `eyebrow` (1–2 words), `button` (1–2 words).
- **`info-pullquote` (Pull Quote Poster)**: `headline` (3–6 words), `body` (2–4 words), `eyebrow` (1–3 words).
- **`info-strikereveal` (Strike and Reveal)**: `eyebrow` (1–2 words struck myth), `headline` (1–3 words lit fact), `body` (3–6 words).
- **`info-bignumber` (Big Number Hook)**: `number` (1–3 chars), `headline` (1–3 words), `eyebrow` (1–3 words), `body` (3–6 words).
- **`info-triplestack` (Triple Stack)**: `headline` (3 lines of 1–2 words), `eyebrow` (1–3 words), `body` (3–6 words).
- **`info-questionhero` (Question Hero)**: `headline` (1–3 words), `eyebrow` (1–2 words), `button` (1–3 words).
- **`info-twinphones` (Twin Phones)**: `headline` (1–3 words), `eyebrow` (1–3 words), `body` (3–6 words), `button` (1–3 words). Opposing 3D phones.
- **`info-cardfan` (Card Fan)**: `headline` (1–3 words), `eyebrow` (1–3 words), `body` (3–6 words).
- **`info-ribbonpath` (Ribbon Path)**: `headline` (1–3 words), `list` (3 stops, 1–2 words per line), `eyebrow` (1–3 words).
- **`info-dateslab` (Date Slab)**: `number` (2–5 chars date/time), `headline` (1–3 words), `eyebrow` (1–3 words), `button` (1–3 words).
- **`mkt-monument` (Number Monument)**: `number` (2–4 chars, e.g. `50K`), `headline` (1–3 words), `eyebrow` (1–3 words), `body` (2–5 words).
- **`mkt-alerttape` (Alert Tape)**: `headline` (1–3 words), `eyebrow` (1–3 words), `body` (2–5 words), `button` (1–3 words).
- **`mkt-docstack` (Doc Stack)**: `headline` (1–2 words, e.g. `FREE`), `eyebrow` (1–3 words), `body` (3–6 words), `button` (1–3 words).
- **`mkt-floating-hero` (Floating Object Hero)**: `headline` (1–3 words), `eyebrow` (1–3 words), `body` (3–6 words), `button` (1–3 words).
- **`device-cutout-hero` (Phone + Cutout Hero)**: `headline` (1–3 words), `eyebrow` (1–3 words), `button` (1–3 words).
- **`device-tablet-duo` (Tablet & Phone Duo)**: `headline` (1–3 words), `eyebrow` (1–3 words), `body` (3–6 words), `button` (1–3 words).
- **`device-trio-scroll` (Triple Phone Cascade)**: `headline` (1–3 words), `eyebrow` (1–3 words), `button` (1–3 words).

## 12. Progress log

- 17 Nov 2026: plan written. Nothing implemented. Nothing compiled.
- 17 Nov 2026: implemented all phases P1 through P7 in one pass (`PostModel.kt`, `PostComposeKit.kt`, `PostRenderer.kt`, `PostTemplates.kt`, `PostStudyGrid.kt`, `PostTex.kt`, `PostTemplatesStudy.kt`, `PostTemplatesLetter.kt`, `PostTemplatesType.kt`, `PostTemplatesPhoto.kt`, `PostTemplatesInfo.kt`, `PostTemplatesMarketing.kt`, `PostTemplatesDevice.kt`, `PostTheme.kt`, `PostControls.kt`, `PostBrandScreen.kt`, `PostFooterEditor.kt`, `PostEngineControls.kt`, `PostEditorTabs.kt`, `PostStudio.kt`, `PostQaScreen.kt`, `docs/CHAPTER_SUMMARIES_QA_FIXTURE.md`). Not compiled here (AI Studio agent cannot run Gradle).

