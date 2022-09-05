package io.admize.sdk.flutter.admize_flutter_sdk;

import androidx.annotation.Nullable;

import io.flutter.plugin.platform.PlatformView;

abstract class FlutterAd {

    protected final int adId;

    FlutterAd(int adId) {
        this.adId = adId;
    }

    /**
     * A {@link FlutterAd} that is overlaid on top of a running application.
     */
    abstract static class FlutterOverlayAd extends FlutterAd {
        abstract void show();

        FlutterOverlayAd(int adId) {
            super(adId);
        }
    }

    abstract void load();

    /**
     * Gets the PlatformView for the ad. Default behavior is to return null. Should
     * be overridden by
     * ads with platform views, such as banner and native ads.
     */
    @Nullable
    PlatformView getPlatformView() {
        return null;
    }

    /**
     * Invoked when dispose() is called on the corresponding Flutter ad object. This
     * perform any
     * necessary cleanup.
     */
    abstract void dispose();
}
