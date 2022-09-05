// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import 'dart:async';

import 'package:admize_flutter_sdk/admize_mobile_ads.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:visibility_detector/visibility_detector.dart';

import 'ad_instance_manager.dart';
import 'ad_listeners.dart';

class AdRequest {
  /// Default constructor for [AdRequest].
  const AdRequest(
      {required this.admizeAdType,
      required this.publisherUid,
      required this.placementUid,
      required this.mediaUid,
      this.admizeMultiBidsList,
      this.coppaEnabled,
      this.setTest});

  final AdType admizeAdType;
  final String publisherUid;
  final String placementUid;
  final String mediaUid;
  final List<AdSize>? admizeMultiBidsList;
  final bool? coppaEnabled;
  final bool? setTest;

  @override
  bool operator ==(Object other) {
    return other is AdRequest &&
        admizeAdType == other.admizeAdType &&
        publisherUid == other.publisherUid &&
        placementUid == other.placementUid &&
        mediaUid == other.mediaUid &&
        listEquals<AdSize>(admizeMultiBidsList, other.admizeMultiBidsList) &&
        coppaEnabled == other.coppaEnabled &&
        setTest == other.setTest;
  }
}

enum AdSize {
  smallBanner(width: 320, height: 50, name4Java: "SMALL_BANNER"),
  mediumRectangleBanner(
      width: 300, height: 250, name4Java: "MEDIUM_RECTANGLE_BANNER"),
  largeBanner(width: 320, height: 100, name4Java: "LARGE_BANNER");

  const AdSize(
      {required this.width, required this.height, required this.name4Java});

  /// The vertical span of an ad.
  final int height;

  /// The horizontal span of an ad.
  final int width;

  final String name4Java;
}

enum AdType {
  banner,
  interstitial;
}

abstract class Ad {
  /// Frees the plugin resources associated with this ad.
  Future<void> dispose() {
    return instanceManager.disposeAd(this);
  }
}

/// Base class for mobile [Ad] that has an in-line view.
///
/// A valid [adUnitId] and [size] are required.
abstract class AdWithView extends Ad {
  /// Default constructor, used by subclasses.
  AdWithView({required this.listener});

  /// The [AdWithViewListener] for the ad.
  final AdWithViewListener listener;

  /// Starts loading this ad.
  ///
  /// Loading callbacks are sent to this [Ad]'s [listener].
  Future<void> load();
}

/// An [Ad] that is overlaid on top of the UI.
abstract class AdWithoutView extends Ad {}

/// Displays an [Ad] as a Flutter widget.
///
/// This widget takes ads inheriting from [AdWithView]
/// (e.g. [BannerAd] and [NativeAd]) and allows them to be added to the Flutter
/// widget tree.
///
/// Must call `load()` first before showing the widget. Otherwise, a
/// [PlatformException] will be thrown.
class AdWidget extends StatefulWidget {
  /// Default constructor for [AdWidget].
  ///
  /// [ad] must be loaded before this is added to the widget tree.
  const AdWidget({Key? key, required this.ad}) : super(key: key);

  /// Ad to be displayed as a widget.
  final AdWithView ad;

  @override
  State<AdWidget> createState() => _AdWidgetState();
}

class _AdWidgetState extends State<AdWidget> {
  bool _adIdAlreadyMounted = false;
  bool _adLoadNotCalled = false;
  bool _firstVisibleOccurred = false;

  @override
  void initState() {
    super.initState();
    final int? adId = instanceManager.adIdFor(widget.ad);
    if (adId != null) {
      if (instanceManager.isWidgetAdIdMounted(adId)) {
        _adIdAlreadyMounted = true;
      }
      instanceManager.mountWidgetAdId(adId);
    } else {
      _adLoadNotCalled = true;
    }
  }

