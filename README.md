![Banner](https://raw.githubusercontent.com/AndroidGears/Resources/master/Images/AndroidGearsBanner.png)

## Android Gears - Android Studio Plugin

Android Gears is a new <b>dependency management system</b> built on open source technology through Github. Gears is fully integrated with the Gradle build processes of Android Studio and IntelliJ. In addition to providing a robust search interface for library discovery, Gears greatly reduces the effort it takes to share your library with others. Listing your library with Android Gears is just a pull request away.

## Table of Contents

* [**Installation**](#installation)
* [**Basic Usage**](#basic-usage)
* [**Core Concepts**](#core-concepts)
  * [Gears](#gears)
  * [The Specs Repository](#the-specs-repository)
* [**Android Studio Plugin**](#android-studio-plugin)
  * [Managing Android Gears](#managing-android-gears)
  * [.gitignore Considerations](#.gitignore-considerations)
  * [Creating a Gear Spec](#creating-a-gear-spec)
  * [Linting a Gear Spec](#linting-a-gear-spec)
  * [Settings](#settings) 
* [**Adding Your Library to Android Gears**](#adding-your-library-to-android-gears)
  * [Packaging](#packaging)
  * [Pull Requests](pull-requests)
* [**Credits**](#credits)
* [**License**](#license)


##Installation

- Download and install Android Studio
- Download a release and unzip to get the "Android Gears" plugin folder
- With th IDE closed, got to Applications -> Android Studio, right click on the icon and select "Show Package Contents". You should see a plugin folder in the package contents
- Drag the Android Gears folder into the the plugin folder to install Android Gears.
- Open Android Studio


##Basic Usage
- To manage your Android Gears, click on Tools -> Android Gears -> Manage Android Gears
- Search for and install a library. Colours is stable right now as well as Joda Time and the support libraries
- Once you have installed all the gears, click on "Done"
- Now, right click on your project in the project navigator and select "Synchronize <Project Name>"
- Now, open up your main app module's build.gradle file and select "Sync Now" in the top right of your screen. This will be in a yellow strip across the top of your screen. If it is not there, try adding some new lines to the file and it should pop up.
- All done! You should now be able to use the code from the gears you selected.

##Android Studio Plugin

####Managing Android Gears

####.gitignore Considerations

####Creating a Gear Spec

####Linting a Gear Spec

####Settings

##Under the Hood

Before getting started with Android Gears, you may find it helpful to gain a better understanding of how things work "under the hood". This section will detail some core terminology that helps make sense of the Gears system.

####Gears

A <b>Gear</b> is simply another name for an Android or Java library. Gears come in two well-known flavors, JARs and modules. Both are downloaded, installed and maintained in the same way by the Android Gears plugin. Gears will show up in the root of your project folder with JARs and Modules stored in separate folders for easy access. Both will be available to view in your project file immediately after installing them and synchronizing your project.

####The Specs Repository

<b>The Specs Repository</b> is a [**Github repository**](https://github.com/AndroidGears/Specs) owned by the Android Gears organization. It houses all the metadata on Gears that are available for download through the Android Gears plugin. The repository is laid out with the following rules:

* The repository root contains only directories whose names are the individual libraries available through Android Gears
* The library directory contains only directories representing available gear versions with simple dot notation (i.e. 1.0.0, 1.0.1, etc)
* Each version folder contains a single file by the extension <code>.gearspec</code> with the name of the file being the library name (i.e. "Colours.gearspec")
* Each .gearspec file contains a simple JSON packet of information with information about the library and its version.\

####.gearspec Files

A <code>.gearspec</code> file is a text file containing a JSON packet with information about the library and its version. These files are the fundamental unit of the repository and let the Android Gears plugin know about a specific version number. Information includes the library <b>name</b>, <b>version number</b>, <b>location for download</b>, <b>release notes for the specific version</b> and a host of other data. The contents of a sample .gearspec file can be found below:

```json
{
  "name": "Colours",
  "summary": "A beautiful set of predefined colors and a set of color methods to make your Android development life easier.",
  "realease_notes":"The initial release of Colours for Android",
  "version": "1.0.0",
  "type": "module",
  "copyright": "Matthew York 2014",
  "homepage": "https://github.com/MatthewYork/Colours",
  "license": "MIT",
  "authors": [
    {
      "name": "Matthew York",
      "email": "myork@cs.ua.edu"
    }
  ],
  "minimum_api": 9,
  "source": {
    "url": "https://github.com/MatthewYork/Colours.git",
    "tag": "v1.0.0",
    "source_files": "ColoursLibrary"
  },
  "dependencies": null,
  "tags": [
    "color",
    "colour",
    "utility",
    "hsb",
    "rgb",
    "scheme"
  ]
}

```

You can use your favorite text editor like Sublime Text or Atom to create a .gearspec, or you can use the GUI based [.gearspec creator](#creating-a-gear-spec) available as part of the Android Gears plugin.

##Adding Your Library to Android Gears

So you've got your brand new Android library and you want to make it available to the world through Android Gears! Adding your library is 

####Packaging

####Pull Requests

##Credits

##License

The MIT License (MIT)

Copyright (c) 2014 Matthew York and Aaron Fleshner

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
