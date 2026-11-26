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

    // 7. 3D Floating Multiple-Choice Exam Card + Instant Explanation Card (scn-backpackportal) — 2 photo slots
    PostTemplateSpec(
        id = "scn-backpackportal",
        name = "3D Exam Quiz Card",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "PRACTICE MODE · INSTANT FEEDBACK",
            PostSlots.HEADLINE to "Don't just guess.\nSee why it's right.",
            PostSlots.BODY to "Every multiple-choice question includes step-by-step diagrams and explanations."
        ),
        looks = listOf(
            TemplateLook("Cobalt exam", "#38BDF8", "#071830", PostBase.DARK),
            TemplateLook("Explorer gold", "#F59E0B", "#0C121E", PostBase.DARK),
            TemplateLook("Campus teal", "#10B981", "#071718", PostBase.DARK),
            TemplateLook("Light exam", "#2563EB", "#F4F7FC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.04f, y = 0.20f, w = 0.92f, h = 0.46f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 12
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
                        x = 0.06f, y = 0.12f, w = 0.88f, h = 0.60f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 12
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
                        variant = 12
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

    // 9. 3D Optical Answer Sheet (Bubble Scantron) + Score Trophy Card (scn-examcliff) — 2 photo slots
    PostTemplateSpec(
        id = "scn-examcliff",
        name = "3D Bubble Answer Sheet",
        family = "Scene",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "MOCK EXAMS · AUTO-GRADED",
            PostSlots.HEADLINE to "Cross from\ndoubt to A+",
            PostSlots.BODY to "Practice real national exam answer sheets with instant score analytics."
        ),
        looks = listOf(
            TemplateLook("Canyon amber", "#F59E0B", "#071830", PostBase.DARK),
            TemplateLook("Summit cobalt", "#38BDF8", "#0A1222", PostBase.DARK),
            TemplateLook("Emerald score", "#10B981", "#071714", PostBase.DARK),
            TemplateLook("Daylight exam", "#2563EB", "#F5F7FC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.04f, y = 0.20f, w = 0.92f, h = 0.46f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 13
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
                        x = 0.06f, y = 0.12f, w = 0.88f, h = 0.60f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 13
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
                        variant = 13
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

    // 10. 3D Graduation Cap (Mortarboard) on Hardcover Book Pedestal + Showcase Frame (scn-capufo) — 2 photo slots
    PostTemplateSpec(
        id = "scn-capufo",
        name = "3D Graduation Pedestal",
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
            TemplateLook("Starlight cobalt", "#38BDF8", "#071830", PostBase.DARK),
            TemplateLook("Ivory honors", "#D97706", "#FAF6EE", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.04f, y = 0.20f, w = 0.92f, h = 0.46f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 14
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
                        x = 0.06f, y = 0.12f, w = 0.88f, h = 0.60f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 14
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
                        variant = 14
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
