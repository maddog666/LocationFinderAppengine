package com.locationFinder.appengine.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

//import com.locationFinder.appengine.domain.FetchedLocationObject;
import com.locationFinder.appengine.domain.ResultObject;

//import com.locationFinder.appengine.domain.Location;
import com.locationFinder.appengine.adaptor.LocationAdaptor;

public class FetchLocationServlet extends HttpServlet {

	private float radiusMeters = 500f;
	
	private ResultObject result;
	
	private static final Logger log = 
			Logger.getLogger(StoreLocationServlet.class.getName());
	
	public enum ExecuteStage {
		LatLng,
		LatLngCat,
		Cat,
		Fail
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException{
		this.doGet(req, resp);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException{
		String latString = req.getParameter("lat");
		String lngString = req.getParameter("lng");
		String category = req.getParameter("category");
		String radiusString = req.getParameter("radius");
		
		float lat = 0f;
		float lng = 0f;
		
		ExecuteStage mode = ExecuteStage.Fail;
		
		try{
			lat = Float.parseFloat(latString);
			lng = Float.parseFloat(lngString);
			mode = ExecuteStage.LatLng;
		} catch(RuntimeException ex) {
			log.warning("Undefined Latitude or Longitude");			
		}
		
		if (category != null && category != "") {
			if(mode == ExecuteStage.LatLng){
				//fetch by Lat, Lng, category
				mode = ExecuteStage.LatLngCat;
			} else {
				//fetch by category
				mode = ExecuteStage.Cat;
			}
		}
		
		//Set the maximum radius in meter for returning locations
		try{
			setRadiusMeters(Float.parseFloat(radiusString));
		}catch (RuntimeException ex){
			log.warning("Unable to convert radius meter, use default");
		}
		
		LocationAdaptor locationAdaptor = new LocationAdaptor();
		ResultObject result = new ResultObject();
		
		switch(mode){
			case LatLng:
				result.setLocations(
						locationAdaptor.fetchLocations(
								lat, lng, radiusMeters));
				break;
			case Cat:
				result.setLocations(
						locationAdaptor.fetchLocations(
								category, radiusMeters));
				break;
			case LatLngCat:
				result.setLocations(
						locationAdaptor.fetchLocations(
								lat, lng, category, radiusMeters));
				break;
			default:
				result.setStatus(mode.toString());
		}
		
		//write json objects to string
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		json = mapper.writeValueAsString(result);
		
				
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/jsonrequest");
		//resp.setContentType("application/json");
		
		PrintWriter out = resp.getWriter();		
		out.print(json);		
		out.flush();
		
		/*
		if(((latString != null && latString != "") &&
				(lngString != null && lngString != null)) &&
				(category != null && category != "")) {
			
		}else{
			
		}
		*/
		
		
		
		//}
		
	}

	/**
	 * @return the radiusMeters
	 */
	public float getRadiusMeters() {
		return radiusMeters;
	}

	/**
	 * @param radiusMeters the radiusMeters to set
	 */
	public void setRadiusMeters(float radiusMeters) {
		this.radiusMeters = radiusMeters;
	}

}
