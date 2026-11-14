# Rules for Claude (and any other assistant) working in this repo

1. **Work on `main` only.** Commit straight to `main`. Do not create branches or merge requests unless Hiyab asks for one in that same message.
2. Before editing any file, read the latest copy from `main`, because `main` may have moved since the last session.
3. If a change was made on another branch earlier, bring it onto `main` (ask first if it would replace newer work on `main`) instead of continuing on that branch.
4. The assistant cannot compile or run the app. Say so after each change so it gets built and tested before anything depends on it.
5. **Where to continue:** read `docs/APP_QUALITY_ROADMAP.md` first (whole-system plan, slices Q1 to Q7), then `docs/DESIGN_PLAN.md` (single-template fixes). `docs/CHAT_CONTROL_PLAN.md` explains how posters travel from chat to the phone inbox. For the Chapter summaries (handwritten dense notes) read `docs/CHAPTER_SUMMARIES_PLAN.md` (the spec) and then `docs/CHAPTER_SUMMARIES_PROGRESS.md` (what is done and what is next).


## Content-agent integration (mandatory)

6. Before any content generation or integration change, read `docs/AI_CONTENT_DELIVERY.md` and the linked Brand Kit `rules/AGENT_START.md` in full (the Brand Kit has three content folders: `facts/`, `rules/`, `posts/`). The Brand Kit is the single source of truth for all post content and original Amharic/hybrid writing. This app is the authority for actual visual templates and rendering. Agents must select a supported template, save structured slots plus one caption per post to the existing Supabase `wt_post.batches` flow, and **must not return poster/caption/batch title or a success message in chat after saving**. The owner receives the work in Poster inbox.
7. Never touch the shared Supabase project's `public` schema for this integration. Use only `wt_post` and the existing `wt-mcp` function. Never disclose credentials.
