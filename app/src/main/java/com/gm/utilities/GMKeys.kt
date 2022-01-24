package com.gm.utilities

import com.gm.models.Model

object GMKeys {
    const val VIDEO_ENCRYPTION_TRANSFORMATION = "AES/CTR/NoPadding"
    const val APP_KEY = "app_key"  // Client id
    const val ENCRYPTION_KEY_DEV = "encryption_key_dev"
    const val VECTOR_KEY_DEV = "vector_key_dev"
    const val FORCE_UPDATE_KEY = "android_force_update"
    const val FORCE_UPDATE_VERSION_CODE_KEY = "android_version_code"
    const val WEATHER_API_KEY="weather_api_key"
    const val KEY_TOKEN_1 = "KEY_TOKEN_1"
    const val KEY_TOKEN_2 = "KEY_TOKEN_2"
    const val clientType = "Mobile-Android"
    const val SHARED_PREF = "APP_PREF"
    const val FCMKEY = "FCMKEY"
    const val REGISTRATION_COMPLETE = "registrationComplete"
    const val TOPIC_GLOBAL = "global"
    const val PENDING_BUTTON = 1
    const val COMPLETED_BUTTON = 2
    const val KEY_LOGIN_DATA = "KEY_LOGIN_DATA"
    const val LOGIN_USER_ID = "loginuserid"
    const val SESS_LOGIN_USER_ID = "loginuserid"
    const val LANGUAGE_ID = "Language_ID"
    const val FARMCODE = "FarmCode"
    const val PASSWORD = "Password"
    const val LanguageId = "LanguageId"
    const val isCompleted = "isCompleted"
    const val isFromNotification = "isFromNotification"
    const val selectedActivity = "activityModel"
    const val ACTION_CONNECTION = "com.gm.receiver.connectionchanged"
    const val PAGE_SIZE = 10
    const val UserId = "UserId"
    const val chartDetails = "ChartDetails"
    const val userName = "userName"  // Client id
    const val farmCode = "farmCode"  // Client id
    const val exsistingLoginUserId = "exsistingLoginUserId"  // Client id
    const val languageId = "languageId"
    const val RepositoryCategoryId = "RepositoryCategoryId"
    const val SupportCategoryId = "SupportCategoryId"
    const val SupportId = "SupportId"
    const val PageNo = "PageNo"
    const val token = "token"
    const val LATITUDE = "LATITUDE"
    const val LONGITUDE = "LONGITUDE"
    const val Village = "village"
    const val postalCode="postalCode"
    const val ChannelID = "ChannelID"
    const val Notification = "Notification"
    const val FILE_NOT_FOUND = "FILE_NOT_FOUND"
    val AUDIO_ID = 1
    val VIDEO_ID = 2
    val IMAGE_ID = 3
    var VIDEO_CAPTURED = 1
    var AUDIO_CAPTURED = 2
    var forwardTime = 2000
    var backwardTime = 2000
    var NotificatioRepository = 4
    var NotificationActivity = 1
    var NotificationSupport = 5
    var bodyWeight = 1
    var dayGain = 2
    var feedintake = 3
    var mortality = 4
    var fcr=5

    var ITEM_IN_CHART = 15

    const val repositoryActivity = "repositoryActivity"
    const val fromHomeActivity = "fromHomeActivity"
    const val fromNotification = "fromNotificationActivity"
    const val audio = "Audio"
    const val video = "Video"
    const val image = "Image"
    const val PushTokenKey = "PushTokenKey"
    const val selectedMediaType = "selectedMediaType"
    const val gallery = "gallery"
    const val KEY_DAILY = 1
    const val KEY_WEEKLY = 2
    const val KEY_BASEACTIVITY = 1
    const val KEY_DEFAULT_ACTIVITY = 2
    const val KEY_SHED_READY_ACTIVITY = 3
    const val HTTP_401_unauthorized = "HTTP 401 Unauthorized"
    const val mediaPlayerSample = "mediaPlayerSample"


    const val TAKE_PHOTO = 1
    const val TAKE_VIDEO = 2
    const val RECORD_AUDIO = 3
    const val GALLERY_INTENT = 4
    const val DOWNLOAD_STORAGE_PERMISSION_AUDIO = 5
    const val DOWNLOAD_STORAGE_PERMISSION_VIDEO = 6


    const val MEDIA_MINUTES = 2

}
