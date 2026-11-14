# Wisdom Tower Post Maker — Rich Study Sheet Format

This format is for dense educational TikTok sheets and chapter series. It follows the inline marks in Wisdom Tower Academy's RichContent / notification composer, with Post Maker-specific color and emphasis marks. Inline marks may be used inside paragraphs and each list row; they are not limited to whole text boxes.

## Inline marks (implemented in the native bitmap renderer)

- **bold words** — bold
- *italic phrase* — italic
- ==highlight phrase== — warm yellow marker highlight
- [[key term]] — blue key-term highlight + bold
- #underlined topic# — underline
- [[#D11A26:RED WORDS]] — custom ink color by hex
- [[#176BCE;B:BLUE BOLD]] — color + bold
- [[#16803C;BU:GREEN BOLD UNDERLINE]] — color + bold + underline
- [[#7C3AED;I:purple italic]] — color + italic
- [[#D11A26;H:colored marker]] — color + yellow marker highlight
- `code` — monospace chip
- \\newline — explicit line break
- \\box{remember this} — yellow note/emphasis box
- $F=ma$ or $$v=\\frac{d}{t}$$ — formula notation recognized and rendered in serif italic blue math styling (current bitmap renderer displays readable source notation; this is not KaTeX typesetting yet).

## Structural cues for authoring

- `# Chapter title`, `## Topic`, `### Subtopic` — heading levels for source text
- `> NB: ...` or `> KEY IDEA: ...` — callout / note cue
- `- ★ ...`, `- ✦ ...`, `- • ...` — custom symbol bullets
- `1. Topic`, `1.1. Subtopic`, `2.3.1. Detail` — manual nested numbering
- `---` — divider cue

In list-template text boxes, each newline is a separate row. Hand-authored bullet and nested numbering markers are preserved. Rich inline syntax is supported within each row. The template currently determines the structural layout; the heading/callout/table syntax above is an authoring convention and does not yet dynamically create arbitrary block layouts.

## Chapter-sheet authoring principles

Keep source accuracy, topic/subtopic order, and course terminology. Use bold/color/highlight selectively for key definitions, variables, laws, units, cautions, and exam-critical concepts. Prefer compact topic headings, one concept per row, concise NB callouts, and formula lines. Do not force all content into a generic numbered list.
