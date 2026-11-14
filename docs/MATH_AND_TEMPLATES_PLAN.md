# Math rendering, template switching and design controls

Status: planned, not started. Written 14 Nov 2026. Read this before touching math rendering, the six templates, the inbox draft flow or the editor controls.

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

## Progress

Nothing implemented yet.
