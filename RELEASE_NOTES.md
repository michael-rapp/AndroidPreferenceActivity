# AndroidPreferenceActivity - RELEASE NOTES

## Version 5.0.2 (Oct. 31th 2017)

A minor release, which introduces the following changes:

- Added `descendantFocusability` attribute to the layouts of preferences.
- Fixed Lint errors
- Updated dependency "AndroidUtil" to version 1.18.1.
- Updated AppCompat v7 support library to version 27.0.0.
- Updated CardView v7 support library to version 27.0.0.

## Version 5.0.2 (Oct. 25th 2017)

A bugfix release, which fixes the following issues:

- Fixed an issue, which causes the attributes of a `PreferenceFragment` to be obtained from the wrong theme. 

## Version 5.0.1 (Oct. 25th 2017)

A bugfix release, which fixes the following issues:

- The divider color of a `PreferenceFragment` is now correctly obtained from the theme.

## Version 5.0.0 (Oct. 25th 2017)

A major release, which aims at adapting the library according to the use of preferences in Android 8.0 and at fixing some fundamental issues. This required to re-implement large parts of the library:

- `NavigationPreference`s are now used as navigation items. This enables to use regular preferences and categories alongside with navigation items.
- Many methods and theme attributes have been renamed. However, most of the functionality of the previous versions is still available.
- Fixed situations where the state of fragments was not completely restored after orientation changes or after resuming the activity.
- Fixed a possible crash when using Android 7's multi-window mode on tablets.
- It is now possible to use the fullscreen layout instead of the split screen layout on tablets.
- The appearance pf dividers has been changed. They are now only displayed above a `PreferenceCategory`. 
- Changed the activity's background color on phones.
- Changed the title text color of preferences and preference headers.
- Paddings and icon sizes are now equal for phones and tablet devices.
- Updated `targetSdkVersion` to API level 26 (Android 8.0).
- Updated dependency "AndroidUtil" to version 1.18.0.
- Updated AppCompat v7 support library to version 26.1.0.
- Updated CardView v7 support library to version 26.1.0.
- Updating the dependencies required to increase the `minSdkVersion` to API level 14.

## Version 4.2.11 (Apr. 28th 2017)
 
A minor release, which introduces the following changes:
 
 - Improved RTL support.
 - Updated dependency "AndroidUtil" to version 1.15.2.
 - Updated Appcompat v7 support library to version 25.3.1.
 - Updated CardView v7 support library to version 25.3.1.

## Version 4.2.10 (Feb. 16th 2017)

A bugfix release, which fixes the following issues:

