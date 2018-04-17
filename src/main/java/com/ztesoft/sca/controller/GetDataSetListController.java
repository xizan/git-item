package com.ztesoft.sca.controller;

import java.util.HashMap;
import java.util.Map;

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
import com.aliyuncs.qualitycheck.model.v20160801.GetDataSetListRequest;
import com.aliyuncs.qualitycheck.model.v20160801.GetDataSetListResponse;
import com.ztesoft.sca.common.SysConstant;

@RestController
public class GetDataSetListController {
	private static Logger logger = Logger.getLogger(GetDataSetListController.class);

	@RequestMapping(value = "/getDataSetList", method = { RequestMethod.POST })
	public String getDataSetList(@RequestBody String jsonStr) {
		String result = "";
		try {
			IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID,
					SysConstant.ACCESS_KEY_SECRET);
			DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT,
					SysConstant.DOMAIN);
			IAcsClient client = new DefaultAcsClient(profile);

			GetDataSetListRequest getDataSetListRequest = new GetDataSetListRequest();
			getDataSetListRequest.setAcceptFormat(FormatType.JSON);
			Map<String, Object> callMap = new HashMap<String, Object>();
			callMap.put("appKey", SysConstant.APP_KEY);
			callMap.put("setId", 111);
			getDataSetListRequest.setJsonStr(JSON.toJSONString(callMap));
			GetDataSetListResponse response = client.getAcsResponse(getDataSetListRequest);

			if (logger.isInfoEnabled()) {
				logger.info(JSON.toJSONString(response));
			}
			result = JSON.toJSONString(response);

		} catch (ServerException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (ClientException e) {
			e.printStackTrace();
			result = e.getMessage();
		}

		return result;
	}
}
