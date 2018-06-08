package de.smartsquare.kickchain.util

object NameValidator {

    fun validateNotBlank(name: CharSequence): String? {
        if (name.isBlank()) {
            return "A name is required"
        } else {
            return null
        }
    }

    fun validateDistinct(names: List<CharSequence>): String? {
        // Workaround bug in SpannableStringBuilder which implements equals incorrectly by casting to String.
        if (names.map { it.toString() }.distinct().size == names.size) {
            return null
        } else {
            return "All names should be distinct"
        }
    }
}
