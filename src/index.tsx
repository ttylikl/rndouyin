import { DeviceEventEmitter, NativeModules } from 'react-native';
import { EventEmitter } from 'events';

// type RndouyinType = {
//   multiply(a: number, b: number): Promise<number>;
//   dyauth(): Promise<string>;
//   registerApp(clientKey: string): Promise<string>;
//   sendAuthRequest(scope: string = "user_info", scope1: string = "", scope0: string = "", state: string = ""): Promise<any>;
//   foo(): Promise<string>;
// };

const { Rndouyin } = NativeModules;

// Event emitter to dispatch request and response from Rndouyin.
const emitter = new EventEmitter();

DeviceEventEmitter.addListener('DouYin_Resp', (resp) => {
  console.log('DouYin_Resp', resp);
  emitter.emit(resp.type, resp);
});

Rndouyin.foo = (): Promise<any> => {
  return new Promise((resolve) => {
    return resolve('okokok');
  });
};

Rndouyin.sendAuthRequest = (scope: string = "user_info", scope1: string = "", scope0: string = "", state: string = ""): Promise<any> => {
  return new Promise((resolve, _) => {
    emitter.once('SendAuth.Resp', (resp) => {
      console.log('SendAuth.Resp', resp);
      if (resp.errCode === 0) {
        resolve(resp);
      } else {
        console.log('User auth failed!', resp);
        // reject(resp);
        // 不做 reject了，交给应用代码去根据authCode进行处理
        resolve(resp);
      }
    });
    // let state = "";
    // // "user_info" 是必选scope，native实现自带，dyauth参数只有两个附加scope可选
    // let scope0 = ""; // "following.list"; // 关注列表   // 获取用户手机号 "mobile_alert"
    // let scope1 = ""; //"fans.list"; // 粉丝列表
    Rndouyin.dyauth(scope, scope1, scope0, state);
  });
};

export default Rndouyin; // as RndouyinType;
