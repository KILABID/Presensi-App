package com.murad.presensi.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.murad.presensi.R

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var showPasswordIcon: Drawable? = null
    private var hidePasswordIcon: Drawable? = null
    private var isPasswordVisible = false

    init {
        setupIcons()
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                val currentInputType = inputType

                // Validasi hanya dilakukan jika inputType bukan tipe teks biasa
                if (currentInputType != InputType.TYPE_CLASS_TEXT) {
                    when (currentInputType) {
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT -> {
                            val isValid =
                                android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
                            error = if (!isValid) "Email tidak valid" else null
                        }

                        InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT -> {
                            val isValid = input.length >= 8
                            error = if (!isValid) "Password harus lebih dari 8 karakter" else null
                        }

                        else -> {
                            error = when {
                                input.contains(" ") -> "Tidak boleh mengandung spasi"
                                input.isEmpty() -> "Tidak boleh kosong"
                                else -> null
                            }
                        }
                    }
                    setBackgroundResource(
                        if (error != null) R.drawable.rectangle_edit_text_error
                        else R.drawable.rectangle_edit_text_success
                    )
                } else {
                    // Reset error dan background jika tipe teks biasa
                    error = null
                    setBackgroundResource(R.drawable.rectangle_edit_text_success)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        setOnTouchListener(this)
    }

    private fun setupIcons() {
        showPasswordIcon = ContextCompat.getDrawable(context, R.drawable.show_password_icon)
        hidePasswordIcon = ContextCompat.getDrawable(context, R.drawable.show_password_icon)
        updatePasswordIcon()
    }

    private fun updatePasswordIcon() {
        // Menampilkan ikon hanya jika inputType adalah password
        val isPasswordField =
            inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) ||
                    inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)

        if (isPasswordField) {
            val icon = if (isPasswordVisible) hidePasswordIcon else showPasswordIcon
            setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            val drawableEnd = compoundDrawables[2]
            if (drawableEnd != null && event.rawX >= (right - drawableEnd.bounds.width())) {
                togglePasswordVisibility()
                return true
            }
        }
        return false
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        setSelection(text?.length ?: 0)
        updatePasswordIcon()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setPadding(20, 0, 15, 0)
    }
}
