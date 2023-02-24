import { Events } from './constants';
import { eventEmitter } from './nativeApi';

export default function addErrorListener(onErrorReceived: (event: any) => void) {
  const listener = eventEmitter.addListener(Events.AKV_SMS_RETRIEVE_ERROR, onErrorReceived);

  function removeErrorListener() {
    listener.remove();
  }

  return removeErrorListener;
}
