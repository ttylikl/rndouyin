import { NativeModules } from 'react-native';

type RndouyinType = {
  multiply(a: number, b: number): Promise<number>;
};

const { Rndouyin } = NativeModules;

export default Rndouyin as RndouyinType;
