# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class * extends android.app.Activity
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}
-keep class com.google.** { *; }
-keep class android.support.** { *; }
-keep class com.balamurugan.marshmallowsdfix.MediaGranter



# Suppress warnings if you are NOT using IAP:
 -dontwarn com.google.**
 -dontwarn android.support.**



-keepattributes Signature

# For using GSON @Expose annotation

# Gson specific classes
#-keep class com.google.gson.stream.** { *; }


# The official support library.
#-keep class android.support.v4.app.** { *; }
#-keep interface android.support.v4.app.** { *; }

#  Library JARs.
#-keep class de.greenrobot.dao.** { *; }
#-keep interface de.greenrobot.dao.** { *; }

# Library projects.
#-keep class com.actionbarsherlock.** { *; }
#-keep interface com.actionbarsherlock.** { *; }
