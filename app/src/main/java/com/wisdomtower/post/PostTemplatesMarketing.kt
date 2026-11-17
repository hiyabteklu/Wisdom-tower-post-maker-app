package com.wisdomtower.post

/**
 * Family 5 — Marketing & Announcements (6 templates: 5 new designs: mkt-poster, mkt-ticket,
 * mkt-download, mkt-notice, mkt-milestone + 1 kept design: mkt-follow).
 * Built for 4:3, 9:16, and 16:9 using PostSize.pick3.
 */
internal val MarketingTemplates: List<PostTemplateSpec> = listOf(
    // 1. mkt-poster — Launch poster
    PostTemplateSpec(
        id = "mkt-poster",
        name = "Launch poster",
        family = "Marketing",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "NEW SEASON LAUNCH",
            PostSlots.HEADLINE to "Enrollment is now open for the 2026 cohort.",
            PostSlots.BODY to "Live classes, structured chapter summaries, and daily exam drills.",
            PostSlots.BUTTON to "JOIN TODAY"
        ),
        looks = listOf(
            TemplateLook("Launch gold", "#F59E0B", "#070910", PostBase.DARK),
            TemplateLook("Crimson stage", "#E11D48", "#0C0609", PostBase.DARK),
            TemplateLook("Paper poster", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                decor = listOf(
                    DecorBox(DecorShape.FRAME, 0.05f, 0.09f, 0.90f, 0.81f, PostTone.ACCENT, alpha = 190, panel = true),
                    DecorBox(DecorShape.BAR, 0.12f, 0.65f, 0.76f, 0.004f, PostTone.ACCENT, alpha = 160, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.16f, y = 0.14f, w = 0.68f, h = 0.045f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.22f, w = 0.80f, h = 0.39f,
                        size = 68f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.03f,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.68f, w = 0.76f, h = 0.09f,
                        size = 25f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.22f, y = 0.80f, w = 0.56f, h = 0.06f,
                        size = 24f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                decor = listOf(
                    DecorBox(DecorShape.FRAME, 0.05f, 0.09f, 0.90f, 0.81f, PostTone.ACCENT, alpha = 190, panel = true),
                    DecorBox(DecorShape.BAR, 0.12f, 0.64f, 0.76f, 0.004f, PostTone.ACCENT, alpha = 160, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.22f, y = 0.13f, w = 0.56f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.21f, w = 0.80f, h = 0.40f,
                        size = 64f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.03f,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.67f, w = 0.76f, h = 0.09f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.28f, y = 0.79f, w = 0.44f, h = 0.068f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                decor = listOf(
                    DecorBox(DecorShape.FRAME, 0.04f, 0.10f, 0.92f, 0.80f, PostTone.ACCENT, alpha = 190, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.26f, y = 0.14f, w = 0.48f, h = 0.055f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.22f, w = 0.80f, h = 0.40f,
                        size = 66f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.03f,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.16f, y = 0.65f, w = 0.68f, h = 0.09f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.34f, y = 0.77f, w = 0.32f, h = 0.075f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            )
        )
    },

    // 2. mkt-ticket — Event ticket
    PostTemplateSpec(
        id = "mkt-ticket",
        name = "Event ticket",
        family = "Marketing",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.NUMBER, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "LIVE SEMINAR PASS",
            PostSlots.HEADLINE to "National Exam Strategy Masterclass",
            PostSlots.BODY to "Sunday · 2:00 PM EAT · Live on Telegram",
            PostSlots.NUMBER to "ADMIT 01",
            PostSlots.BUTTON to "FREE ENTRY"
        ),
        looks = listOf(
            TemplateLook("Gold ticket", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("VIP emerald", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Cream stub", "#B45309", "#FAF5EC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.07f, 0.12f, 0.86f, 0.54f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.07f, 0.12f, 0.86f, 0.012f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.LINE, 0.11f, 0.67f, 0.78f, 0.003f, PostTone.ACCENT, alpha = 180, panel = true),
                    DecorBox(DecorShape.CARD, 0.07f, 0.68f, 0.86f, 0.20f, PostTone.ACCENT, alpha = 240, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.12f, y = 0.17f, w = 0.76f, h = 0.042f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.12f, y = 0.24f, w = 0.76f, h = 0.26f,
                        size = 52f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.53f, w = 0.76f, h = 0.09f,
                        size = 25f, font = PostFont.BOLD, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.12f, y = 0.71f, w = 0.42f, h = 0.14f,
                        size = 54f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.56f, y = 0.73f, w = 0.32f, h = 0.10f,
                        size = 24f, font = PostFont.BOLD, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.DEEP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.18f, 0.62f, 0.64f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.18f, 0.012f, 0.64f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.CARD, 0.69f, 0.18f, 0.25f, 0.64f, PostTone.ACCENT, alpha = 240, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.11f, y = 0.23f, w = 0.52f, h = 0.048f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.11f, y = 0.31f, w = 0.52f, h = 0.32f,
                        size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.11f, y = 0.66f, w = 0.52f, h = 0.11f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.71f, y = 0.26f, w = 0.21f, h = 0.26f,
                        size = 56f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.72f, y = 0.60f, w = 0.19f, h = 0.12f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.DEEP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.18f, 0.64f, 0.64f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.05f, 0.18f, 0.012f, 0.64f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.CARD, 0.70f, 0.18f, 0.25f, 0.64f, PostTone.ACCENT, alpha = 240, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.23f, w = 0.54f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.31f, w = 0.54f, h = 0.32f,
                        size = 48f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.66f, w = 0.54f, h = 0.11f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.72f, y = 0.26f, w = 0.21f, h = 0.26f,
                        size = 58f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.73f, y = 0.60f, w = 0.19f, h = 0.12f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.DEEP
                    )
                )
            )
        )
    },

    // 3. mkt-docstack — Doc Stack (replaces mkt-download)
    PostTemplateSpec(
        id = "mkt-docstack",
        name = "Doc Stack",
        family = "Marketing",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "FREE PDF",
            PostSlots.HEADLINE to "PHYSICS FORMULAS",
            PostSlots.BUTTON to "DOWNLOAD NOW ↓"
        ),
        looks = listOf(
            TemplateLook("Gold resource", "#F59E0B", "#080B14", PostBase.DARK),
            TemplateLook("Mint resource", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Clean paper", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Violet pack", "#A855F7", "#090614", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.DOC_STACK, 0.16f, 0.16f, 0.68f, 0.48f, PostTone.ACCENT, secondaryTone = PostTone.SOFT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.28f, 0.09f, 0.44f, 0.048f, size = 21f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.66f, 0.84f, 0.12f, size = 96f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BUTTON, 0.22f, 0.80f, 0.56f, 0.062f, size = 24f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.DOC_STACK, 0.06f, 0.14f, 0.42f, 0.72f, PostTone.ACCENT, secondaryTone = PostTone.SOFT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.54f, 0.20f, 0.32f, 0.055f, size = 20f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT),
                    SlotBox(PostSlots.HEADLINE, 0.54f, 0.30f, 0.40f, 0.32f, size = 92f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BUTTON, 0.54f, 0.68f, 0.36f, 0.085f, size = 22f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.DOC_STACK, 0.06f, 0.12f, 0.38f, 0.76f, PostTone.ACCENT, secondaryTone = PostTone.SOFT)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.50f, 0.20f, 0.28f, 0.06f, size = 20f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT),
                    SlotBox(PostSlots.HEADLINE, 0.50f, 0.30f, 0.44f, 0.32f, size = 96f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BUTTON, 0.50f, 0.68f, 0.34f, 0.09f, size = 22f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            )
        )
    },

    // 4. mkt-alerttape — Alert Tape (replaces mkt-notice)
    PostTemplateSpec(
        id = "mkt-alerttape",
        name = "Alert Tape",
        family = "Marketing",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "URGENT ALERT",
            PostSlots.HEADLINE to "BREAKING",
            PostSlots.BUTTON to "SATURDAY · 9:00 AM"
        ),
        looks = listOf(
            TemplateLook("Amber alert", "#F59E0B", "#080A10", PostBase.DARK),
            TemplateLook("Crimson alert", "#EF4444", "#0D0608", PostBase.DARK),
            TemplateLook("Official paper", "#DC2626", "#FAF7F2", PostBase.LIGHT),
            TemplateLook("Cyber lime", "#10B981", "#05100C", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.DIAG,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.TAPE_BAND, 0.0f, 0.14f, 1.0f, 0.72f, PostTone.ACCENT, secondaryTone = PostTone.DARK),
                    DecorBox(DecorShape.CARD, 0.08f, 0.34f, 0.84f, 0.30f, PostTone.DEEP, alpha = 245, panel = true),
                    DecorBox(DecorShape.FRAME, 0.08f, 0.34f, 0.84f, 0.30f, PostTone.ACCENT, alpha = 230)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.24f, 0.10f, 0.52f, 0.045f, size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.HEADLINE, 0.12f, 0.37f, 0.76f, 0.24f, size = 160f, font = PostFont.CONDENSED, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.GLITCH_SPLIT),
                    SlotBox(PostSlots.BUTTON, 0.22f, 0.78f, 0.56f, 0.062f, size = 24f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.DIAG,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.TAPE_BAND, 0.0f, 0.14f, 1.0f, 0.72f, PostTone.ACCENT, secondaryTone = PostTone.DARK),
                    DecorBox(DecorShape.CARD, 0.10f, 0.30f, 0.80f, 0.36f, PostTone.DEEP, alpha = 245, panel = true),
                    DecorBox(DecorShape.FRAME, 0.10f, 0.30f, 0.80f, 0.36f, PostTone.ACCENT, alpha = 230)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.26f, 0.09f, 0.48f, 0.048f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.HEADLINE, 0.14f, 0.33f, 0.72f, 0.30f, size = 160f, font = PostFont.CONDENSED, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.GLITCH_SPLIT),
                    SlotBox(PostSlots.BUTTON, 0.28f, 0.78f, 0.44f, 0.072f, size = 23f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.DIAG,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.TAPE_BAND, 0.0f, 0.12f, 1.0f, 0.76f, PostTone.ACCENT, secondaryTone = PostTone.DARK),
                    DecorBox(DecorShape.CARD, 0.14f, 0.28f, 0.72f, 0.40f, PostTone.DEEP, alpha = 245, panel = true),
                    DecorBox(DecorShape.FRAME, 0.14f, 0.28f, 0.72f, 0.40f, PostTone.ACCENT, alpha = 230)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.30f, 0.08f, 0.40f, 0.052f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.HEADLINE, 0.18f, 0.32f, 0.64f, 0.32f, size = 160f, font = PostFont.CONDENSED, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.GLITCH_SPLIT),
                    SlotBox(PostSlots.BUTTON, 0.34f, 0.78f, 0.32f, 0.08f, size = 23f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            )
        )
    },

    // 5. mkt-monument — Number Monument (replaces mkt-milestone)
    PostTemplateSpec(
        id = "mkt-monument",
        name = "Number Monument",
        family = "Marketing",
        slots = listOf(PostSlots.NUMBER, PostSlots.EYEBROW, PostSlots.HEADLINE),
        sample = mapOf(
            PostSlots.NUMBER to "50K",
            PostSlots.EYEBROW to "MILESTONE UNLOCKED",
            PostSlots.HEADLINE to "THANK YOU"
        ),
        looks = listOf(
            TemplateLook("Celebration gold", "#F59E0B", "#070A12", PostBase.DARK),
            TemplateLook("Emerald milestone", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Royal violet", "#8B5CF6", "#0A0714", PostBase.DARK),
            TemplateLook("Ivory gold", "#D97706", "#FAF6EE", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.STARBURST, 0.08f, 0.12f, 0.84f, 0.56f, PostTone.ACCENT, alpha = 150),
                    DecorBox(DecorShape.MONUMENT_SLAB, 0.12f, 0.18f, 0.76f, 0.44f, PostTone.ACCENT, secondaryTone = PostTone.DEEP, alpha = 240)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.20f, 0.09f, 0.60f, 0.045f, size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.NUMBER, 0.14f, 0.20f, 0.72f, 0.40f, size = 320f, font = PostFont.CONDENSED, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.EXTRUDE, fxTo = PostTone.DARK),
                    SlotBox(PostSlots.HEADLINE, 0.10f, 0.66f, 0.80f, 0.16f, size = 120f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, shadow = TextShadow(strength = 75, mode = ShadowMode.GLOW))
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.STARBURST, 0.08f, 0.12f, 0.84f, 0.58f, PostTone.ACCENT, alpha = 150),
                    DecorBox(DecorShape.MONUMENT_SLAB, 0.14f, 0.18f, 0.72f, 0.46f, PostTone.ACCENT, secondaryTone = PostTone.DEEP, alpha = 240)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.22f, 0.09f, 0.56f, 0.048f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.NUMBER, 0.16f, 0.20f, 0.68f, 0.42f, size = 320f, font = PostFont.CONDENSED, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.EXTRUDE, fxTo = PostTone.DARK),
                    SlotBox(PostSlots.HEADLINE, 0.12f, 0.67f, 0.76f, 0.16f, size = 110f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, shadow = TextShadow(strength = 75, mode = ShadowMode.GLOW))
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.STARBURST, 0.06f, 0.10f, 0.46f, 0.80f, PostTone.ACCENT, alpha = 150),
                    DecorBox(DecorShape.MONUMENT_SLAB, 0.08f, 0.14f, 0.42f, 0.72f, PostTone.ACCENT, secondaryTone = PostTone.DEEP, alpha = 240)
                ),
                slots = listOf(
                    SlotBox(PostSlots.NUMBER, 0.10f, 0.18f, 0.38f, 0.64f, size = 320f, font = PostFont.CONDENSED, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, fx = TextFx.EXTRUDE, fxTo = PostTone.DARK),
                    SlotBox(PostSlots.EYEBROW, 0.54f, 0.24f, 0.38f, 0.055f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.HEADLINE, 0.54f, 0.34f, 0.40f, 0.34f, size = 110f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER)
                )
            )
        )
    },

    // 6. KEPT: mkt-follow — Follow us
    PostTemplateSpec(
        id = "mkt-follow",
        name = "Follow us",
        family = "Marketing",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "STAY CONNECTED",
            PostSlots.HEADLINE to "Follow Wisdom Tower on every channel",
            PostSlots.BODY to "Daily study cards, live Q&A streams, and full chapter notes.",
            PostSlots.BUTTON to "TAP LINKS BELOW"
        ),
        looks = listOf(
            TemplateLook("Social gold", "#F59E0B", "#070910", PostBase.DARK),
            TemplateLook("Social mint", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Social light", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                social = SocialArea(0.10f, 0.44f, 0.80f, 0.34f),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.18f, y = 0.12f, w = 0.64f, h = 0.042f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.18f, w = 0.84f, h = 0.14f,
                        size = 48f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.33f, w = 0.80f, h = 0.08f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.22f, y = 0.81f, w = 0.56f, h = 0.06f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                social = SocialArea(0.10f, 0.44f, 0.80f, 0.34f),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.20f, y = 0.11f, w = 0.60f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.17f, w = 0.84f, h = 0.15f,
                        size = 44f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.33f, w = 0.76f, h = 0.08f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.28f, y = 0.81f, w = 0.44f, h = 0.068f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                social = SocialArea(0.50f, 0.20f, 0.44f, 0.62f),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.20f, w = 0.38f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.28f, w = 0.40f, h = 0.26f,
                        size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.06f, y = 0.56f, w = 0.40f, h = 0.12f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.06f, y = 0.72f, w = 0.32f, h = 0.08f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            )
        )
    },

    // 7. mkt-countdown — Big "days left" number plus a button
    PostTemplateSpec(
        id = "mkt-countdown",
        name = "Countdown alert",
        family = "Marketing",
        slots = listOf(PostSlots.EYEBROW, PostSlots.NUMBER, PostSlots.WORD, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "FINAL REGISTRATION WINDOW",
            PostSlots.NUMBER to "03",
            PostSlots.WORD to "DAYS LEFT",
            PostSlots.HEADLINE to "National Exam Prep Cohort Closes Soon",
            PostSlots.BODY to "Secure your study pack, mock exams, and live tutoring seat before midnight.",
            PostSlots.BUTTON to "REGISTER NOW →"
        ),
        looks = listOf(
            TemplateLook("Urgent amber", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Signal crimson", "#EF4444", "#0B0709", PostBase.DARK),
            TemplateLook("Paper alert", "#DC2626", "#FAF7F2", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.20f, 0.14f, 0.60f, 0.30f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.FRAME, 0.20f, 0.14f, 0.60f, 0.30f, PostTone.ACCENT, alpha = 220),
                    DecorBox(DecorShape.PILL, 0.30f, 0.41f, 0.40f, 0.052f, PostTone.ACCENT, alpha = 255)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.12f, y = 0.08f, w = 0.76f, h = 0.04f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.24f, y = 0.16f, w = 0.52f, h = 0.22f,
                        size = 156f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, shadow = TextShadow(strength = 65)
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.32f, y = 0.415f, w = 0.36f, h = 0.042f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.49f, w = 0.84f, h = 0.16f,
                        size = 50f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.67f, w = 0.80f, h = 0.09f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.18f, y = 0.79f, w = 0.64f, h = 0.068f,
                        size = 24f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.18f, 0.36f, 0.56f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.FRAME, 0.06f, 0.18f, 0.36f, 0.56f, PostTone.ACCENT, alpha = 220),
                    DecorBox(DecorShape.PILL, 0.10f, 0.65f, 0.28f, 0.06f, PostTone.ACCENT, alpha = 255)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.08f, y = 0.22f, w = 0.32f, h = 0.38f,
                        size = 148f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, shadow = TextShadow(strength = 65)
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.11f, y = 0.655f, w = 0.26f, h = 0.05f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.46f, y = 0.18f, w = 0.48f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.46f, y = 0.25f, w = 0.48f, h = 0.26f,
                        size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.46f, y = 0.53f, w = 0.48f, h = 0.12f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.46f, y = 0.68f, w = 0.40f, h = 0.075f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.16f, 0.34f, 0.64f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.FRAME, 0.05f, 0.16f, 0.34f, 0.64f, PostTone.ACCENT, alpha = 220),
                    DecorBox(DecorShape.PILL, 0.09f, 0.69f, 0.26f, 0.07f, PostTone.ACCENT, alpha = 255)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.07f, y = 0.20f, w = 0.30f, h = 0.44f,
                        size = 148f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, shadow = TextShadow(strength = 65)
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.10f, y = 0.695f, w = 0.24f, h = 0.055f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.44f, y = 0.18f, w = 0.50f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.44f, y = 0.26f, w = 0.50f, h = 0.28f,
                        size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.44f, y = 0.56f, w = 0.50f, h = 0.12f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.44f, y = 0.71f, w = 0.36f, h = 0.085f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            )
        )
    },

    // 8. mkt-seal — Round seal for results or admissions announcements
    PostTemplateSpec(
        id = "mkt-seal",
        name = "Official seal",
        family = "Marketing",
        slots = listOf(PostSlots.EYEBROW, PostSlots.NUMBER, PostSlots.WORD, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "OFFICIAL ADMISSIONS & RESULTS",
            PostSlots.NUMBER to "99.4%",
            PostSlots.WORD to "PASS RATE",
            PostSlots.HEADLINE to "2026 National Exam Honour Roll Announced",
            PostSlots.BODY to "Congratulations to all Wisdom Tower scholars who achieved top university placement.",
            PostSlots.BUTTON to "VIEW HONOUR ROLL"
        ),
        looks = listOf(
            TemplateLook("Gold seal", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Emerald seal", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Royal parchment", "#B45309", "#FAF5EC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.DISC, 0.28f, 0.15f, 0.44f, 0.25f, PostTone.ACCENT, alpha = 245, panel = true),
                    DecorBox(DecorShape.RING, 0.25f, 0.135f, 0.50f, 0.28f, PostTone.ACCENT, alpha = 210)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.08f, w = 0.80f, h = 0.04f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.31f, y = 0.19f, w = 0.38f, h = 0.11f,
                        size = 68f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.33f, y = 0.31f, w = 0.34f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.45f, w = 0.84f, h = 0.18f,
                        size = 50f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.65f, w = 0.80f, h = 0.10f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.20f, y = 0.78f, w = 0.60f, h = 0.068f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.DISC, 0.07f, 0.22f, 0.34f, 0.46f, PostTone.ACCENT, alpha = 245, panel = true),
                    DecorBox(DecorShape.RING, 0.05f, 0.19f, 0.38f, 0.52f, PostTone.ACCENT, alpha = 210)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.10f, y = 0.31f, w = 0.28f, h = 0.18f,
                        size = 64f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.11f, y = 0.51f, w = 0.26f, h = 0.06f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.46f, y = 0.18f, w = 0.48f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.46f, y = 0.25f, w = 0.48f, h = 0.26f,
                        size = 46f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.46f, y = 0.53f, w = 0.48f, h = 0.13f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.46f, y = 0.69f, w = 0.40f, h = 0.075f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.DISC, 0.07f, 0.20f, 0.30f, 0.54f, PostTone.ACCENT, alpha = 245, panel = true),
                    DecorBox(DecorShape.RING, 0.05f, 0.17f, 0.34f, 0.60f, PostTone.ACCENT, alpha = 210)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.09f, y = 0.30f, w = 0.26f, h = 0.20f,
                        size = 64f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.10f, y = 0.52f, w = 0.24f, h = 0.06f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.43f, y = 0.18f, w = 0.50f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.43f, y = 0.26f, w = 0.50f, h = 0.28f,
                        size = 46f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.43f, y = 0.56f, w = 0.50f, h = 0.13f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.43f, y = 0.72f, w = 0.36f, h = 0.085f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            )
        )
    },

    // 9. mkt-floating-hero — Floating Object Hero (P7 bonus)
    PostTemplateSpec(
        id = "mkt-floating-hero",
        name = "Floating Object Hero",
        family = "Marketing",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "NEW DROP",
            PostSlots.HEADLINE to "FUTURE READY",
            PostSlots.BUTTON to "EXPLORE NOW"
        ),
        looks = listOf(
            TemplateLook("Gold object", "#F59E0B", "#070910", PostBase.DARK, cutoutStyle = CutoutStyle.RIM_GLOW),
            TemplateLook("Emerald object", "#10B981", "#05100C", PostBase.DARK, cutoutStyle = CutoutStyle.STICKER_EDGE),
            TemplateLook("Studio light", "#2563EB", "#F8FAFC", PostBase.LIGHT, cutoutStyle = CutoutStyle.RIM_GLOW)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                cutoutShowDefault = true,
                cutout = PostPhotoArea(0.24f, 0.24f, 0.52f, 0.44f, rounded = false, rimTone = PostTone.ACCENT, shadow = true),
                decor = listOf(
                    DecorBox(DecorShape.FLOATING_OBJECT, 0.14f, 0.18f, 0.72f, 0.50f, PostTone.ACCENT, secondaryTone = PostTone.DEEP)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.22f, 0.09f, 0.56f, 0.042f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.68f, 0.84f, 0.12f, size = 104f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, shadow = TextShadow(strength = 75, mode = ShadowMode.GLOW)),
                    SlotBox(PostSlots.BUTTON, 0.24f, 0.82f, 0.52f, 0.058f, size = 23f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                cutoutShowDefault = true,
                cutout = PostPhotoArea(0.50f, 0.18f, 0.42f, 0.64f, rounded = false, rimTone = PostTone.ACCENT, shadow = true),
                decor = listOf(
                    DecorBox(DecorShape.FLOATING_OBJECT, 0.46f, 0.14f, 0.48f, 0.72f, PostTone.ACCENT, secondaryTone = PostTone.DEEP)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.07f, 0.22f, 0.36f, 0.048f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.07f, 0.29f, 0.38f, 0.34f, size = 92f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BUTTON, 0.07f, 0.68f, 0.32f, 0.08f, size = 22f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                cutoutShowDefault = true,
                cutout = PostPhotoArea(0.52f, 0.16f, 0.40f, 0.68f, rounded = false, rimTone = PostTone.ACCENT, shadow = true),
                decor = listOf(
                    DecorBox(DecorShape.FLOATING_OBJECT, 0.48f, 0.12f, 0.46f, 0.76f, PostTone.ACCENT, secondaryTone = PostTone.DEEP)
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.07f, 0.22f, 0.38f, 0.052f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.07f, 0.29f, 0.40f, 0.36f, size = 96f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BUTTON, 0.07f, 0.70f, 0.30f, 0.085f, size = 22f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            )
        )
    }
)
