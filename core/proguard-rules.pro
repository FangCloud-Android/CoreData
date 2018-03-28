# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/wangjinpeng/Program/android-sdk-macosx/tools/proguard/proguard-android.txt
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

# 使用CoreData的混淆代码
-keep class * extends com.coredata.core.CoreDao
-keep class com.coredata.annotation.Entity
-keepnames @com.coredata.annotation.Entity class *

# 如果使用到加密库，请将下面的代码也做配置
-keepclasseswithmembers class com.coredata.cipher.CipherOpenHelper {
    public <init>(...);
}
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }