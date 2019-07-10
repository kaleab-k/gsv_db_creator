# Google Street View (GSV) database creator
Commandline tool to extract streetview images along a route between two locations

# Requirements
- Java Development Kit (https://www.oracle.com/technetwork/java/javase/overview/index.html)
- IDE (IntelliJ IDEA) (https://www.jetbrains.com/idea/specials/idea/ultimate.html?gclid=EAIaIQobChMIr9GS-cDo4gIVCJ7VCh0anw2bEAAYASAAEgLpvPD_BwE)
- Apache Maven (https://maven.apache.org/) - (*IntelliJ IDEA has in-built support for Maven*)

Comments: [pcl]
- At least for me, maven was not directly installed when installing IntellJ IDEA

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
To import the project into IDEA, click on *File -> Open* and select the root directory of the repository (where pom.xml is located). Clicking on 'Terminal' at the bottom-right sied of the IDE window and follow the same steps as on the *Using the Terminal* section.

## Using the Terminal
You need Apache Maven. From a terminal run `mvn package shade:shade` to build an executable jar.
This creates the executable jar under the *target* subfolder. You can ow follow the *Usage* section steps to execute commands. 

# Usage
Comments: [pcl]
- what are the differences between VAL and N? VAL is a string and N is a number?
- which are the possible values for MODE?
- Indicate default values, and possible values (ranges)
- For fov, heading and pitch, indicate which tag enables the range mode
- fov-rate (and others) is really a step, right? not a rate. Modify the names and descriptions

You will need a Google API key - get one here: https://console.developers.google.com.
You will also need to enable the "Directions API" and "Street View Image API" for your Google Cloud account.

Run the program with `java -jar target/StreetviewExtractor-1.0-SNAPSHOT.jar`


	Usage: java -jar StreetviewExtractor.jar [options...]  		*from IDEA*
	       java -jar target/StreetviewExtractor-1.0-SNAPSHOT.jar    *from terminal after building*
		 
		 --api-key (-a) VAL  : Google API Key
		 
		 --single            : indicates single location mode, not a route
		 --from VAL          : FROM coordinates formatted as lat,lng. Location for single
		 		       location mode (--single)
		 --to VAL            : TO coordinates formatted as lat,lng
		 --follow-route      : Heading value is relative to the direction between
				       consecutive points in the route. If not present, 
				       heading value is absolute.
		 --fpx N             : Number of images per X. If --time-recode is enabled X is
				       seconds; otherwise it is metres.
		 --time-recode       : Recode the path based on the time of each segment; the
				       images will be further apart when moving faster
		 --mode VAL          : Route mode
		 
		 --height (-h) N     : Image height
		 --width (-w) N      : Image width
				       
		 --fov (-f) N        : Horizontal field of view
		 --fov-start N       : starting fov value, when using a range
		 --fov-end N         : ending fov value when using a range.
		 --fov-rate N        : sampling rate of the fov values when using a range
	 
		 --heading (-he) N   : heading value.
		 --heading-start N   : starting heading value when using a range
		 --heading-end N     : ending heading value when using a range.
		 --heading-rate N    : sampling rate of the heading values when using a range
				       
		 --pitch (-p) N      : pitch value
		 --pitch-start N     : starting pitch value when using a range
		 --pitch-end N       : ending pitch value when using a range
		 --pitch-rate N      : sampling rate of the pitch values when using a range

		 --output (-o) VAL   : Output geojson file; the .json extension will be added
				       if it's not present
		 
		 --write-images (-i) : Output the images of the route.
		 --write-video (-v)  : Output a video of the route.

Comments: [pcl]
- Add different examples and comment cases: e.g. 
	- Single location and varying heading and pitch
	- Route with absolute heading value
	- Route with relative heading value

Examples:

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
