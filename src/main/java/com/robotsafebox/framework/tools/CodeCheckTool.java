package com.robotsafebox.framework.tools;

import org.apache.commons.lang3.StringUtils;

public class CodeCheckTool {

    public static final String SMS_CODE = "smsCode";
    public static final String CHECK_TYPE = "checkType";
    public static final String LINK_CODE = "_";
    public static final String[] NOT_REMOVE_CHECK_TYPE = {""};

    public static Boolean checkSmsCodeSuccess(String phone, String clientCode, String checkType, String serverCode) {
        if (StringUtils.isAnyBlank(phone, clientCode, checkType, serverCode)) {
            return false;
        }
        return (saveSmsCode(phone, clientCode, checkType)).equals(serverCode);
    }

    public static Boolean checkSmsCodeFailure(String phone, String clientCode, String checkType, String serverCode) {
        return !checkSmsCodeSuccess(phone, clientCode, checkType, serverCode);
    }

    public static String saveSmsCode(String phone, String clientCode, String checkType) {
        return new StringBuffer(phone).append(LINK_CODE).append(clientCode).append(LINK_CODE).append(checkType).toString();
    }

    public static Boolean needRemoveSmsCode(String checkType) {
        if (StringUtils.isNotBlank(checkType)) {
            for (String notType : NOT_REMOVE_CHECK_TYPE) {
                if (notType.equals(checkType)) {
                    return false;
                }
            }
        }
        return true;
    }


}
