<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.locationFinder.appengine.adaptor.LocationAdaptor" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.locationFinder.appengine.domain.Location" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Location list</title>
</head>
<body>

<%

String offsetString = request.getParameter("offset");
final int pageLimit = 20;
int offset = 0;
if(offsetString != null){
    try{
		offset = Integer.parseInt(offsetString);
    }catch(NumberFormatException e){
    	offset = 0;
    }
}

/*
DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
Key locationKey = KeyFactory.createKey(Location.class.getSimpleName(), 
		"Location");

Query query = new Query("Location", locationKey).addSort(
        "name", Query.SortDirection.DESCENDING);
List<Entity> locations = datastore.prepare(query).asList(
      FetchOptions.Builder.withLimit(pageLimit).offset(offset*pageLimit));
*/
LocationAdaptor locationAdaptor = new LocationAdaptor();
List<Location> locations = locationAdaptor.fetchLocations(pageLimit, offset);
    if(locations.isEmpty()){
    %>
    <p>No location was stored.</p>
    
    <% 
    }else{
        %>
        <p><%= locations.size() %> locations in database</p>
        
        <% 
        for(Location location: locations){
        %>
            <p>
            <%=location.getName()%> wrote:

          <blockquote>          
          Address: <%=location.getAddress()%><br>
          Phone: <%=location.getPhone()%><br>
          Lat: <%=location.getLat()%><br>
          Lng: <%=location.getLng()%><br>
          Categories: <%
          for(String category: location.getCategories()){
        	  out.print(category+" ");
          }
          
          %>
          </blockquote>
          </p>
            <%
        }
        
    }
    
    %>
    <p>
    <%
    if(offset>0){
    %>	
    
    <a href="locations.jsp?offset=<%=(offset-1)%>">Last Page</a>
    <%
    }
    %>
    <a href="locations.jsp?offset=<%=(1+offset)%>">Next Page</a>
    
    </p>
    
    


<!-- 
<form action="/storeLocation" method="post">
    <div><textarea name="content" rows="3" cols="60"></textarea></div>
    <input type="hidden" name="guestbookName" value="" />
    <div><input type="submit" value="Post Greeting" /></div>
    
</form>
-->
</body>
</html>