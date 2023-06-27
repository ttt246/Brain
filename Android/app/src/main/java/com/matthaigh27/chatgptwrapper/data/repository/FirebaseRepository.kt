package com.matthaigh27.chatgptwrapper.data.repository

import com.google.firebase.storage.FirebaseStorage
import com.google.protobuf.Empty
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.utils.helpers.OnFailure
import com.matthaigh27.chatgptwrapper.utils.helpers.OnSuccess
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    fun uploadImageAsync(
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

    suspend fun uploadImage(imageByteArray: ByteArray): String = suspendCoroutine { continuation ->
        val storageRef = FirebaseStorage.getInstance().reference
        val uuid = UUID.randomUUID()
        val imageName = "images/${uuid}"
        val imageRef = storageRef.child(imageName)

        val uploadTask = imageRef.putBytes(imageByteArray)

        uploadTask.addOnFailureListener {
            continuation.resume("Error")
        }.addOnSuccessListener {
            continuation.resume("$uuid")
        }
    }
}