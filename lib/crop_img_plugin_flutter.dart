
import 'crop_img_plugin_flutter_platform_interface.dart';

class CropImgPluginFlutter {
  Future<String?> getPlatformVersion() {
    return CropImgPluginFlutterPlatform.instance.getPlatformVersion();
  }
}
