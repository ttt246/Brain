# Upload Image

## What you'll find

This folder contains an example test, and is composed of the following elements:
- `README.md`: (This file) Contains the purpose of the test.
- `input.yaml`: Sets variables that will provided to test.
- `unit.yaml`: Sets the steps to perform while executing the test.

You can also get familiar with our test script syntax by following our [Synthax Documentation](https://github.com/saucelabs/saucectl-apix-example/blob/main/docs/README.md).

## Details

In addition to the HTTP request done in the first test, we proceed by adding some really basic assertions:
- we expect the response payload (obtained by the above request) to exist
- we expect the payload to be of type ARRAY
