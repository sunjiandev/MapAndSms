package com.sunkaisens.gisandsms.event;

/**
 * @author:sun
 * @date:2018/12/24
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class MessageEvent {
    private String number;
    private String content;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "number='" + number + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
