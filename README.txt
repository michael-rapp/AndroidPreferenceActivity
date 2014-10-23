AndroidSeekBarPreference - README
=================================


About
-----

"AndroidPreferenceActivity" is an Android-library, which provides an alternative implementation of
the Android SDK's built-in PreferenceActivity. Such an activity provides a visualization of 
categorized preferences. The appearance of the activity varies depending on the size of the device's
display. On devices with small screens, e.g. on smartphones, the navigation is designed to use the 
whole available space and selecting an item causes the corresponding preferences to be shown full 
screen as well. On devices with large screens, e.g. on tablets, the navigation and the preferences 
of the currently selected item are shown split screen instead. Although the library's API is 
designed to be similar to the API of the Android SDK's PreferenceActivity, it provides additional
possibilities, beyond the functionalities of the original. This includes easier adaption of the
activity's appearance, as well as the possibility to use the activity as a wizard and to add or
remove the activity's preference headers dynamically at runtime. 

The library provides the following features:

    -   The activity's navigation allows to show preference headers, which categorize the
        preferences of a PreferenceFragment. Furthermore, regular fragments can be shown.
        Besides a title, the preference headers may contain an icon and a summary and it
        is possible to launch an intent when a header is selected.
        
    -   The activity's preference headers can be defined via XML resources, which are compatible 
        to the ones used to initialize the Android SDK's built-in PreferenceActivity. 
        Alternatively, the preference headers can be added or removed dynamically at runtime, 
        which causes the current selected preference header to be adapted automatically.
        
    -   The activity provides methods, which easily allow to access its child views in
        order to manipulate their appearance. For the most common manipulations even 
        dedicated methods exist.
        
    -   The library allows to override the behavior of the action bar's back button in
        order to use it for navigating on devices with a small screen.
    
    -   It is possible to launch the activity using an intent, which specifies the preference
        header, which should be initially selected. Such an intent also allows to hide the
        navigation.
        
    -   By specifying appropriate intent extras, it is also possible to use the activity as a
        wizard, which provides an alternative navigation, which allows to navigate from one
        step of the wizard to an other. The navigation can be observed and influenced by 
        implementing and registering an appropriate listener.
        
    -   The library provides a class, which is extended from the Android SDK's built-in 
        PreferenceFragment. This class allows to show a button, which may be
        used to restore the default values of the fragment's preferences.

	
License Agreement
-----------------

AndroidPreferenceActivity is distributed under the GNU Lesser Public License version 3.0 (GLPLv3 ). For 
further information about this license agreement's content, please refer to its full version, which is 
available at http://www.gnu.org/licenses/.


Download
--------

The project homepage of the project "AndroidPreferenceActivity" is available on Sourceforge via 
the internet address https://sourceforge.net/projects/androidpreferenceactivity.

The latest release of the project can be downloaded as a zip archive from the download 
section of the Sourceforge project site mentioned above, which is available via the direct 
link https://sourceforge.net/projects/androidpreferenceactivity/files.

As well, the complete source code and documentation is available via a Mercurial repository, 
which can be accessed by the URL http://hg.code.sf.net/p/androidpreferenceactivity/code.


Contact information
-------------------

For personal feedback or questions you can either contact the developer via his profile on 
Sourceforge, which is available under the direct link https://sourceforge.net/users/mrapp, or 
via the email address michael.rapp90@googlemail.com.