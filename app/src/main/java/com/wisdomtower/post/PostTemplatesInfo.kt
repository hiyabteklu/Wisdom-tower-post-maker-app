package com.wisdomtower.post

/**
 * Family 4 — Information & Teaching (10 templates: 7 new designs: info-definition, info-stat,
 * info-tip, info-versus, info-formula, info-quote, info-qa + 3 kept designs: info-quiz, info-checklist, info-timetable).
 * Built for 4:3, 9:16, and 16:9 using PostSize.pick3.
 */
internal val InfoTemplates: List<PostTemplateSpec> = listOf(
    // 1. info-definition — Torn-Edge Zine Collage (Batch 3 replacement: ripped archival paper pocket for pop-out cutout)
    PostTemplateSpec(
        id = "info-definition",
        name = "Torn-Edge Zine Collage",
        family = "Information",
        slots = listOf(PostSlots.WORD, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.LIST),
        sample = mapOf(
            PostSlots.WORD to "Epistemology",
            PostSlots.EYEBROW to "CONCEPT BRIEF · ZINE EDITION",
            PostSlots.HEADLINE to "The study of how we know what is true.",
            PostSlots.BODY to "Insert a BG-removed cutout — it tucks cleanly inside the ripped archival paper edge.",
            PostSlots.LIST to "Source of knowledge\nLimits of belief"
        ),
        looks = listOf(
            TemplateLook("Archival zine", "#B45309", "#FAF5EC", PostBase.LIGHT),
            TemplateLook("Midnight riso", "#F59E0B", "#070A12", PostBase.DARK),
            TemplateLook("Cobalt print", "#2563EB", "#F5F7FC", PostBase.LIGHT),
            TemplateLook("Crimson editorial", "#E11D48", "#0B0609", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                cutout = CutoutBox(0.16f, 0.12f, 0.68f, 0.39f),
                cutoutShowDefault = true,
                decor = listOf(
                    DecorBox(DecorShape.TORN_ZINE_COLLAGE, 0f, 0.44f, 1f, 0.56f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO, panel = true),
                    DecorBox(DecorShape.TORN_ZINE_COLLAGE, 0f, 0.44f, 1f, 0.56f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.495f, w = 0.84f, h = 0.036f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.08f, y = 0.535f, w = 0.84f, h = 0.095f,
                        size = 76f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.BOTTOM
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.645f, w = 0.84f, h = 0.085f,
                        size = 34f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.08f, y = 0.735f, w = 0.84f, h = 0.055f,
                        size = 21f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.08f, y = 0.798f, w = 0.84f, h = 0.095f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.NUMBER, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                cutout = CutoutBox(0.06f, 0.14f, 0.42f, 0.78f),
                cutoutShowDefault = true,
                decor = listOf(
                    DecorBox(DecorShape.TORN_ZINE_COLLAGE, 0.44f, 0f, 0.56f, 1f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO, variant = 1, panel = true),
                    DecorBox(DecorShape.TORN_ZINE_COLLAGE, 0.44f, 0f, 0.56f, 1f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO, variant = 1, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.49f, y = 0.14f, w = 0.44f, h = 0.044f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.49f, y = 0.195f, w = 0.44f, h = 0.14f,
                        size = 72f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.BOTTOM
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.49f, y = 0.36f, w = 0.44f, h = 0.20f,
                        size = 34f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.49f, y = 0.58f, w = 0.44f, h = 0.11f,
                        size = 21f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.49f, y = 0.71f, w = 0.44f, h = 0.17f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.NUMBER, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                cutout = CutoutBox(0.05f, 0.10f, 0.40f, 0.84f),
                cutoutShowDefault = true,
                decor = listOf(
                    DecorBox(DecorShape.TORN_ZINE_COLLAGE, 0.42f, 0f, 0.58f, 1f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO, variant = 1, panel = true),
                    DecorBox(DecorShape.TORN_ZINE_COLLAGE, 0.42f, 0f, 0.58f, 1f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO, variant = 1, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.48f, y = 0.14f, w = 0.45f, h = 0.048f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.48f, y = 0.20f, w = 0.45f, h = 0.15f,
                        size = 76f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.BOTTOM
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.48f, y = 0.37f, w = 0.45f, h = 0.20f,
                        size = 36f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.48f, y = 0.59f, w = 0.45f, h = 0.11f,
                        size = 21f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.48f, y = 0.72f, w = 0.45f, h = 0.17f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.NUMBER, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            )
        )
    },

    // 2. info-stat — Typographic Plinth (Batch 2 replacement: monumental 3D stand-on letters/numbers for cutout perch)
    PostTemplateSpec(
        id = "info-stat",
        name = "Typographic Plinth",
        family = "Information",
        slots = listOf(PostSlots.NUMBER, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.LIST),
        sample = mapOf(
            PostSlots.NUMBER to "94%",
            PostSlots.EYEBROW to "ANNUAL STUDENT OUTCOMES",
            PostSlots.HEADLINE to "Built on active recall.",
            PostSlots.BODY to "Insert a BG-removed person or object to sit, lean, or peek right over the 3D sculptural number plinth.",
            PostSlots.LIST to "Practice questions (+42%)\nSpaced review (+31%)\nPeer teaching (+21%)"
        ),
        looks = listOf(
            TemplateLook("Monument gold", "#F59E0B", "#060911", PostBase.DARK),
            TemplateLook("Emerald plinth", "#10B981", "#040F0C", PostBase.DARK),
            TemplateLook("Cobalt block", "#38BDF8", "#050B16", PostBase.DARK),
            TemplateLook("Gallery stone", "#2563EB", "#F8F6F0", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.22f, 0.15f, 0.56f, 0.30f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(DecorShape.TYPOGRAPHIC_PLINTH, 0.05f, 0.28f, 0.90f, 0.30f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.12f, w = 0.80f, h = 0.038f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.05f, y = 0.28f, w = 0.90f, h = 0.30f,
                        size = 280f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, drawnByDecor = true
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.09f, y = 0.60f, w = 0.82f, h = 0.085f,
                        size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.11f, y = 0.695f, w = 0.78f, h = 0.065f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.14f, y = 0.77f, w = 0.72f, h = 0.115f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.08f, 0.10f, 0.40f, 0.42f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(DecorShape.TYPOGRAPHIC_PLINTH, 0.04f, 0.34f, 0.46f, 0.44f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO, panel = true),
                    DecorBox(DecorShape.BAR, 0.54f, 0.48f, 0.22f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.54f, y = 0.14f, w = 0.40f, h = 0.045f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.04f, y = 0.34f, w = 0.46f, h = 0.44f,
                        size = 260f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, drawnByDecor = true
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.54f, y = 0.21f, w = 0.40f, h = 0.23f,
                        size = 44f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.54f, y = 0.52f, w = 0.40f, h = 0.12f,
                        size = 21f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.54f, y = 0.66f, w = 0.40f, h = 0.22f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.08f, 0.08f, 0.38f, 0.44f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(DecorShape.TYPOGRAPHIC_PLINTH, 0.04f, 0.34f, 0.44f, 0.46f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO, panel = true),
                    DecorBox(DecorShape.BAR, 0.52f, 0.47f, 0.20f, 0.009f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.52f, y = 0.14f, w = 0.42f, h = 0.050f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.04f, y = 0.34f, w = 0.44f, h = 0.46f,
                        size = 260f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, drawnByDecor = true
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.52f, y = 0.21f, w = 0.42f, h = 0.22f,
                        size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.52f, y = 0.51f, w = 0.42f, h = 0.12f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.52f, y = 0.65f, w = 0.42f, h = 0.23f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            )
        )
    },

    // 3. info-bignumber — Depth Sandwich (Batch 1 replacement: solid back + movable cutout + front hairline outline)
    PostTemplateSpec(
        id = "info-bignumber",
        name = "Depth Sandwich",
        family = "Information",
        slots = listOf(PostSlots.WORD, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.WORD to "UNSTOPPABLE",
            PostSlots.EYEBROW to "EDITORIAL DEPTH · CUTOUT STAGE",
            PostSlots.HEADLINE to "PUT YOURSELF INSIDE THE STORY.",
            PostSlots.BODY to "Insert any BG-removed person or object and drag (X, Y) freely."
        ),
        looks = listOf(
            TemplateLook("Cobalt editorial", "#38BDF8", "#060B16", PostBase.DARK),
            TemplateLook("Crimson cover", "#F43F5E", "#12060B", PostBase.DARK),
            TemplateLook("Gallery chalk", "#EA580C", "#FAF6EE", PostBase.LIGHT),
            TemplateLook("Emerald studio", "#10B981", "#05110D", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                cutoutShowDefault = true,
                cutout = CutoutBox(0.14f, 0.28f, 0.72f, 0.62f, rimTone = PostTone.ACCENT, shadow = true),
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.10f, 0.24f, 0.80f, 0.48f, PostTone.ACCENT, alpha = 115)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.14f, w = 0.84f, h = 0.034f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.20f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.02f, y = 0.22f, w = 0.96f, h = 0.44f,
                        size = 260f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        leading = 0.88f, fx = TextFx.GRADIENT, fxTo = PostTone.GLOW
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.72f, w = 0.84f, h = 0.085f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.81f, w = 0.76f, h = 0.052f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                cutoutShowDefault = true,
                cutout = CutoutBox(0.22f, 0.22f, 0.56f, 0.68f, rimTone = PostTone.ACCENT, shadow = true),
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.16f, 0.18f, 0.68f, 0.58f, PostTone.ACCENT, alpha = 115)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.09f, w = 0.80f, h = 0.040f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.20f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.03f, y = 0.17f, w = 0.94f, h = 0.50f,
                        size = 290f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        leading = 0.88f, fx = TextFx.GRADIENT, fxTo = PostTone.GLOW
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.72f, w = 0.84f, h = 0.095f,
                        size = 54f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.14f, y = 0.82f, w = 0.72f, h = 0.055f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                cutoutShowDefault = true,
                cutout = CutoutBox(0.28f, 0.18f, 0.44f, 0.74f, rimTone = PostTone.ACCENT, shadow = true),
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.22f, 0.14f, 0.56f, 0.66f, PostTone.ACCENT, alpha = 115)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.16f, y = 0.08f, w = 0.68f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.20f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.04f, y = 0.16f, w = 0.92f, h = 0.54f,
                        size = 310f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        leading = 0.88f, fx = TextFx.GRADIENT, fxTo = PostTone.GLOW
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.72f, w = 0.80f, h = 0.10f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.18f, y = 0.83f, w = 0.64f, h = 0.055f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
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

    // 5. info-formula — Architectural Blueprint Stage (Batch 3 replacement: millimeter cyanotype grid + reticle + callout pointers)
    PostTemplateSpec(
        id = "info-formula",
        name = "Architectural Blueprint",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.WORD, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "BLUEPRINT SPEC // PHYSICS & MATH",
            PostSlots.WORD to "E = mc²",
            PostSlots.HEADLINE to "Precision Theorem Stage",
            PostSlots.BODY to "Drop any BG-removed object, device, or scholar right inside the calibrated engineering reticle."
        ),
        looks = listOf(
            TemplateLook("Cyanotype blue", "#38BDF8", "#05142B", PostBase.DARK),
            TemplateLook("Noir CAD gold", "#F59E0B", "#070B12", PostBase.DARK),
            TemplateLook("Matrix emerald", "#10B981", "#04120E", PostBase.DARK),
            TemplateLook("Drafting vellum", "#1D4ED8", "#F4F7FC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = null,
                cutout = CutoutBox(0.18f, 0.22f, 0.64f, 0.40f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(DecorShape.ARCHITECTURAL_BLUEPRINT, 0.10f, 0.18f, 0.80f, 0.44f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.11f, w = 0.80f, h = 0.036f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.10f, y = 0.635f, w = 0.80f, h = 0.090f,
                        size = 64f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.73f, w = 0.80f, h = 0.070f,
                        size = 42f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.808f, w = 0.76f, h = 0.068f,
                        size = 21f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = null,
                cutout = CutoutBox(0.08f, 0.14f, 0.42f, 0.72f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(DecorShape.ARCHITECTURAL_BLUEPRINT, 0.06f, 0.12f, 0.46f, 0.76f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO, panel = true),
                    DecorBox(DecorShape.BAR, 0.56f, 0.58f, 0.24f, 0.007f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.56f, y = 0.16f, w = 0.36f, h = 0.044f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.56f, y = 0.225f, w = 0.36f, h = 0.14f,
                        size = 64f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.56f, y = 0.38f, w = 0.36f, h = 0.18f,
                        size = 42f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.56f, y = 0.62f, w = 0.36f, h = 0.18f,
                        size = 21f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = null,
                cutout = CutoutBox(0.08f, 0.10f, 0.40f, 0.78f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(DecorShape.ARCHITECTURAL_BLUEPRINT, 0.06f, 0.10f, 0.44f, 0.80f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO, panel = true),
                    DecorBox(DecorShape.BAR, 0.54f, 0.58f, 0.22f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.54f, y = 0.15f, w = 0.38f, h = 0.048f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.54f, y = 0.22f, w = 0.38f, h = 0.15f,
                        size = 68f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.54f, y = 0.385f, w = 0.38f, h = 0.17f,
                        size = 44f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.54f, y = 0.62f, w = 0.38f, h = 0.19f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            )
        )
    },

    // 6. info-pullquote — Darkroom Spotlight Aperture (Batch 2 replacement: velvety matte + keyhole lens spotlight reveal)
    PostTemplateSpec(
        id = "info-pullquote",
        name = "Darkroom Aperture",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "TIMELESS WISDOM · EDITORIAL",
            PostSlots.HEADLINE to "LIGHT A FIRE.",
            PostSlots.BODY to "Drop a photo or BG-removed cutout into the calibrated keyhole spotlight while editorial copy breathes in the darkroom matte."
        ),
        looks = listOf(
            TemplateLook("Darkroom amber", "#F59E0B", "#07090F", PostBase.DARK),
            TemplateLook("Gallery linen", "#B45309", "#F7F3EA", PostBase.LIGHT),
            TemplateLook("Emerald lens", "#10B981", "#050E0B", PostBase.DARK),
            TemplateLook("Crimson cinema", "#E11D48", "#0B0508", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = null,
                logoAnchor = LogoAnchor.TOP_LEFT,
                photo = PostPhotoArea(0f, 0f, 1f, 1f),
                cutout = CutoutBox(0.22f, 0.20f, 0.56f, 0.44f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(DecorShape.DARKROOM_APERTURE, 0.16f, 0.17f, 0.68f, 0.47f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO, panel = true)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.11f, 0.76f, 0.038f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.675f, 0.84f, 0.105f, size = 62f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.96f),
                    SlotBox(PostSlots.BODY, 0.12f, 0.79f, 0.76f, 0.075f, size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED, centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = null,
                logoAnchor = LogoAnchor.TOP_LEFT,
                photo = PostPhotoArea(0f, 0f, 1f, 1f),
                cutout = CutoutBox(0.10f, 0.16f, 0.36f, 0.68f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(DecorShape.DARKROOM_APERTURE, 0.07f, 0.14f, 0.42f, 0.72f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO, panel = true),
                    DecorBox(DecorShape.BAR, 0.55f, 0.62f, 0.22f, 0.008f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO, panel = true)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.55f, 0.20f, 0.38f, 0.046f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.HEADLINE, 0.55f, 0.27f, 0.38f, 0.32f, size = 64f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.96f),
                    SlotBox(PostSlots.BODY, 0.55f, 0.66f, 0.38f, 0.16f, size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED, fit = FitMode.FILL, anchor = VAnchor.TOP)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = null,
                logoAnchor = LogoAnchor.TOP_LEFT,
                photo = PostPhotoArea(0f, 0f, 1f, 1f),
                cutout = CutoutBox(0.10f, 0.14f, 0.34f, 0.74f),
                cutoutShowDefault = false,
                decor = listOf(
                    DecorBox(DecorShape.DARKROOM_APERTURE, 0.07f, 0.12f, 0.40f, 0.76f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO, panel = true),
                    DecorBox(DecorShape.BAR, 0.53f, 0.62f, 0.20f, 0.009f, PostTone.ACCENT, layer = DecorLayer.FRONT_OF_PHOTO, panel = true)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.53f, 0.18f, 0.41f, 0.050f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.HEADLINE, 0.53f, 0.26f, 0.41f, 0.33f, size = 66f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.96f),
                    SlotBox(PostSlots.BODY, 0.53f, 0.66f, 0.41f, 0.17f, size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED, fit = FitMode.FILL, anchor = VAnchor.TOP)
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

    // 13. info-steps — Perspective Horizon Runway (Batch 3 replacement: one-point perspective receding grid & glowing runway floor)
    PostTemplateSpec(
        id = "info-steps",
        name = "Horizon Runway",
        family = "Information",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "FUTURE ROADMAP · 2026 VISION",
            PostSlots.HEADLINE to "WALK INTO YOUR FUTURE.",
            PostSlots.BODY to "Drop a full-body BG-removed scholar or object onto the receding perspective horizon runway."
        ),
        looks = listOf(
            TemplateLook("Horizon gold", "#F59E0B", "#060912", PostBase.DARK),
            TemplateLook("Neon cyber", "#38BDF8", "#040814", PostBase.DARK),
            TemplateLook("Emerald runway", "#10B981", "#040E0C", PostBase.DARK),
            TemplateLook("Daylight grid", "#2563EB", "#F5F7FC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.18f, 0.34f, 0.64f, 0.44f),
                cutoutShowDefault = true,
                decor = listOf(
                    DecorBox(DecorShape.PERSPECTIVE_RUNWAY, 0f, 0.52f, 1f, 0.48f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.12f, w = 0.80f, h = 0.038f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.17f, w = 0.86f, h = 0.135f,
                        size = 64f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.96f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.80f, w = 0.80f, h = 0.075f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.24f, 0.28f, 0.52f, 0.54f),
                cutoutShowDefault = true,
                decor = listOf(
                    DecorBox(DecorShape.PERSPECTIVE_RUNWAY, 0f, 0.50f, 1f, 0.50f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.12f, y = 0.07f, w = 0.76f, h = 0.042f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.12f, w = 0.86f, h = 0.135f,
                        size = 62f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.96f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.84f, w = 0.76f, h = 0.070f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.50f, 0.16f, 0.40f, 0.70f),
                cutoutShowDefault = true,
                decor = listOf(
                    DecorBox(DecorShape.PERSPECTIVE_RUNWAY, 0.20f, 0.52f, 0.80f, 0.48f, PostTone.ACCENT, layer = DecorLayer.BEHIND_PHOTO, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.16f, w = 0.42f, h = 0.048f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.23f, w = 0.42f, h = 0.34f,
                        size = 64f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.96f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.06f, y = 0.62f, w = 0.40f, h = 0.18f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            )
        )
    }
)
