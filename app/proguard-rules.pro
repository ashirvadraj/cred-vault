# Proguard rules for CredTracker

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# SQLCipher
-keep class net.zetetic.database.sqlcipher.** { *; }
-dontwarn net.zetetic.database.sqlcipher.**

# Google Play Services & Auth
-keep class com.google.android.gms.auth.api.** { *; }
-keep class com.google.api.services.gmail.** { *; }

# Models
-keep class com.credtracker.data.model.** { *; }
