package com.wisdomtower.post

/**
 * Family 1 — Letter & Number (6 templates: ltr-window, ltr-dropcap, ltr-number, ltr-alphabet, ltr-bigword, ltr-split).
 * Built for 4:3, 9:16, and 16:9 using PostSize.pick3. No waves, rings, dot grids, stripes, or gems.
 */
internal val LetterTemplates: List<PostTemplateSpec> = listOf(
    // 1. ltr-window — Letter window
    PostTemplateSpec(
        id = "ltr-window",
        name = "Letter window",
        family = "Letter",
        slots = listOf(PostSlots.LETTER, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.LETTER to "W",
            PostSlots.EYEBROW to "WISDOM TOWER",
            PostSlots.HEADLINE to "Turn one bold character into a window for your story.",
            PostSlots.BODY to "Add a template photo to fill the giant letter, or keep the rich brand gradient."
        ),
        looks = listOf(
            TemplateLook("Gold glow", "#F59E0B", "#07090E", PostBase.DARK),
            TemplateLook("Editorial paper", "#D97706", "#FAF6EE", PostBase.LIGHT),
            TemplateLook("Cobalt night", "#38BDF8", "#060D1A", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.68f, 0.90f, 0.23f, PostTone.DEEP, alpha = 225, panel = true),
                    DecorBox(DecorShape.BAR, 0.05f, 0.68f, 0.24f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.06f, y = 0.09f, w = 0.88f, h = 0.72f,
                        size = 680f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        centered = true, caps = true,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.PHOTO_FILL, fxTo = PostTone.GLOW,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.11f, w = 0.48f, h = 0.045f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.LIGHT,
                        caps = true, fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER,
                        tracking = 0.12f, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.09f, y = 0.70f, w = 0.82f, h = 0.11f,
                        size = 44f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f,
                        shadow = TextShadow(strength = 45, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.09f, y = 0.82f, w = 0.82f, h = 0.07f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.64f, 0.88f, 0.25f, PostTone.DEEP, alpha = 225, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.64f, 0.22f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.06f, y = 0.06f, w = 0.88f, h = 0.78f,
                        size = 640f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        centered = true, caps = true,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.PHOTO_FILL, fxTo = PostTone.GLOW,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.11f, w = 0.36f, h = 0.055f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.LIGHT,
                        caps = true, fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER,
                        tracking = 0.12f, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.09f, y = 0.67f, w = 0.82f, h = 0.12f,
                        size = 42f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f,
                        shadow = TextShadow(strength = 45, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.09f, y = 0.80f, w = 0.82f, h = 0.07f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.44f, 0.18f, 0.50f, 0.66f, PostTone.DEEP, alpha = 220, panel = true),
                    DecorBox(DecorShape.BAR, 0.44f, 0.18f, 0.008f, 0.66f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.03f, y = 0.06f, w = 0.48f, h = 0.84f,
                        size = 640f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        centered = true, caps = true,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.PHOTO_FILL, fxTo = PostTone.GLOW,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.48f, y = 0.24f, w = 0.28f, h = 0.065f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.LIGHT,
                        caps = true, fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER,
                        tracking = 0.12f, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.48f, y = 0.35f, w = 0.42f, h = 0.28f,
                        size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.48f, y = 0.65f, w = 0.42f, h = 0.14f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            )
        )
    },

    // 2. ltr-dropcap — Magazine drop-cap
    PostTemplateSpec(
        id = "ltr-dropcap",
        name = "Magazine drop-cap",
        family = "Letter",
        slots = listOf(PostSlots.LETTER, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.LETTER to "A",
            PostSlots.EYEBROW to "ESSAY · ISSUE 14",
            PostSlots.HEADLINE to "Attention is the rarest currency of our century.",
            PostSlots.BODY to "What we choose to study shapes the architecture of our mind. Slow down and read deeply."
        ),
        looks = listOf(
            TemplateLook("Editorial paper", "#B45309", "#FAF5EC", PostBase.LIGHT),
            TemplateLook("Midnight press", "#F59E0B", "#0B0F19", PostBase.DARK),
            TemplateLook("Crimson review", "#BE123C", "#FDF8F6", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                decor = listOf(
                    DecorBox(DecorShape.LINE, 0.07f, 0.13f, 0.86f, 0.002f, PostTone.TEXT, alpha = 90, panel = true),
                    DecorBox(DecorShape.BAR, 0.13f, 0.48f, 0.28f, 0.006f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.LINE, 0.07f, 0.67f, 0.86f, 0.002f, PostTone.TEXT, alpha = 75, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.02f, y = 0.24f, w = 0.22f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.MUTED,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH,
                        rotate = -90f, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.13f, y = 0.15f, w = 0.34f, h = 0.31f,
                        size = 420f, font = PostFont.SERIF, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.BOTTOM
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.50f, y = 0.17f, w = 0.43f, h = 0.31f,
                        size = 46f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.BOTTOM, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.13f, y = 0.52f, w = 0.80f, h = 0.34f,
                        size = 29f, font = PostFont.REGULAR, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.28f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                decor = listOf(
                    DecorBox(DecorShape.LINE, 0.06f, 0.14f, 0.88f, 0.002f, PostTone.TEXT, alpha = 90, panel = true),
                    DecorBox(DecorShape.BAR, 0.12f, 0.74f, 0.26f, 0.008f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.LINE, 0.44f, 0.52f, 0.50f, 0.002f, PostTone.TEXT, alpha = 85, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = -0.02f, y = 0.44f, w = 0.26f, h = 0.05f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.MUTED,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH,
                        rotate = -90f, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.10f, y = 0.17f, w = 0.31f, h = 0.55f,
                        size = 480f, font = PostFont.SERIF, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.44f, y = 0.18f, w = 0.50f, h = 0.31f,
                        size = 48f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.BOTTOM, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.44f, y = 0.55f, w = 0.50f, h = 0.30f,
                        size = 26f, font = PostFont.REGULAR, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.25f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                decor = listOf(
                    DecorBox(DecorShape.LINE, 0.05f, 0.15f, 0.90f, 0.002f, PostTone.TEXT, alpha = 90, panel = true),
                    DecorBox(DecorShape.BAR, 0.10f, 0.78f, 0.24f, 0.008f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.LINE, 0.42f, 0.52f, 0.53f, 0.002f, PostTone.TEXT, alpha = 85, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = -0.03f, y = 0.45f, w = 0.24f, h = 0.055f,
                        size = 18f, font = PostFont.BOLD, tone = PostTone.MUTED,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH,
                        rotate = -90f, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.09f, y = 0.18f, w = 0.30f, h = 0.58f,
                        size = 490f, font = PostFont.SERIF, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.42f, y = 0.19f, w = 0.53f, h = 0.30f,
                        size = 50f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.BOTTOM, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.42f, y = 0.55f, w = 0.53f, h = 0.30f,
                        size = 26f, font = PostFont.REGULAR, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            )
        )
    },

    // 3. ltr-number — Big number
    PostTemplateSpec(
        id = "ltr-number",
        name = "Big number",
        family = "Letter",
        slots = listOf(PostSlots.NUMBER, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.NUMBER to "01",
            PostSlots.EYEBROW to "PRINCIPLE ONE",
            PostSlots.HEADLINE to "Clarity beats complexity every single time.",
            PostSlots.BODY to "Strip away every element that does not directly serve the reader's understanding."
        ),
        looks = listOf(
            TemplateLook("Gold glow", "#F59E0B", "#07090F", PostBase.DARK),
            TemplateLook("Emerald pulse", "#10B981", "#05100D", PostBase.DARK),
            TemplateLook("Paper & ink", "#DC2626", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.07f, 0.58f, 0.86f, 0.006f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.06f, y = 0.11f, w = 0.88f, h = 0.44f,
                        size = 540f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.BOTTOM,
                        tracking = -0.03f, fx = TextFx.GRADIENT, fxTo = PostTone.GLOW,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.61f, w = 0.44f, h = 0.042f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.LIGHT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.67f, w = 0.86f, h = 0.13f,
                        size = 48f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.07f, y = 0.81f, w = 0.86f, h = 0.09f,
                        size = 26f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.07f, 0.60f, 0.86f, 0.007f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.06f, y = 0.08f, w = 0.88f, h = 0.50f,
                        size = 500f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.BOTTOM,
                        tracking = -0.03f, fx = TextFx.GRADIENT, fxTo = PostTone.GLOW,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.63f, w = 0.34f, h = 0.052f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.LIGHT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.70f, w = 0.86f, h = 0.11f,
                        size = 44f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.07f, y = 0.82f, w = 0.86f, h = 0.08f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.46f, 0.16f, 0.006f, 0.68f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.05f, y = 0.12f, w = 0.38f, h = 0.74f,
                        size = 520f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        tracking = -0.03f, fx = TextFx.GRADIENT, fxTo = PostTone.GLOW,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.50f, y = 0.22f, w = 0.28f, h = 0.06f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.LIGHT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.50f, y = 0.32f, w = 0.44f, h = 0.30f,
                        size = 48f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.50f, y = 0.64f, w = 0.44f, h = 0.18f,
                        size = 25f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f
                    )
                )
            )
        )
    },

    // 4. ltr-alphabet — Alphabet card
    PostTemplateSpec(
        id = "ltr-alphabet",
        name = "Alphabet card",
        family = "Letter",
        slots = listOf(PostSlots.LETTER, PostSlots.WORD, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.LETTER to "ሀ",
            PostSlots.WORD to "ሀብት",
            PostSlots.EYEBROW to "FIDEL · WORD OF THE DAY",
            PostSlots.HEADLINE to "Habt (n.) — Wealth, heritage, and inner abundance.",
            PostSlots.BODY to "True wealth is measured by the wisdom you cultivate and share with your community."
        ),
        looks = listOf(
            TemplateLook("Gold & slate", "#F59E0B", "#0B1120", PostBase.DARK),
            TemplateLook("Warm parchment", "#B45309", "#FAF5EC", PostBase.LIGHT),
            TemplateLook("Forest sage", "#10B981", "#071712", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.50f, 0.11f, 0.43f, 0.38f, PostTone.SOFT, alpha = 230, panel = true),
                    DecorBox(DecorShape.CARD, 0.07f, 0.52f, 0.86f, 0.37f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.07f, 0.52f, 0.86f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.13f, w = 0.40f, h = 0.04f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.07f, y = 0.20f, w = 0.40f, h = 0.24f,
                        size = 86f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.53f, y = 0.13f, w = 0.37f, h = 0.34f,
                        size = 380f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.11f, y = 0.56f, w = 0.78f, h = 0.13f,
                        size = 38f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.11f, y = 0.71f, w = 0.78f, h = 0.15f,
                        size = 26f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.54f, 0.10f, 0.40f, 0.42f, PostTone.SOFT, alpha = 230, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.55f, 0.88f, 0.35f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.55f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.12f, w = 0.44f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.06f, y = 0.19f, w = 0.44f, h = 0.29f,
                        size = 92f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.57f, y = 0.12f, w = 0.34f, h = 0.38f,
                        size = 360f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.59f, w = 0.80f, h = 0.12f,
                        size = 36f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.73f, w = 0.80f, h = 0.14f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.56f, 0.12f, 0.38f, 0.74f, PostTone.SOFT, alpha = 230, panel = true),
                    DecorBox(DecorShape.CARD, 0.05f, 0.46f, 0.48f, 0.40f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.05f, 0.46f, 0.48f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.05f, y = 0.13f, w = 0.46f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.05f, y = 0.20f, w = 0.46f, h = 0.22f,
                        size = 88f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.59f, y = 0.16f, w = 0.32f, h = 0.66f,
                        size = 440f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.50f, w = 0.42f, h = 0.15f,
                        size = 34f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.08f, y = 0.67f, w = 0.42f, h = 0.16f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f
                    )
                )
            )
        )
    },

    // 5. ltr-bigword — Big word
    PostTemplateSpec(
        id = "ltr-bigword",
        name = "Big word",
        family = "Letter",
        slots = listOf(PostSlots.WORD, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.WORD to "FOCUS",
            PostSlots.EYEBROW to "DAILY DISCIPLINE",
            PostSlots.HEADLINE to "Guard your morning hours before the world asks for them.",
            PostSlots.BODY to "One uninterrupted hour builds more momentum than a fragmented day."
        ),
        looks = listOf(
            TemplateLook("Gold punch", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Electric cyan", "#06B6D4", "#050B14", PostBase.DARK),
            TemplateLook("Editorial red", "#E11D48", "#FAF8F5", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.LINE, 0.15f, 0.17f, 0.70f, 0.002f, PostTone.MUTED, alpha = 90, panel = true),
                    DecorBox(DecorShape.BAR, 0.04f, 0.36f, 0.92f, 0.22f, PostTone.ACCENT, alpha = 235, panel = true, rotate = -4f)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.15f, y = 0.12f, w = 0.70f, h = 0.04f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.04f, y = 0.22f, w = 0.92f, h = 0.46f,
                        size = 340f, font = PostFont.CONDENSED, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        tracking = -0.02f, shadow = TextShadow(strength = 70, mode = ShadowMode.HARD)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.70f, w = 0.84f, h = 0.10f,
                        size = 42f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.81f, w = 0.80f, h = 0.08f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.LINE, 0.20f, 0.17f, 0.60f, 0.002f, PostTone.MUTED, alpha = 90, panel = true),
                    DecorBox(DecorShape.BAR, 0.04f, 0.35f, 0.92f, 0.25f, PostTone.ACCENT, alpha = 235, panel = true, rotate = -4f)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.20f, y = 0.11f, w = 0.60f, h = 0.045f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.04f, y = 0.20f, w = 0.92f, h = 0.50f,
                        size = 340f, font = PostFont.CONDENSED, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        tracking = -0.02f, shadow = TextShadow(strength = 70, mode = ShadowMode.HARD)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.72f, w = 0.84f, h = 0.10f,
                        size = 40f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.83f, w = 0.80f, h = 0.07f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.04f, 0.33f, 0.92f, 0.28f, PostTone.ACCENT, alpha = 235, panel = true, rotate = -3f)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.25f, y = 0.11f, w = 0.50f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.04f, y = 0.18f, w = 0.92f, h = 0.52f,
                        size = 360f, font = PostFont.CONDENSED, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        tracking = -0.02f, shadow = TextShadow(strength = 70, mode = ShadowMode.HARD)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.12f, y = 0.72f, w = 0.76f, h = 0.09f,
                        size = 38f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.16f, y = 0.82f, w = 0.68f, h = 0.07f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            )
        )
    },

    // 6. ltr-split — Letter split
    PostTemplateSpec(
        id = "ltr-split",
        name = "Letter split",
        family = "Letter",
        slots = listOf(PostSlots.LETTER, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.LETTER to "Q",
            PostSlots.EYEBROW to "CONTRAST SERIES",
            PostSlots.HEADLINE to "Ask better questions to unlock better answers.",
            PostSlots.BODY to "Two perspectives meet right down the center of the page."
        ),
        looks = listOf(
            TemplateLook("Gold split", "#F59E0B", "#090D16", PostBase.DARK),
            TemplateLook("Mint split", "#10B981", "#07120E", PostBase.DARK),
            TemplateLook("Coral split", "#F43F5E", "#0F070B", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.BLOCK, 0f, 0f, 0.50f, 1f, PostTone.DEEP, panel = true),
                    DecorBox(DecorShape.BLOCK, 0.50f, 0f, 0.50f, 1f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.12f, w = 0.38f, h = 0.05f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.12f, y = 0.18f, w = 0.76f, h = 0.48f,
                        size = 620f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.SPLIT_TONE, fxTo = PostTone.DEEP,
                        shadow = TextShadow(strength = 55, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.53f, y = 0.67f, w = 0.41f, h = 0.13f,
                        size = 40f, font = PostFont.DISPLAY, tone = PostTone.DARK,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.53f, y = 0.81f, w = 0.41f, h = 0.09f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.DARK,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.16f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.BLOCK, 0f, 0f, 0.50f, 1f, PostTone.DEEP, panel = true),
                    DecorBox(DecorShape.BLOCK, 0.50f, 0f, 0.50f, 1f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.13f, w = 0.38f, h = 0.055f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.16f, y = 0.14f, w = 0.68f, h = 0.58f,
                        size = 580f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.SPLIT_TONE, fxTo = PostTone.DEEP,
                        shadow = TextShadow(strength = 55, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.54f, y = 0.64f, w = 0.40f, h = 0.14f,
                        size = 38f, font = PostFont.DISPLAY, tone = PostTone.DARK,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.54f, y = 0.79f, w = 0.40f, h = 0.10f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.BLOCK, 0f, 0f, 0.50f, 1f, PostTone.DEEP, panel = true),
                    DecorBox(DecorShape.BLOCK, 0.50f, 0f, 0.50f, 1f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.05f, y = 0.14f, w = 0.36f, h = 0.06f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.22f, y = 0.10f, w = 0.56f, h = 0.72f,
                        size = 580f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.SPLIT_TONE, fxTo = PostTone.DEEP,
                        shadow = TextShadow(strength = 55, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.55f, y = 0.58f, w = 0.39f, h = 0.16f,
                        size = 40f, font = PostFont.DISPLAY, tone = PostTone.DARK,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.55f, y = 0.76f, w = 0.39f, h = 0.11f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    )
                )
            )
        )
    }
)
