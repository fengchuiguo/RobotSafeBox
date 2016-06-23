package com.robotsafebox.framework.properties;

public class Constant {

    public static final String UTF_8 = "UTF-8";

    public static final String ISO_8859_1 = "ISO-8859-1";

    public static final String CONTENT_TYPE_HTML = "text/html;charset=" + UTF_8;

    public static final String CONTENT_TYPE_JSON = "application/json; charset=" + UTF_8;

    public static final String CONTENT_TYPE_PDF = "application/pdf; charset=" + UTF_8;

    public static final String EXCEPTION_MESSAGE = "Server busy, please try again later ";

    public static final String NO_PERMISSION_MESSAGE = "No permission to access ";

//  API
    public static final String API_CHECK = "apicheck";
    public static final String API_CHECK_TOKEN = "8Hmj5FDP1X32v0GqsU_SR7c2";


    /**
     * 用户上传文件可访问目录
     */
    public static final String[] ALLOW_UPLOAD_FOLDER = new String[]{"/memberHead", "/report", "uploadFile", "/userPhoto"};

    /**
     * 用户初始化密码
     */
    public static final String INIT_PASSWORD = "123456";

    /**
     * 用户状态 冻结
     */
    public static final String USER_STATE_NO = "1";

    /**
     * 用户状态 正常
     */
    public static final String USER_STATE_OK = "0";

}
