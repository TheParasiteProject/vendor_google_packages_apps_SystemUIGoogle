/*
 * Copyright (C) 2022 The PixelExperience Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.systemui.power;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.UserHandle;
import android.text.format.DateFormat;
import android.util.Log;

import com.android.internal.annotations.VisibleForTesting;

import java.time.Clock;
import java.util.Locale;

public abstract class PowerUtils {
    public static PendingIntent createBatterySettingsPendingIntentAsUser(Context context) {
        return PendingIntent.getActivityAsUser(
                context,
                0,
                new Intent("android.intent.action.POWER_USAGE_SUMMARY"),
                67108864,
                null,
                UserHandle.CURRENT);
    }

    public static PendingIntent createHelpArticlePendingIntentAsUser(int i, Context context) {
        return PendingIntent.getActivityAsUser(
                context,
                0,
                new Intent("android.intent.action.VIEW", Uri.parse(context.getString(i))),
                67108864,
                null,
                UserHandle.CURRENT);
    }

    public static PendingIntent createPendingIntent(Context context, String str, Bundle bundle) {
        Intent flags = new Intent(str).setPackage(context.getPackageName()).setFlags(1342177280);
        if (bundle != null) {
            flags.putExtras(bundle);
        }
        return PendingIntent.getBroadcastAsUser(
                context, 0, flags, bundle != null ? 335544320 : 67108864, UserHandle.CURRENT);
    }

    public static PendingIntent createHelpArticlePendingIntent(Context context, int i) {
        return PendingIntent.getActivity(
                context,
                0,
                new Intent("android.intent.action.VIEW", Uri.parse(context.getString(i))),
                67108864);
    }

    public static PendingIntent createNormalChargingIntent(Context context, String str) {
        return PendingIntent.getBroadcastAsUser(
                context,
                0,
                new Intent(str).setPackage(context.getPackageName()).setFlags(1342177280),
                67108864,
                UserHandle.CURRENT);
    }

    public static boolean postNotificationThreshold(long j) {
        return j > 0 && Clock.systemUTC().millis() - j >= 600000;
    }

    public static int getBatteryLevel(Intent intent) {
        int intExtra = intent.getIntExtra("level", -1);
        int intExtra2 = intent.getIntExtra("scale", 0);
        if (intExtra2 == 0) {
            return -1;
        }
        return Math.round((intExtra / intExtra2) * 100.0f);
    }

    public static boolean isFullyCharged(Intent intent) {
        return (intent.getIntExtra("status", 1) == 5) || getBatteryLevel(intent) >= 100;
    }

    public static String getCurrentTime(Context context, long j) {
        Locale locale = getLocale(context);
        return DateFormat.format(
                        DateFormat.getBestDateTimePattern(
                                locale, DateFormat.is24HourFormat(context) ? "HH:mm" : "h:m"),
                        j)
                .toString()
                .toUpperCase(locale);
    }

    @VisibleForTesting
    public static Locale getLocale(Context context) {
        LocaleList locales = context.getResources().getConfiguration().getLocales();
        return (locales == null || locales.isEmpty()) ? Locale.getDefault() : locales.get(0);
    }

    public static boolean isFlipendoEnabled(ContentResolver contentResolver) {
        try {
            Bundle call =
                    contentResolver.call(
                            "com.google.android.flipendo.api",
                            "get_flipendo_state",
                            (String) null,
                            Bundle.EMPTY);
            if (call != null) {
                return call.getBoolean("flipendo_state", false);
            }
            return false;
        } catch (Exception e) {
            Log.e("PowerUtils", "isFlipendoEnabled() failed", e);
            return false;
        }
    }
}
