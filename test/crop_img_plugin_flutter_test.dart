import 'package:flutter_test/flutter_test.dart';
import 'package:crop_img_plugin_flutter/crop_img_plugin_flutter.dart';
import 'package:crop_img_plugin_flutter/crop_img_plugin_flutter_platform_interface.dart';
import 'package:crop_img_plugin_flutter/crop_img_plugin_flutter_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockCropImgPluginFlutterPlatform
    with MockPlatformInterfaceMixin
    implements CropImgPluginFlutterPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final CropImgPluginFlutterPlatform initialPlatform = CropImgPluginFlutterPlatform.instance;

  test('$MethodChannelCropImgPluginFlutter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelCropImgPluginFlutter>());
  });

  test('getPlatformVersion', () async {
    CropImgPluginFlutter cropImgPluginFlutterPlugin = CropImgPluginFlutter();
    MockCropImgPluginFlutterPlatform fakePlatform = MockCropImgPluginFlutterPlatform();
    CropImgPluginFlutterPlatform.instance = fakePlatform;

    expect(await cropImgPluginFlutterPlugin.getPlatformVersion(), '42');
  });
}
