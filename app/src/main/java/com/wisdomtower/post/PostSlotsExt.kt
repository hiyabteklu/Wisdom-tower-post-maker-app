package com.wisdomtower.post

/**
 * Used when an old draft moves to the template that replaced it (PostDrafts.migrate).
 * Keeps every word the draft already has and only fills in a missing big letter or big word,
 * for the slots the new template actually uses. Nothing the user typed is overwritten.
 */
fun PostSlots.deriveLetterAndWord(
    texts: Map<String, String>,
    samples: Map<String, String>
): Map<String, String> = withDerivedSlots(texts, samples.keys)
