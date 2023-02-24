# react-native-sms-auto-read

This package will automatically read sms
Only supported in Android

## Installation

```sh
npm install react-native-sms-auto-read
```

## Usage

```js
import { useSmsUserConsent } from 'react-native-sms-auto-read';

const Example = () => {
  const [code, setCode] = useState();

  const retrievedCode = useSmsUserConsent();

  useEffect(() => {
    if (retrievedCode) setCode(retrievedCode);
  }, [retrievedCode]);

  return <TextInput value={code} onChangeText={setCode} />;
};

// ...
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
