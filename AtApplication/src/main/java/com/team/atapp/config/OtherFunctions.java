package com.team.atapp.config;

import java.net.URL;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class OtherFunctions {
	public static String encryptTheMapKey(String urlString)
	{
		String keyString="2HlC4Ih2QDWUWLjqxz7hDZz5";
		String client="342097785750-p4tbi09mskjhc76o8r2uoc7ico69bra0.apps.googleusercontent.com";
			
		  byte[] key=null;
		  URL url =null;
		  try {
			url = new URL(urlString+"&avoid=tolls&client="+client);		
			keyString = keyString.replace('-', '+');
			keyString = keyString.replace('_', '/');
			key=DatatypeConverter.parseBase64Binary(keyString);
			String resource = url.getPath() + '?' + url.getQuery();		   
		    // Get an HMAC-SHA1 signing key from the raw key bytes
		    SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");

		    // Get an HMAC-SHA1 Mac instance and initialize it with the HMAC-SHA1 key
		    Mac mac = Mac.getInstance("HmacSHA1");
		    mac.init(sha1Key);

		    // compute the binary signature for the request
		    byte[] sigBytes = mac.doFinal(resource.getBytes());

		    // base 64 encode the binary signature
		    String signature = DatatypeConverter.printBase64Binary(sigBytes);
		    
		    // convert the signature to 'web safe' base 64
		    signature = signature.replace('+', '-');
		    signature = signature.replace('/', '_');
		  //  System.out.println(url.getProtocol() + "://" + url.getHost() + resource + "&signature=" + signature);
		    return url.getProtocol() + "://" + url.getHost() + resource + "&signature=" + signature;
			
			
			
			
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  return urlString;
}
}