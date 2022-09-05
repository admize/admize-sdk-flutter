# 목차
1. [Admize 시작하기](#1-Admize-시작하기)
    * [Admize SDK 추가](#admize-sdk-추가)   
    * [AndroidManifest.xml 속성 지정](#androidmanifestxml-속성-지정)
    * [proguard 설정](#proguard-설정-Admize-SDK-포함된-Class는-난독화-시키지-않도록-주의)
    * [AndroidX 설정](#androidx-사용하는-경우)
2. [Android 프로젝트에 클래스 추가하기](#2-Android-프로젝트에-클래스-추가하기)
    * [FlutterPlugin클래스를 구현하는 클래스](#FlutterPlugin-클래스--자세한-내용은-admizesample-참조)
    * [AdInstanceManager 클래스](#AdInstanceManager-클래스--자세한-내용은-admizesample-참조)
    * [AdMessageCodec 클래스](#AdMessageCodec-클래스--자세한-내용은-admizesample-참조)
    * [FlutterMobileAdsWrapper 클래스](#FlutterMobileAdsWrapper-클래스--자세한-내용은-admizesample-참조)
    * [AdmizeAdsViewFactory 클래스](#AdmizeAdsViewFactory-클래스--자세한-내용은-admizesample-참조)
    * [BannerAdCreator 클래스](#BannerAdCreator클래스를-구현하는-클래스--자세한-내용은-admizesample-참조)
    * [FlutterAd 클래스](#FlutterAd-클래스--자세한-내용은-admizesample-참조)
    * [FlutterAdLoader 클래스](#FlutterAdLoader-클래스--자세한-내용은-admizesample-참조)
    * [FlutterAdRequest 클래스](#FlutterAdRequest-클래스--자세한-내용은-admizesample-참조)
    * [FlutterBannerAd 클래스](#FlutterBannerAd-클래스--자세한-내용은-admizesample-참조)
    * [FlutterBannerAdListener 클래스](#FlutterBannerAdListener-클래스--자세한-내용은-admizesample-참조)
    * [FlutterInterstitialAd 클래스](#FlutterInterstitialAd-클래스--자세한-내용은-admizesample-참조)
    * [FlutterPlatformView 클래스](#FlutterPlatformView-클래스--자세한-내용은-admizesample-참조)    
    * [AdmizeAdRequest 설정방법](#AdmizeAdRequest-설정방법)
3. [Flutter 프로젝트에 main.dart 수정하기](#3-Flutter-프로젝트에-main.dart-수정하기)
    * [example/src/main.dart](#example/src/main.dart--자세한-내용은-admizesample-참조)
    * [lib/src/ad_instance_manager.dart](#lib/src/ad_instance_manager.dart--자세한-내용은-admizesample-참조)
    * [lib/src/ad_listeners.dart](#lib/src/ad_listeners.dart--자세한-내용은-admizesample-참조)
    * [lib/src/mobile_ads.dart](#lib/src/mobile_ads.dart--자세한-내용은-admizesample-참조)
    * [lib/admize_mobile_ads.dart](#lib/admize_mobile_ads.dart--자세한-내용은-admizesample-참조)    

- - - 
# 1. Admize 시작하기


### Admize SDK 추가
	
- Flutter 프로젝트 내 안드로이드 프로젝트를 열고, 앱 level build.gradle 에  maven repository 추가

	```clojure
	allprojects {
	    repositories {
        	google()
	        jcenter()
			maven {
				url "s3://repo.admize.io/releases"
				credentials(AwsCredentials) {
					accessKey "AKIA5XG4MQG6UOZLGC4Q"
					secretKey "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"
				}
			}
		}
	    }
	}
	```
	
- 앱 level build.gradle 에 'dependencies'  추가

	```clojure
 	dependencies {
	implementation 'com.google.android.gms:play-services-ads-identifier:17.0.0'
	implementation 'com.google.android.gms:play-services-appset:16.0.0'
    implementation 'io.admize.sdk.android:admize-sdk-android:1.0.0'
    }
	```



### AndroidManifest.xml 속성 지정

#### 필수 퍼미션 추가
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="com.google.android.gms.permission.AD_ID"/>
```

#### 네트워크 보안 설정 (targetSdkVersion 28 이상)

`광고 노출 및 클릭이 정상적으로 동작하기 위해서 cleartext 네트워크 설정 필요`

```xml
<application android:usesCleartextTraffic="true" />
```	


#### Activity orientation
- Activity 형식의 전체 화면 랜딩을 지원하기 위해선 아래의 설정으로 추가한다.
- 만약, 설정하지 않으면 화면 전환 시 마다 광고view 가 `초기화` 됩니다.

```xml
<activity
    android:name=".MainActivity"
    android:configChanges="keyboard|keyboardHidden|orientation|screenSize">
</activity>
```

#### Media Uid 설정
- APP 등록 후 부여 받은 media uid를 아래의 설정으로 추가한다.
- 만약, 설정하지 않으면 광고가 표시가 되지 않습니다. AndroidManifest.xml에서 아래와 같이 설정을 하거나, AdmizeAdRequest의 mediaUid() 둘 중 한 곳에 media uid가 반드시 선언되어야 합니다.
```xml
<meta-data
	android:name="io.admize.sdk.android.ads.MEDIA_UID"
	android:value="abc" />
```


### proguard 설정 Admize SDK 포함된 Class는 난독화 시키지 않도록 주의

```clojure
proguard-rules.pro ::
-keep class io.admize.sdk.android.cores.http.admize.response.** {*;}
-keep interface io.admize.sdk.android.cores.http.admize.response.** {*;}
-keep class io.admize.sdk.android.cores.http.admize.request.** {*;}
-keep interface io.admize.sdk.android.cores.http.admize.request.** {*;}

-keep class io.admize.sdk.android.ads.ADMIZE_AD_SIZE {*;}
-keep class io.admize.sdk.android.ads.ADMIZE_AD_TYPE {*;}
-keep class io.admize.sdk.android.ads.AdmizeLog$LogLevel{*;}
-keep class io.admize.sdk.android.ads.AdmizeCustomSize {*;}


-keepclasseswithmembers class io.admize.sdk.android.ads.AdmizeAds {
  public static *** initialize(***);
  public static *** initialize(***, ***);
}

-keepclasseswithmembers class io.admize.sdk.android.ads.AdmizeAdView {
    public <init>(...);
    public *** loadAd(***);
    public *** loadAd(***, ***);
    public *** setAdmizeAdListener(***);
    public *** clearView();
}

-keepclasseswithmembers class io.admize.sdk.android.ads.AdmizeLog {
    public static *** setLogLevel(***);
    public static *** getLogLevel();
    public static *** d(***);
    public static *** i(***);
    public static *** w(***);
    public static *** e(java.lang.String);
    public static *** e(java.lang.Exception);
    public static *** e(java.lang.String, java.lang.Exception);
    public static *** e(java.lang.String, java.lang.Throwable);
}

-keepclasseswithmembers class io.admize.sdk.android.ads.AdmizeAdRequest$Builder {
    public <init>(...);
    public *** admizeAdType(***);
    public *** publisherUid(***);
    public *** placementUid(***);
    public *** mediaUid(***);
    public *** admizeMultiBidsList(***);
    public *** setTest(***);
    public *** coppaEnabled(***);
    public *** build();
}

-keepclasseswithmembers class io.admize.sdk.android.ads.AdmizeInterstitialAd {
    public static *** loadAd(***, ***, ***);
    public *** show(***);
    public *** setAdmizeInterstitialAdListener(***);
}

-keep interface io.admize.sdk.android.ads.IAdmizeListener {*;}                                                        
-keep interface io.admize.sdk.android.ads.AdmizeInterstitialAdListener {*;}
-keep interface io.admize.sdk.android.ads.AdmizeAdListener {*;}
-keep interface io.admize.sdk.android.ads.init.AdmizeOnInitializationCompleteListener {*;}
```
	
### AndroidX 사용하는 경우
```xml
gradle.properties ::
 * android.useAndroidX=true
 * android.enableJetifier=true
```

- 참고 : https://developer.android.com/jetpack/androidx/migrate

# 2. Android 프로젝트에 클래스 추가하기

Admize Android SDK와 통신을 위한 필요 클래스들을 추가

#### FlutterPlugin클래스를 구현하는 클래스 : 자세한 내용은 ‘AdmizeSample’ 참조
``` java

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
```

#### AdInstanceManager 클래스 : 자세한 내용은 ‘AdmizeSample’ 참조
``` java

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

class AdInstanceManager {
    @Nullable
    private Activity activity;

    @NonNull
    private final Map<Integer, FlutterAd> ads;
    @NonNull
    private final MethodChannel channel;

    /**
     * Initializes the ad instance manager. We only need a method channel to start
     * loading ads, but an
     * activity must be present in order to attach any ads to the view hierarchy.
     */
    AdInstanceManager(@NonNull MethodChannel channel) {
        this.channel = channel;
        this.ads = new HashMap<>();
    }

    void setActivity(@Nullable Activity activity) {
        this.activity = activity;
    }

    @Nullable
    Activity getActivity() {
        return activity;
    }

    @Nullable
    FlutterAd adForId(int id) {
        return ads.get(id);
    }

    @Nullable
    Integer adIdFor(@NonNull FlutterAd ad) {
        for (Integer adId : ads.keySet()) {
            if (ads.get(adId) == ad) {
                return adId;
            }
        }
        return null;
    }

    void trackAd(@NonNull FlutterAd ad, int adId) {
        if (ads.get(adId) != null) {
            throw new IllegalArgumentException(
                    String.format("Ad for following adId already exists: %d", adId));
        }
        ads.put(adId, ad);
    }

    void disposeAd(int adId) {
        if (!ads.containsKey(adId)) {
            return;
        }
        FlutterAd ad = ads.get(adId);
        if (ad != null) {
            ad.dispose();
        }
        ads.remove(adId);
    }

    void disposeAllAds() {
        for (Map.Entry<Integer, FlutterAd> entry : ads.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().dispose();
            }
        }
        ads.clear();
    }

    void onAdLoaded(int adId) {
        Map<Object, Object> arguments = new HashMap<>();
        arguments.put("adId", adId);
        arguments.put("eventName", "onAdLoaded");
        invokeOnAdEvent(arguments);
    }

    void onAdFailedToLoad(int adId, String errorMessage) {
        Map<Object, Object> arguments = new HashMap<>();
        arguments.put("adId", adId);
        arguments.put("eventName", "onAdFailedToLoad");
        arguments.put("errorMessage", errorMessage);
        invokeOnAdEvent(arguments);
    }

    void onAdClicked(int id) {
        Map<Object, Object> arguments = new HashMap<>();
        arguments.put("adId", id);
        arguments.put("eventName", "onAdClicked");
        invokeOnAdEvent(arguments);
    }

    void onAdOpened(int adId) {
        Map<Object, Object> arguments = new HashMap<>();
        arguments.put("adId", adId);
        arguments.put("eventName", "onAdOpened");
        invokeOnAdEvent(arguments);
    }

    void onAdClosed(int adId) {
        Map<Object, Object> arguments = new HashMap<>();
        arguments.put("adId", adId);
        arguments.put("eventName", "onAdClosed");
        invokeOnAdEvent(arguments);
    }

    boolean showAdWithId(int id) {
        final FlutterAd.FlutterOverlayAd ad = (FlutterAd.FlutterOverlayAd) adForId(id);

        if (ad == null) {
            return false;
        }

        ad.show();
        return true;
    }

    /**
     * Invoke the method channel using the UI thread. Otherwise the message gets
     * silently dropped.
     */
    private void invokeOnAdEvent(final Map<Object, Object> arguments) {
        new Handler(Looper.getMainLooper())
                .post(
                        new Runnable() {
                            @Override
                            public void run() {
                                channel.invokeMethod("onAdEvent", arguments);
                            }
                        });
    }
}
```

#### AdMessageCodec 클래스 : 자세한 내용은 ‘AdmizeSample’ 참조
``` java

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import io.flutter.plugin.common.StandardMessageCodec;

/**
 * Encodes and decodes values by reading from a ByteBuffer and writing to a ByteArrayOutputStream.
 */
class AdMessageCodec extends StandardMessageCodec {
    // The type values below must be consistent for each platform.
    private static final byte VALUE_AD_REQUEST = (byte) 129;

    @NonNull
    Context context;

    AdMessageCodec(@NonNull Context context) {
        this.context = context;
    }

    void setContext(@NonNull Context context) {
        this.context = context;
    }

    @Override
    protected void writeValue(ByteArrayOutputStream stream, Object value) {
        if (value instanceof FlutterAdRequest) {
            stream.write(VALUE_AD_REQUEST);
            final FlutterAdRequest request = (FlutterAdRequest) value;
            writeValue(stream, request.getAdmizeAdType());
            writeValue(stream, request.getPublisherUid());
            writeValue(stream, request.getPlacementUid());
            writeValue(stream, request.getMediaUid());
            writeValue(stream, request.getAdmizeMultiBidsList());
            writeValue(stream, request.getCoppaEnabled());
            writeValue(stream, request.getSetTest());
        } else {
            super.writeValue(stream, value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object readValueOfType(byte type, ByteBuffer buffer) {
        switch (type) {
            case VALUE_AD_REQUEST:
                return new FlutterAdRequest.Builder((String) readValueOfType(buffer.get(), buffer),
                        (String) readValueOfType(buffer.get(), buffer),
                        (String) readValueOfType(buffer.get(), buffer),
                        (String) readValueOfType(buffer.get(), buffer))
                        .setAdmizeMultiBidsList((List<String>) readValueOfType(buffer.get(), buffer))
                        .setCoppaEnabled(booleanValueOf(readValueOfType(buffer.get(), buffer)))
                        .setSetTest(booleanValueOf(readValueOfType(buffer.get(), buffer)))
                        .build();
            default:
                return super.readValueOfType(type, buffer);
        }
    }

    @Nullable
    private static Boolean booleanValueOf(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        return (Boolean) object;
    }
}
```

#### FlutterMobileAdsWrapper 클래스 : 자세한 내용은 ‘AdmizeSample’ 참조
``` java

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
```

#### AdmizeAdsViewFactory 클래스 : 자세한 내용은 ‘AdmizeSample’ 참조
``` java

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class AdmizeAdsViewFactory extends PlatformViewFactory {
    @NonNull
    private final AdInstanceManager manager;

    private static class ErrorTextView implements PlatformView {
        private final TextView textView;

        private ErrorTextView(Context context, String message) {
            textView = new TextView(context);
            textView.setText(message);
            textView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.YELLOW);
        }

        @Override
        public View getView() {
            return textView;
        }

        @Override
        public void dispose() {
        }
    }

    AdmizeAdsViewFactory(@NonNull AdInstanceManager manager) {
        super(StandardMessageCodec.INSTANCE);
        this.manager = manager;
    }

    @NonNull
    @Override
    public PlatformView create(@NonNull Context context, int id, @Nullable Object args) {
        final Integer adId = (Integer) args;
        FlutterAd ad = manager.adForId(adId);
        if (ad == null || ad.getPlatformView() == null) {
            return getErrorView(context, adId);
        }
        return ad.getPlatformView();
    }

    /**
     * Returns an ErrorView with a debug message for debug builds only. Otherwise just returns an
     * empty PlatformView.
     */
    private static PlatformView getErrorView(@NonNull final Context context, int adId) {
        final String message =
                String.format(
                        Locale.getDefault(),
                        "This ad may have not been loaded or has been disposed. "
                                + "Ad with the following id could not be found: %d.",
                        adId);

        if (BuildConfig.DEBUG) {
            return new ErrorTextView(context, message);
        } else {
            Log.e(AdmizeAdsViewFactory.class.getSimpleName(), message);
            return new PlatformView() {
                @Override
                public View getView() {
                    return new View(context);
                }

                @Override
                public void dispose() {
                    // Do nothing.
                }
            };
        }
    }
}
```

#### BannerAdCreator 클래스 : 자세한 내용은 ‘AdmizeSample’ 참조
``` java

import android.content.Context;
import androidx.annotation.NonNull;

import io.admize.sdk.android.ads.AdmizeAdView;

/** Creates AdmizeAdView for banner ads. */
public class BannerAdCreator {

  @NonNull
  private final Context context;

  public BannerAdCreator(@NonNull Context context) {
    this.context = context;
  }

  public AdmizeAdView createAdView() {
    return new AdmizeAdView(context);
  }
}
```

#### FlutterAd 클래스 : 자세한 내용은 ‘AdmizeSample’ 참조
``` java

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
```

#### FlutterAdLoader 클래스 : 자세한 내용은 ‘AdmizeSample’ 참조
``` java

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
```

#### FlutterAdRequest 클래스 : 자세한 내용은 ‘AdmizeSample’ 참조
``` java

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.admize.sdk.android.ads.ADMIZE_AD_SIZE;
import io.admize.sdk.android.ads.ADMIZE_AD_TYPE;
import io.admize.sdk.android.ads.AdmizeAdRequest;

class FlutterAdRequest {
    @Nullable
    private final String admizeAdType;
    @Nullable
    private final String publisherUid;
    @Nullable
    private final String placementUid;
    @Nullable
    private final String mediaUid;
    @Nullable
    private final List<String> admizeMultiBidsList;
    @Nullable
    private final Boolean coppaEnabled;
    @Nullable
    private final Boolean setTest;

    //TODO 추후 없어질 옵션임


    protected static class Builder {
        private final String admizeAdType;

        private final String publisherUid;

        private final String placementUid;

        private final String mediaUid;
        @Nullable
        private List<String> admizeMultiBidsList;
        @Nullable
        private Boolean coppaEnabled;
        @Nullable
        private Boolean setTest;

        //TODO 추후 없어질 옵션임


        public Builder(String admizeAdType,
                       String publisherUid,
                       String placementUid,
                       String mediaUid) {
            this.admizeAdType = admizeAdType;
            this.publisherUid = publisherUid;
            this.placementUid = placementUid;
            this.mediaUid = mediaUid;
        }

        public FlutterAdRequest.Builder setAdmizeMultiBidsList(@Nullable List<String> admizeMultiBidsList) {
            this.admizeMultiBidsList = admizeMultiBidsList;
            return this;
        }

        public FlutterAdRequest.Builder setCoppaEnabled(@Nullable Boolean coppaEnabled) {
            this.coppaEnabled = coppaEnabled;
            return this;
        }

        public FlutterAdRequest.Builder setSetTest(@Nullable Boolean setTest) {
            this.setTest = setTest;
            return this;
        }

        @Nullable
        public String getAdmizeAdType() {
            return admizeAdType;
        }

        @Nullable
        public String getPublisherUid() {
            return publisherUid;
        }

        @Nullable
        public String getPlacementUid() {
            return placementUid;
        }

        @Nullable
        public String getMediaUid() {
            return mediaUid;
        }

        @Nullable
        public List<String> getAdmizeMultiBidsList() {
            return admizeMultiBidsList;
        }

        @Nullable
        public Boolean getCoppaEnabled() {
            return coppaEnabled;
        }

        @Nullable
        public Boolean getSetTest() {
            return setTest;
        }

        FlutterAdRequest build() {
            return new FlutterAdRequest(
                    admizeAdType,
                    publisherUid,
                    placementUid,
                    mediaUid,
                    admizeMultiBidsList,
                    coppaEnabled,
                    setTest
            );
        }
    }

    protected FlutterAdRequest(
            String admizeAdType,
            String publisherUid,
            String placementUid,
            String mediaUid,
            @Nullable List<String> admizeMultiBidsList,
            @Nullable Boolean coppaEnabled,
            @Nullable Boolean setTest) {
        this.admizeAdType = admizeAdType;
        this.publisherUid = publisherUid;
        this.placementUid = placementUid;
        this.mediaUid = mediaUid;
        this.admizeMultiBidsList = admizeMultiBidsList;
        this.coppaEnabled = coppaEnabled;
        this.setTest = setTest;
    }

    protected AdmizeAdRequest.Builder updateAdRequestBuilder(AdmizeAdRequest.Builder builder) {
        builder.admizeAdType(ADMIZE_AD_TYPE.valueOf(admizeAdType.toUpperCase()));
        builder.publisherUid(publisherUid);
        builder.placementUid(placementUid);
        builder.mediaUid(mediaUid);

        if (admizeMultiBidsList != null) {
            List<ADMIZE_AD_SIZE> sizeList = admizeMultiBidsList.stream()
                    .map(sizeString -> ADMIZE_AD_SIZE.valueOf(sizeString.toUpperCase()))
                    .collect(Collectors.toList());
            builder.admizeMultiBidsList(sizeList);
        }

        if (coppaEnabled != null) {
            builder.coppaEnabled(coppaEnabled);
        }
        if (setTest != null) {
            builder.setTest(setTest);
        }
        return builder;
    }

    AdmizeAdRequest asAdRequest() {
        return updateAdRequestBuilder(new AdmizeAdRequest.Builder()).build();
    }

    @Nullable
    protected String getAdmizeAdType() {
        return admizeAdType;
    }

    @Nullable
    protected String getPublisherUid() {
        return publisherUid;
    }

    @Nullable
    protected String getPlacementUid() {
        return placementUid;
    }

    @Nullable
    protected String getMediaUid() {
        return mediaUid;
    }

    @Nullable
    protected List<String> getAdmizeMultiBidsList() {
        return admizeMultiBidsList;
    }

    @Nullable
    protected Boolean getCoppaEnabled() {
        return coppaEnabled;
    }

    @Nullable
    protected Boolean getSetTest() {
        return setTest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlutterAdRequest)) return false;
        FlutterAdRequest that = (FlutterAdRequest) o;
        return admizeAdType.equals(that.admizeAdType)
                && publisherUid.equals(that.publisherUid)
                && placementUid.equals(that.placementUid)
                && mediaUid.equals(that.mediaUid)
                && Objects.equals(admizeMultiBidsList, that.admizeMultiBidsList)
                && Objects.equals(coppaEnabled, that.coppaEnabled)
                && Objects.equals(setTest, that.setTest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(admizeAdType,
                publisherUid,
                placementUid,
                mediaUid,
                admizeMultiBidsList,
                coppaEnabled,
                setTest
        );
    }
}
```

#### FlutterBannerAd 클래스 : 자세한 내용은 ‘AdmizeSample’ 참조
``` java

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
```

#### FlutterBannerAdListener 클래스 : 자세한 내용은 ‘AdmizeSample’ 참조
``` java

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
```

#### FlutterInterstitialAd 클래스 : 자세한 내용은 ‘AdmizeSample’ 참조
``` java

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
```

#### FlutterPlatformView 클래스 : 자세한 내용은 ‘AdmizeSample’ 참조
``` java

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.plugin.platform.PlatformView;

public class FlutterPlatformView implements PlatformView {
    @Nullable private View view;

    FlutterPlatformView(@NonNull View view) {
        this.view = view;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void dispose() {
        this.view = null;
    }
}
```

#### 주의사항  

- Lifecycle에 따라 AdmizeAdView의 pause/resume/destroy API를 호출하지 않을 경우, 광고 수신에 불이익을 받을 수 있습니다.

### `AdmizeAdRequest 설정방법`

Adinfo|설 명
---|---
admizeAdType()|광고 종류를 설정합니다. 필수 값이며 "BANNER", "INTERSTITIAL"을 설정할 수 있습니다.
publisherUid()|APP 등록 후 APP 소유자에게 발급되는 고유 ID입니다. 필수 값이며 발급 관련한 자세한 내용은 <ops_admize@fsn.co.kr>로 문의바랍니다.
placementUid()|지면 ID로 게재할 광고의 위치에 부여되는 고유 ID입니다. 필수 값이며 발급 관련한 자세한 내용은 <ops_admize@fsn.co.kr>로 문의바랍니다.
mediaUid()|APP 등록 후 부여 받은 media uid 입력합니다. 필수 값이며 만약, 설정하지 않으면 광고가 표시가 되지 않습니다. AndroidManifest.xml에서 아래와 같이 설정을 하거나, AdmizeAdRequest의 mediaUid() 둘 중 한 곳에 media uid가 반드시 선언되어야 합니다.
admizeMultiBidsList()|지원하는 배너 사이즈입니다. admizeAdType을 "BANNER"로 지정한 경우 필수 값이며 "BANNER320x50", "BANNER320x100", "BANNER300x250"을 지원합니다.
setTest()|테스트 모드를 지원합니다. 옵션값이며 true일 경우 테스트 광고가 보여지고, false일 경우 실제 광고가 보여집니다.테스트 광고와 관련한 자세한 내용은 <ops_admize@fsn.co.kr>로 문의바랍니다.

# 3. Flutter 프로젝트에 main.dart 수정하기

#### example/src/main.dart : 자세한 내용은 ‘AdmizeSample’ 참조

```dart
import 'dart:io';

import 'package:admize_flutter_sdk/admize_mobile_ads.dart';
import 'package:flutter/material.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  MobileAds.instance.initialize();

  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  BannerAd? _banner;
  InterstitialAd? _interstitialAd;

  @override
  void initState() {
    super.initState();
    _createBanner();
    Future.delayed(
        const Duration(milliseconds: 1500), () => _createInterstitialAd());
  }

  void _createBanner() {
    _banner = BannerAd(
        listener: BannerAdListener(
          onAdClicked: (ad) {
            debugPrint('BannerAdListener onAdClicked!!!');
          },
          onAdClosed: (ad) {
            debugPrint('BannerAdListener onAdClosed!!!');
          },
          onAdFailedToLoad: (ad, errorMessage) {
            debugPrint('BannerAdListener onAdFailedToLoad : $errorMessage');
          },
          onAdLoaded: (ad) {
            debugPrint('BannerAdListener onAdLoaded!!!');
          },
          onAdOpened: (ad) {
            debugPrint('BannerAdListener onAdOpened!!!');
          },
        ),
        request: const AdRequest(
            admizeAdType: AdType.banner,
            publisherUid: '666fe91f-4a46-4f9a-95b4-a8255603da69',
            placementUid: 'sdk_test_tagid_banner',
            mediaUid: '7cbabbd272f782ce3dbfb8ab5705cd79caf7dc93',
            admizeMultiBidsList: [
              AdSize.smallBanner,
              // AdSize.mediumRectangleBanner,
              // AdSize.largeBanner,
            ],
            coppaEnabled: false,
            setTest: true))
      ..load();
  }

  void _createInterstitialAd() {
    InterstitialAd.load(
        request: const AdRequest(
            admizeAdType: AdType.interstitial,
            publisherUid: '666fe91f-4a46-4f9a-95b4-a8255603da69',
            placementUid: 'sdk_test_tagid',
            mediaUid: '7cbabbd272f782ce3dbfb8ab5705cd79caf7dc93',
            coppaEnabled: false,
            setTest: true),
        adLoadCallback: InterstitialAdLoadCallback(
          onAdLoaded: (ad) {
            _interstitialAd = ad;
            debugPrint('InterstitialAd onAdLoaded!!!');
          },
          onAdFailedToLoad: ((errorMessage) {
            debugPrint('InterstitialAd onAdFailedToLoad: $errorMessage');
            _interstitialAd = null;
          }),
          onAdOpened: (ad) {
            debugPrint('InterstitialAd onAdOpened');
          },
          onAdClicked: (ad) {
            debugPrint('InterstitialAd onAdClicked');
          },
          onAdClosed: (ad) {
            debugPrint('InterstitialAd onAdClosed');
            if (Platform.isAndroid) {
              exit(0);
            }
          },
        ));
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Admize Plugin example app'),
        ),
        body: Column(
          children: [
            Expanded(
              child: Container(
                decoration: const BoxDecoration(color: Colors.amber),
                child: Center(
                    child: ElevatedButton(
                        onPressed: () => _showInterstitialAd(),
                        child: const Text('show interstitial ad'))),
              ),
            ),
            _banner == null
                ? Container()
                : SizedBox(
                    height: _banner!.size.height.toDouble(),
                    child: AdWidget(ad: _banner!),
                  )
          ],
        ),
      ),
    );
  }

  _showInterstitialAd() {
    if (_interstitialAd != null) {
      _interstitialAd!.show();
      _interstitialAd = null;
      _createInterstitialAd();
    }
  }
}
```

#### lib/src/ad_instance_manager.dart : 자세한 내용은 ‘AdmizeSample’ 참조

```dart

import 'dart:async';
import 'dart:collection';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'ad_containers.dart';

/// Loads and disposes [BannerAds] and [InterstitialAds].
AdInstanceManager instanceManager = AdInstanceManager(
  'admize_flutter_sdk',
);

/// Maintains access to loaded [Ad] instances and handles sending/receiving
/// messages to platform code.
class AdInstanceManager {
  AdInstanceManager(String channelName)
      : channel = MethodChannel(
          channelName,
          StandardMethodCodec(AdMessageCodec()),
        ) {
    channel.setMethodCallHandler((MethodCall call) async {
      assert(call.method == 'onAdEvent');

      final int adId = call.arguments['adId'];
      final String eventName = call.arguments['eventName'];

      final Ad? ad = adFor(adId);
      if (ad != null) {
        _onAdEvent(ad, eventName, call.arguments);
      } else {
        debugPrint('$Ad with id `$adId` is not available for $eventName.');
      }
    });
  }

  int _nextAdId = 0;
  final _BiMap<int, Ad> _loadedAds = _BiMap<int, Ad>();

  /// Invokes load and dispose calls.
  final MethodChannel channel;

  void _onAdEvent(Ad ad, String eventName, Map<dynamic, dynamic> arguments) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      _onAdEventAndroid(ad, eventName, arguments);
    } else {
      throw Exception('is not supported on $defaultTargetPlatform');
    }
  }

  void _onAdEventAndroid(
      Ad ad, String eventName, Map<dynamic, dynamic> arguments) {
    switch (eventName) {
      case 'onAdLoaded':
        _invokeOnAdLoaded(ad, eventName, arguments);
        break;
      case 'onAdFailedToLoad':
        _invokeOnAdFailedToLoad(ad, eventName, arguments);
        break;
      case 'onAdOpened':
        _invokeOnAdOpened(ad, eventName);
        break;
      case 'onAdClosed':
        _invokeOnAdClosed(ad, eventName);
        break;
      case 'onAdClicked':
        _invokeOnAdClicked(ad, eventName);
        break;
      default:
        debugPrint('invalid ad event name: $eventName');
    }
  }

  void _invokeOnAdLoaded(
      Ad ad, String eventName, Map<dynamic, dynamic> arguments) {
    if (ad is AdWithView) {
      ad.listener.onAdLoaded?.call(ad);
    } else if (ad is InterstitialAd) {
      ad.adLoadCallback.onAdLoaded.call(ad);
    } else {
      debugPrint('invalid ad: $ad, for event name: $eventName');
    }
  }

  void _invokeOnAdFailedToLoad(
      Ad ad, String eventName, Map<dynamic, dynamic> arguments) {
    if (ad is AdWithView) {
      ad.listener.onAdFailedToLoad?.call(ad, arguments['errorMessage']);
    } else if (ad is InterstitialAd) {
      ad.dispose();
      ad.adLoadCallback.onAdFailedToLoad.call(arguments['errorMessage']);
    } else {
      debugPrint('invalid ad: $ad, for event name: $eventName');
    }
  }

  void _invokeOnAdOpened(Ad ad, String eventName) {
    if (ad is AdWithView) {
      ad.listener.onAdOpened?.call(ad);
    } else if (ad is InterstitialAd) {
      ad.adLoadCallback.onAdOpened?.call(ad);
    } else {
      debugPrint('invalid ad: $ad, for event name: $eventName');
    }
  }

  void _invokeOnAdClosed(Ad ad, String eventName) {
    if (ad is AdWithView) {
      ad.listener.onAdClosed?.call(ad);
    } else if (ad is InterstitialAd) {
      ad.adLoadCallback.onAdClosed?.call(ad);
    } else {
      debugPrint('invalid ad: $ad, for event name: $eventName');
    }
  }

  void _invokeOnAdClicked(Ad ad, String eventName) {
    if (ad is AdWithView) {
      ad.listener.onAdClicked?.call(ad);
    } else if (ad is InterstitialAd) {
      ad.adLoadCallback.onAdClicked?.call(ad);
    } else {
      debugPrint('invalid ad: $ad, for event name: $eventName');
    }
  }

  Future<void> initialize() async {
    return (await instanceManager.channel.invokeMethod<void>(
      'MobileAds#initialize',
    ));
  }

  /// Returns null if an invalid [adId] was passed in.
  Ad? adFor(int adId) => _loadedAds[adId];

  /// Returns null if an invalid [Ad] was passed in.
  int? adIdFor(Ad ad) => _loadedAds.inverse[ad];

  final Set<int> _mountedWidgetAdIds = <int>{};

  /// Returns true if the [adId] is already mounted in a [WidgetAd].
  bool isWidgetAdIdMounted(int adId) => _mountedWidgetAdIds.contains(adId);

  /// Indicates that [adId] is mounted in widget tree.
  void mountWidgetAdId(int adId) => _mountedWidgetAdIds.add(adId);

  /// Indicates that [adId] is unmounted from the widget tree.
  void unmountWidgetAdId(int adId) => _mountedWidgetAdIds.remove(adId);

  /// Starts loading the ad if not previously loaded.
  ///
  /// Does nothing if we have already tried to load the ad.
  Future<void> loadBannerAd(BannerAd ad) {
    if (adIdFor(ad) != null) {
      return Future<void>.value();
    }

    final int adId = _nextAdId++;
    _loadedAds[adId] = ad;
    return channel.invokeMethod<void>(
      'loadBannerAd',
      <dynamic, dynamic>{
        'adId': adId,
        'request': ad.request,
      },
    );
  }

  Future<void> loadInterstitialAd(InterstitialAd ad) {
    if (adIdFor(ad) != null) {
      return Future<void>.value();
    }

    final int adId = _nextAdId++;
    _loadedAds[adId] = ad;
    return channel.invokeMethod<void>(
      'loadInterstitialAd',
      <dynamic, dynamic>{
        'adId': adId,
        'request': ad.request,
      },
    );
  }

  /// Free the plugin resources associated with this ad.
  ///
  /// Disposing a banner ad that's been shown removes it from the screen.
  /// Interstitial ads can't be programmatically removed from view.
  Future<void> disposeAd(Ad ad) {
    final int? adId = adIdFor(ad);
    final Ad? disposedAd = _loadedAds.remove(adId);
    if (disposedAd == null) {
      return Future<void>.value();
    }
    return channel.invokeMethod<void>(
      'disposeAd',
      <dynamic, dynamic>{
        'adId': adId,
      },
    );
  }

  /// Display an [AdWithoutView] that is overlaid on top of the application.
  Future<void> showAdWithoutView(AdWithoutView ad) {
    assert(
      adIdFor(ad) != null,
      '$Ad has not been loaded or has already been disposed.',
    );

    return channel.invokeMethod<void>(
      'showAdWithoutView',
      <dynamic, dynamic>{
        'adId': adIdFor(ad),
      },
    );
  }
}

@visibleForTesting
class AdMessageCodec extends StandardMessageCodec {
  // The type values below must be consistent for each platform.
  static const int _valueAdRequest = 129;

  @override
  void writeValue(WriteBuffer buffer, dynamic value) {
    if (value is AdRequest) {
      buffer.putUint8(_valueAdRequest);
      writeValue(buffer, value.admizeAdType.name);
      writeValue(buffer, value.publisherUid);
      writeValue(buffer, value.placementUid);
      writeValue(buffer, value.mediaUid);
      writeValue(
          buffer, value.admizeMultiBidsList?.map((e) => e.name4Java).toList());
      writeValue(buffer, value.coppaEnabled);
      writeValue(buffer, value.setTest);
    } else {
      super.writeValue(buffer, value);
    }
  }

  @override
  dynamic readValueOfType(dynamic type, ReadBuffer buffer) {
    switch (type) {
      case _valueAdRequest:
        return AdRequest(
          admizeAdType: AdType.values.byName(
              readValueOfType(buffer.getUint8(), buffer).cast<String>()),
          publisherUid:
              readValueOfType(buffer.getUint8(), buffer).cast<String>(),
          placementUid:
              readValueOfType(buffer.getUint8(), buffer).cast<String>(),
          mediaUid: readValueOfType(buffer.getUint8(), buffer).cast<String>(),
          admizeMultiBidsList: ((readValueOfType(buffer.getUint8(), buffer)
                  .cast<String>()) as List<String>)
              .map((e) => AdSize.values.byName(e))
              .toList(),
          coppaEnabled: readValueOfType(buffer.getUint8(), buffer),
          setTest: readValueOfType(buffer.getUint8(), buffer),
        );
      default:
        return super.readValueOfType(type, buffer);
    }
  }
}

class _BiMap<K extends Object, V extends Object> extends MapBase<K, V> {
  _BiMap() {
    _inverse = _BiMap<V, K>._inverse(this);
  }

  _BiMap._inverse(this._inverse);

  final Map<K, V> _map = <K, V>{};
  late _BiMap<V, K> _inverse;

  _BiMap<V, K> get inverse => _inverse;

  @override
  V? operator [](Object? key) => _map[key];

  @override
  void operator []=(K key, V value) {
    assert(!_map.containsKey(key));
    assert(!inverse.containsKey(value));
    _map[key] = value;
    inverse._map[value] = key;
  }

  @override
  void clear() {
    _map.clear();
    inverse._map.clear();
  }

  @override
  Iterable<K> get keys => _map.keys;

  @override
  V? remove(Object? key) {
    if (key == null) return null;
    final V? value = _map[key];
    inverse._map.remove(value);
    return _map.remove(key);
  }
}
```

#### lib/src/ad_listeners.dart : 자세한 내용은 ‘AdmizeSample’ 참조

```dart

import 'package:meta/meta.dart';

import 'ad_containers.dart';

/// The callback type to handle an event occurring for an [Ad].
typedef AdEventCallback = void Function(Ad ad);

/// Generic callback type for an event occurring on an Ad.
typedef GenericAdEventCallback<Ad> = void Function(Ad ad);

/// A callback type for when an error occurs loading a full screen ad.
typedef FullScreenAdLoadErrorCallback = void Function(String errorMessage);

/// The callback type to handle an error loading an [Ad].
typedef AdLoadErrorCallback = void Function(Ad ad, String errorMessage);

/// Listener for app events.
class AppEventListener {
  /// Called when an app event is received.
  void Function(Ad ad, String name, String data)? onAppEvent;
}

/// Shared event callbacks used in Native and Banner ads.
abstract class AdWithViewListener {
  /// Default constructor for [AdWithViewListener], meant to be used by subclasses.
  @protected
  const AdWithViewListener({
    this.onAdLoaded,
    this.onAdFailedToLoad,
    this.onAdOpened,
    this.onAdClosed,
    this.onAdClicked,
  });

  /// Called when an ad is successfully received.
  final AdEventCallback? onAdLoaded;

  /// Called when an ad request failed.
  final AdLoadErrorCallback? onAdFailedToLoad;

  /// A full screen view/overlay is presented in response to the user clicking
  /// on an ad. You may want to pause animations and time sensitive
  /// interactions.
  final AdEventCallback? onAdOpened;

  /// Called when the full screen view has been closed. You should restart
  /// anything paused while handling onAdOpened.
  final AdEventCallback? onAdClosed;

  /// Called when the ad is clicked.
  final AdEventCallback? onAdClicked;
}

/// A listener for receiving notifications for the lifecycle of a [BannerAd].
class BannerAdListener extends AdWithViewListener {
  /// Constructs a [BannerAdListener] that notifies for the provided event callbacks.
  ///
  /// Typically you will override [onAdLoaded] and [onAdFailedToLoad]:
  /// ```dart
  /// BannerAdListener(
  ///   onAdLoaded: (ad) {
  ///     // Ad successfully loaded - display an AdWidget with the banner ad.
  ///   },
  ///   onAdFailedToLoad: (ad, error) {
  ///     // Ad failed to load - log the error and dispose the ad.
  ///   },
  ///   ...
  /// )
  /// ```
  const BannerAdListener({
    AdEventCallback? onAdLoaded,
    AdLoadErrorCallback? onAdFailedToLoad,
    AdEventCallback? onAdOpened,
    AdEventCallback? onAdClosed,
    AdEventCallback? onAdClicked,
  }) : super(
          onAdLoaded: onAdLoaded,
          onAdFailedToLoad: onAdFailedToLoad,
          onAdOpened: onAdOpened,
          onAdClosed: onAdClosed,
          onAdClicked: onAdClicked,
        );
}

/// Generic parent class for ad load callbacks.
abstract class FullScreenAdLoadCallback<T> {
  /// Default constructor for [FullScreenAdLoadCallback[, used by subclasses.
  const FullScreenAdLoadCallback({
    required this.onAdLoaded,
    required this.onAdFailedToLoad,
    this.onAdOpened,
    this.onAdClosed,
    this.onAdClicked,
  });

  /// Called when the ad successfully loads.
  final GenericAdEventCallback<T> onAdLoaded;

  /// Called when an error occurs loading the ad.
  final FullScreenAdLoadErrorCallback onAdFailedToLoad;

  /// A full screen view/overlay is presented in response to the user clicking
  /// on an ad. You may want to pause animations and time sensitive
  /// interactions.
  final AdEventCallback? onAdOpened;

  /// Called when the full screen view has been closed. You should restart
  /// anything paused while handling onAdOpened.
  final AdEventCallback? onAdClosed;

  /// Called when the ad is clicked.
  final AdEventCallback? onAdClicked;
}

/// This class holds callbacks for loading an [InterstitialAd].
class InterstitialAdLoadCallback
    extends FullScreenAdLoadCallback<InterstitialAd> {
  /// Construct a [InterstitialAdLoadCallback].
  const InterstitialAdLoadCallback({
    required GenericAdEventCallback<InterstitialAd> onAdLoaded,
    required FullScreenAdLoadErrorCallback onAdFailedToLoad,
    AdEventCallback? onAdOpened,
    AdEventCallback? onAdClosed,
    AdEventCallback? onAdClicked,
  }) : super(
          onAdLoaded: onAdLoaded,
          onAdFailedToLoad: onAdFailedToLoad,
          onAdOpened: onAdOpened,
          onAdClosed: onAdClosed,
          onAdClicked: onAdClicked,
        );
}
```

#### lib/src/mobile_ads.dart : 자세한 내용은 ‘AdmizeSample’ 참조

```dart
import 'ad_instance_manager.dart';

class MobileAds {
  MobileAds._();

  static final MobileAds _instance = MobileAds._().._init();

  static MobileAds get instance => _instance;

  Future<void> initialize() {
    return instanceManager.initialize();
  }

  void _init() {
    instanceManager.channel.invokeMethod('_init');
  }
}
```

#### lib/admize_mobile_ads.dart : 자세한 내용은 ‘AdmizeSample’ 참조

```dart
export 'src/mobile_ads.dart';
export 'src/ad_containers.dart';
export 'src/ad_listeners.dart';
```

> admize SDK 설치 관련하여 문의 사항은 고객센터 **1544-8867**
> 또는 ops_admize@fsn.co.kr 로 문의주시기 바랍니다.
