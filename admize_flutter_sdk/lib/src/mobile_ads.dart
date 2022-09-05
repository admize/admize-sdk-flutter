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
