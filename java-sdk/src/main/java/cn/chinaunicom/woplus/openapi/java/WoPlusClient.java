package cn.chinaunicom.woplus.openapi.java;

import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 工作类
 * 封装了所有操作
 * @author sharetop
 * @version 0.0.1
 * 
 * */
public class WoPlusClient {
	
	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(WoPlusClient.class);
	
	
	public WoPlusClient(){
		this(null,null);
	}
	/**
	 * @param appKey 		应用标识
	 * @param appSecret 	应用密钥
	 * */
	public WoPlusClient(String appKey,String appSecret){
		Authenticate.getInstance().config(appKey, appSecret);
	}
	
	
	/**
	 * @param api_url API接口地址
	 * @param params 参数集
	 * @return 返回值 @see WoPlusResponse
	 * @exception 
	 * */
	public WoPlusResponse post(String api_url,HashMap<String,Object> params) throws Exception{
		
		HashMap<String,String> auth = Authenticate.getInstance().getAuthorization();
		if(auth==null) throw new Exception("Token is Null");
		
		String secretKey=Authenticate.getInstance().getAppKey()+"&"+Authenticate.getInstance().getAppSecret();
		String signature = EMCSign.signValue(params, secretKey);
		
		params.put("signType", "HMAC-SHA1");
		params.put("signature", signature);
		
		return postJSONEntity(api_url,auth,params);
		
	}
	
	/**
	 * @param api_url API接口地址
	 * @param params 参数集
	 * @param platform 平台ID
	 * @param password 平台密码
	 * 
	 * @return 返回值 @see WoPlusResponse
	 * @exception 
	 * */
	public WoPlusResponse post(String api_url,HashMap<String,Object> params,String platform,String password) throws Exception{
		
		HashMap<String,String> auth = Authenticate.getAuthorizationWithPlatform(platform, password);
		
		String secretKey=platform+"&"+password;
		String signature = EMCSign.signValue(params, secretKey);
		
		params.put("signType", "HMAC-SHA1");
		params.put("signature", signature);
		
		return postJSONEntity(api_url,auth,params);
		
	}
	/**
	 * @param api_url API接口地址
	 * @param params 参数集
	 * @return 返回值 @see WoPlusResponse
	 * @exception 
	 * */
	public WoPlusResponse get(String api_url,HashMap<String,Object> params) throws Exception{
		
		HashMap<String,String> auth = Authenticate.getInstance().getAuthorization();
		if(auth==null) throw new Exception("Token is Null");
		
		return getJSONEntity(api_url,auth,params);
		
	}
	

	
	/////////////////////////////////
	//私有
	////////////////////////////////
	private static synchronized WoPlusResponse _transObject(JSONObject obj){
		
		WoPlusResponse resp = new WoPlusResponse();
		
		HashMap<String,Object> msg=new HashMap<String,Object>();
		
		for(String k:obj.keySet()){
			if(k.equals("resultCode")) 
				resp.resultCode=obj.getString(k);
			else if(k.equals("resultDescription"))
				resp.resultDescription=obj.getString(k);
			else
				msg.put(k, obj.get(k));
		}
		resp.content=msg;
		
		return resp;
		
	}
	
	 static synchronized WoPlusResponse postJSONEntity(String api_url,HashMap<String,String> auth,HashMap<String,Object> params) 
		throws Exception 
	{
		
		String body=JSON.toJSONString(params,SerializerFeature.WriteNullNumberAsZero);
		
		StringEntity entity = new StringEntity(body, "utf-8");
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httppost = new HttpPost(api_url);
		httppost.addHeader("Content-Type", "application/json;charset=UTF-8");
		httppost.addHeader("Accept","application/json");
		
		StringBuilder sb=new StringBuilder();
		for(String k : auth.keySet()){
			sb.append(",")
			.append(k)
			.append("=\"")
			.append(auth.get(k))
			.append("\"");
		}
		
		try{
			httppost.addHeader("Authorization",sb.toString().substring(1));
			
			httppost.setEntity(entity);
			System.out.println(EntityUtils.toString(entity));
			
			CloseableHttpResponse response =  httpclient.execute(httppost);
			try{
				HttpEntity respEntity = response.getEntity();
				if (respEntity != null) {
			        body = EntityUtils.toString(respEntity); 
			        logger.debug(body);
				}
			} finally {
			    response.close();
			}
		}
		finally{
			httpclient.close();
		}
		
		return _transObject(JSON.parseObject(body));
		
	}
	
	
	 static synchronized WoPlusResponse getJSONEntity(String api_url,HashMap<String,String> auth,HashMap<String,Object> params) 
		throws Exception
	{
		
		String body=JSON.toJSONString(params,SerializerFeature.WriteNullNumberAsZero);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		StringBuilder sb = new StringBuilder();
		sb.append(api_url);
		sb.append("?");
		for(String key:params.keySet()){
			sb.append(key)
			.append("=")
			.append(params.get(key))
			.append("&");
		}
		String furl = sb.toString().substring(0, sb.toString().lastIndexOf("&"));
		
		HttpGet httpget = new HttpGet(furl);
		httpget.addHeader("Content-Type", "application/json;charset=UTF-8");
		httpget.addHeader("Accept","application/json");
		
		if(auth!=null && auth.values().size()>0){
			StringBuilder sb2=new StringBuilder();
			for(String k : auth.keySet()){
				sb2.append(k);
				sb2.append("=\"");
				sb2.append(auth.get(k));
				sb2.append("\",");
			}
			httpget.addHeader("Authorization",sb2.toString());
		}
		
		try {
			CloseableHttpResponse response =  httpclient.execute(httpget);
			logger.debug(response.getStatusLine().getStatusCode());
			try{
				HttpEntity respEntity = response.getEntity();
				if (respEntity != null) {
					body = EntityUtils.toString(respEntity); 
					logger.debug(body);
				}
			} finally {
			    response.close();
			}
			
		} 
		finally{
			httpclient.close();
		}
		
		return _transObject(JSON.parseObject(body));
		
	}
}
