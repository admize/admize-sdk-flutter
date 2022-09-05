package io.admize.sdk.flutter.admize_flutter_sdk;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import io.admize.sdk.android.ads.AdmizeAdRequest;
import io.admize.sdk.android.ads.init.AdmizeOnInitializationCompleteListener;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.StandardMethodCodec;

/**
 * AdmizeFlutterSdkPlugin
 */
public class AdmizeFlutterSdkPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {
    private static final String TAG = "AdmizeFlutterSdkPlugin";

    @Nullable
    private FlutterPluginBinding pluginBinding;
    @Nullable
    private AdInstanceManager instanceManager;
    @Nullable
    private AdMessageCodec adMessageCodec;

    /// The MethodChannel that will the communication between Flutter and native
    /// Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine
    /// and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;

    private final FlutterMobileAdsWrapper flutterMobileAds;

    public AdmizeFlutterSdkPlugin() {
        this.flutterMobileAds = new FlutterMobileAdsWrapper();
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.pluginBinding = flutterPluginBinding;
        adMessageCodec = new AdMessageCodec(flutterPluginBinding.getApplicationContext());

        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(),
                "admize_flutter_sdk", new StandardMethodCodec(adMessageCodec));
        channel.setMethodCallHandler(this);
        instanceManager = new AdInstanceManager(channel);

        flutterPluginBinding
                .getPlatformViewRegistry()
                .registerViewFactory("admize_flutter_sdk/ad_widget", new AdmizeAdsViewFactory(instanceManager));
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        if (instanceManager != null) {
            instanceManager.setActivity(binding.getActivity());
        }
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        if (instanceManager != null) {
            instanceManager.setActivity(null);
        }
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        if (instanceManager != null) {
            instanceManager.setActivity(binding.getActivity());
        }
    }

    @Override
    public void onDetachedFromActivity() {
        if (instanceManager != null) {
            instanceManager.setActivity(null);
        }
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (instanceManager == null || pluginBinding == null) {
            Log.e(TAG, "method call received before instanceManager initialized: " + call.method);
            return;
        }
        // Use activity as context if available.
        Context context = (instanceManager.getActivity() != null)
                ? instanceManager.getActivity()
                : pluginBinding.getApplicationContext();

        switch (call.method) {
            case "_init":
                // Internal init. This is necessary to cleanup state on hot restart.
                instanceManager.disposeAllAds();
                result.success(null);
                break;
            case "MobileAds#initialize":
                flutterMobileAds.initialize(context, new FlutterInitializationListener(result));
                break;
            case "loadBannerAd":
                final FlutterBannerAd bannerAd = new FlutterBannerAd(
                        call.<Integer>argument("adId"),
                        context,
                        instanceManager,
                        call.argument("request"),
                        getBannerAdCreator(context));
                instanceManager.trackAd(bannerAd, call.<Integer>argument("adId"));
                bannerAd.load();
                result.success(null);
                break;
            case "loadInterstitialAd":
                final FlutterInterstitialAd interstitial = new FlutterInterstitialAd(
                        call.<Integer>argument("adId"),
                        instanceManager,
                        call.<FlutterAdRequest>argument("request"),
                        new FlutterAdLoader(context));
                instanceManager.trackAd(interstitial, call.<Integer>argument("adId"));
                interstitial.load();
                result.success(null);
                break;
            case "disposeAd":
                instanceManager.disposeAd(call.<Integer>argument("adId"));
                result.success(null);
                break;
            case "showAdWithoutView":
                final boolean adShown = instanceManager.showAdWithId(call.<Integer>argument("adId"));
                if (!adShown) {
                    result.error("AdShowError", "Ad failed to show.", null);
                    break;
                }
                result.success(null);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @VisibleForTesting
    BannerAdCreator getBannerAdCreator(@NonNull Context context) {
        return new BannerAdCreator(context);
    }

    private static final class FlutterInitializationListener
            implements AdmizeOnInitializationCompleteListener {

        private final Result result;
        private boolean isInitializationCompleted;

        private FlutterInitializationListener(@NonNull final Result result) {
            this.result = result;
            isInitializationCompleted = false;
        }

        @Override
        public void onInitializationComplete(int i, String s) {
            // Make sure not to invoke this more than once, since Dart will throw an
            // exception if success
            // is invoked more than once. See b/193418432.
            if (isInitializationCompleted) {
                return;
            }
            result.success(null);
            isInitializationCompleted = true;
        }
    }
}
