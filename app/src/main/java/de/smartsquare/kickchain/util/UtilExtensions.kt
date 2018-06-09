package de.smartsquare.kickchain.util

import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout

var TextInputLayout.simpleError
    get() = this.error
    set(value) {
        this.isErrorEnabled = value != null
        this.error = value
    }

val TextInputLayout.trimmedText
    get() = this.editText?.text?.trim() ?: ""

val TextInputEditText.trimmedText
    get() = text.trim()

val TextInputEditText.inputLayout
    get() = this.parent.parent as TextInputLayout