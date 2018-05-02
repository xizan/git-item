package com.ztesoft.sca.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.qualitycheck.model.v20160801.GetAudioDataStatusRequest;
import com.aliyuncs.qualitycheck.model.v20160801.GetAudioDataStatusResponse;
import com.aliyuncs.qualitycheck.model.v20160801.GetResultRequest;
import com.aliyuncs.qualitycheck.model.v20160801.GetResultResponse;
import com.aliyuncs.qualitycheck.model.v20160801.GetResultResponse.ResultInfo;
import com.aliyuncs.qualitycheck.model.v20160801.GetRuleDetailRequest;
import com.aliyuncs.qualitycheck.model.v20160801.GetRuleDetailResponse;
import com.aliyuncs.qualitycheck.model.v20160801.GetScoreInfoRequest;
import com.aliyuncs.qualitycheck.model.v20160801.GetScoreInfoResponse;
import com.aliyuncs.qualitycheck.model.v20160801.SaveReviewResultRequest;
import com.aliyuncs.qualitycheck.model.v20160801.SaveReviewResultResponse;
import com.aliyuncs.qualitycheck.model.v20160801.UploadAudioDataWithRulesRequest;
import com.aliyuncs.qualitycheck.model.v20160801.UploadAudioDataWithRulesResponse;
import com.ztesoft.sca.common.SysConstant;
import com.ztesoft.sca.dao.AiChatDao;
import com.ztesoft.sca.model.AiChatDto;
import com.ztesoft.sca.common.Constants;
import com.ztesoft.sca.util.message.ResponseUtils;
import com.ztesoft.sca.util.exception.ExceptionUtil;
import com.ztesoft.sca.util.seq.SequenceCreator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * zxz 语音文件处理，获取分析结果，质检复核
 * */

@RestController
public class AiChatController {
	private static Logger logger = Logger.getLogger(AiChatController.class);
    @Autowired
    private AiChatDao aiChatDao;
    
    //默认文件保存路径
    @Value("${fileUpload.defaultUploadPath}")
    private String defaultUploadPath;
    
    //文件服务器存放地址 目录
    @Value("${fileUpload.filePathUrl}")
    private String filePathUrl;
    
    //请求方的回调地址
    @Value("${sca.FeedbackRecvPath}")
    private String FeedbackRecvPath;
    
