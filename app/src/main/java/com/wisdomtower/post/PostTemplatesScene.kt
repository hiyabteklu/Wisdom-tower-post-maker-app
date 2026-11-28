package com.wisdomtower.post

// ─────────────────────────────────────────────────────────────────────────────
// Family 8 — Scene (11 surreal 3D photo-composite study & campus posters)
// Built for 4:3 (standard), 9:16 (story), and 16:9 (wide) using PostSize.pick3.
// ─────────────────────────────────────────────────────────────────────────────

internal val SceneTemplates: List<PostTemplateSpec> = listOf(

    // 1. 3D Open Textbook Spread (scn-esckey) — 2 photo slots (Left Page + Right Page)
    PostTemplateSpec(
        id = "scn-esckey",
        name = "3D Open Textbook",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "CHAPTER SUMMARY · GRADE 12",
            PostSlots.HEADLINE to "Master every chapter\npage by page.",
            PostSlots.BODY to "Structured textbook notes, clear diagrams, and exam-focused explanations."
        ),
        looks = listOf(
            TemplateLook("Cobalt studio", "#38BDF8", "#071A36", PostBase.DARK),
            TemplateLook("Scholar gold", "#F59E0B", "#080C16", PostBase.DARK),
            TemplateLook("Emerald academy", "#10B981", "#061512", PostBase.DARK),
            TemplateLook("Daylight study", "#2563EB", "#F4F6FB", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.04f, y = 0.22f, w = 0.92f, h = 0.44f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 6
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.14f, 0.76f, 0.038f, 21f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.67f, 0.84f, 0.085f, 48f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.12f, 0.765f, 0.76f, 0.042f, 23f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.06f, y = 0.14f, w = 0.88f, h = 0.58f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 6
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.06f, 0.80f, 0.042f, 21f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.75f, 0.84f, 0.12f, 52f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.885f, 0.72f, 0.05f, 23f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.42f, y = 0.08f, w = 0.54f, h = 0.84f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 6
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.34f, 0.05f, 21f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.27f, 0.34f, 0.36f, 56f, PostFont.DISPLAY, PostTone.TEXT, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.67f, 0.34f, 0.16f, 25f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 2. 3D Spiral Ring Notebook + Tear-Out Sheets (scn-bookpool) — 1..4 dynamic photo layers at 45°
    PostTemplateSpec(
        id = "scn-bookpool",
        name = "3D Spiral Notebook",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE),
        sample = mapOf(
            PostSlots.EYEBROW to "HANDWRITTEN & DIGITAL NOTES",
            PostSlots.HEADLINE to "All your lecture notes in one smart notebook."
        ),
        looks = listOf(
            TemplateLook("Cobalt notebook", "#38BDF8", "#071E3D", PostBase.DARK),
            TemplateLook("Amber notebook", "#F59E0B", "#080C16", PostBase.DARK),
            TemplateLook("Teal notebook", "#14B8A6", "#061618", PostBase.DARK),
            TemplateLook("Paper studio", "#2563EB", "#F5F7FC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.05f, y = 0.24f, w = 0.90f, h = 0.56f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 7
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.135f, 0.76f, 0.036f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.175f, 0.84f, 0.06f, 40f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.06f, y = 0.20f, w = 0.88f, h = 0.72f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 7
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.05f, 0.80f, 0.04f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.10f, 0.84f, 0.085f, 44f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.40f, y = 0.06f, w = 0.56f, h = 0.88f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 7
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.24f, 0.32f, 0.05f, 20f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.33f, 0.32f, 0.40f, 52f, PostFont.DISPLAY, PostTone.TEXT, fit = FitMode.FILL)
                )
            )
        )
    },

    // 3. 3D Fanned Flashcard Deck (scn-housedoor) — 1..4 dynamic fanned revision cards
    PostTemplateSpec(
        id = "scn-housedoor",
        name = "3D Flashcard Deck",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "ACTIVE RECALL · FLASHCARDS",
            PostSlots.HEADLINE to "Memorize formulas\nin half the time.",
            PostSlots.BODY to "Swipe through curated subject flashcards for Biology, Physics, Math, and SAT."
        ),
        looks = listOf(
            TemplateLook("Cobalt deck", "#38BDF8", "#081428", PostBase.DARK),
            TemplateLook("Emerald deck", "#10B981", "#071814", PostBase.DARK),
            TemplateLook("Gold deck", "#F59E0B", "#0C101A", PostBase.DARK),
            TemplateLook("Clean studio", "#2563EB", "#F4F7FC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.05f, y = 0.20f, w = 0.90f, h = 0.46f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 8
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.14f, 0.76f, 0.038f, 21f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.67f, 0.84f, 0.085f, 46f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.12f, 0.765f, 0.76f, 0.042f, 23f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.08f, y = 0.12f, w = 0.84f, h = 0.60f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 8
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.055f, 0.80f, 0.042f, 21f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.745f, 0.84f, 0.125f, 52f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.14f, 0.885f, 0.72f, 0.05f, 23f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.42f, y = 0.07f, w = 0.54f, h = 0.86f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 8
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.34f, 0.05f, 21f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.27f, 0.34f, 0.36f, 56f, PostFont.DISPLAY, PostTone.TEXT, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.67f, 0.34f, 0.16f, 25f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 4. 3D Exam Clipboard + Layered A4 Exam Papers (scn-deskflat) — 1..4 dynamic photo layers at 45°
    PostTemplateSpec(
        id = "scn-deskflat",
        name = "3D Exam Clipboard",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE),
        sample = mapOf(
            PostSlots.EYEBROW to "PAST EXAMS & WORKSHEETS",
            PostSlots.HEADLINE to "Practice real national exam papers."
        ),
        looks = listOf(
            TemplateLook("Cobalt board", "#38BDF8", "#071D3B", PostBase.DARK),
            TemplateLook("Warm espresso", "#F59E0B", "#0C111D", PostBase.DARK),
            TemplateLook("Sage studio", "#10B981", "#071714", PostBase.DARK),
            TemplateLook("Daylight board", "#2563EB", "#F5F7FC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.05f, y = 0.24f, w = 0.90f, h = 0.56f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 9
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.135f, 0.76f, 0.036f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.175f, 0.84f, 0.06f, 40f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.06f, y = 0.20f, w = 0.88f, h = 0.72f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 9
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.05f, 0.80f, 0.04f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.10f, 0.84f, 0.085f, 44f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.40f, y = 0.06f, w = 0.56f, h = 0.88f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 9
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.24f, 0.32f, 0.05f, 20f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.33f, 0.32f, 0.40f, 52f, PostFont.DISPLAY, PostTone.TEXT, fit = FitMode.FILL)
                )
            )
        )
    },

    // 5. 3D Subject Ring-Binder Folder with Color Divider Tabs (scn-facelight) — 1..4 dynamic photo layers at 45°
    PostTemplateSpec(
        id = "scn-facelight",
        name = "3D Subject Binder",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE),
        sample = mapOf(
            PostSlots.EYEBROW to "ORGANIZED BY CHAPTER",
            PostSlots.HEADLINE to "Every unit sorted with color-coded study tabs."
        ),
        looks = listOf(
            TemplateLook("Synapse cyan", "#38BDF8", "#071A36", PostBase.DARK),
            TemplateLook("Mind gold", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Emerald binder", "#10B981", "#071713", PostBase.DARK),
            TemplateLook("Light binder", "#2563EB", "#F5F7FC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.05f, y = 0.24f, w = 0.90f, h = 0.56f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 10
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.135f, 0.76f, 0.036f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.175f, 0.84f, 0.06f, 40f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.06f, y = 0.20f, w = 0.88f, h = 0.72f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 10
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.05f, 0.80f, 0.04f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.10f, 0.84f, 0.085f, 44f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.40f, y = 0.06f, w = 0.56f, h = 0.88f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 10
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.24f, 0.32f, 0.05f, 20f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.33f, 0.32f, 0.40f, 52f, PostFont.DISPLAY, PostTone.TEXT, fit = FitMode.FILL)
                )
            )
        )
    },

    // 6. 3D Stacked Textbooks Tower & Standing Cover Showcase (scn-pencilcity) — 1..3 photo slots
    PostTemplateSpec(
        id = "scn-pencilcity",
        name = "3D Textbook Stack",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "GRADE 10 · GRADE 11 · GRADE 12",
            PostSlots.HEADLINE to "Your entire curriculum\nin your pocket",
            PostSlots.BODY to "Every textbook, unit summary, and solved exercise ready offline."
        ),
        looks = listOf(
            TemplateLook("Metro amber", "#F59E0B", "#0A1222", PostBase.DARK),
            TemplateLook("Skyline emerald", "#10B981", "#071718", PostBase.DARK),
            TemplateLook("Cobalt library", "#38BDF8", "#071833", PostBase.DARK),
            TemplateLook("Daylight plaza", "#2563EB", "#EFF4FA", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.05f, y = 0.24f, w = 0.90f, h = 0.48f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 11
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.135f, 0.76f, 0.036f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.175f, 0.84f, 0.065f, 42f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.12f, 0.74f, 0.76f, 0.042f, 23f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.08f, y = 0.20f, w = 0.84f, h = 0.66f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 11
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.08f, 0.055f, 0.84f, 0.04f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.105f, 0.84f, 0.09f, 46f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.14f, 0.885f, 0.72f, 0.05f, 23f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.42f, y = 0.07f, w = 0.54f, h = 0.86f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 11
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.34f, 0.05f, 20f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.27f, 0.34f, 0.36f, 54f, PostFont.DISPLAY, PostTone.TEXT, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.06f, 0.67f, 0.34f, 0.16f, 25f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 7. Kinetic Sliced Louvers (scn-backpackportal) — Batch 3 replacement (1 photo slot + movable cutout)
    PostTemplateSpec(
        id = "scn-backpackportal",
        name = "Kinetic Sliced Louvers",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "EDITORIAL MOTION · OPTICAL SLICE",
            PostSlots.HEADLINE to "SEE EVERY ANGLE.",
            PostSlots.BODY to "Pick a photo to slice across 7 staggered optical louvers, then pop a sharp BG-removed cutout right in front."
        ),
        looks = listOf(
            TemplateLook("Cobalt slice", "#38BDF8", "#050B16", PostBase.DARK),
            TemplateLook("Amber gallery", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Emerald kinetic", "#10B981", "#040E0C", PostBase.DARK),
            TemplateLook("Gallery white", "#2563EB", "#F5F7FC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.18f, 0.24f, 0.64f, 0.44f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.KINETIC_SLICED_LOUVERS,
                        x = 0.06f, y = 0.19f, w = 0.88f, h = 0.46f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        layer = DecorLayer.BEHIND_PHOTO, panel = true
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.12f, 0.80f, 0.038f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.68f, 0.84f, 0.095f, 58f, PostFont.DISPLAY, PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.10f, 0.785f, 0.80f, 0.070f, 22f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.08f, 0.14f, 0.44f, 0.72f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.KINETIC_SLICED_LOUVERS,
                        x = 0.05f, y = 0.12f, w = 0.50f, h = 0.76f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        layer = DecorLayer.BEHIND_PHOTO, panel = true
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.58f, 0.20f, 0.36f, 0.046f, 20f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.58f, 0.27f, 0.36f, 0.34f, 62f, PostFont.DISPLAY, PostTone.TEXT, caps = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.58f, 0.64f, 0.36f, 0.18f, 22f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.48f, 0.10f, 0.44f, 0.80f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.KINETIC_SLICED_LOUVERS,
                        x = 0.44f, y = 0.09f, w = 0.50f, h = 0.82f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        layer = DecorLayer.BEHIND_PHOTO, panel = true
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.35f, 0.050f, 20f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.26f, 0.35f, 0.36f, 64f, PostFont.DISPLAY, PostTone.TEXT, caps = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.66f, 0.35f, 0.17f, 23f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
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
                    SlotBox(PostSlots.WORD, 0.20f, 0.20f, 0.60f, 0.08f, 28f, PostFont.DISPLAY, PostTone.ACCENT, centered = true, caps = true, fx = TextFx.CURVE_ARC),
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
                    SlotBox(PostSlots.WORD, 0.24f, 0.12f, 0.52f, 0.10f, 32f, PostFont.DISPLAY, PostTone.ACCENT, centered = true, caps = true, fx = TextFx.CURVE_ARC),
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
                    SlotBox(PostSlots.WORD, 0.52f, 0.10f, 0.34f, 0.10f, 30f, PostFont.DISPLAY, PostTone.ACCENT, centered = true, caps = true, fx = TextFx.CURVE_ARC),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.28f, 0.36f, 0.36f, 56f, PostFont.DISPLAY, PostTone.TEXT, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.68f, 0.36f, 0.16f, 26f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 9. 35mm Contact-Sheet Pop-Out (scn-examcliff) — Batch 3 replacement (3 photo slots + pop-out cutout)
    PostTemplateSpec(
        id = "scn-examcliff",
        name = "35mm Contact-Sheet",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "35MM ARCHIVE · CAMPUS REEL",
            PostSlots.HEADLINE to "EVERY FRAME TELLS THE STORY.",
            PostSlots.BODY to "Load 1 to 3 photos into the angled 35mm cinema strip and layer your full-color BG-removed cutout right in front."
        ),
        looks = listOf(
            TemplateLook("Kodak amber", "#F59E0B", "#080A10", PostBase.DARK),
            TemplateLook("Cinema cobalt", "#38BDF8", "#050A14", PostBase.DARK),
            TemplateLook("Emerald reel", "#10B981", "#050F0C", PostBase.DARK),
            TemplateLook("Contact paper", "#D97706", "#F7F3EA", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.20f, 0.26f, 0.60f, 0.44f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.CONTACT_SHEET_FILM,
                        x = 0.04f, y = 0.28f, w = 0.92f, h = 0.27f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        layer = DecorLayer.BEHIND_PHOTO, panel = true
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.12f, 0.80f, 0.038f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.65f, 0.84f, 0.105f, 56f, PostFont.DISPLAY, PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.10f, 0.768f, 0.80f, 0.075f, 22f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.26f, 0.18f, 0.48f, 0.58f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.CONTACT_SHEET_FILM,
                        x = 0.05f, y = 0.24f, w = 0.90f, h = 0.36f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        layer = DecorLayer.BEHIND_PHOTO, panel = true
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.07f, 0.80f, 0.042f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.69f, 0.84f, 0.115f, 56f, PostFont.DISPLAY, PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.12f, 0.815f, 0.76f, 0.072f, 22f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.54f, 0.12f, 0.36f, 0.78f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.CONTACT_SHEET_FILM,
                        x = 0.56f, y = 0.06f, w = 0.32f, h = 0.88f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        layer = DecorLayer.BEHIND_PHOTO, panel = true
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.44f, 0.050f, 20f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.26f, 0.44f, 0.34f, 60f, PostFont.DISPLAY, PostTone.TEXT, caps = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.64f, 0.42f, 0.18f, 23f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 10. Luminous Eclipse Halo Stage (scn-capufo) — Batch 3 replacement (backlit solar eclipse corona behind cutout)
    PostTemplateSpec(
        id = "scn-capufo",
        name = "Luminous Eclipse Halo",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "CLASS OF EXCELLENCE · SOLAR EDITION",
            PostSlots.HEADLINE to "STEP INTO TOTAL RADIANCE.",
            PostSlots.BODY to "Insert a BG-removed graduate, scholar, or object in front of the backlit solar eclipse corona."
        ),
        looks = listOf(
            TemplateLook("Solar gold", "#F59E0B", "#05070E", PostBase.DARK),
            TemplateLook("Corona emerald", "#10B981", "#040C0A", PostBase.DARK),
            TemplateLook("Starlight cyan", "#38BDF8", "#040A16", PostBase.DARK),
            TemplateLook("Ivory eclipse", "#D97706", "#FAF6EE", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = null,
                cutout = CutoutBox(0.16f, 0.22f, 0.68f, 0.48f),
                cutoutShowDefault = true,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.ECLIPSE_HALO_STAGE,
                        x = 0.08f, y = 0.18f, w = 0.84f, h = 0.46f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        layer = DecorLayer.BEHIND_PHOTO, panel = true
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.12f, 0.80f, 0.038f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.70f, 0.84f, 0.095f, 58f, PostFont.DISPLAY, PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.10f, 0.805f, 0.80f, 0.070f, 22f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = null,
                cutout = CutoutBox(0.24f, 0.15f, 0.52f, 0.56f),
                cutoutShowDefault = true,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.ECLIPSE_HALO_STAGE,
                        x = 0.16f, y = 0.10f, w = 0.68f, h = 0.58f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        layer = DecorLayer.BEHIND_PHOTO, panel = true
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.06f, 0.80f, 0.042f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.71f, 0.84f, 0.115f, 58f, PostFont.DISPLAY, PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.12f, 0.835f, 0.76f, 0.070f, 22f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = null,
                cutout = CutoutBox(0.50f, 0.12f, 0.42f, 0.78f),
                cutoutShowDefault = true,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.ECLIPSE_HALO_STAGE,
                        x = 0.46f, y = 0.08f, w = 0.48f, h = 0.84f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        layer = DecorLayer.BEHIND_PHOTO, panel = true
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.38f, 0.050f, 20f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.26f, 0.38f, 0.34f, 62f, PostFont.DISPLAY, PostTone.TEXT, caps = true, fit = FitMode.FILL, shadow = TextShadow(65, ShadowMode.SOFT)),
                    SlotBox(PostSlots.BODY, 0.06f, 0.64f, 0.38f, 0.18f, 23f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    },

    // 11. 3D Folded Study Brochure Trifold (scn-paperboat) — 1..3 dynamic photo panels
    PostTemplateSpec(
        id = "scn-paperboat",
        name = "3D Trifold Study Guide",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "QUICK-REFERENCE CHEAT SHEETS",
            PostSlots.HEADLINE to "Every unit folded into one clear guide.",
            PostSlots.BODY to "Insert 1, 2, or 3 study sheets to unfold a 3D trifold revision brochure."
        ),
        looks = listOf(
            TemplateLook("Ocean breeze", "#38BDF8", "#071828", PostBase.DARK),
            TemplateLook("Voyage gold", "#F59E0B", "#0A1424", PostBase.DARK),
            TemplateLook("Emerald guide", "#10B981", "#071714", PostBase.DARK),
            TemplateLook("Dawn parchment", "#D97706", "#F5EFE3", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.04f, y = 0.23f, w = 0.92f, h = 0.48f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 15
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.135f, 0.76f, 0.036f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.175f, 0.84f, 0.06f, 40f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.12f, 0.74f, 0.76f, 0.042f, 23f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.06f, y = 0.19f, w = 0.88f, h = 0.67f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 15
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.055f, 0.80f, 0.04f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.105f, 0.84f, 0.085f, 44f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.14f, 0.885f, 0.72f, 0.05f, 23f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.42f, y = 0.07f, w = 0.54f, h = 0.86f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 15
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.18f, 0.34f, 0.05f, 20f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.27f, 0.34f, 0.36f, 52f, PostFont.DISPLAY, PostTone.TEXT, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.06f, 0.67f, 0.34f, 0.16f, 25f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL)
                )
            )
        )
    }
)
