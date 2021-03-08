package ksb.strokesos.tester.common;

public class Constants {

    public static final String SYS_ENV_HOME_DIR = "strokeSOS_HOME";
    public static final String CONF_DIR = "conf";
    public static final String DATA_DIR = "data";
    public static final String REST_DIR = "rest";
    public static final String STATISTICS_DIR = "statistics";
    public static final String REST_APP_CONFIG_FILE = "collectorApp.conf";
    public static final String CONF_REST_PORT = "rest.port";
    public static final int REST_SERVER_DEFAULT_PORT = 9110;

    public static final String CONF_ES_HOST = "es.master.host";
    public static final String CONF_ES_TRANSPORT_PORT = "es.transport.port";
    public static final String CONF_ES_CLUSTER_NAME = "es.cluster.name";

    public static final String CONF_MARIA_HOST = "db.maria.host";
    public static final String CONF_MARIA_PORT = "db.maria.port";
    public static final String CONF_MARIA_USER = "db.maria.user";
    public static final String CONF_MARIA_PWD = "db.maria.password";
    public static final String CONF_MARIA_DATABASE = "db.maria.database";
    public static final String CONF_MARIA_QUERY = "db.maria.query";

    public static final String ES_INDEX_TYPE = "_doc";

    public static final String ERROR_RESPONSE = "error_msg";
    public static final int STAT_TITLE_VALUE_DEFAULT = 0;
    public static final int STAT_GRAPH_VALUE_DEFAULT = 0;
    public static final String STAT_TITLE_TEXT_DEFAULT = "없음";

    public static final String CONF_ACTIVE_MQ_QUEUE = "SENSOR";
    public static final String CONF_TESTER_BASE_TIME = "tester.base.time";
    public static final String CONF_ACTIVE_MQ_URL = "tester.activemq.url";
}
