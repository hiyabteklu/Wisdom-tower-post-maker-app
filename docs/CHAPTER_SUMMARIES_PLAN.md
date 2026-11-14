# Chapter Summaries — Handwritten Dense Notes (Plan)

Status: PLAN ONLY (nothing implemented yet). Written 2026-11-14 after a long design discussion. Continue implementation from this file.

This plan replaces the earlier ChatGPT-written "six full-page study note templates" spec, which assumed a human-driven rich-text editor with toolbars. That was the wrong task.

---

## 1. What this is really for

TikTok students save photographed handwritten-style study notes. We want to publish the same kind of content for Wisdom Tower Academy as **9:16 images that look like handwritten notes**: dense, colorful, beautiful, full page.

**Claude is the author, not the user.** The workflow is a content pipeline:

1. The owner says e.g. "Freshman package, Physics, Chapter 4, notes, in 5 sheets".
2. Claude reads the chapter notes from the Wisdom Tower **Supabase** (Claude has the Supabase connector).
3. Claude splits the chapter into N sheets and writes each sheet in the app's markup, choosing colors and layout itself.
4. Claude sends all N sheets into the **post maker inbox** as drafts (1/N ... N/N).
5. The owner reviews in the inbox, optionally changes template variant, and saves to gallery. No hand-editing of colors/design.

So the app's job is a **reliable renderer** of the markup Claude sends. A heavy rich-text editor is NOT required.

## 2. Reference look

A TikTok geography note page (grade 9, climate of Ethiopia) shows the target:

- Ruled notebook paper, text sitting exactly on the lines.
- Numbered section headings (2.6, 2.7, 2.8 ...) in bold colored handwriting, underlined.
- Real bordered tables (colored border, header row, wrapped cells).
- Bullets where the key term is colored and the explanation is dark ink.
- A boxed "★ Key Point" at the bottom.
- Many purposeful colors, page filled edge to edge.

## 3. Problems seen in the current output (screenshots)

1. Full-page template overflows: item 13 was cut off at the bottom (silent truncation).
2. The first draft in the inbox (1/5) rendered raw LaTeX (`$\omega$`, `$F=-kx$`, `$a_{max}$`) instead of symbols, and used only 3 short points on a mostly empty page.
3. Content is not dense: roughly half the page is empty, and the content does not start at the top.
4. Fonts look like computer type, not handwriting.
5. Top-right Wisdom Tower logo is too big (even at the smallest setting) and sits mid-page instead of in the corner.
6. Website + Telegram handle chips are too big and not at the bottom edge.
7. Wrapped lines sometimes cross ruled lines (two text lines falling in one line space, text between lines).

## 4. Decisions made with the owner

- The existing six study templates MAY be replaced, as long as the new ones actually work.
- Keep **6 design variants** so there is choice.
- Keep existing color settings, review flow, and other inbox settings.
- These long-text templates form their **own family**, named **"Chapter summaries"**.
  - When a draft from this family is opened in the editor, the template picker shows ONLY this family's 6 variants.
  - They must not be mixed with the other poster templates (and poster drafts should not show these).

## 5. Layout requirements (page geometry)

- 9:16 is the primary target (4:3 and 16:9 must still work).
- About **30 ruled lines per page**. Every text baseline sits on a rule.
  - Line height = exactly the rule spacing (or an exact integer multiple for headings).
  - Wrapped lines each take their own rule; never two text lines in one line space, never text floating between rules.
  - Headings, table rows, and boxes must snap to the line grid (heights are multiples of the rule spacing).
- Content **starts at the top** (line 1 area, below the logo row), and fills edge to edge down to the footer.
- Footer: website and Telegram chips **much smaller**, sitting at the very bottom edge (around line 30).
- Logo: **smaller**, moved to the **top-right corner** (first line, right end), not mid-page.
- No overlap, no clipping, no silent truncation. If content does not fit on a sheet, expose overflow (warning in the review UI, or Claude re-splits the chapter into more/shorter sheets). Never shrink to unreadable size.
- Because Claude authors the content, Claude should budget content per sheet to about 30 lines. A renderer-side helper that reports "lines used / 30" would let Claude verify fit.

## 6. Fonts

- Beautiful **handwriting-style** fonts (not computer type). Inspect the bundled Wisdom Tower fonts first and reuse any handwriting-style ones already in assets.
- If a new handwriting font is added it must be bundled in the app (offline-first, no network).
- Must render English well; check Amharic/Ge'ez fallback so mixed text does not break line spacing.
- Different handwriting styles can be used for headings vs body (e.g. marker-like headings, pen-like body) to get the notebook feel.

