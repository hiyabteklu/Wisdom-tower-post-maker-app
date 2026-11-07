# AI content delivery: Brand Kit → Supabase → Poster Inbox

This is the current operating contract for every AI agent/automation creating Wisdom Tower posts. It supersedes older wording in `CHAT_CONTROL_PLAN.md` that asks the agent to announce the batch title or return content in chat.

## Authorities and verified backend

- **Content authority:** [Wisdom Tower Brand Kit](https://github.com/hiyabteklu/wisdom-tower-brand-kit). Read the entire current repo on `main`, starting at `AGENT_START.md`. Facts, product claims, offers, voice, original Amharic/hybrid writing, anti-AI rules, strategy, post ideas and content-slot limits come from this repo. Do not use shallow summaries, model memory, generic copy or invented facts as substitutes.
- **Template/rendering authority:** this app repo. Brand Kit `TEMPLATES.md` specifies copy shape and limits; `PostTemplates.kt` and its template files define the actual app template registry and renderer. The chosen ID must exist in both. The app renders the poster; agents send structured slot text and captions, not a generated image file.
- **Supabase project:** **Ft agritech**, ref/ID `zfgrgbrrqclugszsotfo`, region `eu-west-1`, URL https://zfgrgbrrqclugszsotfo.supabase.co.
- **Edge Function:** `wt-mcp`, ID `cf54ecaf-9ac4-4492-835d-8c611a31f580`, URL https://zfgrgbrrqclugszsotfo.supabase.co/functions/v1/wt-mcp.
- **Private schema:** `wt_post`; tables `batches`, `post_log`, `template_boxes`. RLS is enabled.

## Required agent workflow

1. Read the full Brand Kit, not just its README. Follow `AGENT_START.md`'s read order. For Amharic/hybrid, read `AMHARIC_VOICE_LIBRARY.md`, `LANGUAGE.md` and relevant original categorized examples in full; preserve the original voice and code-switching.
2. Read recent batches and the post log before drafting to avoid repetition.
3. Select the best supported template from Brand Kit `TEMPLATES.md`; verify the exact ID against app `PostTemplates.kt`. The agent chooses; do not ask the owner for an editor screenshot.
4. Write every required slot and one matching caption per post. Apply all brand facts, anti-AI, language, voice, emoji, footer and length rules.
5. Save directly to `wt_post.batches` using `wt-mcp` `save_batch` (or an authorized Supabase connector write if that is the available route), with `status=draft`.
6. **After confirmed save, do not respond in chat.** No copy, caption, batch title, preview, summary or success message. The owner finds the saved work in **Poster inbox** and taps **Make posts**.
7. Never claim success without a confirmed write. If save fails, fix a specific validation error or retry a transient failure; otherwise stop, without replacing the inbox workflow with chat output.

## Batch payload

`wt_post.batches` fields:
- `template`: exact ID in both Brand Kit and app.
- `title`: short searchable title.
- `level`: one level only.
- `pillar`: Brand Kit pillar.
- `posts`: JSON array of poster objects using supported slots `eyebrow`, `headline`, `message`, `number`, `list`, `button`. Include all required slots; `list` is an array of strings.
- `captions`: one caption per post, same order/index.
- `status`: `draft`.
- `notes`: language and any manual action, e.g. photo required before Make posts.

One poster is one batch with one post and one caption. One batch uses one template. Maximum 50 posts. The backend stores structured text and captions, not the final poster bitmap.

## Runtime flow

`PostChat.kt` sends POST JSON-RPC requests to `wt-mcp` with `x-wt-key`. The function exposes `save_batch`, `list_batches`, `get_batch`, `set_batch_status`, `log_post`, and `list_post_log`. `PostChatInboxScreen.kt` lists batches and opens a selected batch. The app fetches the post fields and captions, maps slot names, previews using the selected template and hands the post to the existing Make posts/render/export flow. Captions are available beside the rendered post. Status and publishing log actions use the corresponding backend tools. Inbox list can be cached for offline viewing; backend fetch requires internet.

## Security

- The Supabase project is shared with FT Agri-Tech. Use only `wt_post` and the existing `wt-mcp` function. Never touch `public` or unrelated schemas, policies, grants or migrations.
- Do not weaken RLS or expose the schema through the Data API.
- Never disclose or commit `WT_MCP_KEY`, app credentials, tokens or passwords.
- `PostChat.kt` currently contains a built-in fallback credential in addition to the editable Chat link setting. Do not reproduce it. This is a hardening issue for a separate security change (rotate server secret and remove the built-in fallback).
- Only poster text, captions and workflow metadata belong in this backend. No learning material or student private data.

## Source links

- [Brand Kit AGENT_START](https://github.com/hiyabteklu/wisdom-tower-brand-kit/blob/main/AGENT_START.md)
- [Brand Kit templates](https://github.com/hiyabteklu/wisdom-tower-brand-kit/blob/main/TEMPLATES.md)
- [Brand Kit Amharic library](https://github.com/hiyabteklu/wisdom-tower-brand-kit/blob/main/AMHARIC_VOICE_LIBRARY.md)
- [Brand Kit language rules](https://github.com/hiyabteklu/wisdom-tower-brand-kit/blob/main/LANGUAGE.md)
- [Post Maker chat API](https://github.com/hiyabteklu/Wisdom-tower-post-maker-app/blob/main/app/src/main/java/com/wisdomtower/post/PostChat.kt)
- [Post Maker Inbox UI](https://github.com/hiyabteklu/Wisdom-tower-post-maker-app/blob/main/app/src/main/java/com/wisdomtower/post/PostChatInboxScreen.kt)
- [Post Maker template registry](https://github.com/hiyabteklu/Wisdom-tower-post-maker-app/blob/main/app/src/main/java/com/wisdomtower/post/PostTemplates.kt)
- [Supabase project dashboard](https://supabase.com/dashboard/project/zfgrgbrrqclugszsotfo)
- [wt-mcp function dashboard](https://supabase.com/dashboard/project/zfgrgbrrqclugszsotfo/functions/wt-mcp/details)

**Success means the batch is saved and visible in Poster inbox. The agent does not reply with content in chat.**
