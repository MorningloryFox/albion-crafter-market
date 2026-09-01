# Retrofit & OkHttp
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Moshi rules
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep class com.squareup.moshi.** { *; }
-keep class com.example.data.remote.model.** { *; }
-keepclassmembers class com.example.data.remote.model.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
