package com.matthaigh27.chatgptwrapper.utils.helpers

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import com.matthaigh27.chatgptwrapper.data.models.ContactModel

object ContactHelper {
    @SuppressLint("Range")
    fun getContacts(context: Context): ArrayList<ContactModel> {
        val resolver: ContentResolver = context.contentResolver;
        val cursor = resolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null, null
        )

        val contacts = ArrayList<ContactModel>()
        if (cursor!!.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = (cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                )).toInt()

                val contact = ContactModel()
                contact.id = id
                contact.name = name

                if (phoneNumber > 0) {
                    val cursorPhone = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        arrayOf(id),
                        null
                    )

                    if (cursorPhone!!.count > 0) {
                        while (cursorPhone.moveToNext()) {
                            val phoneNumValue = cursorPhone.getString(
                                cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                            contact.phoneList.add(phoneNumValue)
                        }
                    }
                    cursorPhone.close()
                }

                contacts.add(contact)
            }
        }
        cursor.close()
        return contacts
    }
}