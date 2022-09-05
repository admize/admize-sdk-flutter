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
