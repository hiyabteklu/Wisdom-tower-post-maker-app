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

    // 3. type-spotlight — Spotlight word
    PostTemplateSpec(
        id = "type-spotlight",
        name = "Spotlight word",
        family = "Type",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "KEY INSIGHT",
            PostSlots.HEADLINE to "Small daily habits compound into *extraordinary* lifelong results.",
            PostSlots.BODY to "Wrap any word in *stars* to light it up in your brand accent while dimming surrounding lines."
        ),
        looks = listOf(
            TemplateLook("Gold spotlight", "#F59E0B", "#06080F", PostBase.DARK),
            TemplateLook("Mint spotlight", "#10B981", "#050E0B", PostBase.DARK),
            TemplateLook("Paper spotlight", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.08f, 0.73f, 0.22f, 0.006f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.14f, w = 0.45f, h = 0.04f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.21f, w = 0.84f, h = 0.49f,
                        size = 76f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f,
                        dimOthers = 0.42f, shadow = TextShadow(strength = 45, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.08f, y = 0.76f, w = 0.84f, h = 0.12f,
                        size = 25f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.08f, 0.72f, 0.18f, 0.007f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.12f, w = 0.40f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.19f, w = 0.84f, h = 0.50f,
                        size = 72f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.05f,
                        dimOthers = 0.42f, shadow = TextShadow(strength = 45, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.08f, y = 0.75f, w = 0.84f, h = 0.13f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.07f, 0.72f, 0.15f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.13f, w = 0.35f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.20f, w = 0.86f, h = 0.49f,
                        size = 74f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f,
                        dimOthers = 0.42f, shadow = TextShadow(strength = 45, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.07f, y = 0.75f, w = 0.86f, h = 0.12f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
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

    // 5. type-grid — Swiss type grid
    PostTemplateSpec(
        id = "type-grid",
        name = "Swiss type grid",
        family = "Type",
        slots = listOf(PostSlots.NUMBER, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.LIST),
        sample = mapOf(
            PostSlots.NUMBER to "04",
            PostSlots.EYEBROW to "DESIGN SYSTEM · 2026",
            PostSlots.HEADLINE to "Grid precision creates effortless visual harmony.",
            PostSlots.BODY to "Inspired by classic Zurich exhibition posters with visible hairline rules.",
            PostSlots.LIST to "Hierarchy first\nGenerous margin\nSharp contrast"
        ),
        looks = listOf(
            TemplateLook("Zurich paper", "#DC2626", "#F7F4ED", PostBase.LIGHT),
            TemplateLook("Bauhaus night", "#F59E0B", "#090C12", PostBase.DARK),
            TemplateLook("Cobalt studio", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                decor = listOf(
                    DecorBox(DecorShape.LINE, 0.06f, 0.12f, 0.88f, 0.002f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.06f, 0.30f, 0.88f, 0.002f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.42f, 0.12f, 0.002f, 0.18f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.06f, 0.62f, 0.88f, 0.002f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.50f, 0.62f, 0.002f, 0.26f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.06f, 0.88f, 0.88f, 0.002f, PostTone.TEXT, alpha = 120, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.07f, y = 0.13f, w = 0.33f, h = 0.16f,
                        size = 120f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.45f, y = 0.15f, w = 0.47f, h = 0.12f,
                        size = 24f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.33f, w = 0.86f, h = 0.26f,
                        size = 64f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, tracking = -0.02f, leading = 1.02f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.07f, y = 0.65f, w = 0.40f, h = 0.21f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.53f, y = 0.65f, w = 0.40f, h = 0.21f,
                        size = 24f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.NUMBER, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                decor = listOf(
                    DecorBox(DecorShape.LINE, 0.06f, 0.12f, 0.88f, 0.002f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.06f, 0.32f, 0.88f, 0.002f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.36f, 0.12f, 0.002f, 0.20f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.06f, 0.64f, 0.88f, 0.002f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.52f, 0.64f, 0.002f, 0.24f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.06f, 0.88f, 0.88f, 0.002f, PostTone.TEXT, alpha = 120, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.07f, y = 0.13f, w = 0.27f, h = 0.18f,
                        size = 115f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.39f, y = 0.15f, w = 0.53f, h = 0.14f,
                        size = 24f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.35f, w = 0.86f, h = 0.26f,
                        size = 62f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, tracking = -0.02f, leading = 1.02f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.07f, y = 0.67f, w = 0.42f, h = 0.19f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.55f, y = 0.67f, w = 0.38f, h = 0.19f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.NUMBER, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.LIGHT,
                backdrop = Backdrop.PAPER,
                decor = listOf(
                    DecorBox(DecorShape.LINE, 0.05f, 0.14f, 0.90f, 0.002f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.32f, 0.14f, 0.002f, 0.74f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.05f, 0.50f, 0.27f, 0.002f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.32f, 0.56f, 0.63f, 0.002f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.64f, 0.56f, 0.002f, 0.32f, PostTone.TEXT, alpha = 120, panel = true),
                    DecorBox(DecorShape.LINE, 0.05f, 0.88f, 0.90f, 0.002f, PostTone.TEXT, alpha = 120, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.06f, y = 0.16f, w = 0.24f, h = 0.32f,
                        size = 130f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.54f, w = 0.24f, h = 0.30f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.TOP, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.35f, y = 0.17f, w = 0.58f, h = 0.36f,
                        size = 64f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, tracking = -0.02f, leading = 1.02f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.35f, y = 0.60f, w = 0.27f, h = 0.25f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.67f, y = 0.60f, w = 0.26f, h = 0.25f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        list = ListStyle.NUMBER, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f
                    )
                )
            )
        )
    },

    // 6. type-aurora — Aurora type
    PostTemplateSpec(
        id = "type-aurora",
        name = "Aurora type",
        family = "Type",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "GRADIENT EDITION",
            PostSlots.HEADLINE to "Design with luminous color and quiet confidence.",
            PostSlots.BODY to "Multi-point aurora mesh glow with gradient-filled display typography."
        ),
        looks = listOf(
            TemplateLook("Aurora gold", "#F59E0B", "#070913", PostBase.DARK),
            TemplateLook("Northern teal", "#14B8A6", "#040D12", PostBase.DARK),
            TemplateLook("Sunset violet", "#EC4899", "#0D0614", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.MESH,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.12f, 0.88f, 0.76f, PostTone.DEEP, alpha = 155, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.14f, y = 0.17f, w = 0.72f, h = 0.045f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.22f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.25f, w = 0.80f, h = 0.44f,
                        size = 76f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        leading = 1.04f, fx = TextFx.GRADIENT, fxTo = PostTone.LIGHT,
                        shadow = TextShadow(strength = 65, mode = ShadowMode.GLOW)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.12f, y = 0.72f, w = 0.76f, h = 0.12f,
                        size = 25f, font = PostFont.REGULAR, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.MESH,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.11f, 0.88f, 0.78f, PostTone.DEEP, alpha = 155, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.18f, y = 0.16f, w = 0.64f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.22f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.24f, w = 0.80f, h = 0.46f,
                        size = 72f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        leading = 1.04f, fx = TextFx.GRADIENT, fxTo = PostTone.LIGHT,
                        shadow = TextShadow(strength = 65, mode = ShadowMode.GLOW)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.14f, y = 0.73f, w = 0.72f, h = 0.12f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.MESH,
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.12f, 0.88f, 0.76f, PostTone.DEEP, alpha = 155, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.22f, y = 0.17f, w = 0.56f, h = 0.055f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.22f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.25f, w = 0.80f, h = 0.44f,
                        size = 74f, font = PostFont.DISPLAY, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        leading = 1.04f, fx = TextFx.GRADIENT, fxTo = PostTone.LIGHT,
                        shadow = TextShadow(strength = 65, mode = ShadowMode.GLOW)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.18f, y = 0.72f, w = 0.64f, h = 0.12f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f
                    )
                )
            )
        )
    },

    // 7. type-ticker — Diagonal scrolling-style band
    PostTemplateSpec(
        id = "type-ticker",
        name = "Diagonal ticker band",
        family = "Type",
        slots = listOf(PostSlots.WORD, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.WORD to "MOMENTUM",
            PostSlots.EYEBROW to "KINETIC BAND · እንቅስቃሴ",
            PostSlots.HEADLINE to "Keep moving forward even when the pace feels slow.",
            PostSlots.BODY to "Diagonal scrolling-style band with repeating kinetic typography."
        ),
        looks = listOf(
            TemplateLook("Gold ticker", "#F59E0B", "#070A10", PostBase.DARK),
            TemplateLook("Emerald ticker", "#10B981", "#050E0B", PostBase.DARK),
            TemplateLook("Paper ticker", "#DC2626", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.BAR, -0.06f, 0.14f, 1.12f, 0.085f, PostTone.ACCENT, alpha = 225, panel = true, rotate = -6f),
                    DecorBox(DecorShape.CARD, 0.08f, 0.31f, 0.84f, 0.42f, PostTone.DEEP, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.08f, 0.31f, 0.84f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = -0.04f, y = 0.08f, w = 1.08f, h = 0.80f,
                        size = 110f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.TICKER, anchor = VAnchor.CENTER,
                        rotate = -6f
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.13f, y = 0.35f, w = 0.56f, h = 0.040f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.10f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.13f, y = 0.41f, w = 0.74f, h = 0.16f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.13f, y = 0.59f, w = 0.74f, h = 0.10f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.BAR, -0.06f, 0.13f, 1.12f, 0.09f, PostTone.ACCENT, alpha = 225, panel = true, rotate = -6f),
                    DecorBox(DecorShape.CARD, 0.10f, 0.27f, 0.80f, 0.48f, PostTone.DEEP, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.10f, 0.27f, 0.80f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = -0.04f, y = 0.06f, w = 1.08f, h = 0.84f,
                        size = 110f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.TICKER, anchor = VAnchor.CENTER,
                        rotate = -6f
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.15f, y = 0.31f, w = 0.52f, h = 0.044f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.10f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.15f, y = 0.375f, w = 0.70f, h = 0.18f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.15f, y = 0.575f, w = 0.70f, h = 0.12f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.BAR, -0.06f, 0.12f, 1.12f, 0.10f, PostTone.ACCENT, alpha = 225, panel = true, rotate = -6f),
                    DecorBox(DecorShape.CARD, 0.14f, 0.24f, 0.72f, 0.54f, PostTone.DEEP, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.14f, 0.24f, 0.72f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = -0.04f, y = 0.06f, w = 1.08f, h = 0.84f,
                        size = 115f, font = PostFont.CONDENSED, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.TICKER, anchor = VAnchor.CENTER,
                        rotate = -6f
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.19f, y = 0.28f, w = 0.44f, h = 0.052f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.10f,
                        chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.19f, y = 0.355f, w = 0.62f, h = 0.22f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.19f, y = 0.60f, w = 0.62f, h = 0.13f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
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

    // 9. type-ribbon — Diagonal scrolling band
    PostTemplateSpec(
        id = "type-ribbon",
        name = "Diagonal ribbon band",
        family = "Type",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "BREAKING UPDATE · አዲስ ዜና",
            PostSlots.HEADLINE to "Diagonal bands command immediate attention.",
            PostSlots.BODY to "Built for high-energy announcements in both Amharic and English.",
            PostSlots.BUTTON to "READ FULL UPDATE"
        ),
        looks = listOf(
            TemplateLook("Hazard gold", "#F59E0B", "#07090F", PostBase.DARK),
            TemplateLook("Mint express", "#10B981", "#050E0B", PostBase.DARK),
            TemplateLook("Crimson alert", "#EF4444", "#0C0608", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.BAR, -0.08f, 0.15f, 1.16f, 0.08f, PostTone.ACCENT, panel = true, rotate = -7f),
                    DecorBox(DecorShape.BAR, -0.08f, 0.76f, 1.16f, 0.07f, PostTone.DEEP, alpha = 235, panel = true, rotate = -7f)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.165f, w = 0.84f, h = 0.05f,
                        size = 30f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.14f, rotate = -7f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.30f, w = 0.84f, h = 0.26f,
                        size = 64f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.59f, w = 0.80f, h = 0.11f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.22f, y = 0.77f, w = 0.56f, h = 0.048f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, rotate = -7f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.BAR, -0.08f, 0.14f, 1.16f, 0.085f, PostTone.ACCENT, panel = true, rotate = -6f),
                    DecorBox(DecorShape.BAR, -0.08f, 0.75f, 1.16f, 0.075f, PostTone.DEEP, alpha = 235, panel = true, rotate = -6f)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.155f, w = 0.84f, h = 0.052f,
                        size = 30f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.14f, rotate = -6f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.28f, w = 0.84f, h = 0.28f,
                        size = 60f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.58f, w = 0.80f, h = 0.12f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.22f, y = 0.76f, w = 0.56f, h = 0.05f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, rotate = -6f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                decor = listOf(
                    DecorBox(DecorShape.BAR, -0.08f, 0.14f, 1.16f, 0.09f, PostTone.ACCENT, panel = true, rotate = -5f),
                    DecorBox(DecorShape.BAR, -0.08f, 0.74f, 1.16f, 0.08f, PostTone.DEEP, alpha = 235, panel = true, rotate = -5f)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.155f, w = 0.80f, h = 0.055f,
                        size = 30f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, tracking = 0.14f, rotate = -5f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.28f, w = 0.80f, h = 0.28f,
                        size = 60f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.14f, y = 0.58f, w = 0.72f, h = 0.12f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.24f, y = 0.75f, w = 0.52f, h = 0.055f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL, rotate = -5f
                    )
                )
            )
        )
    }
)
