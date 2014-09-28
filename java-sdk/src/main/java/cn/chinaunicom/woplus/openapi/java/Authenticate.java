package cn.chinaunicom.woplus.openapi.java;

import java.util.Date;
import java.util.HashMap;

/**
 * 基本验证功能
 * 
 * @author sharetop
 * @version 0.0.1
 * 
 * */
public class Authenticate {
	private static final String get_token_api_url="http://open.wo.com.cn/openapi/authenticate/v1.0";
	
	private static Authenticate ainstance=null;
	private Authenticate(){
		//测试数据
		tokenMap.put("4ce73b59a95840baa3e7095b00a9fdc4", new Token("24f1fc2e1a4821d83b562f6a66c771f55a544cec",2592000));
	}
	
	public static Authenticate getInstance(){
		if(ainstance==null){
			ainstance=new Authenticate();
		}
		return ainstance;
	}
	
	/**
	 * 获取应用标识（AppKey）
	 * */
	public String getAppKey() {
		return appKey;
	}
	/**
	 * 获取应用密钥（AppSecret）
	 * */
	public String getAppSecret() {
		return appSecret;
	}
	
	/**
	 * 配置应用标识和应用密钥
	 * */
	public Authenticate config(String aKey,String aSecret){
		this.appKey=aKey;
		this.appSecret=aSecret;
		
		return this;
	}
	
	/**
	 * 获取TOKEN
	 * */
	public String getToken(){
	
		/**
		 * 事先从本地缓存中查找
		 * */
		if(tokenMap.containsKey(appKey)){
			Token tk=tokenMap.get(appKey);
			if( _checkToken(tk) )
				return tk.value;
		}
		
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("appKey", appKey);
		params.put("appSecret", appSecret);
		
		WoPlusResponse resp;
		try {
			resp = WoPlusClient.getJSONEntity(get_token_api_url, null, params);
			if(resp.resultCode.equals("0")){
				
				Token tk = new Token(resp.content.get("token").toString()
						,Long.parseLong(resp.content.get("tokenExpireIn").toString()));
				tokenMap.put(appKey, tk);
				
				return tk.value;
			}
			else throw new Exception(resp.resultDescription);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * 组装签名集合（TOKEN）
	 * 
	 * */
	public HashMap<String,String> getAuthorization(){
		HashMap<String,String> auth = new HashMap<String,String>();
		auth.put("appKey", appKey);
		auth.put("token", getToken());
		return auth;
	}
	
	/***
	 * 组装签名集合（PLATFORM）
	 * @param platform 平台ID
	 * @param password 平台调用密码
	 */
	public static HashMap<String,String> getAuthorizationWithPlatform(String platform,String password){
		HashMap<String,String> auth = new HashMap<String,String>();
		auth.put("platformID", platform);
		auth.put("password", password);
		
		return auth;
	}
	
	
	/**
	 * 内部类 Token
	 * 
	 * */
	public static class Token {
		/**
		 * Token值
		 * */
		public String value;
		/**
		 * 过期时长（秒）
		 * */
		public long expire;
		/**
		 * 获取时间
		 * */
		public Date getdate;
		
		public Token(String v,long e){
			this.value=v;
			this.expire=e;
			this.getdate=new Date();
		}
	}
	
	///////////////////////////////////////////////////
	// 私有
	///////////////////////////////////////////////////
	private String appKey;
	private String appSecret;
	
	private HashMap<String,Token> tokenMap=new HashMap<String,Token>();
	
	private boolean _checkToken(Token tk){
		if(tk==null || tk.value==null) return false;
		
		if( (new Date().getTime())> (tk.getdate.getTime()+tk.expire) )
			return false;
		
		return true;
	}
}
