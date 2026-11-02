# Wisdom Tower Post: chat control plan

Written 2 Nov 2026 by Claude, for any assistant that continues this work, and for Hiyab. Read `CLAUDE.md` and `BATCH_PLAN.md` first.

## 1. Goal

Hiyab makes posters, one or a batch, by chatting with an AI. The AI writes the posts from the brand kit (`wisdom-tower-brand-kit`), saves them to a small backend, and the Post app pulls them in and renders the images. No copy and paste between the chat and the app.

```
Chat (brand kit rules) --save_batch--> Supabase wt_post --Inbox--> Post app --Make posts--> images
```

## 2. This changes a decision from BATCH_PLAN.md

BATCH_PLAN.md section 1 says: "no MCP server, no Supabase, no network, no accounts" (decided 1 Nov 2026). On 2 Nov 2026 Hiyab decided to add a backend and a network link, so chat can control the app. What stays true:

- The app still works fully offline. The chat link is optional. With no link, no key or no internet, every screen works as it does today.
- No accounts. One shared secret key, entered by Hiyab on the phone.
- No new library (section 5).
- The app does not become a Canva copy. The Inbox only brings words in. Design stays in the editor.

## 3. What already exists (built 2 Nov 2026)

In the Supabase project **Ft agritech** (`zfgrgbrrqclugszsotfo`, region eu-west-1), shared with the FT Agri-Tech app and web, kept apart on purpose:

- Schema `wt_post`, not `public`. Row security on, no access for anon or signed-in roles. The FT app and web cannot see it. Never add anything to `public` for this project.
- Table `wt_post.batches`: `id` uuid, `created_at`, `template` text, `title`, `level`, `pillar`, `posts` jsonb, `captions` jsonb, `status` (`draft`, `approved`, `published`), `notes`.
- Table `wt_post.post_log`: same columns as `POST_LOG.md` (`posted_on`, `platform`, `pillar`, `topic`, `hook_no`, `headline`, `format`, `lever`, `results`, `batch_id`).
- Edge Function `wt-mcp` at `https://zfgrgbrrqclugszsotfo.supabase.co/functions/v1/wt-mcp`. POST only, JSON-RPC (MCP style). Header `x-wt-key: <WT_MCP_KEY>` (or `Authorization: Bearer <key>`). The key is a Supabase Edge Function secret set by Hiyab. It is never written in this repo, in the app source or in a chat.
- Tools: `save_batch`, `list_batches`, `get_batch`, `set_batch_status`, `log_post`, `list_post_log`. Input shapes are returned by `tools/list`.
- Not tested end to end yet (nobody here can reach the project from a sandbox). Step C0 below tests it.

### The shape of a batch (the contract)

`template`: the template id from the brand kit's `TEMPLATES.md` (for example `info-definition`, `mkt-feature`, `type-aurora`). It must match a `postTemplate(id)` in the app.

`posts`: a list, one object per poster. Keys are the box names the brand kit uses: `eyebrow`, `headline`, `message`, `number`, `list`, `button`. `list` is an array of strings, one per line. Only boxes the template has.

```json
{
  "template": "mkt-feature",
  "title": "Freshman registration",
  "level": "Freshman",
  "pillar": "Announcements",
  "posts": [
    {
      "eyebrow": "FRESHMAN REGISTRATION",
      "headline": "First round is open",
      "list": ["Books, Short Notes, Flashcards", "Question Banks with solutions", "An exam for every chapter", "AI tutor, Planner, Your status"]
    }
  ],
  "captions": ["Freshman, first round registration is open. ..."],
  "status": "draft"
}
```

`captions[i]` belongs to `posts[i]`. A single poster is a batch with one post.

## 4. The chat side

The chat AI reads the brand kit, writes the posts with the usual checks, then saves the batch.

- **Now (no new work):** a chat that has the Supabase connector inserts the batch with SQL into `wt_post.batches` only. It must never read or write `public`.
- **Later (optional):** register `wt-mcp` as a custom connector so any chat can call `save_batch` directly. Not verified: the chat app may not accept a custom header for the key. If it does not, keep using the Supabase connector, or put a small key check in front of the function.
- Brand kit change after C2 works on a phone: `AGENT_START.md` gets one rule, "when the chat link is on, save every batch with `save_batch` and say the batch title". Do not add it before then.

