The Chemistry Development Rendering Kit
===

A small library that helps in the rendering of molecules within the [Chemistry Development Kit][CDK].

The key difference with this and the original CDK rendering library is that this maintains the links between the atoms and the rendering elements. In order to complete this the visitor pattern was removed. This pattern can be useful; however some rendering, Small Vector Graphics (SVG) for one, has the ability to do more than draw elements and may contain more contextual information.

```bash
$ git clone https://github.com/sacko87/cdk.git
$ cd cdk
$ mvn test
```

[CDK]: https://github.com/cdk/cdk
