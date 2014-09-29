WoPlus_Java_SDK
===============

用HashMap调用API方法：

### 第一步：初始化WoPlusClient实例，需要提供appKey和appSecret

    WoPlusClient client;
    client = new WoPlusClient(appKey,appSecret);


### 第二步：提供API的地址，参数封到HashMap里，提交请求，得到响应。

    String api_url="http://open.wo.com.cn/openapi/rpc/paymentcodesms/v2.0";
		
    HashMap<String,Object> params = new HashMap<String,Object>();
    params.put("paymentUser", "{mobile_number}");
    params.put("paymentType", 0);
    params.put("outTradeNo","100008");
    params.put("paymentAcount", "001");
    params.put("subject", "金币一堆");
		params.put("totalFee", 0.01f);
		
    try {
    	WoPlusResponse resp = client.post(api_url, params);
			assertNotNull(resp);
			assertEquals("0",resp.resultCode);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


### 第三步：WoPlusResponse中的resultCode和resultDescription可以判断调用是否成功。
其它内容在WoPlusResponse的content字段里（HashMap类型）。


