package com.wisdomtower.post

/**
 * Family 2 — Kinetic & Expressive Type (7 templates: type-ink, type-extrude, type-spotlight,
 * type-chips, type-grid, type-aurora, type-ticker).
 * Built for 4:3, 9:16, and 16:9 using PostSize.pick3.
 */
internal val TypeTemplates: List<PostTemplateSpec> = listOf(
    // 1. type-ink — Ink & echo
    PostTemplateSpec(
        id = "type-ink",
        name = "Ink & echo",
        family = "Type",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "BOLD STATEMENT",
            PostSlots.HEADLINE to "BUILD ONCE\nSHARE DAILY\nGROW FAST",
            PostSlots.BODY to "Stacked kinetic typography with outline echo lines for high-impact announcements.",
            PostSlots.BUTTON to "START NOW"
        ),
        looks = listOf(
            TemplateLook("Gold ink", "#F59E0B", "#06080E", PostBase.DARK),
            TemplateLook("Neon lime", "#84CC16", "#070B06", PostBase.DARK),
            TemplateLook("Swiss paper", "#DC2626", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.11f, w = 0.44f, h = 0.042f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.17f, w = 0.86f, h = 0.52f,
                        size = 120f, font = PostFont.CONDENSED, tone = PostTone.TEXT,
                        caps = true, fit = FitMode.STACK, anchor = VAnchor.CENTER,
                        leading = 0.94f, stackFx = listOf(TextFx.SOLID, TextFx.OUTLINE, TextFx.SOLID, TextFx.OUTLINE)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.07f, y = 0.72f, w = 0.86f, h = 0.09f,
                        size = 25f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.07f, y = 0.83f, w = 0.44f, h = 0.055f,
                        size = 24f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.11f, w = 0.36f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.18f, w = 0.88f, h = 0.52f,
                        size = 115f, font = PostFont.CONDENSED, tone = PostTone.TEXT,
                        caps = true, fit = FitMode.STACK, anchor = VAnchor.CENTER,
                        leading = 0.94f, stackFx = listOf(TextFx.SOLID, TextFx.OUTLINE, TextFx.SOLID, TextFx.OUTLINE)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.06f, y = 0.73f, w = 0.60f, h = 0.14f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.18f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.69f, y = 0.75f, w = 0.25f, h = 0.09f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.05f, y = 0.12f, w = 0.28f, h = 0.055f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.05f, y = 0.20f, w = 0.56f, h = 0.66f,
                        size = 118f, font = PostFont.CONDENSED, tone = PostTone.TEXT,
                        caps = true, fit = FitMode.STACK, anchor = VAnchor.CENTER,
                        leading = 0.94f, stackFx = listOf(TextFx.SOLID, TextFx.OUTLINE, TextFx.SOLID, TextFx.OUTLINE)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.65f, y = 0.34f, w = 0.30f, h = 0.30f,
                        size = 25f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.65f, y = 0.68f, w = 0.26f, h = 0.10f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            )
        )
    },

    // 2. type-extrude — 3D block type
    PostTemplateSpec(
        id = "type-extrude",
        name = "3D block type",
        family = "Type",
        slots = listOf(PostSlots.WORD, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.WORD to "CREATE",
            PostSlots.EYEBROW to "3D BLOCK SERIES",
            PostSlots.HEADLINE to "Make your message jump off the screen with extruded block lettering.",
            PostSlots.BODY to "Layered offset copies create crisp vintage poster depth."
        ),
        looks = listOf(
            TemplateLook("Gold block", "#F59E0B", "#080B14", PostBase.DARK),
            TemplateLook("Cyan pop", "#06B6D4", "#06101E", PostBase.DARK),
            TemplateLook("Retro cream", "#EA580C", "#FAF5EC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.07f, 0.64f, 0.86f, 0.25f, PostTone.DEEP, alpha = 230, panel = true),
                    DecorBox(DecorShape.BAR, 0.07f, 0.64f, 0.86f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.15f, y = 0.12f, w = 0.70f, h = 0.045f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.06f, y = 0.20f, w = 0.88f, h = 0.38f,
                        size = 280f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        rotate = -3f, fx = TextFx.EXTRUDE, fxTo = PostTone.ACCENT,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.HARD)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.11f, y = 0.67f, w = 0.78f, h = 0.11f,
                        size = 38f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.11f, y = 0.79f, w = 0.78f, h = 0.08f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.07f, 0.63f, 0.86f, 0.26f, PostTone.DEEP, alpha = 230, panel = true),
                    DecorBox(DecorShape.BAR, 0.07f, 0.63f, 0.86f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.20f, y = 0.11f, w = 0.60f, h = 0.048f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.06f, y = 0.18f, w = 0.88f, h = 0.41f,
                        size = 280f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        rotate = -3f, fx = TextFx.EXTRUDE, fxTo = PostTone.ACCENT,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.HARD)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.11f, y = 0.66f, w = 0.78f, h = 0.11f,
                        size = 36f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.11f, y = 0.78f, w = 0.78f, h = 0.08f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.RAYS,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.10f, 0.64f, 0.80f, 0.24f, PostTone.DEEP, alpha = 230, panel = true),
                    DecorBox(DecorShape.BAR, 0.10f, 0.64f, 0.80f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.25f, y = 0.11f, w = 0.50f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.06f, y = 0.18f, w = 0.88f, h = 0.42f,
                        size = 290f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        rotate = -3f, fx = TextFx.EXTRUDE, fxTo = PostTone.ACCENT,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.HARD)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.14f, y = 0.67f, w = 0.72f, h = 0.10f,
                        size = 36f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.14f, y = 0.78f, w = 0.72f, h = 0.08f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            )
        )
    },

    // 3. type-spotlightcut — 3D Leaderboard Podium (replaces Spotlight Cut)
    PostTemplateSpec(
        id = "type-spotlightcut",
        name = "3D Leaderboard Podium",
        family = "Type",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "TOP SCHOLARS · WEEKLY RANK",
            PostSlots.HEADLINE to "CLIMB THE NATIONAL LEADERBOARD.",
            PostSlots.BODY to "3D 1st, 2nd, and 3rd place champion podiums with student avatars & XP crowns."
        ),
        looks = listOf(
            TemplateLook("Gold champion", "#F59E0B", "#050811", PostBase.DARK),
            TemplateLook("Emerald league", "#10B981", "#040E0B", PostBase.DARK),
            TemplateLook("Electric cyber", "#38BDF8", "#040A16", PostBase.DARK),
            TemplateLook("Crimson arena", "#F43F5E", "#0F0508", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.08f, 0.28f, 0.84f, 0.54f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.05f, 0.27f, 0.90f, 0.54f, PostTone.ACCENT, panel = true, variant = 29)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.22f, y = 0.08f, w = 0.56f, h = 0.042f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.14f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.135f, w = 0.86f, h = 0.12f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        leading = 1.02f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.83f, w = 0.80f, h = 0.08f,
                        size = 26f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.08f, 0.26f, 0.84f, 0.56f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.06f, 0.26f, 0.88f, 0.55f, PostTone.ACCENT, panel = true, variant = 29)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.24f, y = 0.07f, w = 0.52f, h = 0.045f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.14f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.125f, w = 0.86f, h = 0.12f,
                        size = 52f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        leading = 1.02f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.83f, w = 0.76f, h = 0.08f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.44f, 0.10f, 0.52f, 0.78f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.44f, 0.10f, 0.52f, 0.78f, PostTone.ACCENT, panel = true, variant = 29)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.18f, w = 0.34f, h = 0.055f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.14f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.27f, w = 0.36f, h = 0.34f,
                        size = 54f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        leading = 1.02f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.06f, y = 0.65f, w = 0.36f, h = 0.16f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            )
        )
    },

    // 4. type-chips — Sticker chips
    PostTemplateSpec(
        id = "type-chips",
        name = "Sticker chips",
        family = "Type",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "MINDSET",
            PostSlots.HEADLINE to "THINK CLEARLY\nACT BOLDLY\nSTAY HUMBLE",
            PostSlots.BODY to "Each line renders inside its own high-contrast sticker pill with subtle tilt."
        ),
        looks = listOf(
            TemplateLook("Gold stickers", "#F59E0B", "#080C14", PostBase.DARK),
            TemplateLook("Violet pop", "#8B5CF6", "#0A0714", PostBase.DARK),
            TemplateLook("Warm paper", "#EA580C", "#FAF5EC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.MESH,
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.15f, y = 0.12f, w = 0.70f, h = 0.045f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.20f, w = 0.88f, h = 0.52f,
                        size = 78f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.CHIPS, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.76f, w = 0.80f, h = 0.12f,
                        size = 25f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.MESH,
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.20f, y = 0.11f, w = 0.60f, h = 0.05f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.18f, w = 0.88f, h = 0.56f,
                        size = 76f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.CHIPS, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.77f, w = 0.80f, h = 0.11f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.MESH,
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.25f, y = 0.11f, w = 0.50f, h = 0.055f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.18f, w = 0.84f, h = 0.56f,
                        size = 78f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.CHIPS, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.15f, y = 0.77f, w = 0.70f, h = 0.11f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            )
        )
    },

    // 5. type-grid — 3D Exam Countdown Flip-Clock (replaces Swiss type grid)
    PostTemplateSpec(
        id = "type-grid",
        name = "3D Exam Countdown Flip-Clock",
        family = "Type",
        slots = listOf(PostSlots.NUMBER, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.NUMBER to "14",
            PostSlots.EYEBROW to "FINAL COUNTDOWN · NATIONAL EXAM",
            PostSlots.HEADLINE to "Every hour counts toward your target score.",
            PostSlots.BODY to "3D mechanical desk flip-clock with split-flap study schedule & exam checklist."
        ),
        looks = listOf(
            TemplateLook("Chrono gold", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Crimson alert", "#EF4444", "#0E070A", PostBase.DARK),
            TemplateLook("Cobalt studio", "#38BDF8", "#060B16", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.08f, 0.27f, 0.84f, 0.54f, PostTone.ACCENT, alpha = 140, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.05f, 0.27f, 0.90f, 0.54f, PostTone.ACCENT, panel = true, variant = 30)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.16f, y = 0.08f, w = 0.68f, h = 0.042f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.14f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.135f, w = 0.86f, h = 0.12f,
                        size = 52f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.83f, w = 0.80f, h = 0.08f,
                        size = 25f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.08f, 0.26f, 0.84f, 0.55f, PostTone.ACCENT, alpha = 140, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.06f, 0.26f, 0.88f, 0.55f, PostTone.ACCENT, panel = true, variant = 30)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.18f, y = 0.07f, w = 0.64f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.14f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.125f, w = 0.86f, h = 0.12f,
                        size = 50f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.83f, w = 0.76f, h = 0.08f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.44f, 0.10f, 0.52f, 0.78f, PostTone.ACCENT, alpha = 140, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.44f, 0.10f, 0.52f, 0.78f, PostTone.ACCENT, panel = true, variant = 30)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.18f, w = 0.34f, h = 0.055f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.14f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.27f, w = 0.36f, h = 0.34f,
                        size = 52f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.06f, y = 0.65f, w = 0.36f, h = 0.16f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            )
        )
    },

    // 6. type-auroraword — 3D Holographic Mastery Poster (replaces Aurora Word)
    PostTemplateSpec(
        id = "type-auroraword",
        name = "3D Holographic Mastery Poster",
        family = "Type",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "MASTERY EDITION · 99.4% SCORE",
            PostSlots.HEADLINE to "UNLOCK FULL SUBJECT MASTERY.",
            PostSlots.BODY to "3D holographic acrylic mastery frame with floating analytics and live app preview."
        ),
        looks = listOf(
            TemplateLook("Aurora gold", "#F59E0B", "#060812", PostBase.DARK),
            TemplateLook("Northern teal", "#14B8A6", "#040C12", PostBase.DARK),
            TemplateLook("Sunset violet", "#EC4899", "#0C0514", PostBase.DARK),
            TemplateLook("Cobalt chrome", "#38BDF8", "#050A18", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.MESH,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.06f, 0.26f, 0.88f, 0.55f, PostTone.ACCENT, alpha = 175, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.05f, 0.26f, 0.90f, 0.55f, PostTone.ACCENT, panel = true, variant = 31)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.18f, y = 0.08f, w = 0.64f, h = 0.042f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.16f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.135f, w = 0.86f, h = 0.115f,
                        size = 54f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        leading = 1.02f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.83f, w = 0.80f, h = 0.08f,
                        size = 25f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.MESH,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.08f, 0.25f, 0.84f, 0.56f, PostTone.ACCENT, alpha = 175, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.06f, 0.25f, 0.88f, 0.56f, PostTone.ACCENT, panel = true, variant = 31)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.20f, y = 0.07f, w = 0.60f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.16f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.125f, w = 0.86f, h = 0.115f,
                        size = 52f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        leading = 1.02f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.83f, w = 0.76f, h = 0.08f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.MESH,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.44f, 0.10f, 0.52f, 0.78f, PostTone.ACCENT, alpha = 175, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.44f, 0.10f, 0.52f, 0.78f, PostTone.ACCENT, panel = true, variant = 31)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.18f, w = 0.34f, h = 0.055f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.16f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.27f, w = 0.36f, h = 0.34f,
                        size = 54f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        leading = 1.02f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.06f, y = 0.65f, w = 0.36f, h = 0.16f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            )
        )
    },

    // 7. type-ticker — 3D Subject Bento Grid Poster (replaces Diagonal ticker band)
    PostTemplateSpec(
        id = "type-ticker",
        name = "3D Subject Bento Grid Poster",
        family = "Type",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "ALL-IN-ONE STUDY ECOSYSTEM",
            PostSlots.HEADLINE to "Every subject organized in a modular 3D workspace.",
            PostSlots.BODY to "Insert 1 to 4 study screens into the tilted 3D glassmorphic bento dashboard."
        ),
        looks = listOf(
            TemplateLook("Gold bento", "#F59E0B", "#070A10", PostBase.DARK),
            TemplateLook("Emerald bento", "#10B981", "#050E0B", PostBase.DARK),
            TemplateLook("Cobalt bento", "#38BDF8", "#050B16", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.06f, 0.26f, 0.88f, 0.55f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.05f, 0.26f, 0.90f, 0.55f, PostTone.ACCENT, panel = true, variant = 32)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.18f, y = 0.08f, w = 0.64f, h = 0.042f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.14f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.135f, w = 0.86f, h = 0.115f,
                        size = 52f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.83f, w = 0.80f, h = 0.08f,
                        size = 25f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.08f, 0.25f, 0.84f, 0.56f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.06f, 0.25f, 0.88f, 0.56f, PostTone.ACCENT, panel = true, variant = 32)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.20f, y = 0.07f, w = 0.60f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.14f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.125f, w = 0.86f, h = 0.115f,
                        size = 50f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.83f, w = 0.76f, h = 0.08f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.44f, 0.10f, 0.52f, 0.78f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.44f, 0.10f, 0.52f, 0.78f, PostTone.ACCENT, panel = true, variant = 32)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.18f, w = 0.34f, h = 0.055f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.14f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.27f, w = 0.36f, h = 0.34f,
                        size = 52f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.06f, y = 0.65f, w = 0.36f, h = 0.16f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            )
        )
    },

    // 8. type-neon — Glowing outlined headline stack
    PostTemplateSpec(
        id = "type-neon",
        name = "Neon outline stack",
        family = "Type",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "NIGHT SESSION · ብርሃን",
            PostSlots.HEADLINE to "THINK\nBOLDLY\nEVERY DAY",
            PostSlots.BODY to "Glowing outlined headline stack built for high-contrast night feeds."
        ),
        looks = listOf(
            TemplateLook("Cyber cyan", "#22D3EE", "#040812", PostBase.DARK),
            TemplateLook("Electric gold", "#FACC15", "#08070C", PostBase.DARK),
            TemplateLook("Neon magenta", "#EC4899", "#0C0512", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.10f, 0.18f, 0.80f, 0.50f, PostTone.ACCENT, alpha = 120, panel = true),
                    DecorBox(DecorShape.FRAME, 0.06f, 0.12f, 0.88f, 0.74f, PostTone.ACCENT, alpha = 180, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.14f, y = 0.16f, w = 0.72f, h = 0.042f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.16f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.23f, w = 0.80f, h = 0.44f,
                        size = 110f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.STACK, anchor = VAnchor.CENTER,
                        fx = TextFx.OUTLINE, leading = 0.94f,
                        shadow = TextShadow(strength = 92, mode = ShadowMode.GLOW)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.71f, w = 0.76f, h = 0.11f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.12f, 0.16f, 0.76f, 0.54f, PostTone.ACCENT, alpha = 120, panel = true),
                    DecorBox(DecorShape.FRAME, 0.06f, 0.11f, 0.88f, 0.76f, PostTone.ACCENT, alpha = 180, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.16f, y = 0.15f, w = 0.68f, h = 0.045f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.16f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.22f, w = 0.80f, h = 0.46f,
                        size = 105f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.STACK, anchor = VAnchor.CENTER,
                        fx = TextFx.OUTLINE, leading = 0.94f,
                        shadow = TextShadow(strength = 92, mode = ShadowMode.GLOW)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.14f, y = 0.71f, w = 0.72f, h = 0.11f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.15f, 0.14f, 0.70f, 0.58f, PostTone.ACCENT, alpha = 120, panel = true),
                    DecorBox(DecorShape.FRAME, 0.05f, 0.11f, 0.90f, 0.76f, PostTone.ACCENT, alpha = 180, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.20f, y = 0.15f, w = 0.60f, h = 0.05f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.16f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.22f, w = 0.80f, h = 0.45f,
                        size = 105f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.STACK, anchor = VAnchor.CENTER,
                        fx = TextFx.OUTLINE, leading = 0.94f,
                        shadow = TextShadow(strength = 92, mode = ShadowMode.GLOW)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.16f, y = 0.70f, w = 0.68f, h = 0.12f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            )
        )
    },

    // 9. type-ribbon — 3D Academic Journal Cover Poster (replaces Diagonal ribbon band)
    PostTemplateSpec(
        id = "type-ribbon",
        name = "3D Academic Journal Cover Poster",
        family = "Type",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "SPECIAL EDITION · VOL. 12",
            PostSlots.HEADLINE to "The Future of Smart Study & Exam Prep.",
            PostSlots.BODY to "3D glossy academic journal issue with floating feature pull-out cards.",
            PostSlots.BUTTON to "READ FULL ISSUE"
        ),
        looks = listOf(
            TemplateLook("Journal gold", "#F59E0B", "#07090F", PostBase.DARK),
            TemplateLook("Emerald press", "#10B981", "#050E0B", PostBase.DARK),
            TemplateLook("Crimson review", "#EF4444", "#0C0608", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.06f, 0.25f, 0.88f, 0.54f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.05f, 0.25f, 0.90f, 0.54f, PostTone.ACCENT, panel = true, variant = 33)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.20f, y = 0.08f, w = 0.60f, h = 0.042f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.14f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.13f, w = 0.86f, h = 0.11f,
                        size = 52f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.80f, w = 0.80f, h = 0.06f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.26f, y = 0.87f, w = 0.48f, h = 0.045f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL,
                        chip = PostTone.ACCENT
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.08f, 0.24f, 0.84f, 0.55f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.06f, 0.24f, 0.88f, 0.55f, PostTone.ACCENT, panel = true, variant = 33)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.22f, y = 0.07f, w = 0.56f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.14f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.125f, w = 0.86f, h = 0.11f,
                        size = 50f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.80f, w = 0.76f, h = 0.06f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.28f, y = 0.87f, w = 0.44f, h = 0.046f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL,
                        chip = PostTone.ACCENT
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.GLOW, 0.44f, 0.10f, 0.52f, 0.78f, PostTone.ACCENT, alpha = 150, panel = true),
                    DecorBox(DecorShape.DEVICE_3D, 0.44f, 0.10f, 0.52f, 0.78f, PostTone.ACCENT, panel = true, variant = 33)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.16f, w = 0.34f, h = 0.055f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.14f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.24f, w = 0.36f, h = 0.30f,
                        size = 50f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.06f, y = 0.57f, w = 0.36f, h = 0.14f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.06f, y = 0.75f, w = 0.28f, h = 0.06f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL,
                        chip = PostTone.ACCENT
                    )
                )
            )
        )
    }
)
