package com.ckt.francis.navigationmap.util;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by wuchangzhi on 15-8-20.
 */
public class Utils {
    public static double pi = 3.1415926535897932384626;

    /*
           将百度坐标转化成火星坐标
     */
    public static LatLng bd09_To_Gcj02(LatLng ll) {
        double x = ll.longitude - 0.0065, y = ll.latitude - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);

        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new LatLng(gg_lat,gg_lon);
    }


}
