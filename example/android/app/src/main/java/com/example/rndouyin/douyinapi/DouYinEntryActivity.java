package com.example.rndouyin.douyinapi;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import androidx.annotation.Nullable;
import android.widget.Toast;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;
import com.example.rndouyin.MainActivity;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.rndouyin.RndouyinModule;
/**
 * 主要功能：接受授权返回结果的activity
 * <p>
 * <p>
 * 也可通过request.callerLocalEntry = "com.xxx.xxx...activity"; 定义自己的回调类
 */
public class DouYinEntryActivity extends Activity implements IApiEventHandler {

    DouYinOpenApi douYinOpenApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        douYinOpenApi = DouYinOpenApiFactory.create(this);
        douYinOpenApi.handleIntent(getIntent(), this);
        finish();
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == CommonConstants.ModeType.SHARE_CONTENT_TO_TT_RESP) {
            Share.Response response = (Share.Response) resp;
            Toast.makeText(this, " code：" + response.errorCode + " 文案：" + response.errorMsg, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (resp.getType() == CommonConstants.ModeType.SEND_AUTH_RESPONSE) {
            Authorization.Response response = (Authorization.Response) resp;
            Intent intent = null;
            if (resp.isSuccess()) {

                Toast.makeText(this, "授权成功，获得权限：" + response.grantedPermissions,
                        Toast.LENGTH_LONG).show();

            }
            /*
            步骤五： 获取auth code 结果返回说明#
            返回值及相关说明

            返回值	说明
            errorCode	OK = 0 授权成功， ERRORUNKNOW = -1 未知错误， ERRORCANCEL = -2 用户手动取消 更多错误码请参考CommonConstants.java
            authCode	临时票据code，用来换取access_token
            state	第三方程序发送时用于表示其请求的唯一性标志，由第三方程序调openApi.authorize(request)时传入，由抖音终端回传。
            grantedPermissions	第三方通过用户授权取得的授权域
            */
            WritableMap map = Arguments.createMap();
            map.putInt("errCode", resp.errorCode);
            map.putString("authCode", response.authCode);
            map.putString("state", response.state);
            map.putString("grantedPermissions", response.grantedPermissions);
            map.putString("type", "SendAuth.Resp");
            //this.getReactApplicationContext()
            Context ctx = this.getApplicationContext();
            Application apc = this.getApplication();
            ReactApplication rapc = (ReactApplication) apc;
            ReactNativeHost rhost = rapc.getReactNativeHost();
            ReactContext context = rhost.getReactInstanceManager().getCurrentReactContext();

            ReactApplicationContext rctx = (ReactApplicationContext) context;
            rctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("DouYin_Resp", map);
        }

    }

    @Override
    public void onErrorIntent(@Nullable Intent intent) {
        // 错误数据
        Toast.makeText(this, "Intent出错", Toast.LENGTH_LONG).show();
    }
}