- Fixed possible errors when restoring default values in the example app (https://github.com/michael-rapp/AndroidPreferenceActivity/issues/20)

## Version 4.2.9 (Jan. 26th 2017)

A minor release, which introduces the following changes:

- Updated `targetSdkVersion` to API level 25 (Android 7.1)
- Updated dependency "AndroidUtil" to version 1.12.3.
- Updated Appcompat v7 support library to version 25.1.0.
- Updated CardView v7 support library to version 25.1.0.

## Version 4.2.8 (Sep. 13th 2016)

A minor release, which introduces the following changes:

- The font Roboto Medium is now used on devices with API level 21 or greater.

## Version 4.2.7 (Sep. 12th 2016)

A bugfix release, which fixes the following issues:

- Merged pull request https://github.com/michael-rapp/AndroidPreferenceActivity/pull/18

## Version 4.2.6 (Sep. 12th 2016)

A bugfix release, which fixes the following issues:

- Fixed issue https://github.com/michael-rapp/AndroidPreferenceActivity/issues/17

## Version 4.2.5 (Sep. 11th 2016)

A bugfix release, which fixes the following issues:

- Fixed issue https://github.com/michael-rapp/AndroidPreferenceActivity/issues/15
- Fixed issue https://github.com/michael-rapp/AndroidPreferenceActivity/issues/16
- Updated `targetSdkVersion` to API level 24 (Android 7.0)
- Updated dependency "AndroidUtil" to version 1.11.1.
- Updated Appcompat v7 support library to version 24.2.0.
- Updated CardView v7 support library to version 24.2.0.

## Version 4.2.4 (Aug. 14th 2016)

A bugfix release, which fixes the following issues:

- A possible `IllegalStateException` when resuming a `PreferenceFragment` has been fixed.

## Version 4.2.3 (May 26th 2016)

A minor release, which introduces the following changes:

- The library does now provide a built-in dark and light theme.
- Slightly adjusted button sizes and paddings.
- Updated AppCompat v7 support library to version 23.4.0.

## Version 4.2.2 (Apr. 15th 2016)

A bugfix release, which fixes the following issues:

- Fixed the return value of the `unselectPreferenceHeader`-method of the class `PreferenceActivity`.

## Version 4.2.1 (Apr. 15th 2016)

A minor release, which introduces the following changes:

- Added the `unselectPreferenceHeader`-method, which allows to programmatically return to the navigation on smartphones, to the class `PreferenceActivity`.

## Version 4.2.0 (Apr. 15th 2016)

A feature release, which introduces the following changes:

- It is now possible to change the background color of the bread crumbs on tablets programmatically or via a theme attribute.
- It is now possible to hide bread crumbs (on tablets and smartphones) globally or per preference header using a setter method or via Intent extras.

## Version 4.1.1 (Apr. 15th 2016)

A minor release, which introduces the following changes:

- Changed the appearance of preference categories on pre-Lollipop devices.
- The divider after the last item of a `PreferenceActivity`'s navigation is not shown anymore.
- Updated the AppCompat v7 support library to version 23.3.0.
- Updated the CardView v7 support library to version 23.3.0.

## Version 4.1.0 (Mar. 17th 2016)

A feature release, which introduces the following changes:

- Fixed issue https://github.com/michael-rapp/AndroidPreferenceActivity/issues/14
- Removed the `setPreferenceScreenBackground` and `getPreferenceScreenBackground`-methods from the class `PreferenceActivity`, because setting the background drawable of a `CardView` leads to issues. However, it is still possible to set a background color using the `setPreferenceScreenBackgroundColor`-method.
- Updated dependency "AndroidUtil" to version 1.4.5.
- Updated the AppCompat v7 support library to version 23.2.1.
- Updated the CardView v7 support library to version 23.2.1.

## Version 4.0.7 (Feb. 24th 2016)

A minor release, which introduces the following changes:

- The library is from now on distributed under the Apache License version 2.0. 
- Updated dependency "AndroidUtil" to version 1.4.3.
- Minor changes of the example app.

## Version 4.0.6 (Jan. 31th 2016)

A minor release, which introduces the following changes:

- Added `showView`- and `hideView`-methods, which allow to manually show or hide the animated view, to the class `HideViewOnScrollAnimation`.

## Version 4.0.5 (Jan. 3rd 2016)

A minor release, which introduces the following changes:

- Updated dependency "AndroidUtil" to version 1.3.0.

## Version 4.0.4 (Dec. 24th 2015)

A bugfix release, which fixes the following issues:

- https://github.com/michael-rapp/AndroidPreferenceActivity/issues/13

## Version 4.0.3 (Dec. 23th 2015)

A bugfix release, which fixes the following issues:

- https://github.com/michael-rapp/AndroidPreferenceActivity/issues/12

## Version 4.0.2 (Nov. 6th 2015)

A minor release, which introduces the following changes:

- The view `ElevationShadowView`, which is provided by version 2.1.1 of the library "AndroidUtil", is now used to visualize elevations.

## Version 4.0.1 (Nov. 3rd 2015)

A minor release, which introduces the following changes:

- The default elevation of button bars has been reduced from 6dp to 2dp. Furthermore, the functionality to use parallel illumination for emulating the shadows of elevated views of version 1.2.1 of the library "AndroidUtil" is now used in order to ensure, that the elevation of button bars appears identically to the elevation of bread crumbs. 

## Version 4.0.0 (Oct. 28th 2015)

A major release, which introduces the following changes:

- A customizable elevation has been added to a `PreferenceActivity`'s toolbar on smartphones, as well as on tablet devices.
- The design of the `PreferenceActivity` on tablets has been reworked. A `CardView` is now used to show the currently active fragment. If you prefer the previous style of the activity, you can continue to use the 3.x.x-development branch. Critical bugfixes (not upcoming new features) are planned to be ported back to this branch.  

## Version 3.0.0 (Oct. 22th 2015)

A major release, which introduces the following changes:

- The project has been migrated from the legacy Eclipse ADT folder structure to Android Studio. It now uses the Gradle build system and the library as well as the example app are contained by one single project.
- The library can now be added to Android apps using the Gradle dependency `com.github.michael-rapp:android-preference-activity:3.0.0` (https://github.com/michael-rapp/AndroidPreferenceActivity/issues/7)

## Version 2.2.1 (Sept. 10th 2015)

A bugfix release, which fixes the following issue:

- https://github.com/michael-rapp/AndroidPreferenceActivity/issues/10

## Version 2.2.0 (June 3rd 2015)

A feature release, which introduces the following changes:

- The signatures of the methods of the class `WizardListener` have been changed. Instances of the class `Bundle` are now only used as return values, when the activity is not about to be closed. Additionally, `Bundle` instances, which contain the arguments of the currently shown fragment, are now passed to each method as a parameter.

## Version 2.1.1 (June 1st 2015)

A minor release, which provides the following changes:

- It is now possible to add listeners to the class `PreferenceActivity`, which are notified, when the currently shown preference fragment changes.

## Version 2.1.0 (May 31th 2015)

A feature release, which introduces the following changes:

- It is now possible to pass parameters within a `Bundle` from one fragment of a wizard to an other. Therefore the method signatures of the interface `WizardListener` have been changed.
- Fragment transitions are now properly handled, when multiple instances of the same class are used as the preference headers' fragments.
- Added the layout `R.layout.preference_child`, which can be used as as preference's layout, if a left indent should be added. May be useful for creating hierarchical preference screens together with the `android.dependency` attribute.
- Added a `FrameLayout` to the `PreferenceActivity`. It can be accessed by using the ID `R.id.preference_activity_frame_layout`. Additionally, `getFrameLayout`-methods have been added to allow referencing a `PreferenceActivity`'s, respectively a `PreferenceFragment`'s, frame layout.
- The issue https://github.com/michael-rapp/AndroidPreferenceActivity/issues/9 has been solved. The library now relies on the AppCompat v7 revision 22 support library. Revision 21 is not supported anymore.

## Version 2.0.8 (Apr. 19th 2015)

A bugfix release, which fixes the following issues:

- The `FrameLayout` of a `PreferenceFragment` can now be accessed using the ID `R.id.preference_fragment_frame_layout`.
- API level 22 is now used.

## Version 2.0.7 (Feb. 7th 2015)

A bugfix release, which fixes the following issues:

- https://github.com/michael-rapp/AndroidPreferenceActivity/issues/8

## Version 2.0.6 (Nov. 16th 2014)

A bugfix release, which fixes the following issues:

- Changed the appearance of the dialog's buttons to be identically on Lollipop-devices, as well as on pre-Lollipop devices.

## Version 2.0.5 (Nov. 12th 2014)

A bugfix release, which fixes the following issues:

- https://github.com/michael-rapp/AndroidPreferenceActivity/issues/6

## Version 2.0.4 (Nov. 11th 2014)

A minor release, which provides the following changes:

- The texts of buttons are not bold anymore.

## Version 2.0.3 (Nov. 5th 2014)

A minor release, which provides the following changes:

- Added a dimen resource, which specifies the height of the large toolbar.
- Added the possibility to register a listener, which is notified about a `HideViewOnScrollAnimation`'s internal state.

## Version 2.0.2 (Nov. 5th 2014)

A minor release, which provides the following changes:

- The minimum height and the vertical padding of a preference have been changed.

## Version 2.0.1 (Nov. 5th 2014)

A bugfix release, which fixes the following issues:

- https://github.com/michael-rapp/AndroidPreferenceActivity/issues/4
- https://github.com/michael-rapp/AndroidPreferenceActivity/issues/5

## Version 2.0.0 (Nov. 4th 2014)

A major release, which introduces the following features:

- The UI has been re-designed according to the Android 5 "Material Design" guidelines. To provide Material Design even on pre-Lollipop devices (API level less than 21), the AppCompat v7 revision 21 support library is used.
- The methods to set/retrieve shadow widths and colors have been replaced by according methods, which allow to set/retrieve elevations like used by the Android SDK 21.
- Added style attributes, which allow easier customizing of a `PreferenceFragment`'s appearance from within a theme.
- The button bar, which contains the button, which allows to restore the default values of a `PreferenceFragment`'s preferences, is now animated to become hidden when the user scrolls downwards and to become shown when the user scrolls upwards.

## Version 1.2.1 (Oct. 25th 2014)

A bugfix release, which fixes the following issues:

- https://github.com/michael-rapp/AndroidPreferenceActivity/issues/2
- https://github.com/michael-rapp/AndroidPreferenceActivity/issues/3

## Version 1.2.0 (Oct. 25th 2014)

A feature release, which introduces the following functionalities:

- The interface `RestoreDefaultsListener` does now provide an additional method, which allows to determine whether a single preference's default value should be restored, or not. This functionality replaces the methods to manage a black list and to specify, whether disabled preferences should be restored, which have been offered previously by the class `PreferenceFragment`.
- Added a method to the interface `RestoreDefaultsListener`, which is invoked, when a preference's default value has been restored.

## Version 1.1.2 (Oct. 24th 2014)

A bugfix release, which fixes the following issue:

- https://github.com/michael-rapp/AndroidPreferenceActivity/issues/1

## Version 1.1.1 (Oct. 24th 2014)

A minor release, which provides the following changes:

- Added a black list to the class `PreferenceFragment`, which allows to specify the keys of the preferences, whose default values should not be restored.

## Version 1.1.0 (Oct. 23th 2014)

A feature release, which introduces the following functionalities:

- The library does now provide a class, which is extended from the Android SDK's built-in `PreferenceFragment`. This class allows to show a button, which may be used to restore the default values of the fragment's preferences.

## Version 1.0.1 (Oct. 21th 2014)

A minor release, which provides the following changes:

- Added a public inner class, which implements the interface `android.os.parcelable.Creator` to the class `PreferenceHeader` in order to allow creating instances from a `Parcel`.
- Prepared for Android 5.0 (API level 21).

## Version 1.0.0 (Oct. 19th 2014)

The first stable release, which provides an activity, an alternative implementation of the Android SDK's built-in `PreferenceActivity`. The implementation initially provides the following features:
	
- The activity's navigation allows to show preference headers, which categorize the preferences of a `PreferenceFragment`. Furthermore, regular Fragments can be shown. Besides a title, the preference headers may contain an icon and a summary and it is possible to launch an intent when a header is selected.
- The activity's preference headers can be defined via XML resources, which are compatible to the ones used to initialize the Android SDK's built-in `PreferenceActivity`. Alternatively, the preference headers can be added or removed dynamically at runtime, which causes the current selected preference header to be adapted automatically.
- The activity provides methods, which easily allow to access its child views in order to manipulate their appearance. For the most common manipulations even dedicated methods are provided.
- The library allows to override the behavior of the action bar's back button in order to use it for navigating on devices with a small screen.
- It is possible to launch the activity using an intent, which specifies the preference header, which should be initially selected. Such an intent also allows to hide the navigation.
- By specifying appropriate intent extras, it is also possible to use the activity as a wizard, which provides an alternative navigation, which allows to navigate from one step of the wizard to an other. The navigation can be observed and influenced by implementing and registering an appropriate listener.
- The activity is visually-consistent with Android's built-in `PreferenceActivity`.