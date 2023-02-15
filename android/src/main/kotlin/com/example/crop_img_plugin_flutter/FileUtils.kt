package com.example.crop_img_plugin_flutter

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import com.yalantis.ucrop.util.FileUtils.*
import java.io.*


internal class FileUtils {
    fun getPathFromUri(context: Context, uri: Uri): String? {
        var path = getPathFromLocalUri(context, uri)
        if (path == null) {
            path = getPathFromRemoteUri(context, uri)
        }
        return path
    }

    @SuppressLint("NewApi")
    private fun getPathFromLocalUri(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                if (!TextUtils.isEmpty(id)) {
                    return try {
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id)
                        )
                        getDataColumn(context, contentUri, null, null)
                    } catch (e: java.lang.NumberFormatException) {
                        null
                    }
                }
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) {
                uri.lastPathSegment
            } else getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    companion object {
        private fun getDataColumn(
            context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?
        ): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor =
                    context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }

        private fun getPathFromRemoteUri(context: Context, uri: Uri): String? {
            // The code below is why Java now has try-with-resources and the Files utility.
            var file: File? = null
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            var success = false
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                file = File.createTempFile("image_picker", "jpg", context.cacheDir)
                outputStream = FileOutputStream(file)
                if (inputStream != null) {
                    copy(inputStream, outputStream)
                    success = true
                }
            } catch (ignored: IOException) {
            } finally {
                try {
                    inputStream?.close()
                } catch (ignored: IOException) {
                }
                try {
                    outputStream?.close()
                } catch (ignored: IOException) {
                    // If closing the output stream fails, we cannot be sure that the
                    // target file was written in full. Flushing the stream merely moves
                    // the bytes into the OS, not necessarily to the file.
                    success = false
                }
            }
            return if (success) file!!.path else null
        }

        @Throws(IOException::class)
        private fun copy(`in`: InputStream, out: OutputStream) {
            val buffer = ByteArray(4 * 1024)
            var bytesRead: Int
            while (`in`.read(buffer).also { bytesRead = it } != -1) {
                out.write(buffer, 0, bytesRead)
            }
            out.flush()
        }

        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        private fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority
        }
    }
}
