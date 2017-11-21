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
		/*String MID = "Wheelc40923916143942"; 
		 String MercahntKey = "m7XeF8cBlTF9XMnp";
		 String INDUSTRY_TYPE_ID = "Retail";
		 String CHANNLE_ID = "WAP";
		 String WEBSITE = "wheelcare";
		 String CALLBACK_URL = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
		 
		 
		 TreeMap<String,String> paramMap = new TreeMap<String,String>();
			paramMap.put("MID" , MID);
			paramMap.put("ORDER_ID" , "WCOrder100008548");
			paramMap.put("CUST_ID" , "WCUser31");
			paramMap.put("INDUSTRY_TYPE_ID" , INDUSTRY_TYPE_ID);
			paramMap.put("CHANNEL_ID" , CHANNLE_ID);
			paramMap.put("TXN_AMOUNT" ,  "1.00");
			paramMap.put("WEBSITE" , WEBSITE);
			paramMap.put("CALLBACK_URL" , CALLBACK_URL);
			
			String checkSum="";
			try{
				checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(MercahntKey, paramMap);
			   		System.out.println(checkSum);			
			}catch(Exception e) {
				e.printStackTrace();
			}*/
	   

	}  
	
}
