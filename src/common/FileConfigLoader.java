package ksb.strokesos.tester.common;

import java.io.*;
import java.nio.file.Paths;
import java.util.Properties;

public class FileConfigLoader extends ConfigBase {
    private Properties prop;
    private String path;

    /**
     * To load all setting
     *
     * @throws IOException
     */
    public FileConfigLoader(String path) throws IOException {
        InputStream input = null;
        prop = new Properties();
        this.path = path;
        input = new FileInputStream(this.path);
        prop.load(input);
    }

    /**
     * To update configuration file This function uses to update setting from
     * web page
     *
     * @param key
     * @param value
     */
    public void updateProperty(String key, String value) {
        prop.setProperty(key, value);
    }

    /**
     * @param key
     */
    @SuppressWarnings("unchecked")
    public String getSetting(String key) {
        return prop.getProperty(key);
    }

    /**
     * This function uses for sending properties from web-server to spark-server
     *
     * @param out
     *            The output stream to write
     * @throws IOException
     */
    public void sendProperties(OutputStream out) throws IOException {
        prop.store(out, null);
    }

    /**
     * to get current Properties
     *
     * @return
     */
    public Properties getProperties() {
        return prop;
    }

    /**
     * to save all changes to configuration file
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void saveProperties() throws FileNotFoundException, IOException {
        // User updated information
        String userName = System.getProperty("user.name");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVer = System.getProperty("os.version");
        prop.store(new FileOutputStream(path),
                String.format(("Updated by user: %s\nOS information: name: %s, architectue: %s, version: %s"), userName,
                        osName, osArch, osVer));
    }

    /**
     * to make file path
     *
     * @param strings
     * @return
     * @throws IOException
     */
    public static String getPathInWorkingFolder(String... strings) throws IOException {

        String homeDir = System.getenv(Constants.SYS_ENV_HOME_DIR);
        if (homeDir == null) {
            throw new IOException("The environment " + Constants.SYS_ENV_HOME_DIR + "is not yet set.");
        }
        /*
        else {
            System.out.println("HOME_DIR: " + homeDir);
        }
         */
        return Paths.get(homeDir, strings).toString();
    }
}
