# Chapter Summaries QA Fixture (`study-*`)

This fixture exercises all P2 capabilities of `PostStudyGrid.kt`, `PostTex.kt`, and `PostStudyInline.kt` across `study-list`, `study-table`, `study-define`, `study-cover`, `study-checklist`, and `study-mnemonic` at all 4 aspect ratios (`1:1`, `4:5`, `9:16`, `16:9`):

- **Line 1 Title Guarantee (`cell = 0`)**: `HEADLINE` always begins on the very first ruled line (`cell = 0`) with full page width (`room(0, width) == width`) and no `EYEBROW` or counter above the title.
- **Logo Keep-Out**: Study templates anchor the brand mark at `LogoAnchor.BOTTOM_RIGHT` outside the ruled lines.
- **Shrink-to-Readable-Minimum Policy**: Dense notes automatically step through `1.0x -> 0.90x -> 0.82x -> 0.75x -> 0.68x` (down to the `0.54 * step` readable floor) before leaving any rows out.

---

## 1. 5-Column Wide Table with Inline Math (`study-table`)

```markdown
| Zone | Altitude (m) | Mean Temp | Annual Rain | Dominant Crop |
|---|---|---|---|---|
| Wurch | > 3,200 m | $< 11.5^\circ\text{C}$ | 900–2,200 mm | Barley (ገብስ) |
| Dega | 2,300–3,200 m | $11.5–16^\circ\text{C}$ | 900–1,200 mm | Wheat, Teff |
| Woina Dega | 1,500–2,300 m | $16–20^\circ\text{C}$ | 800–1,200 mm | Maize, Teff |
| Kolla | 500–1,500 m | $20–27.5^\circ\text{C}$ | 200–800 mm | Sorghum |
| Bereha | < 500 m | $> 27.5^\circ\text{C}$ | < 200 mm | Irrigated Cotton |
```

## 2. 4×4 Matrix & Determinant (`study-list`)

```latex
$$
\begin{pmatrix}
a_{11} & a_{12} & a_{13} & a_{14} \\
a_{21} & a_{22} & a_{23} & a_{24} \\
a_{31} & a_{32} & a_{33} & a_{34} \\
a_{41} & a_{42} & a_{43} & a_{44}
\end{pmatrix}
\quad
\det(A) = \begin{vmatrix}
\lambda - 1 & 2 & 0 \\
0 & \lambda - 3 & 4 \\
0 & 0 & \lambda + 2
\end{vmatrix}
$$
```

## 3. Augmented Matrix (`[A | b]`)

```latex
$$
\left[\begin{array}{ccc|c}
1 & 2 & -1 & 4 \\
0 & 1 & 3 & -2 \\
0 & 0 & 1 & 5
\end{array}\right]
$$
```

## 4. Piecewise Function (`\begin{cases}`)

```latex
$$
f(x) = \begin{cases}
\dfrac{x^2 - 1}{x - 1}, & x \neq 1 \\
2x + \sin(\pi x), & 0 \leq x < 1 \\
0, & \text{otherwise}
\end{cases}
$$
```

## 5. Nested Fraction with Integral & Limits

```latex
$$
\mathcal{I}(\alpha) = \frac{\displaystyle \int_{0}^{\infty} \frac{x^2 e^{-\alpha x}}{\sqrt{1 + x^2}}\,dx}{\displaystyle 1 + \sum_{n=1}^{\infty} \frac{1}{n^2 + \alpha^2}}
$$
```

## 6. Chemical Reaction with Conditions (`\xrightarrow`, `\rightleftharpoons`, `\ce`)

```latex
$$
\ce{6CO2 + 6H2O} \xrightarrow[\text{chlorophyll}]{h\nu,\,\Delta} \ce{C6H12O6 + 6O2}\uparrow
$$
$$
\ce{N2 + 3H2} \xrightleftharpoons[\text{450}^\circ\text{C}]{\text{Fe catalyst, 200 atm}} \ce{2NH3}
$$
```

## 7. 12-Item Nested List + Amharic Paragraph

```markdown
1. Survey the chapter headings and summary boxes
  - Inspect diagrams, maps, and key formulas
  - Note unfamiliar bold terms
2. Question each heading before reading
  - Turn titles into "Why" and "How" questions
3. Read actively with pen in hand
  - Mark core definitions with [[#176BCE;B:blue ink]]
  - Highlight exam traps with [[#D11A26;H:red callouts]]
4. Recite aloud from memory without looking
  - Write down key equations from scratch
5. Review spaced over 1, 3, and 7 days
  - Test yourself using flashcards
>> የጥናት መመሪያ | ንቁ ተሳትፎ ያለው ጥናት (Active Recall) ከደጋግሞ ማንበብ በሦስት እጥፍ የተሻለ የማስታወስ ችሎታን ይገነባል።
```
