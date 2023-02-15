package com.example.crop_img_plugin_flutter

//import androidx.annotation.NonNull
//
import android.app.Activity
import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** CropImgPluginFlutterPlugin */
class CropImgPluginFlutterPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {

    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var cropImgCallBack: CropImgCallBack
    private lateinit var delegate: CropImgCallBack

    lateinit var binding: ActivityPluginBinding
    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "crop_img_plugin_flutter")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
        cropImgCallBack = CropImgCallBack(context, channel)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "getImage" -> {
                cropImgCallBack.startCrop(call, result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

     fun setupActivity(activity: Activity): CropImgCallBack {
         cropImgCallBack.imageCropperDelegate(activity)
         delegate =cropImgCallBack
        return delegate
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        setupActivity(binding.activity)
        this.binding = binding
        binding.addActivityResultListener(delegate)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        binding.removeActivityResultListener(delegate)
    }
}
