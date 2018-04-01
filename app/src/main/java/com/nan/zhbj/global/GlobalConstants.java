package com.nan.zhbj.global;

/**
 * Created by nan on 2018/3/5.
 */

public class GlobalConstants {
    //10.0.2.2是系统预留给模拟器访问本机的ip,模拟器访问本机服务器使用该ip即可
    //public static final String SERVER_URL = "http://10.0.2.2:8080/zhbj";
    //该ip是电脑局域网ip，模拟器当然也可以通过该ip使用
    public static final String SERVER_URL = "http://192.168.1.103:8080/zhbj";
    public static final String CATEGORY_URL = SERVER_URL + "/categories.json";
    public static final String NEW_IP = "192.168.1.103";
    public static final String OLD_IP = "10.0.2.2";
    //组图URL
    public static final String PHOTOS_URL = SERVER_URL + "/photos/photos_1.json";
}
