# NamiWallet ProGuard Rules

# Keep JNI methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep NamiCore bridge class
-keep class com.namiwallet.bridge.NamiCore { *; }
-keep class com.namiwallet.bridge.NamiCore$* { *; }

# Keep data classes used with Retrofit/Gson
-keep class com.namiwallet.network.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# ZXing
-keep class com.google.zxing.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.InstallIn class *
-keep @dagger.hilt.android.components.** class *
