# Wisdom Tower Post

**Wisdom Tower Post is a private, offline-first post maker for Wisdom Tower Academy.** It is for personal use, not public release. The goal is a branded social post in under a minute, with the same logo and footer every time.

The direction is set in **[docs/IMPROVEMENT_PLAN.md](docs/IMPROVEMENT_PLAN.md)**: template first, brand locked, several sizes. Current status of the templates and the engine is in **[docs/REDESIGN_PLAN.md](docs/REDESIGN_PLAN.md)**. The next feature, making many posts from a pasted list, is planned in **[docs/BATCH_PLAN.md](docs/BATCH_PLAN.md)**.

## Status by phase

- [x] **Phase 1, foundation.** Post document v2, one renderer for preview, thumbnails and export, three sizes, brand store, logo copied into app storage.
- [x] **Phase 2, brand.** Brand screen, locked header and footer, bundled footer icon set. The app now starts in `PostApp` (an earlier build still started the old screen).
- [x] **Phase 3, templates (code done, needs your check).** 25 templates in the Library, each laid out per size. Shapes, photo area, lists (dot, check, letter, number, schedule rows), template gallery with live previews. *Check:* open the Library, switch through the sizes, and look for any card marked 'text too long'.
- [ ] **Phase 4, editor and navigation.** Templates home, Editor with collapsed groups, My posts tab, photo picker.
- [ ] **Phase 5, export sheet and cleanup.** Multi-size export sheet, overflow warning before saving.
- [ ] **Batch posts.** Paste a list of words, get a folder of finished posts. Plan and status: docs/BATCH_PLAN.md.

## Product principles

- Templates are data: slots, shapes and a photo area per size. Content belongs to the slot, so switching template or size keeps what you typed.
- The logo and footer are brand components, not layers. Change them once on the Brand screen.
- One renderer draws everything, at export size, so what you see is what you save.
- Local-first: no sign-in, backend, analytics or internet requirement for editing or export.
- Personal project only; no Play Store release is planned.

## Build

Builds run only when started by hand. On GitHub: Actions, 'Build Wisdom Tower Post Release APK', Run workflow (`.github/workflows/build-release-apk.yml`, JDK 17, Gradle 8.9, gives `Wisdom-Tower-Post-release.apk`). The GitLab file `.gitlab-ci.yml` does the same when started through the API. Locally, open the repository in Android Studio (Kotlin, Jetpack Compose, Material 3).

The launcher icon is `new_logo.png` in the repo root. The build copies it into the app (see `app/build.gradle.kts`), so keep it in the root with that name. To change the icon, replace that file.

Optional assets: put `outfit_extrabold.ttf`, `plus_jakarta_sans_bold.ttf` and `plus_jakarta_sans_regular.ttf` in `app/src/main/assets/fonts/`, and a transparent `wt_logo.png` in `app/src/main/res/drawable-nodpi/`. Without them the app falls back to the system font and the logo you pick on the Brand screen.
