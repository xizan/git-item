package com.ztesoft.sca.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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
import com.aliyuncs.qualitycheck.model.v20160801.UploadAudioDataRequest;
import com.aliyuncs.qualitycheck.model.v20160801.UploadAudioDataResponse;
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
		logger.info("----------uploadAudioData------------------");
		try {
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
			logger.info("-----uploadAudioData-----result----------"+result);
		} catch (ServerException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (ClientException e) {
			e.printStackTrace();
			result = e.getMessage();
		}

		return result;
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
