package io.mycat.web.config;

import io.mycat.config.loader.ConfigLoader;
import io.mycat.config.model.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by jiang on 2016/11/24 0024.
 * 这个文件用来读取配置文件，目前是从文件里面读取。
 */
public class MyConfigLoader implements ConfigLoader {
    public static final String SCHEMAKEY = "schemaConfigMap";
    public static final String DATANODEKEY = "dataNodeConfigMap";
    public static final String DATAHOSTKEY = "dataHostConfigMap";
    public static final String SYSTEMKEY = "systemConfig";
    public static final String USERKEY = "userConfigMap";
    public static final String FIREWALLKEY = "firewallConfig";
    public static final String CLUSTERKEY = "clusterConfig";
    private Map<String, SchemaConfig> schemaConfigMap;
    private Map<String, DataNodeConfig> dataNodeConfigMap;
    private Map<String, DataHostConfig> dataHostConfigMap;
    private SystemConfig systemConfig;
    private Map<String, UserConfig> userConfigMap;
    private FirewallConfig firewallConfig;
    private ClusterConfig clusterConfig;
    static final String DEFALUT_FILENAME = "map.mapdb";
    private static MyConfigLoader install = new MyConfigLoader();
    private MyDiscMap3 map3;
    public static MyConfigLoader getInstance() {
        return install;
    }
    /***把配置保存到文件*/
    public void save() {
        map3.put(SYSTEMKEY, getSystemConfig());
        map3.put(SCHEMAKEY, getSchemaConfigs());
        map3.put(DATANODEKEY, getDataNodes());
        map3.put(DATAHOSTKEY, getDataHosts());
        map3.put(USERKEY, getUserConfigs());
        map3.put(FIREWALLKEY, getFirewallConfig());
        map3.put(CLUSTERKEY, getClusterConfig());
    }
    /***把配置从文件读出或者重新加载*/
  synchronized   public void load() {
        systemConfig = (SystemConfig) map3.get(SYSTEMKEY);
        schemaConfigMap = (Map<String, SchemaConfig>) map3.get(SCHEMAKEY);
        dataNodeConfigMap = (Map<String, DataNodeConfig>) map3.get(DATANODEKEY);
        dataHostConfigMap = (Map<String, DataHostConfig>) map3.get(DATAHOSTKEY);
        userConfigMap = (Map<String, UserConfig>) map3.get(USERKEY);
        firewallConfig = (FirewallConfig) map3.get(FIREWALLKEY);
        clusterConfig = (ClusterConfig) map3.get(CLUSTERKEY);
    }
    private MyConfigLoader() {
        map3 = new MyDiscMap3(DEFALUT_FILENAME);
        load();
    }
    @Override
    public SchemaConfig getSchemaConfig(String schema) {
        if (schemaConfigMap != null) {
            return schemaConfigMap.get(schema);
        }
        TableConfig tableConfig = new TableConfig("PERSON", "id", false, false,
                TableConfig.TYPE_GLOBAL_DEFAULT, "dn1", null, null, false, null, false, null, null, null);
        Map<String, TableConfig> map = new HashMap<>();
        map.put("PERSON", tableConfig);
        SchemaConfig schemaConfig = new SchemaConfig("db", null, map, 100, false);
//        schemaConfig = null;
        return schemaConfig;
    }

    @Override
    public Map<String, SchemaConfig> getSchemaConfigs() {
        if (schemaConfigMap != null) {
            return schemaConfigMap;
        }
        Map<String, SchemaConfig> map = new HashMap<>();
        map.put("db", getSchemaConfig("db"));
        return map;
    }

    @Override
    public Map<String, DataNodeConfig> getDataNodes() {
        if (dataNodeConfigMap != null) {
            return dataNodeConfigMap;
        }
        Map<String, DataNodeConfig> map = new HashMap<>();
        DataNodeConfig nodeConfig = new DataNodeConfig("dn1", "changhong", "localhost1");
        map.put("dn1", nodeConfig);
        return map;
    }

    @Override
    public Map<String, DataHostConfig> getDataHosts() {
        if (dataHostConfigMap != null) {
            return dataHostConfigMap;
        }
        Map<String, DataHostConfig> map = new HashMap<>();
        DBHostConfig dbHostConfig = new DBHostConfig("hostS1", "localhost", 3306, "localhost:3306", "root", "0000", "0000");
        dbHostConfig.setMinCon(10);
        dbHostConfig.setMaxCon(1000);
        DBHostConfig[] dataHostConfigs = new DBHostConfig[]{dbHostConfig};
        DataHostConfig hostConfig = new DataHostConfig("localhost1", "mysql", "native", dataHostConfigs, new HashMap<Integer,DBHostConfig[]>(), 0, 100, false);
        hostConfig.setMinCon(10);
        hostConfig.setMaxCon(1000);
        hostConfig.setHearbeatSQL("select user()");
        map.put("localhost1", hostConfig);
        return map;
    }

    @Override
    public SystemConfig getSystemConfig() {
        if (systemConfig != null) {
            return systemConfig;
        }
        SystemConfig systemConfig = new SystemConfig();
        return systemConfig;
    }

    @Override
    public UserConfig getUserConfig(String user) {
        if (userConfigMap != null) {
            return userConfigMap.get(user);
        }
        UserConfig userConfig = new UserConfig();
        userConfig.setName("root");

        userConfig.setPassword("123456");
        HashSet<String> schemas = new HashSet<String>();
        schemas.add("db");
        userConfig.setSchemas(schemas);
        UserPrivilegesConfig privilegesConfig = new UserPrivilegesConfig();
        privilegesConfig.setCheck(false);
        userConfig.setPrivilegesConfig(privilegesConfig);
        return userConfig;
    }

    @Override
    public Map<String, UserConfig> getUserConfigs() {
        if (userConfigMap != null) {
            return userConfigMap;
        }
        Map<String, UserConfig> map = new HashMap<>();
        map.put("root", getUserConfig("root"));
        return map;
    }

    @Override
    public FirewallConfig getFirewallConfig() {
        if (firewallConfig != null) {
            return firewallConfig;
        }
        FirewallConfig firewallConfig = new FirewallConfig();
        return firewallConfig;
    }

    @Override
    public ClusterConfig getClusterConfig() {
        if (clusterConfig != null) {
            return clusterConfig;
        }
        ClusterConfig clusterConfig = new ClusterConfig();
        return clusterConfig;
    }

}