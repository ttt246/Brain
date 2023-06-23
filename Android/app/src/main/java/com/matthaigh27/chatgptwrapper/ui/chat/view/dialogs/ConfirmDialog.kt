package com.matthaigh27.chatgptwrapper.ui.chat.view.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.matthaigh27.chatgptwrapper.R

class ConfirmDialog(context: Context) : Dialog(context), View.OnClickListener {

    private var txtMessage: TextView? = null
    private lateinit var btnClickListener: OnDialogButtonClickListener

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_ok -> {
                btnClickListener.onPositiveButtonClick()
            }

            R.id.btn_cancel -> {
                btnClickListener.onNegativeButtonClick()
            }
        }
        this.dismiss()
    }

    private fun initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_confirm)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        findViewById<Button>(R.id.btn_ok).setOnClickListener(this)
        findViewById<Button>(R.id.btn_cancel).setOnClickListener(this)

        txtMessage = findViewById(R.id.txt_confirm_message)

        setCanceledOnTouchOutside(true)
    }

    fun setOnClickListener(listener: OnDialogButtonClickListener) {
        btnClickListener = listener
    }

    fun setMessage(message: String) {
        txtMessage?.text = message
    }


    interface OnDialogButtonClickListener {
        fun onPositiveButtonClick()
        fun onNegativeButtonClick()
    }
}