package com.wisdomtower.post

/**
 * LaTeX to clean one-line text for the handwritten Chapter summaries (docs/CHAPTER_SUMMARIES_PLAN.md, section 7).
 *
 * The notes sit on a ruled 30-line grid, so a formula must stay inside ONE line height: no stacked fractions, no tall roots.
 * This turns the usual school-level LaTeX (Greek letters, powers, indices, fractions, roots, arrows, units) into plain text
 * with Unicode symbols, the way a student would write it on one line: $\omega=2\pi f$ becomes ω=2πf,
 * $a_{max}=\omega^2 A$ becomes aₘₐₓ=ω²A, $T=2\pi\sqrt{L/g}$ becomes T=2π√(L/g).
 *
 * Rules:
 * - Powers and indices become Unicode superscripts and subscripts when every character has one; otherwise ^(...) or _(...).
 * - \frac{a}{b} becomes a/b, with brackets when a or b is not a single block. 1/2, 1/3, 2/3, 1/4, 3/4 become ½ ⅓ ⅔ ¼ ¾.
 * - Anything unknown never crashes: the command name is kept as plain text. A formula that cannot be read is returned as typed.
 * A real two-line (stacked) display formula is planned for a later slice and will take two grid lines on purpose.
 */
internal object PostMath {

    fun toText(latex: String): String =
        runCatching { Parser(latex).run() }.getOrDefault(latex).replace(Regex(" {2,}"), " ").trim()

    private val SUP: Map<Char, Char> = mapOf(
        '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴', '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹',
        '+' to '⁺', '-' to '⁻', '−' to '⁻', '=' to '⁼', '(' to '⁽', ')' to '⁾',
        'a' to 'ᵃ', 'b' to 'ᵇ', 'c' to 'ᶜ', 'd' to 'ᵈ', 'e' to 'ᵉ', 'f' to 'ᶠ', 'g' to 'ᵍ', 'h' to 'ʰ', 'i' to 'ⁱ', 'j' to 'ʲ',
        'k' to 'ᵏ', 'l' to 'ˡ', 'm' to 'ᵐ', 'n' to 'ⁿ', 'o' to 'ᵒ', 'p' to 'ᵖ', 'r' to 'ʳ', 's' to 'ˢ', 't' to 'ᵗ', 'u' to 'ᵘ',
        'v' to 'ᵛ', 'w' to 'ʷ', 'x' to 'ˣ', 'y' to 'ʸ', 'z' to 'ᶻ', '°' to '°'
    )

    private val SUB: Map<Char, Char> = mapOf(
        '0' to '₀', '1' to '₁', '2' to '₂', '3' to '₃', '4' to '₄', '5' to '₅', '6' to '₆', '7' to '₇', '8' to '₈', '9' to '₉',
        '+' to '₊', '-' to '₋', '−' to '₋', '=' to '₌', '(' to '₍', ')' to '₎',
        'a' to 'ₐ', 'e' to 'ₑ', 'h' to 'ₕ', 'i' to 'ᵢ', 'j' to 'ⱼ', 'k' to 'ₖ', 'l' to 'ₗ', 'm' to 'ₘ', 'n' to 'ₙ', 'o' to 'ₒ',
        'p' to 'ₚ', 'r' to 'ᵣ', 's' to 'ₛ', 't' to 'ₜ', 'u' to 'ᵤ', 'v' to 'ᵥ', 'x' to 'ₓ'
    )

    private val SUP_VALUES: Set<Char> = SUP.values.toSet()
    private val SUB_VALUES: Set<Char> = SUB.values.toSet()

    private val FRACTIONS: Map<String, String> = mapOf(
        "1/2" to "½", "1/3" to "⅓", "2/3" to "⅔", "1/4" to "¼", "3/4" to "¾"
    )

    private val SYMBOLS: Map<String, String> = mapOf(
        "alpha" to "α", "beta" to "β", "gamma" to "γ", "delta" to "δ", "epsilon" to "ε", "varepsilon" to "ε", "zeta" to "ζ",
        "eta" to "η", "theta" to "θ", "vartheta" to "ϑ", "iota" to "ι", "kappa" to "κ", "lambda" to "λ", "mu" to "μ", "nu" to "ν",
        "xi" to "ξ", "pi" to "π", "rho" to "ρ", "sigma" to "σ", "tau" to "τ", "upsilon" to "υ", "phi" to "φ", "varphi" to "φ",
        "chi" to "χ", "psi" to "ψ", "omega" to "ω",
        "Gamma" to "Γ", "Delta" to "Δ", "Theta" to "Θ", "Lambda" to "Λ", "Xi" to "Ξ", "Pi" to "Π", "Sigma" to "Σ",
        "Phi" to "Φ", "Psi" to "Ψ", "Omega" to "Ω",
        "cdot" to "·", "times" to "×", "div" to "÷", "pm" to "±", "mp" to "∓", "leq" to "≤", "le" to "≤", "geq" to "≥", "ge" to "≥",
        "neq" to "≠", "ne" to "≠", "approx" to "≈", "sim" to "∼", "propto" to "∝", "equiv" to "≡", "infty" to "∞", "partial" to "∂",
        "nabla" to "∇", "to" to "→", "rightarrow" to "→", "leftarrow" to "←", "Rightarrow" to "⇒", "Leftarrow" to "⇐",
        "Leftrightarrow" to "⇔", "leftrightarrow" to "↔", "uparrow" to "↑", "downarrow" to "↓", "degree" to "°", "circ" to "°",
        "angle" to "∠", "perp" to "⊥", "parallel" to "∥", "therefore" to "∴", "because" to "∵", "in" to "∈", "notin" to "∉",
        "subset" to "⊂", "cup" to "∪", "cap" to "∩", "forall" to "∀", "exists" to "∃", "sum" to "∑", "prod" to "∏", "int" to "∫",
        "oint" to "∮", "ldots" to "…", "dots" to "…", "cdots" to "⋯", "hbar" to "ħ", "ell" to "ℓ", "prime" to "′", "mid" to "|",
        "langle" to "⟨", "rangle" to "⟩", "lbrace" to "{", "rbrace" to "}", "lbrack" to "[", "rbrack" to "]"
    )

