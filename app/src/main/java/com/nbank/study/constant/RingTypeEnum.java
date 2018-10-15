package com.nbank.study.constant;

import android.content.Context;

/**
 * 该枚举列举了几种铃声设置
 * {@link ico.ico.util.Common#setRing(Context, String, RingTypeEnum)}
 */
public enum RingTypeEnum {
    ALL(0)          //所有铃声
    , RINGTONE(1)   //手机铃声
    , ALARM(2)      //手机闹铃
    , NOTIFY(3);    //通知铃声

    int index;

    RingTypeEnum(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
