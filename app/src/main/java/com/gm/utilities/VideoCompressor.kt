package com.gm.utilities

import android.util.Log
import com.gm.VideoCompressor.VideoCompress

@Suppress("INACCESSIBLE_TYPE")
class VideoCompressor(var sourcPath: String?, var destinationPath: String?) {
    fun compressLowQualityVideo(listener:VideoCompress.CompressListener) {
        sourcPath?.let { Log.i("SOURCE PATH ", it) }
        destinationPath?.let { Log.i("DESTINATION PATH ", it) }
        VideoCompress.compressVideoLow(sourcPath, destinationPath,listener)
    }
}
