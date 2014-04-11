![Banner](https://raw.githubusercontent.com/AndroidGears/Resources/master/Images/AndroidGearsBanner.png)

## Android Gears - Android Studio Plugin

Android Gears is a new <b>dependency management system</b> built on open source technology through Github. Gears is fully integrated with the Gradle build processes of Android Studio and IntelliJ. In addition to providing a robust search interface for library discovery, Gears greatly reduces the effort it takes to share your library with others. Listing your library with Android Gears is just a pull request away.

##Installation

- Download and install Android Studio
- Download a release and unzip to get the "Android Gears" plugin folder
- With th IDE closed, got to Applications -> Android Studio, right click on the icon and select "Show Package Contents". You should see a plugin folder in the package contents
- Drag the Android Gears folder into the the plugin folder to install Android Gears.
- Open Android Studio


#Usage
- To manage your Android Gears, click on Tools -> Android Gears -> Manage Android Gears
- Search for and install a library. Colours is stable right now as well as Joda Time and the support libraries
- Once you have installed all the gears, click on "Done"
- Now, right click on your project in the project navigator and select "Synchronize <Project Name>"
- Now, open up your main app module's build.gradle file and select "Sync Now" in the top right of your screen. This will be in a yellow strip across the top of your screen. If it is not there, try adding some new lines to the file and it should pop up.
- All done! You should now be able to use the code from the gears you selected.
