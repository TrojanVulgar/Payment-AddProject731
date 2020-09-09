# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#project rules
-optimizations !class/unboxing/enum
-keep class net.makemoney.android.data.** { *; }
-optimizationpasses 3
-allowaccessmodification
-useuniqueclassmembernames
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**
-dontnote sun.misc.Unsafe
-dontnote kotlin.jvm.internal.**
-dontnote kotlin.internal.**
#-printconfiguration config.txt
#-repackageclasses
#-optimizations !method/removal/parameter

#okhttp
-dontnote okhttp3.**
-dontwarn okio.**
-dontnote okio.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

#retrofit
-dontwarn retrofit2.**
#-dontnote retrofit2.Platform
#-dontwarn retrofit2.Platform$Java8
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep class com.google.gson.** { *; }

#Crashlytics
#Added for faster builds
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

#Timber
-assumenosideeffects class timber.log.Timber* {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
}

#Coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

#Picasso
-dontwarn com.squareup.okhttp.**
-dontwarn org.conscrypt.**
#A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Fyber Mediation
-dontwarn com.fyber.mediation.annotations.MediationAPI
-dontwarn com.fyber.annotations.SDKFeatures
-dontwarn com.fyber.annotations.FyberTestSuite
-keepattributes JavascriptInterface
-keep class android.webkit.JavascriptInterface
-keep class com.fyber.mediation.MediationConfigProvider {
    public static *;
}
-keep class com.fyber.mediation.MediationAdapterStarter {
    public static *;
}
-keepclassmembers class com.fyber.ads.videos.mediation.RewardedVideoMediationJSInterface {
    void setValue(java.lang.String);
}

#AdColony
# For communication with AdColony's WebView
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep ADCNative class members unobfuscated
-keepclassmembers class com.adcolony.sdk.ADCNative** {
    *;
}

