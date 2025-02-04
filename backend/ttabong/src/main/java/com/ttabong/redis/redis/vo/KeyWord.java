package com.ttabong.redis.redis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class KeyWord implements Serializable {
    private String data1;
    private String data2;

    public static KeyWord of(String keyWord) {
        KeyWord key = new KeyWord();
        key.data1 = keyWord;
        key.data2 = keyWord;
        return key;
    }
}
