# BACKDROPS: 10 fundamentally different poster backgrounds

Written 2026-11-01 (follows docs/BRAND_COLOR_PLAN.md). Status of that plan: phase 1 (dark-first renderer, commit 4e969cd) is done, phase 2 (the 10 themes) is done here, phases 3 and 4 are still open.

## What changed

The old themes only changed four colors, so every poster kept the same tinted-gradient background. Now a theme also picks a **backdrop look** (PostBackdrops.kt). A look replaces the template's own backdrop on every template (dark-first brands only, and only when the post uses the template background). Each look has its own base gradient, its own glow placement and its own pattern.

| id | Look | Base | Glow | Pattern |
|----|------|------|------|---------|
| 1 | Signature Navy | blue-black | Main, top-right | none |
| 2 | Black Gold | black to bronze | Main, bottom-left | diagonal lines |
| 3 | Deep Petrol | dark teal | Main, spotlight | rings |
| 4 | Warm Amber | black to amber | Main, spotlight | dot grid |
| 5 | Cyan Grid | navy blue | Main + blue, two corners | fine grid |
| 6 | Obsidian | graphite black | Main, rising from bottom | none |
| 7 | Midnight Indigo | indigo | Main + Second, two corners | dot grid |
| 8 | Graphite | charcoal | Main, top-right | fine grid |
| 9 | Bronze | brown-black | Main, bottom-left | arcs |
| 10 | Neon Night | purple-black | Main + pink, two corners | diagonal lines |

- Brand > Colors > **Themes**: sets the four colors and the look together.
- Brand > Colors > **Background**: changes only the look (Auto = the phase 1 tinted dark, no look).
- The look id is stored in the brand as `bgLook` (0 = Auto). Never renumber ids.
- Glows use the brand's Main and Second, so changing a color still restyles the glows.

## Renderer polish added with it (dark-first only)

- Headlines in light text get a soft glow in the Main color (drawSlot).
- Large rounded cards get a thin light border, like the glass cards of the app (drawDecor).

## Not done yet

- Per-template design review (layouts, ribbons and medals, phase 4 of BRAND_COLOR_PLAN.md). Needs screenshots of the new backgrounds first.
- Phase 3 light posters option.
- Nothing here was compiled or run. Build and test, then send screenshots of 3 templates (photo, quote, marketing) on 3 different looks.
