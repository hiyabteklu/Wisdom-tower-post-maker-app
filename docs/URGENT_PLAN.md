# Urgent plan (owner's list, 15 Nov 2026)

The assistant cannot build the app. Everything marked "written" is NOT compiled and NOT tested on the phone. Build first, fix compile errors, then test each item. The owner tests at the end, after the written items are done.
Code comments in `PostInboxActions.kt`, `PostChatInboxScreen.kt` and `PostBatchSync.kt` point here.

## Status at a glance

| # | Item | Status |
|---|------|--------|
| 1 | Delete single or batch drafts, and their Supabase copy | WRITTEN (single delete, multi-select, and removing one post inside a batch; none compiled); "also delete the log" needs the owner's decision |
| 2 | Mark as posted with platform selector, in-app notification | PARTLY: picker used in inbox AND review screen (written); notification NOT done |
| 3 | Clear edit and save UI, edits synced to Supabase | PARTLY: `update_batch` is LIVE on wt-mcp (v5); autosave for drafts, confirm for approved, one state line, offline retry (slices 3a and 3b, written, not compiled); words not kept across an app restart, clearer edit flow and keeping the last version NOT done |
| 4 | No hex field, only tappable color circles | Written by an earlier agent; check on the phone |
| 5 | Logo as a thin fading header instead of a corner box | WRITTEN (15 Nov), not compiled: needs a look on the phone |
| 6 | Redesign the other poster templates (bold, creative, photo posters) | NOT DONE |

## 1. Delete drafts (and the Supabase copy)

Written (U1): card menu in the Poster inbox, "Delete draft" on a draft batch: confirm dialog, then `wt-mcp` `delete_batch` (v4). The server refuses approved and posted batches and does not touch `wt_post.post_log`.
Files: `PostInboxActions.kt`, `PostChatInboxScreen.kt`, `PostChat.kt` (`PostChatApi.deleteBatch`).

Written (15 Nov, not compiled): multi-select in `PostChatInboxScreen.kt`. Long-press a DRAFT card to start selecting, tap more drafts to add or remove, "select all drafts in this list" and the bin sit in the top bar, one confirm, then one `delete_batch` call per draft. It stops at the first error and leaves the undeleted ones selected. Only drafts can be selected. Close icon or Back leaves selecting.

Server, LIVE (15 Nov 2026, `wt-mcp` v6, owner approved "all is to do"): tool `remove_post` with `id`, `index` (0-based) and optional `expected_count`. It removes ONE post from a DRAFT or APPROVED batch, and the caption at the same position when captions line up. One atomic statement; refuses a published batch, an index out of range, a batch with only one post left (use `delete_batch` for a draft), and a stale `expected_count` (the phone's post count no longer matches the cloud). It never touches status, folder or the post log. Nothing else in `wt-mcp` changed from v5.

Written (15 Nov, not compiled): removing one post in the app. `PostBatchSync.removePost` (the wt-mcp call), `PostBatchSync.shiftLocalAfterRemove` (moves the captions in `wt_post_captions` and the page designs in `wt_post_designs` down by one for the posts above the removed one) and the composable `BatchRemovePostBar`, shown under the save bar in the review step of `PostBatchScreen.kt`: "Remove post N from this batch", confirm dialog, then the screen closes (`onBack`) so the batch is reopened from the cloud copy. Hidden for a posted batch; disabled when the batch has one post, or when the phone and the cloud disagree on the post count.

To do:
- [ ] Decide with the owner what "also delete the log" means. Today the post log is kept on purpose so posted history is never lost. Option: deleting a DRAFT removes its log rows too (there should be none), deleting a POSTED batch asks a second, stronger confirm and removes the log rows as well. Do not change this without the owner's yes.
- [ ] Check on the phone after building: after removing a post, the Poster inbox must show the new post count when it comes back (it may show the cached list until it refreshes: if so, refresh the inbox list on return in `PostApp.kt`/`PostChatInboxScreen.kt`). Unsaved edits of other posts are lost when the screen closes (drafts save 2 s after the last edit); check that the captions and designs of the posts above the removed one still match their posters.

## 2. Mark as posted

Written (U1): "Mark posted" asks for every platform used (Telegram, TikTok, LinkedIn, Instagram, Facebook), sets the batch to `published`, then saves one `wt_post.post_log` row per post and platform. The review screen (`PostBatchScreen.kt`) now uses the same picker and `markBatchPosted` (written 15 Nov, not compiled).

To do:
- [ ] In-app notification: a reminder when a planned or approved batch has not been marked posted (dot on the Inbox icon already exists for drafts and now also lights for approved batches not marked posted, `PostInboxState.countsFrom`; add a real notification if the owner wants one).
- [ ] Review screen: the old chips and the old "Mark published" button are gone; check on the phone that nothing of them is left.

## 3. Edit and save, synced to Supabase

Backend: `wt-mcp` v5 added `update_batch` (still live inside v6). It replaces the posts and/or captions and/or title of a DRAFT or APPROVED batch, refuses a PUBLISHED batch, and never touches status, folder or the post log.

Written (slices 3a and 3b, 15 Nov, NOT compiled): `PostBatchSync.kt` holds `updateBatch` (the wt-mcp call, in its own file so `PostChat.kt` with the built-in key was not rewritten), `mergePosts` (edited words laid over the cloud posts, every other field kept; the app's `body` box is `message` on the server) and the composable `BatchSaveBar`. `PostBatchScreen.kt` shows the bar under the caption box in the review step.
- One clear state line: "Saved: same as the cloud copy", "Unsaved changes", "Saving...", "Waiting for internet", or the server's error.
- DRAFT batches save themselves about 2 seconds after the last edit; the state line shows it. "Save now" saves at once.
- Offline: the unsent edits are retried every 15 seconds while the batch is open. A server error (for example a rejected post) is NOT retried; press Save now after fixing it.
- APPROVED (planned) batches never save by themselves: Save now asks first. A published batch shows "cannot be edited online".
- Captions typed on the phone are also kept in the `wt_post_captions` preferences, so after a restart they are found again and sent when the batch is reopened.
- Words are only written when the phone and the cloud agree on the number of posts; otherwise only captions are saved and a line says so.

To do:
- [ ] Words edited in the editor are NOT kept across an app restart (only designs and captions are). If the owner edits words offline and closes the app, they are lost. Fix: keep the edited words per batch in preferences and reload them into the review items.
- [ ] Possible bug to check: in Full blocks mode a `---` line splits a post. A Chapter summaries list item that is only `---` (blank line) would then split a sheet in two on the phone, so the post count differs and words are not synced for that batch (captions still save). Check on a real study batch; if it happens, parse inbox batches from the JSON posts instead of from text.
- [ ] One clear editing flow: tap a sheet, edit, "Done" saves that sheet; "Apply design to all" is a separate, clearly named button. Designs are still phone-only (`PostDesignStore`); only words and captions sync.
- [ ] Keep the last version before overwriting an approved batch (needs a small wt-mcp change or a column; tell the owner before deploying, wt-mcp is shared and live).
- [ ] Test (owner, at the end): edit a caption, wait, reopen the batch from the inbox, check it came back from Supabase. Same for a word edited in the editor. Approved batch (dialog), airplane mode ("Waiting for internet", then back online).

## 4. Color circles instead of hex code

Replaced by an earlier agent with tappable swatches (shown in the commit history). Check on the phone: no hex text field anywhere (editor tabs, batch Design panel), circles show the real color, tap changes it. Check icon colors too.

## 5. Logo placement: thin fading header

Written in `PostRenderer.drawLogo`: a slim bar along the top edge. It starts as a hairline at the top-left, grows smoothly to the logo's height near the right, and is transparent at the left edge (fades in toward the logo). A thin line along its lower edge fades the same way. The logo sits at the right end, white on the dark part. On tall 9:16 posts the logo is lowered to 8.5% of the height (Instagram and TikTok top buttons) and the bar reaches down to it. Study sheets use a smaller bar about one ruled line high.

To check on the phone, then adjust:
- [ ] Does it look right on a light poster, a dark poster, a photo poster and a study sheet? Does it cover any eyebrow or headline text near the top? (Text is drawn before the header, so the header can cover it. If it does, lower the safe area at the top or shrink the bar.)
- [ ] Brand screen and editor still say "Left / Center / Right" for the logo corner and "S / M / L" for size: size works with the new bar, the corner setting is ignored. Remove or repurpose it.
- [ ] Optional: a "Header style" choice (fading bar, or the old corner box) in the Brand screen, if the owner wants both.

## 6. Redesign the other poster templates

Now: mostly flat gradients and text boxes, so the templates look the same. The photo posters are weak.

To do (one family at a time, each reviewed on the phone before the next):
- [ ] Look at all current templates side by side (Template QA screen) and list which ones to keep.
- [ ] Typography-led posters: one giant letter, one oversized word, extra-bold weights, stacked words, outlined or cut-out letters, a stylized first letter used as a graphic.
- [ ] Photo posters: big cut-out subject over huge type, duotone or color-block photo, photo in a bold shape (arch, circle, torn edge), text overlapping the photo edge, caption chip.
- [ ] Different structures, not only different colors: split screens, bold borders, stickers, number badges, ticket and label shapes.
- [ ] Every template must work with real batch text of any length (no sample words, see below).

## Fixed on 14 to 15 Nov (written, not compiled)

- Inbox batch pages no longer show the template's SAMPLE words (the old "SQ3R" and five-step line on the EMF sheets). `PostBatchScreen.pageDoc`.
- Powers and indices inside `\sqrt{...}` (A_y², A_z²) now draw as real sub and superscripts. `PostStudyInline.appendMath`.
- Logo header (item 5), see above.
- Known, not fixed: the study sheets draw all text in capitals (the chosen font), so `a_x` and `A_x` look the same. Pick a font with lowercase letters in the Design panel, or add a lowercase option.

## Suggested order

1. Build and fix compile errors (everything above is untested).
2. Finish the written items (item 2 notification, item 3 leftovers, item 6 templates).
3. Test items 1 to 5 on the phone, then item 6 one family at a time.
