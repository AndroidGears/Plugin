![Banner](https://raw.githubusercontent.com/AndroidGears/Resources/master/Images/AndroidGearsBanner.png)

## Android Gears - Android Studio Plugin

Android Gears is a new <b>dependency management system</b> built on open source technology through Github. Gears is fully integrated with the Gradle build processes of Android Studio and IntelliJ. In addition to providing a robust search interface for library discovery, Gears greatly reduces the effort it takes to share your library with others. Listing your library with Android Gears is just a pull request away.

An example of searching for, downloading and using an Android library in your project in **under 15 seconds**.

![ColoursInstallDemo](https://raw.githubusercontent.com/AndroidGears/Resources/master/Screenshots/ColoursInstallDemo.gif)

## Table of Contents

* [**Installation**](#installation)
* [**Basic Usage**](#basic-usage)
* [**Core Concepts**](#core-concepts)
  * [Gears](#gears)
  * [The Specs Repository](#the-specs-repository)
  * [Accessing the Specs Repository](#accessing-the-specs-repository)
  * [Gitignore Considerations and Working in Teams](#gitignore-considerations-and-working-in-teams)
* [**Android Studio Plugin**](#android-studio-plugin)
  * [Managing Android Gears](#managing-android-gears)
  * [Creating a Gear Spec](#creating-a-gear-spec)
  * [Linting a Gear Spec](#linting-a-gear-spec)
  * [Settings](#settings) 
* [**Adding Your Library to Android Gears**](#adding-your-library-to-android-gears)
  * [Packaging Your Library](#packaging-your-library)
  * [Pull Requests](pull-requests)
* [**Future Enchancements**](#future-enhancements)
* [**Credits**](#credits)
* [**License**](#license)


##Installation

- Download and install (and Update) [Android Studio](http://developer.android.com/sdk/installing/studio.html)
- [Download latest Android Gears release](https://github.com/AndroidGears/Plugin/releases)

**Mac (OSX 10.9.2 & 10.9.1)**
- Open Android Studio
- Go To Android Studio > Preferences... ( Command+,) > Go to "Plugins" under IDE Settings > Click Install plugin from disk... > find the Android Gears latest release on your computer. Click "OK" and restart your IDE.
- And you are DONE!!
- Time to go get some [GEARS!](#basic-usage)

**Windows (Win8 tested) & Linux (Ubuntu 14.04 tested)**
- Open Android Studio
- Go To File > Settings... ( Ctrl+Alt+S ) > Go to "Plugins" under IDE Settings > Click Install plugin from disk... > find the Android Gears latest release on your computer. Click "OK" and restart your IDE.
- And you are DONE!!
- Time to go get some [GEARS!](#basic-usage)


( Android Gears also tested in Community version of Intellij )

![InstallDemo1](https://raw.githubusercontent.com/AndroidGears/Resources/master/Screenshots/Installing/Android-Gears-Install-Steps.png)

(**Note For updating plugin**) 
When updating first Navigate back to Plugins in IDE then uninstall plugin first then go through installation process again to get the plugin to update correctly.


Finally, to access the Android Gears menu, navigate to Tools -> Android Gears (Win&Linux-Crtl+Alt+Shift+G , M)(OSX - Command+Alt+Shift+G,M)

![ToolsMenuScreenshot](https://raw.githubusercontent.com/AndroidGears/Resources/master/Screenshots/MenuScreenshot.png)

##Basic Usage
- To manage your Android Gears, click on Tools -> Android Gears -> Manage Android Gears
- Search for and install a library.
- Once you have installed all the gears, click "Done"
- All done! You should now be able to use the code from the gears you selected.

##Core Concepts

Before getting started with Android Gears, you may find it helpful to gain a better understanding of how things work "under the hood". This section will detail some core terminology that helps make sense of the Gears system.

####Gears

A <b>Gear</b> is simply another name for an Android or Java library. Gears come in two well-known flavors, JARs and modules. Both are downloaded, installed and maintained in the same way by the Android Gears plugin. Gears will show up in the root of your project folder with JARs and Modules stored in separate folders for easy access.

![GearsDirectoryScreenshot](https://raw.githubusercontent.com/AndroidGears/Resources/master/Screenshots/GearsFolderScreenshotAnnotated.png)

Both will be available to view in your project immediately after installing them and synchronizing your project.

![GearsProjectDirectoryScreenshot](https://raw.githubusercontent.com/AndroidGears/Resources/master/Screenshots/GearsProjectFolderScreenshot.png)

####The Specs Repository

<b>The Specs Repository</b> is a [**Github repository**](https://github.com/AndroidGears/Specs) owned by the Android Gears organization. It houses all the metadata on Gears that are available for download through the Android Gears plugin. The repository is laid out with the following rules:

* The repository root contains only directories whose names are the individual libraries available through Android Gears
* The library directory contains only directories representing available gear versions with [semantic versioning](http://en.wikipedia.org/wiki/Software_versioning#Semantic_versioning) notation (i.e. 1.0.0, 1.0.1, etc)
* Each version folder contains a single file by the extension <code>.gearspec</code> with the name of the file being the library name (i.e. "Colours.gearspec")
* Each .gearspec file contains a simple JSON packet of information with information about the library and its version.

Below is an annotated screenshot of a cloned copy of the specs repository:

![SpecsRepository](https://raw.githubusercontent.com/AndroidGears/Resources/master/Screenshots/SpecsRepositoryScreenshot.png)

####Gearspec Files

A <code>.gearspec</code> file is a text file containing a JSON packet with information about a library and its version. These files are the fundamental unit of the repository and let the Android Gears plugin know about a specific version number. Information includes the library <b>name</b>, <b>version number</b>, <b>location for download</b>, <b>release notes for the specific version</b> and a host of other data. The contents of a sample .gearspec file can be found below:

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

####Accessing the Specs Repository

You may ask the question, <b>"How does the Android Gears plugin communicate with the Specs Repository?"</b> Whenever Android Gears opens for the first time, it clones down a copy of the Specs Repository (typically a very small download) to your user directory. Once the initial clone is complete, any updates to the specs repository on Github are automatically pulled down whenever you open your IDE giving you access to the latest available Gears.

####Gitignore Considerations and Working in Teams

[Recall](#gears) that all the libraries you download through Android Gears are housed in a special directory in your project folder called "Gears". **To keep the size of your repository as small as possible**, this folder is automatically ignored by creating an entry in your .gitignore file. To keep track of what gears are included in the project, another file <code>GearSpecRegister</code> is created that contains a list of all Gears associated with a given project. This helps those working in teams by **declaring** dependencies instead of including them in the repository. 

A great example of this system's utility is when someone inherits a mature project using Android Gears. This person may checkout the repository and then sync (that is, download and install) the Gears declared in the <code>GearSpecRegister</code>. All of this is streamlined through the [Manage Android Gears](#managing-android-gears) portion of the plugin, and is a simple as clicking a button. 

*Note:** There is no need to actually open the <code>GearSpecRegister</code> file, but if you do, you will find a simple array of Gear Specs. If there is ever a conflict in this file due to more than one team member manipulating it, resolving the conflict is as easy as shuffling a few JSON objects around. 

##Android Studio Plugin

The Android Gears plugin for Android Studio and IntelliJ is the engine that makes Android Gears work. It coordinates with the Specs Repository and your project to ensure that managing libraries is as painless as possible. This section will cover the basics of the plugin including how to install and uninstall Gears, as well as finer points like auto-syncing Gears across teams.

####Managing Android Gears

To manage the Android Gears for a given project, select Tools -> Android Gears -> Manage Android Gears.

####Creating a Gear Spec

Gear Specs is a text file that holds information about an Android library listed in Android Gears. The Android Gears plugin for Android Studio comes with a GUI for creating a Gear Spec that may be pulled into the Specs Repository. Use this tool to create a listing for your library. For more information about the process, see [**Adding Your Library to Android Gears**](#adding-your-library-to-android-gears).

When using GUI First go to Tools > Android Gears > Create GearSpec
![CreateGearSpecMenu](https://raw.githubusercontent.com/AndroidGears/Resources/master/Screenshots/CreateGearMenu.png)

Then fill out form as best you can and we will tell you if you have done it correctly. 

![CreateGearSpec](https://raw.githubusercontent.com/AndroidGears/Resources/master/Screenshots/CreateGearScreen.png)

Things to take into consideration. 
* All fields must be filled out unless you are pointing your Url Source directly to a .jar file. Then "Tag" and "Location" are to be left blank.
* Dependencies for your libraries must already be gearspecs in the Specs Repository.
* A Gearspec can have more then one Author but you must have at least one author.
* Each Author must have a name and email address.
* A library's version must be [semantic versioning](http://en.wikipedia.org/wiki/Software_versioning#Semantic_versioning)
* Use tags to create greater visibility for your library. 
* When creating tag use commas to seprate out each tag.
* When you are searching for your Gear after it has been added to the Specs Repository it is a full text search so Author name and project name are not required to be in the tags as well they are already part of the search algorithm.
* Source must be a valid .git repository or .jar file. See the [Packaging](#packaging) section for a complete list of your options.
* The homepage is the face or your library. Link it to a site that looks good and/or has alot of detailed information about your library. The library's Github page is a wonderful place to start.
* If you have already created a gearspec and would like to edit it and reupload it. Use the "Load Android Gear Spec" button in the bottom left corner for quick and easy filler and editing.
* To remove authors or dependencies just click on the the listing in the table and then click on the - Remove Author or Dependency button below the selected table.
* Dependency addition requires both fields to be filled. These need to be the name and version of a valid gearspec in the specs repo.
* License is the **name** of the License not the entire license text.
* Click "Create Android Gear Spec" in the bottom right corner. This will summon the Android Gear Spec Linter. If your spec makes it past the linter you are ready to create a pull request to the Android Gears Specs Repository and add your library to our ever growing community. 
* HAPPY CODING!

####Linting a Gear Spec

The plugin also has a built-in linting tool for making sure your <code>.gearspec</code> file is valid before issuing a pull request to the Specs Repository. This tool will tell you if there is anything wrong. This same linting tool is used by the "Create Gear Spec" mechanism.

![LintGearSpec](https://raw.githubusercontent.com/AndroidGears/Resources/master/Screenshots/LintGearSpecScreenshot.png)

####Settings

##Adding Your Library to Android Gears

So you have created your shiny, new Android library and you want to make it available to all the world through Android Gears! Adding your library is as simple as initiating a properly formatted pull request to the [Specs Repository](https://github.com/AndroidGears/Specs). At the high level, the entire process is as follows:

* Create a Module or JAR (henceforth "Gear") you would like to share through Android Gears
* House the Gear online in one or more ways covered in the [Packaging](#packaging-your-library) section below
* Create a <code>.gearspec</code> file through the Plugin or elsewhere that contains the Gear's metadata. See [here](#gearspec-files) for format.
* Fork the Android Gears [Specs Repository](https://github.com/AndroidGears/Specs)
* Add your project and/or version to your forked repository
* Initiate a pull-request to the Android Gears Specs master repository
* Your new spec will be reviewed for integrity and, when approved, immediately allow your library to be accessed through the Android Gears plugin

####Packaging Your Library

Your code must be stored somewhere online to be available for download through Android Gears. A few options are available with small variations. It is recommended that you use the [Create Gear Spec](creating-a-gear-spec) portion of the Android Gears plugin, but, as this simply outputs a text file with JSON inside, you may also use your favorite text editor.

**Method 1 - Module in Git**

The first option is housing a module in a git repository on Github. If this is the method you have chosen, the source object of your <code>.gearspec</code> file might look something like the one from the [Colours Library](https://github.com/MatthewYork/Colours):

```json
"source": {
    "url": "https://github.com/MatthewYork/Colours.git",
    "tag": "v1.0.0",
    "source_files": "ColoursLibrary"
  }
```

Here, a url to the git repository is given as well as the tag associated with that specific release of Colours. Also, a directory "ColoursLibrary" is listed, meaning that the module is housed in a folder name "ColoursLibrary" at the root of the repository.

<b>*Note</b>  When using modules, please ensure that all build variables in <code>build.gradle</code> are local in scope to the specified library path listed in <code>source_files</code>. If they are defined in the directory above in, say, the <code>gradle.properties</code> file, then they will be undefined when your library is retreived through Android Gears. This is because Android Gears <b>only uses the directory specified</b> in the <code>source_files</code> portion of the source object.

**Method 2 - JAR in Git**

The second way of housing a Gear is as a JAR file versioned through Git. Like method 1, you have a repository url and tag, but this time the <code>source_files</code> field contains the path to a JAR.

```json
"source": {
    "url": "https://github.com/AndroidGears/JodaTime.git",
    "tag": "v2.3.0",
    "source_files": "joda-time-2.3.jar"
  }
```

**Method 3 - Static JAR**

The final way of housing Gear is through a static JAR file. This choice is the only one that allows disassociation from a parent git repository. An example of this source might look something like this.

```json
"source": {
    "url": "http://mirrors.ibiblio.org/pub/mirrors/maven2/net/objectlab/kit/datecalc/datecalc-joda/1.0.1/datecalc-joda-1.0.1.jar",
    "tag": null,
    "source_files": null
  }
```

####Pull Requests

Pull requests to the Specs master repo will be reviewed and linted so as not to harm the integrity of the Specs repository. To avoid having your request denied, please use the linting and/or creation tools available in the plugin. We will review all requests as FIFO as possible and do our best to ensure the process is quick. If you would like to help become a reviewer, please contact gearshelp@gmail.com

##Future Enhancements

We envision Android Gears to be a complete solution for managing dependencies in Android apps. As such, there are many enhancements that we would like to see in the future. **We hope for much community involvement in the Android Gears project. Your pull requests and feature suggestions are welcome.** Here are just a few things we have in mind.

* Creating **separate Gear areas** for debug, release and test run configurations (Better lifecycle management).
* **Better dependency resolution** for projects involving many dependencies of potentially conflicting version numbers. Right now, only basic management is done. For instance, if two libs are dependent on the same lib of different versions, how does this get handled effectively? That is an open question we would like to discuss.
* **Icons** for the menu items. Right now there is a "gears.png" icon that we cannot get recognized in the plugin.xml. Any help here would be great.

##Credits

[**Matthew York**](https://github.com/MatthewYork) - Creator of Android Gears, Senior iOS and Android Developer for the [Center for Advanced Public Safety](https://github.com/uacaps)

[**Aaron Fleshner**](https://github.com/adfleshner) - Creator of Android Gears, Senior Android Developer for the [Center for Advanced Public Safety](https://github.com/uacaps)

[**Ben Gordon**](https://github.com/bennyguitar) - Senior iOS Developer at [Intermark](https://github.com/Intermark) and creator of [androidgears.org](http://androidgears.org)

[**CocoaPods.org**](http://cocoapods.org) - The iOS dependency manager which much of the structure for Android Gears is based on.

[**JGit**](http://www.eclipse.org/jgit/) - The eclipse foundation project that handles git functionality in Android Gears

[**Java Swing**](http://en.wikipedia.org/wiki/Swing_(Java)) - The wonderful GUI library that created all of the spectacular forms you see in Android Gears.

I would also like to thank **God** through whom all things live and move and have their being. [Acts 17:28](http://www.biblegateway.com/passage/?search=Acts+17%3A16-34&version=NIV)

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
