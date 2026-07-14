package org.sara.api.Util;

import java.util.ResourceBundle;

public class LoginUtil {
    public static ResourceBundle resourceBundle() {
        if (System.getProperty("env") != null) {
            return ResourceBundle.getBundle(System.getProperty("env"));
        } else {
            return ResourceBundle.getBundle("Datos/user");
        }
    }
    public static String getLoginUtil(String keys) {
        return resourceBundle().getString(keys);
    }

}
