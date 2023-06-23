package com.matthaigh27.chatgptwrapper.utils.helpers.chat

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import com.matthaigh27.chatgptwrapper.data.local.entity.ContactEntity
import com.matthaigh27.chatgptwrapper.data.models.ContactModel
import com.matthaigh27.chatgptwrapper.data.repository.RoomRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

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

    suspend fun getChangedContacts(
        contacts: ArrayList<ContactModel>
    ): ArrayList<ContactModel> {
        return CoroutineScope(Dispatchers.IO).async {
            val originalContacts = RoomRepository.getAllContacts().value
            val changedContactList = ArrayList<ContactModel>()
            for (i in originalContacts!!.indices) {
                var isExist = false
                contacts.forEach { contact ->
                    if (originalContacts[i].id == contact.id) {
                        if (originalContacts[i].name != contact.name ||
                            originalContacts[i].phoneNumber != contact.phoneList.toString()
                        ) {
                            contact.status = "updated"
                            changedContactList.add(contact)

                            try {
                                RoomRepository.updateContact(
                                    ContactEntity(
                                        contact.id,
                                        contact.name,
                                        contact.phoneList.toString()
                                    )
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            contact.status = "nothing"
                        }
                        isExist = true
                        return@forEach
                    }
                }
                if (!isExist) {
                    val deletedContacts = ContactModel()
                    deletedContacts.id = originalContacts[i].id
                    deletedContacts.status = "deleted"
                    changedContactList.add(deletedContacts)

                    try {
                        RoomRepository.deleteContact(
                            ContactEntity(
                                deletedContacts.id,
                                deletedContacts.name,
                                deletedContacts.phoneList.toString()
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            contacts.forEach { contact ->
                if (contact.status.isEmpty()) {
                    contact.status = "created"
                    changedContactList.add(contact)
                    try {
                        RoomRepository.insertContact(
                            ContactEntity(
                                contact.id,
                                contact.name,
                                contact.phoneList.toString()
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            changedContactList
        }.await()
    }
}