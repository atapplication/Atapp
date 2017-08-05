package com.team.atapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.zip.CRC32;

import com.team.atapp.exception.AtAppException;
import com.team.atapp.logger.AtLogger;

public class StringUtil {

	private static AtLogger logger = AtLogger.getLogger(StringUtil.class);
	
	private StringUtil(){}
	
	public static String append(String delimiter, Object... args){
		StringBuilder buf = new StringBuilder();
		boolean firstElem = true;
		for (Object obj : args) {
			if (firstElem) {
				firstElem = false;
			} else {
				buf.append (delimiter);
			}
			buf.append (obj);
		}
		
		return buf.toString();
	}
	
	public static String stringReplace(String text, String searchString,String replacement) {
		if (!text.contains(searchString)) {
			return text;
		}
		text = text.replaceAll(searchString, replacement);
		return text;
	}
	
	/**
	 * Given the input String, if the string is <b>EMPTY</b> or <b>NULL<b> then return true.
	 * @param inputStr
	 * @return
	 */
	public static boolean isEmpty(String inputStr) {
		if ((inputStr != null) && (inputStr.length() > 0)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Given the input String, if the string is <b>NOT NULL</b> and <b>NOT EMPTY</b> return true;
	 * @param inputStr
	 * @return
	 */
	public static boolean notEmpty(String inputStr) {
		return ! isEmpty(inputStr);
	}
	
	/**
	 * Given the Clob Object, it will convert to string
	 * @param obj
	 * @return
	 * @throws AtAppException
	 */
	public static String converClobToString(Object obj) throws AtAppException {
		StringBuilder sb = new StringBuilder();
		String line = null;
		BufferedReader br = null;
		try{
			Clob caseClob = (Clob) obj;
			Reader reader = caseClob.getCharacterStream();
			br = new BufferedReader(reader);
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		}catch(Exception e){
			throw new AtAppException(e);
		}finally{
			if(null != br){
				try {
					br.close();
				} catch (IOException e) {
					logger.error(e, "Exception Occured While Closing Buffered Reader in Clob to String conversion");
				}
			}
		}
		return sb.toString();	
	}
	
	public static List<String> convertToList(String strTxt, String delimiter) {
		List<String> strList = null;
		if (!StringUtil.isEmpty(strTxt)) {
			strList = new ArrayList<String>();
			String[] strArray = strTxt.split(delimiter);
			for (int i = 0; i < strArray.length; i++) {
				strList.add(strArray[i]);
			}
		}
		return strList;
	}

	
	/**
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomUUID(int length) {
		char[] chars = "abcdefghijklmnopqrstuvwxyzABSDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
		Random r = new Random(System.currentTimeMillis());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(chars[r.nextInt(chars.length)]);
		}
		return sb.toString();

	}

	/**
	 * Pass UI Input and Validate with SAOS Value
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static boolean matchValues(Object value1, Object value2) {
		if ("*".equals(value2)) {
			return true;
		}

		if (value2.equals(value1)) {
			return true;
		}
		return false;
	}
	

	/**
	 * Given the input Object, if the object is <b>EMPTY</b> or <b>NULL<b> then return true.
	 * @param inputObj
	 * @return
	 */
	public static boolean isEmptyObj(Object inputObj) {
		if ((inputObj == null) || "".equals(inputObj)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Given the input Object, if the object is <b>NOT NULL</b> and <b>NOT EMPTY</b> return true;
	 * @param inputObj
	 * @return
	 */
	public static boolean notEmptyObj(Object inputObj) {
		return ! isEmptyObj(inputObj);
	}
	
	/**
	 * To convert the input String to Upper Case
	 * @param inputStr
	 * @return
	 */
	public static String toUpperCase(String inputStr) {
		if (isEmpty(inputStr)) {
			return "";
		}
		return inputStr.toUpperCase();
	}

	/**
	 * Get UUID in 20 Length
	 * @return
	 */
	public static String generateUUID20() {

		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replaceAll("-", "");

		CRC32 checksum = new CRC32();
		byte bytes[] = uuid.getBytes();
		checksum.update(bytes, 0, bytes.length);
		long checksumValue = checksum.getValue();
		long currentTime = System.currentTimeMillis();
		String total = checksumValue + "" + currentTime + uuid;

		total = total.substring(0, 20);
		return (total);

	}
	
	public static void main(String[] args) {
		String s = replaceString("123456789012345678", 6, "123456789012345678".length(), "X");
		System.out.println(s);
	}
	
	/**
	 * To Validate the Input Parameter
	 * @param fileName
	 * @return
	 */
	public static String validate(String fileName) {
		if(StringUtil.notEmpty(fileName)) {
			return fileName;
		} else {
			return null;
		}
	}

	public static String validateString(String str) {
		return str;
	}
	
	public static String replaceString(String str,int start,int end,String replaceStr) {
		String newString = str;
		StringBuilder sb = new StringBuilder();
		for(int i=start;i<end;i++) {
			sb.append(replaceStr);
		}
		if(start != 0) {
			newString = newString.substring(0,start);
			sb.insert(0, newString);
		} else {
		newString = newString.substring(end);
		sb.append(newString);
		}
		return sb.toString();
	}
	


}
