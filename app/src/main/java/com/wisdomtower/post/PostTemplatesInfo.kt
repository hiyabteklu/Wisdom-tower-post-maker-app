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

    // 3. info-bignumber — Big Number Hook (replaces info-tip)
    PostTemplateSpec(
        id = "info-bignumber",
        name = "Big Number Hook",
        family = "Information",
        slots = listOf(PostSlots.NUMBER, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.NUMBER to "01",
            PostSlots.EYEBROW to "TIP OF THE DAY",
            PostSlots.HEADLINE to "ACTIVE RECALL",
            PostSlots.BODY to "TEST BEFORE RE-READING"
        ),
        looks = listOf(
            TemplateLook("Gold badge", "#F59E0B", "#080C15", PostBase.DARK),
            TemplateLook("Mint badge", "#10B981", "#06110D", PostBase.DARK),
            TemplateLook("Clean notebook", "#EA580C", "#FAF6EE", PostBase.LIGHT),
            TemplateLook("Electric violet", "#A855F7", "#090614", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.RING, 0.14f, 0.14f, 0.72f, 0.42f, PostTone.ACCENT, alpha = 190),
                    DecorBox(DecorShape.MONUMENT_SLAB, 0.18f, 0.16f, 0.64f, 0.38f, PostTone.ACCENT, secondaryTone = PostTone.DEEP, alpha = 235),
                    DecorBox(DecorShape.PILL, 0.24f, 0.80f, 0.52f, 0.055f, PostTone.SOFT, alpha = 230, panel = true)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.20f, 0.09f, 0.60f, 0.042f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.NUMBER, 0.16f, 0.16f, 0.68f, 0.38f, size = 340f, font = PostFont.CONDENSED, tone = PostTone.ACCENT, centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.EXTRUDE, fxTo = PostTone.DARK),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.58f, 0.84f, 0.18f, size = 110f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f, shadow = TextShadow(strength = 75, mode = ShadowMode.GLOW)),
                    SlotBox(PostSlots.BODY, 0.26f, 0.805f, 0.48f, 0.045f, size = 22f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER, maxLines = 1)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.MONUMENT_SLAB, 0.06f, 0.14f, 0.42f, 0.72f, PostTone.ACCENT, secondaryTone = PostTone.DEEP, alpha = 235),
                    DecorBox(DecorShape.BAR, 0.52f, 0.56f, 0.18f, 0.010f, PostTone.ACCENT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.NUMBER, 0.08f, 0.16f, 0.38f, 0.68f, size = 340f, font = PostFont.CONDENSED, tone = PostTone.ACCENT, centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.EXTRUDE, fxTo = PostTone.DARK),
                    SlotBox(PostSlots.EYEBROW, 0.52f, 0.22f, 0.40f, 0.05f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.52f, 0.29f, 0.42f, h = 0.24f, size = 96f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BODY, 0.52f, 0.61f, 0.42f, 0.10f, size = 26f, font = PostFont.BOLD, tone = PostTone.MUTED, caps = true, fit = FitMode.FILL_WIDTH, anchor = VAnchor.TOP)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.MONUMENT_SLAB, 0.05f, 0.12f, 0.38f, 0.76f, PostTone.ACCENT, secondaryTone = PostTone.DEEP, alpha = 235),
                    DecorBox(DecorShape.BAR, 0.48f, 0.58f, 0.16f, 0.012f, PostTone.ACCENT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.NUMBER, 0.07f, 0.14f, 0.34f, 0.72f, size = 340f, font = PostFont.CONDENSED, tone = PostTone.ACCENT, centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.EXTRUDE, fxTo = PostTone.DARK),
                    SlotBox(PostSlots.EYEBROW, 0.48f, 0.20f, 0.44f, 0.055f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.48f, 0.28f, 0.46f, 0.26f, size = 104f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BODY, 0.48f, 0.62f, 0.44f, 0.10f, size = 26f, font = PostFont.BOLD, tone = PostTone.MUTED, caps = true, fit = FitMode.FILL_WIDTH, anchor = VAnchor.TOP)
                )
            )
        )
    },

    // 4. info-strikereveal — Strike and Reveal (replaces info-versus)
    PostTemplateSpec(
        id = "info-strikereveal",
        name = "Strike and Reveal",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.WORD, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "MYTH VS TRUTH",
            PostSlots.WORD to "RE-READING",
            PostSlots.HEADLINE to "SELF-TESTING",
            PostSlots.BODY to "3X MEMORY RETENTION"
        ),
        looks = listOf(
            TemplateLook("Gold vs Slate", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Emerald vs Ink", "#10B981", "#050E0B", PostBase.DARK),
            TemplateLook("Paper contrast", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Signal red", "#EF4444", "#0B0608", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.DIAG,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.08f, 0.18f, 0.84f, 0.26f, PostTone.DEEP, alpha = 225, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.48f, 0.88f, 0.34f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.FRAME, 0.06f, 0.48f, 0.88f, 0.34f, PostTone.ACCENT, alpha = 220)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.20f, 0.10f, 0.60f, 0.045f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.WORD, 0.12f, 0.21f, 0.76f, 0.20f, size = 130f, font = PostFont.CONDENSED, tone = PostTone.MUTED, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.STRIKE_REVEAL, fxTo = PostTone.RED),
                    SlotBox(PostSlots.HEADLINE, 0.10f, 0.51f, 0.80f, 0.20f, size = 140f, font = PostFont.DISPLAY, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.GRADIENT, fxTo = PostTone.GLOW, shadow = TextShadow(strength = 80, mode = ShadowMode.GLOW)),
                    SlotBox(PostSlots.BODY, 0.20f, 0.73f, 0.60f, 0.055f, size = 22f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.DIAG,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.18f, 0.88f, 0.26f, PostTone.DEEP, alpha = 225, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.48f, 0.88f, 0.36f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.FRAME, 0.06f, 0.48f, 0.88f, 0.36f, PostTone.ACCENT, alpha = 220)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.24f, 0.10f, 0.52f, 0.048f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.WORD, 0.10f, 0.20f, 0.80f, 0.22f, size = 130f, font = PostFont.CONDENSED, tone = PostTone.MUTED, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.STRIKE_REVEAL, fxTo = PostTone.RED),
                    SlotBox(PostSlots.HEADLINE, 0.10f, 0.51f, 0.80f, 0.20f, size = 140f, font = PostFont.DISPLAY, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.GRADIENT, fxTo = PostTone.GLOW, shadow = TextShadow(strength = 80, mode = ShadowMode.GLOW)),
                    SlotBox(PostSlots.BODY, 0.26f, 0.74f, 0.48f, 0.062f, size = 22f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.DIAG,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.18f, 0.42f, 0.68f, PostTone.DEEP, alpha = 225, panel = true),
                    DecorBox(DecorShape.CARD, 0.51f, 0.18f, 0.44f, 0.68f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.FRAME, 0.51f, 0.18f, 0.44f, 0.68f, PostTone.ACCENT, alpha = 220)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.28f, 0.09f, 0.44f, 0.055f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.WORD, 0.08f, 0.28f, 0.36f, 0.48f, size = 120f, font = PostFont.CONDENSED, tone = PostTone.MUTED, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.STRIKE_REVEAL, fxTo = PostTone.RED),
                    SlotBox(PostSlots.HEADLINE, 0.54f, 0.26f, 0.38f, 0.36f, size = 120f, font = PostFont.DISPLAY, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.GRADIENT, fxTo = PostTone.GLOW),
                    SlotBox(PostSlots.BODY, 0.58f, 0.68f, 0.30f, 0.09f, size = 21f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT)
                )
            )
        )
    },

    // 5. info-formula — 3D Glass Formula Board (2 photo slots)
    PostTemplateSpec(
        id = "info-formula",
        name = "3D Glass Formula Board",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "PHYSICS & MATH · LAB BOARD",
            PostSlots.HEADLINE to "Master every formula & worked theorem proof.",
            PostSlots.BODY to "Insert your formula sheet on the 3D frosted glass board and your step-by-step derivation card."
        ),
        looks = listOf(
            TemplateLook("Cyan theorem", "#38BDF8", "#050B16", PostBase.DARK),
            TemplateLook("Chalkboard gold", "#F59E0B", "#070B12", PostBase.DARK),
            TemplateLook("Emerald theorem", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Lab paper", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.04f, 0.08f, 0.92f, 0.54f, PostTone.ACCENT, variant = 23, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.64f, 0.88f, 0.25f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.64f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.665f, w = 0.80f, h = 0.040f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.715f, w = 0.80f, h = 0.095f,
                        size = 44f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.815f, w = 0.80f, h = 0.055f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.10f, 0.50f, 0.78f, PostTone.ACCENT, variant = 23, panel = true),
                    DecorBox(DecorShape.CARD, 0.55f, 0.16f, 0.40f, 0.68f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.55f, 0.16f, 0.40f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.58f, y = 0.21f, w = 0.34f, h = 0.050f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.58f, y = 0.29f, w = 0.34f, h = 0.32f,
                        size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.58f, y = 0.64f, w = 0.34f, h = 0.15f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.08f, 0.48f, 0.82f, PostTone.ACCENT, variant = 23, panel = true),
                    DecorBox(DecorShape.CARD, 0.53f, 0.14f, 0.42f, 0.72f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.53f, 0.14f, 0.42f, 0.009f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.56f, y = 0.20f, w = 0.36f, h = 0.055f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.56f, y = 0.28f, w = 0.36f, h = 0.34f,
                        size = 48f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.56f, y = 0.65f, w = 0.36f, h = 0.16f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            )
        )
    },

    // 6. info-pullquote — 3D Revision Pinboard (1–4 dynamic photo slots)
    PostTemplateSpec(
        id = "info-pullquote",
        name = "3D Revision Pinboard",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "EXAM REVISION PINBOARD",
            PostSlots.HEADLINE to "PIN YOUR TOP REVISION NOTES.",
            PostSlots.BODY to "Insert 1, 2, 3, or 4 study sheets to pin onto the 3D revision board."
        ),
        looks = listOf(
            TemplateLook("Midnight gold", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Literary paper", "#B45309", "#FAF5EC", PostBase.LIGHT),
            TemplateLook("Sage classic", "#10B981", "#06110D", PostBase.DARK),
            TemplateLook("Crimson stage", "#E11D48", "#0C0609", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.04f, 0.09f, 0.92f, 0.54f, PostTone.ACCENT, variant = 24, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.65f, 0.88f, 0.24f, PostTone.DEEP, alpha = 238, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.65f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.675f, 0.80f, 0.040f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.10f, 0.72f, 0.80f, 0.095f, size = 52f, font = PostFont.DISPLAY, tone = PostTone.TEXT, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.98f),
                    SlotBox(PostSlots.BODY, 0.10f, 0.82f, 0.80f, 0.055f, size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED, fit = FitMode.FILL, anchor = VAnchor.TOP)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.10f, 0.50f, 0.78f, PostTone.ACCENT, variant = 24, panel = true),
                    DecorBox(DecorShape.BAR, 0.56f, 0.64f, 0.22f, 0.009f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.56f, 0.20f, 0.38f, 0.048f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.56f, 0.27f, 0.38f, 0.34f, size = 56f, font = PostFont.DISPLAY, tone = PostTone.TEXT, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.98f),
                    SlotBox(PostSlots.BODY, 0.56f, 0.67f, 0.38f, 0.14f, size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED, fit = FitMode.FILL, anchor = VAnchor.TOP)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.08f, 0.48f, 0.82f, PostTone.ACCENT, variant = 24, panel = true),
                    DecorBox(DecorShape.BAR, 0.54f, 0.64f, 0.20f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.54f, 0.18f, 0.40f, 0.052f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.54f, 0.26f, 0.40f, 0.35f, size = 58f, font = PostFont.DISPLAY, tone = PostTone.TEXT, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.98f),
                    SlotBox(PostSlots.BODY, 0.54f, 0.67f, 0.40f, 0.15f, size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED, fit = FitMode.FILL, anchor = VAnchor.TOP)
                )
            )
        )
    },

    // 7. info-twinphones — 3D Laptop & Study Notes (2 photo slots)
    PostTemplateSpec(
        id = "info-twinphones",
        name = "3D Laptop & Study Notes",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.WORD, PostSlots.HEADLINE),
        sample = mapOf(
            PostSlots.EYEBROW to "STUDY ANYWHERE · CROSS-DEVICE SYNC",
            PostSlots.WORD to "DESKTOP + MOBILE",
            PostSlots.HEADLINE to "SEAMLESS EXAM PREP"
        ),
        looks = listOf(
            TemplateLook("Gold sync", "#F59E0B", "#080C14", PostBase.DARK),
            TemplateLook("Mint sync", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Paper sync", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Violet sync", "#A855F7", "#090614", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.04f, 0.13f, 0.92f, 0.56f, PostTone.ACCENT, secondaryTone = PostTone.DEEP, variant = 25, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.71f, 0.88f, 0.18f, PostTone.DEEP, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.71f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.08f, 0.76f, 0.042f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.WORD, 0.10f, 0.73f, 0.80f, 0.07f, size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER),
                    SlotBox(PostSlots.HEADLINE, 0.10f, 0.81f, 0.80f, 0.06f, size = 34f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.10f, 0.52f, 0.78f, PostTone.ACCENT, secondaryTone = PostTone.DEEP, variant = 25, panel = true),
                    DecorBox(DecorShape.CARD, 0.57f, 0.22f, 0.38f, 0.54f, PostTone.DEEP, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.57f, 0.22f, 0.38f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.60f, 0.27f, 0.32f, 0.055f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.WORD, 0.60f, 0.36f, 0.32f, 0.18f, size = 50f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER),
                    SlotBox(PostSlots.HEADLINE, 0.60f, 0.57f, 0.32f, 0.14f, size = 36f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.08f, 0.50f, 0.82f, PostTone.ACCENT, secondaryTone = PostTone.DEEP, variant = 25, panel = true),
                    DecorBox(DecorShape.CARD, 0.55f, 0.20f, 0.40f, 0.58f, PostTone.DEEP, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.55f, 0.20f, 0.40f, 0.009f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.58f, 0.25f, 0.34f, 0.06f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.WORD, 0.58f, 0.35f, 0.34f, 0.19f, size = 52f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER),
                    SlotBox(PostSlots.HEADLINE, 0.58f, 0.57f, 0.34f, 0.15f, size = 36f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER)
                )
            )
        )
    },

    // 8. info-questionhero — Question Hero (replaces info-quiz)
    PostTemplateSpec(
        id = "info-questionhero",
        name = "Question Hero",
        family = "Information",
        slots = listOf(PostSlots.NUMBER, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.NUMBER to "?",
            PostSlots.EYEBROW to "DAILY CHALLENGE",
            PostSlots.HEADLINE to "POWERHOUSE OF CELL?",
            PostSlots.BUTTON to "A · B · C"
        ),
        looks = listOf(
            TemplateLook("Quiz gold", "#F59E0B", "#080B14", PostBase.DARK),
            TemplateLook("Classroom light", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Emerald quiz", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Magenta quiz", "#EC4899", "#0C0610", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.DISC, 0.18f, 0.14f, 0.64f, 0.38f, PostTone.SOFT, alpha = 225, panel = true),
                    DecorBox(DecorShape.RING, 0.15f, 0.12f, 0.70f, 0.42f, PostTone.ACCENT, alpha = 210)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.22f, 0.08f, 0.56f, 0.042f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.NUMBER, 0.24f, 0.14f, 0.52f, 0.38f, size = 380f, font = PostFont.DISPLAY, tone = PostTone.ACCENT, centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.EXTRUDE, fxTo = PostTone.DARK),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.56f, 0.84f, 0.18f, size = 104f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BUTTON, 0.20f, 0.78f, 0.60f, 0.065f, size = 30f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.DISC, 0.06f, 0.16f, 0.42f, 0.68f, PostTone.SOFT, alpha = 225, panel = true),
                    DecorBox(DecorShape.RING, 0.04f, 0.13f, 0.46f, 0.74f, PostTone.ACCENT, alpha = 210)
                ),
                slots = listOf(
                    SlotBox(PostSlots.NUMBER, 0.10f, 0.18f, 0.34f, 0.64f, size = 380f, font = PostFont.DISPLAY, tone = PostTone.ACCENT, centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.EXTRUDE, fxTo = PostTone.DARK),
                    SlotBox(PostSlots.EYEBROW, 0.54f, 0.20f, 0.38f, 0.048f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.54f, 0.28f, 0.40f, 0.32f, size = 92f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BUTTON, 0.54f, 0.66f, 0.34f, 0.085f, size = 28f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.DISC, 0.06f, 0.14f, 0.38f, 0.72f, PostTone.SOFT, alpha = 225, panel = true),
                    DecorBox(DecorShape.RING, 0.04f, 0.11f, 0.42f, 0.78f, PostTone.ACCENT, alpha = 210)
                ),
                slots = listOf(
                    SlotBox(PostSlots.NUMBER, 0.09f, 0.16f, 0.32f, 0.68f, size = 380f, font = PostFont.DISPLAY, tone = PostTone.ACCENT, centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.EXTRUDE, fxTo = PostTone.DARK),
                    SlotBox(PostSlots.EYEBROW, 0.50f, 0.20f, 0.42f, 0.052f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.50f, 0.28f, 0.44f, 0.34f, size = 96f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BUTTON, 0.50f, 0.68f, 0.32f, 0.09f, size = 28f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            )
        )
    },

    // 9. info-triplestack — Triple Stack (replaces info-checklist)
    PostTemplateSpec(
        id = "info-triplestack",
        name = "Triple Stack",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.LIST),
        sample = mapOf(
            PostSlots.EYEBROW to "EXAM READY",
            PostSlots.HEADLINE to "PACK TONIGHT",
            PostSlots.LIST to "ID CARD\nBLACK PENS\nANALOG WATCH"
        ),
        looks = listOf(
            TemplateLook("Gold check", "#F59E0B", "#080C14", PostBase.DARK),
            TemplateLook("Emerald check", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Paper check", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Violet stack", "#A855F7", "#090614", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.CHECK_CARDS, 0.06f, 0.32f, 0.88f, 0.54f, PostTone.ACCENT, secondaryTone = PostTone.SOFT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.20f, 0.09f, 0.60f, 0.042f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.15f, 0.84f, 0.14f, size = 96f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.LIST, 0.26f, 0.34f, 0.62f, 0.50f, size = 44f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, list = ListStyle.NONE, anchorsFrom = DecorShape.CHECK_CARDS)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.CHECK_CARDS, 0.44f, 0.14f, 0.50f, 0.72f, PostTone.ACCENT, secondaryTone = PostTone.SOFT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.07f, 0.22f, 0.34f, 0.048f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.07f, 0.29f, 0.34f, 0.36f, size = 88f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.LIST, 0.56f, 0.16f, 0.34f, 0.68f, size = 38f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, list = ListStyle.NONE, anchorsFrom = DecorShape.CHECK_CARDS)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.CHECK_CARDS, 0.44f, 0.12f, 0.50f, 0.76f, PostTone.ACCENT, secondaryTone = PostTone.SOFT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.07f, 0.22f, 0.34f, 0.052f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.07f, 0.29f, 0.34f, 0.38f, size = 92f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.LIST, 0.56f, 0.14f, 0.34f, 0.72f, size = 38f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, list = ListStyle.NONE, anchorsFrom = DecorShape.CHECK_CARDS)
                )
            )
        )
    },

    // 10. info-dateslab — Date Slab (replaces info-timetable)
    PostTemplateSpec(
        id = "info-dateslab",
        name = "Date Slab",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.NUMBER, PostSlots.HEADLINE, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "NOVEMBER",
            PostSlots.NUMBER to "21",
            PostSlots.HEADLINE to "MOCK EXAM LIVE",
            PostSlots.BUTTON to "SAT · 9:00 AM"
        ),
        looks = listOf(
            TemplateLook("Schedule gold", "#F59E0B", "#080B14", PostBase.DARK),
            TemplateLook("Schedule paper", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Schedule mint", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Crimson date", "#EF4444", "#0C0608", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.DATE_SLAB, 0.16f, 0.14f, 0.68f, 0.42f, PostTone.ACCENT, secondaryTone = PostTone.SOFT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.22f, 0.165f, 0.56f, 0.06f, size = 28f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.NUMBER, 0.22f, 0.25f, 0.56f, 0.27f, size = 280f, font = PostFont.CONDENSED, tone = PostTone.TEXT, centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.EXTRUDE, fxTo = PostTone.ACCENT),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.60f, 0.84f, 0.16f, size = 104f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BUTTON, 0.22f, 0.79f, 0.56f, 0.062f, size = 24f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.DATE_SLAB, 0.06f, 0.14f, 0.42f, 0.72f, PostTone.ACCENT, secondaryTone = PostTone.SOFT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.18f, 0.34f, 0.08f, size = 26f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.NUMBER, 0.10f, 0.32f, 0.34f, 0.46f, size = 280f, font = PostFont.CONDENSED, tone = PostTone.TEXT, centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.EXTRUDE, fxTo = PostTone.ACCENT),
                    SlotBox(PostSlots.HEADLINE, 0.54f, 0.24f, 0.40f, 0.34f, size = 92f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BUTTON, 0.54f, 0.64f, 0.36f, 0.085f, size = 24f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.DATE_SLAB, 0.06f, 0.12f, 0.38f, 0.76f, PostTone.ACCENT, secondaryTone = PostTone.SOFT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.09f, 0.16f, 0.32f, 0.09f, size = 26f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.NUMBER, 0.09f, 0.31f, 0.32f, 0.48f, size = 280f, font = PostFont.CONDENSED, tone = PostTone.TEXT, centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.EXTRUDE, fxTo = PostTone.ACCENT),
                    SlotBox(PostSlots.HEADLINE, 0.50f, 0.24f, 0.44f, 0.34f, size = 96f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BUTTON, 0.50f, 0.64f, 0.34f, 0.09f, size = 24f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            )
        )
    },

    // 11. info-ribbonpath — Ribbon Path (replaces info-timeline)
    PostTemplateSpec(
        id = "info-ribbonpath",
        name = "Ribbon Path",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.LIST),
        sample = mapOf(
            PostSlots.EYEBROW to "3-STEP ROADMAP",
            PostSlots.HEADLINE to "MASTER ANY UNIT",
            PostSlots.LIST to "01 READ\n02 SOLVE\n03 TEST"
        ),
        looks = listOf(
            TemplateLook("Timeline gold", "#F59E0B", "#070B14", PostBase.DARK),
            TemplateLook("Timeline mint", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Timeline paper", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Neon path", "#A855F7", "#090614", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.RIBBON_PATH, 0.08f, 0.30f, 0.84f, 0.54f, PostTone.ACCENT, secondaryTone = PostTone.DEEP)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.20f, 0.09f, 0.60f, 0.042f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.15f, 0.84f, 0.14f, size = 96f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.LIST, 0.12f, 0.34f, 0.76f, 0.48f, size = 54f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, list = ListStyle.NONE, anchorsFrom = DecorShape.RIBBON_PATH)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.RIBBON_PATH, 0.42f, 0.14f, 0.52f, 0.72f, PostTone.ACCENT, secondaryTone = PostTone.DEEP)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.07f, 0.22f, 0.34f, 0.048f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.07f, 0.29f, 0.34f, 0.36f, size = 88f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.LIST, 0.48f, 0.18f, 0.42f, 0.64f, size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, list = ListStyle.NONE, anchorsFrom = DecorShape.RIBBON_PATH)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.RIBBON_PATH, 0.42f, 0.12f, 0.52f, 0.76f, PostTone.ACCENT, secondaryTone = PostTone.DEEP)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.07f, 0.22f, 0.34f, 0.052f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.07f, 0.29f, 0.34f, 0.38f, size = 92f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.LIST, 0.48f, 0.16f, 0.42f, 0.68f, size = 48f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, list = ListStyle.NONE, anchorsFrom = DecorShape.RIBBON_PATH)
                )
            )
        )
    },

    // 12. info-cardfan — Card Fan (replaces info-flashcard)
    PostTemplateSpec(
        id = "info-cardfan",
        name = "Card Fan",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "FLASHCARD DECK",
            PostSlots.HEADLINE to "NEWTON'S 3RD LAW",
            PostSlots.BUTTON to "TAP TO FLIP ↻"
        ),
        looks = listOf(
            TemplateLook("Flashcard gold", "#F59E0B", "#070A12", PostBase.DARK),
            TemplateLook("Flashcard mint", "#10B981", "#05100D", PostBase.DARK),
            TemplateLook("Study deck light", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Royal deck", "#A855F7", "#090614", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.CARD_FAN, 0.10f, 0.18f, 0.80f, 0.56f, PostTone.ACCENT, secondaryTone = PostTone.SOFT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.20f, 0.10f, 0.60f, 0.042f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.HEADLINE, 0.18f, 0.30f, 0.64f, 0.28f, size = 96f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f, anchorsFrom = DecorShape.CARD_FAN),
                    SlotBox(PostSlots.BUTTON, 0.24f, 0.78f, 0.52f, 0.06f, size = 24f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.CARD_FAN, 0.12f, 0.16f, 0.76f, 0.62f, PostTone.ACCENT, secondaryTone = PostTone.SOFT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.24f, 0.09f, 0.52f, 0.045f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.HEADLINE, 0.20f, 0.28f, 0.60f, 0.32f, size = 92f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f, anchorsFrom = DecorShape.CARD_FAN),
                    SlotBox(PostSlots.BUTTON, 0.28f, 0.80f, 0.44f, 0.068f, size = 22f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.CARD_FAN, 0.16f, 0.14f, 0.68f, 0.66f, PostTone.ACCENT, secondaryTone = PostTone.SOFT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.28f, 0.08f, 0.44f, 0.05f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.HEADLINE, 0.24f, 0.26f, 0.52f, 0.36f, size = 96f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f, anchorsFrom = DecorShape.CARD_FAN),
                    SlotBox(PostSlots.BUTTON, 0.34f, 0.81f, 0.32f, 0.075f, size = 22f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            )
        )
    },

    // 13. info-steps — 3D Learning Roadmap Steps (1–3 dynamic photo slots)
    PostTemplateSpec(
        id = "info-steps",
        name = "3D Learning Roadmap Steps",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "3-STEP MASTERY ROADMAP · መመሪያ",
            PostSlots.HEADLINE to "From Chapter Notes to Top Exam Score",
            PostSlots.BODY to "Insert 1, 2, or 3 images to showcase your ascending 3D study milestones."
        ),
        looks = listOf(
            TemplateLook("Roadmap gold", "#F59E0B", "#070A12", PostBase.DARK),
            TemplateLook("Roadmap mint", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Roadmap light", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.04f, 0.08f, 0.92f, 0.55f, PostTone.ACCENT, variant = 26, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.65f, 0.88f, 0.24f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.65f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.675f, w = 0.80f, h = 0.040f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.72f, w = 0.80f, h = 0.095f,
                        size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.82f, w = 0.80f, h = 0.055f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.10f, 0.52f, 0.78f, PostTone.ACCENT, variant = 26, panel = true),
                    DecorBox(DecorShape.CARD, 0.57f, 0.18f, 0.38f, 0.64f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.57f, 0.18f, 0.38f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.60f, y = 0.23f, w = 0.32f, h = 0.050f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.60f, y = 0.31f, w = 0.32f, h = 0.30f,
                        size = 48f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.60f, y = 0.64f, w = 0.32f, h = 0.15f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.08f, 0.50f, 0.82f, PostTone.ACCENT, variant = 26, panel = true),
                    DecorBox(DecorShape.CARD, 0.55f, 0.16f, 0.40f, 0.68f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.55f, 0.16f, 0.40f, 0.009f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.58f, y = 0.22f, w = 0.34f, h = 0.055f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.58f, y = 0.30f, w = 0.34f, h = 0.32f,
                        size = 50f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.58f, y = 0.65f, w = 0.34f, h = 0.15f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            )
        )
    }
)
