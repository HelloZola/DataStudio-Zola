/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.loader;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class MessageConfigLoader.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class MessageConfigLoader {
    /** 
     * The Constant UNKNOWN_ERROR. 
     */
    public static final String UNKNOWN_ERROR = "Unknown error.";
    private static MessagePropertiesLoader messagePropertiesLoader = null;
    private static final Object LOCK = new Object();

    /**
     * Static block to load the resource bundle file. This will be loaded once
     * in a session.
     * 
     * MPPDBIDELoggerUtility MessageConfigLoader
     */
    static {
        try {
            MessageConfigLoader.load("messages.properties");
        } catch (Exception exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_LOAD_PROPERTIES),
                    exception);
        }
    }

    /**
     * Load.
     *
     * @param propertiesFileName the properties file name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void load(String propertiesFileName) throws IOException {
        synchronized (LOCK) {
            // get the URL of the property file
            ClassLoader classLoader = MessagePropertiesLoader.class.getClassLoader();

            String messageFileName = propertiesFileName;

            StringBuffer msgFileCon = new StringBuffer("messages_");

            if (null != classLoader) {

                String locale = Locale.getDefault().toString();

                if (locale.equals(MPPDBIDEConstants.CHINESE_LOCALE)) {
                    messageFileName = msgFileCon.append(locale).append(".properties").toString();
                }

                URL propertiesURL = classLoader.getResource(messageFileName);
                if (null != propertiesURL) {
                    messagePropertiesLoader = new MessagePropertiesLoader(propertiesURL);
                }
            }
        }

    }

    /**
     * Gets the property.
     *
     * @param key the key
     * @return the property
     */
    public static String getProperty(String key) {
        if (null != messagePropertiesLoader) {
            String message = messagePropertiesLoader.getProperty(key);

            if (null == message) {
                return UNKNOWN_ERROR;
            } else if (key.equalsIgnoreCase(message)) {
                return UNKNOWN_ERROR;
            }

            return message;
        } else {
            return "";
        }

    }

    /**
     * Gets the property.
     *
     * @param key the key
     * @param params the params
     * @return the property
     */
    public static String getProperty(String key, Object... params) {
        if (null != messagePropertiesLoader) {
            String message = messagePropertiesLoader.getProperty(key, params);

            if (null == message || key.equalsIgnoreCase(message)) {
                return UNKNOWN_ERROR;
            }

            return message;
        } else {
            return "";
        }
    }

}
