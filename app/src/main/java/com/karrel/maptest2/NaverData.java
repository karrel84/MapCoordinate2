package com.karrel.maptest2;

import java.util.List;

/**
 * 네이버 api요청시 가져오는 모델입니다.
 */

public class NaverData {
    public result result;
    public String errorMessage;
    public String errorCode;

    public class result {
        public int total;
        public String userquery;
        public List<items> items;

        public class items {
            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder();
                builder.append("address : ");
                builder.append(address);
                builder.append("\n");
                builder.append("point : ");
                builder.append(point.x);
                builder.append(" ");
                builder.append(point.y);
                builder.append("\n");
                return builder.toString();
            }

            public String address;
            public addrdetail addrdetail;

            public class addrdetail {
                public String country;
                public String sido;
                public String sigugun;
                public String dongmyun;
                public String rest;
            }

            public point point;

            public class point {
                public double x;
                public double y;
            }
        }
    }

    @Override
    public String toString() {
        if (result == null) {
            return String.format("errorMessage : %s, errorCode : %s", errorMessage, errorCode);
        } else {
            return result.items.get(0).toString();
        }
    }
}
