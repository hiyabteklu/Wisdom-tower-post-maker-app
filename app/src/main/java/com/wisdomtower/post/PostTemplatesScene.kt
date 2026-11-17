package com.wisdomtower.post

// ─────────────────────────────────────────────────────────────────────────────
// Family 8 — Scene (11 surreal 3D photo-composite study & campus posters)
// Built for 4:3 (standard), 9:16 (story), and 16:9 (wide) using PostSize.pick3.
// ─────────────────────────────────────────────────────────────────────────────

internal val SceneTemplates: List<PostTemplateSpec> = listOf(

    // 1. Escape Key Portal (scn-esckey)
    PostTemplateSpec(
        id = "scn-esckey",
        name = "Escape Key Portal",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.WORD, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "BREAK THE ROUTINE",
            PostSlots.WORD to "ESC",
            PostSlots.HEADLINE to "Escape cramming.\nStudy smart.",
            PostSlots.BODY to "Step out of endless burnout and into a structured daily system."
        ),
        looks = listOf(
            TemplateLook("Cyber gold", "#F59E0B", "#0A0E17", PostBase.DARK),
            TemplateLook("Neon mint", "#10B981", "#071318", PostBase.DARK),
            TemplateLook("Electric cobalt", "#3B82F6", "#090D1A", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(
                    x = 0.16f, y = 0.36f, w = 0.68f, h = 0.30f, rounded = true,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.escKeyWellClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_ESCKEY, 0.10f, 0.22f, 0.80f, 0.50f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_ESCKEY, 0.10f, 0.22f, 0.80f, 0.50f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.14f, 0.76f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(
                        PostSlots.WORD, 0.24f, 0.24f, 0.32f, 0.08f, 44f, PostFont.DISPLAY, PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL,
                        perspectiveQuad = floatArrayOf(0.24f, 0.25f, 0.54f, 0.22f, 0.56f, 0.31f, 0.22f, 0.33f)
                    ),
                    SlotBox(PostSlots.HEADLINE, 0.12f, 0.68f, 0.76f, 0.08f, 46f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.76f, 0.72f, 0.04f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(
                    x = 0.18f, y = 0.38f, w = 0.64f, h = 0.40f, rounded = true,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.escKeyWellClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_ESCKEY, 0.10f, 0.18f, 0.80f, 0.66f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_ESCKEY, 0.10f, 0.18f, 0.80f, 0.66f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.07f, 0.80f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(
                        PostSlots.WORD, 0.24f, 0.20f, 0.28f, 0.11f, 52f, PostFont.DISPLAY, PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL,
                        perspectiveQuad = floatArrayOf(0.24f, 0.22f, 0.52f, 0.18f, 0.54f, 0.30f, 0.22f, 0.33f)
                    ),
                    SlotBox(PostSlots.HEADLINE, 0.10f, 0.78f, 0.80f, 0.11f, 50f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.89f, 0.72f, 0.05f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(
                    x = 0.48f, y = 0.32f, w = 0.44f, h = 0.50f, rounded = true,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.escKeyWellClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_ESCKEY, 0.42f, 0.12f, 0.52f, 0.76f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_ESCKEY, 0.42f, 0.12f, 0.52f, 0.76f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.34f, 0.05f, 22f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(
                        PostSlots.WORD, 0.50f, 0.15f, 0.22f, 0.12f, 48f, PostFont.DISPLAY, PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL,
                        perspectiveQuad = floatArrayOf(0.50f, 0.17f, 0.72f, 0.14f, 0.74f, 0.26f, 0.48f, 0.28f)
                    ),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.28f, 0.34f, 0.36f, 56f, PostFont.DISPLAY, PostTone.LIGHT, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.68f, 0.34f, 0.16f, 26f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 2. Book Swimming Pool (scn-bookpool)
    PostTemplateSpec(
        id = "scn-bookpool",
        name = "Book Swimming Pool",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "DEEP IMMERSION",
            PostSlots.HEADLINE to "Dive deep into\nyour notes",
            PostSlots.BODY to "Don't just skim the surface. Master the core concepts first."
        ),
        looks = listOf(
            TemplateLook("Pool aqua", "#06B6D4", "#0C1826", PostBase.DARK),
            TemplateLook("Sunlit teal", "#14B8A6", "#F3EFE6", PostBase.LIGHT),
            TemplateLook("Gold dive", "#F59E0B", "#0F172A", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                photo = PostPhotoArea(
                    x = 0.48f, y = 0.34f, w = 0.38f, h = 0.34f, rounded = true,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.bookPoolClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_BOOKPOOL, 0.09f, 0.24f, 0.82f, 0.50f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_BOOKPOOL, 0.09f, 0.24f, 0.82f, 0.50f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.15f, 0.76f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f),
                    SlotBox(
                        PostSlots.HEADLINE, 0.14f, 0.34f, 0.31f, 0.22f, 44f, PostFont.SERIF, PostTone.DARK,
                        fit = FitMode.FILL,
                        perspectiveQuad = floatArrayOf(0.14f, 0.35f, 0.45f, 0.33f, 0.45f, 0.55f, 0.14f, 0.57f)
                    ),
                    SlotBox(PostSlots.BODY, 0.14f, 0.58f, 0.31f, 0.10f, 22f, PostFont.REGULAR, PostTone.DARK, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                photo = PostPhotoArea(
                    x = 0.50f, y = 0.26f, w = 0.38f, h = 0.54f, rounded = true,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.bookPoolClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_BOOKPOOL, 0.06f, 0.16f, 0.88f, 0.72f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_BOOKPOOL, 0.06f, 0.16f, 0.88f, 0.72f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.26f, 0.34f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f),
                    SlotBox(
                        PostSlots.HEADLINE, 0.12f, 0.33f, 0.34f, 0.28f, 52f, PostFont.SERIF, PostTone.DARK,
                        fit = FitMode.FILL,
                        perspectiveQuad = floatArrayOf(0.12f, 0.34f, 0.46f, 0.32f, 0.46f, 0.60f, 0.12f, 0.62f)
                    ),
                    SlotBox(PostSlots.BODY, 0.12f, 0.64f, 0.34f, 0.14f, 24f, PostFont.REGULAR, PostTone.DARK, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                photo = PostPhotoArea(
                    x = 0.50f, y = 0.22f, w = 0.38f, h = 0.58f, rounded = true,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.bookPoolClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_BOOKPOOL, 0.06f, 0.12f, 0.88f, 0.78f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_BOOKPOOL, 0.06f, 0.12f, 0.88f, 0.78f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.24f, 0.34f, 0.05f, 22f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f),
                    SlotBox(
                        PostSlots.HEADLINE, 0.12f, 0.32f, 0.34f, 0.32f, 54f, PostFont.SERIF, PostTone.DARK,
                        fit = FitMode.FILL,
                        perspectiveQuad = floatArrayOf(0.12f, 0.33f, 0.46f, 0.31f, 0.46f, 0.63f, 0.12f, 0.65f)
                    ),
                    SlotBox(PostSlots.BODY, 0.12f, 0.66f, 0.34f, 0.14f, 24f, PostFont.REGULAR, PostTone.DARK, fit = FitMode.FILL)
                )
            )
        )
    },

    // 3. Open Door to Campus (scn-housedoor)
    PostTemplateSpec(
        id = "scn-housedoor",
        name = "Open Door to Campus",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "NEW SEMESTER",
            PostSlots.HEADLINE to "Walk into\nyour future",
            PostSlots.BODY to "Every exam passed opens a bigger door ahead."
        ),
        looks = listOf(
            TemplateLook("Golden threshold", "#F59E0B", "#0C1018", PostBase.DARK),
            TemplateLook("Emerald gate", "#10B981", "#091412", PostBase.DARK),
            TemplateLook("Sunset hall", "#FB7185", "#170E18", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(
                    x = 0.34f, y = 0.24f, w = 0.44f, h = 0.40f,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.houseDoorClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_HOUSEDOOR, 0.12f, 0.18f, 0.76f, 0.52f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_HOUSEDOOR, 0.12f, 0.18f, 0.76f, 0.52f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.14f, 0.76f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.12f, 0.66f, 0.76f, 0.08f, 46f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.75f, 0.72f, 0.04f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(
                    x = 0.38f, y = 0.16f, w = 0.38f, h = 0.58f,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.houseDoorClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_HOUSEDOOR, 0.16f, 0.10f, 0.68f, 0.76f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_HOUSEDOOR, 0.16f, 0.10f, 0.68f, 0.76f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.06f, 0.80f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.10f, 0.76f, 0.80f, 0.12f, 52f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.89f, 0.72f, 0.05f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(
                    x = 0.54f, y = 0.14f, w = 0.34f, h = 0.66f,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.houseDoorClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_HOUSEDOOR, 0.42f, 0.08f, 0.52f, 0.82f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_HOUSEDOOR, 0.42f, 0.08f, 0.52f, 0.82f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.34f, 0.05f, 22f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.28f, 0.34f, 0.36f, 56f, PostFont.DISPLAY, PostTone.LIGHT, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.68f, 0.34f, 0.16f, 26f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 4. Study Desk Flat-Lay (scn-deskflat) — 2 photos
    PostTemplateSpec(
        id = "scn-deskflat",
        name = "Study Desk Flat-Lay",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "STUDY DESK LOG",
            PostSlots.HEADLINE to "Today's workspace\n& progress",
            PostSlots.BODY to "2 HRS POMODORO · ZERO DISTRACTIONS"
        ),
        looks = listOf(
            TemplateLook("Warm espresso", "#D97706", "#F5EFE4", PostBase.LIGHT),
            TemplateLook("Midnight desk", "#F59E0B", "#141923", PostBase.DARK),
            TemplateLook("Sage studio", "#10B981", "#EEF4F0", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                photo = PostPhotoArea(
                    x = 0.12f, y = 0.22f, w = 0.76f, h = 0.42f,
                    cells = listOf(
                        PhotoCell(0.14f, 0.24f, 0.40f, 0.28f, rotate = -5f, radius = 0.03f, photoIndex = 0, rimTone = PostTone.LIGHT, shadow = true),
                        PhotoCell(0.44f, 0.36f, 0.40f, 0.28f, rotate = 4f, radius = 0.03f, photoIndex = 1, rimTone = PostTone.LIGHT, shadow = true)
                    ),
                    shadow = true, innerShadow = true
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_DESKFLAT, 0.09f, 0.16f, 0.82f, 0.60f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_DESKFLAT, 0.09f, 0.16f, 0.82f, 0.60f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO),
                    DecorBox(DecorShape.TAPE_BAND, 0.26f, 0.22f, 0.14f, 0.03f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.14f, 0.15f, 0.72f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f),
                    SlotBox(PostSlots.HEADLINE, 0.14f, 0.66f, 0.46f, 0.09f, 44f, PostFont.SERIF, PostTone.TEXT, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.62f, 0.66f, 0.22f, 0.08f, 22f, PostFont.BOLD, PostTone.DARK, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                photo = PostPhotoArea(
                    x = 0.10f, y = 0.18f, w = 0.76f, h = 0.56f,
                    cells = listOf(
                        PhotoCell(0.12f, 0.20f, 0.36f, 0.48f, rotate = -5f, radius = 0.03f, photoIndex = 0, rimTone = PostTone.LIGHT, shadow = true),
                        PhotoCell(0.46f, 0.26f, 0.36f, 0.48f, rotate = 4f, radius = 0.03f, photoIndex = 1, rimTone = PostTone.LIGHT, shadow = true)
                    ),
                    shadow = true, innerShadow = true
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_DESKFLAT, 0.04f, 0.06f, 0.92f, 0.88f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_DESKFLAT, 0.04f, 0.06f, 0.92f, 0.88f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO),
                    DecorBox(DecorShape.TAPE_BAND, 0.24f, 0.17f, 0.12f, 0.04f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO),
                    DecorBox(DecorShape.TAPE_BAND, 0.58f, 0.23f, 0.12f, 0.04f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.10f, 0.50f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f),
                    SlotBox(PostSlots.HEADLINE, 0.10f, 0.74f, 0.52f, 0.14f, 48f, PostFont.SERIF, PostTone.TEXT, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.64f, 0.74f, 0.22f, 0.12f, 22f, PostFont.BOLD, PostTone.DARK, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                photo = PostPhotoArea(
                    x = 0.40f, y = 0.16f, w = 0.52f, h = 0.66f,
                    cells = listOf(
                        PhotoCell(0.42f, 0.18f, 0.24f, 0.56f, rotate = -4f, radius = 0.03f, photoIndex = 0, rimTone = PostTone.LIGHT, shadow = true),
                        PhotoCell(0.66f, 0.24f, 0.24f, 0.56f, rotate = 4f, radius = 0.03f, photoIndex = 1, rimTone = PostTone.LIGHT, shadow = true)
                    ),
                    shadow = true, innerShadow = true
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_DESKFLAT, 0.04f, 0.08f, 0.92f, 0.84f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_DESKFLAT, 0.04f, 0.08f, 0.92f, 0.84f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.08f, 0.20f, 0.30f, 0.05f, 22f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.28f, 0.30f, 0.36f, 52f, PostFont.SERIF, PostTone.TEXT, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.08f, 0.68f, 0.30f, 0.12f, 24f, PostFont.BOLD, PostTone.MUTED, caps = true, fit = FitMode.FILL)
                )
            )
        )
    },

    // 5. Glowing Mind Silhouette (scn-facelight)
    PostTemplateSpec(
        id = "scn-facelight",
        name = "Glowing Mind Silhouette",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "MINDSET SHIFT",
            PostSlots.HEADLINE to "Feed your mind\nwhat matters",
            PostSlots.BODY to "What you visualize every day shapes how you perform under pressure."
        ),
        looks = listOf(
            TemplateLook("Mind gold", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Synapse cyan", "#38BDF8", "#07101C", PostBase.DARK),
            TemplateLook("Violet aura", "#A855F7", "#11091C", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                photo = PostPhotoArea(
                    x = 0.20f, y = 0.20f, w = 0.60f, h = 0.42f,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.faceSilhouetteClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_FACELIGHT, 0.20f, 0.20f, 0.60f, 0.42f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_FACELIGHT, 0.20f, 0.20f, 0.60f, 0.42f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.14f, 0.76f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.12f, 0.66f, 0.76f, 0.08f, 46f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.75f, 0.72f, 0.04f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                photo = PostPhotoArea(
                    x = 0.24f, y = 0.12f, w = 0.52f, h = 0.60f,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.faceSilhouetteClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_FACELIGHT, 0.24f, 0.12f, 0.52f, 0.60f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_FACELIGHT, 0.24f, 0.12f, 0.52f, 0.60f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.06f, 0.80f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.10f, 0.75f, 0.80f, 0.13f, 52f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.89f, 0.72f, 0.05f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                photo = PostPhotoArea(
                    x = 0.50f, y = 0.10f, w = 0.40f, h = 0.78f,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.faceSilhouetteClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_FACELIGHT, 0.50f, 0.10f, 0.40f, 0.78f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_FACELIGHT, 0.50f, 0.10f, 0.40f, 0.78f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.38f, 0.05f, 22f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.28f, 0.38f, 0.36f, 56f, PostFont.DISPLAY, PostTone.LIGHT, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.68f, 0.38f, 0.16f, 26f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 6. Pencil Skyscraper City (scn-pencilcity)
    PostTemplateSpec(
        id = "scn-pencilcity",
        name = "Pencil Skyscraper City",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "BUILD YOUR FUTURE",
            PostSlots.HEADLINE to "Every page\nbuilds the skyline",
            PostSlots.BODY to "Small daily study sessions stack into massive results."
        ),
        looks = listOf(
            TemplateLook("Metro amber", "#F59E0B", "#0E1525", PostBase.DARK),
            TemplateLook("Skyline emerald", "#10B981", "#0C1A1E", PostBase.DARK),
            TemplateLook("Daylight plaza", "#2563EB", "#EFF4FA", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.MESH,
                photo = PostPhotoArea(
                    x = 0.40f, y = 0.32f, w = 0.38f, h = 0.30f, rounded = true,
                    rimTone = PostTone.ACCENT, shadow = true, innerShadow = true, lightSpill = true
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_PENCILCITY, 0.09f, 0.26f, 0.82f, 0.48f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_PENCILCITY, 0.09f, 0.26f, 0.82f, 0.48f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.14f, 0.76f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.12f, 0.18f, 0.76f, 0.08f, 44f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.74f, 0.72f, 0.04f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.MESH,
                photo = PostPhotoArea(
                    x = 0.44f, y = 0.26f, w = 0.32f, h = 0.44f, rounded = true,
                    rimTone = PostTone.ACCENT, shadow = true, innerShadow = true, lightSpill = true
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_PENCILCITY, 0.04f, 0.18f, 0.92f, 0.70f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_PENCILCITY, 0.04f, 0.18f, 0.92f, 0.70f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.08f, 0.06f, 0.84f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.11f, 0.84f, 0.11f, 50f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.89f, 0.72f, 0.05f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.MESH,
                photo = PostPhotoArea(
                    x = 0.58f, y = 0.20f, w = 0.28f, h = 0.50f, rounded = true,
                    rimTone = PostTone.ACCENT, shadow = true, innerShadow = true, lightSpill = true
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_PENCILCITY, 0.42f, 0.10f, 0.54f, 0.82f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_PENCILCITY, 0.42f, 0.10f, 0.54f, 0.82f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.34f, 0.05f, 22f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.28f, 0.34f, 0.36f, 56f, PostFont.DISPLAY, PostTone.TEXT, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.68f, 0.34f, 0.16f, 26f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 7. Unzipped Backpack World (scn-backpackportal)
    PostTemplateSpec(
        id = "scn-backpackportal",
        name = "Unzipped Backpack World",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "PACK YOUR SKILLS",
            PostSlots.HEADLINE to "Everything you need\nis inside",
            PostSlots.BODY to "Past exams, curated summaries, and smart revision tools in one place."
        ),
        looks = listOf(
            TemplateLook("Explorer gold", "#F59E0B", "#0D131F", PostBase.DARK),
            TemplateLook("Campus teal", "#14B8A6", "#09171A", PostBase.DARK),
            TemplateLook("Crimson pack", "#E11D48", "#180B12", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(
                    x = 0.20f, y = 0.24f, w = 0.60f, h = 0.38f,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.backpackPortalClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_BACKPACKPORTAL, 0.12f, 0.18f, 0.76f, 0.48f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_BACKPACKPORTAL, 0.12f, 0.18f, 0.76f, 0.48f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.14f, 0.76f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.12f, 0.66f, 0.76f, 0.08f, 46f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.75f, 0.72f, 0.04f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(
                    x = 0.24f, y = 0.20f, w = 0.52f, h = 0.50f,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.backpackPortalClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_BACKPACKPORTAL, 0.16f, 0.12f, 0.68f, 0.64f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_BACKPACKPORTAL, 0.16f, 0.12f, 0.68f, 0.64f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.06f, 0.80f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.76f, 0.84f, 0.12f, 52f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.89f, 0.72f, 0.05f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(
                    x = 0.50f, y = 0.16f, w = 0.38f, h = 0.64f,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.backpackPortalClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_BACKPACKPORTAL, 0.44f, 0.08f, 0.50f, 0.82f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_BACKPACKPORTAL, 0.44f, 0.08f, 0.50f, 0.82f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.36f, 0.05f, 22f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.28f, 0.36f, 0.36f, 56f, PostFont.DISPLAY, PostTone.LIGHT, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.68f, 0.36f, 0.16f, 26f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 8. Hanging on the Clock Hand (scn-clockhang) — 0 photos (pure 3D illustration)
    PostTemplateSpec(
        id = "scn-clockhang",
        name = "Hanging on the Clock Hand",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.WORD, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "BEAT THE CLOCK",
            PostSlots.WORD to "FINAL COUNTDOWN",
            PostSlots.HEADLINE to "Stop racing\nthe deadline",
            PostSlots.BODY to "Start 20 minutes earlier today and save yourself the midnight panic."
        ),
        looks = listOf(
            TemplateLook("Crunch gold", "#F59E0B", "#0C111D", PostBase.DARK),
            TemplateLook("Urgent crimson", "#EF4444", "#180C10", PostBase.DARK),
            TemplateLook("Morning mint", "#10B981", "#F4F1EA", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                decor = listOf(
                    DecorBox(DecorShape.SCENE_CLOCKHANG, 0.12f, 0.20f, 0.76f, 0.46f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_CLOCKHANG, 0.12f, 0.20f, 0.76f, 0.46f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.14f, 0.76f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.WORD, 0.20f, 0.20f, 0.60f, 0.08f, 28f, PostFont.DISPLAY, PostTone.ACCENT, centered = true, caps = true, fx = TextFx.ARC),
                    SlotBox(PostSlots.HEADLINE, 0.12f, 0.66f, 0.76f, 0.08f, 46f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.75f, 0.72f, 0.04f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                decor = listOf(
                    DecorBox(DecorShape.SCENE_CLOCKHANG, 0.14f, 0.10f, 0.72f, 0.64f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_CLOCKHANG, 0.14f, 0.10f, 0.72f, 0.64f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.05f, 0.80f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.WORD, 0.24f, 0.12f, 0.52f, 0.10f, 32f, PostFont.DISPLAY, PostTone.ACCENT, centered = true, caps = true, fx = TextFx.ARC),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.74f, 0.84f, 0.13f, 52f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.89f, 0.72f, 0.05f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                decor = listOf(
                    DecorBox(DecorShape.SCENE_CLOCKHANG, 0.44f, 0.08f, 0.50f, 0.84f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_CLOCKHANG, 0.44f, 0.08f, 0.50f, 0.84f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.36f, 0.05f, 22f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.WORD, 0.52f, 0.10f, 0.34f, 0.10f, 30f, PostFont.DISPLAY, PostTone.ACCENT, centered = true, caps = true, fx = TextFx.ARC),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.28f, 0.36f, 0.36f, 56f, PostFont.DISPLAY, PostTone.TEXT, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.68f, 0.36f, 0.16f, 26f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 9. Exam Paper Canyon Bridge (scn-examcliff)
    PostTemplateSpec(
        id = "scn-examcliff",
        name = "Exam Paper Canyon Bridge",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "BRIDGE THE GAP",
            PostSlots.HEADLINE to "Cross from\ndoubt to A+",
            PostSlots.BODY to "Practice papers are the bridge between knowing and scoring."
        ),
        looks = listOf(
            TemplateLook("Canyon amber", "#F59E0B", "#0E1628", PostBase.DARK),
            TemplateLook("Summit cobalt", "#3B82F6", "#0A1222", PostBase.DARK),
            TemplateLook("Sunrise coral", "#FB7185", "#1A0F1A", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.DIAG,
                photo = PostPhotoArea(
                    x = 0.10f, y = 0.20f, w = 0.80f, h = 0.36f, rounded = true,
                    shadow = true, innerShadow = true, lightSpill = true
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_EXAMCLIFF, 0.09f, 0.24f, 0.82f, 0.44f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_EXAMCLIFF, 0.09f, 0.24f, 0.82f, 0.44f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.14f, 0.76f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.12f, 0.66f, 0.76f, 0.08f, 46f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.75f, 0.72f, 0.04f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.DIAG,
                photo = PostPhotoArea(
                    x = 0.06f, y = 0.08f, w = 0.88f, h = 0.56f, rounded = true,
                    shadow = true, innerShadow = true, lightSpill = true
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_EXAMCLIFF, 0.04f, 0.18f, 0.92f, 0.60f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_EXAMCLIFF, 0.04f, 0.18f, 0.92f, 0.60f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.11f, 0.80f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.10f, 0.75f, 0.80f, 0.13f, 52f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.89f, 0.72f, 0.05f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.DIAG,
                photo = PostPhotoArea(
                    x = 0.42f, y = 0.10f, w = 0.52f, h = 0.62f, rounded = true,
                    shadow = true, innerShadow = true, lightSpill = true
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_EXAMCLIFF, 0.40f, 0.18f, 0.56f, 0.72f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_EXAMCLIFF, 0.40f, 0.18f, 0.56f, 0.72f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.32f, 0.05f, 22f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.28f, 0.32f, 0.36f, 56f, PostFont.DISPLAY, PostTone.LIGHT, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.68f, 0.32f, 0.16f, 26f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 10. Graduation Cap Tractor Beam (scn-capufo)
    PostTemplateSpec(
        id = "scn-capufo",
        name = "Graduation Cap Tractor Beam",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "CLASS OF EXCELLENCE",
            PostSlots.HEADLINE to "Rise toward\ngraduation day",
            PostSlots.BODY to "Keep your eyes on the cap and gown — every study session counts."
        ),
        looks = listOf(
            TemplateLook("Honors gold", "#F59E0B", "#080C18", PostBase.DARK),
            TemplateLook("Beam emerald", "#10B981", "#071412", PostBase.DARK),
            TemplateLook("Starlight violet", "#A855F7", "#11091D", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(
                    x = 0.18f, y = 0.30f, w = 0.64f, h = 0.34f,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.ufoBeamClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_CAPUFO, 0.12f, 0.16f, 0.76f, 0.50f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_CAPUFO, 0.12f, 0.16f, 0.76f, 0.50f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.14f, 0.76f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.12f, 0.66f, 0.76f, 0.08f, 46f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.75f, 0.72f, 0.04f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(
                    x = 0.22f, y = 0.28f, w = 0.56f, h = 0.44f,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.ufoBeamClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_CAPUFO, 0.14f, 0.08f, 0.72f, 0.68f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_CAPUFO, 0.14f, 0.08f, 0.72f, 0.68f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.04f, 0.80f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.76f, 0.84f, 0.12f, 52f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.89f, 0.72f, 0.05f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(
                    x = 0.48f, y = 0.28f, w = 0.42f, h = 0.56f,
                    shadow = true, innerShadow = true, lightSpill = true,
                    clipShape = PostScenery.ufoBeamClip
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_CAPUFO, 0.44f, 0.06f, 0.50f, 0.84f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_CAPUFO, 0.44f, 0.06f, 0.50f, 0.84f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.36f, 0.05f, 22f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.28f, 0.36f, 0.36f, 56f, PostFont.DISPLAY, PostTone.LIGHT, fit = FitMode.FILL, shadow = TextShadow(70, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.68f, 0.36f, 0.16f, 26f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 11. Origami Paper Boat on Sea (scn-paperboat)
    PostTemplateSpec(
        id = "scn-paperboat",
        name = "Origami Paper Boat on Sea",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "STEADY VOYAGE",
            PostSlots.HEADLINE to "Navigate the\nsemester calm",
            PostSlots.BODY to "Consistent daily revision keeps you afloat through exam week."
        ),
        looks = listOf(
            TemplateLook("Voyage gold", "#F59E0B", "#0A1424", PostBase.DARK),
            TemplateLook("Ocean breeze", "#38BDF8", "#071828", PostBase.DARK),
            TemplateLook("Dawn parchment", "#D97706", "#F5EFE3", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.EDITORIAL,
                photo = PostPhotoArea(
                    x = 0.10f, y = 0.42f, w = 0.80f, h = 0.24f, rounded = true,
                    shadow = true, innerShadow = true, lightSpill = true
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_PAPERBOAT, 0.14f, 0.26f, 0.72f, 0.40f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_PAPERBOAT, 0.14f, 0.26f, 0.72f, 0.40f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.14f, 0.76f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.12f, 0.18f, 0.76f, 0.09f, 44f, PostFont.SERIF, PostTone.TEXT, centered = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.72f, 0.72f, 0.05f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.EDITORIAL,
                photo = PostPhotoArea(
                    x = 0.08f, y = 0.44f, w = 0.84f, h = 0.36f, rounded = true,
                    shadow = true, innerShadow = true, lightSpill = true
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_PAPERBOAT, 0.18f, 0.20f, 0.64f, 0.56f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_PAPERBOAT, 0.18f, 0.20f, 0.64f, 0.56f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.06f, 0.80f, 0.04f, 22f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.10f, 0.12f, 0.80f, 0.13f, 52f, PostFont.SERIF, PostTone.TEXT, centered = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.86f, 0.72f, 0.06f, 24f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.EDITORIAL,
                photo = PostPhotoArea(
                    x = 0.44f, y = 0.46f, w = 0.48f, h = 0.40f, rounded = true,
                    shadow = true, innerShadow = true, lightSpill = true
                ),
                decor = listOf(
                    DecorBox(DecorShape.SCENE_PAPERBOAT, 0.46f, 0.16f, 0.44f, 0.68f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO),
                    DecorBox(DecorShape.SCENE_PAPERBOAT, 0.46f, 0.16f, 0.44f, 0.68f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.34f, 0.05f, 22f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.28f, 0.34f, 0.36f, 56f, PostFont.SERIF, PostTone.TEXT, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.68f, 0.34f, 0.16f, 26f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    }
)
