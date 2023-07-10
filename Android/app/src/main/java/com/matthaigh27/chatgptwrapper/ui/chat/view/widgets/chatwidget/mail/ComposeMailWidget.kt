package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.mail

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.OnHideListener
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.MailHelper.isGmail

class ComposeMailWidget(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), View.OnClickListener {

    private var context: Context
    var callback: ChatMessageInterface? = null

    private var edtMailFrom: TextInputLayout
    private var edtMailPassword: TextInputLayout
    private var swhMailType: SwitchMaterial
    private var edtMailTo: TextInputLayout
    private var edtMailSubject: TextInputLayout
    private var edtMailContent: TextInputLayout
    private var mailChipGroup: ChipGroup
    private var attachmentChipGroup: ChipGroup

    var hideListener: OnHideListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_mail_compose, this, true)
        this.context = context

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        this.setOnClickListener(this)
        findViewById<ImageView>(R.id.btn_send).setOnClickListener(this)
        findViewById<ImageView>(R.id.btn_send_cancel).setOnClickListener(this)
        findViewById<ImageView>(R.id.btn_attachment).setOnClickListener(this)

        mailChipGroup = findViewById(R.id.mail_chip_group)
        attachmentChipGroup = findViewById(R.id.attachment_chip_group)
        edtMailTo = findViewById(R.id.edt_mail_to)
        edtMailSubject = findViewById(R.id.edt_mail_subject)
        edtMailContent = findViewById(R.id.edt_mail_content)
        edtMailFrom = findViewById(R.id.edt_mail_from)
        edtMailPassword = findViewById(R.id.edt_mail_password)
        swhMailType = findViewById(R.id.swh_mail_type)

        edtMailTo.editText?.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                // convert the text into a chip
                createNewChip(edtMailTo.editText?.text.toString(), mailChipGroup)
                // clear the text
                edtMailTo.editText?.text?.clear()
                return@OnKeyListener true
            }
            false
        })
    }

    private fun createNewChip(chipText: String, chipGroup: ChipGroup) {
        if (chipText.isNotEmpty() && isGmail(chipText)) {
            val chip = Chip(context)
            chip.text = chipText
            chip.isCloseIconVisible = true

            chip.setOnCloseIconClickListener { view ->
                chipGroup.removeView(view)
            }

            chipGroup.addView(chip)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_send -> {
                callback?.sendMail(
                    from = edtMailFrom.editText?.text.toString(),
                    password = edtMailFrom.editText?.text.toString(),
                    to = edtMailTo.editText?.text.toString(),
                    subject = edtMailSubject.editText?.text.toString(),
                    body = edtMailContent.editText?.text.toString(),
                    isInbox = swhMailType.isChecked,
                    filename = "",
                    fileContent = "",
                )
            }

            R.id.btn_cancel -> {
                hideListener?.hide()
            }

            R.id.btn_attachment -> {

            }
        }
    }
}