package ksb.strokesos.tester.common;

public interface IConfigurable {
    /**
     * To get setting value from key
     *
     * @param key
     * @return
     */
    public <T> T getSetting(String key);

    /**
     * to set value for key
     *
     * @param key
     * @param value
     */
    public <T> void set(String key, T value);
}
