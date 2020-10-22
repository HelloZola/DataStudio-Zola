/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties.handler;

import java.sql.SQLException;
import java.util.List;

import com.huawei.mppdbide.presentation.IWindowDetail;
import com.huawei.mppdbide.presentation.PropertyOperationType;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.objectproperties.IObjectPropertyData;
import com.huawei.mppdbide.presentation.objectproperties.IServerObjectProperties;
import com.huawei.mppdbide.presentation.objectproperties.factory.ServerFactory;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertyHandlerCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class PropertyHandlerCore {
    private IServerObjectProperties iServerObject;
    private String objectName;

    /**
     * The conn infra.
     */
    protected TerminalExecutionConnectionInfra connInfra;

    /**
     * The details.
     */
    protected IWindowDetail details;
    /**
     * Instantiates a new property handler core.
     */
    public PropertyHandlerCore() {

    }

    /**
     * Instantiates a new property handler core.
     *
     * @param obj the obj
     */
    public PropertyHandlerCore(Object obj) {
        this(obj, PropertyOperationType.PROPERTY_OPERATION_VIEW);
    }

    /**
     * Instantiates a new property handler core.
     *
     * @param obj the obj
     * @param propertyOperationEdit the property operation edit
     */
    public PropertyHandlerCore(Object obj, PropertyOperationType propertyOperationEdit) {
        // initialization of the object
        ServerFactory factory = new ServerFactory();
        this.iServerObject = factory.getObject(obj, propertyOperationEdit);
        if (null != iServerObject) {
            this.setObjectName(iServerObject.getObjectName());
        }

        details = new ObjectPropertiesWindowDetails();
    }

    /**
     * Sets the properties object.
     *
     * @param obj the new properties object
     */
    protected void setPropertiesObject(IServerObjectProperties obj) {
        this.iServerObject = obj;
    }

    /**
     * Checks if is executable.
     *
     * @return true, if is executable
     */
    public boolean isExecutable() {
        if (iServerObject != null) {
            return true;
        }
        return false;
    }

    /**
     * Gets the property.
     *
     * @return the property
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws SQLException the SQL exception
     */
    public IPropertyDetail getproperty() throws MPPDBIDEException, SQLException {

        PropertyDetailImpl obj = null;
        List<IObjectPropertyData> propList = null;
        IObjectPropertyData parentProperty = null;
        propList = iServerObject.getAllProperties(connInfra.getConnection());
        parentProperty = iServerObject.getParentProperties(connInfra.getConnection());
        String uId = getUniqueID();
        String windowTitle = getWindowTitle();
        obj = new PropertyDetailImpl(uId, windowTitle, propList, this, parentProperty);
        return obj;
    }

    private String getWindowTitle() {
        return iServerObject.getHeader();
    }

    private String getUniqueID() {
        return iServerObject.getUniqueID();
    }

    private void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class PropertyDetailImpl.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class PropertyDetailImpl implements IPropertyDetail {
        private String uid;
        private String title;
        private List<IObjectPropertyData> prop;
        private PropertyHandlerCore core;
        private IObjectPropertyData parentObjectProperty;

        PropertyDetailImpl(String uID, String windowTitle, List<IObjectPropertyData> propList,
                PropertyHandlerCore propertyHandlerCore, IObjectPropertyData parentObjectProperty) {
            this.uid = uID;
            this.title = windowTitle;
            this.prop = propList;
            this.core = propertyHandlerCore;
            this.parentObjectProperty = parentObjectProperty;

        }

        @Override
        public String getHeader() {
            return title;
        }

        @Override
        public String getUniqueID() {
            return uid;
        }

        @Override
        public List<IObjectPropertyData> objectproperties() {
            return prop;
        }

        @Override
        public PropertyHandlerCore getPropertyCore() {
            return this.core;
        }

        @Override
        public IObjectPropertyData getParentProperty() {
            return this.parentObjectProperty;
        }

    }

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    public TerminalExecutionConnectionInfra getTermConnection() {
        if (null == connInfra) {
            this.connInfra = new TerminalExecutionConnectionInfra();
            this.connInfra.setDatabase(iServerObject.getDatabase());
        }

        return connInfra;
    }

    /**
     * Gets the window details.
     *
     * @return the window details
     */
    public IWindowDetail getWindowDetails() {
        return details;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ObjectPropertiesWindowDetails.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class ObjectPropertiesWindowDetails implements IWindowDetail {

        @Override
        public String getTitle() {
            return iServerObject.getHeader();
        }

        @Override
        public String getUniqueID() {
            return iServerObject.getUniqueID();   
        }

        @Override
        public String getIcon() {
            return null;
        }

        @Override
        public String getShortTitle() {
            return objectName;
        }

        @Override
        public boolean isCloseable() {
            return true;
        }
    }

}