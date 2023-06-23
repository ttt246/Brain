package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.contact

import android.content.ContentUris
import android.content.Context
import android.provider.ContactsContract
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.data.models.ContactModel
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface
import de.hdodenhof.circleimageview.CircleImageView

class SearchContactWidget(
    context: Context, cotactModel: ContactModel, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), View.OnClickListener {

    private var context: Context

    private val civInfoAvatar: CircleImageView
    private val txtInfoName: TextView

    private var contactModel = cotactModel
    var callback:ChatMessageInterface? = null

    init {
        inflate(context, R.layout.widget_search_contact, this)
        this.context = context

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        civInfoAvatar = findViewById(R.id.civ_avatar)
        txtInfoName = findViewById(R.id.txt_info_name)

        txtInfoName.text = contactModel.displayName
        civInfoAvatar.setContactAvatar(context, contactModel.contactId.toLong())

        this.setOnClickListener(this)
    }

    fun CircleImageView.setContactAvatar(context: Context, contactId: Long) {
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


    private fun showContactDetailView() {
        val bottomSheetDialog = ContactDetailWidget(context, contactModel).apply {
            this.callback = callback
        }
        bottomSheetDialog.show()
    }

    override fun onClick(view: View?) {
        showContactDetailView()
    }
}