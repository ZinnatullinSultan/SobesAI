# Сохраняем модели данных для сериализации
-keepattributes *Annotation*, Signature, InnerClasses
-keepclassmembers class com.example.sobesai.data.remote.model.** { *; }

# Koin
-keepclassmembers class * {
    @org.koin.core.annotation.KoinInternalApi *;
}

# Tracer
-keep class ru.ok.tracer.** { *; }
-dontwarn ru.ok.tracer.**

-keepclassmembers class androidx.compose.runtime.Recomposer { *; }

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.google.errorprone.annotations.CanIgnoreReturnValue
-dontwarn com.google.errorprone.annotations.CheckReturnValue
-dontwarn com.google.errorprone.annotations.Immutable
-dontwarn com.google.errorprone.annotations.RestrictedApi