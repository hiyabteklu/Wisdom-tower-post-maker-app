package com.wisdomtower.post

/**
 * Family 3 — Photo & Editorial (8 templates: photo-floor, photo-behind, photo-polaroid, photo-triptych,
 * photo-cover, photo-tag, photo-testimonial, photo-idcard).
 * Built for 4:3, 9:16, and 16:9 using PostSize.pick3.
 */
internal val PhotoTemplates: List<PostTemplateSpec> = listOf(
    // 1. photo-floor — 3D Magnifying Lens Sheet (2 photo slots)
    PostTemplateSpec(
        id = "photo-floor",
        name = "3D Magnifying Lens Sheet",
        family = "Photo",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "DEEP CONCEPT FOCUS",
            PostSlots.HEADLINE to "Zoom into every tricky exam diagram & step.",
            PostSlots.BODY to "Insert your full study sheet and a zoomed key detail inside the 3D optical lens."
        ),
        looks = listOf(
            TemplateLook("Optical cyan", "#38BDF8", "#050B16", PostBase.DARK),
            TemplateLook("Cinema gold", "#F59E0B", "#05070C", PostBase.DARK),
            TemplateLook("Emerald dusk", "#10B981", "#040B09", PostBase.DARK),
            TemplateLook("Academic ivory", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.04f, 0.08f, 0.92f, 0.54f, PostTone.ACCENT, variant = 22, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.64f, 0.88f, 0.25f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.64f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.665f, w = 0.44f, h = 0.040f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.715f, w = 0.80f, h = 0.095f,
                        size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
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
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.10f, 0.50f, 0.78f, PostTone.ACCENT, variant = 22, panel = true),
                    DecorBox(DecorShape.BAR, 0.56f, 0.64f, 0.20f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.56f, y = 0.18f, w = 0.36f, h = 0.048f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.56f, y = 0.26f, w = 0.38f, h = 0.34f,
                        size = 50f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.56f, y = 0.67f, w = 0.38f, h = 0.15f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.08f, 0.48f, 0.82f, PostTone.ACCENT, variant = 22, panel = true),
                    DecorBox(DecorShape.BAR, 0.54f, 0.64f, 0.20f, 0.009f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.54f, y = 0.16f, w = 0.34f, h = 0.052f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.54f, y = 0.24f, w = 0.40f, h = 0.36f,
                        size = 52f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.54f, y = 0.67f, w = 0.40f, h = 0.16f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            )
        )
    },

    // 2. photo-behind — Words behind subject
    PostTemplateSpec(
        id = "photo-behind",
        name = "Words behind subject",
        family = "Photo",
        slots = listOf(PostSlots.WORD, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.WORD to "VISION",
            PostSlots.EYEBROW to "MAGAZINE COVER",
            PostSlots.HEADLINE to "Step into the frame and let the story speak.",
            PostSlots.BODY to "Add a cut-out PNG in the Photo tab so your hero word sits right behind the subject."
        ),
        looks = listOf(
            TemplateLook("Cover gold", "#F59E0B", "#070910", PostBase.DARK),
            TemplateLook("Vogue crimson", "#E11D48", "#0D0609", PostBase.DARK),
            TemplateLook("Studio ice", "#38BDF8", "#070D17", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeBottom = true),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.15f, y = 0.10f, w = 0.70f, h = 0.04f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.04f, y = 0.14f, w = 0.92f, h = 0.34f,
                        size = 320f, font = PostFont.CONDENSED, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        tracking = -0.02f, shadow = TextShadow(strength = 80, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.68f, w = 0.84f, h = 0.12f,
                        size = 44f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.BOTTOM, leading = 1.05f,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.81f, w = 0.80f, h = 0.08f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP,
                        shadow = TextShadow(strength = 65, mode = ShadowMode.SOFT)
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeBottom = true),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.20f, y = 0.10f, w = 0.60f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.04f, y = 0.15f, w = 0.92f, h = 0.42f,
                        size = 330f, font = PostFont.CONDENSED, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        tracking = -0.02f, shadow = TextShadow(strength = 80, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.67f, w = 0.84f, h = 0.12f,
                        size = 42f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.BOTTOM, leading = 1.05f,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.80f, w = 0.80f, h = 0.08f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP,
                        shadow = TextShadow(strength = 65, mode = ShadowMode.SOFT)
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeBottom = true),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.25f, y = 0.10f, w = 0.50f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.04f, y = 0.15f, w = 0.92f, h = 0.45f,
                        size = 340f, font = PostFont.CONDENSED, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER,
                        tracking = -0.02f, shadow = TextShadow(strength = 80, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.12f, y = 0.68f, w = 0.76f, h = 0.11f,
                        size = 40f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.BOTTOM,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.16f, y = 0.80f, w = 0.68f, h = 0.08f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP,
                        shadow = TextShadow(strength = 65, mode = ShadowMode.SOFT)
                    )
                )
            )
        )
    },

    // 3. photo-polaroid — Tilted polaroid
    PostTemplateSpec(
        id = "photo-polaroid",
        name = "Tilted polaroid",
        family = "Photo",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "FIELD NOTES",
            PostSlots.HEADLINE to "Captured moments that stay with us long after.",
            PostSlots.BODY to "Warm instant-film frame tilted at -4° with a top tape strip."
        ),
        looks = listOf(
            TemplateLook("Warm studio", "#F59E0B", "#0C1018", PostBase.DARK),
            TemplateLook("Scrapbook paper", "#D97706", "#F6F1E6", PostBase.LIGHT),
            TemplateLook("Sage journal", "#10B981", "#071410", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.PAPER,
                photo = PostPhotoArea(0.14f, 0.15f, 0.72f, 0.42f, rounded = true, rotate = -4f),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.10f, 0.12f, 0.80f, 0.56f, PostTone.PAPER, panel = true, rotate = -4f),
                    DecorBox(DecorShape.BAR, 0.38f, 0.095f, 0.24f, 0.035f, PostTone.ACCENT, alpha = 210, panel = true, rotate = 2f)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.16f, y = 0.59f, w = 0.68f, h = 0.055f,
                        size = 26f, font = PostFont.SERIF, tone = PostTone.DARK,
                        centered = true, fit = FitMode.FILL_WIDTH, rotate = -4f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.71f, w = 0.84f, h = 0.11f,
                        size = 42f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.83f, w = 0.80f, h = 0.07f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.PAPER,
                photo = PostPhotoArea(0.09f, 0.16f, 0.40f, 0.54f, rounded = true, rotate = -4f),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.12f, 0.46f, 0.70f, PostTone.PAPER, panel = true, rotate = -4f),
                    DecorBox(DecorShape.BAR, 0.20f, 0.09f, 0.18f, 0.04f, PostTone.ACCENT, alpha = 210, panel = true, rotate = 2f)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.72f, w = 0.38f, h = 0.065f,
                        size = 24f, font = PostFont.SERIF, tone = PostTone.DARK,
                        centered = true, fit = FitMode.FILL_WIDTH, rotate = -4f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.56f, y = 0.24f, w = 0.38f, h = 0.32f,
                        size = 46f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.BOTTOM, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.56f, y = 0.59f, w = 0.38f, h = 0.20f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.PAPER,
                photo = PostPhotoArea(0.08f, 0.16f, 0.36f, 0.56f, rounded = true, rotate = -4f),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.12f, 0.42f, 0.72f, PostTone.PAPER, panel = true, rotate = -4f),
                    DecorBox(DecorShape.BAR, 0.18f, 0.09f, 0.16f, 0.04f, PostTone.ACCENT, alpha = 210, panel = true, rotate = 2f)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.09f, y = 0.74f, w = 0.34f, h = 0.065f,
                        size = 22f, font = PostFont.SERIF, tone = PostTone.DARK,
                        centered = true, fit = FitMode.FILL_WIDTH, rotate = -4f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.52f, y = 0.24f, w = 0.42f, h = 0.32f,
                        size = 48f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.BOTTOM, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.52f, y = 0.59f, w = 0.42f, h = 0.20f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.22f
                    )
                )
            )
        )
    },

    // 4. photo-triptych — Three-strip photo
    PostTemplateSpec(
        id = "photo-triptych",
        name = "Three-strip photo",
        family = "Photo",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "VISUAL ESSAY",
            PostSlots.HEADLINE to "One moment seen through three vertical windows.",
            PostSlots.BODY to "Your photo pans smoothly across all three staggered vertical strips."
        ),
        looks = listOf(
            TemplateLook("Gallery dark", "#F59E0B", "#070A12", PostBase.DARK),
            TemplateLook("Museum paper", "#B45309", "#FAF6EE", PostBase.LIGHT),
            TemplateLook("Teal cinema", "#14B8A6", "#051012", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(
                    x = 0.07f, y = 0.11f, w = 0.86f, h = 0.47f, rounded = true,
                    cells = listOf(
                        PhotoCell(0.07f, 0.13f, 0.27f, 0.43f, panOffsetX = -0.35f),
                        PhotoCell(0.365f, 0.10f, 0.27f, 0.45f, panOffsetX = 0f),
                        PhotoCell(0.66f, 0.14f, 0.27f, 0.43f, panOffsetX = 0.35f)
                    )
                ),
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.07f, 0.60f, 0.22f, 0.006f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.62f, w = 0.56f, h = 0.038f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL, tracking = 0.12f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.67f, w = 0.86f, h = 0.115f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.07f, y = 0.798f, w = 0.86f, h = 0.075f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(
                    x = 0.06f, y = 0.10f, w = 0.88f, h = 0.47f, rounded = true,
                    cells = listOf(
                        PhotoCell(0.06f, 0.12f, 0.28f, 0.43f, panOffsetX = -0.35f),
                        PhotoCell(0.36f, 0.095f, 0.28f, 0.45f, panOffsetX = 0f),
                        PhotoCell(0.66f, 0.13f, 0.28f, 0.43f, panOffsetX = 0.35f)
                    )
                ),
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.06f, 0.595f, 0.18f, 0.007f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.615f, w = 0.54f, h = 0.040f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL, tracking = 0.12f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.665f, w = 0.88f, h = 0.115f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.06f, y = 0.792f, w = 0.88f, h = 0.075f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(
                    x = 0.05f, y = 0.12f, w = 0.52f, h = 0.72f, rounded = true,
                    cells = listOf(
                        PhotoCell(0.05f, 0.16f, 0.16f, 0.66f, panOffsetX = -0.35f),
                        PhotoCell(0.225f, 0.12f, 0.16f, 0.70f, panOffsetX = 0f),
                        PhotoCell(0.40f, 0.18f, 0.16f, 0.66f, panOffsetX = 0.35f)
                    )
                ),
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.60f, 0.22f, 0.14f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.60f, y = 0.25f, w = 0.34f, h = 0.048f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL, tracking = 0.12f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.60f, y = 0.32f, w = 0.34f, h = 0.28f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.60f, y = 0.62f, w = 0.34f, h = 0.18f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            )
        )
    },

    // 5. photo-cover — Magazine cover
    PostTemplateSpec(
        id = "photo-cover",
        name = "Magazine cover",
        family = "Photo",
        slots = listOf(PostSlots.WORD, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.WORD to "WISDOM",
            PostSlots.EYEBROW to "ISSUE 24 · SPECIAL EDITION",
            PostSlots.HEADLINE to "The Architecture of Deep Learning",
            PostSlots.BODY to "Inside: How small teams build enduring educational institutions."
        ),
        looks = listOf(
            TemplateLook("Gold masthead", "#F59E0B", "#07090F", PostBase.DARK),
            TemplateLook("Time crimson", "#EF4444", "#090608", PostBase.DARK),
            TemplateLook("Ivory masthead", "#FAF5EC", "#0B0F17", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeBottom = true),
                decor = listOf(
                    DecorBox(DecorShape.FRAME, 0.035f, 0.035f, 0.93f, 0.93f, PostTone.ACCENT, alpha = 210, panel = true),
                    DecorBox(DecorShape.LINE, 0.07f, 0.25f, 0.86f, 0.002f, PostTone.LIGHT, alpha = 150, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.06f, y = 0.07f, w = 0.88f, h = 0.17f,
                        size = 180f, font = PostFont.SERIF, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER,
                        tracking = 0.04f, shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.27f, w = 0.84f, h = 0.035f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.64f, w = 0.84f, h = 0.14f,
                        size = 50f, font = PostFont.SERIF, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.BOTTOM, leading = 1.03f,
                        shadow = TextShadow(strength = 80, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.08f, y = 0.79f, w = 0.84f, h = 0.09f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f,
                        shadow = TextShadow(strength = 70, mode = ShadowMode.SOFT)
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeBottom = true),
                decor = listOf(
                    DecorBox(DecorShape.FRAME, 0.035f, 0.04f, 0.93f, 0.92f, PostTone.ACCENT, alpha = 210, panel = true),
                    DecorBox(DecorShape.LINE, 0.07f, 0.27f, 0.86f, 0.002f, PostTone.LIGHT, alpha = 150, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.06f, y = 0.07f, w = 0.88f, h = 0.19f,
                        size = 180f, font = PostFont.SERIF, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER,
                        tracking = 0.04f, shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.29f, w = 0.84f, h = 0.04f,
                        size = 18f, font = PostFont.BOLD, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.18f,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.62f, w = 0.84f, h = 0.15f,
                        size = 46f, font = PostFont.SERIF, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.BOTTOM, leading = 1.03f,
                        shadow = TextShadow(strength = 80, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.08f, y = 0.78f, w = 0.84f, h = 0.09f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f,
                        shadow = TextShadow(strength = 70, mode = ShadowMode.SOFT)
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeBottom = true),
                decor = listOf(
                    DecorBox(DecorShape.FRAME, 0.03f, 0.04f, 0.94f, 0.92f, PostTone.ACCENT, alpha = 210, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.06f, y = 0.08f, w = 0.88f, h = 0.20f,
                        size = 180f, font = PostFont.SERIF, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER,
                        tracking = 0.04f, shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.52f, w = 0.40f, h = 0.05f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.LIGHT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.16f,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.59f, w = 0.72f, h = 0.17f,
                        size = 46f, font = PostFont.SERIF, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.BOTTOM, leading = 1.03f,
                        shadow = TextShadow(strength = 80, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.06f, y = 0.77f, w = 0.72f, h = 0.09f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP,
                        shadow = TextShadow(strength = 70, mode = ShadowMode.SOFT)
                    )
                )
            )
        )
    },

    // 6. photo-cornerpeel — Corner Peel (replaces photo-tag)
    PostTemplateSpec(
        id = "photo-cornerpeel",
        name = "Corner Peel",
        family = "Photo",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "JUST DROPPED",
            PostSlots.HEADLINE to "PEEL BACK THE FUTURE.",
            PostSlots.BODY to "Full-bleed visual with 3D curled reveal.",
            PostSlots.BUTTON to "EXPLORE NOW"
        ),
        looks = listOf(
            TemplateLook("Gold peel", "#F59E0B", "#06080E", PostBase.DARK),
            TemplateLook("Studio paper", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Emerald peel", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Crimson peel", "#F43F5E", "#12060A", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                logoAnchor = LogoAnchor.TOP_LEFT,
                photo = PostPhotoArea(0.05f, 0.05f, 0.90f, 0.86f, corner = 0.04f, scrim = ScrimMode.BOTTOM, rimTone = PostTone.ACCENT, shadow = true),
                decor = listOf(
                    DecorBox(DecorShape.PEEL_CORNER, 0.62f, 0.05f, 0.33f, 0.24f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.BAR, 0.09f, 0.70f, 0.22f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.09f, y = 0.63f, w = 0.42f, h = 0.042f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.09f, y = 0.72f, w = 0.82f, h = 0.10f,
                        size = 64f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 0.96f,
                        shadow = TextShadow(strength = 85, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.09f, y = 0.83f, w = 0.56f, h = 0.05f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.67f, y = 0.825f, w = 0.24f, h = 0.048f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                logoAnchor = LogoAnchor.TOP_LEFT,
                photo = PostPhotoArea(0.05f, 0.05f, 0.90f, 0.85f, corner = 0.04f, scrim = ScrimMode.BOTTOM, rimTone = PostTone.ACCENT, shadow = true),
                decor = listOf(
                    DecorBox(DecorShape.PEEL_CORNER, 0.64f, 0.05f, 0.31f, 0.26f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.BAR, 0.09f, 0.68f, 0.20f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.09f, y = 0.60f, w = 0.36f, h = 0.046f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.09f, y = 0.70f, w = 0.82f, h = 0.11f,
                        size = 60f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 0.96f,
                        shadow = TextShadow(strength = 85, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.09f, y = 0.82f, w = 0.56f, h = 0.05f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.68f, y = 0.815f, w = 0.23f, h = 0.052f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                logoAnchor = LogoAnchor.TOP_LEFT,
                photo = PostPhotoArea(0.04f, 0.06f, 0.92f, 0.84f, corner = 0.04f, scrim = ScrimMode.BOTTOM, rimTone = PostTone.ACCENT, shadow = true),
                decor = listOf(
                    DecorBox(DecorShape.PEEL_CORNER, 0.72f, 0.06f, 0.24f, 0.30f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.BAR, 0.08f, 0.64f, 0.18f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.55f, w = 0.28f, h = 0.052f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.67f, w = 0.62f, h = 0.13f,
                        size = 62f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 0.96f,
                        shadow = TextShadow(strength = 85, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.08f, y = 0.81f, w = 0.54f, h = 0.055f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.72f, y = 0.78f, w = 0.20f, h = 0.065f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            )
        )
    },

    // 7. photo-voiceframe — 3D Video Lecture Player (2 photo slots)
    PostTemplateSpec(
        id = "photo-voiceframe",
        name = "3D Video Lecture Player",
        family = "Photo",
        slots = listOf(PostSlots.HEADLINE, PostSlots.EYEBROW, PostSlots.BODY),
        sample = mapOf(
            PostSlots.HEADLINE to "HD VIDEO LECTURES.",
            PostSlots.EYEBROW to "CHAPTER-BY-CHAPTER WALKTHROUGHS",
            PostSlots.BODY to "Stream bite-sized exam tutorials with synced summary notes & offline playback."
        ),
        looks = listOf(
            TemplateLook("Cobalt lecture", "#38BDF8", "#050B16", PostBase.DARK),
            TemplateLook("Gold stage", "#F59E0B", "#070A12", PostBase.DARK),
            TemplateLook("Emerald trust", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Editorial cream", "#B45309", "#FAF5EC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.04f, 0.09f, 0.92f, 0.53f, PostTone.ACCENT, variant = 20, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.64f, 0.88f, 0.25f, PostTone.DEEP, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.64f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.665f, w = 0.80f, h = 0.040f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.715f, w = 0.80f, h = 0.095f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.96f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.815f, w = 0.80f, h = 0.055f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.10f, 0.50f, 0.78f, PostTone.ACCENT, variant = 20, panel = true),
                    DecorBox(DecorShape.CARD, 0.55f, 0.16f, 0.40f, 0.68f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.55f, 0.16f, 0.40f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.58f, y = 0.21f, w = 0.34f, h = 0.050f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.58f, y = 0.29f, w = 0.34f, h = 0.30f,
                        size = 64f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.96f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.58f, y = 0.63f, w = 0.34f, h = 0.16f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_RIGHT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.08f, 0.48f, 0.82f, PostTone.ACCENT, variant = 20, panel = true),
                    DecorBox(DecorShape.CARD, 0.53f, 0.14f, 0.42f, 0.72f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.53f, 0.14f, 0.42f, 0.009f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.56f, y = 0.20f, w = 0.36f, h = 0.055f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.56f, y = 0.28f, w = 0.36f, h = 0.32f,
                        size = 68f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.96f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.56f, y = 0.64f, w = 0.36f, h = 0.16f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            )
        )
    },

    // 8. photo-gueststage — 3D Tutor Spotlight Card (2 photo slots)
    PostTemplateSpec(
        id = "photo-gueststage",
        name = "3D Tutor Spotlight Card",
        family = "Photo",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.NUMBER),
        sample = mapOf(
            PostSlots.EYEBROW to "MASTERCLASS MENTOR",
            PostSlots.HEADLINE to "PROF. YONATAN",
            PostSlots.BODY to "Live Exam Prep & Interactive Q&A · Saturday 7:00 PM",
            PostSlots.NUMBER to "LIVE"
        ),
        looks = listOf(
            TemplateLook("Keynote gold", "#F59E0B", "#060810", PostBase.DARK),
            TemplateLook("Cobalt arena", "#38BDF8", "#050A14", PostBase.DARK),
            TemplateLook("Emerald summit", "#10B981", "#040E0A", PostBase.DARK),
            TemplateLook("Editorial light", "#D97706", "#FAF6EE", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.04f, 0.13f, 0.92f, 0.55f, PostTone.ACCENT, variant = 21, panel = true),
                    DecorBox(DecorShape.CARD, 0.08f, 0.72f, 0.84f, 0.17f, PostTone.DEEP, alpha = 242, panel = true),
                    DecorBox(DecorShape.BAR, 0.08f, 0.72f, 0.84f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.20f, y = 0.08f, w = 0.42f, h = 0.04f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.64f, y = 0.08f, w = 0.16f, h = 0.04f,
                        size = 20f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.12f, y = 0.74f, w = 0.76f, h = 0.08f,
                        size = 62f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.14f, y = 0.83f, w = 0.72f, h = 0.048f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.10f, 0.50f, 0.78f, PostTone.ACCENT, variant = 21, panel = true),
                    DecorBox(DecorShape.BAR, 0.58f, 0.62f, 0.24f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.58f, y = 0.24f, w = 0.24f, h = 0.048f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.83f, y = 0.24f, w = 0.11f, h = 0.048f,
                        size = 19f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.58f, y = 0.32f, w = 0.36f, h = 0.26f,
                        size = 68f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.96f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.58f, y = 0.66f, w = 0.36f, h = 0.12f,
                        size = 22f, font = PostFont.BOLD, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                logoAnchor = LogoAnchor.TOP_LEFT,
                decor = listOf(
                    DecorBox(DecorShape.DEVICE_3D, 0.03f, 0.08f, 0.48f, 0.82f, PostTone.ACCENT, variant = 21, panel = true),
                    DecorBox(DecorShape.BAR, 0.55f, 0.62f, 0.24f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.55f, y = 0.22f, w = 0.24f, h = 0.052f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.81f, y = 0.22f, w = 0.12f, h = 0.052f,
                        size = 19f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.55f, y = 0.31f, w = 0.39f, h = 0.27f,
                        size = 72f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        caps = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 0.96f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.55f, y = 0.66f, w = 0.39f, h = 0.12f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            )
        )
    },

    // 9. photo-duo — Two side-by-side photos with a headline
    PostTemplateSpec(
        id = "photo-duo",
        name = "Side-by-side duo",
        family = "Photo",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "BEFORE & AFTER · ንፅፅር",
            PostSlots.HEADLINE to "Two side-by-side frames that tell a complete story.",
            PostSlots.BODY to "Pick a template photo and a background photo to compare two moments side by side."
        ),
        looks = listOf(
            TemplateLook("Duo gold", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Gallery cream", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Emerald pair", "#10B981", "#05100D", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(
                    x = 0.06f, y = 0.12f, w = 0.88f, h = 0.46f, rounded = true, shadow = true, innerShadow = true, lightSpill = true,
                    cells = listOf(
                        PhotoCell(0.06f, 0.12f, 0.425f, 0.46f, radius = 0.04f, panOffsetX = 0f, photoIndex = 0),
                        PhotoCell(0.515f, 0.12f, 0.425f, 0.46f, radius = 0.04f, panOffsetX = 0f, photoIndex = 1)
                    )
                ),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.61f, 0.88f, 0.26f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.61f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.635f, w = 0.56f, h = 0.038f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.10f, chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.685f, w = 0.80f, h = 0.105f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.798f, w = 0.80f, h = 0.065f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(
                    x = 0.06f, y = 0.11f, w = 0.88f, h = 0.47f, rounded = true, shadow = true, innerShadow = true, lightSpill = true,
                    cells = listOf(
                        PhotoCell(0.06f, 0.11f, 0.425f, 0.47f, radius = 0.04f, panOffsetX = 0f, photoIndex = 0),
                        PhotoCell(0.515f, 0.11f, 0.425f, 0.47f, radius = 0.04f, panOffsetX = 0f, photoIndex = 1)
                    )
                ),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.605f, 0.88f, 0.265f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.605f, 0.88f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.63f, w = 0.54f, h = 0.040f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.10f, chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.682f, w = 0.80f, h = 0.105f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.795f, w = 0.80f, h = 0.065f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(
                    x = 0.05f, y = 0.12f, w = 0.52f, h = 0.72f, rounded = true, shadow = true, innerShadow = true, lightSpill = true,
                    cells = listOf(
                        PhotoCell(0.05f, 0.12f, 0.25f, 0.72f, radius = 0.04f, panOffsetX = 0f, photoIndex = 0),
                        PhotoCell(0.32f, 0.12f, 0.25f, 0.72f, radius = 0.04f, panOffsetX = 0f, photoIndex = 1)
                    )
                ),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.60f, 0.16f, 0.35f, 0.66f, PostTone.DEEP, alpha = 235, panel = true),
                    DecorBox(DecorShape.BAR, 0.60f, 0.16f, 0.35f, 0.010f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.63f, y = 0.21f, w = 0.29f, h = 0.05f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.10f, chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.63f, y = 0.29f, w = 0.29f, h = 0.28f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.63f, y = 0.60f, w = 0.29f, h = 0.18f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    )
                )
            )
        )
    },

    // 10. photo-glass — Full photo with a frosted card at the bottom
    PostTemplateSpec(
        id = "photo-glass",
        name = "Frosted glass card",
        family = "Photo",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "SPOTLIGHT · ትኩረት",
            PostSlots.HEADLINE to "Clarity floats over every full-bleed photograph.",
            PostSlots.BODY to "A luminous frosted glass card keeps both Amharic and English effortless to read.",
            PostSlots.BUTTON to "EXPLORE NOW"
        ),
        looks = listOf(
            TemplateLook("Frosted cyan", "#38BDF8", "#07101C", PostBase.DARK),
            TemplateLook("Frosted gold", "#F59E0B", "#090B12", PostBase.DARK),
            TemplateLook("Frosted mint", "#10B981", "#05110E", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeBottom = true),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.56f, 0.88f, 0.31f, PostTone.DEEP, alpha = 195, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.585f, w = 0.52f, h = 0.038f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL, tracking = 0.12f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.632f, w = 0.80f, h = 0.108f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.748f, w = 0.80f, h = 0.062f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.10f, y = 0.818f, w = 0.44f, h = 0.042f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT, maxLines = 1
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeBottom = true),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.55f, 0.88f, 0.32f, PostTone.DEEP, alpha = 195, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.575f, w = 0.50f, h = 0.040f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL, tracking = 0.12f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.625f, w = 0.80f, h = 0.11f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.742f, w = 0.80f, h = 0.062f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.10f, y = 0.812f, w = 0.42f, h = 0.044f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT, maxLines = 1
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeBottom = true),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.46f, 0.88f, 0.40f, PostTone.DEEP, alpha = 195, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.09f, y = 0.49f, w = 0.44f, h = 0.048f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL, tracking = 0.12f, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.09f, y = 0.55f, w = 0.62f, h = 0.16f,
                        size = 56f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.09f, y = 0.72f, w = 0.62f, h = 0.10f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.73f, y = 0.64f, w = 0.18f, h = 0.08f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT, maxLines = 1
                    )
                )
            )
        )
    },

    // 11. photo-magazine — Magazine-cover masthead
    PostTemplateSpec(
        id = "photo-magazine",
        name = "Magazine masthead",
        family = "Photo",
        slots = listOf(PostSlots.WORD, PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.WORD to "CHRONICLE",
            PostSlots.EYEBROW to "VOL. 12 · ጥበብ መጽሔት",
            PostSlots.HEADLINE to "Inside Ethiopia's Next Generation of Thinkers",
            PostSlots.BODY to "Exclusive interviews, study frameworks, and cultural essays."
        ),
        looks = listOf(
            TemplateLook("Editorial gold", "#F59E0B", "#06080E", PostBase.DARK),
            TemplateLook("Press red", "#EF4444", "#0A0608", PostBase.DARK),
            TemplateLook(" Emerald issue", "#10B981", "#050E0B", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeTop = true, fadeBottom = true),
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.06f, 0.065f, 0.88f, 0.006f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.255f, 0.88f, 0.004f, PostTone.LIGHT, alpha = 160, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.62f, 0.88f, 0.25f, PostTone.DEEP, alpha = 215, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.06f, y = 0.08f, w = 0.88f, h = 0.16f,
                        size = 180f, font = PostFont.CONDENSED, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER,
                        tracking = 0.03f, shadow = TextShadow(strength = 80, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.64f, w = 0.56f, h = 0.038f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.10f, chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.69f, w = 0.80f, h = 0.10f,
                        size = 56f, font = PostFont.SERIF, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.798f, w = 0.80f, h = 0.062f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeTop = true, fadeBottom = true),
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.06f, 0.065f, 0.88f, 0.006f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.265f, 0.88f, 0.004f, PostTone.LIGHT, alpha = 160, panel = true),
                    DecorBox(DecorShape.CARD, 0.06f, 0.61f, 0.88f, 0.26f, PostTone.DEEP, alpha = 215, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.06f, y = 0.08f, w = 0.88f, h = 0.17f,
                        size = 180f, font = PostFont.CONDENSED, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER,
                        tracking = 0.03f, shadow = TextShadow(strength = 80, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.63f, w = 0.54f, h = 0.040f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.10f, chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.682f, w = 0.80f, h = 0.105f,
                        size = 56f, font = PostFont.SERIF, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.795f, w = 0.80f, h = 0.065f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeTop = true, fadeBottom = true),
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.05f, 0.07f, 0.90f, 0.008f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.CARD, 0.05f, 0.54f, 0.90f, 0.33f, PostTone.DEEP, alpha = 215, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.WORD,
                        x = 0.05f, y = 0.09f, w = 0.90f, h = 0.22f,
                        size = 180f, font = PostFont.CONDENSED, tone = PostTone.LIGHT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, anchor = VAnchor.CENTER,
                        tracking = 0.03f, shadow = TextShadow(strength = 80, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.08f, y = 0.57f, w = 0.44f, h = 0.048f,
                        size = 28f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL, tracking = 0.10f, chip = PostTone.ACCENT, maxLines = 1
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.635f, w = 0.84f, h = 0.12f,
                        size = 56f, font = PostFont.SERIF, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.08f, y = 0.765f, w = 0.84f, h = 0.08f,
                        size = 34f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.15f
                    )
                )
            )
        )
    }
)
