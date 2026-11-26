package com.wisdomtower.post

/**
 * Family 1 — Letter & Number (6 templates: ltr-portal, ltr-coverline, ltr-number, ltr-alphabet, ltr-bigword, ltr-mirrorsplit).
 * Built for 4:3, 9:16, and 16:9 using PostSize.pick3. No waves, rings, dot grids, stripes, or gems.
 */
internal val LetterTemplates: List<PostTemplateSpec> = listOf(
    // 1. ltr-portal — Letter Portal (replaces ltr-window)
    PostTemplateSpec(
        id = "ltr-portal",
        name = "Letter Portal",
        family = "Letter",
        slots = listOf(PostSlots.LETTER, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.LETTER to "W",
            PostSlots.EYEBROW to "WISDOM PORTAL",
            PostSlots.HEADLINE to "STEP INSIDE.",
            PostSlots.BODY to "One bold letter becomes a dimensional doorway."
        ),
        looks = listOf(
            TemplateLook("Gold portal", "#F59E0B", "#06080E", PostBase.DARK),
            TemplateLook("Editorial cream", "#D97706", "#FAF5EC", PostBase.LIGHT),
            TemplateLook("Electric cyan", "#06B6D4", "#040B14", PostBase.DARK),
            TemplateLook("Crimson stage", "#F43F5E", "#12060B", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.PORTAL_ARCH, 0.10f, 0.08f, 0.80f, 0.68f, PostTone.ACCENT, alpha = 235, panel = true),
                    DecorBox(DecorShape.GLOW, 0.14f, 0.14f, 0.72f, 0.56f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.BAR, 0.34f, 0.78f, 0.32f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.05f, w = 0.44f, h = 0.04f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.08f, y = 0.10f, w = 0.84f, h = 0.64f,
                        size = 720f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.PHOTO_FILL, fxTo = PostTone.GLOW,
                        shadow = TextShadow(strength = 85, mode = ShadowMode.GLOW)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.80f, w = 0.84f, h = 0.08f,
                        size = 64f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        tracking = -0.02f, shadow = TextShadow(strength = 55, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.885f, w = 0.76f, h = 0.045f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.PORTAL_ARCH, 0.14f, 0.07f, 0.72f, 0.68f, PostTone.ACCENT, alpha = 235, panel = true),
                    DecorBox(DecorShape.GLOW, 0.18f, 0.12f, 0.64f, 0.56f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.BAR, 0.36f, 0.77f, 0.28f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.06f, w = 0.36f, h = 0.045f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.10f, y = 0.09f, w = 0.80f, h = 0.65f,
                        size = 680f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.PHOTO_FILL, fxTo = PostTone.GLOW,
                        shadow = TextShadow(strength = 85, mode = ShadowMode.GLOW)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.79f, w = 0.84f, h = 0.085f,
                        size = 60f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.14f, y = 0.88f, w = 0.72f, h = 0.05f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.PORTAL_ARCH, 0.06f, 0.06f, 0.44f, 0.84f, PostTone.ACCENT, alpha = 235, panel = true),
                    DecorBox(DecorShape.GLOW, 0.08f, 0.10f, 0.40f, 0.74f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.BAR, 0.54f, 0.62f, 0.24f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.05f, y = 0.06f, w = 0.46f, h = 0.84f,
                        size = 660f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.PHOTO_FILL, fxTo = PostTone.GLOW,
                        shadow = TextShadow(strength = 85, mode = ShadowMode.GLOW)
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.54f, y = 0.24f, w = 0.28f, h = 0.055f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.54f, y = 0.34f, w = 0.40f, h = 0.24f,
                        size = 68f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.96f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.54f, y = 0.66f, w = 0.38f, h = 0.14f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            )
        )
    },

    // 2. ltr-coverline — 45° Layered Mobile Mockup (replaces old Cover Line with 1..4 3D layered mobile screens)
    PostTemplateSpec(
        id = "ltr-coverline",
        name = "Cover Line",
        family = "Letter",
        slots = emptyList(),
        sample = emptyMap(),
        looks = listOf(
            TemplateLook("Cobalt studio", "#38BDF8", "#071E3D", PostBase.DARK),
            TemplateLook("Vogue gold", "#F59E0B", "#070910", PostBase.DARK),
            TemplateLook("Emerald folio", "#10B981", "#05110D", PostBase.DARK),
            TemplateLook("Light showcase", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.05f,
                        y = 0.135f,
                        w = 0.90f,
                        h = 0.65f,
                        tone = PostTone.ACCENT,
                        secondaryTone = PostTone.DEEP,
                        variant = 4
                    )
                ),
                slots = emptyList(),
                bare = true,
                safeFit = true
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.06f,
                        y = 0.045f,
                        w = 0.88f,
                        h = 0.91f,
                        tone = PostTone.ACCENT,
                        secondaryTone = PostTone.DEEP,
                        variant = 4
                    )
                ),
                slots = emptyList(),
                bare = true,
                safeFit = true
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.12f,
                        y = 0.04f,
                        w = 0.76f,
                        h = 0.92f,
                        tone = PostTone.ACCENT,
                        secondaryTone = PostTone.DEEP,
                        variant = 4
                    )
                ),
                slots = emptyList(),
                bare = true,
                safeFit = true
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

    // 6. ltr-mirrorsplit — Mirror Split (replaces ltr-split)
    PostTemplateSpec(
        id = "ltr-mirrorsplit",
        name = "Mirror Split",
        family = "Letter",
        slots = listOf(PostSlots.LETTER, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.LETTER to "Q",
            PostSlots.EYEBROW to "DUAL PERSPECTIVE",
            PostSlots.HEADLINE to "QUESTION EVERYTHING.",
            PostSlots.BODY to "Two worlds collide across a razor-sharp seam."
        ),
        looks = listOf(
            TemplateLook("Gold razor", "#F59E0B", "#070A12", PostBase.DARK),
            TemplateLook("Mint razor", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Crimson razor", "#F43F5E", "#12060A", PostBase.DARK),
            TemplateLook("Cobalt paper", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.BLOCK, 0f, 0f, 0.50f, 1f, PostTone.DEEP, panel = true),
                    DecorBox(DecorShape.BLOCK, 0.50f, 0f, 0.50f, 1f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.SLANT, 0.485f, 0.06f, 0.03f, 0.82f, PostTone.LIGHT, alpha = 220, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.11f, w = 0.38f, h = 0.045f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.10f, y = 0.16f, w = 0.80f, h = 0.52f,
                        size = 660f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.GLITCH_SPLIT, fxTo = PostTone.DEEP,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.HARD)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.53f, y = 0.70f, w = 0.41f, h = 0.12f,
                        size = 54f, font = PostFont.DISPLAY, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 0.96f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.53f, y = 0.83f, w = 0.41f, h = 0.07f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.BLOCK, 0f, 0f, 0.50f, 1f, PostTone.DEEP, panel = true),
                    DecorBox(DecorShape.BLOCK, 0.50f, 0f, 0.50f, 1f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.SLANT, 0.485f, 0.06f, 0.03f, 0.82f, PostTone.LIGHT, alpha = 220, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.12f, w = 0.38f, h = 0.05f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.14f, y = 0.13f, w = 0.72f, h = 0.58f,
                        size = 620f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.GLITCH_SPLIT, fxTo = PostTone.DEEP,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.HARD)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.54f, y = 0.67f, w = 0.40f, h = 0.13f,
                        size = 50f, font = PostFont.DISPLAY, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 0.96f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.54f, y = 0.81f, w = 0.40f, h = 0.08f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.BLOCK, 0f, 0f, 0.50f, 1f, PostTone.DEEP, panel = true),
                    DecorBox(DecorShape.BLOCK, 0.50f, 0f, 0.50f, 1f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.SLANT, 0.488f, 0.06f, 0.024f, 0.82f, PostTone.LIGHT, alpha = 220, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.05f, y = 0.13f, w = 0.36f, h = 0.055f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.LETTER,
                        x = 0.20f, y = 0.08f, w = 0.60f, h = 0.74f,
                        size = 620f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        fx = TextFx.GLITCH_SPLIT, fxTo = PostTone.DEEP,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.HARD)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.55f, y = 0.60f, w = 0.39f, h = 0.16f,
                        size = 52f, font = PostFont.DISPLAY, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 0.96f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.55f, y = 0.78f, w = 0.39f, h = 0.09f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            )
        )
    }
)
