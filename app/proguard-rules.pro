# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\Android\AsSdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# JNI
-keep class com.edwintech.vdp.jni.Avapi { *; }
-keep class com.rl.p2plib.BridgeService { *; }

# ucrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

# 极光推送
-dontoptimize
-dontpreverify
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

# GreenDao 3.0
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class net.sqlcipher.** { *; }

# If you do not use SQLCipher:
#-dontwarn org.greenrobot.greendao.database.**
# If you do not use Rx:
-dontwarn rx.**


# FastJson
-keepattributes Signature
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

# eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

#-keep class org.greenrobot.greendao.**{*;}
#-keep public interface org.greenrobot.greendao.**
#-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
#public static java.lang.String TABLENAME;
#}
#-keep class **$Properties
#-keep class net.sqlcipher.database.**{*;}
#-keep public interface net.sqlcipher.database.**
#-dontwarn net.sqlcipher.database.**
#-dontwarn org.greenrobot.greendao.**
#-dontwarn com.xiaomi.push.**
#-keep class com.xiaomi.push.** { *; }
#
#-keep class com.huawei.hms.**{*;}
