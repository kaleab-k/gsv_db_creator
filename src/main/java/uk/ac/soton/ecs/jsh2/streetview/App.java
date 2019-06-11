package uk.ac.soton.ecs.jsh2.streetview;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.video.VideoWriter;
import org.openimaj.video.xuggle.XuggleVideoWriter;
import java.io.File;
import java.io.IOException;
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
			required = true)
	String to;

	@Option(
			name = "--mode",
			usage = "Mode of rute")
	String mode = "DRIVING";

	@Option(
			name = "--time-recode",
			usage = "Recode the path based on the time of each segment; the images will be further apart when moving faster")
	private boolean timeRecode;

	@Option(
			name = "--head",
			usage = "Si esta activado, las imagenes tienen el head de la siguiente")
	private boolean head;

	@Option(
			name = "--fpx",
			usage = "Number of images per X. If --time-recode is enabled X is seconds; otherwise it is metres.")
	private double fpx = 0.1;

	private File jsonPath;
	private File videoPath;
	private File imagesPath;
	int num = 0;

	private void checkPaths() throws CmdLineException {
		jsonPath = output.endsWith(".json") ? new File(output) : new File(output + "_" +  heading + "_" + pitch + "_" + fov + ".json");


		String base = jsonPath.getAbsolutePath();
		base = base.substring(0, base.lastIndexOf("."));

		if (saveVideo) {
			videoPath = new File(base + "_" + heading + "_" + pitch + "_" + fov + ".mp4");


		}

		if (saveImages) {
			if(this.head== false)
			imagesPath = new File(base +  "_" + mode + "_" + heading + "_" + pitch + "_" + fov + "-jpegs");
			else{
				imagesPath = new File(base +  "_" + mode + "_+" + heading + "_" + pitch + "_" + fov + "-jpegs");
			}

			imagesPath.mkdirs();
		}
	}

	private void execute() throws Exception {
		final RouteMaster routeMaster = new RouteMaster(googleApiKey, width, height, timeRecode, fpx, fov, pitch, heading, head, mode);
		final Route route = routeMaster.computeRoute(from, to);

		VideoWriter<MBFImage> writer = null;
		try {
			FileUtils.write(jsonPath, route.toString());

			if (saveVideo)
				writer = new XuggleVideoWriter(videoPath.getAbsolutePath(), width, height, 60);

			for (final Waypoint wp : route) {
				System.err.println("Processing " + wp.latlng.lat + " " + wp.latlng.lng);
				final MBFImage image = ImageUtilities.readMBF(new URL(wp.getStreetviewUrl()));

				if (saveVideo && writer != null) {
					writer.addFrame(image);
				}

				if (saveImages) {

					final File imgFile = new File(imagesPath, String.format("%d_%f_%f_%d_%d_%d.jpg",num, wp.latlng.lat, wp.latlng.lng, heading, pitch, fov));

					ImageUtilities.write(image, imgFile);
					num = num + 1;
				}
			}
		} finally {
			if (saveVideo && writer != null) {
				writer.close();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		final App app = new App();

		final CmdLineParser parser = new CmdLineParser(app);
		try {
			parser.parseArgument(args);
			app.checkPaths();
		} catch (final CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("Usage: java -jar StreetviewExtractor.jar [options...]");
			parser.printUsage(System.err);

			return;
		}

		app.execute();
	}
}
