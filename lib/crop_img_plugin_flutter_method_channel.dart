import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'crop_img_plugin_flutter_platform_interface.dart';

/// An implementation of [CropImgPluginFlutterPlatform] that uses method channels.
class MethodChannelCropImgPluginFlutter extends CropImgPluginFlutterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('crop_img_plugin_flutter');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
