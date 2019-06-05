package ensharp.yeey.whisperer.Common;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import ensharp.yeey.whisperer.Common.VO.BusStopVO;
import ensharp.yeey.whisperer.Common.VO.BusVO;
import ensharp.yeey.whisperer.Common.VO.CloserStationVO;
import ensharp.yeey.whisperer.Common.VO.DefaultInfoVO;
import ensharp.yeey.whisperer.Common.VO.ExchangeInfoVO;
import ensharp.yeey.whisperer.Common.VO.ExitInfoVO;
import ensharp.yeey.whisperer.Common.VO.PathVO;
import ensharp.yeey.whisperer.Common.VO.StationVO;
import ensharp.yeey.whisperer.Common.VO.SubwayStationInfoVO;
import ensharp.yeey.whisperer.Common.VO.SubwayTimeTableVO;
import ensharp.yeey.whisperer.Common.VO.TimeVO;
import ensharp.yeey.whisperer.Common.VO.UseInfoVO;

/**
 * JsonElement를 파싱하는 클래스입니다.
 * 싱글톤으로 생성됩니다.
 */
public class ParseManager {
    private static final ParseManager ourInstance = new ParseManager();

    public static ParseManager getInstance() {
        return ourInstance;
    }

    private Gson gson = new Gson();

    /**
     * 지하철 경로를 파싱하는 메소드입니다.
     * @param jsonObject API에서 반환된 JSONObject
     * @return 파싱된 PathVO 객체
     */
    public PathVO parsePath(JSONObject jsonObject) {
        PathVO pathvo = new Parser<>(PathVO.class, "result").parse(gson.fromJson(jsonObject.toString(), JsonElement.class));
        if(pathvo.getExChangeInfoSet() != null){
            pathvo.setExchangeInfoList(parseExchangeInfo(pathvo.getExChangeInfoSet()));
        }
        return pathvo;
    }

    public CloserStationVO parseCloserStation(JSONObject jsonObject) {
        CloserStationVO closerStationVO = new Parser<>(CloserStationVO.class, "result").parse(gson.fromJson(jsonObject.toString(), JsonElement.class));
        if(closerStationVO != null)
            closerStationVO.setCloserStationList(parseStationInfo(closerStationVO.getStation()));

        return closerStationVO;
    }

    public List<StationVO> parseStationInfo(JsonElement jsonElement) {
        Log.e("Station",jsonElement.toString());
        return new Parser<>(StationVO.class, "station").NotKeyParseList(jsonElement, new TypeToken<List<StationVO>>() {}.getType());
    }

    /**
     * 지하철 세부정보를 파싱하는 메소드입니다.
     * @param jsonObject API에서 반환된 JSONObject
     * @return 파싱된 SubwayStationVO 객체
     */
    public SubwayStationInfoVO parseStation(JSONObject jsonObject) {
        SubwayStationInfoVO station = new Parser<>(SubwayStationInfoVO.class, "result").parse(gson.fromJson(jsonObject.toString(), JsonElement.class));

        // 환승역 리스트를 저장합니다.
        if (station.getExOBJ() != null)
            station.setExOBJList(parseExOBJ(station.getExOBJ()));

        // 기본적인 역 정보를 저장합니다.
        if (station.getDefaultInfo() != null)
            station.setStationDefaultInfo(parseDefaultInfo(station.getDefaultInfo()));

        // 이용 정보를 저장합니다.
        if (station.getUseInfo() != null)
            station.setStationUseInfo(parseUseInfo(station.getUseInfo()));

        // 출구 정보를 저장합니다.
        if (station.getExitInfo() != null) {
            station.setStationExitInfoList(parseExitInfo(station.getExitInfo()));
        }

        return station;
    }

    /**
     * 지하철 환승 정보를 파싱하는 메소드입니다.
     * @param jsonElement 환승 정보를 담고있는 jsonElement
     * @return 파싱된 ExchangeInfoVO List
     */
    public List<ExchangeInfoVO> parseExchangeInfo(JsonElement jsonElement) {
        return new Parser<>(ExchangeInfoVO.class, "exChangeInfo").parseList(jsonElement, new TypeToken<List<ExchangeInfoVO>>() {}.getType());
    }

    /**
     * 지하철 시간표를 파싱하는 메소드입니다.
     * @param jsonObject 지하철 시간표를 담고있는 jsonObject
     * @return 파싱된 지하철 시간표 객체
     */
    public SubwayTimeTableVO parseTimeTable(JSONObject jsonObject, String wayCode) {
        SubwayTimeTableVO timeTable = new Parser<>(SubwayTimeTableVO.class, "result").parse(gson.fromJson(jsonObject.toString(), JsonElement.class));
        if (timeTable.getOrdList() != null)
            timeTable.setOrdTimeList(parseTime(timeTable.getOrdList(), wayCode));
        if (timeTable.getSatList() != null)
            timeTable.setSatTimeList(parseTime(timeTable.getSatList(), wayCode));
        if (timeTable.getSunList() != null)
            timeTable.setSunTimeList(parseTime(timeTable.getSunList(), wayCode));

        return timeTable;
    }

