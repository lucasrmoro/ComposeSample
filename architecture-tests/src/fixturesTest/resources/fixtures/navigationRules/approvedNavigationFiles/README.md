# approvedNavigationFiles

Rule method: `NavigationRulesTest.feature navigation packages must contain only approved feature files`.

Both modules contain the three required files. Only the invalid module adds ExampleNavigationHelper.kt in the navigation package.

Both cases execute only this method. Expected assertion diagnostic: `approved feature navigation files`.
The package must be exactly br.com.composesample.example.navigation; subpackages and
files with another feature prefix are not approved.
