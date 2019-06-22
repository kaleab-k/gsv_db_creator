package uk.ac.soton.ecs.jsh2.streetview;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.google.gson.*;
import com.google.maps.model.LatLng;
import org.apache.commons.io.FileUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.video.VideoWriter;
import org.openimaj.video.xuggle.XuggleVideoWriter;
import uk.ac.soton.ecs.jsh2.streetview.Route.Waypoint;

/**
 * Simple commandline app to download streetview images
 *
 */
public class App
{
	@Option(name = "--api-key", aliases = "-a", usage = "Google API Key", required = true)
	String googleApiKey;

	@Option(
			name = "--output",
			aliases = "-o",
			usage = "Output geojson file; the .json extension will be added if it's not present",
			required = true)
	String output;

	@Option(name = "--write-video", aliases = "-v", usage = "Output a video of the route.")
	boolean saveVideo = false;

	@Option(name = "--write-images", aliases = "-i", usage = "Output the images of the route.")
	boolean saveImages = false;

	@Option(name = "--width", aliases = "-w", usage = "Image width.")
	int width = 600;

	@Option(name = "--height", aliases = "-h", usage = "Image height.")
	int height = 300;

	@Option(name = "--fov", aliases = "-f", usage = "Image zoom.")
	int fov = 90;

	@Option(name = "--pitch", aliases = "-p", usage = "rotamiento vertical.")
	int pitch = 0;

	@Option(name = "--heading", aliases = "-he", usage = "rotamiento.")
	int heading = 361;

	@Option(
			name = "--from",
			usage = "From coordinates formatted as lat,lng",
			required = true)
	String from;

	@Option(
			name = "--to",
			usage = "To coordinates formatted as lat,lng",
			required = false)
	String to;

	@Option(
			name = "--mode",
			usage = "Mode of route")
	String mode = "DRIVING";

	@Option(
			name = "--time-recode",
			usage = "Recode the path based on the time of each segment; the images will be further apart when moving faster")
	private boolean timeRecode;

	@Option(
			name = "--follow-route",
			usage = "Recompute the heading according to the direction between consecutive points")
	private boolean followRoute;

	@Option(
			name = "--fpx",
			usage = "Number of images per X. If --time-recode is enabled X is seconds; otherwise it is metres.")
	private double fpx = 0.1;

	/**
	 *  Options used when 'heading' value is a range, not fixed,
	 */
	@Option(
			name = "--heading-start",
			usage = "the starting heading value when using a range")
	private int headingStart;

	@Option(
			name = "--heading-end",
			usage = "the ending heading value when using a range.")
	private int headingEnd;

	@Option(
			name = "--heading-rate",
			usage = "the sampling rate/frequency of the heading value change when using a range")
	private int headingRate;

	/**
	 *  Options used when 'pitch' value is a range, not fixed,
	 */
	@Option(
			name = "--pitch-start",
			usage = "the starting pitch value when using a range")
	private int pitchStart;

	@Option(
			name = "--pitch-end",
			usage = "the ending pitch value when using a range.")
	private int pitchEnd;

	@Option(
			name = "--pitch-rate",
			usage = "the sampling rate/frequency of the pitch value change when using a range")
	private int pitchRate;

	/**
	 *  Options used when 'fov' value is a range, not fixed,
	 */
	@Option(
			name = "--fov-start",
			usage = "the starting field of view value when using a range")
	private int fovStart;

	@Option(
			name = "--fov-end",
			usage = "the ending field of view value when using a range.")
	private int fovEnd;

	@Option(
			name = "--fov-rate",
			usage = "the sampling rate/frequency of the field of view value change when using a range")
	private double fovRate;

	// Option when querying single location rather than a route
	@Option(
			name = "--single",
			usage = "indicates if the query is for a single location, not a route")
	private boolean single=false;


	private File jsonPath;
	private File videoPath;
	private File imagesPath;
	int num = 0;

	// Used to avoid saving same image more than once
	MBFImage prevImg;

	private void checkPaths() throws CmdLineException {



		String base = jsonPath.getAbsolutePath();
		base = base.substring(0, base.lastIndexOf("."));

		if (saveVideo) {
			videoPath = new File(base + "_" + heading + "_" + pitch + "_" + fov + ".mp4");
		}

		if (saveImages) {
			if(this.followRoute == false)
//				imagesPath = new File(base +  "_" + mode + "_" + heading + "_" + pitch + "_" + fov + "-jpegs");
				imagesPath = new File(base + "-jpegs");

			else{
//				imagesPath = new File(base +  "_" + mode + "_" + heading + "_" + pitch + "_" + fov + "-jpegs");
				imagesPath = new File(base + "-jpegs");
			}

			imagesPath.mkdirs();
		}
	}

