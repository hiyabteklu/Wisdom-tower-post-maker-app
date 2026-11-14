# Urgent plan

Status: U1 CODE WRITTEN, NOT COMPILED (14 Nov 2026). Code comments in `PostInboxActions.kt` and `PostChatInboxScreen.kt` point here.
The assistant cannot build the app. Build and test before anything depends on this. Expect small compile errors (a missing import, a renamed helper); fix those first.

## U1: Poster inbox actions

Card menu (three dots) on every batch card in the Poster inbox:

- **Mark posted** (draft or approved batch): a dialog asks for every platform used (Telegram, TikTok, LinkedIn, Instagram, Facebook). The batch status becomes `published` in Supabase, then one `wt_post.post_log` row is saved per post and platform (`PostChatApi.logPost`). The status is set first; if the log stops half way, the message says how many rows were saved and the inbox reloads.
- **Delete draft** (draft batch only): confirm dialog, then `wt-mcp` `delete_batch` (v4). The server refuses approved and posted batches and never touches the post log.
- **Remove from inbox** (posted batch only): hidden on this phone only (SharedPreferences `wt_post_inbox_hidden`). The batch and its post log rows stay in Supabase.

Files: `PostInboxActions.kt` (picker dialog, confirm dialog, `markBatchPosted`, `PostInboxHidden`), `PostChatInboxScreen.kt` (menu and wiring), `PostChat.kt` (`PostChatApi.deleteBatch`).

Progress:

- [x] wt-mcp v4 with `delete_batch` deployed (per the previous session; not re-checked)
- [x] App code pushed, not compiled
- [ ] Build, fix compile errors
- [ ] Test on the phone: Mark posted with one and with several platforms (check `wt_post.post_log`), Delete draft (draft disappears here and in Supabase; an approved batch is refused), Remove from inbox (posted batch disappears and the post log stays), kill and reopen the app (hidden batches stay hidden)
