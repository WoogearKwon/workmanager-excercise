package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import timber.log.Timber
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


/**
 * Saves the image to a permanent file
 * */
class SaveImageToFileWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    private val title = "처리된 이미지"
    private val dateFormatter = SimpleDateFormat(
            "yyyy.MM.dd 'at' HH:mm:ss z",
            Locale.getDefault()
    )

    override fun doWork(): Result {
        // Makes a notification when the work starts and slows down the work so that
        // it's easier to see each WorkRequest start, ,even on emulated devices
        makeStatusNotification("이미지 저장중..", applicationContext)
        sleep()

        val resolver = applicationContext.contentResolver

        return try {
            val resourceUri = inputData.getString(KEY_IMAGE_URI)
            val bitmap = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)))
            val imageUri = MediaStore.Images.Media.insertImage(
                    resolver, bitmap, title, dateFormatter.format(Date()))

            if (!imageUri.isNullOrEmpty()) {
                val output = workDataOf(KEY_IMAGE_URI to imageUri)
                Result.success()
            } else {
                Timber.e("Writing to MediaStore faield")
                Result.failure()
            }

        } catch (exception: Exception) {
            Timber.e(exception)
            Result.failure()
        }
    }
}