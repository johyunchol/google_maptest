package kr.co.kkensu.maptest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Response implements Serializable {

    private CumulativeDistance cumulativeDistance;
    private RealTimeVehicleStatus realTimeVehicleStatus;
    private ParkLocation parkLocation;

    public CumulativeDistance getCumulativeDistance() {
        return cumulativeDistance;
    }

    public void setCumulativeDistance(CumulativeDistance cumulativeDistance) {
        this.cumulativeDistance = cumulativeDistance;
    }

    public RealTimeVehicleStatus getRealTimeVehicleStatus() {
        return realTimeVehicleStatus;
    }

    public void setRealTimeVehicleStatus(RealTimeVehicleStatus realTimeVehicleStatus) {
        this.realTimeVehicleStatus = realTimeVehicleStatus;
    }

    public ParkLocation getParkLocation() {
        return parkLocation;
    }

    public void setParkLocation(ParkLocation parkLocation) {
        this.parkLocation = parkLocation;
    }

    @Override
    public String toString() {
        return "Response{" +
                "realTimeVehicleStatus=" + realTimeVehicleStatus +
                "parkLocation=" + parkLocation +
                '}';
    }

    public static class ParkLocation implements Serializable {
        double lat;
        double lon;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        @Override
        public String toString() {
            return "ParkLocation{" +
                    "lat=" + lat +
                    ", lon=" + lon +
                    '}';
        }
    }

    public static class CumulativeDistance implements Serializable {
        private List<Odometer> odometers = new ArrayList<>();
        private String msgId;

        public List<Odometer> getOdometers() {
            return odometers;
        }

        public void setOdometers(List<Odometer> odometers) {
            this.odometers = odometers;
        }

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        @Override
        public String toString() {
            return "CumulativeDistance{" +
                    "odometers=" + odometers +
                    ", msgId='" + msgId + '\'' +
                    '}';
        }

        public static class Odometer {
            String timestamp;
            double value;
            int unit;

            public String getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
            }

            public double getValue() {
                return value;
            }

            public void setValue(double value) {
                this.value = value;
            }

            public int getUnit() {
                return unit;
            }

            public void setUnit(int unit) {
                this.unit = unit;
            }

            @Override
            public String toString() {
                return "Odometers{" +
                        "timestamp='" + timestamp + '\'' +
                        ", value=" + value +
                        ", unit=" + unit +
                        '}';
            }
        }
    }

    public static class RealTimeVehicleStatus implements Serializable {
        private ResMsg resMsg;
        private String msgId;

        public ResMsg getResMsg() {
            return resMsg;
        }

        public void setResMsg(ResMsg resMsg) {
            this.resMsg = resMsg;
        }

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public static class ResMsg {
            double lat;
            double lon;
            int type;

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLon() {
                return lon;
            }

            public void setLon(double lon) {
                this.lon = lon;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }
        }

        @Override
        public String toString() {
            return "RealTimeVehicleStatus{" +
                    "resMsg=" + resMsg +
                    ", msgId='" + msgId + '\'' +
                    '}';
        }
    }


//    "driveAbleDistance":{
//        "errCode":"4045",
//                "errMsg":"No Data",
//                "errId":"6a402656-3734-414f-aff8-969e8ec5dca3"
//    },
//            "cumulativeDistance":{
//        "odometers":[
//        {
//            "timestamp":"20200810170150",
//                "value":43532.4,
//                "unit":1
//        }
//],
//        "msgId":"fb8daf4e-b237-4097-adfb-694c936a76f0"
//    },
//            "parkLocation":{
//        "timestamp":"20200810170150",
//                "lat":37.50763888,
//                "lon":127.0452111,
//                "type":"0",
//                "alt":null,
//                "msgId":"a5b3167f-da7f-4847-9181-21e251f69a8b"
//    },

}
