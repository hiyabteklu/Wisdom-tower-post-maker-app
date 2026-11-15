package com.wisdomtower.post

/**
 * Family 4 — Information & Teaching (10 templates: 7 new designs: info-definition, info-stat,
 * info-tip, info-versus, info-formula, info-quote, info-qa + 3 kept designs: info-quiz, info-checklist, info-timetable).
 * Built for 4:3, 9:16, and 16:9 using PostSize.pick3.
 */
internal val InfoTemplates: List<PostTemplateSpec> = listOf(
    // 1. info-definition — Dictionary card
    PostTemplateSpec(
        id = "info-definition",
        name = "Dictionary card",
        family = "Information",
        slots = listOf(PostSlots.WORD, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.LIST),
        sample = mapOf(
            PostSlots.WORD to "Epistemology",
            PostSlots.EYEBROW to "/ɪˌpɪs.təˈmɒl.ə.dʒi/ · NOUN",
            PostSlots.HEADLINE to "The branch of philosophy concerned with the nature, origin, and limits of human knowledge.",
            PostSlots.BODY to "From Greek epistēmē ('knowledge') + logos ('study of').",
            PostSlots.LIST to "How do we know what is true?\nWhat separates belief from knowledge?"
        ),
        looks = listOf(
            TemplateLook("Oxford paper", "#B45309", "#FAF6EE", PostBase.LIGHT),
            TemplateLook("Night lexicon", "#F59E0B", "#080B14", PostBase.DARK),
            TemplateLook("Cobalt lexicon", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.11f, 0.88f, 0.78f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.LINE, 0.11f, 0.34f, 0.78f, 0.002f, PostTone.TEXT, alpha = 90, panel = true),
                    DecorBox(DecorShape.BAR, 0.11f, 0.34f, 0.22f, 0.006f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.11f, y = 0.15f, w = 0.78f, h = 0.11f,
                        size = 76f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.BOTTOM
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.11f, y = 0.27f, w = 0.78f, h = 0.045f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER, tracking = 0.06f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.11f, y = 0.38f, w = 0.78f, h = 0.18f,
                        size = 38f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.12f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.11f, y = 0.58f, w = 0.78f, h = 0.09f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.11f, y = 0.69f, w = 0.78f, h = 0.16f,
                        size = 24f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.NUMBER, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.26f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.10f, 0.88f, 0.80f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.LINE, 0.10f, 0.35f, 0.80f, 0.002f, PostTone.TEXT, alpha = 90, panel = true),
                    DecorBox(DecorShape.BAR, 0.10f, 0.35f, 0.20f, 0.007f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.10f, y = 0.14f, w = 0.80f, h = 0.13f,
                        size = 76f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.BOTTOM
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.28f, w = 0.80f, h = 0.05f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER, tracking = 0.06f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.39f, w = 0.80f, h = 0.19f,
                        size = 36f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.10f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.60f, w = 0.80f, h = 0.09f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.10f, y = 0.71f, w = 0.80f, h = 0.15f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.NUMBER, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.11f, 0.90f, 0.78f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.LINE, 0.44f, 0.16f, 0.002f, 0.68f, PostTone.TEXT, alpha = 90, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.08f, y = 0.22f, w = 0.33f, h = 0.22f,
                        size = 74f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.BOTTOM
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.46f, w = 0.33f, h = 0.06f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER, tracking = 0.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.08f, y = 0.56f, w = 0.33f, h = 0.22f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.48f, y = 0.20f, w = 0.43f, h = 0.32f,
                        size = 38f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.10f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.48f, y = 0.56f, w = 0.43f, h = 0.26f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.NUMBER, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            )
        )
    },

    // 2. info-stat — Big stat + breakdown
    PostTemplateSpec(
        id = "info-stat",
        name = "Big stat + breakdown",
        family = "Information",
        slots = listOf(PostSlots.NUMBER, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.LIST),
        sample = mapOf(
            PostSlots.NUMBER to "94%",
            PostSlots.EYEBROW to "ANNUAL STUDENT OUTCOMES",
            PostSlots.HEADLINE to "Active recall outperforms passive re-reading across every subject.",
            PostSlots.BODY to "Students who self-test retain more after 30 days.",
            PostSlots.LIST to "Practice questions (+42%)\nSpaced review (+31%)\nPeer teaching (+21%)"
        ),
        looks = listOf(
            TemplateLook("Data gold", "#F59E0B", "#070A12", PostBase.DARK),
            TemplateLook("Emerald metrics", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Report light", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.11f, 0.88f, 0.36f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.50f, 0.88f, 0.39f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.50f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.11f, y = 0.14f, w = 0.78f, h = 0.04f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.10f, y = 0.19f, w = 0.80f, h = 0.25f,
                        size = 280f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.GRADIENT, fxTo = PostTone.GLOW
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.11f, y = 0.54f, w = 0.78f, h = 0.12f,
                        size = 38f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.11f, y = 0.67f, w = 0.78f, h = 0.06f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.11f, y = 0.74f, w = 0.78f, h = 0.13f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.12f, 0.40f, 0.76f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.CARD, 0.49f, 0.12f, 0.45f, 0.76f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.49f, 0.12f, 0.45f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.09f, y = 0.17f, w = 0.34f, h = 0.05f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.08f, y = 0.25f, w = 0.36f, h = 0.42f,
                        size = 260f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.GRADIENT, fxTo = PostTone.GLOW
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.09f, y = 0.70f, w = 0.34f, h = 0.14f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.53f, y = 0.18f, w = 0.37f, h = 0.28f,
                        size = 38f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.53f, y = 0.50f, w = 0.37f, h = 0.34f,
                        size = 24f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.26f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.12f, 0.38f, 0.76f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.CARD, 0.46f, 0.12f, 0.49f, 0.76f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.46f, 0.12f, 0.49f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.17f, w = 0.32f, h = 0.05f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.07f, y = 0.25f, w = 0.34f, h = 0.42f,
                        size = 260f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.GRADIENT, fxTo = PostTone.GLOW
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.08f, y = 0.70f, w = 0.32f, h = 0.14f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.50f, y = 0.18f, w = 0.41f, h = 0.28f,
                        size = 40f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.50f, y = 0.50f, w = 0.41f, h = 0.34f,
                        size = 24f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.26f
                    )
                )
            )
        )
    },

    // 3. info-tip — Numbered tip card
    PostTemplateSpec(
        id = "info-tip",
        name = "Numbered tip card",
        family = "Information",
        slots = listOf(PostSlots.NUMBER, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.LIST),
        sample = mapOf(
            PostSlots.NUMBER to "07",
            PostSlots.EYEBROW to "STUDY HABIT",
            PostSlots.HEADLINE to "Write a 3-sentence summary at the end of every page.",
            PostSlots.BODY to "If you cannot explain the page simply, re-read the key paragraph before turning it.",
            PostSlots.LIST to "Read one section\nClose the book\nWrite 3 clear lines"
        ),
        looks = listOf(
            TemplateLook("Gold badge", "#F59E0B", "#080C15", PostBase.DARK),
            TemplateLook("Mint badge", "#10B981", "#06110D", PostBase.DARK),
            TemplateLook("Clean notebook", "#EA580C", "#FAF6EE", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.14f, 0.88f, 0.75f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.PILL, 0.11f, 0.10f, 0.22f, 0.09f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.12f, y = 0.105f, w = 0.20f, h = 0.08f,
                        size = 64f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.36f, y = 0.155f, w = 0.52f, h = 0.04f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.11f, y = 0.23f, w = 0.78f, h = 0.20f,
                        size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.11f, y = 0.45f, w = 0.78f, h = 0.13f,
                        size = 25f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.11f, y = 0.61f, w = 0.78f, h = 0.24f,
                        size = 26f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.NUMBER, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.28f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.15f, 0.88f, 0.74f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.PILL, 0.10f, 0.10f, 0.18f, 0.11f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.11f, y = 0.105f, w = 0.16f, h = 0.10f,
                        size = 64f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.31f, y = 0.17f, w = 0.56f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.25f, w = 0.80f, h = 0.20f,
                        size = 44f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.47f, w = 0.80f, h = 0.12f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.10f, y = 0.61f, w = 0.80f, h = 0.24f,
                        size = 24f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.NUMBER, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.25f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.15f, 0.90f, 0.74f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.PILL, 0.09f, 0.10f, 0.15f, 0.12f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.10f, y = 0.105f, w = 0.13f, h = 0.11f,
                        size = 64f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.27f, y = 0.18f, w = 0.40f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.09f, y = 0.27f, w = 0.44f, h = 0.34f,
                        size = 44f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.09f, y = 0.64f, w = 0.44f, h = 0.20f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.57f, y = 0.27f, w = 0.34f, h = 0.56f,
                        size = 25f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.NUMBER, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.28f
                    )
                )
            )
        )
    },

    // 4. info-versus — Myth vs Fact / Before vs After
    PostTemplateSpec(
        id = "info-versus",
        name = "Myth vs Fact",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.LIST),
        sample = mapOf(
            PostSlots.EYEBROW to "MYTH VS FACT",
            PostSlots.HEADLINE to "Highlighting whole pages is the fastest way to study.",
            PostSlots.BODY to "Testing yourself from memory builds durable neural pathways.",
            PostSlots.LIST to "Close the book after reading\nWrite down 3 key ideas\nCheck what you missed"
        ),
        looks = listOf(
            TemplateLook("Gold vs Slate", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Emerald vs Ink", "#10B981", "#050E0B", PostBase.DARK),
            TemplateLook("Paper contrast", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.16f, 0.88f, 0.32f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.16f, 0.88f, 0.008f, PostTone.RED, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.51f, 0.88f, 0.38f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.51f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.10f, w = 0.88f, h = 0.042f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = "myth_tag",
                        x = 0.10f, y = 0.19f, w = 0.24f, h = 0.038f,
                        size = 18f, font = PostFont.BOLD, tone = PostTone.LIGHT,
                        centered = true, caps = true, literal = true, fit = FitMode.FILL_WIDTH, chip = PostTone.RED
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.25f, w = 0.80f, h = 0.20f,
                        size = 38f, font = PostFont.DISPLAY, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.08f
                    ),
                    SlotBox(
                        slot = "fact_tag",
                        x = 0.10f, y = 0.54f, w = 0.24f, h = 0.038f,
                        size = 18f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, literal = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.60f, w = 0.80f, h = 0.12f,
                        size = 34f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.10f, y = 0.74f, w = 0.80f, h = 0.13f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.17f, 0.42f, 0.71f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.17f, 0.42f, 0.010f, PostTone.RED, panel = true),
                    DecorBox(DecorShape.CARD, 0.52f, 0.17f, 0.42f, 0.71f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.52f, 0.17f, 0.42f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.10f, w = 0.88f, h = 0.048f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.24f, w = 0.34f, h = 0.58f,
                        size = 38f, font = PostFont.DISPLAY, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.56f, y = 0.24f, w = 0.34f, h = 0.28f,
                        size = 34f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.56f, y = 0.55f, w = 0.34f, h = 0.29f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.18f, 0.43f, 0.70f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.05f, 0.18f, 0.43f, 0.010f, PostTone.RED, panel = true),
                    DecorBox(DecorShape.CARD, 0.52f, 0.18f, 0.43f, 0.70f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.52f, 0.18f, 0.43f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.05f, y = 0.10f, w = 0.90f, h = 0.05f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.09f, y = 0.25f, w = 0.35f, h = 0.56f,
                        size = 40f, font = PostFont.DISPLAY, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.56f, y = 0.25f, w = 0.35f, h = 0.26f,
                        size = 34f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.56f, y = 0.54f, w = 0.35f, h = 0.30f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            )
        )
    },

    // 5. info-formula — Formula / rule card
    PostTemplateSpec(
        id = "info-formula",
        name = "Formula card",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.WORD, PostSlots.HEADLINE, PostSlots.LIST),
        sample = mapOf(
            PostSlots.EYEBROW to "PHYSICS · MECHANICS",
            PostSlots.WORD to "E = mc²",
            PostSlots.HEADLINE to "Mass-energy equivalence links rest mass directly to total energy.",
            PostSlots.LIST to "E — Total energy (Joules)\nm — Rest mass (kg)\nc — Speed of light (3×10⁸ m/s)"
        ),
        looks = listOf(
            TemplateLook("Chalkboard gold", "#F59E0B", "#070B12", PostBase.DARK),
            TemplateLook("Lab paper", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Emerald theorem", "#10B981", "#05100C", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.16f, 0.88f, 0.28f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.FRAME, 0.06f, 0.16f, 0.88f, 0.28f, PostTone.ACCENT, alpha = 210, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.47f, 0.88f, 0.42f, PostTone.DEEP, alpha = 235, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.10f, w = 0.88f, h = 0.042f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.10f, y = 0.19f, w = 0.80f, h = 0.22f,
                        size = 130f, font = PostFont.SERIF, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.11f, y = 0.51f, w = 0.78f, h = 0.12f,
                        size = 34f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.11f, y = 0.65f, w = 0.78f, h = 0.21f,
                        size = 25f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        list = ListStyle.DOT, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.26f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.16f, 0.88f, 0.30f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.FRAME, 0.06f, 0.16f, 0.88f, 0.30f, PostTone.ACCENT, alpha = 210, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.49f, 0.88f, 0.40f, PostTone.DEEP, alpha = 235, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.10f, w = 0.88f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.10f, y = 0.19f, w = 0.80f, h = 0.24f,
                        size = 130f, font = PostFont.SERIF, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.53f, w = 0.80f, h = 0.11f,
                        size = 32f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.10f, y = 0.66f, w = 0.80f, h = 0.20f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        list = ListStyle.DOT, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.16f, 0.42f, 0.72f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.FRAME, 0.05f, 0.16f, 0.42f, 0.72f, PostTone.ACCENT, alpha = 210, panel = true),
                    DecorBox(DecorShape.CARD, 0.50f, 0.16f, 0.45f, 0.72f, PostTone.DEEP, alpha = 235, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.21f, w = 0.36f, h = 0.05f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.08f, y = 0.30f, w = 0.36f, h = 0.48f,
                        size = 130f, font = PostFont.SERIF, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.54f, y = 0.22f, w = 0.37f, h = 0.24f,
                        size = 34f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.54f, y = 0.50f, w = 0.37f, h = 0.34f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        list = ListStyle.DOT, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            )
        )
    },

    // 6. info-quote — Editorial pull-quote
    PostTemplateSpec(
        id = "info-quote",
        name = "Editorial pull-quote",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "TIMELESS WISDOM",
            PostSlots.HEADLINE to "Education is not the filling of a pail, but the lighting of a fire.",
            PostSlots.BODY to "— William Butler Yeats"
        ),
        looks = listOf(
            TemplateLook("Literary paper", "#B45309", "#FAF5EC", PostBase.LIGHT),
            TemplateLook("Midnight gold", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Sage classic", "#10B981", "#06110D", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                decor = listOf(
                    DecorBox(DecorShape.QUOTE, 0.08f, 0.14f, 0.24f, 0.14f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.BAR, 0.08f, 0.74f, 0.20f, 0.006f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.11f, w = 0.60f, h = 0.04f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.26f, w = 0.84f, h = 0.44f,
                        size = 64f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.08f, y = 0.77f, w = 0.84f, h = 0.08f,
                        size = 26f, font = PostFont.BOLD, tone = PostTone.MUTED,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                decor = listOf(
                    DecorBox(DecorShape.QUOTE, 0.08f, 0.14f, 0.20f, 0.16f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.BAR, 0.08f, 0.74f, 0.18f, 0.007f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.10f, w = 0.50f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.26f, w = 0.84f, h = 0.44f,
                        size = 60f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.08f, y = 0.77f, w = 0.84f, h = 0.08f,
                        size = 25f, font = PostFont.BOLD, tone = PostTone.MUTED,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                decor = listOf(
                    DecorBox(DecorShape.QUOTE, 0.07f, 0.15f, 0.16f, 0.18f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.BAR, 0.07f, 0.75f, 0.15f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.10f, w = 0.45f, h = 0.048f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.27f, w = 0.86f, h = 0.44f,
                        size = 62f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.07f, y = 0.78f, w = 0.86f, h = 0.08f,
                        size = 24f, font = PostFont.BOLD, tone = PostTone.MUTED,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.TOP
                    )
                )
            )
        )
    },

    // 7. info-qa — Question & answer
    PostTemplateSpec(
        id = "info-qa",
        name = "Question & answer",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "STUDENT Q&A",
            PostSlots.HEADLINE to "How long should a focused study block last before taking a break?",
            PostSlots.BODY to "Aim for 45 to 50 minutes of zero-distraction study followed by a 10-minute walk away from screens."
        ),
        looks = listOf(
            TemplateLook("Gold Q&A", "#F59E0B", "#080C14", PostBase.DARK),
            TemplateLook("Mint Q&A", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Paper Q&A", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.16f, 0.88f, 0.34f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.53f, 0.88f, 0.36f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.53f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.10f, w = 0.88f, h = 0.042f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = "Q",
                        x = 0.10f, y = 0.19f, w = 0.14f, h = 0.10f,
                        size = 86f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, literal = true, fit = FitMode.FILL
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.26f, y = 0.20f, w = 0.64f, h = 0.26f,
                        size = 40f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.06f
                    ),
                    SlotBox(
                        slot = "A",
                        x = 0.10f, y = 0.57f, w = 0.14f, h = 0.10f,
                        size = 86f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, literal = true, fit = FitMode.FILL
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.26f, y = 0.58f, w = 0.64f, h = 0.27f,
                        size = 28f, font = PostFont.REGULAR, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.22f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.16f, 0.88f, 0.34f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.53f, 0.88f, 0.36f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.53f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.10f, w = 0.88f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = "Q",
                        x = 0.09f, y = 0.19f, w = 0.12f, h = 0.12f,
                        size = 86f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, literal = true, fit = FitMode.FILL
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.23f, y = 0.19f, w = 0.67f, h = 0.28f,
                        size = 38f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.06f
                    ),
                    SlotBox(
                        slot = "A",
                        x = 0.09f, y = 0.57f, w = 0.12f, h = 0.12f,
                        size = 86f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, literal = true, fit = FitMode.FILL
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.23f, y = 0.57f, w = 0.67f, h = 0.28f,
                        size = 26f, font = PostFont.REGULAR, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.20f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.18f, 0.43f, 0.70f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.CARD, 0.52f, 0.18f, 0.43f, 0.70f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.52f, 0.18f, 0.43f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.05f, y = 0.10f, w = 0.90f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = "Q",
                        x = 0.08f, y = 0.22f, w = 0.12f, h = 0.14f,
                        size = 88f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, literal = true, fit = FitMode.FILL
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.38f, w = 0.37f, h = 0.45f,
                        size = 38f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = "A",
                        x = 0.55f, y = 0.22f, w = 0.12f, h = 0.14f,
                        size = 88f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, literal = true, fit = FitMode.FILL
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.55f, y = 0.38f, w = 0.37f, h = 0.45f,
                        size = 26f, font = PostFont.REGULAR, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    )
                )
            )
        )
    },

    // 8. KEPT: info-quiz — Multiple choice
    PostTemplateSpec(
        id = "info-quiz",
        name = "Multiple choice",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.LIST, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "DAILY QUIZ",
            PostSlots.HEADLINE to "Which organelle is responsible for generating most of the cell's ATP?",
            PostSlots.LIST to "Ribosome\nMitochondrion\nGolgi apparatus\nEndoplasmic reticulum",
            PostSlots.BUTTON to "Comment A, B, C or D"
        ),
        looks = listOf(
            TemplateLook("Quiz gold", "#F59E0B", "#080B14", PostBase.DARK),
            TemplateLook("Classroom light", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Emerald quiz", "#10B981", "#05100C", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.15f, 0.88f, 0.24f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.42f, 0.88f, 0.38f, PostTone.DEEP, alpha = 235, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.09f, w = 0.42f, h = 0.042f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.18f, w = 0.80f, h = 0.18f,
                        size = 38f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.10f, y = 0.45f, w = 0.80f, h = 0.32f,
                        size = 27f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.LETTER, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.32f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.18f, y = 0.82f, w = 0.64f, h = 0.055f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.16f, 0.88f, 0.24f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.43f, 0.88f, 0.36f, PostTone.DEEP, alpha = 235, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.09f, w = 0.36f, h = 0.048f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.19f, w = 0.80f, h = 0.18f,
                        size = 36f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.10f, y = 0.46f, w = 0.80f, h = 0.30f,
                        size = 25f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.LETTER, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.28f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.24f, y = 0.81f, w = 0.52f, h = 0.065f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.18f, 0.44f, 0.68f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.CARD, 0.52f, 0.18f, 0.43f, 0.68f, PostTone.DEEP, alpha = 235, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.05f, y = 0.10f, w = 0.28f, h = 0.055f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.09f, y = 0.24f, w = 0.36f, h = 0.40f,
                        size = 36f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.09f, y = 0.70f, w = 0.36f, h = 0.09f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.56f, y = 0.24f, w = 0.35f, h = 0.56f,
                        size = 26f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.LETTER, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.32f
                    )
                )
            )
        )
    },

    // 9. KEPT: info-checklist — Checklist
    PostTemplateSpec(
        id = "info-checklist",
        name = "Checklist",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.LIST, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "EXAM READINESS",
            PostSlots.HEADLINE to "5 things to pack the night before your exam",
            PostSlots.LIST to "Admission card & ID\nTwo black pens & pencils\nApproved calculator\nWater bottle\nWatch (no smartwatches)",
            PostSlots.BODY to "Save this checklist and share it with your study group."
        ),
        looks = listOf(
            TemplateLook("Gold check", "#F59E0B", "#080C14", PostBase.DARK),
            TemplateLook("Emerald check", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Paper check", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.12f, 0.88f, 0.76f, PostTone.SOFT, alpha = 238, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.12f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.11f, y = 0.16f, w = 0.50f, h = 0.04f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.11f, y = 0.22f, w = 0.78f, h = 0.14f,
                        size = 42f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.11f, y = 0.39f, w = 0.78f, h = 0.37f,
                        size = 27f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.32f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.11f, y = 0.79f, w = 0.78f, h = 0.07f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.11f, 0.88f, 0.78f, PostTone.SOFT, alpha = 238, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.11f, 0.88f, 0.009f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.15f, w = 0.45f, h = 0.045f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.21f, w = 0.80f, h = 0.14f,
                        size = 40f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.10f, y = 0.38f, w = 0.80f, h = 0.38f,
                        size = 25f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.28f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.79f, w = 0.80f, h = 0.07f,
                        size = 21f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.12f, 0.90f, 0.76f, PostTone.SOFT, alpha = 238, panel = true),
                    DecorBox(DecorShape.BAR, 0.05f, 0.12f, 0.90f, 0.009f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.09f, y = 0.18f, w = 0.36f, h = 0.05f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.09f, y = 0.26f, w = 0.38f, h = 0.34f,
                        size = 42f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.09f, y = 0.64f, w = 0.38f, h = 0.18f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.51f, y = 0.20f, w = 0.40f, h = 0.62f,
                        size = 26f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.32f
                    )
                )
            )
        )
    },

    // 10. KEPT: info-timetable — Timetable
    PostTemplateSpec(
        id = "info-timetable",
        name = "Timetable",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.LIST, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "WEEKLY SCHEDULE",
            PostSlots.HEADLINE to "Live Revision Sessions",
            PostSlots.LIST to "Mon 19:00 · Mathematics\nWed 19:00 · Physics\nFri 19:00 · Chemistry\nSat 10:00 · Mock Exam Review",
            PostSlots.BODY to "All sessions stream live on Telegram and YouTube."
        ),
        looks = listOf(
            TemplateLook("Schedule gold", "#F59E0B", "#080B14", PostBase.DARK),
            TemplateLook("Schedule paper", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Schedule mint", "#10B981", "#05100C", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.12f, 0.88f, 0.76f, PostTone.SOFT, alpha = 238, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.12f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.11f, y = 0.16f, w = 0.50f, h = 0.04f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.11f, y = 0.22f, w = 0.78f, h = 0.11f,
                        size = 44f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.11f, y = 0.36f, w = 0.78f, h = 0.39f,
                        size = 27f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.ROW, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.35f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.11f, y = 0.78f, w = 0.78f, h = 0.07f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.11f, 0.88f, 0.78f, PostTone.SOFT, alpha = 238, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.11f, 0.88f, 0.009f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.15f, w = 0.45f, h = 0.045f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.21f, w = 0.80f, h = 0.11f,
                        size = 40f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.10f, y = 0.35f, w = 0.80f, h = 0.41f,
                        size = 25f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.ROW, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.32f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.79f, w = 0.80f, h = 0.07f,
                        size = 21f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.12f, 0.90f, 0.76f, PostTone.SOFT, alpha = 238, panel = true),
                    DecorBox(DecorShape.BAR, 0.05f, 0.12f, 0.90f, 0.009f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.09f, y = 0.18f, w = 0.34f, h = 0.05f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.09f, y = 0.26f, w = 0.36f, h = 0.32f,
                        size = 42f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.09f, y = 0.62f, w = 0.36f, h = 0.20f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.49f, y = 0.20f, w = 0.42f, h = 0.62f,
                        size = 25f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.ROW, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.34f
                    )
                )
            )
        )
    }
)
