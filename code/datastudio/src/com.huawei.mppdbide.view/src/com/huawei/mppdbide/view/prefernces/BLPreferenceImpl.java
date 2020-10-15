/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.view.utils.Preferencekeys;

/**
 * 
 * Title: class
 * 
 * Description: The Class BLPreferenceImpl.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class BLPreferenceImpl implements IBLPreference, IPropertyChangeListener {

    private static final String SQL_QUERY_LENGTH = ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH;
    private static final String SQL_HISTORY_SIZE = ISQLHistoryPreferencesLabelFactory.SQL_HISTORY_SIZE;
    private static final String OBECT_COUNT_FOR_LAZY_RENDERING = "com.huawei.mppdbide.environment.sessionsetting.lazyrendering";
    private PreferenceStore ps = null;
    private static volatile IBLPreference systemPrefernce = null;
    private static final Object LOCK = new Object();

    /**
     * Instantiates a new BL preference impl.
     */
    public BLPreferenceImpl() {
        this.ps = PreferenceWrapper.getInstance().getPreferenceStore();
        this.ps.addPropertyChangeListener(this);
    }

    /**
     * Gets the BL preference.
     *
     * @return the BL preference
     */
    public static IBLPreference getBLPreference() {
        if (null == systemPrefernce) {
            synchronized (LOCK) {
                if (null == systemPrefernce) {
                    systemPrefernce = new BLPreferenceImpl();
                }

            }
        }
        return systemPrefernce;

    }

    /**
     * Gets the SQL history size.
     *
     * @return the SQL history size
     */
    @Override
    public int getSQLHistorySize() {
        return ps.getInt(SQL_HISTORY_SIZE);
    }

    /**
     * Gets the SQL query length.
     *
     * @return the SQL query length
     */
    @Override
    public int getSQLQueryLength() {
        return ps.getInt(SQL_QUERY_LENGTH);
    }

    /**
     * Gets the DS encoding.
     *
     * @return the DS encoding
     */
    @Override
    public String getDSEncoding() {

        return ps.getString(UserEncodingOption.DATA_STUDIO_ENCODING);
    }

    /**
     * Gets the file encoding.
     *
     * @return the file encoding
     */
    @Override
    public String getFileEncoding() {
        return ps.getString(UserEncodingOption.FILE_ENCODING);
    }

    /**
     * Checks if is include encoding.
     *
     * @return true, if is include encoding
     */
    public boolean isIncludeEncoding() {
        return ps.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_ENCODING);
    }

    /**
     * Property change.
     *
     * @param event the event
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(SQL_HISTORY_SIZE)) {
            Integer historySize = (Integer) event.getNewValue();
            SQLHistoryFactory.getInstance().setHistoryRetensionSize(historySize);
        }

        if (event.getProperty().equals(SQL_QUERY_LENGTH)) {
            Integer queryLength = (Integer) event.getNewValue();
            SQLHistoryFactory.getInstance().setSQLQuerySize(queryLength);
        }

    }

    /**
     * Read preference value for object count to show in object browser
     */
    @Override
    public int getLazyRenderingObjectCount() {
        return ps.getInt(OBECT_COUNT_FOR_LAZY_RENDERING);
    }

    /**
     * Gets the date format.
     *
     * @return the date format.
     */
    @Override
    public String getDateFormat() {
        return ps.getString(MPPDBIDEConstants.DATE_FORMAT_VALUE);
    }

    /**
     * Gets the date format.
     *
     * @return the date format.
     */
    @Override
    public String getTimeFormat() {
        return ps.getString(MPPDBIDEConstants.TIME_FORMAT_VALUE);
    }
    
    @Override
    public int getImportFileSizeInMb() {
        return ps.getInt(Preferencekeys.FILE_LIMIT_FOR_TABLE_DATA);
    }
}
