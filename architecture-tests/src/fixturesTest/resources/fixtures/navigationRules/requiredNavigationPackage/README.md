# requiredNavigationPackage

Rule method: `NavigationRulesTest.feature presentation modules must declare a complete navigation package`.

The valid module contains ExampleEntryPointImpl.kt, ExampleNavHost.kt, and ExampleRoutes.kt in example.navigation. The invalid module has only ordinary UI content and no navigation package.

Both cases execute only this method. Expected assertion diagnostic: `complete feature navigation package`.
The package must be exactly br.com.composesample.example.navigation; subpackages and
files with another feature prefix are not approved.
