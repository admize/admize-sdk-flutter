package io.admize.sdk.flutter.admize_flutter_sdk;

import android.content.Context;

import androidx.annotation.NonNull;

import io.admize.sdk.android.ads.AdmizeAdRequest;
import io.admize.sdk.android.ads.AdmizeInterstitialAd;
import io.admize.sdk.android.ads.AdmizeInterstitialAdListener;

/**
 * A wrapper around load methods in GMA. This exists mainly to make the Android
 * code more testable.
 */
public class FlutterAdLoader {

    @NonNull
    private final Context context;

    public FlutterAdLoader(@NonNull Context context) {
        this.context = context;
    }

    /**
     * Load an interstitial ad.
     */
    public void loadInterstitial(
            @NonNull AdmizeAdRequest adRequest,
            @NonNull AdmizeInterstitialAdListener loadCallback) {
        AdmizeInterstitialAd.loadAd(context, adRequest, loadCallback);
    }
}
