package ksb.strokesos.tester.io;

import com.google.gson.Gson;
import ksb.strokesos.tester.bean.*;
import ksb.strokesos.tester.common.Constants;
import ksb.strokesos.tester.common.IConfigurable;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class EsHighConnector {
    private RestHighLevelClient client;
    private final static Logger log = LoggerFactory.getLogger(EsHighConnector.class);

    private static EsHighConnector INSTANCE = null;

    private Map<String, String> sensorMsgMapJson = new HashMap<>();
    private Map<String, SensorMsg> sensorMsgMap = new HashMap<>();
    private Map<String, SensorMsgTmp> sensorMsgMapTmp = new HashMap<>();

    private List<String> ecgField = new ArrayList<>(
            Arrays.asList(
                    "timestamp",
                    "ecg_msb", "ecg_mid", "ecg_lsb"
            )
    );

    private List<String> accField = new ArrayList<>(
            Arrays.asList(
                    "timestamp",
                    "x_msb", "x_lsb",
                    "y_msb", "y_lsb",
                    "z_msb", "x_lsb"
            )
    );

    private List<String> pedField = new ArrayList<>(
            Arrays.asList(
                    "timestamp",
                    "l1", "l2", "l3", "l4",
                    "l5", "l6", "l7", "l8",
                    "r1", "r2", "r3", "r4",
                    "r5", "r6", "r7", "r8",
                    "acc_l_x", "acc_l_y", "acc_l_z",
                    "gyro_l_x", "gyro_l_y", "gyro_l_z",
                    "acc_r_x", "acc_r_y", "acc_r_z",
                    "gyro_r_x", "gyro_r_y", "gyro_r_z"
            )
    );

    /**
     * Constructor, to initialize the connection to ElasticeSearch
     *
     * @param config
     * @throws UnknownHostException
     */
    @SuppressWarnings("resource")
    private EsHighConnector(IConfigurable config) throws UnknownHostException {
        String clusterName = config.getSetting(Constants.CONF_ES_CLUSTER_NAME);
        String host = config.getSetting(Constants.CONF_ES_HOST);
        String strPort = config.getSetting(Constants.CONF_ES_TRANSPORT_PORT);
        int port = 0;
        if (strPort != null) {
            port = Integer.parseInt(strPort);
        } else {
            port = 9200;
        }

        client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, "http")));
    }

    /**
     * to get current INSTANCE<br>
     * Singleton pattern (lazy initialization mechanism technique within thread
     * safe)
     *
     * @param config
     * @return
     * @throws UnknownHostException
     */
    public synchronized static EsHighConnector getInstance(IConfigurable config) throws UnknownHostException {
        if (INSTANCE == null) {
            INSTANCE = new EsHighConnector(config);
            return INSTANCE;
        } else {
            return INSTANCE;
        }
    }

    private SensorMsg getSensorMsg(String device_id) {
        if(!sensorMsgMap.containsKey(device_id)) sensorMsgMap.put(device_id, new SensorMsg());
        return sensorMsgMap.get(device_id);
    }

    private SensorMsgTmp getSensorMsgTmp(String device_id) {
        if(!sensorMsgMapTmp.containsKey(device_id)) sensorMsgMapTmp.put(device_id, new SensorMsgTmp());
        return sensorMsgMapTmp.get(device_id);
    }

    private SensorDataTmp getSensorDataTmp(SensorMsgTmp sensorMsgTmp, String sensor_type) {
        Map<String, SensorDataTmp> map = sensorMsgTmp.getData();
        if(!map.containsKey(sensor_type)) map.put(sensor_type, new SensorDataTmp());
        return map.get(sensor_type);
    }

    public Map<String, String> getSensorMsgMap(String time) {
        SearchHit[] searchHits;
        searchHits = getDataFromES(SensorType.ACCELEROMETER, time);
        if(searchHits != null) updateDataTmp(searchHits, SensorType.ACCELEROMETER);

        searchHits = getDataFromES(SensorType.ECG, time);
        if(searchHits != null) updateDataTmp(searchHits, SensorType.ECG);

        searchHits = getDataFromES(SensorType.PEDOMETER, time);
        if(searchHits != null) updateDataTmp(searchHits, SensorType.PEDOMETER);

        SensorMsg sensorMsg;
        SensorMsgTmp sensorMsgTmp;
        for(String s : sensorMsgMapTmp.keySet()) {
            sensorMsg = getSensorMsg(s);
            sensorMsgTmp = sensorMsgMapTmp.get(s);
            sensorMsg.setDevice_id(sensorMsgTmp.getDevice_id());
            sensorMsg.setSend_time(sensorMsgTmp.getSend_time());
            sensorMsg.setData(new ArrayList<>(sensorMsgTmp.getData().values()));
            sensorMsgMapJson.put(s, new Gson().toJson(sensorMsg));
        }

        return sensorMsgMapJson;
    }

    public void clearSensorMsg() {
        sensorMsgMapJson.clear();
        sensorMsgMap.clear();
        sensorMsgMapTmp.clear();
    }

    private void updateDataTmp(SearchHit[] searchHits, SensorType type) {
        String device_id, sensor_id, sensor_type;
        SensorMsgTmp msgTmp;
        SensorDataTmp dataTmp;

        List<String> field;
        switch (type) {
            case ECG: field = ecgField; break;
            case ACCELEROMETER: field = accField; break;
            case PEDOMETER: field = pedField; break;
            default:
                log.error("unknown sensor type : " + type);
                return;
        }

        String[] data = new String[field.size()];

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date now = new Date();

        for(SearchHit hit : searchHits) {
            Map<String, Object> map = hit.getSourceAsMap();
            device_id = map.get("device_id").toString();
            sensor_id = map.get("SENSORID").toString();
            sensor_type = map.get("SENSORTYPE").toString();

            msgTmp = getSensorMsgTmp(device_id);
            msgTmp.setDevice_id(device_id);
            msgTmp.setSend_time(format.format(now));

            dataTmp = getSensorDataTmp(msgTmp, sensor_type);
            dataTmp.setSENSORID(sensor_id);
            dataTmp.setSENSORTYPE(sensor_type);
            dataTmp.setFIELD(String.join(",", field));

            String errorMsg = "";
            try {
                for(int i = 0; i < data.length; i++) {
                    errorMsg = "i: " + i +", field: "+ field.get(i) +", data: "+ map.get(field.get(i)).toString();
                    //log.info("i: {}, field: {}, data: {}", i, field.get(i), map.get(field.get(i).toString()));
                    data[i] = map.get(field.get(i)).toString();
                }
            } catch (Exception e) {
                log.error(errorMsg);
                log.error("");
                log.error(e.getMessage());
            }
            dataTmp.getDATA().add(String.join(",", data));
        }
    }

    private SearchHit[] getDataFromES(SensorType type, String time) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termQuery("send_time", time));
        builder.from(0);
        builder.size(9000);
        builder.timeout(new TimeValue(30, TimeUnit.SECONDS));

        SearchRequest request = new SearchRequest(type.getName());
        request.source(builder);

        SearchResponse response;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

        RestStatus status = response.status();
        TimeValue took = response.getTook();

        SearchHits hits = response.getHits();
        long totalHits = hits.getTotalHits();

        if(totalHits == 0) return null;
        log.info("index : " + type.getName() + ", time : " + time);
        log.info("HTTP status({}) took({})", status, took);
        log.info("{} docs found.", totalHits);
        log.info("");

        return hits.getHits();
    }
}
