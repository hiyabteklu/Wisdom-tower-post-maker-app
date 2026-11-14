# Chapter Summaries: progress log

Companion to `CHAPTER_SUMMARIES_PLAN.md` (the plan stays the spec). Updated 2026-11-14 (late evening). The assistant cannot compile or run the app: everything marked "pushed" has been written but not compiled. Only the owner's first phone test (section "First phone test") has exercised the engine, and the new text engine (`PostStudyInline.kt`, see "Implemented after the first test") has not been built at all.

## START HERE (next agent)

The owner's verdict on the first phone build: the layout engine is good, but the sheets look **flat**. His words: "colors are not coming, LaTeX / real math symbols when it comes to advanced math or complicated formulas and Greek symbols etc. need work", and, in a second message: "why is the entire page just flat color text, why no italics, bold, highlight, bullet marks, different color important words etc.".

Reasons the page was flat (diagnosed from the code and the screenshots):
1. The content agent sent plain sentences with no markup, and the engine only styled text that was explicitly marked.
2. The colored-term tag `[[#HEX;B:term]]` was printed raw on the two row-style sheets (mnemonic, table-notes), so even the one colored element failed.
3. Only the `ROW` variants had a term label at all; the checklist and numbered sheets had colored markers and nothing else.
4. Row-style sheets had no bullet mark.
5. Formulas were plain text (and typed without `$...$`), so they got no styling and no real subscripts.

Order of work now: **(1) build and test what was just pushed**, (2) the overflow warning, (3) real math layout (stacked fractions, root bars), (4) the rest of the list below.

Before editing: read the latest `main`, work on `main` only (see `CLAUDE.md`), and tell the owner after each change that it has not been compiled.

## Implemented after the first test (pushed, NOT compiled, NOT run)

Commit "PostStudyInline, a study-sheet text engine". New file `PostStudyInline.kt`; `PostStudyGrid.kt` now calls it instead of `PostFonts.styled`. Study sheets therefore no longer depend on the code path where the raw tags appeared.

- Explicit markup, nestable: `[[#HEX;FLAGS:text]]` and `[[#HEX:text]]` (flags B bold, I italic, U underline, H highlight), `[[key term]]`, `**bold**`, `*italic*`, `==highlight==`, `#underline#`, `` `code` ``, `\box{note}`, `$math$`, `$$math$$`.
- Automatic styling of plain text (on for body text, off for titles):
  - a short term before a colon (up to 3 words) becomes a bold colored label on EVERY row style; row styles table-notes and mnemonic use their own color, the others rotate blue, red, green, purple, navy;
  - formulas (any token with `=`, e.g. `F=-kx`, `T=2π√(m/k)`) become colored italic;
  - `v_max`, `vmax`, `amax`, `x^2`, `s^-1` become real baseline-shifted subscripts and superscripts (a `ScriptSpan`), not Unicode letters;
  - Greek letters use a serif italic face and math symbols a serif face, so ω π φ Δ ± √ match each other (the handwriting font has no Greek);
  - bracketed remarks with two or more words, e.g. "(within elastic limit)", become muted italic;
  - not / never / always / only are bold red; maximum / minimum / zero / constant / conserved / proportional / opposite are bold.
- Row-style sheets (table-notes, mnemonic) now draw a bullet dot on every row. `•` is drawn as a filled circle, so it needs no font. Checklist keeps green ✓, numbered keeps red numbers.
- `hexOf` now formats with `Locale.US` (a phone locale could otherwise change the digits of the color tag). This is also one candidate for the original raw-tag bug.
- Math in `$...$`: top-level `^` and `_` become real raised/lowered text; everything else still goes through `PostMath` (one-line Unicode text).

Things to check when it is first built (compile risks and visual risks):
- Compile errors in the two files above (written without a compiler).
- Bold on the handwriting font is synthesized by Android; check it looks acceptable. Same for italic.
- A `MetricAffectingSpan` baseline shift inside the per-line clip in `drawLayout`: superscripts must not be cut at the top.
- The auto rules may be too eager or too weak on real content; they are small lists at the top of `PostStudyInline.kt` (regexes `FORMULA`, `SCRIPT`, `PAREN`, `NEGATION`, `KEYWORD`). Tune after seeing a real chapter. Consider a per-post switch to turn auto styling off.
- Row text is now longer or shorter than before (no raw tags, but a bullet indent), so recount how many rows fit a sheet.

