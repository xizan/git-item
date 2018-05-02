package com.ztesoft.sca.model;

public class AiChatDto {
    private long receiveId;

    private String receiveTaskid;

    private String receiveFileid;

    private String receiveFilecodec;

    private String receiveStatus;
    
    private String receiveFilepath;

    private String ruleId;
    
    public long getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(long receiveId) {
        this.receiveId = receiveId;
    }

    public String getReceiveTaskid() {
        return receiveTaskid;
    }

    public void setReceiveTaskid(String receiveTaskid) {
        this.receiveTaskid = receiveTaskid == null ? null : receiveTaskid.trim();
    }

    public String getReceiveFileid() {
        return receiveFileid;
    }

    public void setReceiveFileid(String receiveFileid) {
        this.receiveFileid = receiveFileid == null ? null : receiveFileid.trim();
    }

    public String getReceiveFilecodec() {
        return receiveFilecodec;
    }

    public void setReceiveFilecodec(String receiveFilecodec) {
        this.receiveFilecodec = receiveFilecodec == null ? null : receiveFilecodec.trim();
    }

    public String getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(String receiveStatus) {
        this.receiveStatus = receiveStatus == null ? null : receiveStatus.trim();
    }

    public String getReceiveFilepath() {
        return receiveFilepath;
    }

    public void setReceiveFilepath(String receiveFilepath) {
        this.receiveFilepath = receiveFilepath == null ? null : receiveFilepath.trim();
    }
 
    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId == null ? null : ruleId.trim();
    }
    
}
