# Google Street View (GSV) database creator
Commandline tool to extract streetview images along a route between two locations

# Requirements
- Java Development Kit (https://www.oracle.com/technetwork/java/javase/overview/index.html)
- Apache Maven (https://maven.apache.org/)
- IDE (IntelliJ IDEA) (https://www.jetbrains.com/idea/specials/idea/ultimate.html?gclid=EAIaIQobChMIr9GS-cDo4gIVCJ7VCh0anw2bEAAYASAAEgLpvPD_BwE)

Comments: [pcl]
- Add instructions on installing jdk in linux using apt-get
- Add information about what is required and what is optional (e.g. if Maven is optional if you have installed IDEA)

# Dependencies
This code uses the StreetviewExtractor library (https://github.com/jonhare/StreetviewExtractor)

# Installation and compilation
It can be compiled using a terminal or via the IDE.
## Using IDEA
To import the project into the IDEA, click on File -> Open and then select the directory. 
and follow the same steps as discussed for the *terminal* by clicking on 'Terminal' on the bottom of the IDE.

## Using the Terminal
You need Apache Maven. From a terminal run `mvn package shade:shade` to build an executable jar.
This creates the executable jar under the *target* subfolder. Thus, you can follow the *Usage* section to execute commands. 

# Usage
You will need a Google API key - get one here: https://console.developers.google.com.
You'll need to enable the "Directions API" and "Street View Image API".

Run the program with `java -jar target/StreetviewExtractor-1.0-SNAPSHOT.jar`


		Usage: java -jar StreetviewExtractor.jar [options...]
		 --api-key (-a) VAL  : Google API Key (enabled for Directions and Street View Images)
		 --fpx N             : Number of images per X. If --time-recode is enabled X is
		                       seconds; otherwise it is metres.
		 --from VAL          : From coordinates formatted as lat,lng
		 --height (-h) N     : Image height.
		 --output (-o) VAL   : Output geojson file; the .json extension will be added
		                       if it's not present
		 --time-recode       : Recode the path based on the time of each segment; the
		                       images will be further apart when moving faster
		 --to VAL            : To coordinates formatted as lat,lng
		 --width (-w) N      : Image width.
		 --write-images (-i) : Output the images of the route.
		 --write-video (-v)  : Output a video of the route.

Example:

		java -jar target/StreetviewExtractor-1.0-SNAPSHOT.jar --from 40.631538,-73.965327 --to 40.691099,-73.991785 -i -v -o test.json -a <your_api_key>


# Database structure
-
-

