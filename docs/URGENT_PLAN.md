# Urgent plan (owner's list, 15 Nov 2026)

The assistant cannot build the app. Everything marked "written" is NOT compiled and NOT tested on the phone. Build first, fix compile errors, then test each item. The owner tests at the end, after the written items are done.
The owner leaves design decisions to the assistant ("you are the designer here, do what is best"); decisions taken are listed under "Decisions" below.
Code comments in `PostInboxActions.kt`, `PostChatInboxScreen.kt`, `PostBatchSync.kt` and `PostWordsStore.kt` point here.
LESSON (15 Nov): an earlier agent's note said item 4 (color circles) was done. It was NOT: the hex code box was still in the app. Never mark an item done from a note; open the code and check it.

## Status at a glance

| # | Item | Status |
|---|------|--------|
| 1 | Delete single or batch drafts, and their Supabase copy | WRITTEN, none compiled: bin button on every draft card, "Clean up" button in the inbox top bar, multi-select, card menu, and "Remove post N from this batch" in the review screen. Log question DECIDED, see Decisions |
| 2 | Mark as posted with platform selector, in-app notification | PARTLY: picker used in inbox AND review screen (written); the inbox dot also lights for approved-not-posted batches; a real notification is NOT done (not needed for now, see Decisions) |
| 3 | Clear edit and save UI, edits synced to Supabase | PARTLY: `update_batch` LIVE (wt-mcp v5); autosave for drafts, confirm for approved, one state line, offline retry (slices 3a, 3b); words now kept across an app restart (`PostWordsStore.kt`, written); clearer edit flow and keeping the last version NOT done |
| 4 | No hex field, only tappable color circles | WRITTEN 15 Nov, not compiled: `PostHexField` (PostControls.kt) now shows color circles and `WordBox` (PostTextStyle.kt) word-ink color is circles. Check the phone for any other hex box |
| 5 | Logo header | REVERTED (15 Nov): the thin fading bar looked bad on the phone, the original corner box is back. Not compiled |
| 6 | Redesign the other poster templates (bold, creative, photo posters) | WRITTEN 15 Nov, not compiled: full `docs/TEMPLATE_REDESIGN_PLAN.md` built in one run (engine `FitMode`/`VAnchor`/`TextShadow`/`TextFx`/`PhotoFrame`, floating HUD, background photo dim/blur/panels, `letter` & `word` derivation, 33 new designs + 6 kept across 5 families, clones removed & mapped in `LegacyTemplateIds`, `PostQaScreen` upgraded) |

## Decisions (taken by the assistant as designer, 15 Nov 2026, owner delegated)

- Post log: it is NEVER deleted by the app or by wt-mcp. A draft has no log rows, and a posted batch cannot be deleted from the app, so "also delete the log" is closed: posted history stays safe. If the owner ever wants a posted batch removed, do it by hand in Supabase.
- Notification for "not marked posted": the inbox dot (drafts to review plus approved batches not posted) is enough for now. Add a real Android notification only if the owner asks, because it needs a notification permission and a background job.
- Removing one post inside a batch is allowed only for DRAFT and APPROVED batches and never for the last post.
- Logo header: the owner rejected the thin fading bar (it looked like a swoosh cutting across the poster and the logo had no clear box). The rule now: do NOT change the logo header design again without the owner's explicit yes; small refinements only (size, padding, corner radius), never a new shape.
- Color controls: never a hex code text box anywhere in the app; always tappable circles.
- Template redesign (15 Nov, owner): one run by Google AI Studio for the whole plan instead of one family per push; the owner and Claude review quality afterwards. The Chapter summaries (`study-*`) are NOT part of it.

## 1. Delete drafts (and the Supabase copy)

Written (U1): card menu in the Poster inbox, "Delete draft" on a draft batch: confirm dialog, then `wt-mcp` `delete_batch` (v4). The server refuses approved and posted batches and does not touch `wt_post.post_log`.
Files: `PostInboxActions.kt`, `PostChatInboxScreen.kt`, `PostChat.kt` (`PostChatApi.deleteBatch`).

Written (15 Nov, not compiled): multi-select in `PostChatInboxScreen.kt`. Long-press a DRAFT card to start selecting, tap more drafts to add or remove, "select all drafts in this list" and the bin sit in the top bar, one confirm, then one `delete_batch` call per draft. It stops at the first error and leaves the undeleted ones selected. Only drafts can be selected. Close icon or Back leaves selecting.

