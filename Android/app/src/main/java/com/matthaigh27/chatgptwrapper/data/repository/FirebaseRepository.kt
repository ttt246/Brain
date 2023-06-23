package com.matthaigh27.chatgptwrapper.data.repository

import com.google.firebase.storage.FirebaseStorage
import com.matthaigh27.chatgptwrapper.utils.helpers.OnFailure
import com.matthaigh27.chatgptwrapper.utils.helpers.OnSuccess
import java.util.UUID

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

    fun uploadImage(
        imageByteArray: ByteArray,
        onSuccess: OnSuccess<String>,
        onFailure: OnFailure<String>
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val uuid = UUID.randomUUID()
        val imageName = "images/${uuid}"
        val imageRef = storageRef.child(imageName)

        val uploadTask = imageRef.putBytes(imageByteArray)
        uploadTask.addOnFailureListener {
            onFailure("Fail to upload image to firebase.")
        }.addOnSuccessListener {
            onSuccess("$uuid")
        }
    }
}