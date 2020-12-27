package com.rndouyin

import android.app.Activity;

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;
import com.bytedance.sdk.open.douyin.DouYinOpenConfig;

class RndouyinModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    lateinit var douYinOpenApi:DouYinOpenApi
    var appId: String = ""

    init {
    }

    override fun getName(): String {
        return "Rndouyin"
    }

    // Example method
    // See https://reactnative.dev/docs/native-modules-android
    @ReactMethod
    fun multiply(a: Int, b: Int, promise: Promise) {

      promise.resolve(a * b)

    }

    @ReactMethod
    fun hellodouyin(promise: Promise) {

      promise.resolve("hello from rndouyin!")

    }


    @ReactMethod
    fun registerApp(clientKey: String, promise: Promise) {
      if(appId!="") {
        promise.resolve("noop")
        return
      }
      var currentActivity: Activity? = getCurrentActivity()
      if(currentActivity==null) {
        promise.resolve("currentActivity is null!")
        return
      }
      DouYinOpenApiFactory.init(DouYinOpenConfig(clientKey));

      // 初始化api,需要传入targetApp,默认为TikTok
      douYinOpenApi = DouYinOpenApiFactory.create(currentActivity)
      appId = clientKey
      promise.resolve("ok")
    }

    @ReactMethod
    fun dyauth(promise: Promise) {
      var request: Authorization.Request = Authorization.Request();
      request.scope = "user_info";                          // 用户授权时必选权限
//                request.optionalScope1 = mOptionalScope2;     // 用户授权时可选权限（默认选择）
//                request.optionalScope0 = mOptionalScope1;    // 用户授权时可选权限（默认不选）
      request.state = "ww";                                   // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
      douYinOpenApi.authorize(request);               // 优先使用抖音app进行授权，如果抖音app因版本或者其他原因无法授权，则使用wap页授权

      promise.resolve("called")
    }
}
