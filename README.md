# Callback for Android

##What?

Callback is an app which reacts to SMS triggers to callback someone automatically.

##Why?

A few scenarios where this might be useful:

 - You need to talk to someone who is near their phone but otherwise unable to answer
 - A simple baby monitor
 - To find your phone
 
##How?

Callback is written fully in [Kotlin](https://kotlinlang.org/) and includes Unit and UI tests for the main activity and presenter.
Callback currently supports Android API 19+ and targets API 23, with support for runtime permissions.

See the spike in the [/spike/costraint-layout](https://github.com/K4KYA/callback-android/tree/spike/constraint-layout) branch for an example implementation of the new `ConstraintLayout` ViewGroup.

Callback uses [RxKotlin](https://github.com/ReactiveX/RxKotlin), which is broken out into a separate, tested, module. This showcases the power of Kotlin extension methods by adding an `Observable` stream which wraps a `TextWatcher` object. This is largely inspired by the [RxBindings project by Jake Wharton](https://github.com/JakeWharton/RxBinding), but works slightly differently. 
