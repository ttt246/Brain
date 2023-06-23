package com.matthaigh27.chatgptwrapper.data.repository

import com.google.firebase.storage.FirebaseStorage
import com.matthaigh27.chatgptwrapper.utils.helpers.network.OnFailure
import com.matthaigh27.chatgptwrapper.utils.helpers.network.OnSuccess

object FirebaseRepository {
    fun downloadImageWithName(
        name: String,
        onSuccess: OnSuccess<ByteArray>,
        onFailure: OnFailure<String>
    ) {
        val reference = "images/$name"

        val storageReference = FirebaseStorage.getInstance().getReference(reference)
        storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            onSuccess(bytes)
        }.addOnFailureListener { e ->
            onFailure(e.toString())
        }
        return
    }
}