## First phone test (2026-11-14): what the output looked like

Test content: "Physics Chapter 5 · Oscillations, SHM & Waves", 12 dense statements (periodic motion, displacement/amplitude, T and f, angular frequency, SHM definition, Hooke's law, position, velocity, acceleration, mass-spring, small-angle pendulum, spring energy), 9:16, six sheets. Screenshots were sent in chat (not stored in the repo; the owner can add them under `docs/screens/`).

What works (keep it):
- 30-rule notebook page, text baselines on the rules, red margin line, red eyebrow "FULL PAGE · PHYSICS CHAPTER 5", big navy title with red underline.
- Small logo box flush in the top-right corner and small footer chips (website, Telegram) on the last line.
- Handwriting look: bundled Patrick Hand is used for Latin text.
- Checklist sheet (`study-checklist`): green ✓ markers, 12 of 12 rows fit. Numbered sheet (`study-list`): red numbers 1 to 12, hanging indent, 12 of 12 rows fit.
- Green Key Point / key-idea box with star ("REMEMBER: active recall > rereading") on the cover sheet.

What was wrong (the new commit addresses the first four, see above):
- Raw tags printed on the mnemonic (purple, big acronym "SQ3R") and table-notes (blue) sheets, e.g. `[[#7C3AED;B:SHM definition]] – acceleration is proportional to...`; the raw tag also made rows wrap onto an extra cell.
- Black ink everywhere except markers and the title: no colored terms on checklist or numbered sheets, no bold, italic or highlight anywhere, no bullets on row sheets.
- Formulas as flat text: `vmax=Aω`, `amax=ω²A` (no subscripts), `ω=2πf=2π/T` (slash fraction), `T=2π√(L/g)` and `f=(1/2π)√(g/L)` (root has no bar).
- Greek letters pasted from another font (Patrick Hand has no Greek).
- **Rows lost without any warning.** Mnemonic sheet: the last two rows (small-angle pendulum, spring energy) missing; table-notes sheet: the 12th row missing. A row that does not fit is skipped whole (by design, "never shrink"), but the badge still says "Text shrunk to fit", which is the wrong wording and does not say what was left out. NOT fixed yet.
- Cover sheet is mostly empty (title, one intro line, one key box). Expected for a cover; it cannot hold dense text.

## Done earlier (pushed to main, built once by the owner)

- [x] Step 3, line-grid engine: `PostStudyGrid.kt` (30 rules, 29 text cells, baselines on the rules, content from the top, no shrinking).
- [x] Step 4, small logo in the top-right corner and small footer chips on the last line (`PostRenderer.kt`).
- [x] Step 6 (first version), formulas: `PostMath.kt` turns `$...$` into one-line Unicode text. Real layout still to do (below).
- [x] Step 2, own family: `CHAPTER_FAMILY = "Chapter summaries"` (`PostTemplatesStudy.kt`); the template strip shows only this family for a chapter draft and never for a poster draft; the Library lists it as its own row.
- [x] Step 7 (part): real tables and the Key Point box in `PostStudyGrid.kt` (`|a|b|c|` rows, first row header, `|---|` rows ignored; `>> text` or `>> Label | text`).
- [x] The six variants no longer draw their old fixed underlines and rules. Only the paper is decoration now.
- [x] Step 5, handwriting font: `assets/fonts/english/PatrickHand-Regular.ttf` is bundled and used by default (the post's own English font still wins). Regular face only; bold and italic are synthesized.
- [x] Backend (checked live, 14 Nov 2026, migration `chapter_summaries_study_template_limits`, `wt_post` only): the `list` box of `study-list`, `study-checklist`, `study-table`, `study-mnemonic` takes 1 to 40 items of up to 320 characters, and `wt_post.check_batch` no longer refuses a list item that starts with a bullet, letter or number for `study-%` templates. Agent rules are in `AI_CONTENT_DELIVERY.md`, section "Chapter summaries".

## Not done yet (priority order)

### P0

- [ ] **Build and test the new text engine** (see "Things to check"). Send the owner's screenshots of 9:16, 4:3, 16:9 back for tuning.
- [ ] **Never lose text silently (Step 8).** Replace the badge wording for `study-*` ("Text shrunk to fit: ..." in `PostStudio.kt` preview, the Save dialog and `PostBatchScreen.kt`) with "Does not fit, split this sheet" naming the first row left out. Add a "lines used / 30" counter (`PostStudyGrid` knows `page.cell`; return it next to the overflow list). Agents need a way to ask how many lines a draft takes so they split chapters correctly (budget: about 29 cells, a wrapped line costs one cell; the test page held 11 to 12 two-line statements).
- [ ] **Real math layout (Step 6 rework).** The new engine fixes glyph mismatch and subscripts/superscripts. Still missing:
  1. roots drawn with a bar over the radicand;
  2. fractions as a small stacked fraction inside one cell (numerator and denominator at about 55 percent, a bar between), via a `ReplacementSpan`;
  3. `$$...$$` display math as its own block 2 to 3 cells high (full-size stacked fraction, centered), counted by the grid like a table;
  4. if that is still not enough, render complex formulas with a Kotlin LaTeX renderer to a bitmap that snaps to whole cells.
  Keep every slice inside the grid rule (nothing crosses a rule, baselines stay on the rules).
- [ ] **Original raw-tag bug in `PostFonts.styled` (`PostRenderer.kt`) is still unexplained.** Study sheets bypass it now, but every other template still uses it. `styled` wraps everything in `runCatching { ... }.getOrElse { text }`, so any exception in `styledUnsafe` returns the raw string with all tags and hides the error. Log the exception, add a JVM unit test for `[[#7C3AED;B:SHM definition]] – x`, and confirm the phone runs the latest `main`.
- [ ] **Content agents must wrap formulas in `$...$`** (example: `$v_{max}=A\omega$`, `$T=2\pi\sqrt{m/k}$`) and may use the explicit marks; with the new engine plain text is also styled automatically, but explicit marks give better results. Add this to `AI_CONTENT_DELIVERY.md`, section "Chapter summaries".

### P1

- [ ] Step 7 (rest): colored section numbers (2.6, 2.7) as their own heading style, a mnemonic row with a big colored letter, per-table color. Headings with `##` already work as numbered headings if the number is typed in the text.
- [ ] `study-cover` and `study-define` have no `list` box, so they cannot hold dense content (message limits 120 and 220, one line). Decide whether to give `study-define` a list box (needs a slot in `stDefineLayout` and a row in `wt_post.template_boxes`).
- [ ] Tune auto styling on real chapters (which words to emphasize, formula color, whether bracket remarks should be italic), possibly as a per-post option.

### P2

- [ ] Step 9: tune the six variants (sizes, spacing). Only 9:16 has been seen.
- [ ] Step 10: finish the phone test with dense content in 4:3 and 16:9: text on the rules, table lines match the rules, logo and footer do not touch text, wrapped Amharic lines do not break the grid.
- [ ] Step 11 (rest): Brand Kit `rules/TEMPLATES.md` does not list the six study templates yet (separate repo). Then run the real generation flow (Supabase chapter, split into N sheets, markup, inbox drafts), starting with the physics chapter used in the test.
- [ ] Mixed variants: the backend allows one template per batch, so a chapter using several variants is several batches in the same folder and subfolder. One batch with mixed variants would be a backend and inbox change.

## Markup Claude can send (list and body slots)

```
# Title            ## Topic (underlined)        ### Subtopic
- bullet           ★ / ✓ / → marker lines       1. numbered   1.1. numbered
> key idea         > WARNING text (red bar)     >> Label | text  (Key Point box)
Term: explanation  (the term becomes a colored bold label on every row style)
---                (one blank line)
|Zone|Altitude|Climate|
|Dega|Above 2,500 m|Cool|
```

Inline: `**bold**`, `*italic*`, `==highlight==`, `[[key term]]`, `#underline#`, `[[#D11A26;B:word]]`, `$math$`.
Budget about 29 text lines per sheet (the footer takes the last one). A wrapped line costs one line.
