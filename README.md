# Wisdom Tower Post

**Wisdom Tower Post is a private, offline-first post maker for Wisdom Tower Academy.** It is for personal use, not public release. The goal is a branded social post in under a minute, with the same logo and footer every time.

The direction is set in **[docs/IMPROVEMENT_PLAN.md](docs/IMPROVEMENT_PLAN.md)**: template first, brand locked, three sizes (1:1, 4:3, 9:16). It replaces the Canva-style layer editor described in docs/PRODUCT_PLAN.md.

## Status by phase

- [x] **Phase 1, foundation.** Post document v2, one renderer for preview, thumbnails and export, three sizes, brand store, logo copied into app storage.
- [x] **Phase 2, brand.** Brand screen, locked header and footer, bundled footer icon set. The app now starts in `PostApp` (an earlier build still started the old screen).
- [x] **Phase 3, templates (code done, needs your check).** 20 templates in 10 families, each laid out per size. Shapes, photo area, lists (dot, check, letter, number, schedule rows), template gallery with live previews. *Check:* open Templates, switch through 1:1, 4:3 and 9:16, and look for any card marked "text too long".
- [x] **Phase 4, editor and navigation.** Templates home with search, family filters and 1:1 / 4:3 / 9:16 preview sizing; fixed preview editor; collapsible editing groups; Brand screen; local photo picker; and My posts shelf for editable saved documents.
- [x] **Phase 5, export workflow.** Modal export sheet, one-or-all size selection, PNG/JPG, multi-size save/share, and live text-fit warning in the preview. The unused legacy layer/PDF source files still need a separate cleanup pass.

The app now opens on the template gallery. Select a template to enter the editor; use the save icon to add an editable copy to My posts. The old studio is no longer part of the navigation flow, but its source files have not yet been deleted.

## Product principles

- Templates are data: slots, shapes and a photo area per size. Content belongs to the slot, so switching template or size keeps what you typed.
- The logo and footer are brand components, not layers. Change them once on the Brand screen.
- One renderer draws everything, at export size, so what you see is what you save.
- Local-first: no sign-in, backend, analytics or internet requirement for editing or export.
- Personal project only; no Play Store release is planned.

## Build

The CI build runs only when started through the API (see `.gitlab-ci.yml`) and produces `app-debug.apk`. Locally, open the repository in Android Studio (Kotlin, Jetpack Compose, Material 3).

Optional assets: put `outfit_extrabold.ttf`, `plus_jakarta_sans_bold.ttf` and `plus_jakarta_sans_regular.ttf` in `app/src/main/assets/fonts/`, and a transparent `wt_logo.png` in `app/src/main/res/drawable-nodpi/`. Without them the app falls back to the system font and the logo you pick on the Brand screen.
