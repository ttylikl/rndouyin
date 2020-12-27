import * as React from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import Rndouyin from 'rndouyin';

export interface Props {
  name: string;
}
export interface State {
  result: number;
  msg: string;
}

export default class Home extends React.Component<Props, State> {
  constructor(props: any) {
    super(props);
  }

  state: State = {
    result: 0,
    msg: '',
  };

  componentDidMount() {}

  onTest1 = async (e: any) => {
    console.log('onTest1', e);
    let result: number = await Rndouyin.multiply(3, 7);
    this.setState({ result });
  };

  onTest2 = async (e: any) => {
    console.log('onTest2', e);
    //let msg: string = await Rndouyin.hellodouyin();
    let msg: string = await Rndouyin.foo();
    this.setState({ msg });
  };

  onTest3 = async (e: any) => {
    console.log('onTest3', e);
    let msg: string = await Rndouyin.registerApp('awsxdh3k1fiojgnu'); // 申请完成后替换
    this.setState({ msg });
    await Rndouyin.dyauth();
  };

  render() {
    return (
      <View style={styles.container}>
        <Text>Home</Text>
        <Text>Result: {this.state.result}</Text>
        <Text>Message: {this.state.msg}</Text>
        <TouchableOpacity onPress={this.onTest1}>
          <Text>Test #1</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={this.onTest2}>
          <Text>Test #2</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={this.onTest3}>
          <Text>Test #3(init DouYin)</Text>
        </TouchableOpacity>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 15,
    backgroundColor: '#F5FCFF',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
