import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:crop_img_plugin_flutter/crop_img_plugin_flutter_method_channel.dart';

void main() {
  MethodChannelCropImgPluginFlutter platform = MethodChannelCropImgPluginFlutter();
  const MethodChannel channel = MethodChannel('crop_img_plugin_flutter');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
