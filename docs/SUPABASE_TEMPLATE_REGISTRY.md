# Supabase template registry — current Post Maker library

Generated from the current Android app template definitions and live `wt_post.template_boxes` registry.

- App source commit: `113239d02d1dd4218572328fb7090c0e3dc22579`
- Current templates: **70**
- Current text-slot definitions: **250**
- Legacy template IDs are retained separately for historical batches and are not offered for new AI saves.
- Supabase project: Ft agritech (`zfgrgbrrqclugszsotfo`), private schema `wt_post`.

## Slot-name mapping

| Android app slot | Supabase JSON key |
|---|---|
| `LETTER` | `letter` |
| `WORD` | `word` |
| `EYEBROW` | `eyebrow` |
| `HEADLINE` | `headline` |
| `BODY` | `message` |
| `NUMBER` | `number` |
| `LIST` | `list` (array of strings) |
| `BUTTON` | `button` |

The database requires every key listed for the selected template and rejects keys that template does not have. The AI must not send app-internal slot names such as `body`; BODY is stored as `message`.

## Current templates and exact required boxes

| Template ID | Family | Required payload keys | Backend limits |
|---|---|---|---|
| `study-checklist` — Exam key-points sheet | Chapter summaries | `eyebrow`, `headline`, `list` | eyebrow 24; headline 36; list 320; 1–40 items |
| `study-cover` — Notebook cover | Chapter summaries | `button`, `eyebrow`, `headline`, `message` | button 38; eyebrow 26; headline 26; message 50 |
| `study-define` — Definition notes | Chapter summaries | `button`, `eyebrow`, `headline`, `message` | button 48; eyebrow 24; headline 24; message 165 |
| `study-list` — Handwritten numbered list | Chapter summaries | `eyebrow`, `headline`, `list` | eyebrow 24; headline 24; list 320; 1–40 items |
| `study-mnemonic` — Mnemonic study sheet | Chapter summaries | `eyebrow`, `headline`, `list`, `number` | eyebrow 24; headline 40; list 320; 1–40 items; number 16 (giant) |
| `study-table` — Handwritten notes table | Chapter summaries | `eyebrow`, `headline`, `list` | eyebrow 27; headline 24; list 320; 1–40 items |
| `device-cutout-hero` — 3D Phone + Cutout | Device mockup | `button`, `eyebrow`, `headline` | button 22; eyebrow 24; headline 25 |
| `device-headline` — Headline + phone | Device mockup | `button`, `eyebrow`, `headline`, `message` | button 22; eyebrow 24; headline 81; message 80 |
| `device-hero` — Phone showcase | Device mockup | — | No text slots; image/screenshot required |
| `device-tablet-duo` — Tablet & Phone Pair | Device mockup | `eyebrow`, `headline` | eyebrow 24; headline 24 |
| `device-trio-scroll` — Triple Phone Scroll | Device mockup | `eyebrow`, `headline` | eyebrow 24; headline 24 |
| `info-bignumber` — Big Number Hook | Information | `eyebrow`, `headline`, `message`, `number` | eyebrow 24; headline 24; message 50; number 16 (giant) |
| `info-cardfan` — Card Fan | Information | `button`, `eyebrow`, `headline` | button 22; eyebrow 24; headline 24 |
| `info-dateslab` — Date Slab | Information | `button`, `eyebrow`, `headline`, `number` | button 22; eyebrow 24; headline 24; number 16 (giant) |
| `info-definition` — Dictionary card | Information | `eyebrow`, `headline`, `list`, `message`, `word` | eyebrow 37; headline 122; list 80; 1–8 items; message 75; word 24 (giant) |
| `info-formula` — Formula card | Information | `eyebrow`, `headline`, `list`, `word` | eyebrow 26; headline 88; list 80; 1–8 items; word 24 (giant) |
| `info-pullquote` — Pull Quote Poster | Information | `eyebrow`, `headline`, `message` | eyebrow 24; headline 24; message 50 |
| `info-questionhero` — Question Hero | Information | `button`, `eyebrow`, `headline`, `number` | button 22; eyebrow 24; headline 26; number 16 (giant) |
| `info-ribbonpath` — Ribbon Path | Information | `eyebrow`, `headline`, `list` | eyebrow 24; headline 24; list 80; 1–8 items |
| `info-stat` — Big stat + breakdown | Information | `eyebrow`, `headline`, `list`, `message`, `number` | eyebrow 32; headline 90; list 80; 1–8 items; message 67; number 16 (giant) |
| `info-steps` — Numbered step cards | Information | `eyebrow`, `headline`, `list`, `message` | eyebrow 34; headline 52; list 80; 1–8 items; message 68 |
| `info-strikereveal` — Strike and Reveal | Information | `eyebrow`, `headline`, `message`, `word` | eyebrow 24; headline 24; message 50; word 24 (giant) |
| `info-triplestack` — Triple Stack | Information | `eyebrow`, `headline`, `list` | eyebrow 24; headline 24; list 80; 1–8 items |
| `info-twinphones` — Twin Phones | Information | `eyebrow`, `headline`, `word` | eyebrow 24; headline 24; word 24 (giant) |
| `ltr-alphabet` — Alphabet card | Letter | `eyebrow`, `headline`, `letter`, `message`, `word` | eyebrow 32; headline 68; letter 8 (giant); message 111; word 24 (giant) |
| `ltr-bigword` — Big word | Letter | `eyebrow`, `headline`, `message`, `word` | eyebrow 24; headline 76; message 90; word 24 (giant) |
| `ltr-coverline` — Cover Line | Letter | `eyebrow`, `headline`, `letter`, `message` | eyebrow 36; headline 26; letter 8 (giant); message 50 |
| `ltr-mirrorsplit` — Mirror Split | Letter | `eyebrow`, `headline`, `letter`, `message` | eyebrow 24; headline 27; letter 8 (giant); message 61 |
| `ltr-number` — Big number | Letter | `eyebrow`, `headline`, `message`, `number` | eyebrow 24; headline 59; message 110; number 16 (giant) |
| `ltr-portal` — Letter Portal | Letter | `eyebrow`, `headline`, `letter`, `message` | eyebrow 24; headline 24; letter 8 (giant); message 63 |
| `mkt-alerttape` — Alert Tape | Marketing | `button`, `eyebrow`, `headline` | button 25; eyebrow 24; headline 24 |
| `mkt-countdown` — Countdown alert | Marketing | `button`, `eyebrow`, `headline`, `message`, `number`, `word` | button 22; eyebrow 34; headline 50; message 102; number 16 (giant); word 24 (giant) |
| `mkt-docstack` — Doc Stack | Marketing | `button`, `eyebrow`, `headline` | button 22; eyebrow 24; headline 24 |
| `mkt-floating-hero` — Floating Object Hero | Marketing | `button`, `eyebrow`, `headline` | button 22; eyebrow 24; headline 24 |
| `mkt-follow` — Follow us | Marketing | `button`, `eyebrow`, `headline`, `message` | button 22; eyebrow 24; headline 49; message 81 |
| `mkt-monument` — Number Monument | Marketing | `eyebrow`, `headline`, `number` | eyebrow 25; headline 24; number 16 (giant) |
| `mkt-poster` — Launch poster | Marketing | `button`, `eyebrow`, `headline`, `message` | button 22; eyebrow 24; headline 59; message 90 |
| `mkt-seal` — Official seal | Marketing | `button`, `eyebrow`, `headline`, `message`, `number`, `word` | button 22; eyebrow 40; headline 54; message 113; number 16 (giant); word 24 (giant) |
| `mkt-ticket` — Event ticket | Marketing | `button`, `eyebrow`, `headline`, `message`, `number` | button 22; eyebrow 24; headline 46; message 53; number 16 (giant) |
| `photo-behind` — Words behind subject | Photo | `eyebrow`, `headline`, `message`, `word` | eyebrow 24; headline 12 (giant); message 113; word 24 (giant) |
| `photo-cornerpeel` — Corner Peel | Photo | `button`, `eyebrow`, `headline`, `message` | button 22; eyebrow 24; headline 29; message 54 |
| `photo-cover` — Magazine cover | Photo | `eyebrow`, `headline`, `message`, `word` | eyebrow 36; headline 20 (giant); message 87; word 24 (giant) |
| `photo-duo` — Side-by-side duo | Photo | `eyebrow`, `headline`, `message` | eyebrow 29; headline 69; message 110 |
| `photo-floor` — Full photo + floor | Photo | `eyebrow`, `headline`, `message` | eyebrow 24; headline 54; message 106 |
| `photo-glass` — Frosted glass card | Photo | `button`, `eyebrow`, `headline`, `message` | button 22; eyebrow 24; headline 65; message 108 |
| `photo-gueststage` — Guest Stage | Photo | `eyebrow`, `headline`, `message`, `number` | eyebrow 24; headline 24; message 50; number 16 (giant) |
| `photo-magazine` — Magazine masthead | Photo | `eyebrow`, `headline`, `message`, `word` | eyebrow 25; headline 61; message 81; word 24 (giant) |
| `photo-polaroid` — Tilted polaroid | Photo | `eyebrow`, `headline`, `message` | eyebrow 24; headline 63; message 81 |
| `photo-triptych` — Three-strip photo | Photo | `eyebrow`, `headline`, `message` | eyebrow 24; headline 64; message 92 |
| `photo-voiceframe` — Voice Frame | Photo | `eyebrow`, `headline`, `message` | eyebrow 29; headline 24; message 50 |
| `scn-backpackportal` — Unzipped Backpack World | Scene | `eyebrow`, `headline`, `message` | eyebrow 24; headline 26; message 94 |
| `scn-bookpool` — Book Swimming Pool | Scene | `eyebrow`, `headline`, `message` | eyebrow 24; headline 24; message 81 |
| `scn-capufo` — Graduation Cap Tractor Beam | Scene | `eyebrow`, `headline`, `message` | eyebrow 26; headline 24; message 87 |
| `scn-clockhang` — Hanging on the Clock Hand | Scene | `eyebrow`, `headline`, `message`, `word` | eyebrow 24; headline 24; message 92; word 24 (giant) |
| `scn-deskflat` — Study Desk Flat-Lay | Scene | `eyebrow`, `headline`, `message` | eyebrow 24; headline 24; message 50 |
| `scn-esckey` — Escape Key Portal | Scene | `eyebrow`, `headline`, `message`, `word` | eyebrow 24; headline 24; message 86; word 24 (giant) |
| `scn-examcliff` — Exam Paper Canyon Bridge | Scene | `eyebrow`, `headline`, `message` | eyebrow 24; headline 24; message 80 |
| `scn-facelight` — Glowing Mind Silhouette | Scene | `eyebrow`, `headline`, `message` | eyebrow 24; headline 24; message 91 |
| `scn-housedoor` — Open Door to Campus | Scene | `eyebrow`, `headline`, `message` | eyebrow 24; headline 24; message 60 |
| `scn-paperboat` — Origami Paper Boat on Sea | Scene | `eyebrow`, `headline`, `message` | eyebrow 24; headline 24; message 83 |
| `scn-pencilcity` — Pencil Skyscraper City | Scene | `eyebrow`, `headline`, `message` | eyebrow 24; headline 25; message 73 |
| `type-auroraword` — Aurora Word | Type | `eyebrow`, `headline`, `message` | eyebrow 24; headline 24; message 50 |
| `type-chips` — Sticker chips | Type | `eyebrow`, `headline`, `message` | eyebrow 24; headline 24; message 104 |
| `type-extrude` — 3D block type | Type | `eyebrow`, `headline`, `message`, `word` | eyebrow 24; headline 92; message 76; word 24 (giant) |
| `type-grid` — Swiss type grid | Type | `eyebrow`, `headline`, `list`, `message`, `number` | eyebrow 27; headline 67; list 80; 1–8 items; message 100; number 16 (giant) |
| `type-ink` — Ink & echo | Type | `button`, `eyebrow`, `headline`, `message` | button 22; eyebrow 24; headline 20 (giant); message 110 |
| `type-neon` — Neon outline stack | Type | `eyebrow`, `headline`, `message` | eyebrow 27; headline 24; message 92 |
| `type-ribbon` — Diagonal ribbon band | Type | `button`, `eyebrow`, `headline`, `message` | button 22; eyebrow 33; headline 59; message 87 |
| `type-spotlightcut` — Spotlight Cut | Type | `eyebrow`, `headline`, `message` | eyebrow 24; headline 24; message 52 |
| `type-ticker` — Diagonal ticker band | Type | `eyebrow`, `headline`, `message`, `word` | eyebrow 29; headline 68; message 87; word 24 (giant) |

## Delivery rules

- One batch uses one template; up to 50 posts per batch.
- Every post has exactly one non-empty caption in the same array position.
- `save_batch` accepts current template IDs only and creates `draft` batches only.
- `pillar` must be one of the eight official pillars; the database derives the inbox folder.
- Normal list boxes allow 1–8 items, up to 80 characters each. Chapter Summary list boxes allow 1–40 items, up to 320 characters each.
- `device-hero` deliberately has no text boxes. Send an empty post object; the owner adds the screenshot in the app.
- Photo and device mockup templates store text and captions only. The owner supplies or selects the actual photo/screenshot in the app; use `notes` to describe what is needed.
- Existing legacy template definitions remain available for old batches but should not be chosen for new content.

## Validation

The database trigger `wt_post.batches_check` calls `wt_post.check_batch`. That function validates template registration, exact slot keys, required non-empty values, list shape and limits, and caption count. The `wt-mcp` Edge Function is version 7 and its `save_batch` tool exposes the 70 current template IDs.
