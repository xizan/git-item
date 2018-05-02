package com.ztesoft.sca.model;

/**
 * @author kira
 * @created 2018 - 03 - 15 3:19 PM
 */
public class Response {
    private String code;
    private String resultDesc;
    private Object data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
