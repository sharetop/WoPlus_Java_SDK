package cn.chinaunicom.woplus.openapi.java;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 签名类
 * 
 * @author sharetop
 * @version 0.0.1
 * 
 * */
public class EMCSign {
	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(EMCSign.class);
	
	public static String signOAuthValue(String method,String uri,HashMap<String,Object> params,String secretKey){
		String password=null;
		
		TreeMap<String,Object> tmap = new TreeMap<String,Object>(new Comparator<String>(){

			public int compare(String k1, String k2) {
				// TODO Auto-generated method stub
				return k1.toLowerCase().compareTo(k2.toLowerCase());
			}});
		tmap.putAll(params);
		
		try {
			StringBuilder sb=new StringBuilder();
			sb.append(method)
			.append("&");
			
			sb.append(URLEncoder.encode(uri, "UTF-8"))
			.append("&");
			
			StringBuilder sb2=new StringBuilder();
			for(Object key :tmap.keySet()){
				sb2.append("&")
				.append(key)
				.append("=")
				.append(tmap.get(key));
			}
			sb.append(URLEncoder.encode(sb2.toString().substring(1),"UTF-8"));
			
			String signStr = sb.toString();
			logger.debug(signStr);
			
			password = checkMsgDigest(signStr, secretKey);
		      
		 } catch (Exception e) {
		      e.printStackTrace();
		 }
		 return password;
	}
	/**
	 * 生成签名字串
	 * @param params 参数集合
	 * @param secretKey 签名密钥
	 * 
	 * */
	public static String signValue(HashMap<String,Object> params,String secretKey){
		
		String password=null;
		
		TreeMap<String,Object> tmap = new TreeMap<String,Object>(new Comparator<String>(){

			public int compare(String k1, String k2) {
				// TODO Auto-generated method stub
				return k1.toLowerCase().compareTo(k2.toLowerCase());
			}});
		tmap.putAll(params);
		
		try {
			StringBuilder sb=new StringBuilder();
			for(Object key :tmap.keySet()){
				sb.append("&");
				sb.append(key);
				sb.append("=");
				sb.append(tmap.get(key));
			}
			String signStr = sb.toString();
			
			String source = checkMsgDigest(signStr.substring(1), secretKey);
		    //password = URLEncoder.encode(source, "UTF-8");
			password = source;
		      
		 } catch (Exception e) {
		      e.printStackTrace();
		 }
		 return password;
	}
	
	static String CRYPTO_NAME_HMAC_SHA1 = "HmacSHA1";
	static byte[] getByte(String source, String secretKey)
			throws NoSuchAlgorithmException, InvalidKeyException {
		try {
			SecretKeySpec secret = new SecretKeySpec(secretKey.getBytes(),
					CRYPTO_NAME_HMAC_SHA1);
			Mac mac = Mac.getInstance(CRYPTO_NAME_HMAC_SHA1);
			mac.init(secret);
			byte[] result = mac.doFinal(source.getBytes("UTF-8"));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
		
	}
	
	static String checkMsgDigest(String sourceStr,String secretKey) throws InvalidKeyException, NoSuchAlgorithmException {
		
		byte[] msgBase64Digest;

		byte[] tmpEncrypted = getByte(sourceStr, secretKey);

		msgBase64Digest = Base64.encodeBase64(tmpEncrypted);
		
		String passWord = null;
		try {
			passWord = new String(msgBase64Digest, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return passWord;

	}
	
}
