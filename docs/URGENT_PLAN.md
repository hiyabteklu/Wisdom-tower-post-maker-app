# Urgent plan (owner's list, 15 Nov 2026)

The assistant cannot build the app. Everything marked "written" is NOT compiled and NOT tested on the phone. Build first, fix compile errors, then test each item.
Code comments in `PostInboxActions.kt` and `PostChatInboxScreen.kt` point here.

## Status at a glance

| # | Item | Status |
|---|------|--------|
| 1 | Delete single or batch drafts, and their Supabase copy | PARTLY: single draft delete written; multi-select NOT done |
| 2 | Mark as posted with platform selector, in-app notification | PARTLY: platform picker written; notification NOT done |
| 3 | Clear edit and save UI, edits synced to Supabase | NOT DONE |
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

Written (U1): "Mark posted" asks for every platform used (Telegram, TikTok, LinkedIn, Instagram, Facebook), sets the batch to `published`, then saves one `wt_post.post_log` row per post and platform.

To do:
- [ ] In-app notification: a reminder when a planned or approved batch has not been marked posted (dot on the Inbox icon already exists for drafts; extend it, or add a notification).
- [ ] The review screen (`PostBatchScreen.kt`) still has its own chips (Telegram, Instagram, Facebook, TikTok, no LinkedIn) and its own "Mark published" button that writes NO post log rows. Make it use the shared picker and `markBatchPosted`.

## 3. Edit and save, synced to Supabase

Today edits live only on the phone: designs in `PostDesignStore`, captions in the `wt_post_captions` preferences. The review screen autosaves silently, so it is unclear what is saved. The `wt-mcp` function has no tool to update the words or captions of a batch.

To do:
- [ ] New `wt-mcp` tool `update_batch` (posts, captions, title) and call it from the app. Tell the owner before deploying; wt-mcp is shared and live.
- [ ] Inbox draft: every edit saves to Supabase when online, and is queued on the phone and sent later when offline. Show one clear state on the batch: "Saved", "Saving...", "Waiting for internet".
- [ ] One clear editing flow: tap a sheet, edit, "Done" saves that sheet; "Apply design to all" is a separate, clearly named button. No hidden autosave.
- [ ] Planned (approved) posts must not change by accident: ask before saving over an approved batch, and keep the last version.

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
3. Item 3 (sync) next: it protects the planned posts.
4. Item 6 (templates), one family per round.
