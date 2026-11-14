# Math rendering, template switching and design controls

Status: CODE WRITTEN FOR STEPS A TO D, NOT COMPILED (started 14 Nov 2026, evening). Written 14 Nov 2026. Read this before touching math rendering, the six templates, the inbox draft flow or the editor controls.
Nothing below has been compiled or run (the assistant cannot build the app). Build and test before anything depends on it. Expect small compile errors (a missing import, a renamed helper); fix those first.

## Brief

Fix the following issues in Wisdom Tower Post Maker App. Inspect the existing codebase and implement the fixes properly. Do not just suggest changes or create mock UI.

### 1. Professional mathematical rendering

- Replace plain-text/poor LaTeX rendering with a proper mathematical typesetting engine.
- Use the same advanced LaTeX rendering approach as our educational app wherever possible.
- Support complex equations, fractions, matrices, integrals, summations, vectors, subscripts, superscripts, Greek symbols, aligned equations and multiline expressions.
- Equations must render beautifully, remain readable, scale correctly and never overflow or break across lines awkwardly.
- Preserve editable LaTeX source.

### 2. Make all 6 templates functional

- All six existing templates must actually work, not just appear as selectable options.
- Allow changing the template after a batch arrives in the inbox as a draft.
- Provide two editing scopes: Single page and Entire batch.
- Single-page changes affect only the selected slide; entire-batch changes apply consistently to all slides.
- Switching templates must preserve the original content and mathematical expressions.

### 3. Reliable design controls

Make these controls fully functional:

- Text and background color selection.
- Font family selection.
- Font size adjustment.
- Template switching.
- Consistent spacing, alignment and layout controls.

### 4. Editing experience

Create a reliable, intuitive editing workflow with clear single-slide and whole-batch selectors. Changes should update the preview immediately and persist correctly when saved. Avoid losing content when switching designs or modifying formatting.

## Constraints

- Study the existing architecture, database structure, rendering pipeline and the educational app's math renderer before implementation.
- Reuse existing components where appropriate rather than introducing unnecessary dependencies.
- Fix the underlying problems, not just their visual symptoms.
- Preserve the existing Wisdom Tower branding and design identity.

## Test list

- All six templates.
- Complex LaTeX expressions.
- Single-slide editing.
- Batch-wide editing.
- Saving and reopening drafts.

## Findings from reading the code (14 Nov 2026)

"The six templates" is read as the six Chapter summaries variants (`study-cover`, `study-list`, `study-define`, `study-checklist`, `study-table`, `study-mnemonic`). Why they did not work as selectable options:

1. `study-cover` and `study-define` had no `list` box, and the other four had no `body` box. `PostStudyGrid` only draws the boxes the variant declares, so switching a dense list sheet to cover or define made all the list rows disappear (the words were still stored, just not drawn).
2. The template strip uses `PostDoc.withTemplate`, which adds the new variant's SAMPLE words for every box that is empty. In a batch the editor then saved those samples into the post (for example `SQ3R` or `GRADE 9 \u2022 GEOGRAPHY` appearing on a physics sheet).
3. `PostStudyGrid` ignored the overall `Text size` (`textScale`) and the per-box Font; headings ignored the text color; a dark background made the dark ink unreadable.
4. The batch review screen had only a one-shot "Apply all"; there was no whole-batch design panel, no template switch for the batch, and design edits were lost when the batch was closed.
5. Math: `PostMath` makes one-line Unicode text only. No stacked fractions, root bars, matrices, integrals with limits, aligned or multi-line equations. The educational app is a native Kotlin app (not KaTeX), so there is no shared renderer to copy: a small Kotlin LaTeX layout engine (`PostTex.kt`) is written instead, with no new dependency.

## Progress

- [x] Step A (pushed, not compiled): every study variant declares every text box (`PostTemplatesStudy.kt`, `stPaperLayout`). Fixes finding 1.
- [x] Step B (pushed, not compiled): `PostTex.kt` LaTeX layout engine; `PostStudyGrid` draws display math through it (formulas on whole cells, multi-line merge).
- [x] Step C (pushed, not compiled): `PostStudyGrid` honors Text size, per-box Font, text color on headings, and light ink on dark backgrounds.
- [x] Step D (pushed, not compiled): `PostBatchDesign.kt` (Design panel, `withTemplateKeepingWords`, `PostDesignStore`) and `PostBatchScreen.kt` wiring. The panel sits under the preview in the review step. Scope switch "This page / Entire batch" (default Entire batch). Entire batch changes the batch design and every page that has its own design; This page gives that page its own design (a copy of the batch design, keeping its own photo). Template switches keep exactly the words the post has (no sample words). Designs of an inbox batch are saved in SharedPreferences `wt_post_designs` (key = inbox batch id) and reloaded when the batch is reopened. Studio "Apply all" and Done also save.
- [ ] Step E: build, fix compile errors, then run the test list above on the phone. Things to look at first: `PostTex` line splitting on long formulas, the font chips in the Design panel (`PostFonts.typeface`), and that a batch opened from the inbox restores its saved designs.

## Known limits of Step D

- Designs are only remembered for inbox batches (they have an id). A local studio batch (typed list) forgets its design when closed.
- The saved batch design is a snapshot of the open post's design the first time something is changed; later changes to the studio's open post do not flow into an already-saved batch design.
- Words typed in a box that the new template does not draw stay stored and come back when the template is switched back.
