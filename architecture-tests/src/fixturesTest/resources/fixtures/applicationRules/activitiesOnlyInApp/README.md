# activitiesOnlyInApp

Rule method: `ApplicationRulesTest.activities must be declared only in the app module`.

Both cases contain one android.app.Activity subclass in app. The valid feature contains
an ordinary ExampleScreen. The invalid feature makes that same class extend
FragmentActivity, without changing its name to end with Activity.

Expected assertion diagnostic: `Activity declarations are only allowed in :app`.
