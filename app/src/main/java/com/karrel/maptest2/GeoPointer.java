package com.karrel.maptest2;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * NAVER_CLIENT_ID 와 NAVER_CLIENT_SECRET는
 * 관련되 주소는 https://developers.naver.com/docs/map/overview/
 * 지도 > 네이버지도 API요청해서 얻으면 됩니다.
 */
public class GeoPointer extends AsyncTask<String, Void, GeoPointer.Point[]> {

    private final static String NAVER_CLIENT_ID = "k8zZEKNT4TUivYGnWaSW";
    private final static String NAVER_CLIENT_SECRET = "EOCVwIAyx4";

    private OnGeoPointListener onGeoPointListener;

    private Context context;

    public GeoPointer(Context context, OnGeoPointListener listener) {
        this.context = context;
        onGeoPointListener = listener;
    }

    public interface OnGeoPointListener {
        void onPoint(Point[] p);

        void onProgress(int progress, int max);
    }

    class Point {
        // 위도
        public double x;
        // 경도
        public double y;
        public String addr;
        // 포인트를 받았는지 여부
        public boolean havePoint;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("x : ");
            builder.append(x);
            builder.append(" y : ");
            builder.append(y);
            builder.append(" addr : ");
            builder.append(addr);

            return builder.toString();
        }
    }

    @Override
    protected Point[] doInBackground(String... params) {
        // 리턴할 포인터 객체를 파람의 수만큼 배열로 만든다.
        Point[] points = new Point[params.length];
        for (int i = 0; i < params.length; i++) {
            // 프로그래스를 돌린다.
            onGeoPointListener.onProgress(i + 1, params.length);

            final String addr = params[i];
            // 구글의 GeoCode로 부터 주소를 기준으로 데이터를 가져온다.
            Point point = getPointFromGeoCoder(addr);

            // 구글의 지오코더로부터 주소를 갖고오지 못했으면 네이버 api를 이용해서 가져온다.
            if (!point.havePoint) point = getPointFromNaver(addr);

            points[i] = point;
        }

        return points;
    }

    /**
     * 네이버 맵 api를 통해서 주소를 가져온다.
     * https://developers.naver.com/docs/map/overview/
     */
    private Point getPointFromNaver(String addr) {
        Point point = new Point();
        point.addr = addr;

        String json = null;
        String clientId = NAVER_CLIENT_ID;// 애플리케이션 클라이언트 아이디값";
        String clientSecret = NAVER_CLIENT_SECRET;// 애플리케이션 클라이언트 시크릿값";
        try {
            addr = URLEncoder.encode(addr, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/map/geocode?query=" + addr; // json
            // String apiURL =
            // "https://openapi.naver.com/v1/map/geocode.xml?query=" + addr; //
            // xml
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else { // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            json = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (json == null) {
            return point;
        }

        Log.d("TEST2", "json => " + json);

        Gson gson = new Gson();
        NaverData data = new NaverData();
        try {
            data = gson.fromJson(json, NaverData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (data.result != null) {
            point.x = data.result.items.get(0).point.x;
            point.y = data.result.items.get(0).point.y;
            point.havePoint = true;
        }

        return point;
    }

    @Override
    protected void onPostExecute(Point[] point) {
        onGeoPointListener.onPoint(point);
    }

    /**
     * 지오코더(구글꺼)에서 좌표를 가져온다.
     */
    private Point getPointFromGeoCoder(String addr) {
        Point point = new Point();
        point.addr = addr;

        Geocoder geocoder = new Geocoder(context);
        List<Address> listAddress;
        try {
            listAddress = geocoder.getFromLocationName(addr, 1);
        } catch (IOException e) {
            e.printStackTrace();
            point.havePoint = false;
            return point;
        }

        if (listAddress.isEmpty()) {
            point.havePoint = false;
            return point;
        }

        point.havePoint = true;
        point.x = listAddress.get(0).getLongitude();
        point.y = listAddress.get(0).getLatitude();
        return point;
    }
}
