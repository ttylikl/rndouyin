package com.rndouyin

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.bytedance.sdk.open.aweme.CommonConstants
import com.bytedance.sdk.open.aweme.authorize.model.Authorization
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler
import com.bytedance.sdk.open.aweme.common.model.BaseReq
import com.bytedance.sdk.open.aweme.common.model.BaseResp
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory
import com.bytedance.sdk.open.douyin.DouYinOpenConfig
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi
import com.facebook.common.logging.FLog
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.rndouyin.modulelist

class RndouyinModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), IApiEventHandler {

  lateinit var douYinOpenApi: DouYinOpenApi
  var appId: String = ""

  override fun getName(): String {
    return "Rndouyin"
  }

  init {
      modulelist.addlist(this)
  }
  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Int, b: Int, promise: Promise) {
    promise.resolve(a * b)
  }

  @ReactMethod
  fun registerApp(clientKey: String, promise: Promise) {
    if (appId != "") {
      promise.resolve("noop")
      return
    }
    var currentActivity: Activity? = getCurrentActivity()
    if (currentActivity == null) {
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
  fun dyauth(scope: String, scope1: String, scope0: String, state: String, promise: Promise) {
    var request: Authorization.Request = Authorization.Request();
    request.scope = scope; // 默认应该是这个"user_info";                          // 用户授权时必选权限
//                request.optionalScope1 = mOptionalScope2;     // 用户授权时可选权限（默认选择）
//                request.optionalScope0 = mOptionalScope1;    // 用户授权时可选权限（默认不选）
    if(scope0 != "") {
      request.optionalScope0 = scope0 // 用户授权时可选权限（默认不选）
    }
    if(scope1 != "") {
      request.optionalScope1 = scope1 // 用户授权时可选权限（默认选择）
    };
    if(state != "") {
      request.state = state //"ww";                                   // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
    };
    douYinOpenApi.authorize(request);               // 优先使用抖音app进行授权，如果抖音app因版本或者其他原因无法授权，则使用wap页授权

    promise.resolve("called")
  }

  fun handleIntent(intent: Intent?) {
    douYinOpenApi.handleIntent(intent, this)
  }

  override fun onReq(req: BaseReq?) {}

  override fun onResp(resp: BaseResp) {
//    if (resp.type == CommonConstants.ModeType.SHARE_CONTENT_TO_TT_RESP) {
//      val response = resp as Share.Response
//      Toast.makeText(this, " code：" + response.errorCode + " 文案：" + response.errorMsg, Toast.LENGTH_SHORT).show()
//      val intent = Intent(this, MainActivity::class.java)
//      startActivity(intent)
//    } else
    if (resp.type == CommonConstants.ModeType.SEND_AUTH_RESPONSE) {
      val response = resp as Authorization.Response
      // val intent: Intent? = null
      /*
            步骤五： 获取auth code 结果返回说明#
            返回值及相关说明

            返回值	说明
            errorCode	OK = 0 授权成功， ERRORUNKNOW = -1 未知错误， ERRORCANCEL = -2 用户手动取消 更多错误码请参考CommonConstants.java
            authCode	临时票据code，用来换取access_token
            state	第三方程序发送时用于表示其请求的唯一性标志，由第三方程序调openApi.authorize(request)时传入，由抖音终端回传。
            grantedPermissions	第三方通过用户授权取得的授权域
            */
      val map = Arguments.createMap()
      map.putInt("errCode", resp.errorCode)
      map.putString("authCode", response.authCode)
      map.putString("state", response.state)
      map.putString("grantedPermissions", response.grantedPermissions)
      map.putString("type", "SendAuth.Resp")

      var ctx: ReactApplicationContext? = this.getReactApplicationContext();
      ctx?.getJSModule(RCTDeviceEventEmitter::class.java)?.emit("DouYin_Resp", map)
    }
  }

  override fun onErrorIntent(intent: Intent?) {
    // 错误数据
    FLog.d("RndouyinModule", "Intent出错");
  }

//  private val modules: ArrayList<RndouyinModule> = ArrayList()
//
//  fun init() {
//    modules.add(this)
//  }
//
//  fun handleIntent(intent: Intent?) {
//    for (mod in modules) {
//      mod.api.handleIntent(intent, mod)
//    }
//  }

}