  @override
  void dispose() {
    super.dispose();
    final int? adId = instanceManager.adIdFor(widget.ad);
    if (adId != null) {
      instanceManager.unmountWidgetAdId(adId);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_adIdAlreadyMounted) {
      throw FlutterError.fromParts(<DiagnosticsNode>[
        ErrorSummary('This AdWidget is already in the Widget tree'),
        ErrorHint(
            'If you placed this AdWidget in a list, make sure you create a new instance '
            'in the builder function with a unique ad object.'),
        ErrorHint(
            'Make sure you are not using the same ad object in more than one AdWidget.'),
      ]);
    }
    if (_adLoadNotCalled) {
      throw FlutterError.fromParts(<DiagnosticsNode>[
        ErrorSummary(
            'AdWidget requires Ad.load to be called before AdWidget is inserted into the tree'),
        ErrorHint(
            'Parameter ad is not loaded. Call Ad.load before AdWidget is inserted into the tree.'),
      ]);
    }
    if (defaultTargetPlatform == TargetPlatform.android) {
      // Do not attach the platform view widget until it will actually become
      // visible. This is a workaround for
      // https://github.com/googleads/googleads-mobile-flutter/issues/580,
      // where impressions are erroneously fired due to how platform views are
      // rendered.
      // TODO (jjliu15): Remove this after the flutter issue is resolved.
      if (_firstVisibleOccurred) {
        return PlatformViewLink(
          viewType: '${instanceManager.channel.name}/ad_widget',
          surfaceFactory:
              (BuildContext context, PlatformViewController controller) {
            return AndroidViewSurface(
              controller: controller as AndroidViewController,
              gestureRecognizers: const <
                  Factory<OneSequenceGestureRecognizer>>{},
              hitTestBehavior: PlatformViewHitTestBehavior.opaque,
            );
          },
          onCreatePlatformView: (PlatformViewCreationParams params) {
            return PlatformViewsService.initSurfaceAndroidView(
              id: params.id,
              viewType: '${instanceManager.channel.name}/ad_widget',
              layoutDirection: TextDirection.ltr,
              creationParams: instanceManager.adIdFor(widget.ad),
              creationParamsCodec: const StandardMessageCodec(),
            )
              ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
              ..create();
          },
        );
      } else {
        final adId = instanceManager.adIdFor(widget.ad);
        return VisibilityDetector(
          key: Key('android-platform-view-$adId'),
          onVisibilityChanged: (visibilityInfo) {
            if (!_firstVisibleOccurred &&
                visibilityInfo.visibleFraction > 0.01) {
              setState(() {
                _firstVisibleOccurred = true;
              });
            }
          },
          child: Container(),
        );
      }
    }

    throw Exception('is not supported on $defaultTargetPlatform');
  }
}

/// A banner ad.
///
/// This ad can either be overlaid on top of all flutter widgets as a static
/// view or displayed as a typical Flutter widget. To display as a widget,
/// instantiate an [AdWidget] with this as a parameter.
class BannerAd extends AdWithView {
  /// Creates a [BannerAd].
  ///
  /// A valid [adUnitId], nonnull [listener], and nonnull request is required.
  BannerAd({
    required AdWithViewListener listener,
    required this.request,
  }) : super(listener: listener);

  /// Targeting information used to fetch an [Ad].
  final AdRequest request;

  @override
  Future<void> load() async {
    await instanceManager.loadBannerAd(this);
  }

  AdSize get size => request.admizeMultiBidsList!
      .reduce((curr, next) => curr.height > next.height ? curr : next);
}

/// A full-screen interstitial ad for the Google Mobile Ads Plugin.
class InterstitialAd extends AdWithoutView {
  InterstitialAd._({
    required this.request,
    required this.adLoadCallback,
  });

  /// Targeting information used to fetch an [Ad].
  final AdRequest request;

  /// Callback to be invoked when the ad finishes loading.
  final InterstitialAdLoadCallback adLoadCallback;

  /// Loads an [InterstitialAd] with the given [adUnitId] and [request].
  static Future<void> load({
    required AdRequest request,
    required InterstitialAdLoadCallback adLoadCallback,
  }) async {
    InterstitialAd ad =
        InterstitialAd._(adLoadCallback: adLoadCallback, request: request);

    await instanceManager.loadInterstitialAd(ad);
  }

  /// Displays this on top of the application.
  ///
  /// Set [fullScreenContentCallback] before calling this method to be
  /// notified of events that occur when showing the ad.
  Future<void> show() {
    return instanceManager.showAdWithoutView(this);
  }
}
