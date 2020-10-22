/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020. All rights reserved.
 */

package com.huawei.mppdbide.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Title: class
 * 
 * Description: The Class DateTimeFormatValidator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * @author s00428892
 * @version [DataStudio 8.0.2, 3 Mar, 2020]
 * @since 3 Mar, 2020
 */

public class DateTimeFormatValidator {
    public DateTimeFormatValidator() {
    }

    /**
     * validates date format
     * 
     * @param format the date format
     * @return true if date format is valid
     */
    public static boolean validateDateFormat(String format) {
        List<String> dateFormatRegExList = new ArrayList<String>();
        String dateFormatRegex1 = "^([y|Y]{2}|[y|Y]{4})(-|/|.|:|)([M]{2}|[m|M]{3})(-|/|.|:|)[d]{2}$";
        String dateFormatRegex2 = "^([y|Y]{2}|[y|Y]{4})(-|/|.|:|)[d]{2}(-|/|.|:|)([M]{2}|[M]{3})$";
        String dateFormatRegex3 = "^([M]{2}|[M]{3})(-|/|.|:|)([y|Y]{2}|[y|Y]{4})(-|/|.|:|)[d]{2}$";
        String dateFormatRegex4 = "^([M]{2}|[M]{3})(-|/|.|:|)[d]{2}(-|/|.|:|)([y|Y]{2}|[y|Y]{4})$";
        String dateFormatRegex5 = "^[d]{2}(-|/|.|:|)([y|Y]{2}|[y|Y]{4})(-|/|.|:|)([M]{2}|[M]{3})$";
        String dateFormatRegex6 = "^[d]{2}(-|/|.|:|)([M]{2}|[M]{3})(-|/|.|:|)([y|Y]{2}|[y|Y]{4})$";
        dateFormatRegExList.add(dateFormatRegex1);
        dateFormatRegExList.add(dateFormatRegex2);
        dateFormatRegExList.add(dateFormatRegex3);
        dateFormatRegExList.add(dateFormatRegex4);
        dateFormatRegExList.add(dateFormatRegex5);
        dateFormatRegExList.add(dateFormatRegex6);
        for (String df : dateFormatRegExList) {
            Pattern pattern = Pattern.compile(df);
            Matcher matcher = pattern.matcher(format);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * validates time format
     * 
     * @param format the time format
     * @return true if time format is valid
     */
    public static boolean validateTimeFormat(String format) {
        List<String> timeFormatRegExList = new ArrayList<String>();
        String timeFormatRegex1 = "^([h|H]{2})(:|)([m|M]{2})(:|)([s]{2})$";
        String timeFormatRegex2 = "^([h|H]{2})(:|)([m|M]{2})(:|)([s|S]{2})(.|)([s|S]{2}|[s|S]{3})$";
        String timeFormatRegex3 = "^([h|H]{2})(:|)([m|M]{2})$";
        timeFormatRegExList.add(timeFormatRegex1);
        timeFormatRegExList.add(timeFormatRegex2);
        timeFormatRegExList.add(timeFormatRegex3);
        for (String timeformat : timeFormatRegExList) {
            Pattern pattern = Pattern.compile(timeformat);
            Matcher matcher = pattern.matcher(format);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * gets the DatePlusTimeFormat
     * 
     * @param dateFormat the date format
     * @param timeFormat the time format
     * @return the date format
     */
    public static String getDatePlusTimeFormat(String dateFormat, String timeFormat) {
        StringBuilder format = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        format.append(dateFormat);
        format.append(" " + timeFormat);
        return format.toString();
    }
}