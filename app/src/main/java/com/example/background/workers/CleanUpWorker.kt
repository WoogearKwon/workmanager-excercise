package com.example.background.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.OUTPUT_PATH
import timber.log.Timber
import java.io.File
import java.lang.Exception

/**
 * Cleans up temporary files generated during blurring process
 * */
class CleanUpWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        // Makes a notification when the work starts and slows down the work so that
        // it's easier to see each WorkRequest start, even on emulated devices
        makeStatusNotification("임시 파일들을 삭제하는 중..", applicationContext)
        sleep()

        return try {
            val outputDirectory = File(applicationContext.filesDir, OUTPUT_PATH)

            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()

                if (entries != null) {

                    for (entry in entries) {
                        val name = entry.name

                        if (name.isNotEmpty() && name.endsWith(".png")) {
                            val deleted = entry.delete()
                            Timber.i("$name 파일을 삭제함 - $deleted")
                        }
                    }
                }
            }

            Result.success()
        } catch (exception: Exception) {
            Timber.e(exception)
            Result.failure()
        }
    }
}