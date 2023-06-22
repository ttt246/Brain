package com.matthaigh27.chatgptwrapper.utils.helpers

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.matthaigh27.chatgptwrapper.RisingApplication
import com.matthaigh27.chatgptwrapper.data.models.ImageModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageHelper {
    fun createSDCardFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File =
            RisingApplication.appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        )
    }

    @Suppress("UNREACHABLE_CODE")
    fun getBytesFromPath(path: String): ByteArray {
        val byteArray: ByteArray
        try {
            val stream = FileInputStream(path)
            byteArray = stream.readBytes()
            stream.close()
            return byteArray
        } catch (e: IOException) {
            throw Exception(e)
        }
        return byteArray
    }

    fun convertImageToByte(uri: Uri): ByteArray? {
        val data: ByteArray
        try {
            val cr = RisingApplication.appContext.contentResolver
            val inputStream: InputStream? = cr.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputByteArray = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputByteArray)
            data = outputByteArray.toByteArray()
        } catch (e: FileNotFoundException) {
            throw Exception(e)
        }
        return data
    }

    fun getImagesFromExternalStorage(contentResolver: ContentResolver): ArrayList<ImageModel> {
        val listOfImages = ArrayList<ImageModel>()

        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_MODIFIED)

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val modifiedDate = cursor.getLong(dateColumn)

                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString()
                )

                listOfImages.add(ImageModel(contentUri, modifiedDate))
            }
        }
        return listOfImages
    }


}