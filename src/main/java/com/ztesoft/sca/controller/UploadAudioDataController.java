package com.ztesoft.sca.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Maps;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.aliyuncs.qualitycheck.model.v20160801.GetResultRequest;
import com.aliyuncs.qualitycheck.model.v20160801.GetResultResponse;
import com.aliyuncs.qualitycheck.model.v20160801.SaveReviewResultRequest;
import com.aliyuncs.qualitycheck.model.v20160801.SaveReviewResultResponse;
import com.aliyuncs.qualitycheck.model.v20160801.UploadAudioDataRequest;
import com.aliyuncs.qualitycheck.model.v20160801.UploadAudioDataResponse;
import com.aliyuncs.qualitycheck.model.v20160801.UploadAudioDataWithRulesRequest;
import com.aliyuncs.qualitycheck.model.v20160801.UploadAudioDataWithRulesResponse;
import com.aliyuncs.qualitycheck.model.v20160801.UploadDataWithRulesRequest;
import com.aliyuncs.qualitycheck.model.v20160801.UploadDataWithRulesResponse;
import com.ztesoft.sca.common.SysConstant;
import com.ztesoft.sca.util.UUIDGenerator;

@RestController
public class UploadAudioDataController {
	private static Logger logger = Logger.getLogger(UploadAudioDataController.class);

	@RequestMapping(value = "/uploadAudioData") //, method = { RequestMethod.POST }
	public String uploadAudioData(String jsonStr) { //@RequestBody 
		String result = "";
		String resultGet ="";
		String resultResponse="";
		String resultResponseRule="";
		logger.info("----------uploadAudioData------------------");
		try {
			IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID, SysConstant.ACCESS_KEY_SECRET);
			DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT, SysConstant.DOMAIN);
			IAcsClient client = new DefaultAcsClient(profile);
			//zxz 上传
			
			
			UploadAudioDataRequest uploadAudioDataRequest = new UploadAudioDataRequest();
			uploadAudioDataRequest.setAcceptFormat(FormatType.JSON);
			Map<String, Object> map = new HashMap<String, Object>();
			List<Map<String, Object>> callMapList = new ArrayList<Map<String, Object>>();
			Map<String, Object> callMap = new HashMap<String, Object>();
			//String voiceFileUrl="https://sca-test.oss-cn-beijing.aliyuncs.com/%E9%BB%98%E8%AE%A4%E6%B5%8B%E8%AF%95%E5%BD%95%E9%9F%B3%E6%95%B0%E6%8D%AE1.wav?Expires=1523241066&OSSAccessKeyId=TMP.AQEsLsvktpw9iuV2z8Y4xUYEtkQzxq3sFhAeMLYCkUgSC4gPxNH8eMPzUGtMMC4CFQCPLieu0uvcldkY1zKSHNauPv4IHgIVAILuSAF2iYgqIixAQxemYp_RbPsS&Signature=GagykaGAGPy%2Fmn91Z4DjRtmMct0%3D";
			//String voiceFileUrl="https://zhengxizan.oss-cn-hangzhou.aliyuncs.com/sca-test/001FJ.wav?Expires=1524086823&OSSAccessKeyId=TMP.AQFM3gr_QztCuOo5Xnu8RqDz1LSECRvo7KEzRbfWceGWaUhCM2d_BiEq3nTWAAAwLAIUQaveXBcO1idzplx9Qi_AmwdtvEICFA4Lr_Y1hO9FuGIn5zqSPpCy3u6f&Signature=YBz6Q0EaTNMsybYME0GR34jyHVg%3D";
			String voiceFileUrl="http://47.106.107.162:9003/IMChat-fileServer/testVoice/default_demo1.wav";
			callMap.put("voiceFileUrl", voiceFileUrl);
			callMap.put("serviceChannel", 0);
			callMap.put("clientChannel", 1);
			callMap.put("sampleRate", 8);
			callMap.put("callStartTime", new Date().getTime());
			callMap.put("tid", UUIDGenerator.getUUID());
			
			callMapList.add(callMap);
			
			map.put("appKey", SysConstant.APP_KEY);
			map.put("serviceChannel", 1);
			map.put("clientChannel", 0);
			map.put("sampleRate", 8);
			map.put("callList", callMapList);
			map.put("recognizeRoleDataSetId", 111);
			uploadAudioDataRequest.setJsonStr(JSON.toJSONString(map));
			UploadAudioDataResponse response = client.getAcsResponse(uploadAudioDataRequest);
			
			if (logger.isInfoEnabled()) {
				logger.info(JSON.toJSONString(response));
			}
			result = JSON.toJSONString(response);
			logger.info("-----uploadAudioData-----result----------"+result+"----response.getData()--"+JSON.toJSONString(map));
			
			//做轮询去获取分析结果主要是语音分析过程需要时间，所以需要轮询
			//zxz 获取分析结果
	       // String getResult= PollingThreadRun(response.getData());
	        //logger.info("-----uploadAudioData-----getResult----------"+getResult);
	        
