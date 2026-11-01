# Text style per box

Every text on a post can now be recolored, and every word box has its own color, font and size.
This is the note for the next session (and for any assistant) about how it works and what to check.

## Why some text did not change color before

The old "Text color" (`PostDoc.textHex`) only reached text whose template tone is TEXT or MUTED. Text on a colored block uses the LIGHT, SECONDARY or ACCENT tone, so it never followed the Text color. List markers, fixed marks (quote mark, ANSWER, Q and A letters) and the footer text had no color control at all.

## What exists now

- `SlotStyle(colorHex, fontEn, fontAm, scale)` in PostModel.kt. `PostDoc.slotStyles` maps a slot name to its style. Empty by default, so every template looks as designed until a style is set. A style with nothing set is dropped, so a post with no styles still counts as "not edited".
- Two extra keys for text that is not a word box: `PostSlots.MARKS` (fixed marks and badge letters, color only) and `PostSlots.FOOTER` (footer and social text, color only).
- Saved in the draft as an optional `slotStyles` object (document version stays 4). Old drafts have none and read as no styles.
- Renderer (PostRenderer.kt): color order for a word box is its own color, else the template tone. Font order for Latin letters is the box's own font, else the English font picked for the whole post, else the template font. Ethiopic letters use the box's own Amharic font, else the post's Amharic font, else the first Amharic font. Size is the template size x the post's Text size x the box's Size.
- A color picked for a list box also colors its markers and row rules. Fixed marks keep the template's font and size (the template centers them using the font's metrics) and only take a color.
- UI (PostTextStyle.kt): `WordBox` is the text field plus Color, Font and Size chips that open one small panel at a time, and Reset for that box. The color panel has A (template color), the four Brand colors, the shared palette, and "Use this color for all words". The font panel shows each font drawn in its own face, English and Amharic in separate rows. `OtherTextStyles` colors the marks and the footer text.
- Studio (PostStudio.kt): sections are collapsible cards with a 48 dp header, only Words starts open, sections reordered (Words, Colors, Background, Photo, Fonts, Text size, Layers and style, Logo, Icons, Footer), labels 13 sp bold.

## Not done on purpose

- No hex typing for the box color (the app keeps colors as circles only).
- Footer text takes a color only, no font or size.
- Dragged words keep their style (the style is keyed by slot name, not by position).

## Check on the phone (the assistant could not compile or run this)

1. The app builds. Fix reports go back as the next push.
2. Words: change the color of the headline on a dark block (for example Definition, Numbered tip). It must change. Reset puts it back.
3. Change the font of one box only. The preview must change that box and nothing else. Chips must show each font in its own face.
4. Size on one box, then the overall Text size: both must act together without clipping (the text shrinks to fit and the gold "Text shrunk to fit" note appears).
5. A list template (Checklist, Weekly timetable, Quiz): a color on the list must also color the ticks, letters and row labels.
6. Other text: Marks and badges (Editorial quote, Question and answer) and Footer text, also on Follow us (the social cards).
7. An Amharic text with an Amharic font in assets/fonts/amharic: pick it under Font > Amharic letters and check it shows. (The folder was empty on 2026-11-01.)
8. Open a draft saved before this change: it must open with no styles and look the same.
9. Export one post at 1:1 and 9:16 and compare with the preview.
