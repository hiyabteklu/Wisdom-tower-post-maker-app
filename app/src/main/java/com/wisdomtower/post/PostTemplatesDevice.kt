package com.wisdomtower.post

/**
 * Kept Device Mockup family (2 templates: device-hero, device-headline).
 * Works with PostFrameExport ("Save transparent frame for video").
 */
internal val DeviceTemplates: List<PostTemplateSpec> = listOf(
    // 1. device-hero — Phone showcase
    PostTemplateSpec(
        id = "device-hero",
        name = "Phone showcase",
        family = "Device mockup",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE),
        sample = mapOf(
            PostSlots.EYEBROW to "NEW IN THE APP",
            PostSlots.HEADLINE to "Study smarter right from your phone"
        ),
        looks = listOf(
            TemplateLook("Gold studio", "#F59E0B", "#070910", PostBase.DARK),
            TemplateLook("Mint studio", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Light showcase", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(0.20f, 0.24f, 0.60f, 0.64f, rounded = true),
                decor = listOf(
                    DecorBox(DecorShape.PHONE, 0.20f, 0.24f, 0.60f, 0.64f, PostTone.ACCENT)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.20f, y = 0.09f, w = 0.60f, h = 0.038f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.135f, w = 0.84f, h = 0.085f,
                        size = 42f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(0.50f, 0.12f, 0.40f, 0.76f, rounded = true),
                decor = listOf(
                    DecorBox(DecorShape.PHONE, 0.50f, 0.12f, 0.40f, 0.76f, PostTone.ACCENT)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.28f, w = 0.38f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.36f, w = 0.38f, h = 0.34f,
                        size = 48f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.SPOT,
                photo = PostPhotoArea(0.54f, 0.10f, 0.34f, 0.80f, rounded = true),
                decor = listOf(
                    DecorBox(DecorShape.PHONE, 0.54f, 0.10f, 0.34f, 0.80f, PostTone.ACCENT)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.28f, w = 0.42f, h = 0.05f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        caps = true, fit = FitMode.FILL_WIDTH, tracking = 0.14f
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.36f, w = 0.42f, h = 0.34f,
                        size = 50f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    )
                )
            )
        )
    },

    // 2. device-headline — Headline + phone
    PostTemplateSpec(
        id = "device-headline",
        name = "Headline + phone",
        family = "Device mockup",
        slots = listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY, PostSlots.BUTTON),
        sample = mapOf(
            PostSlots.EYEBROW to "APP WALKTHROUGH",
            PostSlots.HEADLINE to "Practice tests, instant explanations, and progress tracking.",
            PostSlots.BODY to "Drop a screenshot or export a transparent frame for CapCut.",
            PostSlots.BUTTON to "GET THE APP"
        ),
        looks = listOf(
            TemplateLook("Gold app", "#F59E0B", "#070910", PostBase.DARK),
            TemplateLook("Emerald app", "#10B981", "#05100C", PostBase.DARK),
            TemplateLook("Clean light", "#2563EB", "#F8FAFC", PostBase.LIGHT)
        )
    ) { size ->
        size.pick3(
            story = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                photo = PostPhotoArea(0.22f, 0.34f, 0.56f, 0.54f, rounded = true),
                decor = listOf(
                    DecorBox(DecorShape.PHONE, 0.22f, 0.34f, 0.56f, 0.54f, PostTone.ACCENT)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.18f, y = 0.08f, w = 0.64f, h = 0.036f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.08f, y = 0.13f, w = 0.84f, h = 0.11f,
                        size = 38f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.CENTER, leading = 1.04f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.10f, y = 0.25f, w = 0.80f, h = 0.06f,
                        size = 22f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        centered = true, fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.26f, y = 0.90f, w = 0.48f, h = 0.045f,
                        size = 20f, font = PostFont.BOLD, tone = PostTone.ACCENT,
                        centered = true, caps = true, fit = FitMode.FILL_WIDTH
                    )
                )
            ),
            standard = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                photo = PostPhotoArea(0.52f, 0.12f, 0.38f, 0.76f, rounded = true),
                decor = listOf(
                    DecorBox(DecorShape.PHONE, 0.52f, 0.12f, 0.38f, 0.76f, PostTone.ACCENT)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.16f, w = 0.36f, h = 0.048f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.24f, w = 0.40f, h = 0.28f,
                        size = 42f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.07f, y = 0.55f, w = 0.40f, h = 0.14f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.07f, y = 0.73f, w = 0.32f, h = 0.08f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            ),
            wide = TemplateLayout(
                base = PostBase.DARK,
                backdrop = Backdrop.CORNER,
                photo = PostPhotoArea(0.55f, 0.10f, 0.34f, 0.80f, rounded = true),
                decor = listOf(
                    DecorBox(DecorShape.PHONE, 0.55f, 0.10f, 0.34f, 0.80f, PostTone.ACCENT)
                ),
                slots = listOf(
                    SlotBox(
                        slot = PostSlots.EYEBROW,
                        x = 0.07f, y = 0.16f, w = 0.34f, h = 0.05f,
                        size = 19f, font = PostFont.BOLD, tone = PostTone.DARK,
                        caps = true, fit = FitMode.FILL_WIDTH, chip = PostTone.ACCENT
                    ),
                    SlotBox(
                        slot = PostSlots.HEADLINE,
                        x = 0.07f, y = 0.24f, w = 0.42f, h = 0.28f,
                        size = 44f, font = PostFont.DISPLAY, tone = PostTone.TEXT,
                        fit = FitMode.FILL, anchor = VAnchor.TOP, leading = 1.05f
                    ),
                    SlotBox(
                        slot = PostSlots.BODY,
                        x = 0.07f, y = 0.55f, w = 0.42f, h = 0.14f,
                        size = 23f, font = PostFont.REGULAR, tone = PostTone.MUTED,
                        fit = FitMode.FILL, anchor = VAnchor.TOP
                    ),
                    SlotBox(
                        slot = PostSlots.BUTTON,
                        x = 0.07f, y = 0.73f, w = 0.30f, h = 0.085f,
                        size = 21f, font = PostFont.BOLD, tone = PostTone.DARK,
                        centered = true, caps = true, fit = FitMode.FILL, chip = PostTone.ACCENT
                    )
                )
            )
        )
    }
)
