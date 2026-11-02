Wisdom Tower Post: whole-app quality roadmap

Written 2 Nov 2026 by Claude, for any assistant that continues this work, and for Hiyab. This sits above `DESIGN_PLAN.md`. That file fixes single templates. This one fixes how the whole system works together, so quality stops depending on checking posters one by one.

## 1. The system has three parts, and they are not yet one system

```
Brand kit repo (wisdom-tower-brand-kit)   what to write, how it sounds, box limits per template
        |  chat writes posts
Backend (Supabase schema wt_post)          batches, captions, status, post_log
        |  Poster inbox
Post app (this repo)                       renders, checks fit, saves images
```

The loop works end to end (2 Nov 2026). What is missing is information flowing back, and checks that happen before a problem reaches the phone.

## 2. Gaps found

1. **The brand kit still describes the old workflow.** `AGENT_START.md` says to ask for a screenshot of the open template and to give a paste block. With the inbox the chat can pick the template itself and save the batch. The kit and the app disagree.
2. **Limits are guesses.** `TEMPLATES.md` says its box limits are estimates. The app knows the truth (it shrinks text and shows "Text shrunk") but never tells the chat or the kit.
3. **Nothing checks a batch when it is saved.** A wrong template id, a box the template lacks, or a text over its limit is only found on the phone. Direct SQL inserts bypass any check.
4. **Two post logs.** `POST_LOG.md` in the kit and `wt_post.post_log` in the backend. The chat can only avoid repeating a topic if it reads one source.
5. **Design quality is checked by eye, one poster at a time.** Nothing renders every template at every size with worst-case text, and nothing checks contrast or minimum text size. Fixes made in one template do not reach the others.
6. **Language.** The audience is Ethiopian students. The Amharic font folder was empty on 1 Nov 2026 and the fonts bundled do not cover Ethiopic letters. Not decided whether posts in Amharic are wanted (IMPROVEMENT_PLAN question 2).
7. **Untested:** Save to gallery after an inbox batch, Mark approved, Mark posted and log.

## 3. Work, in priority order

**Q1. Template QA gallery (app, debug screen).** One screen that renders every template at every size with three texts: short, typical, and worst case (every box at its kit limit). It marks any box that shrinks and any text under a minimum size or contrast ratio (4.5 for body text). One screenshot then shows the state of all 36 templates. Replaces poster-by-poster review. Check: open the screen, see a grid, overflow flagged.

**Q2. Design rules in one place (app).** A small file of shared values: minimum message size, minimum label size, text contrast rule, safe margins, logo margin. Templates read from it, so a fix applies to all. Order: Info family, Marketing, Type, then photo and split. Closes DESIGN_PLAN D2 and D3.

**Q3. Feedback loop (app and backend).** After the review step the app writes the shrink result per post into the batch (new column `feedback jsonb` in `wt_post.batches`, migration in schema `wt_post` only). The chat reads it with the next request and the kit's `TEMPLATES.md` limits get corrected from real results, not estimates.

**Q4. Check at save (backend).** A trigger or the `wt-mcp` function rejects a batch with an unknown template id, an unknown box key, or a box over its limit, with a plain message the chat can act on. Needs the template and limit table (generate it from the app's template files, do not retype it). Only schema `wt_post`.

**Q5. Align the brand kit (kit repo).** Update `AGENT_START.md`: when the chat link is on, pick the template from `TEMPLATES.md`, write the batch, save it, and say the batch title. Keep the screenshot and paste route only as a fallback. Read `wt_post.post_log` before writing so topics and hooks do not repeat, and make the database the single log (keep `POST_LOG.md` as an export or retire it, ask Hiyab).

**Q6. Reliability (app).** Cache whole batches for offline review, retry on failure, a clear message when the key is rejected. Test Save to gallery, Mark approved, Mark posted and log.

**Q7. Amharic.** Decide with Hiyab. If yes: bundle Noto Sans Ethiopic as a fallback font, scale the limits to about 70 percent (kit `TEMPLATES.md` section 7, point 10), and run the QA gallery with Amharic text.

## 4. Rules

- Work on `main`, read before editing, one slice per push, say "not compiled, please build".
- Backend: only schema `wt_post` and the `wt-mcp` function. Never `public` (shared with the FT Agri-Tech app).
- Do not hard-code template limits twice. Generate or share one table.
- Tick the slice here in the same push.

## 5. Status

- [x] Q1 Template QA gallery (first version pushed 2 Nov 2026, NOT yet compiled or run, see notes below)
- [ ] Q2 Shared design rules
- [ ] Q3 Feedback loop
- [ ] Q4 Check at save
- [ ] Q5 Brand kit aligned with the inbox workflow
- [ ] Q6 Reliability and untested buttons
- [ ] Q7 Amharic (decision needed)

### Q1 notes (what exists, what does not)

- File: `PostQaScreen.kt`. Open it from Poster inbox, button "Template QA (check every template)". Back returns to the inbox. Route name in `PostApp.kt`: `qa`.
- Done: every template at every size (36 templates, 4 sizes) with three text modes: Sample (the template's own words), Stress (every box 1.5 times longer than its sample) and Amharic (fixed Amharic words). The "Check all" button lists every box that had to shrink, with a Copy report button. Cards below show each template at the chosen size with its shrink result.
- Not done: the contrast check and the minimum text size check named above. They need the shared values from Q2 first, so they move to Q2.
- Not done: "every box at its kit limit". Stress (1.5 times the sample) stands in until the limits are generated from one table (Q4). Do not retype limits.
- Amharic mode is only exact once an Amharic font is installed (Q7).
- First thing to do after a build: open the screen, run Check all in Stress mode, copy the report, and use it to fix templates (Q2) and to correct kit `TEMPLATES.md` limits (Q3 and Q5).
