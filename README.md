# Google Street View (GSV) database creator
Commandline tool to extract streetview images along a route between two locations

# Requirements
- Java Development Kit (https://www.oracle.com/technetwork/java/javase/overview/index.html)
- IDE (IntelliJ IDEA) (https://www.jetbrains.com/idea/specials/idea/ultimate.html?gclid=EAIaIQobChMIr9GS-cDo4gIVCJ7VCh0anw2bEAAYASAAEgLpvPD_BwE)
- Apache Maven (https://maven.apache.org/) - (*IntelliJ IDEA has in-built support for Maven*)

Comments: [pcl]
- Add instructions on installing jdk in linux using apt-get
- Add information about what is required and what is optional (e.g. if Maven is optional if you have installed IDEA)
- At least for me, maven was not directly installed when installing IntellJ

# Dependencies
This code uses the StreetviewExtractor library (https://github.com/jonhare/StreetviewExtractor)

# Installation and compilation
## Insialling JDK on Ubuntu
		apt-get install default-jdk
		update-alternatives --config java
Next, open the file “/etc/environment” with a text editor

		nano /etc/environment
		
And add the following line at the end of the file:

		JAVA_HOME="/your/java/installation-path"
		
Save the file and then reload it:

		source /etc/environment
		
To test if everything’s done right, you can check your JAVA_HOME variable using:

		echo $JAVA_HOME
		
And the output should be your Java installation path.	
# Compiling the program
It can be compiled using a terminal or via the IDE.
## Using IDEA
To import the project into the IDEA, click on *File -> Open* and then select the directory rrot directory of the repository (where pom.xml is located) and follow the same steps as discussed for the *terminal* by clicking on 'Terminal' at the bottom-right sied of the IDE window.

## Using the Terminal
You need Apache Maven. From a terminal run `mvn package shade:shade` to build an executable jar.
This creates the executable jar under the *target* subfolder. Thus, you can follow the *Usage* section to execute commands. 

# Usage
Comments: [pcl]
- The usage options and the example needs to be updated to the current version. The curresnt example produces a folder with the images, but no json file, and an invalid .mp4 video

You will need a Google API key - get one here: https://console.developers.google.com.
You'll need to enable the "Directions API" and "Street View Image API".

Run the program with `java -jar target/StreetviewExtractor-1.0-SNAPSHOT.jar`


	Usage: java -jar StreetviewExtractor.jar [options...]  		*from IDEA*
	       java -jar target/StreetviewExtractor-1.0-SNAPSHOT.jar    *from terminal after building*
		 --api-key (-a) VAL  : Google API Key
		 --follow-route      : Recompute the heading according to the direction between
				       consecutive points
		 --fov (-f) N        : Field of view.
		 --fov-end N         : the ending field of view value when using a range.
		 --fov-rate N        : the sampling rate/frequency of the field of view value
				       change when using a range
		 --fov-start N       : the starting field of view value when using a range
		 --fpx N             : Number of images per X. If --time-recode is enabled X is
				       seconds; otherwise it is metres.
		 --from VAL          : From coordinates formatted as lat,lng
		 --heading (-he) N   : the heading value.
		 --heading-end N     : the ending heading value when using a range.
		 --heading-rate N    : the sampling rate/frequency of the heading value change
				       when using a range
		 --heading-start N   : the starting heading value when using a range
		 --height (-h) N     : Image height.
		 --mode VAL          : Mode of route
		 --output (-o) VAL   : Output geojson file; the .json extension will be added
				       if it's not present
		 --pitch (-p) N      : the pitch value.
		 --pitch-end N       : the ending pitch value when using a range.
		 --pitch-rate N      : the sampling rate/frequency of the pitch value change
				       when using a range
		 --pitch-start N     : the starting pitch value when using a range
		 --single            : indicates if the query is for a single location, not a
				       route
		 --time-recode       : Recode the path based on the time of each segment; the
				       images will be further apart when moving faster
		 --to VAL            : To coordinates formatted as lat,lng
		 --width (-w) N      : Image width.
		 --write-images (-i) : Output the images of the route.
		 --write-video (-v)  : Output a video of the route.

Example:

	java -jar target/StreetviewExtractor-1.0-SNAPSHOT.jar --from 40.631538,-73.965327 --to 40.691099,-73.991785 -i -o test.json -a <your_api_key> 


**Database Structure:**

The proposed database structure is using JSON format to store the data. Thus, we will have a root _JSON Object_ and we will store the global parameters in a route as _key-value pair_ in the root _JSON Object_. These parameters are:

- _From:_ identified by the key _&#39;from&#39;_ and has the string value of the origin latitude and longitude concatenated by comma.
- _To:_ identified by the key _&#39;to&#39;_ and has the string value of the destination latitude and longitude concatenated by comma.
- _Heading:_ identified by the key _&#39;heading&#39;_ and has an integer value of heading used on the query of the Google Street View API.
- _Pitch:_ identified by the key _&#39;pitch&#39;_ and has the an integer value of the pitch used on the query of the Google Street View API.
- _Field of View:_ identified by the key _&#39;fov&#39;_ and has an integer value of the field of view used on the query of the Google Street View API.
- _Frames per X:_ identified by the key _&#39;fpx&#39;_ and has a real value of the frames per meter or frames per second used.
- _Width:_ identified by the key _&#39;width&#39;_ and has a integer value of the width of the image stored.
- _Height:_ identified by the key _&#39;height&#39;_ and has a integer value of the height of the image stored.

Next thing to store is the sequence of all the images/waypoints found in the route. For this, we will use _JSON Array_ that will be part of the root _JSON Object_. Inside this _JSON Array_, there will be as many _JSON Objects_ as the images or waypoints stored. The key of this array will be _&#39;images&#39;_ and and four parameters unique to each waypoint will be stored in each element of the array. The parameters are:
- _Sequence Number:_ identified by the key _&#39;seqNumber&#39;_ and has an integer value of a number between 00000 and 99999 that will identify the waypoint/image.
- _Latitude:_ identified by the key _&#39;lat&#39;_ and has a real value of the latitude of the location.
- _Longitude:_ identified by the key _&#39;lng&#39;_ and has a real value of the longitude of the location.
- _Heading_: identified by the key &#39;heading&#39; and has a real value of the heading used in the Google Street View API for this particular waypoint.

**File System Structure:**

For every route, we will have a folder named as &#39; **A,B\_Y,Z**&#39;, where:

- A and B are the latitude and longitude of the origin respectively; and
- Y and Z are the latitude and longitude of the destination respectively.

Furthermore, inside every route folder, we will have a folder having images of a specific configuration of the parameter values. Thus, it will named as &#39; **H:{HD}\_P:{P}\_FOV:{F}\_M:{M}\_S:{W}x{H}-jpegs**&#39; where:

- **{HD}**: is the heading value
- **{P}**: is the pitch value
- **{F}**: is the field of view value
- **{M}**: is the mode of the direction
- **{W}**: is the width of the images
- **{H}**: is the height of the images
And inside the folder, the images will be named as **X.jpg** where X is a number ranging from 00000 to possibly 99999.
