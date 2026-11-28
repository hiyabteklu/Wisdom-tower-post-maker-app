package com.wisdomtower.post

/**
 * Family 5 — Marketing & Announcements (6 templates: 5 new designs: mkt-poster, mkt-ticket,
 * mkt-download, mkt-notice, mkt-milestone + 1 kept design: mkt-follow).
 * Built for 4:3, 9:16, and 16:9 using PostSize.pick3.
 */
internal val MarketingTemplates: List<PostTemplateSpec> = listOf(
    // 1. mkt-poster — Wet Obsidian Mirror Stage (Batch 2 replacement: glossy studio floor auto-reflecting cutout PNG)
    PostTemplateSpec(
        id = "mkt-poster",
        name = "Wet Obsidian Mirror",
        family = "Marketing",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "NEW SEASON LAUNCH · 2026 COHORT",
            PostSlots.HEADLINE to "Built for top scholars.",
            PostSlots.BODY to "Insert any BG-removed person, book, or device — the wet studio mirror floor reflects it automatically.",
            PostSlots.BUTTON to "EXPLORE NOW"
        ),
        looks = listOf(
            TemplateLook("Obsidian gold", "#F59E0B", "#05070D", PostBase.DARK),
            TemplateLook("Emerald mirror", "#10B981", "#040D0B", PostBase.DARK),
            TemplateLook("Cobalt keynote", "#38BDF8", "#050B18", PostBase.DARK),
            TemplateLook("Marble studio", "#2563EB", "#F5F7FC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.18f, 0.22f, 0.64f, 0.40f),
                cutoutShowDefault = true,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.OBSIDIAN_MIRROR_STAGE,
                        x = 0.0f, y = 0.62f, w = 1.0f, h = 0.38f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        layer = DecorLayer.BEHIND_PHOTO, panel = true
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.13f, 0.76f, 0.036f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.66f, 0.84f, 0.080f, 52f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.12f, 0.748f, 0.76f, 0.048f, 22f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BUTTON, 0.26f, 0.808f, 0.48f, 0.050f, 21f, PostFont.BOLD, PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.24f, 0.15f, 0.52f, 0.49f),
                cutoutShowDefault = true,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.OBSIDIAN_MIRROR_STAGE,
                        x = 0.0f, y = 0.64f, w = 1.0f, h = 0.36f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        layer = DecorLayer.BEHIND_PHOTO, panel = true
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.06f, 0.76f, 0.040f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.68f, 0.84f, 0.105f, 54f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.14f, 0.79f, 0.72f, 0.052f, 22f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BUTTON, 0.32f, 0.858f, 0.36f, 0.056f, 21f, PostFont.BOLD, PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                cutout = CutoutBox(0.50f, 0.10f, 0.42f, 0.58f),
                cutoutShowDefault = true,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.OBSIDIAN_MIRROR_STAGE,
                        x = 0.0f, y = 0.68f, w = 1.0f, h = 0.32f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        layer = DecorLayer.BEHIND_PHOTO, panel = true
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.16f, 0.40f, 0.048f, 20f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.24f, 0.42f, 0.30f, 58f, PostFont.DISPLAY, PostTone.TEXT, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.06f, 0.56f, 0.40f, 0.14f, 23f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL),
                    SlotBox(PostSlots.BUTTON, 0.06f, 0.74f, 0.28f, 0.082f, 21f, PostFont.BOLD, PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            )
        )
    },

    // 2. mkt-ticket — 3D Student Scholar ID & VIP Lanyard Pass (2 photo slots)
    PostTemplateSpec(
        id = "mkt-ticket",
        name = "3D Scholar ID Lanyard",
        family = "Marketing",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "LIVE SEMINAR & SCHOLAR PASS",
            PostSlots.HEADLINE to "National Exam Strategy Masterclass",
            PostSlots.BODY to "Sunday · 2:00 PM EAT · Verified Student Access",
            PostSlots.BUTTON to "CLAIM FREE PASS"
        ),
        looks = listOf(
            TemplateLook("Gold pass", "#F59E0B", "#071529", PostBase.DARK),
            TemplateLook("VIP emerald", "#10B981", "#051410", PostBase.DARK),
            TemplateLook("Cobalt badge", "#38BDF8", "#071830", PostBase.DARK),
            TemplateLook("Cream stub", "#2563EB", "#F5F7FC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.04f, y = 0.18f, w = 0.92f, h = 0.46f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 18
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.13f, 0.76f, 0.036f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.655f, 0.84f, 0.08f, 44f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.12f, 0.742f, 0.76f, 0.042f, 22f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BUTTON, 0.24f, 0.80f, 0.52f, 0.052f, 22f, PostFont.BOLD, PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.06f, y = 0.11f, w = 0.88f, h = 0.57f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 18
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.05f, 0.80f, 0.04f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.70f, 0.84f, 0.10f, 46f, PostFont.DISPLAY, PostTone.TEXT, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.12f, 0.81f, 0.76f, 0.045f, 22f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BUTTON, 0.30f, 0.87f, 0.40f, 0.058f, 21f, PostFont.BOLD, PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.42f, y = 0.06f, w = 0.54f, h = 0.88f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 18
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.16f, 0.34f, 0.05f, 20f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.24f, 0.34f, 0.32f, 50f, PostFont.DISPLAY, PostTone.TEXT, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.06f, 0.59f, 0.34f, 0.14f, 23f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL),
                    SlotBox(PostSlots.BUTTON, 0.06f, 0.76f, 0.28f, 0.08f, 21f, PostFont.BOLD, PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
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

    // 8. mkt-seal — 3D Framed Certificate of Excellence & Wax Seal Plaque (2 photo slots)
    PostTemplateSpec(
        id = "mkt-seal",
        name = "3D Honor Certificate",
        family = "Marketing",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "OFFICIAL ADMISSIONS & RESULTS",
            PostSlots.HEADLINE to "2026 National Exam Honour Roll Announced",
            PostSlots.BODY to "Congratulations to all scholars who achieved top university placement.",
            PostSlots.BUTTON to "VIEW HONOUR ROLL"
        ),
        looks = listOf(
            TemplateLook("Gold seal", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Emerald seal", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Cobalt seal", "#38BDF8", "#071830", PostBase.DARK),
            TemplateLook("Royal parchment", "#B45309", "#FAF5EC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.04f, y = 0.19f, w = 0.92f, h = 0.45f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 19
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.135f, 0.76f, 0.036f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.655f, 0.84f, 0.08f, 44f, PostFont.SERIF, PostTone.TEXT, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.12f, 0.742f, 0.76f, 0.042f, 22f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BUTTON, 0.24f, 0.80f, 0.52f, 0.052f, 22f, PostFont.BOLD, PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.06f, y = 0.12f, w = 0.88f, h = 0.56f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 19
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.10f, 0.05f, 0.80f, 0.04f, 20f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.70f, 0.84f, 0.10f, 46f, PostFont.SERIF, PostTone.TEXT, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.12f, 0.81f, 0.76f, 0.045f, 22f, PostFont.REGULAR, PostTone.MUTED, centered = true, fit = FitMode.FILL),
                    SlotBox(PostSlots.BUTTON, 0.30f, 0.87f, 0.40f, 0.058f, 21f, PostFont.BOLD, PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
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
                        variant = 19
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.16f, 0.34f, 0.05f, 20f, PostFont.BOLD, PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.24f, 0.34f, 0.32f, 50f, PostFont.SERIF, PostTone.TEXT, fit = FitMode.FILL),
                    SlotBox(PostSlots.BODY, 0.06f, 0.59f, 0.34f, 0.14f, 23f, PostFont.REGULAR, PostTone.MUTED, fit = FitMode.FILL),
                    SlotBox(PostSlots.BUTTON, 0.06f, 0.76f, 0.28f, 0.08f, 21f, PostFont.BOLD, PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            )
        )
    },

    // 9. mkt-floating-hero — 3D Course / Subject Collector's Box Set (1..3 dynamic photo slots)
    PostTemplateSpec(
        id = "mkt-floating-hero",
        name = "3D Study Kit Box",
        family = "Marketing",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "COMPLETE EXAM PREP EDITION",
            PostSlots.HEADLINE to "ALL-IN-ONE STUDY KIT",
            PostSlots.BUTTON to "GET THE FULL PACK"
        ),
        looks = listOf(
            TemplateLook("Gold edition", "#F59E0B", "#071529", PostBase.DARK),
            TemplateLook("Emerald edition", "#10B981", "#051410", PostBase.DARK),
            TemplateLook("Cobalt edition", "#38BDF8", "#071830", PostBase.DARK),
            TemplateLook("Studio light", "#2563EB", "#F5F7FC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.04f, y = 0.18f, w = 0.92f, h = 0.48f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 16
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.14f, 0.125f, 0.72f, 0.038f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.675f, 0.84f, 0.10f, size = 76f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER),
                    SlotBox(PostSlots.BUTTON, 0.24f, 0.80f, 0.52f, 0.055f, size = 22f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.06f, y = 0.12f, w = 0.88f, h = 0.60f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 16
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.12f, 0.055f, 0.76f, 0.04f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.08f, 0.735f, 0.84f, 0.11f, size = 72f, font = PostFont.DISPLAY, tone = PostTone.TEXT, centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER),
                    SlotBox(PostSlots.BUTTON, 0.30f, 0.865f, 0.40f, 0.06f, size = 21f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(
                        shape = DecorShape.DEVICE_3D,
                        x = 0.42f, y = 0.07f, w = 0.54f, h = 0.86f,
                        tone = PostTone.ACCENT, secondaryTone = PostTone.DEEP,
                        variant = 16
                    )
                ),
                slots = listOf(
                    SlotBox(PostSlots.EYEBROW, 0.06f, 0.20f, 0.34f, 0.05f, size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f),
                    SlotBox(PostSlots.HEADLINE, 0.06f, 0.28f, 0.34f, 0.36f, size = 84f, font = PostFont.DISPLAY, tone = PostTone.TEXT, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.94f),
                    SlotBox(PostSlots.BUTTON, 0.06f, 0.70f, 0.30f, 0.085f, size = 22f, font = PostFont.BOLD, tone = PostTone.DARK, centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT)
                )
            )
        )
    }
)
