# Basics: Send Notification

## What you'll find

This folder contains an example test, and is composed of the following elements:
- `README.md`: (This file) Contains the purpose of the test.
- `input.yaml`: Sets variables that will provided to test.
- `unit.yaml`: Sets the steps to perform while executing the test.

You can also get familiar with our test script syntax by following our [Synthax Documentation](https://github.com/saucelabs/saucectl-apix-example/blob/main/docs/README.md).

## Details

As first test, we just would like to hit an API endpoint and verify it responds as we are expecting.
The component `get` will perform an HTTP request to the given URL. Let's discard the other parameters for now.
__Note:__ By default we expect a request to respond with a valid HTTP status code. If so, the request is considered as "passed" and so will the test.
