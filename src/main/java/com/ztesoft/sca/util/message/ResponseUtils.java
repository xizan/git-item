package com.ztesoft.sca.util.message;

import com.ztesoft.sca.model.Response;
import com.ztesoft.sca.util.json.GsonUtils;

public class ResponseUtils {

    public static String feedback(String code,String desc,Object obj){
        Response response = new Response();
        response.setCode(code);
        response.setResultDesc(desc);
        response.setData(obj);
        return GsonUtils.toJson(response);
    }
}
