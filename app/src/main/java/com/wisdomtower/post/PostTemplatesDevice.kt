package com.wisdomtower.post

import kotlin.math.min

/**
 * Kept Device Mockup family (2 templates: device-hero, device-headline).
 * Works with PostFrameExport ("Save transparent frame for video").
 *
 * The owner's phone is a TECNO CAMON 30 whose screenshots are 1080 x 2436 (ratio 0.4433, about 9:20.3), so the screen window
 * always has that ratio. The phone body (metal rim and black glass border) is drawn around the screen window by
 * PostPhoneFrame (DecorShape.PHONE), using the PHONE_RIM and PHONE_BEZEL sizes, so the body is always a little bigger than the
 * photo window and the frame stays visible. Each placement below says where the BODY runs (top and bottom as a share of the
 * height, centre as a share of the width); the screen window is worked out from it.
 * The words, looks and samples are unchanged.
 */

/** Screenshot ratio of the TECNO CAMON 30: 1080 / 2436. */
private const val DV_SCREEN_RATIO = 1080f / 2436f

/** Body and screen rectangles as canvas fractions. */
private class DvPhone(
    val bx: Float, val by: Float, val bw: Float, val bh: Float,
    val sx: Float, val sy: Float, val sw: Float, val sh: Float
)

/**
 * A phone whose body runs from topFrac to bottomFrac of the height, centred on cxFrac of the width.
 * The screen is the body minus the rim and the glass border on every side, and has exactly the screenshot ratio in pixels.
 */
private fun dvPhone(size: PostSize, cxFrac: Float, topFrac: Float, bottomFrac: Float): DvPhone {
    val w = size.width.toFloat()
    val h = size.height.toFloat()
    val inset = min(w, h) * (PHONE_RIM + PHONE_BEZEL)
    val top = topFrac * h
    val bodyH = bottomFrac * h - top
    val screenH = bodyH - 2f * inset
    val screenW = screenH * DV_SCREEN_RATIO
    val bodyW = screenW + 2f * inset
    val left = cxFrac * w - bodyW / 2f
    return DvPhone(
        left / w, top / h, bodyW / w, bodyH / h,
        (left + inset) / w, (top + inset) / h, screenW / w, screenH / h
    )
}

private fun dvBody(p: DvPhone) = DecorBox(DecorShape.PHONE, p.bx, p.by, p.bw, p.bh, PostTone.DARK)

private fun dvScreen(p: DvPhone) = PostPhotoArea(p.sx, p.sy, p.sw, p.sh, rounded = true)

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
            story = dvPhone(size, 0.50f, 0.232f, 0.902f).let { p ->
                TemplateLayout(
                    base = PostBase.DARK,
                    backdrop = Backdrop.SPOT,
                    photo = dvScreen(p),
                    decor = listOf(dvBody(p)),
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
                )
            },
            standard = dvPhone(size, 0.70f, 0.105f, 0.900f).let { p ->
                TemplateLayout(
                    base = PostBase.DARK,
                    backdrop = Backdrop.SPOT,
                    photo = dvScreen(p),
                    decor = listOf(dvBody(p)),
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
                )
            },
            wide = dvPhone(size, 0.71f, 0.105f, 0.900f).let { p ->
                TemplateLayout(
                    base = PostBase.DARK,
                    backdrop = Backdrop.SPOT,
                    photo = dvScreen(p),
                    decor = listOf(dvBody(p)),
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
            }
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
            story = dvPhone(size, 0.50f, 0.322f, 0.902f).let { p ->
                TemplateLayout(
                    base = PostBase.DARK,
                    backdrop = Backdrop.CORNER,
                    photo = dvScreen(p),
                    decor = listOf(dvBody(p)),
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
                )
            },
            standard = dvPhone(size, 0.71f, 0.105f, 0.900f).let { p ->
                TemplateLayout(
                    base = PostBase.DARK,
                    backdrop = Backdrop.CORNER,
                    photo = dvScreen(p),
                    decor = listOf(dvBody(p)),
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
                )
            },
            wide = dvPhone(size, 0.72f, 0.105f, 0.900f).let { p ->
                TemplateLayout(
                    base = PostBase.DARK,
                    backdrop = Backdrop.CORNER,
                    photo = dvScreen(p),
                    decor = listOf(dvBody(p)),
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
            }
        )
    }
)
