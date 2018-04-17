package com.ztesoft.sca.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.qualitycheck.model.v20160801.UploadRuleRequest;
import com.aliyuncs.qualitycheck.model.v20160801.UploadRuleResponse;
import com.ztesoft.sca.common.SysConstant;

@RestController
public class UploadRuleController {
	private static Logger logger = Logger.getLogger(UploadRuleController.class);

	@RequestMapping(value = "/uploadRule", method = { RequestMethod.POST })
	public String uploadRule(@RequestBody String jsonStr) {
		try {
			IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID,
					SysConstant.ACCESS_KEY_SECRET);
			DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT,
					SysConstant.DOMAIN);
			IAcsClient client = new DefaultAcsClient(profile);

			UploadRuleRequest uploadRuleRequest = new UploadRuleRequest();
			uploadRuleRequest.setAcceptFormat(FormatType.JSON);
			uploadRuleRequest.setJsonStr(jsonStr);
			UploadRuleResponse response = client.getAcsResponse(uploadRuleRequest);

			logger.info(JSON.toJSONString(response));

		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}

		return null;
	}
}