    //文件服务器存放地址 实际目录 linux
    @Value("${fileUpload.actualFileServerUrl}")
    private String actualFileServerUrl;
	/*
	 * 语音接收报文
	 * 上传
	 * */
	@RequestMapping(value = "/aichat/SCAreceiveService",method = {RequestMethod.POST})
	public String receiveService(@RequestBody String requestBody,HttpServletRequest request) throws NumberFormatException, Exception {
		logger.info("-------SCAreceiveService------");
		String result = "";
		String receiveTaskId="";
		String ruleIds="";
/*		String cId ="1001";
		String fileId="100101";
		String fileCodec="wav";*/
		
		//zxz 存储语音文件到公网地址   
		String filePath=actualFileServerUrl;  //服务器地址 
		//String filePath=defaultUploadPath;      //本地地址
	    JSONObject json = JSONObject.fromObject(requestBody);
	    
        String cId = json.getString("cId");
        JSONArray jsonarr= json.getJSONArray("callList");
        String fileId=jsonarr.getJSONObject(0).getString("fileId");
        String fileCodec=jsonarr.getJSONObject(0).getString("fileCodec");
        String voiceDataString =jsonarr.getJSONObject(0).getString("voiceData");
        
        //v3经过base64解码转成byte数组
        byte[] voiceData=Base64.getDecoder().decode(voiceDataString); 
        if("v3".equals(fileCodec)){
        	fileCodec="wav";
        }
        String fileName=fileId+"."+fileCodec;
        
        //存储文件
        try {
	        boolean file= getFile(voiceData, filePath, fileName);
	        if(file==false){
	        	logger.error("接收的报文不符合要求");
	        }
        }catch (Exception e) {
        	logger.error("报文处理异常");
        }
        
        //记录报文信息，处理报文状态为 未获取分析结果：0，若成功获取分析结果，则更新为：1
        long receiveId = 0l;
        try {
        	receiveId = SequenceCreator.getSequence("SCA_RECEIVE_MSG_SEQ");
        } catch (Exception e) {
           logger.error("sequenceCreator obtain SCA_RECEIVE_MSG occur an error!",e);
           return ResponseUtils.feedback(Constants.INF_CODE_ERROR,Constants.INF_DESC_ERROR,ExceptionUtil.getMessage(e));
        }
        AiChatDto aiChat = new AiChatDto();
        aiChat.setReceiveId(receiveId);
        aiChat.setReceiveFileid(fileId);
        aiChat.setReceiveFilecodec(fileCodec);
        aiChat.setReceiveStatus("0");
        aiChat.setReceiveFilepath(filePath);
        try {
           this.aiChatDao.insertAiChat(aiChat);
        } catch (Exception e) {
           logger.error("create aiChat occur an error !",e);
        }
        
        //查询质检规则表
        List<AiChatDto> ruleIdStr=this.aiChatDao.selectAichatRules(Long.parseLong(cId));
        
        List<String> ruleIdList= new ArrayList<String>();
        for(AiChatDto  stu :  ruleIdStr ){
            String ruleId = stu.getRuleId();
            ruleIdList.add(ruleId);
    	}
        
        //质检上传 
		String voiceFileUrl=filePathUrl+"/"+fileName; //服务器地址
		//String voiceFileUrl=filePathUrl+"/100100110.wav"; //本地测试
		try{
			IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID, SysConstant.ACCESS_KEY_SECRET);
			DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT, SysConstant.DOMAIN);
			IAcsClient client = new DefaultAcsClient(profile);
			//指定规则上传待检数据(语音)
			UploadAudioDataWithRulesRequest uploadAudioDataRequest = new UploadAudioDataWithRulesRequest();
	        uploadAudioDataRequest.setAcceptFormat(FormatType.JSON);	        
			Map<String, Object> map = new HashMap<String, Object>();
			List<Map<String, Object>> callMapList = new ArrayList<Map<String, Object>>();
			Map<String, Object> callMap = new HashMap<String, Object>();
			
	        callMap.put("callStartTime", new Date().getTime());
	        callMap.put("clientChannel", 0);
	        callMap.put("sampleRate", 8);
	        callMap.put("serviceChannel", 1);
	        callMap.put("tid", fileId); 
	        callMap.put("voiceFileUrl", voiceFileUrl);
	        callMap.put("ruleIds", ruleIdList);
	        
	        callMapList.add(callMap);
	        
	        map.put("appKey", SysConstant.APP_KEY);
	        map.put("callList", callMapList);
	        map.put("clientChannel", 1);
	        map.put("sampleRate", 8);
	        map.put("serviceChannel", 0);
	        uploadAudioDataRequest.setJsonStr(JSON.toJSONString(map));

	        UploadAudioDataWithRulesResponse response = client.getAcsResponse(uploadAudioDataRequest);
	        result = JSON.toJSONString(response);
	        receiveTaskId=response.getData();
	        
	        //更新到记录表
	        AiChatDto aiChatUpdate = new AiChatDto();
	        aiChatUpdate.setReceiveId(receiveId);
	        aiChatUpdate.setReceiveFileid(fileId);
	        aiChatUpdate.setReceiveTaskid(receiveTaskId);
	        this.aiChatDao.updateAiChatTask(aiChatUpdate);
	        
