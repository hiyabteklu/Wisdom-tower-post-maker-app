# Batch: review step (B6)

Written 1 Nov 2026 by Claude, on Hiyab's request. Not compiled, please build.

## What Hiyab asked for

Batch used to save every post the moment 'Make posts' was pressed. Now:

1. Write the list (step 1), press 'Preview N posts'.
2. Review (step 2): a horizontal swipe through a preview of every post. Each page has its own 'Edit post N' button.
3. Only then press 'Save to gallery'. Nothing is written to the gallery before that.

## What changed

- Only `PostBatchScreen.kt` changed. `PostBatch.kt`, `PostExport.saveOne`, templates, renderer and `PostCodec` are untouched.
- Step 1 keeps: photos, mode, Fill box, text box, Paste, Example, Clear. The save choices (sizes, PNG or JPG, Standard or 4K) moved to step 2, next to the Save button.
- Step 2 uses `HorizontalPager` (foundation, already in the Compose BOM). Neighbour posts peek at the sides. Previous and next arrows plus 'Post 3 of 12' sit under the pager.
- 'Edit post N' opens a dialog with one field per box of the template (same boxes as the Words section). Save changes updates only that post. A mint 'Edited' tag shows on edited posts. The gold 'Text shrunk to fit' chip shows on a page when its words do not fit.
- Edits live in `reviewItems` (a state list of `BatchItem`). Going back to step 1 and pressing Preview again rebuilds the posts from the list and drops the edits. System Back from step 2 goes back to step 1.
- 'Check text fit on all posts' moved to step 2 and checks the edited words.
- Save runs the same export as before over `reviewItems`, one bitmap at a time, with Cancel. After saving: Share (30 files or fewer), Done, and 'Save again to a new folder'.
- Photos picked in step 1 are no longer deleted when a save ends (so a second save still has them). They are deleted when the screen closes, as before.

## Phone test

1. Paste example list, press Preview. Swipe through all posts; each shows its own words.
2. Edit post 2: change the headline, Save changes. The page shows 'Edited' and the new words. Other posts are unchanged.
3. Nothing appears in the gallery yet.
4. Press Save to gallery. Progress runs, files appear in Pictures/Wisdom Tower Post/Batch ..., the edited post has the edited words.
5. Back from step 2 returns to the list with your text intact.
6. A very long headline: the gold chip shows on that page, and 'Check text fit on all posts' names it.
