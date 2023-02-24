import { NativeEventEmitter, NativeModules } from 'react-native';

export const { SmsAutoRead } = NativeModules;

export const eventEmitter = new NativeEventEmitter(SmsAutoRead);
