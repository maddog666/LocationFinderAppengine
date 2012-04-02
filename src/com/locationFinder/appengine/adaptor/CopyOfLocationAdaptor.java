package com.locationFinder.appengine.adaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.locationFinder.appengine.domain.Location;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class CopyOfLocationAdaptor {
	
	private DatastoreService datastore;
	
	public CopyOfLocationAdaptor(){
		datastore = DatastoreServiceFactory.getDatastoreService();
	}
	
	
	public void storeLocation(Location location) throws IOException {
		//store location
		Key locationKey = 
				KeyFactory.createKey(Location.class.getSimpleName(), 
						"Location");
		Entity locationEntity = new Entity("Location", locationKey);
		locationEntity.setProperty("name", location.getName());
		locationEntity.setProperty("phone", location.getPhone());
		locationEntity.setProperty("address", location.getAddress());
		locationEntity.setProperty("lat", location.getLat());
		locationEntity.setProperty("lng", location.getLng());		

		this.getDatastore().put(locationEntity);
		
		if(location.getCategories()!=null){			
			for(String category: location.getCategories()){
				//store all categories
				Key categoryKey = KeyFactory.createKey("Category", category);
				//Entity categoryEntity;
				
				Query query = new Query("Category");
				query.addFilter("category", 
						Query.FilterOperator.EQUAL, category);
				Entity categoryEntity = 
						this.getDatastore().prepare(query).asSingleEntity();
				if(categoryEntity == null){
					categoryEntity = new Entity("Category", categoryKey);
					categoryEntity.setProperty("category" , category);
					this.getDatastore().put(categoryEntity);
				}
				
				//store a entity containing location and category keys
				Entity locationCategoryEntity = new Entity("LocationCategory");
				locationCategoryEntity.setProperty("locationKey", locationEntity.getKey());
				locationCategoryEntity.setProperty("categoryKey", categoryEntity.getKey());
				this.getDatastore().put(locationCategoryEntity);				
			}
		}
	}
	
	public void updateLocation(Location location) throws IOException {
		
	}
	
	public void deleteLocation(Location location) throws IOException {
		
	}
	
	/**
	 * This method fetch all the locations as location objects, it also 
	 * put all the categories in. It use pageLimit and offset to set a fetch
	 * limit, which means fetching are done within a limit.
	 * 
	 * @param pageLimit The number of locations in a page.
	 * @param offset The offset where the fetching start from.
	 * 
	 * @return Location objects within the given limit from the database.
	 */
	public List<Location> fetchLocations(int pageLimit, int offset){
		//fetch locations
		Key locationKey = KeyFactory.createKey(Location.class.getSimpleName(), 
				"Location");
		Query query = new Query("Location", locationKey).addSort(
		        "name", Query.SortDirection.DESCENDING);
		//set offset limit
		List<Entity> locationEntities = 
				this.getDatastore().prepare(query).asList(
						FetchOptions.Builder.withLimit(pageLimit)
						.offset(offset*pageLimit));
		
		List<Location> locations = new ArrayList<Location>();
		for(Entity locationEntity: locationEntities){
			//initialize location objects
			Location location = new Location();
			location.setName((String)locationEntity.getProperty("name"));
			location.setAddress((String)locationEntity.getProperty("address"));
			location.setPhone((String)locationEntity.getProperty("phone"));
			location.setLng((String)locationEntity.getProperty("lng"));
			location.setLat((String)locationEntity.getProperty("lat"));		
			//fetch entities containing location and category keys
			
			location.setCategories(this.fetchCategories(
					locationEntity.getKey()));
			
			//add location into the locations list
			locations.add(location);
		}
		return locations;
	}
	
	private List<Location> createLocationsWithCategories(
			List<Entity> locationEntities) {
		List<Location> locations = new ArrayList<Location>();
		for(Entity locationEntity: locationEntities){
			//initialize location objects
			Location location = this.createLocation(locationEntity);
			/*
			Location location = new Location();
			location.setName((String)locationEntity.getProperty("name"));
			location.setAddress((String)locationEntity.getProperty("address"));
			location.setPhone((String)locationEntity.getProperty("phone"));
			location.setLng((String)locationEntity.getProperty("lng"));
			location.setLat((String)locationEntity.getProperty("lat"));		
			*/
			
			//fetch entities containing location and category keys
			
			location.setCategories(this.fetchCategories(
					locationEntity.getKey()));
			
			//add location into the locations list
			locations.add(location);
		}
		return locations;
	}
	
	private Location createLocation(Entity locationEntity) {
		Location location = new Location();
		location.setName((String)locationEntity.getProperty("name"));
		location.setAddress((String)locationEntity.getProperty("address"));
		location.setPhone((String)locationEntity.getProperty("phone"));
		location.setLng((String)locationEntity.getProperty("lng"));
		location.setLat((String)locationEntity.getProperty("lat"));			
		return location;
	}
	
	private List<String> fetchCategories(Key locationEntityKey) {
		List<String> categories = new ArrayList<String>();
		
		Query query = new Query("LocationCategory");
		query.addFilter("locationKey", 
				Query.FilterOperator.EQUAL, locationEntityKey);
		PreparedQuery pq = this.getDatastore().prepare(query);
		for(Entity result: pq.asIterable()){	
			//fetch all categories with the category key
			Key categoryKey = (Key) result.getProperty("categoryKey");				
			query = new Query("Category", categoryKey);				
			PreparedQuery pqCategory = this.getDatastore().prepare(query);
			for(Entity categoryResult: pqCategory.asIterable()){
				//add category into the location object
				categories.add((String)categoryResult.getProperty("category"));
			}
		}
		
		return categories;
	}
	
	
	public List<Location> fetchLocations(float lat, float lng, float radius) {
		List<Location> locations = new ArrayList<Location>();
		Query query = new Query("Location");
		PreparedQuery pq = this.getDatastore().prepare(query);
		
		return locations;
	}
	
	public List<Location> fetchLocations(float lat, float lng, String category, float radius) {
		List<Location> locations = new ArrayList<Location>();
		
		return locations;
	}
	
	public List<Location> fetchLocations(String category, float radius) {
		List<Location> locations = new ArrayList<Location>();
		
		return locations;
	}


	/**
	 * @return the datastore
	 */
	public DatastoreService getDatastore() {
		return datastore;
	}

	private double distFrom(double lat1, double lng1, double lat2, double lng2) {
	    //3958 for mile
		//double earthRadius = 3958.75;
		double earthRadius = 6371;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    //distance in km
	    double dist = earthRadius * c;
	    
	    return dist;
	}

	/**
	 * @param datastore the datastore to set
	 */
	public void setDatastore(DatastoreService datastore) {
		this.datastore = datastore;
	}
}