	        logger.info("-----receiveService-----result----------"+result);
	        logger.info("--JSON.toJSONString(map)---"+JSON.toJSONString(map));
		}catch (ServerException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (ClientException e) {
			e.printStackTrace();
			result = e.getMessage();
		}
		
        //返回北向接口 taskId
		String taskId=receiveTaskId;
		JSONObject jsonStrAll = new JSONObject();
		jsonStrAll.put("taskId", taskId);
		return ResponseUtils.feedback(Constants.INF_CODE_SUCC,Constants.INF_DESC_SUCC,jsonStrAll);
	}
	
	/*
	 * 请求方接收质检结果
	 * */
	@RequestMapping(value = "/aichat/SCAFeedbackRecv")
	public List<ResultInfo> feedbackRecv(String taskId) {
        List<ResultInfo> resultGet=null;
        if("".equals(taskId)){
           taskId="676F89BC-3DBE-4066-AFD0-F56664854D76"; //7B5F9196-F883-48EF-951B-36ECB7191706
        }
        try{
			IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID, SysConstant.ACCESS_KEY_SECRET);
			DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT, SysConstant.DOMAIN);
			IAcsClient client = new DefaultAcsClient(profile);
			GetResultRequest getResultRequest = new GetResultRequest();
	        getResultRequest.setAcceptFormat(FormatType.JSON);
	        String queryJsonStr = "{\n" +
	                "\"appKey\": \"" + SysConstant.APP_KEY + "\",    \n" +
	                "\"taskId\": \""+taskId+"\",\n" + //质检任务taskId
	                "\"startTime\": \"2018-04-10 19:50:20\",\n" +
	                "\"endTime\": \"2056-07-14 23:24:44\"\n" +
	                "}";
	        getResultRequest.setJsonStr(queryJsonStr);
	        GetResultResponse responseGet = client.getAcsResponse(getResultRequest);
	        //resultGet = JSON.toJSONString(responseGet);
	        resultGet=responseGet.getData();
        }catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
		return resultGet;
        //return ResponseUtils.feedback(Constants.INF_CODE_SUCC,Constants.INF_DESC_SUCC,resultGet);
	}

	/*
	 * 质检复核   系统自动复核
	 * */
	@RequestMapping(value = "/aichat/SCAreviewAutoService")
	public String reviewAutoService(String param,String hitId,String taskId,String rid,String vid,List<Long> handScoreIdList) {
		logger.info("-----SCAreviewAutoService---系统自动复核----------");
		String resultResponse="";
		try{
			IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID, SysConstant.ACCESS_KEY_SECRET);
			DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT, SysConstant.DOMAIN);
			IAcsClient client = new DefaultAcsClient(profile);
			
	        SaveReviewResultRequest saveReviewResultRequest = new SaveReviewResultRequest();
	        saveReviewResultRequest.setAcceptFormat(FormatType.JSON);
	        String jsonStrSave = "\n{" +
	                "    \"handScoreIdList\":" +handScoreIdList+
	                ",\n" +
	                "    \"reviewInfoList\":[\n" +
	                "        {\n" +
	                "            \"hitId\":\""+hitId+"\",\n" + //质检语音命中id
	                "            \"reviewResult\":\""+param+"\",\n" +
	                "            \"rid\":\""+rid+"\"\n" +
	                "        }\n" +
	                "    ],\n" +
	                "    \"taskId\":\""+taskId+"\",\n" +
	                "    \"vid\":\""+vid+"\"\n" +
	                "}";
	        saveReviewResultRequest.setJsonStr(jsonStrSave);

	        //logger.info("-----SCAreviewAutoService---------"+jsonStrSave);
	        
	        SaveReviewResultResponse responseSave = client.getAcsResponse(saveReviewResultRequest);
	        resultResponse = JSON.toJSONString(responseSave);
		}catch (ServerException e) {
			e.printStackTrace();
			resultResponse = e.getMessage();
			return ResponseUtils.feedback(Constants.INF_CODE_ERROR, Constants.INF_DESC_ERROR, ExceptionUtil.getMessage(e));
		} catch (ClientException e) {
			e.printStackTrace();
			resultResponse = e.getMessage();
			return ResponseUtils.feedback(Constants.INF_CODE_ERROR, Constants.INF_DESC_ERROR, ExceptionUtil.getMessage(e));
		}
		return ResponseUtils.feedback(Constants.INF_CODE_SUCC,Constants.INF_DESC_SUCC,"返回质检复核");
	}
	
	/*
	 * 质检复核  手动复核
	 * */
	@RequestMapping(value = "/aichat/SCAreviewService",method = {RequestMethod.POST})
	public String reviewService(@RequestBody String requestBody,HttpServletRequest request) {
		JSONObject json = JSONObject.fromObject(requestBody);
        String taskId=json.getString("taskId");
        String vid = json.getString("vid"); //vid=1000=fileId
        
        JSONArray jsonarr= json.getJSONArray("reviewInfoList");
        String hitId=jsonarr.getJSONObject(0).getString("hitId");
        String rid=jsonarr.getJSONObject(0).getString("rid");  //130
        String reviewResult=jsonarr.getJSONObject(0).getString("reviewResult");
        
        JSONArray jsonScore = json.getJSONArray("handScoreIdList");
        List<Long> handScoreIdList=new ArrayList<>();
        for (int i = 0; i < jsonScore.size(); i++) {  
        	handScoreIdList.add(jsonScore.getLong(i));  
        } 

		String resultResponse="";
		try{
			IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID, SysConstant.ACCESS_KEY_SECRET);
			DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT, SysConstant.DOMAIN);
			IAcsClient client = new DefaultAcsClient(profile);
	        SaveReviewResultRequest saveReviewResultRequest = new SaveReviewResultRequest();
	        saveReviewResultRequest.setAcceptFormat(FormatType.JSON);
	        String jsonStrSave = "\n{" +
	                "    \"handScoreIdList\":" +handScoreIdList +",\n" +
	                "    \"reviewInfoList\":[\n" +
	                "        {\n" +
	                "            \"hitId\":\""+hitId+"\",\n" + //质检语音命中id
	                "            \"reviewResult\":\""+reviewResult+"\",\n" +
	                "            \"rid\":\""+rid+"\"\n" +
	                "        }\n" +
	                "    ],\n" +
	                "    \"taskId\":\""+taskId+"\",\n" +
	                "    \"vid\":\""+vid+"\"\n" +
	                "}";
	        saveReviewResultRequest.setJsonStr(jsonStrSave);
	        SaveReviewResultResponse responseSave = client.getAcsResponse(saveReviewResultRequest);
	        resultResponse = JSON.toJSONString(responseSave);
	        logger.info("----------reviewService---手动复核-------"+resultResponse);
		}catch (ServerException e) {
			e.printStackTrace();
			resultResponse = e.getMessage();
			return ResponseUtils.feedback(Constants.INF_CODE_ERROR, Constants.INF_DESC_ERROR, ExceptionUtil.getMessage(e));
		} catch (ClientException e) {
			e.printStackTrace();
			resultResponse = e.getMessage();
			return ResponseUtils.feedback(Constants.INF_CODE_ERROR, Constants.INF_DESC_ERROR, ExceptionUtil.getMessage(e));
		}
		return ResponseUtils.feedback(Constants.INF_CODE_SUCC,Constants.INF_DESC_SUCC,"质检复核成功");
	}
	
		 /*
		 * zxz 轮询查询 质检分析结果状态
		 * 根据质检分析状态  
		 * 获取质检分析结果
		 * */
		@RequestMapping(value = "/aichat/PollingThreadRun") 
	   public String PollingThreadRun(String jsonStr) throws ServerException, ClientException, ClientProtocolException, IOException{
		    String taskStr="";
		    
	        //对质检上传之后获取分析结果状态进行轮询
			IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID, SysConstant.ACCESS_KEY_SECRET);
			DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT, SysConstant.DOMAIN);
			IAcsClient client = new DefaultAcsClient(profile);
			
	        GetAudioDataStatusRequest getAudioDataStatusRequest = new GetAudioDataStatusRequest();
	        getAudioDataStatusRequest.setAcceptFormat(FormatType.JSON);
	        Map<String, Object> callMap = new HashMap<String, Object>();
	        callMap.put("appKey", SysConstant.APP_KEY);
           
	        //查询taskid对应的状态为0的记录
	        String receiveStatus="0";
	        List<Long> listId= new ArrayList<>();
		   try {
			   List<AiChatDto> taskList=this.aiChatDao.selectAichatTaskList(receiveStatus);
		        for(AiChatDto pLog : taskList){
		        	//zxz 查询分析状态，是否获取分析完毕
		        	String taskId=pLog.getReceiveTaskid();
			        callMap.put("taskId", taskId);
			        callMap.put("tid", pLog.getReceiveFileid());
			        getAudioDataStatusRequest.setJsonStr(JSON.toJSONString(callMap));
			        GetAudioDataStatusResponse response = client.getAcsResponse(getAudioDataStatusRequest);
			        String resultGet = JSON.toJSONString(response);
			        
			        //处理过程状态码       0 – 新建        1 – 语音转换中      2 – 语音转换完成     3 – 分析完成       -1 – 错误
			        JSONArray jsonArray = JSONArray.fromObject(response.getData());  
			        logger.info("--taskId--获取分析结果状态码----"+taskId+"---"+jsonArray.getJSONObject(0).get("statusCode"));
			        int statusCode=(int) jsonArray.getJSONObject(0).get("statusCode");
			        if(statusCode==3){
			        	taskStr=taskStr+","+taskId;
			        	listId.add(pLog.getReceiveId());
			        }
		        }
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		   
		   //队已经完成语音分析的taskid，执行获取分析结果值
		   Map<String, Object> resultInfoMap = new HashMap<String, Object>();
		  
		   if(taskStr.length()>0){
			   //增加 直接根据获取的状态，获取 算子  包含的时间   大于   请求方  发送过来的 上门服务时间   超过一个小时 则直接调用复核接口去命中该rule 任务，并更新数据状态
			   //调用质检规则接口 获取算子中是否包含的 时间点14：00
		        List<String> ruleIdList= new ArrayList<String>();
		        //ruleIdList.add("130");
		        ruleIdList.add("131");
			   String checkWord=testRules(ruleIdList);
			   
			   if(checkWord.indexOf("14:00")>-1){
				   //直接调用质检复核接口  命中评分项  1为命中
				   String param="1";
					String hitId="95eebf69a9604229900f5a4a613c3b8a"; //95eebf69a9604229900f5a4a613c3b8a
					String taskId="676F89BC-3DBE-4066-AFD0-F56664854D76";
					String rid="130";
					String vid="1000";
					List<Long> handScoreIdList=new ArrayList<>();
					Long scoreList=27l;
					handScoreIdList.add(scoreList);
					handScoreIdList.add(7l);
					
					reviewAutoService(param, hitId, taskId, rid, vid, handScoreIdList);
			   }

			   //否则直接执行下面的获取分析结果和相关信息更新
			   String taskStrSub=taskStr.substring(1);
			   if(!"".equals(taskStrSub)){
				   String[] str = taskStrSub.split(","); 
				   for(String taskId:str){
					   //调用 分析结果接口  获取详细内容
					   List<ResultInfo> ResultInfo=feedbackRecv(taskId);
					   
					   resultInfoMap.put("ResultInfo", ResultInfo);
					  
					   //调用 计分项接口，获取对应的计分项信息
					   List<String>   scorePo=ScorePo("");
					   resultInfoMap.put("ScorePo", scorePo); 
					   
					   //请求方的回调地址   httpClien发送json数据
				        final String CONTENT_TYPE_TEXT_JSON = "text/json";
				        DefaultHttpClient clientFeedback = new DefaultHttpClient(new PoolingClientConnectionManager());
				        String url = FeedbackRecvPath;  //请求方回调地址
				        String js = "";
				        JSONObject jsonStrAll = new JSONObject();
						jsonStrAll.put("data", resultInfoMap);  
						jsonStrAll.put("resultCode", Constants.INF_CODE_SUCC); 
						jsonStrAll.put("resultDesc", Constants.INF_DESC_SUCC);
						js=JSON.toJSONString(jsonStrAll);
						
						logger.info("------请求方获取分析结果的响应---jsonStrAll---"+jsonStrAll);
						
				        HttpPost httpPost = new HttpPost(url);       
				        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
				               
				        StringEntity se = new StringEntity(js);
				        se.setContentType(CONTENT_TYPE_TEXT_JSON);
	
				        httpPost.setEntity(se);
				        
				        CloseableHttpResponse response2 = null;
				        
				        response2 = clientFeedback.execute(httpPost);
				        HttpEntity entity2 = null;
				        entity2 = response2.getEntity();
				        String s2 = EntityUtils.toString(entity2, "UTF-8");
				        
				        //logger.info("------请求方获取分析结果的响应--s2-----"+s2);
				        
				        com.alibaba.fastjson.JSONObject jsonObject= JSON.parseObject(s2);
				        String resultCode = jsonObject.getString("resultCode");
				        //logger.info("------请求方返回 resultCode---"+resultCode);
				   } 
				   
				   //给状态为3 即完成分析的taskId对应的状态receiveStatus更新操作为1
				   try {
					    this.aiChatDao.updateAiChatTaskInStr(listId);
					    //logger.info("---------updateAiChatTaskInStr----更新成功-taskId------"+taskStrSub);
					} catch (Exception e) {
						logger.error(" updateAiChatTaskInStr occur an error!",e);
					}
			   }
		   }else{
			   logger.info("-----无  任务调度-------"); 
		   }
		   return ResponseUtils.feedback(Constants.INF_CODE_SUCC,Constants.INF_DESC_SUCC,"返回质检复核");
	   }
	
	/*
	 * zxz 定时任务执行,
	 * 请求方回调 分析结果
	 * */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Integer count0 = 1;
    @Scheduled(fixedDelay = 60000)
    public void reportCurrentTimeAfterSleep() throws InterruptedException {
        System.out.println(String.format("===定时任务===第%s次执行，当前时间为：%s", count0++, dateFormat.format(new Date())));
        try {
			PollingThreadRun("");
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	/*
	 * 获取规则明细
	 * */
	@RequestMapping(value = "/aichat/TestRules")
	public String testRules(List<String> ruleIdList) throws ServerException, ClientException {
		IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID, SysConstant.ACCESS_KEY_SECRET);
		try {
			DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT, SysConstant.DOMAIN);
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IAcsClient client = new DefaultAcsClient(profile);
        GetRuleDetailRequest getRuleDetailRequest = new GetRuleDetailRequest();
        getRuleDetailRequest.setAcceptFormat(FormatType.JSON);
        
        Map<String, Object> callMap =  new HashMap<String, Object>();
        callMap.put("appKey", SysConstant.APP_KEY);
        callMap.put("ruleIds", ruleIdList);
        callMap.put("isDelete", 0);
        callMap.put("status", 1);
        getRuleDetailRequest.setJsonStr(JSON.toJSONString(callMap));
        GetRuleDetailResponse response = client.getAcsResponse(getRuleDetailRequest);
        String paramWord=response.getData().getConditions().get(0).getOperators().get(0).getParam().getReferences().get(0);
        String resultGet = JSON.toJSONString(response);
        
        logger.info("----获取规则明细-----"+resultGet+"--算子----"+paramWord); 
        return paramWord;
        //return ResponseUtils.feedback(Constants.INF_CODE_SUCC,Constants.INF_DESC_SUCC,resultGet);
	}
	
	/*
	 * 获取计分项信息
	 * */
	@RequestMapping(value = "/aichat/ScorePo")
	public List<String> ScorePo(String jsonStr) throws ServerException, ClientException {
		IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID, SysConstant.ACCESS_KEY_SECRET);
		try {
			DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT, SysConstant.DOMAIN);
		} catch (ClientException e) {
			e.printStackTrace();
		}
		IAcsClient client = new DefaultAcsClient(profile);
		
        GetScoreInfoRequest getScoreInfoRequest = new GetScoreInfoRequest();
        getScoreInfoRequest.setAcceptFormat(FormatType.JSON);
        Map<String, Object> callMap =  new HashMap<String, Object>();
        callMap.put("appKey", SysConstant.APP_KEY);
        getScoreInfoRequest.setJsonStr(JSON.toJSONString(callMap));
        GetScoreInfoResponse response = client.getAcsResponse(getScoreInfoRequest);
        String resultGet = JSON.toJSONString(response);
        
        //获取Json数组
        JSONArray employees = new JSONArray();
        employees=JSONArray.fromObject(response.getData());
        
        //建立 obj和array
        JSONObject jsonObj = new JSONObject();
		JSONObject jsonSub = new JSONObject();
		
        for(int i=0; i<employees.size(); i++) {
        	JSONObject emp = employees.getJSONObject(i); //大项

        	if(emp.get("scoreId").toString().contains("-21")){
        		 jsonObj.put("scoreId", emp.get("scoreId")); 
        		 jsonObj.put("scoreName", emp.get("scoreName")); 
        		 
	            //System.out.println(emp.toString());
	             
	            JSONArray scoreInfosList = new JSONArray();
	            scoreInfosList=JSONArray.fromObject(emp.get("scoreInfos"));
	            for(int j=0; j<scoreInfosList.size(); j++) {
	            	JSONObject score = scoreInfosList.getJSONObject(j); //子项

	            	if(score.get("scoreSubId").toString().contains("27")){
	            		jsonSub.put("scoreSubName", score.get("scoreSubName")); 
	            		jsonSub.put("scoreSubId", score.get("scoreSubId")); 
	            		jsonSub.put("scoreType", score.get("scoreType")); 
	            		jsonSub.put("scoreNum", score.get("scoreNum")); 
	            	}
	            }
	            jsonObj.put("scoreInfos", jsonSub); 
        	}
        }
        List<String> scoreList=new ArrayList<>();
        scoreList.add(jsonObj.toString());
        
        logger.info("----获取计分项  -21 大项  27 小项-----"+scoreList); 
		return scoreList;
        //return ResponseUtils.feedback(Constants.INF_CODE_SUCC,Constants.INF_DESC_SUCC,scoreMap);
	}
	
	   /** 
	     * 获得指定文件的byte数组 
	     */  
	    public static byte[] getBytes(String filePath){  
	        byte[] buffer = null;  
	        try {  
	            File file = new File(filePath);  
	            FileInputStream fis = new FileInputStream(file);  
	            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
	            byte[] b = new byte[1000];  
	            int n;  
	            while ((n = fis.read(b)) != -1) {  
	                bos.write(b, 0, n);  
	            }  
	            fis.close();  
	            bos.close();  
	            buffer = bos.toByteArray();  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        return buffer;  
	    }
	    
		/** 
	     * 根据byte数组，生成文件 
		 * @return 
	     */  
	    public static boolean getFile(byte[] bfile, String filePath,String fileName) {  
	        BufferedOutputStream bos = null;  
	        FileOutputStream fos = null;  
	        File file = null;  
	        try {  
	            File dir = new File(filePath);  
	            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在  
	                dir.mkdirs();  
	            }  
	            file = new File(filePath+"/"+fileName);  
	            fos = new FileOutputStream(file);  
	            bos = new BufferedOutputStream(fos);  
	            bos.write(bfile);  
	            logger.info("文件保存成功:"+fileName);
	            return true;
	        } catch (Exception e) {  
	        	logger.error("保存文件失败", e);
	        	return false;
	        } finally {  
	            if (bos != null) {  
	                try {  
	                    bos.close();  
	                } catch (IOException e1) {  
	                	logger.error("保存文件失败", e1);
	                	return false;
	                }  
	            }  
	            if (fos != null) {  
	                try {  
	                    fos.close();  
	                } catch (IOException e1) {  
	                	logger.error("保存文件失败", e1);
	                	return false;
	                }  
	            }  
	        }
	    }
	    
	    //测试获取分析结果状态
		@RequestMapping(value = "/aichat/TestStatus",method = {RequestMethod.POST})
		public String testStatus(@RequestBody String requestBody,HttpServletRequest request) throws ServerException, ClientException {
			JSONObject json = JSONObject.fromObject(requestBody);
	        String taskId=json.getString("taskId");
	        String tid=json.getString("tid");
	        
	        //对质检上传之后获取分析结果状态进行轮询
			IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID, SysConstant.ACCESS_KEY_SECRET);
			try {
				DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT, SysConstant.DOMAIN);
			} catch (ClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			IAcsClient client = new DefaultAcsClient(profile);		
	        GetAudioDataStatusRequest getAudioDataStatusRequest = new GetAudioDataStatusRequest();
	        getAudioDataStatusRequest.setAcceptFormat(FormatType.JSON);
	        Map<String, Object> callMap =  new HashMap<String, Object>();
	        callMap.put("appKey", SysConstant.APP_KEY);
	        callMap.put("taskId", taskId);
	        callMap.put("tid",  tid);//bce20f94145c4fda8289273ab911e9a9
	        getAudioDataStatusRequest.setJsonStr(JSON.toJSONString(callMap));
	        GetAudioDataStatusResponse response = client.getAcsResponse(getAudioDataStatusRequest);
	        String resultGet = JSON.toJSONString(response);
	        
	        System.out.println("----TestStatus----"+resultGet);
			return ResponseUtils.feedback(Constants.INF_CODE_SUCC,Constants.INF_DESC_SUCC,resultGet);
		}
		
		/*
		 * 请求方接收质检结果   测试
		 * */
		@RequestMapping(value = "/aichat/SCAFeedbackRecvTest",method = {RequestMethod.POST})
		public String feedbackRecvTest(@RequestBody String requestBody,HttpServletRequest request) {
			JSONObject json = JSONObject.fromObject(requestBody);
	        String taskId=json.getString("taskId");
	        
	        List<ResultInfo> resultGet=null;
	        try{
				IClientProfile profile = DefaultProfile.getProfile(SysConstant.REGION_ID, SysConstant.ACCESS_KEY_ID, SysConstant.ACCESS_KEY_SECRET);
				DefaultProfile.addEndpoint(SysConstant.ENDPOINT_NAME, SysConstant.REGION_ID, SysConstant.PRODUCT, SysConstant.DOMAIN);
				IAcsClient client = new DefaultAcsClient(profile);
				GetResultRequest getResultRequest = new GetResultRequest();
		        getResultRequest.setAcceptFormat(FormatType.JSON);
		        String queryJsonStr = "{\n" +
		                "\"appKey\": \"" + SysConstant.APP_KEY + "\",    \n" +
		                "\"taskId\": \""+taskId+"\",\n" + //质检任务taskId
		                "\"startTime\": \"2018-04-10 19:50:20\",\n" +
		                "\"endTime\": \"2056-07-14 23:24:44\"\n" +
		                "}";
		        getResultRequest.setJsonStr(queryJsonStr);
		        GetResultResponse responseGet = client.getAcsResponse(getResultRequest);
		        //resultGet = JSON.toJSONString(responseGet);
		        resultGet=responseGet.getData();
	        }catch (ServerException e) {
				e.printStackTrace();
			} catch (ClientException e) {
				e.printStackTrace();
			}
	        
	        System.out.println("----feedbackRecvTest----"+resultGet);
	        return ResponseUtils.feedback(Constants.INF_CODE_SUCC,Constants.INF_DESC_SUCC,resultGet);
		}
}
