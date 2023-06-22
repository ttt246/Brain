package com.matthaigh27.chatgptwrapper.utils.helpers

import android.text.InputFilter
import android.text.Spanned

class NoNewLineInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        return source?.filter { char -> char != '\n' } ?: ""
    }
}