## 7. Markup (what Claude sends, what the renderer must support)

Start from what `docs/RICH_STUDY_FORMAT.md` already supports and verify each item renders correctly:

- `**bold**`, `*italic*`, `==highlight==`, `[[key term]]`, `#underlined#`, `[[#RRGGBB:text]]`, `[[#RRGGBB;B/I/U/BU/H:text]]`, `` `code` ``, `\box{...}`, headings, bullets, nested numbering, note/key-idea callouts.
- **Formulas / symbols**: must render cleanly (ω, φ, π, Δ, λ, √, ½, superscripts/subscripts, fractions) in handwriting style. Raw `$...$` must never appear in output. Either parse `$...$` into Unicode text with sup/sub styling, or render with a proper math renderer that works offline.
- **Tables**: a real table block (header row, colored borders, wrapped cells, column widths, row heights snapped to the line grid). Needs a markup form Claude can emit, e.g. pipe rows:
  ```
  |Zone|Altitude|Temperature|Characteristics|
  |Dega|>2,500 m|10-20°C|Cool, high rainfall...|
  ```
- **Section headings with numbers** (2.6, 2.7 ...), colored and underlined.
- **Key Point box**: bordered box with ★ label at the bottom (like the reference).
- Colored bullets where the leading term is colored (e.g. `- [[#D11A26:Agriculture]] – determines crop type...`).
- Checkmark items, mnemonic rows (big colored letter + phrase + explanation).

Any new markup should be additive and keep old saved drafts rendering.

## 8. The six variants (Chapter summaries family)

All six share the dense 30-line grid, handwriting fonts, top-right small logo, and tiny bottom footer. They differ in structure and ink style:

1. **Cover / chapter opener** – title, label, short description; denser than a poster but with more whitespace. (id: study-cover)
2. **Numbered notes** – 20–22 numbered points per page, one point per 1–2 lines. (study-list)
3. **Definition notes** – key term, definition paragraphs, examples, "Remember" box. (study-define)
4. **Exam key points / checklist** – sections with sub-headings, check marks, bold key phrase + explanation, red exam-trap lines, green key facts. (study-checklist)
5. **Table notes** – one or more real tables plus short bullet sections and a Key Point box (closest to the TikTok reference). (study-table)
6. **Mnemonic sheet** – big acronym, letter-by-letter rows, memory tricks. (study-mnemonic)

Keep existing template ids where possible so old drafts still open. Keep the visual palette (blue headings, red labels/warnings, green key facts, purple mnemonics, yellow highlight) but let Claude choose per-sheet colors in markup.

## 9. Inbox / batch behaviour

- Claude pushes N drafts into the inbox as one batch ("Review batch", "N posts · swipe to check"), each with a caption, template id from the Chapter summaries family, and aspect ratio 9:16 by default.
- Preserve: aspect ratio selector (4:3, 9:16, 16:9), preview, Save to gallery, edit post, edit caption.
- Open question to verify in code: how drafts are delivered into the inbox today (file, Supabase table, API) and how a draft stores template id + markup. Claude should send through that same path.

## 10. Implementation steps (next session)

1. Read the repo: `PostTemplatesStudy.kt`, the study renderer, `docs/RICH_STUDY_FORMAT.md`, the inbox/draft model, fonts in assets, template picker code.
2. Add a template **family** field (e.g. `family = "chapter-summaries"`) and filter the editor's picker by the draft's family.
3. Build the **line-grid layout engine**: fixed rule spacing, ~30 lines, baseline snapping, content start at top, footer on last line.
4. Shrink and relocate the logo (top-right, first line) and footer chips (bottom edge, small).
5. Add/choose handwriting fonts (bundled) and apply to all six variants.
6. Fix formula/symbol rendering; guarantee no raw `$` in output.
7. Add table, section-heading, Key Point box, colored-bullet and mnemonic blocks snapped to the grid.
8. Overflow handling: report lines used vs 30; never clip silently.
9. Build the six variants on top of the shared engine.
10. Test with real dense content (physics Ch 4/5 from Supabase and a geography table page like the reference) in 9:16, 4:3, 16:9. Check overlap, clipping, line alignment, symbols, export sharpness.
11. Wire Claude's generation flow: Supabase chapter -> split into N sheets -> markup -> inbox drafts.

## 11. Non-negotiables

- Text sits on the ruled lines; no overlap; no two lines in one line space.
- Dense page, content from the top, edge to edge.
- Handwriting look, not computer type.
- No silent truncation; no unreadably small text.
- Do not break existing posters, drafts, exports, colors, review/inbox settings, or offline-first behaviour.
- No new network dependencies.
