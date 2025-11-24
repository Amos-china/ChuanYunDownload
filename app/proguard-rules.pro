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

##xpopup
#-dontwarn com.lxj.xpopup.widget.**
#-keep class com.lxj.xpopup.widget.**{*;}
#
##XXPermissions
#-keep class com.hjq.permissions.** {*;}
#
##AgentWeb
#-keep class com.just.agentweb.** {
#    *;
#}
#-dontwarn com.just.agentweb.**
#
##appupdate
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Service
#
##umeng
#-keep class com.umeng.** {*;}
#
#-keep class org.repackage.** {*;}
#
#-keep class com.uyumao.** { *; }
#
#-keepclassmembers class * {
#   public <init> (org.json.JSONObject);
#}
#
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#-keep public class com.zy.feichixiazai.R$*{
#public static final int *;
#}
#
## 保留 ButterKnife 的注解及其使用的类
#-keep class butterknife.** { *; }
#-keepclasseswithmembernames class * {
#    @butterknife.* <fields>;
#    @butterknife.* <methods>;
#}
#
## 保留绑定生成的代码
#-keep class **$$ViewBinder { *; }
#-keepclassmembers class * {
#    @butterknife.* <methods>;
#}
#
## 如果使用了 ButterKnife 8.8.0 或更高版本，可能需要保留 BindingAdapter
#-keep class **$$ViewBinding { *; }
#
## 防止警告
#-dontwarn butterknife.**


# 基础规则
#-keepattributes Signature
#-keepattributes *Annotation*
#
## ButterKnife
#-keep class butterknife.** { *; }
#-keepclasseswithmembernames class * {
#    @butterknife.* <fields>;
#    @butterknife.* <methods>;
#}
#-keep class **$$ViewBinder { *; }
#-dontwarn butterknife.**
#
## RxJava、RxAndroid、RxBinding
#-keep class io.reactivex.** { *; }
#-keep class com.jakewharton.rxbinding4.** { *; }
#-dontwarn io.reactivex.**
#-dontwarn com.jakewharton.rxbinding4.**
#
## Retrofit
#-keep interface com.squareup.** { *; }
#-keep class com.google.gson.** { *; }
#-keep class retrofit2.adapter.rxjava2.** { *; }
#-dontwarn retrofit2.**
#
## EventBus
#-keep class org.greenrobot.eventbus.** { *; }
#-keepclassmembers class ** {
#    public void onEvent*(...);
#}
#-dontwarn org.greenrobot.eventbus.**
#
## AgentWeb
#-keep class com.just.agentweb.** { *; }
#-dontwarn com.just.agentweb.**
#
## Room
#-keep class androidx.room.** { *; }
#-keepclassmembers class * {
#    @androidx.room.* <fields>;
#}
#-dontwarn androidx.room.**
#
## MMKV
#-keep class com.tencent.mmkv.** { *; }
#-dontwarn com.tencent.mmkv.**
#
## Umeng
#-keep class com.umeng.** { *; }
#-dontwarn com.umeng.**
#
## BaseRecyclerViewAdapterHelper
#-keep class com.chad.library.adapter.base.** { *; }
#-dontwarn com.chad.library.adapter.base.**
#
## XXPermissions
#-keep class com.hjq.permissions.** { *; }
#-dontwarn com.hjq.permissions.**
#
## MagicIndicator
#-keep class net.lucode.hackware.magicindicator.** { *; }
#-dontwarn net.lucode.hackware.magicindicator.**
#
## SuperTextView
#-keep class com.coorchice.library.** { *; }
#-dontwarn com.coorchice.library.**
#
## AppUpdate
#-keep class com.azhon.** { *; }
#-dontwarn com.azhon.**
#
## NumberProgressBar
#-keep class com.daimajia.numberprogressbar.** { *; }
#-dontwarn com.daimajia.numberprogressbar.**
#
#
#
## 保留入口类
#-keep class com.zy.feichixiazai.launcher.LauncherActivity { *; }
#

