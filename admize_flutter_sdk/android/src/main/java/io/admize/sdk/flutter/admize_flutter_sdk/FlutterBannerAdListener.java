package io.admize.sdk.flutter.admize_flutter_sdk;

import androidx.annotation.NonNull;

import io.admize.sdk.android.ads.AdmizeAdListener;

class FlutterBannerAdListener implements AdmizeAdListener {
    protected final int adId;
    @NonNull
    protected final AdInstanceManager manager;

    FlutterBannerAdListener(int adId, @NonNull AdInstanceManager manager) {
        this.adId = adId;
        this.manager = manager;
    }

    @Override
    public void onAdLoaded(String s) {
        manager.onAdLoaded(adId);
    }

    @Override
    public void onAdOpened() {
        manager.onAdOpened(adId);
    }

    @Override
    public void onAdClicked() {
        manager.onAdClicked(adId);
    }

    @Override
    public void onAdClosed() {
        manager.onAdClosed(adId);
    }

    @Override
    public void onAdFailedToLoad(int statusCode, String errorMessage) {
        manager.onAdFailedToLoad(this.adId, errorMessage);
    }
}