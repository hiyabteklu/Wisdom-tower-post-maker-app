# Correction plan: the editor must show everything, not hide it

Status: plan only. No code changes in this commit.

This replaces the editor layout of UI_UX_PLAN.md (section 4.2, Phases 2, 3 and 5 as far as layout goes).
Kept as they are: the renderer, drafts per template, the Library, Brand, export, and the Phase 6 work (two-speed preview, stale renders dropped, old studio moved to Brand > About).

## What went wrong

The goal is fast editing, not a tidy screen. The current editor hides things:

- category tabs (Templates, Words, Look, Photo, Icons, ...) so you must find the right tab first
- a second row of group chips inside each tab (English / Amharic, and so on)
- one small options area of fixed height at the bottom, so only one row is visible at a time
- one shared slider that appears only for some controls
- templates are not on the editing screen at all: you must open the Library (grid button)

Every change costs extra taps just to reach the control. That is slower than Canva, which defeats the purpose of the app.

## Target layout (portrait, one screen)

From top to bottom:

1. Top row: template name, bookmark, Library, Brand. Next to it the size chips (1:1, 4:3, 9:16) and Save.
2. Preview. Pinned, about 40 percent of the screen height. It shrinks when the keyboard opens. It never scrolls away.
3. Template strip. Pinned right under the preview. One horizontal row of template thumbnails with the name under each. Swipe sideways to browse, tap to switch. The open template is highlighted and scrolled into view. Each template keeps its own draft exactly as today.
4. Settings panel. Takes all the remaining height and scrolls vertically. Every section is stacked and visible by scrolling. No tabs, no group chips, no hidden panels.

Preview and template strip are always on screen together. Only the settings panel moves.

## Settings panel rules

- Vertical list of sections, in this order (most used first): Words, Colors, Background, Photo, Fonts, Logo, Icons, Footer, Decoration and text size.
- Each section has a small title and its controls directly under it.
- A control that is a list becomes a horizontal strip that scrolls sideways: color circles, font choices, icon shapes, gradient presets, size or style choices.
- A list with many items (colors, shapes, icons) is a sideways-scrolling grid of two rows, so more fits without opening anything.
- Fonts: one row for English fonts, one row for Amharic fonts, each scrolling sideways, with Add fonts at the end of the row.
- Sliders sit inline in their own section with label and value (text size, photo dim, icon size, logo size, background opacity). No shared slider.
- Switches (show logo, show footer) are plain rows with a switch.
- Text fields (Words) are normal fields, always visible.
- The only things that open on top are the Save sheet and the phone's file pickers.

## Gestures

- The preview is pinned, so it never fights the settings scroll.
- Icons section: when an icon is selected, drag and pinch on the preview move and resize it, as now.
- Logo section: Move logo still lets you drag the logo on the preview.
- Sideways strips inside the vertical panel: horizontal swipes belong to the strip, vertical swipes to the panel.

## Steps

C1. Layout shell. Pinned preview, template strip, vertical settings panel. Remove the tab row, group chips, fixed options area and shared slider rows from PostStudio.kt. Keep the PostStudioScreen inputs as they are.

C2. Sections. Turn what each tab and group shows today (PostEditorTabs.kt, PostIconsTab.kt, PostStudioExtras.kt, PostFooterEditor.kt) into stacked sections. Reuse the existing controls, only change where they sit.

C3. Lists. Colors, fonts, icon shapes, gradient presets become sideways strips, or two-row sideways grids when long. Inline sliders replace the shared slider.

C4. Template strip. Thumbnails from the existing disk cache (Phase 4), name under each, selected one highlighted and scrolled into view. Switching saves the template you leave and opens the other as last edited, as today. The Library stays for Saved, families and bookmarks. Bookmark stays in the top row.

C5. Check on the phone. Sizes, spacing, keyboard behavior, and a pass with Amharic text.

## How to check it (done when all of these are true)

- Preview and template strip are visible together without scrolling, in all three sizes.
- Swiping the strip and tapping a template switches it, and each template keeps its own edits.
- Changing a color or a font takes one tap on something already on screen.
- There is no tab or button you must press to find a setting.
- English and Amharic fonts are two separate rows that scroll sideways.
- Scrolling the settings never moves the preview or the template strip.
- Dragging an icon on the preview stays smooth (the two-speed preview stays).

## Assumptions (tell me if any is wrong)

1. "Two rows" means: long lists scroll sideways as two stacked rows, and fonts are one row per language.
2. The template strip shows all templates in Library order, not only one family.
3. Preview height is about 40 percent of the screen. Easy to change after you see it.
4. The section order above. Easy to change.
