# Chapter Summaries: progress log

Companion to `CHAPTER_SUMMARIES_PLAN.md` (the plan stays the spec). Updated 2026-11-14. Nothing below has been built or run yet: the assistant cannot compile.

## Done (pushed to main, not yet built)

- [x] Step 3, line-grid engine: `PostStudyGrid.kt` (30 rules, 29 text cells, baselines on the rules, content from the top, no shrinking).
- [x] Step 4, small logo in the top-right corner and small footer chips on the last line (`PostRenderer.kt`).
- [x] Step 6, formulas: `PostMath.kt` turns `$...$` into clean one-line text (Greek letters, powers, fractions, roots). Wired into `PostFonts.styled`.
- [x] Step 2, own family: `CHAPTER_FAMILY = "Chapter summaries"` (`PostTemplatesStudy.kt`). The editor's template strip shows only this family for a chapter draft and never shows it for a poster draft (`PostEditorTabs.kt`, `TemplateStrip`). The Library lists it as its own row.
- [x] Step 7 (part): real tables and the Key Point box in `PostStudyGrid.kt`.
  - Table: consecutive `|a|b|c|` rows, first row is the header, `|---|---|` rows are ignored. Columns share the width by text length, cells wrap, every row is whole cells high so the table lines are the notebook rules.
  - Key Point box: `>> text` or `>> Label | text`, green rounded border, the star label on the first cell.
- [x] The six variants no longer draw their old fixed underlines and rules (they would cut through the text). Only the paper is decoration now.
- [x] `study-table` sample text is now a real table plus a Key Point box.

## Not done yet

- [ ] Step 5, handwriting fonts. No handwriting font is bundled (`assets/fonts/` has only `english/` and `amharic/`). The engine already looks in `assets/fonts/hand/` (a file with "bold" in its name is the bold face) and falls back to the phone's own casual font. Fastest route without code: Font tab, English, Add fonts, pick a handwriting .ttf from the phone, then tap it. That choice is per post.
- [ ] Step 7 (rest): colored section numbers (2.6, 2.7) as their own heading style, a mnemonic row with a big colored letter, per-table color. Headings with `##` already work as numbered headings if the number is typed in the text.
- [ ] Step 8: show "lines used / 30" and the overflow warning in the review screen. The renderer already returns the list of slots that did not fit (`PostStudyGrid.draw`); the UI that shows it is not checked.
- [ ] Step 9: tune the six variants after the first run on a phone (sizes, spacing).
- [ ] Step 10: test on the phone with real dense content in 9:16, 4:3, 16:9. First things to check: text sits on the rules, table lines match the rules, the logo and footer do not touch text, symbols render, wrapped Amharic lines do not break the grid.
- [ ] Step 11: Claude's generation flow (Supabase chapter, split into N sheets, markup, inbox drafts). Needs `AI_CONTENT_DELIVERY.md` and the Brand Kit `rules/AGENT_START.md` read first, and the markup above added to the agent rules.
- [ ] Open question from plan section 9: how a draft stores its template id and markup in the inbox path (`wt_post.batches`) and whether a chapter-summaries batch needs a `family` marker so the inbox opens it in the right strip. The strip currently decides by the template id, so no marker is needed.

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
