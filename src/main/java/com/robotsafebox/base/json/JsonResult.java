package com.robotsafebox.base.json;

public class JsonResult {

    public Integer state = JsonResultState.ERROR.ordinal();

    public String message;

    public Object data;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setStateSuccess() {
        setState(JsonResultState.SUCCESS.ordinal());
    }

    public void setStateError() {
        setState(JsonResultState.ERROR.ordinal());
    }

    public void setStateUnlogin() {
        setState(-1);
    }

    public void setStateNoPermission() {
        setState(-2);
    }

}
