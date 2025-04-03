# Base Android rules
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# Keep important Android classes
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

# Compose/Hilt rules
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keep @androidx.compose.runtime.Composable class *
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keep class androidx.lifecycle.ViewModel
-keep class * extends androidx.lifecycle.ViewModel
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep all Composables and previews
-keep @androidx.compose.ui.tooling.preview.Preview class *
-keepclassmembers class * {
    @androidx.compose.ui.tooling.preview.Preview *;
}

# Hardware monitoring specific
-keep class io.android.pixelspec.domain.model.** { *; }
-keep class io.android.pixelspec.data.datasource.** { *; }
-keep class io.android.pixelspec.presentation.component.** { *; }

# BatteryManager and system interactions
-keep class android.os.BatteryManager { *; }
-keep class android.os.** { *; }
-keep class android.hardware.** { *; }

# Retrofit (if used)
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# For JSON serialization
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Crash reporting (Firebase/Crashlytics)
-keep class com.google.firebase.** { *; }
-keep class com.crashlytics.** { *; }
-keepattributes SourceFile,LineNumberTable

# Keep resources
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Google Ads
-keep public class com.google.android.gms.ads.** {
   public *;
}

-keep public class com.google.ads.** {
   public *;
}

-keep class com.google.android.gms.** { *; }
-keep class com.google.ads.** { *; }

-keep class com.google.android.gms.ads.identifier.** { *; }

# SafeAtomicHelper fix
-keep class com.google.android.gms.internal.ads.zzgah { *; }
-keep class com.google.android.gms.internal.ads.zzgae { *; }