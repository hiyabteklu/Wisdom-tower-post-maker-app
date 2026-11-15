# Urgent plan (owner's list, 15 Nov 2026)

The assistant cannot build the app. Everything marked "written" is NOT compiled and NOT tested on the phone. Build first, fix compile errors, then test each item.
Code comments in `PostInboxActions.kt`, `PostChatInboxScreen.kt` and `PostBatchSync.kt` point here.

## Status at a glance

| # | Item | Status |
|---|------|--------|
| 1 | Delete single or batch drafts, and their Supabase copy | PARTLY: single draft delete written; multi-select NOT done |
| 2 | Mark as posted with platform selector, in-app notification | PARTLY: picker used in inbox AND review screen (written); notification NOT done |
| 3 | Clear edit and save UI, edits synced to Supabase | PARTLY: `update_batch` is LIVE on wt-mcp (v5); Save button and state line written (slice 3a, not compiled); offline queue and clearer edit flow NOT done |
| 4 | No hex field, only tappable color circles | Written by an earlier agent; check on the phone |
| 5 | Logo as a thin fading header instead of a corner box | WRITTEN (15 Nov), not compiled: needs a look on the phone |
| 6 | Redesign the other poster templates (bold, creative, photo posters) | NOT DONE |

## 1. Delete drafts (and the Supabase copy)

Written (U1): card menu in the Poster inbox, "Delete draft" on a draft batch: confirm dialog, then `wt-mcp` `delete_batch` (v4). The server refuses approved and posted batches and does not touch `wt_post.post_log`.
Files: `PostInboxActions.kt`, `PostChatInboxScreen.kt`, `PostChat.kt` (`PostChatApi.deleteBatch`).

To do:
- [ ] Multi-select in the inbox: long-press a card, tick several, one "Delete selected" button (drafts only, one confirm, one call per batch).
- [ ] Decide with the owner what "also delete the log" means. Today the post log is kept on purpose so posted history is never lost. Option: deleting a DRAFT removes its log rows too (there should be none), deleting a POSTED batch asks a second, stronger confirm and removes the log rows as well. Do not change this without the owner's yes.
- [ ] Deleting one post (sheet) inside a batch is not built.

## 2. Mark as posted

Written (U1): "Mark posted" asks for every platform used (Telegram, TikTok, LinkedIn, Instagram, Facebook), sets the batch to `published`, then saves one `wt_post.post_log` row per post and platform. The review screen (`PostBatchScreen.kt`) now uses the same picker and `markBatchPosted` (written 15 Nov, not compiled).

To do:
- [ ] In-app notification: a reminder when a planned or approved batch has not been marked posted (dot on the Inbox icon already exists for drafts; extend it, or add a notification).
- [ ] Review screen: the old chips and the old "Mark published" button are gone; check on the phone that nothing of them is left.

## 3. Edit and save, synced to Supabase

Backend: `wt-mcp` v5 is deployed and live (verified 15 Nov 2026, version 5, `update_batch` present). It replaces the posts and/or captions and/or title of a DRAFT or APPROVED batch, refuses a PUBLISHED batch, and never touches status, folder or the post log. It was deployed by an earlier agent; the owner should know it is live.

Written (slice 3a, 15 Nov, NOT compiled): `PostBatchSync.kt` holds `updateBatch` (the wt-mcp call, in its own file so `PostChat.kt` with the built-in key was not rewritten), `mergePosts` (edited words laid over the cloud posts, every other field kept; the app's `body` box is `message` on the server) and the composable `BatchSaveBar`. `PostBatchScreen.kt` shows the bar under the caption box in the review step.
- One clear state line: "Saved: same as the cloud copy", "Unsaved changes", "Saving...", "Waiting for internet", or the server's error.
- Nothing is sent until "Save changes to cloud" is pressed. Captions are still also kept on the phone while typing (`wt_post_captions` preferences), as before.
- An approved batch asks before it is overwritten. A published batch shows "cannot be edited online".
- Words are only written when the phone and the cloud agree on the number of posts; otherwise only captions are saved and a line says so.

To do:
- [ ] Build, then test: edit a caption, Save, reopen the batch from the inbox, check the caption came back from Supabase. Same for a word edited in the editor. Test an approved batch (dialog) and airplane mode ("Waiting for internet").
- [ ] Possible bug to check: in Full blocks mode a `---` line splits a post. A Chapter summaries list item that is only `---` (blank line) would then split a sheet in two on the phone, so the post count differs and words are not synced for that batch (captions still save). Check on a real study batch; if it happens, parse inbox batches from the JSON posts instead of from text.
- [ ] Offline queue: remember unsent edits on the phone and send them later automatically (today the owner presses Save again when online).
- [ ] One clear editing flow: tap a sheet, edit, "Done" saves that sheet; "Apply design to all" is a separate, clearly named button. Designs are still phone-only (`PostDesignStore`); only words and captions sync.
- [ ] Keep the last version before overwriting an approved batch (needs a small wt-mcp change or a column; tell the owner before deploying, wt-mcp is shared and live).

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
2. Test items 1, 2, 4 and 5 on the phone.
3. Test item 3 slice 3a (Save to cloud), then the offline queue and the clearer edit flow.
4. Item 6 (templates), one family per round.
