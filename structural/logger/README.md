# Logger

`logger/api` exposes the `Logger` contract. Consumers receive it through injection;
Flow extensions remain independent of both the logger and DI.

The app depends on `logger/di` and loads `LoggerModule.init()`. This module registers
a shared instance through the factory function in `logger/impl`. The implementation
class remains internal and declares no DI registrations.

Debug builds use Timber through `debugImplementation`, with code in `src/debug`.
Release builds use the empty implementation in `src/release`. The DI module does
not depend on Timber, and no runtime flag enables logging.