## 5. Rules for any assistant working on this

1. Everything in `BATCH_PLAN.md` section 2 still applies (work on `main`, read before editing, small slices, say "not compiled, please build").
2. **No new dependency.** Use `java.net.HttpURLConnection` on a background thread (`Dispatchers.IO`) and `org.json`, both part of Android.
3. The key is typed on the phone and kept in SharedPreferences (`PostChatStore`, same style as `PostBatchStore`). Never hard-code a key, URL secret or password. Never log the key.
4. Add `android.permission.INTERNET` to `AndroidManifest.xml`. It is the only manifest change. Nothing else in the app may use the network.
5. Do not change `PostCodec` (version 4), the templates or `PostRenderer`.
6. The Inbox never overwrites the open post or its drafts. It builds posts with `doc.copy(templateId = batch.template, texts = ...)` on a copy, so colors, logo, fonts and photo of the open post carry over (same idea as `PostBatch.apply`).
7. Words only come from the backend. If a box key is unknown for the template, skip it and show a plain note ("Post 3: box Big number is not in this template"), like `PostBatch.parse` does.
8. If the template id is unknown, show "This app does not know template <id>" and do nothing else.
9. UI words are plain and short. Tap targets 48 dp or more.

## 6. Slices (push in this order)

**C0. Link and test.** Manifest permission. Brand screen section "Chat link": URL (prefilled with the wt-mcp URL), key (password field), a Test button that calls `tools/list` and shows "Connected" or the plain error ("Wrong key", "No internet"). `PostChatStore`, `PostChatApi.call(method, params)`. Check: with a wrong key it says Wrong key; with the right key, Connected; with no key everything else in the app behaves as before.

**C1. Inbox list.** A new icon in the studio title row next to Batch. Screen lists batches (`list_batches`, newest first): title, template name, post count, status chip. Pull to refresh. Works offline by showing the last list it fetched (stored as text in SharedPreferences).

**C2. Open a batch.** Tap a batch: `get_batch`, convert `posts` to `BatchItem`s (slot names from the same key map as `PostBatch`), switch the template on a copy of the open post, and hand them to the existing Batch screen preview, text-fit check and Make posts. Do not rewrite the export. Check: a batch of 3 `info-definition` posts shows 3 correct previews and saves 3 files.

**C3. Captions and status.** Show each post's caption under its preview with a Copy button. After Make posts finishes, offer "Mark approved" (`set_batch_status`). A "Mark posted" button sets `published` and offers to log it (`log_post`, one row per post, platform picked from a short list).

**C4. Photos in order.** Reuse slice B5 for photo templates. Batches with a photo template show "Add photos" before Make posts.

Tick each slice in the Status list below in the same push.

## 7. Status

- [x] Backend: schema, tables, `wt-mcp` function deployed, secret set (2 Nov 2026)
- [ ] Backend test with the real key (first `save_batch` and `list_batches`)
- [x] C0 Link and test (source pushed; build and phone verification pending)
- [x] C1 Inbox list (source pushed; build and phone verification pending)
- [x] C2 Open a batch (source pushed; build and phone verification pending)
- [x] C3 Captions and status (source pushed; build and phone verification pending)
- [x] C4 Photos in order (reuses Batch B5 multi-photo picker; source pushed, build and phone verification pending)

## 8. Safety

- The backend lives in a project shared with the FT Agri-Tech app. Do not run migrations, SQL or function deploys that touch `public`. Only `wt_post` and the `wt-mcp` function.
- If a batch has more than 50 posts the app keeps the first 50 (same limit as Batch).
- The function rejects every request when `WT_MCP_KEY` is missing or shorter than 24 characters.
- If the key leaks, change the secret in the Supabase dashboard (Edge Functions, Secrets). The app then says Wrong key until the new key is entered.
- Study material is never stored in the backend. Only the poster words and captions.

## 9. Phone test (Hiyab)

1. Build the release APK (GitHub Actions, "Build Wisdom Tower Post Release APK").
2. Open Brand, Chat link. Paste the key. Tap Test. It must say Connected.
3. In the chat, ask for a batch of 3 posters. Say the batch title.
4. Open Inbox. The batch is there with 3 posts. Open it, check the previews, Make posts.
5. Check the 3 images in Pictures/Wisdom Tower Post.
