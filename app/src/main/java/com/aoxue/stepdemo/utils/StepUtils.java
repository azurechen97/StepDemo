package com.aoxue.stepdemo.utils;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.TraceLocation;
import com.aoxue.stepdemo.bean.PathRecord;
import com.aoxue.stepdemo.bean.SportRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 运动轨迹工具
 */
public class StepUtils {


    /**
     * 将AMapLocation List 转为TraceLocation list
     *
     * @param list
     * @return
     */
    public static List<TraceLocation> parseTraceLocationList(
            List<AMapLocation> list) {
        List<TraceLocation> traceList = new ArrayList<>();
        if (list == null) {
            return traceList;
        }
        for (int i = 0, size = list.size(); i < size; i++) {
            TraceLocation location = new TraceLocation();
            AMapLocation amapLocation = list.get(i);
            location.setBearing(amapLocation.getBearing());
            location.setLatitude(amapLocation.getLatitude());
            location.setLongitude(amapLocation.getLongitude());
            location.setSpeed(amapLocation.getSpeed());
            location.setTime(amapLocation.getTime());
            traceList.add(location);
        }
        return traceList;
    }

    public static TraceLocation parseTraceLocation(AMapLocation amapLocation) {
        TraceLocation location = new TraceLocation();
        location.setBearing(amapLocation.getBearing());
        location.setLatitude(amapLocation.getLatitude());
        location.setLongitude(amapLocation.getLongitude());
        location.setSpeed(amapLocation.getSpeed());
        location.setTime(amapLocation.getTime());
        return location;
    }

    /**
     * 将AMapLocation List 转为LatLng list
     *
     * @param list
     * @return
     */
    public static List<LatLng> parseLatLngList(List<AMapLocation> list) {
        List<LatLng> traceList = new ArrayList<>();
        if (list == null) {
            return traceList;
        }
        for (int i = 0, size = list.size(); i < size; i++) {
            AMapLocation loc = list.get(i);
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();
            LatLng latlng = new LatLng(lat, lng);
            traceList.add(latlng);
        }
        return traceList;
    }

    public static AMapLocation parseLocation(String latLonStr) {
        if (latLonStr == null || latLonStr.equals("") || latLonStr.equals("[]")) {
            return null;
        }
        String[] loc = latLonStr.split(",");
        AMapLocation location = null;
        if (loc.length == 6) {
            location = new AMapLocation(loc[2]);
            location.setProvider(loc[2]);
            location.setLatitude(Double.parseDouble(loc[0]));
            location.setLongitude(Double.parseDouble(loc[1]));
            location.setTime(Long.parseLong(loc[3]));
            location.setSpeed(Float.parseFloat(loc[4]));
            location.setBearing(Float.parseFloat(loc[5]));
        } else if (loc.length == 2) {
            location = new AMapLocation("gps");
            location.setLatitude(Double.parseDouble(loc[0]));
            location.setLongitude(Double.parseDouble(loc[1]));
        }

        return location;
    }


