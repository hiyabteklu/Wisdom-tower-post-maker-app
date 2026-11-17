# AI content delivery: Brand Kit → Supabase → Poster Inbox

This is the current operating contract for every AI agent/automation creating Wisdom Tower posts. It supersedes older wording in `CHAT_CONTROL_PLAN.md` that asks the agent to announce the batch title or return content in chat.

## Authorities and verified backend

- **Content authority:** [Wisdom Tower Brand Kit](https://github.com/hiyabteklu/wisdom-tower-brand-kit). Read the entire current repo on `main`, starting at `rules/AGENT_START.md`. The kit has three content folders: `facts/` (verified facts), `rules/` (how to write and format) and `posts/` (ready posts by topic, Amharic, hybrid and English together). Facts, product claims, offers, voice, original Amharic/hybrid writing, anti-AI rules, strategy, post ideas and content-slot limits come from this repo. Do not use shallow summaries, model memory, generic copy or invented facts as substitutes.
- **Template/rendering authority:** this app repo. Brand Kit `rules/TEMPLATES.md` specifies copy shape and limits; `PostTemplates.kt` and its template files define the actual app template registry and renderer. The chosen ID must exist in both. The app renders the poster; agents send structured slot text and captions, not a generated image file.
- **Supabase project:** **Ft agritech**, ref/ID `zfgrgbrrqclugszsotfo`, region `eu-west-1`, URL https://zfgrgbrrqclugszsotfo.supabase.co.
- **Edge Function:** `wt-mcp`, ID `cf54ecaf-9ac4-4492-835d-8c611a31f580`, URL https://zfgrgbrrqclugszsotfo.supabase.co/functions/v1/wt-mcp.
- **Private schema:** `wt_post`; tables `batches`, `post_log`, `template_boxes`. RLS is enabled.

## Required agent workflow

1. Read the full Brand Kit, not just its README. Follow `rules/AGENT_START.md`'s read order. For Amharic/hybrid, read `rules/LANGUAGE.md` and the relevant niche files in `posts/` (index: `posts/README.md`) in full; preserve the original voice and code-switching.
2. Read recent batches and the post log before drafting to avoid repetition.
3. Select the best supported template from Brand Kit `rules/TEMPLATES.md`; verify the exact ID against app `PostTemplates.kt`. The agent chooses; do not ask the owner for an editor screenshot.
4. Write every required slot and one matching caption per post. Apply all brand facts, anti-AI, language, voice, emoji, footer and length rules.
5. Save directly to `wt_post.batches` using `wt-mcp` `save_batch` (or an authorized Supabase connector write if that is the available route), with `status=draft`.
6. **After confirmed save, do not respond in chat.** No copy, caption, batch title, preview, summary or success message. The owner finds the saved work in **Poster inbox** (in its folder) and taps **Make posts**.
7. Never claim success without a confirmed write. If save fails, fix a specific validation error or retry a transient failure; otherwise stop, without replacing the inbox workflow with chat output.

## Batch payload

`wt_post.batches` fields:
- `template`: exact ID in both Brand Kit and app.
- `title`: short searchable title.
- `level`: one level only.
- `pillar`: **one of the 8 exact pillar names from Brand Kit `rules/PLAYBOOK.md`**: Academic Mastery, Study Systems, Commercial Offers, Community & Retention, Student Culture & Memes, Skills & Campus Life, Proof & Celebrations, Official Notices. The pillar decides the inbox folder (see below), so do not invent other pillar names.
- `subfolder`: optional short label inside the folder (for example `Motivation` or `Department guides`). Leave it out and the database uses `level`. Maximum 120 characters.
- `posts`: JSON array of poster objects using the app-to-backend slot keys `letter`, `word`, `eyebrow`, `headline`, `message` (app BODY), `number`, `list` (array of strings), and `button`. Include every required slot for the chosen template and no unsupported slots. Exact current template IDs, required keys and live limits are in [SUPABASE_TEMPLATE_REGISTRY.md](SUPABASE_TEMPLATE_REGISTRY.md).
- `captions`: exactly one non-empty caption per post, same order/index (required by `wt-mcp` v7).
- `status`: `draft` only; `save_batch` cannot approve or publish.
- `notes`: language and any manual action, e.g. photo required before Make posts.

One poster is one batch with one post and one caption (a deck of related posters may be one batch). One batch uses one template. Maximum 50 posts. The backend stores structured text and captions, not the final poster bitmap. `device-hero` has no text slots and uses an empty post object; photo/device assets are selected manually in the app.

## Chapter summaries (handwritten dense notes)

Template ids `study-cover`, `study-list`, `study-define`, `study-checklist`, `study-table`, `study-mnemonic` are the **Chapter summaries** family (spec: `CHAPTER_SUMMARIES_PLAN.md`, status: `CHAPTER_SUMMARIES_PROGRESS.md`). They are delivered exactly like any other batch (same `save_batch`, same rules above), with these differences:

- **One batch, one variant.** A chapter split into sheets that use different variants is several batches, one per variant, all with the same `pillar` (normally Academic Mastery) and the same `subfolder` (for example `Physics Ch 4`) so the sheets sit together in the inbox. Number the sheets in `eyebrow` (`PHYSICS CH 4 · 2/5`).
- **The `list` box carries the dense page.** One array item = one line of the study markup (headings, bullets, numbered points, table rows, Key Point box). The page now has 35 ruled writing spaces (34 when the footer takes the final space), and every wrapped line costs another space. Plan content per sheet to fit; if it does not fit, split it into more sheets. The app never shrinks text: what does not fit is not drawn.
- **How to check the fit.** The review page and the editor preview show a chip on every Chapter summaries sheet: `Lines 30 / 35` when everything fits, or `Does not fit, split this sheet. Left out: "<first row left out>" and N more.` when it does not. After the owner's first review, an agent that is told which row was left out re-splits the chapter at that row. Aim for 29 to 32 of 35 writing spaces so a slightly longer wrap on another phone/font does not push the last row off the page. A table that does not fit is drawn with the rows that fit, and everything after it counts as left out.
- **Markup in `list` items** (the app draws these, so unlike other templates a leading `-`, `★`, `1.` is allowed): `## Topic`, `### Subtopic`, `- bullet`, `★ / ✓ / → text`, `1. numbered`, `> key idea`, `> WARNING text` (red), `>> Label | text` (Key Point box), `Term: explanation`, `|a|b|c|` table rows (first row is the header), `---` (blank line). Inline: `**bold**`, `*italic*`, `==highlight==`, `[[key term]]`, `[[#D11A26;B:word]]`, `$formula$` (shown as clean text, never raw LaTeX).
- **Always color and style the page.** A sheet in one flat ink is a failed sheet. In every item that has a key term, write it as `Term: explanation` (or `[[#HEX;B:Term]] – explanation` for a chosen color); put important words in `**bold**` or `==highlight==`; use `*italic*` for remarks; use `> WARNING ...` for exam traps; use `>> Label | text` for the closing Key Point. The app also styles plain text on its own (a short term before a colon, formulas, sub and superscripts, Greek letters, not / never / always / only in red, maximum / minimum / zero / constant / conserved / proportional / opposite in bold), but explicit marks give better results.
- **Always wrap formulas in `$...$`**, with LaTeX inside: `$v_{max}=A\omega$`, `$a_{max}=\omega^{2}A$`, `$T=2\pi\sqrt{m/k}$`, `$F=-kx$`. Do not type a formula as plain text and do not use Unicode subscripts (`vₘₐₓ`). Greek letters are written as LaTeX commands (`\omega`, `\pi`, `\phi`, `\Delta`, `\lambda`) inside `$...$`. Powers and subscripts at the top level of a formula (`^` and `_`) are drawn as real raised or lowered text.
- **Backend limits for these six** (`wt_post.template_boxes`, changed 14 Nov 2026): `list` takes 1 to 40 items of up to 320 characters each. The bullet/letter/number refusal in `wt_post.check_batch` is skipped for `study-%` templates only; every other template is checked as before. The other boxes keep their old limits (eyebrow 40, headline 60 (study-cover 64), message 120 on study-cover and 220 on study-define, button 60 to 80, number 16 on study-mnemonic). Every box the variant has must still be written.
- **Where the dense content goes per variant:** `study-list`, `study-checklist`, `study-table`, `study-mnemonic` take it in `list`. `study-cover` and `study-define` have no `list` box, so they hold a title and one short paragraph only; use them as the opener of a chapter, not for dense content.
- Brand Kit `rules/TEMPLATES.md` does not describe these six yet. Until it does, this section is the reference for them.

## Inbox folders

The Poster inbox shows folders, not one long list. Folder list first, then the batches of the folder you open, newest first. When a folder holds more than one subfolder, chips filter them. The status chips (All, Draft, Approved, Posted) work on both levels.

- Folders are the 8 pillars, plus three catch-all folders: **Other** (not Wisdom Tower content), **Test samples** (design reviews and tests), **Unsorted** (a pillar name the database does not recognise).
- The database fills `wt_post.batches.folder` from `pillar` on every save (`wt_post.folder_for_pillar`, trigger `batches_folder`) and fills `subfolder` from `level` when it is empty. An agent never has to send `folder`. A batch saved with a pillar the database does not recognise lands in Unsorted: fix the pillar name instead of leaving it there.
- Old pillar names still map to the right folder (Study tips, Study habits, Study plans and Exam tips go to Study Systems; Key terms and Practice to Academic Mastery; Product updates to Commercial Offers; Announcements to Official Notices).
- `wt-mcp` tools: `save_batch` accepts optional `subfolder` (and `folder` from the fixed list). `list_batches` returns `folder` and `subfolder` and accepts a `folder` filter and a limit up to 1000. `list_folders` counts batches and posts per folder, subfolder and status. `set_batch_folder` moves a batch to another folder (and subfolder).
- Folder names live in three places that must stay identical: the `FOLDERS` list in the `wt-mcp` function, the CASE in `wt_post.folder_for_pillar`, and `FOLDER_ORDER` in `PostChatInboxScreen.kt`. They must also match the 8 pillar names in Brand Kit `rules/PLAYBOOK.md` (checked against `FOLDER_ORDER` on 12 Nov 2026: identical).

## Runtime flow

`PostChat.kt` sends POST JSON-RPC requests to `wt-mcp` with `x-wt-key`. The current deployed function is v7; its `save_batch` tool enumerates all 70 current app templates, validates official pillars and requires one caption per post. The function exposes `save_batch`, `list_batches`, `list_folders`, `set_batch_folder`, `get_batch`, `set_batch_status`, `log_post`, and `list_post_log`. `PostChatInboxScreen.kt` loads the list once (up to 1000 batches, no poster text), builds the folders on the phone, and opens a selected batch. The app fetches the post fields and captions, maps slot names, previews using the selected template and hands the post to the existing Make posts/render/export flow. Captions are available beside the rendered post. Status and publishing log actions use the corresponding backend tools. Inbox list can be cached for offline viewing (folders included); backend fetch requires internet.

## Current template registry

The current Android library contains 70 templates across Letter, Type, Photo, Information, Marketing, Chapter summaries, Device mockup and Scene families. The private `wt_post.template_registry` records their display names, families and current/legacy status. `wt_post.template_boxes` contains 250 exact slot definitions for the 69 templates with text; `device-hero` is registered with no text slots. Twenty-seven legacy IDs remain registered for historical compatibility. See [SUPABASE_TEMPLATE_REGISTRY.md](SUPABASE_TEMPLATE_REGISTRY.md) for the exact keys and limits. The separate Brand Kit `rules/TEMPLATES.md` still needs its own editorial catalog refresh for the newest designs; the backend registry does not invent copywriting rules.

## Security

- The Supabase project is shared with FT Agri-Tech. Use only `wt_post` and the existing `wt-mcp` function. Never touch `public` or unrelated schemas, policies, grants or migrations.
- Do not weaken RLS or expose the schema through the Data API.
- Never disclose or commit `WT_MCP_KEY`, app credentials, tokens or passwords.
- `PostChat.kt` currently contains a built-in fallback credential in addition to the editable Chat link setting. Do not reproduce it. This is a hardening issue for a separate security change (rotate server secret and remove the built-in fallback).
- Only poster text, captions and workflow metadata belong in this backend. No learning material or student private data.

## Source links

- [Brand Kit AGENT_START](https://github.com/hiyabteklu/wisdom-tower-brand-kit/blob/main/rules/AGENT_START.md)
- [Brand Kit templates](https://github.com/hiyabteklu/wisdom-tower-brand-kit/blob/main/rules/TEMPLATES.md)
- [Brand Kit playbook (the 8 pillars)](https://github.com/hiyabteklu/wisdom-tower-brand-kit/blob/main/rules/PLAYBOOK.md)
- [Brand Kit posts index (Amharic, hybrid and English by topic)](https://github.com/hiyabteklu/wisdom-tower-brand-kit/blob/main/posts/README.md)
- [Brand Kit language rules](https://github.com/hiyabteklu/wisdom-tower-brand-kit/blob/main/rules/LANGUAGE.md)
- [Post Maker chat API](https://github.com/hiyabteklu/Wisdom-tower-post-maker-app/blob/main/app/src/main/java/com/wisdomtower/post/PostChat.kt)
- [Post Maker Inbox UI](https://github.com/hiyabteklu/Wisdom-tower-post-maker-app/blob/main/app/src/main/java/com/wisdomtower/post/PostChatInboxScreen.kt)
- [Post Maker template registry](https://github.com/hiyabteklu/Wisdom-tower-post-maker-app/blob/main/app/src/main/java/com/wisdomtower/post/PostTemplates.kt)
- [Supabase project dashboard](https://supabase.com/dashboard/project/zfgrgbrrqclugszsotfo)
- [wt-mcp function dashboard](https://supabase.com/dashboard/project/zfgrgbrrqclugszsotfo/functions/wt-mcp/details)

**Success means the batch is saved and visible in Poster inbox, inside its folder. The agent does not reply with content in chat.**


### Minimum teaching-content standard (non-negotiable)

A chapter sheet is a compact lesson, not a keyword list. Do not fill the page with disconnected terms, headings, or fragments. Preserve the source chapter's order and terminology, and make each point teach something.

- Define each central concept in a complete sentence, then explain what it means or how it works.
- State the relationships between ideas: cause/effect, input/output, conditions, assumptions, comparisons, and what changes when a condition changes.
- Include every source-supported law, rule, property, classification, formula, unit, and important exception relevant to the chapter. Explain symbols and variables beside the formula; do not leave an unexplained equation by itself.
- Include a worked mini-example or application when the source provides enough information. Show the reasoning link, not just the final answer.
- Use a warning for common confusions or limiting conditions, and a Key Point box to consolidate the most important conclusion.
- Write full, readable teaching statements. Fragments are acceptable only for labels, table cells, or tightly defined mnemonic cues.
- Fill the available ruled spaces with meaningful explanation, not padding or repeated statements. If the source cannot support 35 useful lines, keep the sheet shorter rather than inventing facts.
- Every sheet must make sense on its own: add a concise topic label and sequence marker, and avoid references such as "as above" unless the preceding sheet is explicitly identified.

A good sheet should let a student revise the concept and understand the logic without needing to decode a pile of keywords. Source accuracy takes priority over visual density.
