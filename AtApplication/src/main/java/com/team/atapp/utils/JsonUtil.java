package com.team.atapp.utils;

import java.io.IOException;
import java.io.Serializable;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.atapp.exception.AtAppException;
import com.team.atapp.logger.AtLogger;

/**
 * 
 * @author Shankara
 *
 */
public class JsonUtil implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static AtLogger logger = AtLogger.getLogger(JsonUtil.class);

	private static ObjectMapper objMap = new ObjectMapper();
	
	private JsonUtil(){}
	
	public static <T> T jsonToObj(String json,Class<T> clzz) throws AtAppException {
		T obj = null;
		try {
			obj = (T) objMap.readValue(json,clzz);
			objMap.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			logger.debug("After conversion of Json to Object", obj);
		} catch (JsonParseException e) {
			throw new AtAppException( "Json Parsing Exception in JasonUtil class", HttpStatus.INTERNAL_SERVER_ERROR, e);
		} catch (JsonMappingException e) {
			throw new AtAppException( "Json Mapping Exception in JasonUtil class", HttpStatus.INTERNAL_SERVER_ERROR, e);
		} catch (IOException e) {
			throw new AtAppException( "Json IO Exception in JasonUtil class", HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
		return obj;
	}
	
	public static <T> String objToJson(T obj) throws AtAppException {
		String json = null;
		try {			
			json = objMap.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new AtAppException("Json Processing Exception in JasonUtil class in Obj to Json",HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
		return json;
	}
}