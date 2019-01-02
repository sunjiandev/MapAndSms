package com.sunkaisens.gisandsms.event;

import java.util.List;

/**
 * @author:sun
 * @date:2019/1/2
 * @email:sunjianyun@sunkaisens.com
 * @Description: 群组信息, 组号包括组成员
 */
public class GroupInfo {

    /**
     * groupNo : 0008
     * uri : ["18865002205","18865002206"]
     */

    private String groupNo;
    private List<String> uri;

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public List<String> getUri() {
        return uri;
    }

    public void setUri(List<String> uri) {
        this.uri = uri;
    }
}
