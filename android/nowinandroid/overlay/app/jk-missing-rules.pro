# AGP's missing_rules.txt equivalent: compile-only references R8 full mode flags as
# missing. API-35 platform classes (we link SDK 34) and the androidx.window/emoji2
# reflection-guarded extension surfaces — all runtime-optional by design.
-dontwarn android.view.ScrollFeedbackProvider
-dontwarn androidx.emoji2.text.flatbuffer.**
-dontwarn androidx.window.extensions.**
-dontwarn androidx.window.sidecar.**
