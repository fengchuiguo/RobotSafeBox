package com.robotsafebox.framework.sms.ihuyi;

import com.robotsafebox.framework.properties.PropertiesConfig;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;

/**
 * huiyi短信发送接口
 */
public class SmsSendUtils {

    private final static String IHUYI_SMS_SEND_URL = "ihuyismsSendUrl";

    private final static String IHUYI_SMS_ACCOUNT = "ihuyismsaccount";
    private final static String IHUYI_SMS_PASSWORD = "ihuyismspassword";

    public static String sendSms(String phoneNumber, String numCode, String customerContent) throws IOException, DocumentException {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(PropertiesConfig.getSmsConfigByKey(IHUYI_SMS_SEND_URL));
        client.getParams().setContentCharset("UTF-8");
        method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=UTF-8");
        String content;
        if (StringUtils.isNotBlank(customerContent)) {
            content = customerContent;
        } else {
            content = "您的验证码是：" + numCode + "。请不要把验证码泄露给其他人。";
        }
        NameValuePair[] data = {
                new NameValuePair("account",
                        PropertiesConfig.getSmsConfigByKey(IHUYI_SMS_ACCOUNT)),
                new NameValuePair("password",
                        PropertiesConfig.getSmsConfigByKey(IHUYI_SMS_PASSWORD)),
                new NameValuePair("mobile", phoneNumber),
                new NameValuePair("content", content),};
        method.setRequestBody(data);
        client.executeMethod(method);
        String SubmitResult = method.getResponseBodyAsString();
        Document doc = DocumentHelper.parseText(SubmitResult);
        Element root = doc.getRootElement();
        return root.elementText("code");
    }
}
