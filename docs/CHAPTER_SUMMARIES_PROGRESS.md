# Chapter Summaries: progress log

Companion to `CHAPTER_SUMMARIES_PLAN.md` (the plan stays the spec). Updated 2026-11-14 (evening handoff). The assistant cannot compile or run the app: everything below marked "pushed" has been written but only the owner's phone test (section "First phone test") has exercised it.

## START HERE (next agent)

The first phone build works as a layout engine but has three real defects the owner wants fixed before anything else, in this order:

1. **Colors do not come through** (raw `[[#HEX;B:term]]` text is printed on two sheet types, and the other sheet types are all black ink).
2. **Formulas / Greek letters / advanced math look wrong** (flat text, no real subscripts, fractions and roots, mismatched Greek glyphs).
3. **Dense sheets silently lose their last rows** (nothing tells the owner that text was left out).

The owner's own words to the previous agent (it hit its usage limit): the result is "a bit good, but with some formatting problems, colors are not coming, LaTeX / real math symbols when it comes to advanced math or complicated formulas and Greek symbols etc. need work". Push everything that is not done yet.

Before editing: read the latest `main`, work on `main` only (see `CLAUDE.md`), and tell the owner after each change that it has not been compiled.

## First phone test (2026-11-14): what the output looked like

Test content: "Physics Chapter 5 · Oscillations, SHM & Waves", 12 dense statements (periodic motion, displacement/amplitude, T and f, angular frequency, SHM definition, Hooke's law, position, velocity, acceleration, mass-spring, small-angle pendulum, spring energy), 9:16, six sheets. Screenshots were sent in chat (not stored in the repo; the owner can add them under `docs/screens/` if wanted).

What works (keep it):
- 30-rule notebook page, text baselines on the rules, red margin line, red eyebrow "FULL PAGE · PHYSICS CHAPTER 5", big navy title with red underline.
- Small logo box flush in the top-right corner and small footer chips (website, Telegram) on the last line.
- Handwriting look: bundled Patrick Hand is used for Latin text.
- Checklist sheet (`study-checklist`): green ✓ markers, 12 of 12 rows fit, clean. Numbered sheet (`study-list`): red numbers 1 to 12, hanging indent, 12 of 12 rows fit, clean.
- Green Key Point / key-idea box with star ("REMEMBER: active recall > rereading") on the cover sheet.
- Spacing is already tight enough that 12 statements fill a page.

What is wrong:
- **Raw tags printed.** On the mnemonic sheet (purple, acronym "SQ3R" in big purple letters) and the table-notes sheet (blue) every term shows as literal text, e.g. `[[#7C3AED;B:SHM definition]] – acceleration is proportional to...` and `[[#176BCE;B:Hooke's law]] – ...`. These come from `PostStudyGrid.labelled()` (ROW style, `Term: explanation` lines). Expected: the term in bold colored ink, no brackets. Side effect: the raw tag makes each line longer, so rows wrap to an extra cell.
- **Rows lost without any warning.** On the mnemonic sheet the last two rows (Small-angle pendulum, Spring energy) are missing and two empty cells sit above the footer; on the table-notes sheet the 12th row (Spring energy) is missing. A row that does not fit is skipped whole (by design, "never shrink"), but the preview badge still says "Text shrunk to fit", which is the wrong wording and does not say what was left out.
- **Black ink everywhere else.** Checklist and numbered sheets have colored markers only; terms ("Hooke's law:", "Velocity:") and formulas are plain black, so the sheet looks monochrome next to the colored title. The owner expects colored key terms and formulas.
- **Formulas are plain one-line text.** Examples seen: `vmax=Aω`, `amax=ω²A` (no subscripts), `ω=2πf=2π/T` (slash fraction), `T=2π√(L/g)` and `f=(1/2π)√(g/L)` (root has no bar over its content), `x=±A`. The statements were typed as plain Unicode, not wrapped in `$...$`, so `PostMath` never ran on them.
- **Greek letters look pasted in.** Patrick Hand has no Greek, so ω, π, φ, Δ fall back to the phone's system font: different weight, size and baseline from the handwriting around them.
- **Cover sheet is mostly empty** (`study-cover`: title, one intro line, one key box, then blank lines). Expected for a cover, but it cannot hold dense text.

## Done (pushed to main, built once by the owner, see test above)

- [x] Step 3, line-grid engine: `PostStudyGrid.kt` (30 rules, 29 text cells, baselines on the rules, content from the top, no shrinking).
- [x] Step 4, small logo in the top-right corner and small footer chips on the last line (`PostRenderer.kt`).
- [x] Step 6 (first version), formulas: `PostMath.kt` turns `$...$` into one-line Unicode text (Greek, powers, fractions, roots). Wired into `PostFonts.styled`. Needs the rework below.
- [x] Step 2, own family: `CHAPTER_FAMILY = "Chapter summaries"` (`PostTemplatesStudy.kt`). The editor's template strip shows only this family for a chapter draft and never for a poster draft (`PostEditorTabs.kt`, `TemplateStrip`). The Library lists it as its own row.
- [x] Step 7 (part): real tables and the Key Point box in `PostStudyGrid.kt`.
  - Table: consecutive `|a|b|c|` rows, first row is the header, `|---|---|` rows are ignored. Columns share the width by text length, cells wrap, every row is whole cells high so the table lines are the notebook rules.
  - Key Point box: `>> text` or `>> Label | text`, green rounded border, the star label on the first cell.
- [x] The six variants no longer draw their old fixed underlines and rules. Only the paper is decoration now.
- [x] `study-table` sample text is now a real table plus a Key Point box.
- [x] Step 5, handwriting font: `assets/fonts/english/PatrickHand-Regular.ttf` is bundled and used by default (commit 6623a59); the post's own English font still wins, then `assets/fonts/hand/`, then the phone's casual font. (Patrick Hand has a regular face only; bold is synthesized.)
- [x] Backend (done and checked live, 14 Nov 2026, migration `chapter_summaries_study_template_limits`, `wt_post` only): the `list` box of `study-list`, `study-checklist`, `study-table`, `study-mnemonic` takes 1 to 40 items of up to 320 characters, and `wt_post.check_batch` no longer refuses a list item that starts with a bullet, letter or number for `study-%` templates. Other templates are checked as before. Agent rules are in `AI_CONTENT_DELIVERY.md`, section "Chapter summaries".

## Not done yet (priority order)

### P0 (owner-reported)

- [ ] **P0-1 Colors: raw `[[#HEX;B:term]]` printed.** On paper the parser is right: `PostFonts.styledUnsafe` in `PostRenderer.kt` has `colorRegex = \[\[(#hex6or8)(?:;([BUIH]+))?:(.*?)]]` and `labelled()` emits exactly that shape, yet the sheet shows the tag. Cause NOT confirmed. Check in this order:
  1. `PostFonts.styled` wraps everything in `runCatching { ... }.getOrElse { text }`, so ANY exception in `styledUnsafe` returns the raw string with all tags in it and hides the error. Log the exception (`android.util.Log.w`) and find what throws for a ROW line.
  2. Write a small JVM unit test: `styledUnsafe` on `[[#7C3AED;B:SHM definition]] – acceleration is proportional to displacement` must give "SHM definition – acceleration..." with a foreground span.
  3. Confirm the phone really runs the latest `main` (installed build vs commit 6623a59).
  Also add the same inline colors to the editor's chips (editor `Apply color` writes `[[#HEX:text]]`, no flags; both forms must keep working).
- [ ] **P0-2 Never lose text silently (Step 8).** Replace the badge wording for `study-*` ("Text shrunk to fit: ..." in `PostStudio.kt` preview, the Save dialog and `PostBatchScreen.kt`) with "Does not fit, split this sheet" and name the first row left out. Add a "lines used / 30" counter (PostStudyGrid already knows `cell`; return it next to the overflow list). Agents should be able to ask how many lines a draft needs before saving, so they split chapters into sheets correctly (budget today: about 29 cells, a wrapped line costs one cell; the test page held about 11 to 12 statements of 2 lines).
- [ ] **P0-3 Real math (Step 6 rework).** Owner wants LaTeX-quality symbols for advanced math, complicated formulas and Greek. Suggested slices, smallest first:
  1. Math/Greek glyph font: bundle an OFL font that has Greek and math operators (for example STIX Two or Noto Sans Math, license file included) and apply it to Greek letters and math symbol runs with a span, the way `EthiopicSpan` already switches font for Amharic. This fixes the mismatched ω π φ Δ ± √ ≤ ≈ → look.
  2. Real subscripts and superscripts: replace Unicode sub/superscript characters with baseline-shifted, smaller text (a `MetricAffectingSpan` with about 0.7 size and ±0.25 shift). Unicode forms look wrong in the handwriting font and many letters have none (`v_{max}` currently degrades to `ᵥₘₐₓ` or `_(max)`).
  3. Roots with a real bar over the radicand and fractions drawn as a small stacked fraction inside one cell (numerator and denominator at about 55 percent size, a bar between) via a `ReplacementSpan`.
  4. `$$...$$` display math as its own block 2 to 3 cells high (stacked fraction at full size, centered), counted by the grid like a table.
  5. If this is still not enough, render complex formulas with a Kotlin LaTeX renderer (for example a JLaTeXMath-android style library) to a bitmap that snaps to whole cells.
  Keep every slice inside the grid rule (nothing crosses a rule, baselines stay on the rules).
- [ ] **P0-4 Content agents must wrap every formula in `$...$`.** The test text had plain Unicode like `vmax=Aω`, which bypasses `PostMath`. Add this to `AI_CONTENT_DELIVERY.md` section "Chapter summaries" (example: `$v_{max}=A\omega$`, `$T=2\pi\sqrt{m/k}$`).

### P1 (formatting and color)

- [ ] Default color roles so a sheet is never monochrome: key terms bold and colored in every variant (checklist and numbered rows currently get no term label, only ROW variants do), formulas in an accent color, `==highlight==` and `[[term]]` visible. Decide the palette (navy, blue, red, green, purple are already in `PostStudyGrid`).
- [ ] Step 7 (rest): colored section numbers (2.6, 2.7) as their own heading style, a mnemonic row with a big colored letter, per-table color. Headings with `##` already work as numbered headings if the number is typed in the text.
- [ ] `study-cover` and `study-define` have no `list` box, so they cannot hold dense content (message limits 120 and 220, one line). Decide whether to give `study-define` a list box (needs a slot in `stDefineLayout` and a row in `wt_post.template_boxes`).

### P2 (checks and housekeeping)

- [ ] Step 9: tune the six variants after the fixes (sizes, spacing). Only 9:16 has been seen so far.
- [ ] Step 10: finish the phone test with real dense content in 4:3 and 16:9, and check: text sits on the rules, table lines match the rules, the logo and footer do not touch text, symbols render, wrapped Amharic lines do not break the grid.
- [ ] Step 11 (rest): Brand Kit `rules/TEMPLATES.md` does not list the six study templates yet; add them there (separate repo) so content agents find them. Then run the real generation flow (Supabase chapter, split into N sheets, markup, inbox drafts), starting with the physics chapter used in the test.
- [ ] Mixed variants: the backend allows one template per batch, so a chapter that uses several variants is several batches in the same folder and subfolder. If the owner wants one batch with mixed variants, that is a backend and inbox change.

## Markup Claude can send (list and body slots)

```
# Title            ## Topic (underlined)        ### Subtopic
- bullet           ★ / ✓ / → marker lines       1. numbered   1.1. numbered
> key idea         > WARNING text (red bar)     >> Label | text  (Key Point box)
Term: explanation  (in table-notes and mnemonic sheets, the term becomes a colored label)
---                (one blank line)
|Zone|Altitude|Climate|
|Dega|Above 2,500 m|Cool|
```

Inline: `**bold**`, `*italic*`, `==highlight==`, `[[key term]]`, `#underline#`, `[[#D11A26;B:word]]`, `$math$`.
Budget about 29 text lines per sheet (the footer takes the last one). A wrapped line costs one line.
