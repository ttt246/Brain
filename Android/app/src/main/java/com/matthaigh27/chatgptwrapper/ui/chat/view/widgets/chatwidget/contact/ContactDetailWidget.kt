package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.contact

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.data.models.ContactModel
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface

class ContactDetailWidget(
    context: Context, contactModel: ContactModel
) : BottomSheetDialog(context), View.OnClickListener {

    private var imgAvatar: ImageView? = null
    private var txtDisplayName: TextView? = null
    private var btnEditContact: ImageView? = null
    private var llPhones: LinearLayout? = null

    private var contactModel = contactModel

    var callback: ChatMessageInterface? = null

    init {
        initView()
    }

    private fun initView() {
        setContentView(R.layout.widget_contact_detail)

        txtDisplayName = findViewById(R.id.txt_display_name)
        btnEditContact = findViewById(R.id.btn_edit_contact)
        llPhones = findViewById(R.id.ll_contacts)

        llPhones?.removeAllViews()
        txtDisplayName?.text = contactModel.displayName
        contactModel.phoneNumbers.forEach { phoneNumber ->
            val contactDetailItem = ContactDetailItem(context).apply {
                this.callback = callback
                this.setContactDetailItemInfo(phoneNumber, contactModel.displayName)
                this.setVisibilityListener(object :
                    ContactDetailItem.OnContactDetailVisibilityListener {
                    override fun invisible() {
                        this@ContactDetailWidget.dismiss()
                    }
                })
            }
            llPhones?.addView(contactDetailItem)
        }

        btnEditContact?.setOnClickListener(this)
        imgAvatar = findViewById(R.id.img_avatar)
        imgAvatar?.setContactAvatar(contactModel.contactId.toLong())
    }

    private fun ImageView.setContactAvatar(contactId: Long) {
        val uri = ContentUris.withAppendedId(
            ContactsContract.Contacts.CONTENT_URI, contactId
        )

        Glide.with(context)
            .load(uri)
            .placeholder(R.drawable.image_default_avatar) // Set placeholder image
            .error(R.drawable.image_default_avatar) // Set error image
            .fallback(R.drawable.image_default_avatar) // Set fallback image
            .into(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.btn_edit_contact -> {
                goToContactEditor(contactModel.contactId)
            }

            R.id.btn_send_message -> {
            }
        }
    }

    private fun goToContactEditor(contactId: String) {
        val editIntent = Intent(Intent.ACTION_EDIT)
        val contactUri =
            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong())
        editIntent.setData(contactUri)
        context.startActivity(editIntent)
    }
}