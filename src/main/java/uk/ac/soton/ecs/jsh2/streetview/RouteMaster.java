package uk.ac.soton.ecs.jsh2.streetview;

import java.util.ArrayList;
import java.util.List;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.model.*;
import org.openimaj.math.geometry.path.Polyline;
import org.openimaj.math.geometry.path.resample.LinearResampler;
import org.openimaj.math.geometry.point.Point2d;

import uk.ac.soton.ecs.jsh2.streetview.Route.Waypoint;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.internal.StringJoin.UrlValue;

import com.google.maps.model.DirectionsRoute;

/**
 * Class to compute routes between two points
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 *
 */
public class RouteMaster {

	public static int WAYPOINTS_PER_SECOND = 1;

	private GeoApiContext context;
	protected String mapsKey;
	protected int width = 600;
	protected int height = 300;
	protected int fov = 90;
	protected int pitch = 0;
	protected int heading = 180;
	String modo = "DRIVING";

	private boolean timeRecode;
	private boolean followRoute;
	private double recodeParam;

	/**
	 * @param gmapsApiKey
	 *            google maps api key
	 * @param width
	 *            image width
	 * @param height
	 *            image height
	 * @param fov
	 *            image zoom
	 * @param pitch
	 * 				rotamiento vertical
	 * @param heading
	 * 	 	           rotamiento de la imagen
	 * @param timeRecode
	 *            if true then recode path based on time; otherwise on distance
	 *@param followRoute
	 * 			if true then follow_route by computing the direction
	 * @param recodeParam
	 *            if timeRecode then this is frames per second; otherwise this
	 *            is frames per metre
     * @param modo
     *              Mode of the rute
	 */
	public RouteMaster(String gmapsApiKey, int width, int height, boolean timeRecode, double recodeParam, int fov, int pitch, int heading, boolean followRoute, String modo) {
		this.context = new GeoApiContext();
		this.context.setApiKey(gmapsApiKey);
		this.mapsKey = gmapsApiKey;
		this.width = width;
		this.height = height;
		this.timeRecode = timeRecode;
		this.recodeParam = recodeParam;
		this.fov = fov;
		this.pitch = pitch;
		this.modo = modo;
		this.heading = heading;
		this.followRoute = followRoute;
	}

	public Route computeRoute(String origin, String destination) throws Exception
	{
		final Route r = new Route(width, height, mapsKey, fov, pitch, heading);

		computeDirections(r, origin, destination);

		return r;
	}

	protected void computeDirections(Route r, String origin, String destination) throws Exception {
		// compute raw route using gmaps api
		final DirectionsRoute[] dir = DirectionsApi.getDirections(context, origin, destination).mode(TravelMode.valueOf(modo)).await();



        r.rawRoute = dir[0];

		// now recode the route into a set of waypoints
		for (final DirectionsLeg leg : r.rawRoute.legs) {
			for (final DirectionsStep step : leg.steps) {

				final List<LatLng> path = step.polyline.decodePath();
				r.addAll(recodePath(r, path, step.duration.inSeconds, step.distance.inMeters));

			}
		}

		// update the directions so that when at a waypoint you're looking
		// towards the next
		if (followRoute) {
			double heading = 0;
			for (int i = 0; i < r.size(); i++) {
				final Waypoint current = r.get(i);
				if (i < r.size() - 1) {
					heading = computeDirection(current, r.get(i + 1));
				}
				if (this.heading  == 361){
				    current.heading = heading;}
				else{
				    current.heading = heading + this.heading;
                }
			}
		}
		else{

			for (int i = 0; i < r.size(); i++) {
				final Waypoint current = r.get(i);

				current.heading = this.heading;
			}
		}
	}

	private List<Waypoint> recodePath(Route r, List<LatLng> path, long duration, double distance) {
		final int frames;
		if (timeRecode) {
			// recodeParam is frames per second
			frames = (int) (duration * recodeParam);
		} else {
			// recodeParam is frames per meter
			frames = (int) (distance * recodeParam);
		}

		final Polyline pl = new Polyline();
		for (final LatLng l : path)
			pl.points.add(new LatLngPoint2d(l));

		final ArrayList<Waypoint> out = new ArrayList<Waypoint>();
		for (final Point2d p : new LinearResampler(frames).apply(pl).points)
			out.add(r.new Waypoint(((LatLngPoint2d) p).latlng));

		return out;
	}

	private double computeDirection(Waypoint l1, Waypoint l2) {
		final double lamda1 = Math.toRadians(l1.latlng.lng);
		final double lamda2 = Math.toRadians(l2.latlng.lng);
		final double psi1 = Math.toRadians(l1.latlng.lat);
		final double psi2 = Math.toRadians(l2.latlng.lat);

		final double y = Math.sin(lamda2 - lamda1) * Math.cos(psi2);
		final double x = Math.cos(psi1) * Math.sin(psi2) -
				Math.sin(psi1) * Math.cos(psi2) * Math.cos(lamda2 - lamda1);
		return Math.toDegrees(Math.atan2(y, x));
	}
}
