## Google Street View (GSV) database creator
Commandline tool to extract streetview images along a route between two locations

# Requirements

Required:
- Java Development Kit (https://www.oracle.com/technetwork/java/javase/overview/index.html)
- Apache Maven (https://maven.apache.org/) 

Recommended: 
- IDE (IntelliJ IDEA) (https://www.jetbrains.com/idea/specials/idea/ultimate.html?gclid=EAIaIQobChMIr9GS-cDo4gIVCJ7VCh0anw2bEAAYASAAEgLpvPD_BwE) (*IntelliJ IDEA has built-in support for Maven*)


# Dependencies
This code uses and modifies the StreetviewExtractor library (https://github.com/jonhare/StreetviewExtractor)

# Installation and compilation

## Installing JDK on Ubuntu
		apt-get install default-jdk
		update-alternatives --config java
Next, open the file “/etc/environment” with a text editor

		nano /etc/environment
		
And add the following line at the end of the file:

		JAVA_HOME="/your/java/installation-path"  #for example: /usr/lib/jvm/jdk-10.0.2
		
Save the file and then reload it:

		source /etc/environment
		
To test if everything’s done right, you can check your JAVA_HOME variable using:

		echo $JAVA_HOME
		
And the output should be your Java installation path.

## Installing Maven (only needed if IntelliJ IDEA is not used)
1: Download apache-maven-3.6.0-bin.tar.gz binary archive from this official link: Download Apache Maven. You need to replace the version number by whatever the version you are downloading.

2: Open the Terminal and move to the /opt directory.

	cd /opt
3: Extract the apache-maven archive into the opt directory.

	sudo tar -xvzf ~/Downloads/apache-maven-3.6.0-bin.tar.gz
4: Edit the _/etc/environment_ file and add the following environment variable:

	M2_HOME="/opt/apache-maven-3.6.0"
also, append the bin directory to the PATH variable: _/opt/apache-maven-3.6.0/bin_
You can use nano to edit the file in the terminal itself. Execute the following command and modify the content as given below.

	sudo nano /etc/environment

WARNING: Do not replace your environment file with the following content because you may already have different environment variables which are required by other applications to function properly. Notice the end of PATH variable and the M2_HOME variable.

	PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/usr/lib/jvm/jdk-10.0.2/bin:/opt/apache-maven-3.6.0/bin"
	M2_HOME="/opt/apache-maven-3.6.0"

After the modification, press _Ctrl + O_ to save the changes and _Ctrl + X_ to exit nano.

5: Update the mvn command:

	sudo update-alternatives --install "/usr/bin/mvn" "mvn" "/opt/apache-maven-3.6.0/bin/mvn" 0
	sudo update-alternatives --set mvn /opt/apache-maven-3.6.0/bin/mvn
Step 7: Logout and login to the computer and check the Maven version using the following command.
	
	mvn --version

# Compiling the program
It can be compiled using a terminal or via the IDE.

## Using IDEA
To import the project into IDEA, click on *File -> Open* and select the root directory of the repository (where pom.xml is located). Clicking on 'Terminal' at the bottom-right sied of the IDE window and follow the same steps as on the *Using the Terminal* section.

## Using the Terminal
You need Apache Maven. From a terminal run `mvn install` and `mvn package shade:shade` to build an executable jar.
This creates the executable jar under the *target* subfolder. You can follow the *Usage* section steps to execute commands. 

# Usage

You will need a Google API key - get one here: https://console.developers.google.com.
You will also need to enable the "Directions API" and "Street View Image API" for your Google Cloud account.

Run the program with `java -jar target/StreetviewExtractor-1.0-SNAPSHOT.jar`


	Usage: java -jar StreetviewExtractor.jar [OPTIONS]  			  *from IDEA*
	       java -jar target/StreetviewExtractor-1.0-SNAPSHOT.jar [OPTIONS]    *from terminal after building*
		
		OPTIONS:
		
		 --api-key (-a) VAL  : Google API Key
		 
		 --single            : indicates single location mode, not a route. Default: false
		 --from VAL          : FROM coordinates formatted as lat,lng. Location for single
		 		       location mode (--single)
		 --to VAL            : TO coordinates formatted as lat,lng
		 --follow-route      : Heading value is relative to the direction between consecutive
		 		       points in the route. If not present, heading value is absolute.
				       Default: false 
		 --fpx N             : Number of images per X. If --time-recode is enabled X is
				       seconds; otherwise it is metres. Default: 0.1
		 --time-recode       : Recode the path based on the time of each segment; the
				       images will be further apart when moving faster. Default: false
		 --mode VAL          : Route mode. Options: [DRIVING, WALKING, BICYCLING]
		 		       further details: https://developers.google.com/maps/documentation/directions/intro#TravelModes
				       Default: DRIVING
		 --height (-h) N     : Image height. Default: 600
		 --width (-w) N      : Image width. Default: 300
				       
		 --fov (-f) N        : Horizontal field of view. Default: 90
		 --fov-start N       : starting fov value, when using a range 
		 --fov-end N         : ending fov value when using a range.
		 --fov-step N        : sampling step of the fov values when using a range
	 
		 --heading (-he) N   : heading value. Default: 0
		 --heading-start N   : starting heading value when using a range
		 --heading-end N     : ending heading value when using a range.
		 --heading-step N    : sampling step of the heading values when using a range
				       
		 --pitch (-p) N      : pitch value. Default: 0
		 --pitch-start N     : starting pitch value when using a range
		 --pitch-end N       : ending pitch value when using a range
		 --pitch-step N      : sampling step of the pitch values when using a range

		 --output (-o) VAL   : Output geojson file; the .json extension will be added
				       if it's not present
		 
		 --write-images (-i) : Output the images of the route.
		 --write-video (-v)  : Output a video of the route.
		 
		N.B: 1. VAL indicates a string value. N indicates a number.
		     2. The range modes are activated for fov, pitch or heading if the 'start', 'step', and 'end' values are given.

Examples:

- Example #1: Single location and varying heading and pitch
	
		java -jar target/StreetviewExtractor-1.0-SNAPSHOT.jar --from 40.631538,-73.965327 -o test.json --heading-start --heading-step 60 --heading-end 300 --pitch-start -45 --pitch-step 20 --pitch-end 45 --single -a <your_api_key> 
	
- Example #2:Route with absolute heading value
	
		java -jar target/StreetviewExtractor-1.0-SNAPSHOT.jar --from 40.631538,-73.965327 --to 40.691099,-73.991785 -i -o test.json --heading 50 -a <your_api_key>
	
- Example #3: Route with relative heading value

		java -jar target/StreetviewExtractor-1.0-SNAPSHOT.jar --from 40.631538,-73.965327 --to 40.691099,-73.991785 -i -o test.json --heading 0 --follow-route -a <your_api_key>

# Database Structure:

## File System Structure:

Every route (origin and destination) generates a route folder with name &#39; **A,B\_Y,Z**&#39;, where:

- A and B are the latitude and longitude of the origin respectively; and
- Y and Z are the latitude and longitude of the destination respectively.

Each route folder can contain different versions of the route (different parameters). Each version is contained in a folder with name &#39; **H:{HD}\_P:{P}\_FOV:{F}\_M:{M}\_S:{W}x{H}-jpegs**&#39; where:

- **{HD}**: is the heading value
- **{P}**: is the pitch value
- **{F}**: is the field of view value
- **{M}**: is the mode of the direction
- **{W}**: is the width of the images
- **{H}**: is the height of the images

Inside each route-version folder, the images are named **X.jpg** where X is a number ranging from 00000 up to 99999.

**P.S.** In case of a range of values are given as parameters (fov, heading, pitch): 
 - For a route: different folders will be created for all combinatons of values.   
 - For a single location (--single): images corresponding to the combinations of heading-pitch values will be contained in a single folder. Different fov values will create different folders. 
 
 Comments: [pcl]
- Check if what I have changed in the PS is correct 
 
 ## Metadata

Additionally, the metadata of each route version is saved in a JSON file with name &#39; **H={HD}\_P={P}\_FOV={F}\_M={M}\_S={W}x{H}.json**&#39;. 

The root _JSON Object_ stores all parameters that are common to the route/single-location as _key-value pairs_. It also contains a _JSON Array_, with key  _&#39;images&#39;_ that contains as many _JSON Objects_ as the images/waypoints. Each image _JSON Object_ stores the value of the parameters that are specific to each image/waypoint:

Possible parameters of the roor _JSON Object_ and _JSON Array_ are:

- _from:_ string with origin latitude and longitude, concatenated by a comma.
- _to:_ string with destination latitude and longitude, concatenated by comma.
- _width:_ integer value of the width of the images.
- _height:_ integer value of the height of the images.
- _fpx:_ real value of the frames per meter or frames per second.
- _heading:_  integer value of heading used on the query of the Google Street View API.
- _pitch:_ integer value of the pitch used on the query of the Google Street View API.
- _fov:_ field of view integer value used on the query of the Google Street View API.
- _seqNumber:_  integer value of from 00000 to 99999 that identies the waypoint/image.
- _lat:_ real value of the latitude of the location.
- _lng:_ real value of the longitude of the location.

