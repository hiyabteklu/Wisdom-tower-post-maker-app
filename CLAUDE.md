# Rules for Claude (and any other assistant) working in this repo

1. **Work on `main` only.** Commit straight to `main`. Do not create branches or merge requests unless Hiyab asks for one in that same message.
2. Before editing any file, read the latest copy from `main`, because `main` may have moved since the last session.
3. If a change was made on another branch earlier, bring it onto `main` (ask first if it would replace newer work on `main`) instead of continuing on that branch.
4. The assistant cannot compile or run the app. Say so after each change so it gets built and tested before anything depends on it.
5. **Where to continue:** read `docs/DESIGN_PLAN.md` for the current status and the next slices (D1 onward). `docs/CHAT_CONTROL_PLAN.md` explains how posters travel from chat to the phone inbox.