    /**
     * 지하철 환승역 리스트를 파싱하는 메소드입니다.
     * @param jsonElement 환승역 정보를 담고있는 jsonElement
     * @return 파싱된 SubwayStationIfoVO List
     */
    public List<SubwayStationInfoVO> parseExOBJ(JsonElement jsonElement) {
        return new Parser<>(SubwayStationInfoVO.class, "station").parseList(jsonElement, new TypeToken<List<SubwayStationInfoVO>>() {}.getType());
    }

    /**
     * 지하철 기본 역 정보를 파싱하는 메소드입니다.
     * @param jsonElement 지하철역 기본 정보를 담고있는 jsonObject
     * @return 파싱된 DefaultInfoVO 객체
     */
    public DefaultInfoVO parseDefaultInfo(JsonElement jsonElement) {
        return new Parser<>(DefaultInfoVO.class, "defaultInfo").parse(jsonElement);
    }

    /**
     * 지하철 이용 정보를 파싱하는 메소드입니다.
     * @param jsonElement 지하철 이용 정보를 담고있는 jsonElement
     * @return 파싱된 UseInfoVO 객체
     */
    public UseInfoVO parseUseInfo(JsonElement jsonElement) {
        return new Parser<>(UseInfoVO.class, "useInfo").parse(jsonElement);
    }

    /**
     * 지하철 출구 정보를 파싱하는 메소드입니다.
     * @param jsonElement 지하철 출구 정보를 담고있는 jsonElement
     * @return 파싱된 ExitInfoVO 객체
     */
    public List<ExitInfoVO> parseExitInfo(JsonElement jsonElement) {
        Type listType = new TypeToken<List<ExitInfoVO>>() {}.getType();
        Type stringListType = new TypeToken<List<String>>() {}.getType();
        Type busStopListType = new TypeToken<List<BusStopVO>>() {}.getType();
        Type busListType = new TypeToken<List<BusVO>>() {}.getType();

        List<ExitInfoVO> exitInfoList = new Parser<>(ExitInfoVO.class, "gate").parseList(jsonElement, listType);

        // Bus Stop
        for (int i = 0; i < exitInfoList.size(); i++) {
            // gateLink를 List로 파싱
            if (exitInfoList.get(i).getGateLink() != null) {
                List<String> gateLinkList = gson.fromJson(exitInfoList.get(i).getGateLink(), stringListType);
                exitInfoList.get(i).setGateLinkList(gateLinkList);
            }

            // Bus Stop을 List로 파싱
            if (exitInfoList.get(i).getBUSSTOP() != null) {
                List<BusStopVO> busStopList = gson.fromJson(exitInfoList.get(i).getBUSSTOP(), busStopListType);

                // Bus 파싱
                for (int j = 0; j < busStopList.size(); j++)
                    if (busStopList.get(j).getBus() != null)
                        busStopList.get(j).setBusList((List<BusVO>) gson.fromJson(busStopList.get(j).getBus(), busListType));

                exitInfoList.get(i).setBusStopList(busStopList);
            }
        }

        return exitInfoList;
    }

    /**
     * 지하철 시간표를 파싱해 TImeVO 리스트로 저장하는 메소드입니다.
     * @param jsonElement 지하철 시간 정보     *
     * @return 파싱된 지하철 시간표
     */
    public List<TimeVO> parseTime(JsonElement jsonElement, String wayCode) {
        JsonElement tempJsonElement = jsonElement.getAsJsonObject().get(calculateKey(wayCode)).getAsJsonObject().get("time");
        return new Parser<>(TimeVO.class, "time").NotKeyParseList(tempJsonElement, new TypeToken<List<TimeVO>>() {}.getType());
    }

    public String calculateKey(String wayCode) {
        if (wayCode == "1") return "up";
        else return "down";
    }

    private class Parser<T> {
        private Class<T> usingClass;
        private String key;

        public Parser(Class<T> targetClass, String key) {
            usingClass = targetClass;
            this.key = key;
        }

        public T parse(JsonElement jsonElement) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(usingClass, new RestDeserializer<>(usingClass, key))
                    .create();

            return (T) gson.fromJson(jsonElement.toString(), usingClass);
        }

        public List<T> parseList(JsonElement jsonElement, Type listType) {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonElement rootElement = parser.parse(jsonElement.toString()).getAsJsonObject().get(key);

            return gson.fromJson(rootElement, listType);
        }

        public List<T> NotKeyParseList(JsonElement jsonElement, Type listType) {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonElement rootElement = parser.parse(jsonElement.toString()).getAsJsonArray();

            return gson.fromJson(rootElement, listType);
        }
    }

    /**
     * JSON Object를 Deserialzer해줍니다.
     * 모든 VO class를 사용하기 위해 제네릭 형식을 사용하고, 키값을 받아오도록 합니다.
     * @param <T> 파싱할 VO class
     */
    private class RestDeserializer<T> implements JsonDeserializer<T> {
        private Class<T> usingClass;
        private String key;

        public RestDeserializer(Class<T> targetClass, String key) {
            usingClass = targetClass;
            this.key = key;
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonElement content = json.getAsJsonObject().get(key);
            return new Gson().fromJson(content, usingClass);
        }
    }
}