    private class Parser(private val s: String) {
        private var i = 0

        fun run(): String = seq(false)

        private fun skipSpaces() {
            while (i < s.length && s[i] == ' ') i++
        }

        /** Reads until the end, or until the closing brace of the group when untilBrace is true (the brace is left for the caller). */
        private fun seq(untilBrace: Boolean): String {
            val out = StringBuilder()
            while (i < s.length) {
                val c = s[i]
                if (c == '}') {
                    if (untilBrace) return out.toString()
                    i++
                } else if (c == '{') {
                    i++
                    out.append(seq(true))
                    if (i < s.length && s[i] == '}') i++
                } else if (c == '^') {
                    i++
                    out.append(script(arg(), true))
                } else if (c == '_') {
                    i++
                    out.append(script(arg(), false))
                } else if (c == '\\') {
                    out.append(command())
                } else if (c == '~' || c == '&') {
                    out.append(' ')
                    i++
                } else if (c == '-') {
                    out.append('−')
                    i++
                } else {
                    out.append(c)
                    i++
                }
            }
            return out.toString()
        }

        /** One argument: a {group}, a \\command, or a single character. */
        private fun arg(): String {
            skipSpaces()
            if (i >= s.length) return ""
            val c = s[i]
            if (c == '{') {
                i++
                val inner = seq(true)
                if (i < s.length && s[i] == '}') i++
                return inner
            }
            if (c == '\\') return command()
            i++
            return if (c == '-') "−" else c.toString()
        }

        private fun command(): String {
            i++ // the backslash
            if (i >= s.length) return ""
            val first = s[i]
            if (!first.isLetter()) {
                i++
                return when (first) {
                    ',', ';', ':', ' ' -> " "
                    '!' -> ""
                    '\\' -> " "
                    else -> first.toString()
                }
            }
            val start = i
            while (i < s.length && s[i].isLetter()) i++
            val name = s.substring(start, i)
            when (name) {
                "frac", "dfrac", "tfrac" -> {
                    val a = arg()
                    val b = arg()
                    return frac(a, b)
                }
                "sqrt" -> {
                    skipSpaces()
                    var index = ""
                    if (i < s.length && s[i] == '[') {
                        val end = s.indexOf(']', i)
                        if (end > i) {
                            index = s.substring(i + 1, end)
                            i = end + 1
                        }
                    }
                    return root(index, arg())
                }
                "text", "textbf", "textit", "mathrm", "mathbf", "mathit", "mathsf", "boldsymbol", "operatorname", "mbox" -> return arg()
                "vec" -> return mark(arg(), '\u20D7')
                "hat" -> return mark(arg(), '\u0302')
                "bar", "overline" -> return mark(arg(), '\u0304')
                "dot" -> return mark(arg(), '\u0307')
                "ddot" -> return mark(arg(), '\u0308')
                "tilde" -> return mark(arg(), '\u0303')
                "left", "right", "big", "Big", "bigl", "bigr", "Bigl", "Bigr" -> {
                    if (i < s.length && s[i] == '.') i++
                    return ""
                }
                "quad", "qquad" -> return "  "
                "limits", "nolimits", "displaystyle", "textstyle", "scriptstyle" -> return ""
            }
            val symbol = SYMBOLS[name]
            if (symbol != null) {
                // TeX ignores the space after a letter-like command: \omega t is ωt. After an operator the space is kept: a \leq b.
                if (symbol.length == 1 && symbol[0].isLetter()) skipSpaces()
                return symbol
            }
            return name // sin, cos, tan, log, ln, lim, max, min ... are written as they are
        }

        private fun mark(base: String, combining: Char): String = if (base.isEmpty()) "" else base + combining

        private fun isAtom(t: String): Boolean = t.isNotEmpty() && t.none { it == ' ' || it in "+-−×·/=±" }

        private fun wrap(t: String): String = if (isAtom(t)) t else "($t)"

        private fun frac(a: String, b: String): String {
            FRACTIONS[a + "/" + b]?.let { return it }
            return wrap(a) + "/" + wrap(b)
        }

        private fun root(index: String, a: String): String {
            val sign = when (index) {
                "", "2" -> "√"
                "3" -> "∛"
                "4" -> "∜"
                else -> script(index, true) + "√"
            }
            return sign + (if (a.length <= 1) a else "($a)")
        }

        private fun script(arg: String, up: Boolean): String {
            if (arg.isEmpty()) return ""
            val map = if (up) SUP else SUB
            val already = if (up) SUP_VALUES else SUB_VALUES
            val mapped = StringBuilder()
            for (ch in arg) {
                val m = map[ch]
                if (m != null) mapped.append(m)
                else if (ch in already) mapped.append(ch)
                else return (if (up) "^" else "_") + (if (arg.length == 1) arg else "($arg)")
            }
            return mapped.toString()
        }
    }
}
