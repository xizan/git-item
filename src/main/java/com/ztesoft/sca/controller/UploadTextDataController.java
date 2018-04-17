package com.ztesoft.sca.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.qualitycheck.model.v20160801.UploadDataWithRulesRequest;
import com.aliyuncs.qualitycheck.model.v20160801.UploadDataWithRulesResponse;
import com.ztesoft.sca.common.SysConstant;

import org.apache.log4j.Logger;

@RestController
public class UploadTextDataController {
	private static Logger logger = Logger.getLogger(UploadTextDataController.class);
	public UploadTextDataController() {
		// TODO Auto-generated constructor stub
	}
	
	@RequestMapping(value = "/uploadTextData")
	public String uploadTextData() throws ClientException{
		String result = "";
		logger.info("---------uploadTextData------------");
		UploadDataWithRulesRequest uploadDataWithRulesRequest = new UploadDataWithRulesRequest();
        uploadDataWithRulesRequest.setAcceptFormat(FormatType.JSON);
		IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID, SysConstant.ACCESS_KEY_SECRET);
		DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT, SysConstant.DOMAIN);
		IAcsClient client = new DefaultAcsClient(profile);
        String appkey=SysConstant.APP_KEY;
        String tidOne="10001";
        String tidTwo="10002";
        String dataJsonStr = "{\n" +
                "\"appKey\": \"" + appkey + "\",    \n" +
                "\"tickets\":[\n" +
                "    {\n" +
                "      \"tid\": \"" + tidOne + "\",\n" +
                "        \"dialogue\": \n" +
                "          [{\n" +
                "            \"role\": \"客户\",\n" +
                "            \"identity\": \"test_cm@aliyun.com\",\n" +
                "            \"words\": \"需要绑定预发环境的host，阿里云骗子工单透明化预付验证，请转单给测试组 陈明 谢谢\",\n" +
                "            \"begin\": 0,\n" +
                "            \"end\": 0,\n" +
                "            \"beginTime\": 1368468596000,\n" +
                "            \"hourMinSec\": \"00:00\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"role\": \"客服\",\n" +
                "            \"identity\": \"luoman.wyc\",\n" +
                "            \"words\": \"请稍等，正在核实中, 告诉我一下你的密码\",\n" +
                "            \"begin\": 840000,\n" +
                "            \"end\": 840000,\n" +
                "            \"beginTime\": 1368469436000,\n" +
                "            \"hourMinSec\": \"14:00\"\n" +
                "          }\n" +
                "        ]\n" +
                "  ,      \"context\": null\n" +
                "    }\n" +
                "    ,{\n" +
                "     \"tid\": \"" + tidTwo + "\",\n" +
                "        \"dialogue\": [\n" +
                "          {\n" +
                "            \"role\": \"客户\",\n" +
                "            \"identity\": \"chinaccnet@aliyun.com\",\n" +
                "            \"words\": \"想购买你们的产品，你怎么回事,但是以后升级带宽，想知道你们的计费方式以及价格。\",\n" +
                "            \"begin\": 0,\n" +
                "            \"end\": 0,\n" +
                "            \"beginTime\": 1305252111000,\n" +
                "            \"hourMinSec\": \"00:00\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"role\": \"客服\",\n" +
                "            \"identity\": \"zhuier\",\n" +
                "            \"words\": \"我们在每一个创业者云计算服务的套餐中已经配备了2M多线路G口共享带宽，访问速度上比较有优越性，这个带宽是免费提供的。弄死你\\r\\n如果客户希望再增加带宽，具体购买可以咨询客服。\",\n" +
                "            \"begin\": 1581873000,\n" +
                "            \"end\": 1581873000,\n" +
                "            \"beginTime\": 1306833984000,\n" +
                "            \"hourMinSec\": \"07:24:33\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"context\": null\n" +
                "    }\n" +
                "  ], \n" +
                "\"commonRuleIds\": [\"91\",\"92\"]   \n" +
                "}";
        uploadDataWithRulesRequest.setJsonStr(dataJsonStr);
        UploadDataWithRulesResponse response = client.getAcsResponse(uploadDataWithRulesRequest);
        result = JSON.toJSONString(response);
        logger.info("---------uploadTextData------result------"+result);
		return result;
		
	}

    @RequestMapping("/uploadTextDataView")
    public ModelAndView uploadTextDataView() throws ServerException, ClientException{
        ModelAndView mav = new ModelAndView("uploadTextDataView");
		String result = "";
		logger.info("---------uploadTextDataView------------");
		UploadDataWithRulesRequest uploadDataWithRulesRequest = new UploadDataWithRulesRequest();
        uploadDataWithRulesRequest.setAcceptFormat(FormatType.JSON);
		IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID, SysConstant.ACCESS_KEY_SECRET);
		DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT, SysConstant.DOMAIN);
		IAcsClient client = new DefaultAcsClient(profile);
        String appkey=SysConstant.APP_KEY;
        String tidOne="10001";
        String tidTwo="10002";
        String dataJsonStr = "{\n" +
                "\"appKey\": \"" + appkey + "\",    \n" +
                "\"tickets\":[\n" +
                "    {\n" +
                "      \"tid\": \"" + tidOne + "\",\n" +
                "        \"dialogue\": \n" +
                "          [{\n" +
                "            \"role\": \"客户\",\n" +
                "            \"identity\": \"test_cm@aliyun.com\",\n" +
                "            \"words\": \"需要绑定预发环境的host，阿里云骗子工单透明化预付验证，请转单给测试组 陈明 谢谢\",\n" +
                "            \"begin\": 0,\n" +
                "            \"end\": 0,\n" +
                "            \"beginTime\": 1368468596000,\n" +
                "            \"hourMinSec\": \"00:00\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"role\": \"客服\",\n" +
                "            \"identity\": \"luoman.wyc\",\n" +
                "            \"words\": \"请稍等，正在核实中, 告诉我一下你的密码\",\n" +
                "            \"begin\": 840000,\n" +
                "            \"end\": 840000,\n" +
                "            \"beginTime\": 1368469436000,\n" +
                "            \"hourMinSec\": \"14:00\"\n" +
                "          }\n" +
                "        ]\n" +
                "  ,      \"context\": null\n" +
                "    }\n" +
                "    ,{\n" +
                "     \"tid\": \"" + tidTwo + "\",\n" +
                "        \"dialogue\": [\n" +
                "          {\n" +
                "            \"role\": \"客户\",\n" +
                "            \"identity\": \"chinaccnet@aliyun.com\",\n" +
                "            \"words\": \"想购买你们的产品，你怎么回事,但是以后升级带宽，想知道你们的计费方式以及价格。\",\n" +
                "            \"begin\": 0,\n" +
                "            \"end\": 0,\n" +
                "            \"beginTime\": 1305252111000,\n" +
                "            \"hourMinSec\": \"00:00\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"role\": \"客服\",\n" +
                "            \"identity\": \"zhuier\",\n" +
                "            \"words\": \"我们在每一个创业者云计算服务的套餐中已经配备了2M多线路G口共享带宽，访问速度上比较有优越性，这个带宽是免费提供的。弄死你\\r\\n如果客户希望再增加带宽，具体购买可以咨询客服。\",\n" +
                "            \"begin\": 1581873000,\n" +
                "            \"end\": 1581873000,\n" +
                "            \"beginTime\": 1306833984000,\n" +
                "            \"hourMinSec\": \"07:24:33\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"context\": null\n" +
                "    }\n" +
                "  ], \n" +
                "\"commonRuleIds\": [\"91\",\"92\"]   \n" +
                "}";
        uploadDataWithRulesRequest.setJsonStr(dataJsonStr);
        UploadDataWithRulesResponse response = client.getAcsResponse(uploadDataWithRulesRequest);
        result = JSON.toJSONString(response);
        mav.addObject("result", result);
        logger.info("---------mav------result------"+mav);
        return mav;     
    }


}
