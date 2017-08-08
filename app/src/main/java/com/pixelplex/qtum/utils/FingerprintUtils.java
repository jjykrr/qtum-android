package com.pixelplex.qtum.utils;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompatApi23;


public final class FingerprintUtils {
    private FingerprintUtils() {
    }

    public enum mSensorState {
        NOT_BLOCKED,
        NO_FINGERPRINTS,
        READY
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static mSensorState checkSensorState(@NonNull Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (!keyguardManager.isKeyguardSecure()) {
            return mSensorState.NOT_BLOCKED;
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (!FingerprintManagerCompat.from(context).hasEnrolledFingerprints()) {
                return mSensorState.NO_FINGERPRINTS;
            }
        } else {
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                return mSensorState.NO_FINGERPRINTS;
            }
        }

        return mSensorState.READY;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isSensorStateAt(@NonNull mSensorState state, @NonNull Context context) {
        return checkSensorState(context) == state;
    }
}
