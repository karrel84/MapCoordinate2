package com.karrel.maptest2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViewById(R.id.run).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run();
            }
        });
    }

    private void run() {
        getPoint(
                "긴고랑로 1길",
                "군자역"
        );
    }

    private void getPoint(String... addr) {
        GeoPointer geoPointer = new GeoPointer(Main2Activity.this, listener);
        geoPointer.execute(addr);
    }

    private GeoPointer.OnGeoPointListener listener = new GeoPointer.OnGeoPointListener() {
        @Override
        public void onPoint(GeoPointer.Point[] p) {
            int sCnt = 0, fCnt = 0;
            for (GeoPointer.Point point : p) {
                if (point.havePoint) sCnt++;
                else fCnt++;
                Log.d("TEST_CODE", point.toString());
            }
            Log.d("TEST_CODE", String.format("성공 : %s, 실패 : %s", sCnt, fCnt));
        }

        @Override
        public void onProgress(int progress, int max) {
            Log.d("TEST_CODE", String.format("좌표를 얻어오는중 %s / %s", progress, max));
        }
    };
}
