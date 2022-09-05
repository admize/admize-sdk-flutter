package io.admize.sdk.flutter.admize_flutter_sdk;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import io.admize.sdk.android.ads.AdmizeInterstitialAd;
import io.admize.sdk.android.ads.AdmizeInterstitialAdListener;

class FlutterInterstitialAd extends FlutterAd.FlutterOverlayAd {
    private static final String TAG = "FlutterInterstitialAd";

    @NonNull
    private final AdInstanceManager manager;
    @NonNull
    private final FlutterAdRequest request;
    @Nullable
    private AdmizeInterstitialAd ad;
    @NonNull
    private final FlutterAdLoader flutterAdLoader;

    public FlutterInterstitialAd(
            int adId,
            @NonNull AdInstanceManager manager,
            @NonNull FlutterAdRequest request,
            @NonNull FlutterAdLoader flutterAdLoader) {
        super(adId);
        this.manager = manager;
        this.request = request;
        this.flutterAdLoader = flutterAdLoader;
    }

    @Override
    void load() {
        flutterAdLoader.loadInterstitial(request.asAdRequest(),
                new DelegatingInterstitialAdLoadCallback(this));
    }

    void onAdLoaded(AdmizeInterstitialAd ad) {
        this.ad = ad;
        manager.onAdLoaded(adId);
    }

    void onAdFailedToLoad(String errorMessage) {
        manager.onAdFailedToLoad(adId, errorMessage);
    }

    void onAdOpened() {
        manager.onAdOpened(adId);
    }

    void onAdClicked() {
        manager.onAdClicked(adId);
    }

    void onAdClosed() {
        manager.onAdClosed(adId);
    }

    @Override
    void dispose() {
        ad = null;
    }

    @Override
    public void show() {
        if (ad == null) {
            Log.e(TAG, "Error showing interstitial - the interstitial ad wasn't loaded yet.");
            return;
        }
        if (manager.getActivity() == null) {
            Log.e(TAG, "Tried to show interstitial before activity was bound to the plugin.");
            return;
        }
        ad.show(manager.getActivity());
    }

    /**
     * An InterstitialAdLoadCallback that just forwards events to a delegate.
     */
    private static final class DelegatingInterstitialAdLoadCallback
            implements AdmizeInterstitialAdListener {

        private final WeakReference<FlutterInterstitialAd> delegate;

        DelegatingInterstitialAdLoadCallback(FlutterInterstitialAd delegate) {
            this.delegate = new WeakReference<>(delegate);
        }

        @Override
        public void onAdLoaded(@NonNull AdmizeInterstitialAd interstitialAd, String message) {
            if (delegate.get() != null) {
                delegate.get().onAdLoaded(interstitialAd);
            }
        }

        @Override
        public void onAdFailedToLoad(int statusCode, @NonNull String errorMessage) {
            if (delegate.get() != null) {
                delegate.get().onAdFailedToLoad(errorMessage);
            }
        }

        @Override
        public void onAdOpened() {
            if (delegate.get() != null) {
                delegate.get().onAdOpened();
            }
        }

        @Override
        public void onAdClicked() {
            if (delegate.get() != null) {
                delegate.get().onAdClicked();
            }
        }

        @Override
        public void onAdClosed() {
            if (delegate.get() != null) {
                delegate.get().onAdClosed();
            }
        }
    }
}
