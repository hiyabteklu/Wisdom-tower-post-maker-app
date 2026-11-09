# Wisdom Tower Post

> **AI CONTENT DELIVERY CONTRACT:** Read [docs/AI_CONTENT_DELIVERY.md](docs/AI_CONTENT_DELIVERY.md) before any content-agent integration work. The Brand Kit is the sole content authority; this app is the visual template/rendering authority; Supabase delivers structured posts to Poster inbox. Agents choose the template, save poster slots and captions to the backend, and **must not reply with content in chat after a successful save**.

**Wisdom Tower Post is a private, offline-first post maker for Wisdom Tower Academy.** It is for personal use, not public release. The goal is a branded social post in under a minute, with the same logo and footer every time.

The direction is set in **[docs/IMPROVEMENT_PLAN.md](docs/IMPROVEMENT_PLAN.md)**: template first, brand locked, several sizes. Current status of the templates and the engine is in **[docs/REDESIGN_PLAN.md](docs/REDESIGN_PLAN.md)**. The next feature, making many posts from a pasted list, is planned in **[docs/BATCH_PLAN.md](docs/BATCH_PLAN.md)**. Posters written by an AI chat arrive in the Inbox: **[docs/CHAT_CONTROL_PLAN.md](docs/CHAT_CONTROL_PLAN.md)**. The whole-app plan is **[docs/APP_QUALITY_ROADMAP.md](docs/APP_QUALITY_ROADMAP.md)**. Phone-frame mockups for screenshots: **[docs/DEVICE_MOCKUP_PLAN.md](docs/DEVICE_MOCKUP_PLAN.md)**.

## How it is used

The AI agent reads the full Brand Kit, chooses an app template, writes the structured poster slots and matching caption, then saves the batch to Supabase. Open **Poster inbox**, tap the batch, review it, and press **Make posts**. The successful agent workflow ends at the Inbox; it does not return the copy in chat.

The editor has a tool bar at the bottom: **Text** (the message boxes and overall text size), **Color**, **Font**, **Photo** (add, zoom, move), **Background** (type, colors, full-canvas image) and **More** (layers, logo, icons, footer). Templates are in the strip under the preview.

## Status by phase

- [x] **Phase 1, foundation.** Post document v2, one renderer for preview, thumbnails and export, three sizes, brand store, logo copied into app storage.
- [x] **Phase 2, brand.** Brand screen, locked header and footer, bundled footer icon set.
- [x] **Phase 3, templates.** 38 templates in the Library (36, plus 2 phone mockups added 4 Nov 2026, not compiled yet), each laid out per size. Template gallery with live previews.
- [x] **Chat Inbox.** Batches saved by the AI chat appear in the Inbox (checked on the phone 3 Nov 2026).
- [ ] **Editor tool bar and faster screens (pushed 3 Nov 2026, NOT compiled).** Bottom tool bar in the editor, Inbox redesign, Library built lazily, thumbnails warmed in the background. *Check after building:* each tool button swaps the panel at once; Text is open on start; the bar hides when the keyboard opens; every control that was in the old long list is still reachable (Colors, Background, Photo, Fonts, Text size, Layers, Layer manager, Logo, Icons, Footer); Inbox opens instantly the second time; Library opens fast.
- [ ] **Batch posts.** Paste a list of words, get a folder of finished posts. Plan and status: docs/BATCH_PLAN.md.
- [ ] **Phone mockup image batches (pushed 9 Nov 2026, NOT compiled).** For `Phone mockup` and `Phone and headline`, select multiple screenshots, review them in a swipeable preview, edit individually, and export together. Apply-to-all styling preserves each phone's unique screenshot and crop. Plan/status: docs/DEVICE_MOCKUP_PLAN.md.

## Product principles

- Templates are data: slots, shapes and a photo area per size. Content belongs to the slot, so switching template or size keeps what you typed.
- The logo and footer are brand components, not layers. Change them once on the Brand screen.
- One renderer draws everything, at export size, so what you see is what you save.
- Local-first: editing and export need no sign-in and no internet. The chat link (Inbox) is optional and needs internet only to fetch batches; the last list is kept for offline use.
- Personal project only; no Play Store release is planned.

## Build

Builds run only when started by hand. On GitHub: Actions, 'Build Wisdom Tower Post Release APK', Run workflow (`.github/workflows/build-release-apk.yml`, JDK 17, Gradle 8.9, gives `Wisdom-Tower-Post-release.apk`). The GitLab file `.gitlab-ci.yml` does the same when started through the API. Locally, open the repository in Android Studio (Kotlin, Jetpack Compose, Material 3).

The launcher icon is `new_logo.png` in the repo root. The build copies it into the app (see `app/build.gradle.kts`), so keep it in the root with that name. To change the icon, replace that file.

Optional assets: put `outfit_extrabold.ttf`, `plus_jakarta_sans_bold.ttf` and `plus_jakarta_sans_regular.ttf` in `app/src/main/assets/fonts/`, and a transparent `wt_logo.png` in `app/src/main/res/drawable-nodpi/`. Without them the app falls back to the system font and the logo you pick on the Brand screen.
