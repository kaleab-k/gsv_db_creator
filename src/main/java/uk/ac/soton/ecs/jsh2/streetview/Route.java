package uk.ac.soton.ecs.jsh2.streetview;

import java.util.ArrayList;

import uk.ac.soton.ecs.jsh2.streetview.Route.Waypoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;

/**
 * Class encode a route with the associated streetview imagery
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 */
public class Route extends ArrayList<Waypoint> {
	private static final long serialVersionUID = 1L;

	/**
	 * A waypoint along a route, characterised by a geographical position and
	 * heading. Each waypoint can be serialised to geojson and has an associated
	 * streetview url.
	 *
	 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
	 *
	 */
	public class Waypoint extends LatLngPoint2d {

		public double heading;

		/**
		 * Construct with the given position
		 *
		 * @param latlng
		 * 				Latitud y longitud
		 * @param mapsKey
		 * 				Clave API
		 */
		public Waypoint(LatLng latlng) {
			super(latlng);
		}

		/**

		 * @return the streetview url for this waypoint
		 */
		public String getStreetviewUrl() {
			if (mapsKey != null && mapsKey.length() > 0)
				return "https://maps.googleapis.com/maps/api/streetview?size=" + width + "x" + height + "&location="
						+ latlng
						+ "&heading=" + heading + "&pitch=" + pitch + "&fov=" + fov + "&key=" + mapsKey +"&source=outdoor";

			return "https://maps.googleapis.com/maps/api/streetview?size=" + width + "x" + height + "&location=" + latlng
					+ "&heading=" + heading + "&pitch=" + pitch + "&fov=" + fov +"&source=outdoor";
		}

		/**
		 * @return the streetview url with pitch set to 0 degrees (level)
		 */


		/**
		 * @return the jsonobject encoding this waypoint
		 */
		public JsonObject toJSON() {
			final JsonArray coords = new JsonArray();
			coords.add(new JsonPrimitive(latlng.lat));
			coords.add(new JsonPrimitive(latlng.lng));

			final JsonObject geo = new JsonObject();
			geo.addProperty("type", "Point");
			geo.add("coordinates", coords);

			final JsonObject props = new JsonObject();
			props.addProperty("streetview", getStreetviewUrl());
			props.addProperty("heading", this.heading);

			final JsonObject jo = new JsonObject();
			jo.addProperty("type", "feature");
			jo.add("geometry", geo);
			jo.add("properties", props);
			return jo;
		}

		@Override
		public String toString() {
			return toJSON().toString();
		}
	}

	protected String mapsKey;
	protected int width = 600;
	protected int height = 300;
	protected int fov = 90;
	protected int pitch = 0;
	protected int heading = 120;

	public static int WAYPOINTS_PER_SECOND = 1;

	protected DirectionsRoute rawRoute;

	public Route(int width, int height, String mapsKey, int fov, int pitch,  int heading) {
		this.width = width;
		this.height = height;
		this.mapsKey = mapsKey;
		this.fov = fov;
		this.pitch = pitch;
		this.heading = heading;

	}

	/**
	 * @return the (geo)jsonobject encoding this route
	 */
	public JsonObject toJSON() {
		final JsonArray feats = new JsonArray();
		for (final Waypoint wp : this) {
			feats.add(wp.toJSON());
		}

		final JsonObject jo = new JsonObject();
		jo.addProperty("type", "FeatureCollection");
		jo.add("features", feats);
		return jo;
	}

	@Override
	public String toString() {
		return toJSON().toString();
	}

}