    public static LatLng parseLatLngLocation(String latLonStr) {
        if (latLonStr == null || latLonStr.equals("") || latLonStr.equals("[]")) {
            return null;
        }
        String[] loc = latLonStr.split(",");
        LatLng location = null;
        if (loc.length == 2) {
            location = new LatLng(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));
        }
        return location;
    }

    /**
     * 将数据库里面保存的String 转为对应的LatLng ,以便显示在地图上
     *
     * @param latLonStr
     * @return
     */
    public static ArrayList<LatLng> parseLatLngLocations(String latLonStr) {
        ArrayList<LatLng> locations = new ArrayList<>();
        String[] latLonStrs = latLonStr.split(";");
        for (String latLonStr1 : latLonStrs) {
            LatLng location = parseLatLngLocation(latLonStr1);
            if (location != null) {
                locations.add(location);
            }
        }
        return locations;
    }

    /**
     * 把地图上的经纬度转为String , 以便于保存
     *
     * @return
     */
    public static String getLatLngPathLineString(List<LatLng> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuffer pathline = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            LatLng location = list.get(i);
            String locString = amapLocationToString(location);
            pathline.append(locString).append(";");
        }
        String pathLineString = pathline.toString();
        pathLineString = pathLineString.substring(0,
                pathLineString.length() - 1);
        return pathLineString;
    }

    public static ArrayList<AMapLocation> parseLocations(String latLonStr) {
        ArrayList<AMapLocation> locations = new ArrayList<>();
        String[] latLonStrs = latLonStr.split(";");
        for (String latLonStr1 : latLonStrs) {
            AMapLocation location = parseLocation(latLonStr1);
            if (location != null) {
                locations.add(location);
            }
        }
        return locations;
    }

    public static String amapLocationToString(LatLng location) {
        StringBuffer locString = new StringBuffer();
        locString.append(location.latitude).append(",");
        locString.append(location.longitude);
        return locString.toString();
    }

    public static String amapLocationToString(AMapLocation location) {
        StringBuffer locString = new StringBuffer();
        locString.append(location.getLatitude()).append(",");
        locString.append(location.getLongitude()).append(",");
        locString.append(location.getProvider()).append(",");
        locString.append(location.getTime()).append(",");
        locString.append(location.getSpeed()).append(",");
        locString.append(location.getBearing());
        return locString.toString();
    }


    public static String getPathLineString(List<AMapLocation> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuffer pathline = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            AMapLocation location = list.get(i);
            String locString = amapLocationToString(location);
            pathline.append(locString).append(";");
        }
        String pathLineString = pathline.toString();
        pathLineString = pathLineString.substring(0,
                pathLineString.length() - 1);
        return pathLineString;
    }

    /**
     * 描述: 计算卡路里
     * ---------计算公式：体重（kg）* 距离（km）* 运动系数（k）
     * ---------运动系数：健走：k=0.8214；跑步：k=1.036；自行车：k=0.6142；轮滑、溜冰：k=0.518室外滑雪：k=0.888
     * 作者: james
     * 日期: 2019/2/20 19:40
     * 类名: StepUtils
     *
     * @param weight   体重
     * @param distance 距离
     */
    public static double calculationCalorie(double weight, double distance) {
        return weight * distance * 1.036;
    }

    public static String formatSeconds(long seconds) {
        String hh = seconds / 3600 > 9 ? seconds / 3600 + "" : "0" + seconds
                / 3600;
        String mm = (seconds % 3600) / 60 > 9 ? (seconds % 3600) / 60 + ""
                : "0" + (seconds % 3600) / 60;
        String ss = (seconds % 3600) % 60 > 9 ? (seconds % 3600) % 60 + ""
                : "0" + (seconds % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }

    public static String formatMilliseconds(long milliseconds) {
        return formatSeconds(milliseconds / 1000);
    }

    public static SportRecord parseSportRecord(PathRecord pathRecord) {
        SportRecord sportRecord = new SportRecord();

        sportRecord.setId(pathRecord.getId());
        sportRecord.setDistance(pathRecord.getDistance());
        sportRecord.setDuration(pathRecord.getDuration());
        sportRecord.setPathLine(getLatLngPathLineString(pathRecord.getPathLine()));
        sportRecord.setStartPoint(amapLocationToString(pathRecord.getStartPoint()));
        sportRecord.setEndPoint(amapLocationToString(pathRecord.getEndPoint()));
        sportRecord.setStartTime(pathRecord.getStartTime());
        sportRecord.setEndTime(pathRecord.getEndTime());
        sportRecord.setCalorie(pathRecord.getCalorie());
        sportRecord.setSpeed(pathRecord.getSpeed());
        sportRecord.setDistribution(pathRecord.getDistribution());
        sportRecord.setDateTag(pathRecord.getDateTag());

        return sportRecord;
    }

    public static PathRecord parsePathRecord(SportRecord sportRecord) {
        PathRecord pathRecord = new PathRecord();

        pathRecord.setId(sportRecord.getId());
        pathRecord.setDistance(sportRecord.getDistance());
        pathRecord.setDuration(sportRecord.getDuration());
        pathRecord.setPathLine(parseLatLngLocations(sportRecord.getPathLine()));
        pathRecord.setStartPoint(parseLatLngLocation(sportRecord.getStartPoint()));
        pathRecord.setEndPoint(parseLatLngLocation(sportRecord.getEndPoint()));
        pathRecord.setStartTime(sportRecord.getStartTime());
        pathRecord.setEndTime(sportRecord.getEndTime());
        pathRecord.setCalorie(sportRecord.getCalorie());
        pathRecord.setSpeed(sportRecord.getSpeed());
        pathRecord.setDistribution(sportRecord.getDistribution());
        pathRecord.setDateTag(sportRecord.getDateTag());

        return pathRecord;
    }
}