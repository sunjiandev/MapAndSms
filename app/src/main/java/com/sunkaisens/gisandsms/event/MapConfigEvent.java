package com.sunkaisens.gisandsms.event;

/**
 * @author:sun
 * @date:2018/12/26
 * @email:sunjianyun@sunkaisens.com
 * @Description: 地图配置信息
 */
public class MapConfigEvent {
    /**
     * 配置的类型
     */
    private int type;
    /**
     * 数据的位置
     */
    private int position;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public MapConfigEvent(int type, int position) {
        this.type = type;
        this.position = position;
    }

    public MapConfigEvent() {
    }

    @Override
    public String toString() {
        return "MapConfigEvent{" +
                "type=" + type +
                ", position=" + position +
                '}';
    }
}
