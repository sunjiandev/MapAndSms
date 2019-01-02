package com.sunkaisens.gisandsms.event;

/**
 * @author:sun
 * @date:2019/1/2
 * @email:sunjianyun@sunkaisens.com
 * @Description: 服务器返回消息的固定格式
 */
public class ReceiveServerMsg {

    /**
     * result : {"groupNo":"0008","uri":["18865002205","18865002206"]}
     * type : 0
     */

    private String result;
    private int type;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ReceiveServerMsg{" +
                "result='" + result + '\'' +
                ", type=" + type +
                '}';
    }
}