	private void execute() throws Exception {

		if (headingStart < 0 || headingEnd <=0 || headingRate <= 0) {
			headingStart = heading;
			headingEnd = heading;
			headingRate = 1;
		}

		if (pitchStart == 0 && pitchEnd == 0 || pitchRate <= 0) {
			pitchStart = pitch;
			pitchEnd = pitch ;
			pitchRate = 1;
		}

		if (fovStart < 0 || fovEnd <=0 || fovRate <= 0) {
			fovStart = fov;
			fovEnd = fov ;
			fovRate = 1;
		}
		if (!single) {
			for ( heading = headingStart; heading <= headingEnd; heading += headingRate) {
				for (pitch = pitchStart; pitch <= pitchEnd; pitch += pitchRate) {
					for (fov = fovStart; fov <= fovEnd; fov += fovRate) {
						num = 0;
						output = "dataset/"+ from + "_" + to + "/" + "H:" + heading + "_P:"+ pitch + "_FOV:"+ fov + "_M:" + mode + "_S:" + width+"x"+height + ".json";
						jsonPath = new File(output);
						checkPaths();

						final RouteMaster routeMaster = new RouteMaster(googleApiKey, width, height, timeRecode, fpx, fov, pitch, heading, followRoute, mode);
						final Route route = routeMaster.computeRoute(from, to);

						VideoWriter<MBFImage> writer = null;
						try {
							// Initialize JSONObject to store the route info
							JsonObject routeJSON = new JsonObject();
							// Append the below info of the configuration to the JSONObject
							routeJSON.addProperty("from", from);
							routeJSON.addProperty("to", to);
							routeJSON.addProperty("heading", heading);
							routeJSON.addProperty("pitch", pitch);
							routeJSON.addProperty("fov", fov);
							routeJSON.addProperty("mode", mode);
							routeJSON.addProperty("fpx", fpx);
							routeJSON.addProperty("width", width);
							routeJSON.addProperty("height", height);
							routeJSON.addProperty("follow-route", followRoute);
							// Initialize JSONArray to store the details of every image in the route
							JsonArray imgJArray = new JsonArray();

							if (saveVideo)
								writer = new XuggleVideoWriter(videoPath.getAbsolutePath(), width, height, 60);

							for (final Waypoint wp : route) {
								final MBFImage image = ImageUtilities.readMBF(new URL(wp.getStreetviewUrl()));
								if (prevImg != null && prevImg.equals(image)) {
									continue;
								}
								// Init JSONObject for every image in the route
								//				JSONObject imgJSON = new JSONObject();
								JsonObject imgJSON = new JsonObject();
								// Set the sequence number, latitude and longitude of the place
								imgJSON.addProperty("seqNumber", num);
								imgJSON.addProperty("lat", wp.latlng.lat);
								imgJSON.addProperty("lng", wp.latlng.lng);
								imgJSON.addProperty("heading", wp.heading);
								// Add the object to the array
								imgJArray.add(imgJSON);

								if (saveVideo && writer != null) {
									writer.addFrame(image);
								}

								if (saveImages) {
									//					final File imgFile = new File(imagesPath, String.format("%05d_%f_%f_%d_%d_%d.jpg",num, wp.latlng.lat, wp.latlng.lng, heading, pitch, fov));
									final File imgFile = new File(imagesPath, String.format("%05d.jpg", num));

									ImageUtilities.write(image, imgFile);
								}
								prevImg = image;
								num = num + 1;
							}
							// Append the array to the route JSONObject
							routeJSON.add("images", imgJArray);

							/* PrettyJsonString */
							Gson gson = new GsonBuilder().setPrettyPrinting().create();
							JsonParser jp = new JsonParser();
							JsonElement je = jp.parse(routeJSON.toString());
							// Store the pretty JSON string to the file
							FileUtils.write(jsonPath, gson.toJson(je));

						} finally {
							if (saveVideo && writer != null) {
								writer.close();
							}
						}
					}
				}
			}

		} else {

			String[] latlngStr = from.split(",");
			double lat = Double.parseDouble(latlngStr[0]);
			double lng = Double.parseDouble(latlngStr[1]);

			JsonObject routeJSON = new JsonObject();
			// Append the below info of the configuration to the JSONObject
			routeJSON.addProperty("lat", lat);
			routeJSON.addProperty("lng", lng);
			routeJSON.addProperty("mode", mode);
			routeJSON.addProperty("width", width);
			routeJSON.addProperty("height", height);
			// Initialize JSONArray to store the details of every image in the route
			JsonArray imgJArray = new JsonArray();

			for ( heading = headingStart; heading <= headingEnd; heading += headingRate){
				for (pitch = pitchStart; pitch <= pitchEnd; pitch += pitchRate){
					for( fov = fovStart; fov <= fovEnd; fov += fovRate){

						output = "dataset/"+ from + "/" + "M:" + mode + "_S:" + width+"x"+height + ".json";
						jsonPath = new File(output);
						checkPaths();

						final Route r = new Route(width, height, googleApiKey, fov, pitch, heading);

						LatLng latlng = new LatLng(lat,lng);
						Waypoint wp = r.new Waypoint(latlng);

						final MBFImage image = ImageUtilities.readMBF(new URL(wp.getStreetviewUrl()));

						JsonObject imgJSON = new JsonObject();
						// Set the sequence number, latitude and longitude of the place
						imgJSON.addProperty("seqNumber", num);
						imgJSON.addProperty("heading", wp.heading);
						imgJSON.addProperty("pitch", pitch);
						imgJSON.addProperty("fov", fov);
						// Add the object to the array
						imgJArray.add(imgJSON);

						if (saveImages) {
							//					final File imgFile = new File(imagesPath, String.format("%05d_%f_%f_%d_%d_%d.jpg",num, wp.latlng.lat, wp.latlng.lng, heading, pitch, fov));
							final File imgFile = new File(imagesPath, String.format("%05d.jpg", num));

							ImageUtilities.write(image, imgFile);
						}
						num++;
					}
				}
			}

			routeJSON.add("images", imgJArray);

			/* PrettyJsonString */
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(routeJSON.toString());
			// Store the pretty JSON string to the file
			FileUtils.write(jsonPath, gson.toJson(je));

		}
	}

	private void extractSingle(int heading, int pitch, int fov) throws IOException {

	}

	public static void main(String[] args) throws Exception {
		final App app = new App();

		final CmdLineParser parser = new CmdLineParser(app);
		try {
			parser.parseArgument(args);
//			app.checkPaths();
		} catch (final CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("Usage: java -jar StreetviewExtractor.jar [options...]");
			parser.printUsage(System.err);

			return;
		}

		app.execute();
	}
}