	        //zxz 复核提交
/*	        String hitId="";
	        //result 获取hitId
	        SaveReviewResultRequest saveReviewResultRequest = new SaveReviewResultRequest();
	        saveReviewResultRequest.setAcceptFormat(FormatType.JSON);
	        String jsonStrSave = "{\n" +
	                "    \"handScoreIdList\":[\n" +
	                "        267\n" +
	                "    ],\n" +
	                "    \"reviewInfoList\":[\n" +
	                "        {\n" +
	                "            \"hitId\":\""+hitId+"\",\n" + //质检语音命中id
	                "            \"reviewResult\":0,\n" +
	                "            \"rid\":314\n" +
	                "        }\n" +
	                "    ],\n" +
	                "    \"taskId\":\""+response.getData()+"\",\n" +
	                "    \"vid\":\"*******\"\n" +
	                "}";
	        saveReviewResultRequest.setJsonStr(jsonStrSave);
	        SaveReviewResultResponse responseSave = client.getAcsResponse(saveReviewResultRequest);
	        resultResponse = JSON.toJSONString(responseSave);
	        logger.info("-----uploadAudioData-----responseSave----------"+resultResponse);*/
	        
	        //zxz 指定规则上传待检数据
/*	        UploadAudioDataWithRulesRequest uploadAudioDataWithRulesRequest = new UploadAudioDataWithRulesRequest();
	        uploadAudioDataRequest.setAcceptFormat(FormatType.JSON);
	        Map<String, Object> mapRule = Maps.newHashMap();
	        mapRule.put("appKey", SysConstant.APP_KEY);
	        List<Map<String, Object>> callRuleMapList = Lists.newArrayList();
	        Map<String, Object> callRuleMap = Maps.newHashMap();
	        callRuleMap.put("callStartTime", new Date().getTime());
	        callRuleMap.put("clientChannel", 0);
	        callRuleMap.put("sampleRate", 8);
	        callRuleMap.put("serviceChannel", 1);
	        callRuleMap.put("tid", UUIDGenerator.getUUID());
	        callRuleMap.put("voiceFileUrl", voiceFileUrl);
	        callRuleMap.put("ruleIds", "[3,4]");
	        callMapList.add(callRuleMap);
	        mapRule.put("callList", callMapList);
	        mapRule.put("clientChannel", 1);
	        mapRule.put("sampleRate", 8);
	        mapRule.put("serviceChannel", 0);
	        uploadAudioDataWithRulesRequest.setJsonStr(JSON.toJSONString(mapRule));
	        UploadAudioDataWithRulesResponse responseRule = client.getAcsResponse(uploadAudioDataWithRulesRequest);
	        resultResponseRule = JSON.toJSONString(responseRule);
	        logger.info("-----uploadAudioData-----resultResponseRule----------"+resultResponseRule);*/
	        
		} catch (ServerException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (ClientException e) {
			e.printStackTrace();
			result = e.getMessage();
		}

		return result+"\n"+resultGet;
	}

	   @RequestMapping("/uploadAudioDataView")
	    public ModelAndView uploadAudioDataView() throws ServerException, ClientException{
	        ModelAndView mav = new ModelAndView("uploadAudioDataView");
			String result = "";
			logger.info("---------uploadAudioDataView------------");
			IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID, SysConstant.ACCESS_KEY_SECRET);
			DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT, SysConstant.DOMAIN);
			IAcsClient client = new DefaultAcsClient(profile);

			UploadAudioDataRequest uploadAudioDataRequest = new UploadAudioDataRequest();
			uploadAudioDataRequest.setAcceptFormat(FormatType.JSON);
			
			Map<String, Object> map = new HashMap<String, Object>();
			
			List<Map<String, Object>> callMapList = new ArrayList<Map<String, Object>>();
			
			Map<String, Object> callMap = new HashMap<String, Object>();
			String voiceFileUrl="https://sca-test.oss-cn-beijing.aliyuncs.com/%E9%BB%98%E8%AE%A4%E6%B5%8B%E8%AF%95%E5%BD%95%E9%9F%B3%E6%95%B0%E6%8D%AE1.wav?Expires=1523241066&OSSAccessKeyId=TMP.AQEsLsvktpw9iuV2z8Y4xUYEtkQzxq3sFhAeMLYCkUgSC4gPxNH8eMPzUGtMMC4CFQCPLieu0uvcldkY1zKSHNauPv4IHgIVAILuSAF2iYgqIixAQxemYp_RbPsS&Signature=GagykaGAGPy%2Fmn91Z4DjRtmMct0%3D";
			callMap.put("voiceFileUrl", voiceFileUrl);
			callMap.put("serviceChannel", 0);
			callMap.put("clientChannel", 1);
			callMap.put("sampleRate", 8);
			callMap.put("callStartTime", new Date().getTime());
			callMap.put("tid", UUIDGenerator.getUUID());
			
			callMapList.add(callMap);
			
			map.put("appKey", SysConstant.APP_KEY);
			map.put("serviceChannel", 1);
			map.put("clientChannel", 0);
			map.put("sampleRate", 8);
			map.put("callList", callMapList);
			map.put("recognizeRoleDataSetId", 111);
			uploadAudioDataRequest.setJsonStr(JSON.toJSONString(map));
			
			UploadAudioDataResponse response = client.getAcsResponse(uploadAudioDataRequest);
			
			if (logger.isInfoEnabled()) {
				logger.info(JSON.toJSONString(response));
			}
			result = JSON.toJSONString(response);
	        mav.addObject("result", result);
	        logger.info("---------uploadAudioDataView------result------"+mav);
	        return mav;     
	    }
}
