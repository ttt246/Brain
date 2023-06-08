# Image Relatedness

## What you'll find

This folder contains an example test, and is composed of the following elements:
- `README.md`: (This file) Contains the purpose of the test.
- `input.yaml`: Sets variables that will provided to test.
- `unit.yaml`: Sets the steps to perform while executing the test.

You can also get familiar with our test script syntax by following our [Synthax Documentation](https://github.com/saucelabs/saucectl-apix-example/blob/main/docs/README.md).

## Details

This test only differs from the previous one in the usage of the input variables ([`input.yaml`](./input.yaml) file)
and then referencing those variables in the test, rather than having URLs and Tokens in the test itself.  
That might help in adding some abstraction:

- Use the same test against different API domains (e.g. staging vs production)
- Use multiple input set and test different endpoints

## More about `input.yaml` syntax
More details about the syntax used in this file can be found [here](https://github.com/saucelabs/saucectl-apix-example/blob/main/docs/README.md#input-inputyaml)