Written (15 Nov, after the owner could not find any delete or clean-up button, not compiled): everything is now visible without a menu.
- A bin button on every DRAFT card (confirm, then delete in Supabase too).
- A "Clean up" button (broom-bin icon) always in the inbox top bar: "Select all N drafts to delete" (then the bin in the top bar, one confirm) and "Hide all N posted batches" (phone only; Supabase and the post log are not touched).

Server, LIVE (15 Nov 2026, `wt-mcp` v6): tool `remove_post` with `id`, `index` (0-based) and optional `expected_count`. It removes ONE post from a DRAFT or APPROVED batch, and the caption at the same position when captions line up. One atomic statement; refuses a published batch, an index out of range, a batch with only one post left (use `delete_batch` for a draft), and a stale `expected_count` (the phone's post count no longer matches the cloud). It never touches status, folder or the post log. Nothing else in `wt-mcp` changed from v5.

Written (15 Nov, not compiled): removing one post in the app. `PostBatchSync.removePost`, `PostBatchSync.shiftLocalAfterRemove` (moves the captions in `wt_post_captions` and the page designs in `wt_post_designs` down by one for the posts above the removed one) and the composable `BatchRemovePostBar`, shown under the save bar in the review step of `PostBatchScreen.kt` (scroll down in the review screen): "Remove post N from this batch", confirm dialog, then the screen closes (`onBack`) so the batch is reopened from the cloud copy. Hidden for a posted batch; disabled when the batch has one post, or when the phone and the cloud disagree on the post count. Kept words (`wt_post_words`) need no shifting: the removed post changes the cloud word signature, so the old entry is ignored and dropped.

To do:
- [ ] Check on the phone after building: after removing a post, the Poster inbox must show the new post count when it comes back (it may show the cached list until it refreshes: if so, refresh the inbox list on return in `PostApp.kt`/`PostChatInboxScreen.kt`). Unsaved edits of other posts are lost when the screen closes (drafts save 2 s after the last edit); check that the captions and designs of the posts above the removed one still match their posters.
- [ ] If the owner wants the remove-post button higher up (near the poster instead of below the save bar), move `BatchRemovePostBar` in `PostBatchScreen.kt`.

## 2. Mark as posted

Written (U1): "Mark posted" asks for every platform used (Telegram, TikTok, LinkedIn, Instagram, Facebook), sets the batch to `published`, then saves one `wt_post.post_log` row per post and platform. The review screen (`PostBatchScreen.kt`) now uses the same picker and `markBatchPosted` (written 15 Nov, not compiled). The inbox dot also lights for approved batches not marked posted (`PostInboxState.countsFrom`).

To do:
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

Written (15 Nov, NOT compiled): edited words survive an app restart. `PostWordsStore.kt` keeps them per batch in the preferences file `wt_post_words` (the edited words plus a signature of the cloud words they were made on). `PostBatchScreen.startReview` lays them back over the cloud words when the batch is opened; they then count as unsaved changes, so the save bar sends them (a draft saves itself 2 s later). An entry is dropped when the edited words equal the cloud words again, or when the cloud words changed since (so old words never overwrite newer ones).

To do:
- [ ] Possible bug to check: in Full blocks mode a `---` line splits a post. A Chapter summaries list item that is only `---` (blank line) would then split a sheet in two on the phone, so the post count differs and words are not synced for that batch (captions still save). Check on a real study batch; if it happens, parse inbox batches from the JSON posts instead of from text.
- [ ] One clear editing flow: tap a sheet, edit, "Done" saves that sheet; "Apply design to all" is a separate, clearly named button. Designs are still phone-only (`PostDesignStore`); only words and captions sync.
- [ ] Keep the last version before overwriting an approved batch (needs a small wt-mcp change or a column; tell the owner before deploying, wt-mcp is shared and live).
- [ ] Test (owner, at the end): edit a caption, wait, reopen the batch from the inbox, check it came back from Supabase. Same for a word edited in the editor, and for a word edited offline and then the app closed and reopened (the word must come back and then reach the cloud). Approved batch (dialog), airplane mode ("Waiting for internet", then back online).

## 4. Color circles instead of hex code

NOT done before 15 Nov despite an earlier note (see LESSON at the top). Written 15 Nov, not compiled:
- `PostControls.kt`: `PostHexField` keeps its name and parameters but now draws a scrolling row of tappable color circles (the current color first and selected, then 19 standard colors). Every screen that used the hex box (editor tabs, Brand screen, batch Design panel, backgrounds) gets circles at once.
- `PostTextStyle.kt`: the "Word ink HEX" box is replaced by circles (brand colors plus 14 ink colors); tapping a circle colors the selected word at once.

To do:
- [ ] On the phone, look through every tab and the Brand screen for any remaining hex text box. A custom color the owner needs that is not in the circles: add it to `PickerColors` in `PostControls.kt` or to the Brand colors.

## 5. Logo header: REVERTED to the corner box (15 Nov 2026)

The thin fading bar (commit f9355b73) was rejected by the owner after seeing a real export: it looked like a swoosh across the poster. `PostRenderer.drawLogo` is back to the version from before that commit: a dark brand-gradient box hanging from the top-right corner with the white logo inside it, flush to the top on 1:1, 4:5 and 4:3, lowered to 8.5% of the height on tall 9:16 posts (so Instagram and TikTok buttons do not cover it), a small box one ruled line high on study sheets. Everything else in `PostRenderer.kt` is unchanged.

To do:
- [ ] Look at it on the phone (light, dark and photo posters, 9:16 and 1:1, a study sheet). Only small refinements are allowed (size, padding, corner radius); see Decisions.
- [ ] Brand screen and editor "Left / Center / Right" corner setting and "S / M / L" size: check they still do what their labels say with the box (the corner setting was always ignored by the box, which sits at the right).

## 6. Redesign the other poster templates

PLAN WRITTEN (15 Nov 2026): **`docs/TEMPLATE_REDESIGN_PLAN.md`**. It was written from the real template and renderer code and the owner's requirements: background photo with opacity on every template, a text shadow slider/HUD, a size control that really works (automatic fill for short and long text, exact manual size, pinch), a letter in its own giant box and a giant word, advanced typography effects (outline, gradient, photo-filled letters, echo, extrude, justified stacks, chips, tiles, tickers), 33 new unique designs in five families (Letter, Type, Photo, Information, Marketing), clones removed, old ids mapped. The Chapter summaries are not part of it.

Owner decision: Google AI Studio builds the **whole plan in one run** (no slicing, no stopping after a family); quality is reviewed afterwards by the owner and Claude. The older per-family direction list that stood here is replaced by that plan.

Status:
- [x] Plan written.
- [x] Code: WRITTEN (not compiled) in one run across `PostImageCache.kt`, `PostModel.kt`, `PostTemplates.kt`, `PostEngine.kt`, `PostRenderer.kt`, `PostTextStyle.kt`, `PostEditorTabs.kt`, `PostStudio.kt`, `PostTemplatesLetter.kt`, `PostTemplatesType.kt`, `PostTemplatesPhoto.kt`, `PostTemplatesInfo.kt`, `PostTemplatesMarketing.kt`, `PostTemplatesDevice.kt`, `PostLibraryScreen.kt`, `PostDrafts.kt`, `PostBatch.kt`, `PostBatchScreen.kt`, `PostChat.kt`, `PostBatchSync.kt`, `PostQaScreen.kt` (and removed `PostTemplatesSplit.kt` & `PostTemplatesPhoto2.kt`). Every touched file re-read for compile errors.
- [ ] Build, compile fixes, owner phone test (test matrix in the plan, section 8).
- [ ] Backend rows for the new template ids and Brand Kit docs (plan section 7), after the build.
- [ ] Owner adds the font files (plan section 9).

## Fixed on 14 to 15 Nov (written, not compiled)

- Inbox batch pages no longer show the template's SAMPLE words (the old "SQ3R" and five-step line on the EMF sheets). `PostBatchScreen.pageDoc`.
- Powers and indices inside `\sqrt{...}` (A_y², A_z²) now draw as real sub and superscripts. `PostStudyInline.appendMath`.
- Known, not fixed: the study sheets draw all text in capitals (the chosen font), so `a_x` and `A_x` look the same. Pick a font with lowercase letters in the Design panel, or add a lowercase option.

## Suggested order

1. Build and fix compile errors (everything above is untested).
2. Template redesign: Google AI Studio builds `docs/TEMPLATE_REDESIGN_PLAN.md` in one run, then build and fix; item 3 leftovers.
3. Test items 1 to 5 on the phone, then the template redesign with the plan's test matrix.
