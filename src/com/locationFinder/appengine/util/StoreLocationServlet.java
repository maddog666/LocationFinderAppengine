package com.locationFinder.appengine.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.locationFinder.appengine.domain.Location;
import com.locationFinder.appengine.adaptor.LocationAdaptor;


public class StoreLocationServlet extends HttpServlet {

	private static final Logger log = 
			Logger.getLogger(StoreLocationServlet.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException{
		this.doGet(req, resp);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException{
		String name = req.getParameter("name");
		String address = req.getParameter("address");
		String phone = req.getParameter("phone");
		String lat = req.getParameter("lat");
		String lng = req.getParameter("lng");
		
		String[] categories = req.getParameterValues("category");
		
		resp.setContentType("text/plain");
		resp.getWriter().println("Name " + name);
		resp.getWriter().println("Phone " + phone);
		resp.getWriter().println("Address " + address);
		resp.getWriter().println("Lat " + lat);
		resp.getWriter().println("Lng " + lng);
		
		

		if(categories!=null){
			resp.getWriter().println("category:");
			for(String category: categories){
				resp.getWriter().println(category);
			}
		}
		
		if(name!=null && lat!=null && lng != null){				
			Location location = new Location();
			location.setAddress(address);
			location.setPhone(phone);
			location.setLat(lat);
			location.setLng(lng);
			location.setName(name);
			
			if(categories!=null){
				location.setCategories(Arrays.asList(categories));
			}
			
			LocationAdaptor locationAdaptor = new LocationAdaptor();
			locationAdaptor.storeLocation(location);
			resp.getWriter().println("stored");
		}
	}
}
