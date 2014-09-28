package cn.chinaunicom.woplus.openapi.java;

import java.util.HashMap;

/**
 * 返回值
 * 
 * @author sharetop
 * @version 0.0.1
 * 
 * */
public class WoPlusResponse {

	/**
	 * 返回码，为0表示成功，其它是对应的错误码
	 * */
	public String resultCode;
	
	/**
	 * 返回信息，成功字串，或者失败的原因
	 * */
	public String resultDescription;
	
	/**
	 * 具体的返回值，参考对应的接口文档
	 * */
	public HashMap<String,Object> content;
}
