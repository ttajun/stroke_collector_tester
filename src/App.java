package ksb.strokesos.tester;

import ksb.strokesos.tester.common.Constants;
import ksb.strokesos.tester.common.FileConfigLoader;
import ksb.strokesos.tester.io.ActiveMQClient;
import ksb.strokesos.tester.io.EsHighConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    private final static Logger log = LoggerFactory.getLogger(App.class);
    public static FileConfigLoader collectorAppConfig = null;

    public static void main( String[] args )
    {
        EsHighConnector esHighConnector;
        ActiveMQClient activeMQClient;
        log.info("Stroke SOS collector tester.");

        reloadAppConfig();
        log.info("rest api server port : " + collectorAppConfig.getSetting(Constants.CONF_REST_PORT));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date now, process;
        String baseTime = collectorAppConfig.getSetting(Constants.CONF_TESTER_BASE_TIME);
        String processTime = collectorAppConfig.getSetting(Constants.CONF_TESTER_BASE_TIME);
        if(processTime == null || processTime.equals("")) processTime = "2019-01-01T00:00:00";

        try {
            esHighConnector = EsHighConnector.getInstance(collectorAppConfig);
            activeMQClient = ActiveMQClient.getInstance(collectorAppConfig);
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }

        Map<String, String> map;
        while (true) {
            log.info("process time : " + processTime);
            try {
                now = format.parse(processTime);
                process = new Date(now.getTime() + 1000);
                processTime = format.format(process);
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }

            map = esHighConnector.getSensorMsgMap(processTime);
            for(String s : map.keySet()) {
                //log.info("s: {}, msg: {}", s, map.get(s));
                activeMQClient.sendJMS(map.get(s));
            }
            esHighConnector.clearSensorMsg();

            /*
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
             */
        }
    }

    public static void reloadAppConfig() {
        try {
            String collectorAppConfigFile = FileConfigLoader.getPathInWorkingFolder(Constants.CONF_DIR,
                    Constants.REST_APP_CONFIG_FILE);
            collectorAppConfig = new FileConfigLoader(collectorAppConfigFile);
        } catch (IOException e) {
            log.error("Configuration file was not found: " + e.getMessage());
        }
    }
}
