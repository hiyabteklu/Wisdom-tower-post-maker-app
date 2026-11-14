# Math rendering, template switching and design controls

Status: IN PROGRESS (started 14 Nov 2026, evening). Written 14 Nov 2026. Read this before touching math rendering, the six templates, the inbox draft flow or the editor controls.
Nothing below has been compiled or run (the assistant cannot build the app). Build and test before anything depends on it.

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

- [~] Step A (pushed, not compiled): every study variant declares every text box (`PostTemplatesStudy.kt`, `stPaperLayout`). Fixes finding 1.
- [ ] Step B: `PostTex.kt` LaTeX layout engine and its use in `PostStudyGrid` (display math blocks, auto-promoted standalone formulas).
- [ ] Step C: `PostStudyGrid`: Text size, per-box Font, text color, dark background ink.
- [ ] Step D: `PostBatchScreen`: Single page / Entire batch scope, template + color + background + font + size panel, no sample words on a variant switch, designs saved per inbox batch.
