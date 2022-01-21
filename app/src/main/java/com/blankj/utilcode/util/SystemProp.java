package com.blankj.utilcode.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by coomo on 2017/12/2.
 */

public class SystemProp {
    public static String get(String prop) {
        return get(prop, "");
    }

    public static String get(String prop, String defValue) {
        String ret;
        try {
            Method systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            if ((ret = (String) (systemProperties_get.invoke(null, prop))) != null) {
                return ret;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        return defValue;
    }

    public static void set(String prop, String value) {
        try {
            Method systemProperties_set = Class.forName("android.os.SystemProperties").getDeclaredMethod("set", new Class<?>[]{String.class, String.class});
            systemProperties_set.invoke(null, new Object[]{prop, value});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
