package io.admize.sdk.flutter.admize_flutter_sdk;

import android.content.Context;

import androidx.annotation.NonNull;

import io.admize.sdk.android.ads.AdmizeAds;
import io.admize.sdk.android.ads.init.AdmizeOnInitializationCompleteListener;

/**
 * A wrapper around static methods in
 * {@link io.admize.sdk.android.ads.AdmizeAds}.
 */
public class FlutterMobileAdsWrapper {

    public FlutterMobileAdsWrapper() {
    }

    /**
     * Initializes the sdk.
     */
    public void initialize(
            @NonNull Context context, @NonNull AdmizeOnInitializationCompleteListener listener) {
        AdmizeAds.initialize(context, listener);
    }
}
