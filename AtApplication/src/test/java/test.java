import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.team.atapp.config.OtherFunctions;

public class test {
	
	/*
		  public static void main(String[] args){
				float distance = (float)0.0;
				try{
				DefaultHttpClient client1 = new DefaultHttpClient();					
			     //String sourceLatLng="";
			     //String destinationLatLng="";																																			 
			     String url="http://maps.googleapis.com/maps/api/distancematrix/json?origins=12.9333735,77.6243355&destinations=12.9234622,77.6273518";
			     String keyedURL=OtherFunctions.encryptTheMapKey(url);
				HttpGet request = new HttpGet(keyedURL);
			    HttpResponse response1 = 
			    		 client1.execute(request);
			      
			     BufferedReader rd = new BufferedReader (new InputStreamReader(response1.getEntity().getContent()));
			     String line = "";
			     String nLine="";
			
			     while ((line = rd.readLine()) != null) {
			    	 nLine+=line;
			        }
			    System.out.println("nLine"+nLine);
			     JSONObject obj = new JSONObject(nLine);
			     
			     obj=(JSONObject) (obj.getJSONArray("rows")).get(0);
					obj =(JSONObject) (obj.getJSONArray("elements")).get(0);
					obj=(JSONObject) obj.get("distance");
					int distanceM =(int) obj.get("value");	
					distance=(float)distanceM/1000;
				
				}catch(Exception e) {
					e.printStackTrace();

				}
				
				System.out.println("DIstanceeee"+distance);
		  }*/
	
	public static void main(String[] args){
	
		double lat1=12.9293593;
		double lat2=12.9270167;
		double lon1=77.6348503;
		double lon2=77.6377926;
		double el1=0.0;
		double el2=0.0;
	    final int R = 6371; // Radius of the earth

	    double latDistance = Math.toRadians(lat2 - lat1);
	    double lonDistance = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c ; // convert to meters

	    double height = el1 - el2;

	    //distance = Math.pow(distance, 2) + Math.pow(height, 2);
	    
	    System.out.println("Distance"+distance);

	}  
	
}
