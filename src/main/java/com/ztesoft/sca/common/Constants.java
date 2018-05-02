package com.ztesoft.sca.common;

import java.util.ResourceBundle;

/**
 * @author kira
 * @created 2018 - 03 - 15 3:04 PM
 */
public class Constants {
    public static ResourceBundle sysBundle = ResourceBundle.getBundle("config.sysconfig");
    public static ResourceBundle aliyunBundle = ResourceBundle.getBundle("config.aliyun");

    public static final String INF_CODE_SUCC = "0";

    public static final String INF_CODE_ERROR = "9999";

    public static final String INF_DESC_SUCC = "success";

    public static final String INF_DESC_ERROR = "error";

    //-----------north--------
    public static final String INF_NORTH_QUERY = sysBundle.getString("inf.north.query");
    public static final String INF_NORTH_FEEDBACK= sysBundle.getString("inf.north.feedback");

    public static void main(String[] args) {
        String code = Constants.sysBundle.getString("sequence.TIME_OUT");
        System.out.println(code);
    }
}
