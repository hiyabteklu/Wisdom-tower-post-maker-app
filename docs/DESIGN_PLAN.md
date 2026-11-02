Wisdom Tower Post: design and workflow enhancement plan

Written 2 Nov 2026 by Claude, for any assistant that continues this work, and for Hiyab. Read `CLAUDE.md`, `CHAT_CONTROL_PLAN.md` and `BATCH_PLAN.md` first.

## 1. Where things stand (2 Nov 2026)

The chat to phone loop works, confirmed on Hiyab's phone with a screenshot:

```
Chat (brand kit rules) -> wt_post.batches (Supabase) -> Poster inbox -> Review (preview + caption) -> Save to gallery
```

- A test batch ("Test poster: close the book", template `info-tip`, 1 post) arrived, rendered, and showed its caption with a Copy caption button.
- Two fixes made this work, both on `main`:
  - `PostChat.kt`: the key is built in (`BUILT_IN_KEY`), a key typed on the Brand screen still wins. Owner decision: personal app, one phone, private repo.
  - `PostBatchScreen.kt`: a batch opened from the inbox now fills the list from `initialBatch.batchInput()` (mode Blocks) and opens the review step directly. It does not overwrite the template's saved batch draft.
- Not done yet: tapping "Save to gallery" was not tested after the review screen. Ask Hiyab to test it, and the "Mark approved" and "Mark posted and log" buttons.
- Nothing here has been compiled by the assistant. Build first (GitHub Actions, "Build Wisdom Tower Post Release APK").

## 2. How a chat writes a batch right now

The chat needs the Supabase connector. It inserts one row into `wt_post.batches` only (never `public`, see CHAT_CONTROL_PLAN section 8), project id `zfgrgbrrqclugszsotfo`.

```sql
insert into wt_post.batches (template, title, level, pillar, posts, captions, status, notes)
values ('info-tip', 'Title', 'Grade 12', 'Study tips',
  '[{"eyebrow":"...","number":"01","headline":"...","message":"..."}]'::jsonb,
  '["caption for post 1"]'::jsonb, 'draft', null)
returning id;
```

Rules: write the posts with the brand kit repo `wisdom-tower-brand-kit` (read `AGENT_START.md` first, `TEMPLATES.md` for box limits, `TONE.md` for banned and fluff words). `captions[i]` belongs to `posts[i]`. Use only box keys the template has. Never put study material in the backend.

## 3. Design findings from the first real poster (template `info-tip`, Numbered tip, 9:16)

Seen on the phone preview:

1. **Logo at the right edge.** The Wisdom Tower badge touches the right edge of the poster. It may be an intended tab (rounded on the left, flush on the right, "lowered logo box" in the template notes) or a clipping bug. Not decided. Ask Hiyab which he wants before changing it (slice D3).
2. **Message text too small and low contrast.** Dark grey on cyan, small, and a large empty gap between the headline and the message inside the card.
3. **Card taller than its text.**
4. **Small label too small.** "GRADE 12 STUDY TIP" is tiny next to the giant "01".
5. Footer chips (website and Telegram) look good. Leave them.

## 4. Slices (push in this order, one slice per push, tick in section 6)

Rules for every slice: work on `main`, read the file before editing, change one template at a time, do not change `PostCodec` (version 4), do not touch the other templates in the same push, and say "not compiled, please build".

**D1. Numbered tip fixes (`info-tip`).** PUSHED 2 Nov 2026 in `PostTemplatesInfo.kt` (`tipLayout`), not yet built or checked on the phone. Findings 2 to 4 only:
- Message now uses `PostTone.LIGHT` (same as the headline) instead of `LIGHT_MUTED`, and is larger (9:16: .034 to .040 of the width; 1:1: .030 to .034). 4:3 only changed the message tone.
- Message sits closer to the headline (9:16: y .67 to .63; 1:1: y .70 to .66). Card is shorter (9:16: .46 to .38 high, starts at .42; 1:1: .39 to .37).
- Small label larger (9:16: .024 to .030; 1:1: .024 to .028), moved left and widened so 24 characters fit; the accent rule under it moved with it. Number box narrowed so it does not overlap the label.
- Check on the phone: Library, switch through the sizes, no "text too long", 24 character label fits on one line, a 3 line headline (40 characters) does not touch the message, a 120 character message fits inside the card. If any of those fail, tighten the numbers in `tipLayout` and write the change in section 5.

**D2. Same review for the other text templates.** Open each template with a long and a short text and note the same kind of problems (small or low contrast message, dead space, small label). Do the families in this order: Information, Marketing, Type, then the photo and split templates. One push per family. Keep a short findings list in section 5. Other info templates that still use `PostTone.MUTED` for the message: `info-quote`, `info-stat`.

**D3. Brand logo margin.** Decide with Hiyab whether the logo tab at the right edge is intended. If not, fix it once in the brand header component (logo is a brand component, not a layer) instead of per template.

**D4. Workflow extras (only after D1 is checked).**
- Share to Telegram: after Save to gallery, a button that opens the share sheet limited to Telegram (the existing Share saved files button already works for any app).
- Published marker: in the inbox list, show a tick on batches with status `published`, and sort drafts first.
- Direct save from chat: register `wt-mcp` as a custom connector so any chat can call `save_batch`. Unverified: the chat app may not accept the `x-wt-key` header. If it does not, keep using the Supabase connector. Only after this works add one rule to the brand kit `AGENT_START.md`: when the chat link is on, save every batch and say the batch title.
- Delete the test batch when Hiyab says so (`delete from wt_post.batches where id = 'ae397d3d-4345-428a-abfc-56c7e1c0e12d'`), never earlier.

## 5. Findings log (add to it)

- 2 Nov 2026, `info-tip` 9:16: findings 1 to 4 in section 3. D1 pushed for 2 to 4.

## 6. Status

- [x] Chat to inbox to review loop works on the phone
- [ ] Save to gallery tested after an inbox batch
- [x] D1 Numbered tip fixes (pushed, not yet built or checked)
- [ ] D2 Other text templates reviewed and fixed
- [ ] D3 Brand logo margin (decide first)
- [ ] D4 Workflow extras

## 7. Safety

- The Supabase project is shared with the FT Agri-Tech app. Only touch schema `wt_post` and the `wt-mcp` function.
- The built-in key lives in `PostChat.kt`. If the repo ever becomes public, change the `WT_MCP_KEY` secret in Supabase first and update the key in the app.
