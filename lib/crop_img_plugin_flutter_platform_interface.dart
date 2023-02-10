import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'crop_img_plugin_flutter_method_channel.dart';

abstract class CropImgPluginFlutterPlatform extends PlatformInterface {
  /// Constructs a CropImgPluginFlutterPlatform.
  CropImgPluginFlutterPlatform() : super(token: _token);

  static final Object _token = Object();

  static CropImgPluginFlutterPlatform _instance = MethodChannelCropImgPluginFlutter();

  /// The default instance of [CropImgPluginFlutterPlatform] to use.
  ///
  /// Defaults to [MethodChannelCropImgPluginFlutter].
  static CropImgPluginFlutterPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [CropImgPluginFlutterPlatform] when
  /// they register themselves.
  static set instance(CropImgPluginFlutterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
