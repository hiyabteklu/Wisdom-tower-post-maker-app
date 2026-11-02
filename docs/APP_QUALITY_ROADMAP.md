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

1. **The brand kit still describes the old workflow.** `AGENT_START.md` says to ask for a screenshot of the open template and to give a paste block. With the inbox the chat can pick the template itself and save the batch. The kit and the app disagree. (Fixed by Q5, 2 Nov 2026.)
2. **Limits are guesses.** `TEMPLATES.md` says its box limits are estimates. The app knows the truth (it shrinks text and shows "Text shrunk") but never tells the chat or the kit.
3. **Nothing checks a batch when it is saved.** A wrong template id, a box the template lacks, or a text over its limit is only found on the phone. Direct SQL inserts bypass any check.
4. **Two post logs.** `POST_LOG.md` in the kit and `wt_post.post_log` in the backend. The chat can only avoid repeating a topic if it reads one source. (Q5 made the database the log when the chat link is on; `POST_LOG.md` is the fallback. Hiyab to decide whether to retire it.)
5. **Design quality is checked by eye, one poster at a time.** Nothing renders every template at every size with worst-case text, and nothing checks contrast or minimum text size. Fixes made in one template do not reach the others.
6. **Language.** The audience is Ethiopian students. DECIDED 2 Nov 2026: Hiyab uses English, Amharic and hybrid posters and captions, and chooses in each prompt. The kit now covers this (`LANGUAGE.md`). Still open: how well Ethiopic letters render and fit in the app (the Amharic font folder was empty on 1 Nov 2026; the phone's own font is used until one is added).
7. **Untested:** Save to gallery after an inbox batch, Mark approved, Mark posted and log.

## 3. Work, in priority order

**Q1. Template QA gallery (app, debug screen).** One screen that renders every template at every size with three texts: short, typical, and worst case (every box at its kit limit). It marks any box that shrinks and any text under a minimum size or contrast ratio (4.5 for body text). One screenshot then shows the state of all 36 templates. Replaces poster-by-poster review. Check: open the screen, see a grid, overflow flagged.

**Q2. Design rules in one place (app).** A small file of shared values: minimum message size, minimum label size, text contrast rule, safe margins, logo margin. Templates read from it, so a fix applies to all. Order: Info family, Marketing, Type, then photo and split. Closes DESIGN_PLAN D2 and D3.

**Q3. Feedback loop (app and backend).** After the review step the app writes the shrink result per post into the batch (new column `feedback jsonb` in `wt_post.batches`, migration in schema `wt_post` only). The chat reads it with the next request and the kit's `TEMPLATES.md` limits get corrected from real results, not estimates.

**Q4. Check at save (backend).** A trigger or the `wt-mcp` function rejects a batch with an unknown template id, an unknown box key, or a box over its limit, with a plain message the chat can act on. Needs the template and limit table (generate it from the app's template files, do not retype it). Only schema `wt_post`. Must allow for Amharic: a box with Ethiopic letters has a shorter limit (see `LANGUAGE.md` section 4).

**Q5. Align the brand kit (kit repo).** Update `AGENT_START.md`: when the chat link is on, pick the template from `TEMPLATES.md`, write the batch, save it, and say the batch title. Keep the screenshot and paste route only as a fallback. Read `wt_post.post_log` before writing so topics and hooks do not repeat, and make the database the single log (keep `POST_LOG.md` as an export or retire it, ask Hiyab).

**Q6. Reliability (app).** Cache whole batches for offline review, retry on failure, a clear message when the key is rejected. Test Save to gallery, Mark approved, Mark posted and log.

**Q7. Amharic and hybrid.** DECIDED: yes, all modes, chosen per prompt. Kit side is done (`LANGUAGE.md`). App side still to do: confirm Ethiopic renders well (bundle an Ethiopic font as the default if the phone's own font looks wrong; a font file cannot be pushed by the chat tools, so Hiyab adds it on the phone or in the repo), then run the QA gallery in Amharic mode and correct the 70 percent and 60 percent estimates in `LANGUAGE.md` from the real report.

## 4. Rules

- Work on `main`, read before editing, one slice per push, say "not compiled, please build".
- Backend: only schema `wt_post` and the `wt-mcp` function. Never `public` (shared with the FT Agri-Tech app).
- Do not hard-code template limits twice. Generate or share one table.
- Tick the slice here in the same push.

## 5. Status

- [x] Q1 Template QA gallery (first version pushed 2 Nov 2026, NOT yet compiled or run, see notes below)
- [ ] Q2 Shared design rules: part 1 pushed (`PostDesignRules.kt`, small-text check in Template QA, not compiled); part 2 (templates read the shared values, fix the flagged ones) waits for the QA report
- [ ] Q3 Feedback loop
- [ ] Q4 Check at save
- [x] Q5 Brand kit aligned with the inbox workflow (2 Nov 2026, kit repo commit 4fe88e6; `AGENT_START.md` rewritten, see notes below)
- [ ] Q6 Reliability and untested buttons
- [ ] Q7 Amharic and hybrid: decided and kit side done (2 Nov 2026, `LANGUAGE.md`); app rendering check and real limits still open

### Q1 notes (what exists, what does not)

- File: `PostQaScreen.kt`. Open it from Poster inbox, button "Template QA (check every template)". Back returns to the inbox. Route name in `PostApp.kt`: `qa`.
- Done: every template at every size (36 templates, 4 sizes) with three text modes: Sample (the template's own words), Stress (every box 1.5 times longer than its sample) and Amharic (fixed Amharic words). The "Check all" button lists every box that had to shrink, with a Copy report button. Cards below show each template at the chosen size with its shrink result.
- Not done: the contrast check and the minimum text size check named above. The minimum text size check moved to Q2 and now exists (see Q2 notes). Contrast: the renderer's contrast guard already moves text below about 4:1 to a readable color, so a separate check only matters if we want to know which templates rely on the guard. Not planned yet.
- Not done: "every box at its kit limit". Stress (1.5 times the sample) stands in until the limits are generated from one table (Q4). Do not retype limits.
- Amharic mode is only exact once an Amharic font is installed (Q7).
- First thing to do after a build: open the screen, run Check all in Stress mode, copy the report, and use it to fix templates (Q2) and to correct kit `TEMPLATES.md` limits (Q3 and Q5).

### Q2 notes (shared design rules)

- File: `PostDesignRules.kt`. Holds the minimum text sizes in pixels at 1080: message and list 34, small label and button 26, headline 56, plus the renderer's shrink floor (0.6). `smallText(spec, size)` reads a template's layout and lists boxes whose own size (`box.size` times the shorter side) is under the rule. These numbers are starting estimates. Change them in this one file after looking at the report.
- Template QA has a new button "Check small text (instant, all templates)" with a Copy report button, and each template card shows its own small-text line.
- Not done: templates do not read the shared values yet (they keep their own numbers). Part 2: use the small-text report to raise the flagged sizes, one family per push in the order Information, Marketing, Type, photo and split. Closes `DESIGN_PLAN.md` D2.
- Not done: shared safe margins and logo margin (`DESIGN_PLAN.md` D3 needs Hiyab's decision first).
- Test: build, Template QA, "Check small text", copy the report, send it to the assistant.

### Q5 notes (what changed in the kit)

- `AGENT_START.md` now has two routes. Route A (chat link on, meaning the chat has `wt-mcp` tools or the Supabase connector): read `wt_post.post_log` and recent batches first, pick the template from `TEMPLATES.md`, write every box inside its limits, save one row in `wt_post.batches` with status `draft`, say the batch title. Route B (no link): the old screenshot and paste block, kept as a fallback.
- The database is the one post log on Route A. `POST_LOG.md` stays as the fallback. Open question for Hiyab: retire `POST_LOG.md` or keep it as an export.
- Backend safety rules are written into the kit: only `wt_post`, never `public`, no keys written down, no migrations from a content chat, never claim a save that did not happen.
- A pasted Template QA report counts as a "Text shrunk" report: the agent proposes tighter limits for `TEMPLATES.md`.
- Not done: `TEMPLATES.md` section 0 and the top of `README.md` still say "screenshot first". `AGENT_START.md` says it wins when they disagree. Tidy them in a later pass.
- Not tested: a chat actually saving a batch with this text. Test: new chat, "read the brand kit, write 3 Definition posts for Grade 10 Biology", check that the batch appears in Poster inbox.

### Q7 notes (language)

- Hiyab's decision (2 Nov 2026): English only, Amharic posters, hybrid posters, Amharic or hybrid captions are all used. He chooses in each prompt. Nothing said means English.
- Kit: new `LANGUAGE.md` (modes, Amharic writing rules, shorter limits: about 70 percent for a box with Ethiopic text and about 60 percent for giant boxes, ordinary `:` in timetable rows, never the Ethiopic colon, English feature names kept, a Language check at the end of each reply). `AGENT_START.md` rule 21 and the `notes` field record the language of each batch.
- The limits are estimates. Correct them from a Template QA report in Amharic mode.
- The renderer already draws Ethiopic letters with the Amharic font in a mixed box (`PostFonts.styled`). With no Amharic font installed it uses the phone's own.
- Not tested: how Amharic posters look on the phone. Test: Template QA, mode Amharic, Check all, copy the report.
