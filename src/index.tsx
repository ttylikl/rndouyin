import { DeviceEventEmitter, NativeModules } from 'react-native';
import { EventEmitter } from 'events';

type RndouyinType = {
  multiply(a: number, b: number): Promise<number>;
  hellodouyin(): Promise<string>;
  dyauth(): Promise<string>;
  registerApp(clientKey: string): Promise<string>;
  sendAuthRequest(): Promise<any>;
  foo(): Promise<string>;
};

const { Rndouyin } = NativeModules;

// Event emitter to dispatch request and response from WeChat.
const emitter = new EventEmitter();

DeviceEventEmitter.addListener('WeChat_Resp', (resp) => {
  emitter.emit(resp.type, resp);
});

/**
 * @method sendAuthRequest
 * @param {Array} scopes - the scopes for authentication.
 * @return {Promise}
 */
// function sendAuthRequest(): Promise<any> {
//   return new Promise((resolve, reject) => {
//     Rndouyin.dyauth();
//     emitter.once('SendAuth.Resp', resp => {
//       if (resp.errCode === 0) {
//         resolve(resp);
//       } else {
//         reject(resp);
//       }
//     });
//   });
// }

Rndouyin.foo = (): Promise<any> => {
  return new Promise((resolve) => {
    return resolve('okokok');
  });
};

export default Rndouyin as RndouyinType;
