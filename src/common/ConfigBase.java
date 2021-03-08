package ksb.strokesos.tester.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConfigBase implements IConfigurable {

    /**
     * This interface to inject settings to Spark ML function
     */
    @SuppressWarnings("unchecked")
    public <T> T getSetting(String key) {
        T value = null;
        String methodName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
        Method method = null;
        try {
            method = this.getClass().getMethod(methodName);
            value = (T) method.invoke(this);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public <T> void set(String key, T value) {
        String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
        Method method = null;
        try {
            method = this.getClass().getMethod(methodName);
            method.invoke(this, value);
        } catch (NoSuchMethodException e) {
            method = findMethodInHierarchy(this.getClass(), methodName);
            if (method == null) {
                e.printStackTrace();
            }else {
                try {
                    method.invoke(this, value);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets an array of all methods in a class hierarchy walking up to parent
     * classes
     *
     * @param objectClass
     *            the class
     * @return the methods array
     */
    @SuppressWarnings("unused")
    private static Method[] getAllMethodsInHierarchy(Class<?> objectClass) {
        Set<Method> allMethods = new HashSet<Method>();
        Method[] declaredMethods = objectClass.getDeclaredMethods();
        Method[] methods = objectClass.getMethods();
        if (objectClass.getSuperclass() != null) {
            Class<?> superClass = objectClass.getSuperclass();
            Method[] superClassMethods = getAllMethodsInHierarchy(superClass);
            allMethods.addAll(Arrays.asList(superClassMethods));
        }
        allMethods.addAll(Arrays.asList(declaredMethods));
        allMethods.addAll(Arrays.asList(methods));
        return allMethods.toArray(new Method[allMethods.size()]);
    }

    /**
     * to find a method from all methods in hierarchy
     *
     * @param objectClass
     * @param methodName
     * @return
     */
    private Method findMethodInHierarchy(Class<?> objectClass, String methodName) {
        for (Method method : objectClass.getMethods()) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        if (objectClass.getSuperclass() != null) {
            return findMethodInHierarchy(objectClass.getSuperclass(), methodName);
        } else {
            return null;
        }
    }
}
