package io.admize.sdk.flutter.admize_flutter_sdk;

import android.content.Context;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.admize.sdk.android.ads.AdmizeAdView;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.util.Preconditions;

class FlutterBannerAd extends FlutterAd {

    @NonNull
    private final Context context;
    @NonNull private final RelativeLayout rootView;
    @NonNull
    private final AdInstanceManager manager;
    @NonNull
    private final FlutterAdRequest request;
    @NonNull
    private final BannerAdCreator bannerAdCreator;
    @Nullable
    private AdmizeAdView admizeAdView;

    /**
     * Constructs the FlutterBannerAd.
     */
    public FlutterBannerAd(
            int adId,
            @NonNull Context context,
            @NonNull AdInstanceManager manager,
            @NonNull FlutterAdRequest request,
            @NonNull BannerAdCreator bannerAdCreator) {
        super(adId);
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(manager);
        Preconditions.checkNotNull(request);
        this.context = context;
        this.manager = manager;
        this.request = request;
        this.bannerAdCreator = bannerAdCreator;
        rootView = new RelativeLayout(context);

    }

    @Override
    void load() {
        admizeAdView = bannerAdCreator.createAdView();
        admizeAdView.setAdmizeAdRequest(request.asAdRequest());
        admizeAdView.setAdmizeAdListener(new FlutterBannerAdListener(adId, manager));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rootView.addView(admizeAdView, params);
        admizeAdView.loadAd(context);
    }

    @Nullable
    @Override
    public PlatformView getPlatformView() {
        if (admizeAdView == null) {
            return null;
        }
        return new FlutterPlatformView(rootView);
    }

    @Override
    void dispose() {
        if (admizeAdView != null) {
            admizeAdView.clearView();
            admizeAdView = null;
        }
    }
}
