# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\777\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

-dontwarn com.akexorcist.roundcornerprogressbar.**
-dontwarn com.google.android.gms.internal.**

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.PreferenceActivity
-keep public class * extends android.view.View
-keep public class * extends android.widget.BaseAdapter
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * implements android.view.View.OnTouchListener
-keep public class * implements android.view.View.OnClickListener

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

-keepattributes *Annotation*
  -keepclassmembers class ** {
    @com.mindorks.placeholderview.annotations.** <methods>;
  }

  -keepattributes *Annotation*
  -keepclassmembers class ** {
      @org.greenrobot.eventbus.Subscribe <methods>;
  }
  -keep enum org.greenrobot.eventbus.ThreadMode { *; }

  #Picasso rules
  -dontwarn okhttp3.**
  -dontwarn okio.**
  -dontwarn javax.annotation.**
  -dontwarn org.conscrypt.**
  # A resource is loaded with a relative path so the package of this class must be preserved.
  -keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
