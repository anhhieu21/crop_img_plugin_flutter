import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'crop_img_plugin_flutter_platform_interface.dart';
import 'model/cropped_file/cropped_file.dart';
import 'model/settings.dart';

/// An implementation of [CropImgPluginFlutterPlatform] that uses method channels.
class MethodChannelCropImgPluginFlutter extends CropImgPluginFlutterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('crop_img_plugin_flutter');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<dynamic> getImage({
    required String sourcePath,
    int? maxWidth,
    int? maxHeight,
    CropAspectRatio? aspectRatio,
    List<CropAspectRatioPreset> aspectRatioPresets = const [
      CropAspectRatioPreset.original,
      CropAspectRatioPreset.square,
      CropAspectRatioPreset.ratio3x2,
      CropAspectRatioPreset.ratio4x3,
      CropAspectRatioPreset.ratio16x9
    ],
    CropStyle cropStyle = CropStyle.rectangle,
    ImageCompressFormat compressFormat = ImageCompressFormat.jpg,
    int compressQuality = 90,
    List<PlatformUiSettings>? uiSettings,
  }) async {
    assert(await File(sourcePath).exists());
    assert(maxWidth == null || maxWidth > 0);
    assert(maxHeight == null || maxHeight > 0);
    assert(compressQuality >= 0 && compressQuality <= 100);

    final arguments = <String, dynamic>{
      'source_path': sourcePath,
      'max_width': maxWidth,
      'max_height': maxHeight,
      'ratio_x': aspectRatio?.ratioX,
      'ratio_y': aspectRatio?.ratioY,
      'aspect_ratio_presets':
          aspectRatioPresets.map<String>(aspectRatioPresetName).toList(),
      'crop_style': cropStyleName(cropStyle),
      'compress_format': compressFormatName(compressFormat),
      'compress_quality': compressQuality,
    };

    if (uiSettings != null) {
      for (final settings in uiSettings) {
        arguments.addAll(settings.toMap());
      }
    }

    final String? resultPath =
        await methodChannel.invokeMethod('getImage', arguments);
    return resultPath == null ? null : CroppedFile(resultPath);
  }
}
