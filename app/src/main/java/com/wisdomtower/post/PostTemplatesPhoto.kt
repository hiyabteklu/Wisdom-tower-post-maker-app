package com.wisdomtower.post

/**
 * Family 3 — Photo & Editorial (8 templates: photo-floor, photo-behind, photo-polaroid, photo-triptych,
 * photo-cover, photo-tag, photo-testimonial, photo-idcard).
 * Built for 4:3, 9:16, and 16:9 using PostSize.pick3.
 */
internal val PhotoTemplates: List<PostTemplateSpec> = listOf(
    // 1. photo-floor — Full photo + floor
    PostTemplateSpec(
        id = "photo-floor",
        name = "Full photo + floor",
        family = "Photo",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY),
        sample = mapOf(
            PostSlots.EYEBROW to "FEATURE STORY",
            PostSlots.HEADLINE to "Where stillness meets purposeful action.",
            PostSlots.BODY to "Full-bleed photography with a deep bottom floor fade and soft headline shadow."
        ),
        looks = listOf(
            TemplateLook("Cinema gold", "#F59E0B", "#05070C", PostBase.DARK),
            TemplateLook("Emerald dusk", "#10B981", "#040B09", PostBase.DARK),
            TemplateLook("Sky editorial", "#38BDF8", "#050A14", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeBottom = true),
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.07f, 0.62f, 0.18f, 0.006f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.56f, w = 0.44f, h = 0.042f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.64f, w = 0.86f, h = 0.15f,
                        size = 54f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.BOTTOM, leading = 1.04f,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.07f, y = 0.80f, w = 0.86f, h = 0.09f,
                        size = 26f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeBottom = true),
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.07f, 0.60f, 0.16f, 0.007f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.53f, w = 0.36f, h = 0.048f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.62f, w = 0.86f, h = 0.16f,
                        size = 50f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.BOTTOM, leading = 1.04f,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.07f, y = 0.79f, w = 0.86f, h = 0.09f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0f, 0f, 1f, 1f, fadeBottom = true),
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.06f, 0.58f, 0.14f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.50f, w = 0.30f, h = 0.052f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.60f, w = 0.78f, h = 0.17f,
                        size = 52f, font = PostFont.DISPLAY, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.BOTTOM, leading = 1.04f,
                        shadow = TextShadow(strength = 75, mode = ShadowMode.SOFT)
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.06f, y = 0.78f, w = 0.78f, h = 0.09f,
                        size = 24f, font = PostFont.REGULAR, tone = PostTone.LIGHT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f,
                        shadow = TextShadow(strength = 60, mode = ShadowMode.SOFT)
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
                    x = 0.07f, y = 0.12f, w = 0.86f, h = 0.50f, rounded = true,
                    cells = listOf(
                        PhotoCell(0.07f, 0.14f, 0.27f, 0.46f, panOffsetX = -0.35f),
                        PhotoCell(0.365f, 0.11f, 0.27f, 0.48f, panOffsetX = 0f),
                        PhotoCell(0.66f, 0.15f, 0.27f, 0.46f, panOffsetX = 0.35f)
                    )
                ),
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.07f, 0.65f, 0.22f, 0.006f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.67f, w = 0.48f, h = 0.04f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.72f, w = 0.86f, h = 0.10f,
                        size = 42f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.07f, y = 0.83f, w = 0.86f, h = 0.07f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(
                    x = 0.06f, y = 0.11f, w = 0.88f, h = 0.52f, rounded = true,
                    cells = listOf(
                        PhotoCell(0.06f, 0.13f, 0.28f, 0.48f, panOffsetX = -0.35f),
                        PhotoCell(0.36f, 0.10f, 0.28f, 0.50f, panOffsetX = 0f),
                        PhotoCell(0.66f, 0.14f, 0.28f, 0.48f, panOffsetX = 0.35f)
                    )
                ),
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.06f, 0.66f, 0.18f, 0.007f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.06f, y = 0.68f, w = 0.42f, h = 0.042f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.06f, y = 0.73f, w = 0.88f, h = 0.09f,
                        size = 38f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.06f, y = 0.83f, w = 0.88f, h = 0.07f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(
                    x = 0.05f, y = 0.12f, w = 0.52f, h = 0.74f, rounded = true,
                    cells = listOf(
                        PhotoCell(0.05f, 0.16f, 0.16f, 0.68f, panOffsetX = -0.35f),
                        PhotoCell(0.225f, 0.12f, 0.16f, 0.72f, panOffsetX = 0f),
                        PhotoCell(0.40f, 0.18f, 0.16f, 0.68f, panOffsetX = 0.35f)
                    )
                ),
                decor = listOf(
                    DecorBox(DecorShape.BAR, 0.60f, 0.24f, 0.14f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.60f, y = 0.27f, w = 0.34f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.60f, y = 0.35f, w = 0.34f, h = 0.28f,
                        size = 44f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.60f, y = 0.66f, w = 0.34f, h = 0.18f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.20f
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

    // 6. photo-tag — Corner photo + tag
    PostTemplateSpec(
        id = "photo-tag",
        name = "Corner photo + tag",
        family = "Photo",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "NEW RELEASE",
            PostSlots.HEADLINE to "Clean editorial framing with an overlapping label card.",
            PostSlots.BODY to "Great for product highlights, book recommendations, and course launches.",
            PostSlots.BUTTON to "READ MORE"
        ),
        looks = listOf(
            TemplateLook("Editorial dark", "#F59E0B", "#080C14", PostBase.DARK),
            TemplateLook("Studio paper", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Mint card", "#10B981", "#06110D", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0.14f, 0.11f, 0.79f, 0.49f, rounded = true),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.52f, 0.78f, 0.36f, PostTone.DEEP, alpha = 242, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.52f, 0.78f, 0.008f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.55f, w = 0.42f, h = 0.04f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.61f, w = 0.70f, h = 0.12f,
                        size = 40f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.74f, w = 0.70f, h = 0.07f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.10f, y = 0.82f, w = 0.36f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0.32f, 0.11f, 0.62f, 0.66f, rounded = true),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.36f, 0.56f, 0.52f, PostTone.DEEP, alpha = 242, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.36f, 0.56f, 0.009f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.10f, y = 0.40f, w = 0.36f, h = 0.048f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.10f, y = 0.47f, w = 0.48f, h = 0.18f,
                        size = 38f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.06f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.67f, w = 0.48f, h = 0.11f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.18f
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.10f, y = 0.80f, w = 0.34f, h = 0.05f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                photo = PostPhotoArea(0.38f, 0.11f, 0.57f, 0.76f, rounded = true),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.05f, 0.24f, 0.48f, 0.62f, PostTone.DEEP, alpha = 242, panel = true),
                    DecorBox(DecorShape.BAR, 0.05f, 0.24f, 0.48f, 0.009f, PostTone.ACCENT, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.09f, y = 0.29f, w = 0.30f, h = 0.055f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.12f,
                        chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.09f, y = 0.38f, w = 0.40f, h = 0.22f,
                        size = 40f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.09f, y = 0.62f, w = 0.40f, h = 0.13f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.09f, y = 0.77f, w = 0.28f, h = 0.055f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    )
                )
            )
        )
    },

    // 7. photo-testimonial — Portrait + quote
    PostTemplateSpec(
        id = "photo-testimonial",
        name = "Portrait + quote",
        family = "Photo",
        slots = listOf(PostSlots.HEADLINE, PostSlots.EYEBROW, PostSlots.BODY),
        sample = mapOf(
            PostSlots.HEADLINE to "The structured lessons transformed how our students prepare for exams.",
            PostSlots.EYEBROW to "DR. SELAMAWIT TADESSE",
            PostSlots.BODY to "Academic Director · Addis Ababa"
        ),
        looks = listOf(
            TemplateLook("Gold portrait", "#F59E0B", "#080B12", PostBase.DARK),
            TemplateLook("Editorial cream", "#B45309", "#FAF5EC", PostBase.LIGHT),
            TemplateLook("Emerald trust", "#10B981", "#06110D", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(0.32f, 0.12f, 0.36f, 0.21f, circle = true),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.07f, 0.36f, 0.86f, 0.52f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.QUOTE, 0.11f, 0.38f, 0.16f, 0.09f, PostTone.ACCENT, alpha = 190, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.12f, y = 0.46f, w = 0.76f, h = 0.26f,
                        size = 44f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.10f
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.15f, y = 0.75f, w = 0.70f, h = 0.045f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.15f, y = 0.80f, w = 0.70f, h = 0.05f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL_WIDTH, anchor = VAnchor.TOP
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(0.07f, 0.24f, 0.30f, 0.40f, circle = true),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.41f, 0.16f, 0.53f, 0.68f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.QUOTE, 0.45f, 0.20f, 0.14f, 0.11f, PostTone.ACCENT, alpha = 190, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.45f, y = 0.31f, w = 0.45f, h = 0.34f,
                        size = 40f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.10f
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.45f, y = 0.68f, w = 0.45f, h = 0.05f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.45f, y = 0.74f, w = 0.45f, h = 0.06f,
                        size = 21f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.TOP
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(0.07f, 0.20f, 0.27f, 0.48f, circle = true),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.38f, 0.16f, 0.56f, 0.68f, PostTone.SOFT, alpha = 235, panel = true),
                    DecorBox(DecorShape.QUOTE, 0.42f, 0.20f, 0.12f, 0.12f, PostTone.ACCENT, alpha = 190, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.42f, y = 0.31f, w = 0.48f, h = 0.34f,
                        size = 42f, font = PostFont.SERIF, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.08f
                    ),
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.42f, y = 0.68f, w = 0.48f, h = 0.05f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.42f, y = 0.74f, w = 0.48f, h = 0.06f,
                        size = 21f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL_WIDTH, anchor = VAnchor.TOP
                    )
                )
            )
        )
    },

    // 8. photo-idcard — Speaker badge
    PostTemplateSpec(
        id = "photo-idcard",
        name = "Speaker badge",
        family = "Photo",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.LIST, PostSlots.NUMBER),
        sample = mapOf(
            PostSlots.EYEBROW to "KEYNOTE SPEAKER",
            PostSlots.HEADLINE to "Prof. Yonatan Alemu",
            PostSlots.BODY to "Saturday, Nov 21 · Main Auditorium",
            PostSlots.LIST to "AI in Education\nCurriculum Design\nLive Q&A Session",
            PostSlots.NUMBER to "VIP-08"
        ),
        looks = listOf(
            TemplateLook("Conference gold", "#F59E0B", "#070A12", PostBase.DARK),
            TemplateLook("Press pass light", "#2563EB", "#F8FAFC", PostBase.LIGHT),
            TemplateLook("Backstage mint", "#10B981", "#050E0B", PostBase.DARK)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                photo = PostPhotoArea(0.14f, 0.18f, 0.34f, 0.24f, rounded = true),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.08f, 0.11f, 0.84f, 0.78f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.08f, 0.11f, 0.84f, 0.045f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.PILL, 0.42f, 0.125f, 0.16f, 0.015f, PostTone.DEEP, panel = true),
                    DecorBox(DecorShape.LINE, 0.14f, 0.46f, 0.72f, 0.002f, PostTone.MUTED, alpha = 90, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.51f, y = 0.19f, w = 0.35f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.51f, y = 0.25f, w = 0.35f, h = 0.11f,
                        size = 38f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.51f, y = 0.37f, w = 0.26f, h = 0.045f,
                        size = 20f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.14f, y = 0.49f, w = 0.72f, h = 0.08f,
                        size = 25f, font = PostFont.BOLD, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.14f, y = 0.59f, w = 0.72f, h = 0.25f,
                        size = 25f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.25f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                photo = PostPhotoArea(0.11f, 0.22f, 0.30f, 0.44f, rounded = true),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.11f, 0.88f, 0.78f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.11f, 0.88f, 0.055f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.PILL, 0.43f, 0.128f, 0.14f, 0.018f, PostTone.DEEP, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.45f, y = 0.21f, w = 0.32f, h = 0.045f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.78f, y = 0.21f, w = 0.12f, h = 0.045f,
                        size = 19f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.45f, y = 0.27f, w = 0.44f, h = 0.14f,
                        size = 42f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.45f, y = 0.43f, w = 0.44f, h = 0.08f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.45f, y = 0.53f, w = 0.44f, h = 0.30f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.GRID,
                photo = PostPhotoArea(0.10f, 0.22f, 0.26f, 0.54f, rounded = true),
                decor = listOf(
                    DecorBox(DecorShape.CARD, 0.06f, 0.11f, 0.88f, 0.78f, PostTone.SOFT, alpha = 240, panel = true),
                    DecorBox(DecorShape.BAR, 0.06f, 0.11f, 0.88f, 0.06f, PostTone.ACCENT, panel = true),
                    DecorBox(DecorShape.PILL, 0.44f, 0.13f, 0.12f, 0.02f, PostTone.DEEP, panel = true)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.40f, y = 0.22f, w = 0.34f, h = 0.05f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.10f
                    ),
                    SlotBox(
                        slot = PostSlots.NUMBER,
                        x = 0.76f, y = 0.22f, w = 0.14f, h = 0.05f,
                        size = 19f, font = PostFont.CONDENSED, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.40f, y = 0.29f, w = 0.50f, h = 0.15f,
                        size = 44f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.40f, y = 0.46f, w = 0.50f, h = 0.08f,
                        size = 23f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.LIST,
                        x = 0.40f, y = 0.56f, w = 0.50f, h = 0.28f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        list = ListStyle.CHECK, fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.24f
                    )
                )
            )
        )
    }
)
