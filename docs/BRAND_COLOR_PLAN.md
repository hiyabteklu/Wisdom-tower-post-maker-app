# BRAND COLOR PLAN (Post app): make posters look like the Wisdom Tower app

Written 2026-11-01. Read this first, then do the phases in order. Do not re-audit finished phases.

## 1. The problem (owner's words and screenshots)

The 10 ready-made themes in Brand > Colors > Themes (commits 73fd841, e4bddd8, ea503dd) do not look like the Wisdom Tower app. They gave muddy brown-gold and teal cards on pale backgrounds. The real app look:

- Near-black / blue-black screens everywhere (no pale screens)
- Bright neon cyan #22E0FF for headings, numbers, buttons, glows (hover #00C4E6)
- Gold #FFD54A for sub-headings, gold/bronze ribbons and medals
- White text, a little pink glow on headline edges
- Glassy translucent cards, rounded corners about 24 px, thin lighter border
- Source: docs of the Academy repo (tailwind.config.ts: wisdom.cyan #22e0ff, cyan-dark #00c4e6) and the owner's screenshots

## 2. Root cause (verified by reading PostRenderer.kt)

Colors come from 4 brand values (PostRenderer.colorsFor):

- accent = doc.accentHex ?: brand.primaryHex ("Main")
- dark = darkHex, light = lightHex, secondary = secondaryHex
- deep = mix(accent, dark, .45); glow = mix(accent, secondary, .40); tint = mix(light, accent, .10); soft = mix(light, secondary, .45)
- text = doc.textHex ?: darkHex

Base of each template (PostBase): LIGHT = flat `light`; PRIMARY = flat `accent`; DARK = flat `dark`. Many templates use a backdrop gradient ACCENT to DEEP with light text. The logo box is a gradient deep to dark with a secondary edge.

Problems this causes:

1. Pale posters: every LIGHT-base template is pale, the app has none.
2. Main is both the poster background (white text on it) and the accent. Bright cyan as Main gives white text on cyan (about 1.5:1). Darkening it for the text gives muddy teal / brown. A single Main cannot be right for both uses.
3. Only 3 templates use PostBase.PRIMARY (2 in PostTemplatesPhoto.kt, 1 ticket in PostTemplatesMarketing.kt), but many use the ACCENT to DEEP backdrop.

=> Choosing four colors cannot fix this. The renderer needs a dark-first mode.

## 3. Target look (acceptance criteria)

After the work, with the default theme, on every template and all 4 sizes:

- Background is near-black or deep blue-black (like #060B14 to #0E1726), never pale, unless the user turns "Light posters" on
- Headlines white or bright cyan; sub-labels gold
- Accent shapes (bands, rings, pills, buttons) bright cyan, with a soft cyan glow
- Gold used for badges / ribbons / second accent
- Any text on any fill has contrast 4.5:1 or better (3:1 for text larger than about 24 px); no white text on cyan or gold
- Footer chips, social cards and the logo box stay readable
- A side-by-side with the app home screen looks like the same brand

## 4. Phases

### Phase 1: dark-first switch (renderer)

1. Add `darkFirst: Boolean = true` to PostBrand (PostModel.kt): encodeBrand / decodeBrand with default true so old saves become dark-first. Add a "Poster look: Dark / Light" toggle on Brand > Colors.
2. In PostRenderer.colorsFor when darkFirst:
   - `light` stays the light text color (white or very pale cyan-white)
   - add `surface` = a deep blue-black built from darkHex (slightly lighter than dark, for cards)
   - text = light-ish (unless the post has textHex)
   - tint = mix(dark, accent, .14), soft = mix(dark, secondary, .18) (dark tints instead of pale ones)
3. backgroundFor: PostBase.LIGHT becomes a dark gradient (dark to surface); PostBase.PRIMARY becomes an accent-on-dark gradient (dark to deep, with an accent glow) instead of flat accent.
4. toneColor: TEXT and MUTED map to light text when darkFirst. DARK tone used for text ON accent/secondary fills stays dark (button text on cyan or gold must be near-black, not white).
5. AUDIT (needed, do not guess): grep decor and slots using PostTone.LIGHT / PostTone.TEXT in templates whose base is LIGHT or that draw LIGHT cards. A LIGHT-toned card with TEXT-toned text turns invisible after the mapping. For each, choose: card = surface + light text, or keep pale card + dark text. Files: PostTemplates.kt, PostTemplatesPhoto.kt, PostTemplatesMarketing.kt, PostEngine.kt (drawMesh uses accent, glow, deep, secondary).
6. Glow: add a soft outer glow (blur of accent at about 35% alpha) behind accent bands and rings (drawDecor), and a gentle cyan glow behind big headline text if cheap.
7. Logo box: deep to dark gradient stays; edge uses secondary.

Done when: every template thumbnail in the Library looks dark, readable, on-brand.

### Phase 2: the 10 themes (palette only, after Phase 1)

All themes: near-black Dark, white-ish Light (text), bright Main and Second. Brand colors to use: cyan #22E0FF / #00C4E6, gold #FFD54A / #FFC83D, bronze #B8893A, white #FFFFFF, black #05070D. Suggested set (Dark, Main, Second, Light):

1. Wisdom Signature: #060B14, #22E0FF, #FFD54A, #FFFFFF
2. Gold Lead: #0A0805, #FFD54A, #22E0FF, #FFFFFF
3. Cyan Mono: #04101A, #22E0FF, #7DF0FF, #F2FCFF
4. Gold Mono: #0B0905, #FFC83D, #FFE08A, #FFFAEB
5. Cyan and White: #070B12, #22E0FF, #FFFFFF, #FFFFFF
6. Gold and White: #0B0905, #FFD54A, #FFFFFF, #FFFBF0
7. Midnight Cyan Gold: #070B1A, #00C4E6, #FFD54A, #F6FAFF
8. Charcoal Gold: #14171C, #FFD54A, #22E0FF, #F3F5F7
9. Bronze Ribbon: #0E0B07, #B8893A, #FFD54A, #FFF8E6
10. Neon Pink Touch: #0B0F1C, #22E0FF, #FF5CD6, #FFFFFF (pink only as the glow, use sparingly)

Each card in the Themes row should show the four colors as before. Check each theme with a contrast script for text on every fill.

### Phase 3: Light posters option

When darkFirst is off, the current light behaviour stays. Keep the old 10 pale-friendly themes (commit ea503dd) as a "Light" row if wanted.

### Phase 4: polish

- Gold ribbon / medal graphics as optional decor (the app cards use them)
- Optional pink glow on headline edges
- Update the Brand screen preview so it shows the dark poster

## 5. Rules for whoever continues

- Work on `main` only (CLAUDE.md). Read the latest file from main before editing. The assistant cannot compile: say so after each change and ask the owner to build and test.
- The owner works from a phone and sends screenshots. After Phase 1, ask for screenshots of 3 templates (one photo, one quote, one marketing) and compare with the app.
- Do not reintroduce pale defaults.
- Brand facts and tone live in the `wisdom-tower-brand-kit` repo (BRAND.md, FACTS.md).

## 6. Status

- [x] Themes row added (UI)
- [x] Themes v1, v2, v3 pushed (all rejected by the owner: not like the app)
- [ ] Phase 1 dark-first renderer
- [ ] Phase 2 new 10 themes
- [ ] Phase 3 light option
- [ ] Phase 4 polish
