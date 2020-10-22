/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import org.eclipse.swt.graphics.Image;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.ShowMoreObject;
import com.huawei.mppdbide.bl.serverdatacache.SynonymMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.bl.serverdatacache.UserRole;
import com.huawei.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.DatabaseObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ForeignTableGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.IndexList;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import com.huawei.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SystemNamespaceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TablespaceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.UserNamespaceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.UserRoleObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectBrowserLabelProviderForGaussOLAP.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ObjectBrowserLabelProviderForGaussOLAP extends AbstarctObjectBrowserLabelProvider {

    /**
     * Gets the text.
     *
     * @param element the element
     * @return the text
     */
    @Override
    public String getText(Object element) {
        if (element instanceof ServerObject) {
            return ((ServerObject) element).getObjectBrowserLabel();
        } else if (element instanceof Server) {
            Server node = (Server) element;
            return node.getDisplayNameForDomain();
        } else if (element instanceof OLAPObjectGroup<?>) {
            return ((OLAPObjectGroup<?>) element).getObjectBrowserLabel();
        } else if (element instanceof OLAPObjectList<?>) {
            return ((OLAPObjectList<?>) element).getName();
        } 
        
        // Will be hit only when new datatypes are displayed in further
        // iterations
        return MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_BROWSER_LABEL_MSG);
    }

    /**
     * Gets the tool tip text.
     *
     * @param element the element
     * @return the tool tip text
     */
    @Override
    public String getToolTipText(Object element) {
        if (element instanceof Server) {
            Server serv = (Server) element;
            return getToolTipTextHelper(serv);
        }
        return null;
    }

    /**
     * Gets the image.
     *
     * @param element the element
     * @return the image
     */
    @Override
    public Image getImage(Object element) {
        Image retImage = getImageOne(element);

        if (null == retImage) {
            retImage = getImageTwo(element);
        }

        if (null == retImage) {
            retImage = getImageThree(element);
        }

        if (null == retImage) {
            return getImagePart2(element);
        } else {
            return retImage;
        }

    }

    /**
     * Gets the image three.
     *
     * @param element the element
     * @return the image three
     */
    private Image getImageThree(Object element) {
        Image retImage = null;
        if (element instanceof ViewObjectGroup) {
            retImage = IconUtility.getIconImage(IiconPath.ICO_VIEWS, getClass());
        } else if (element instanceof TablespaceObjectGroup) {
            retImage = IconUtility.getIconImage(IiconPath.TABLESPACE_GROUP, getClass());
        } else if (element instanceof DatabaseObjectGroup) {
            retImage = IconUtility.getIconImage(IiconPath.DATABASE_GROUP, getClass());
        } else if (element instanceof IndexList) {
            retImage = IconUtility.getIconImage(IiconPath.ICO_INDEXES, getClass());
        }

        else if (element instanceof Tablespace) {
            retImage = IconUtility.getIconImage(IiconPath.TABLESPACE, getClass());
        } else if (element instanceof UserRoleObjectGroup) {
            retImage = IconUtility.getIconImage(IiconPath.USER_ROLE_GROUP, getClass());
        } else if (element instanceof UserRole) {
            if (((UserRole) element).getRolCanLogin()) {
                retImage = IconUtility.getIconImage(IiconPath.USER_ROLE_CAN_LOGIN, getClass());
            } else {
                retImage = IconUtility.getIconImage(IiconPath.USER_ROLE_CAN_NOT_LOGIN, getClass());
            }
        }
        return retImage;
    }

    /**
     * Gets the image two.
     *
     * @param element the element
     * @return the image two
     */
    private Image getImageTwo(Object element) {
        Image retImage = null;
        if (element instanceof Namespace) {
            retImage = IconUtility.getIconImage(IiconPath.ICO_NAMESPACE, getClass());
        } else if (element instanceof DebugObjectGroup) {
            retImage = IconUtility.getIconForDebugObjectType(((DebugObjectGroup) element).getObjectGroupType());
        } else if (element instanceof DebugObjects) {
            retImage = IconUtility.getIconForDebugObjectType(((DebugObjects) element).getObjectType());
        }

        else if (element instanceof ForeignTableGroup) {
            retImage = IconUtility.getIconImage(IiconPath.FOREIGN_TABLE_GROUP, getClass());
        } else if (element instanceof SequenceObjectGroup) {
            retImage = IconUtility.getIconImage(IiconPath.SEQUENCE_OBJECT_GROUP, getClass());
        } else if (element instanceof SequenceMetadata) {
            retImage = IconUtility.getIconImage(IiconPath.SEQUENCE_OBJECT, getClass());
        } else if (element instanceof SynonymObjectGroup) {
            retImage = IconUtility.getIconImage(IiconPath.ICO_SYNONYMS, getClass());
        } else if (element instanceof SynonymMetaData) {
            retImage = IconUtility.getIconImage(IiconPath.ICO_SYNONYM, getClass());
        } else if (element instanceof TableObjectGroup) {
            retImage = IconUtility.getIconImage(IiconPath.ICO_TABLES, getClass());
        }

        return retImage;
    }

    /**
     * Gets the image one.
     *
     * @param element the element
     * @return the image one
     */
    private Image getImageOne(Object element) {
        Image retImage = null;

        if (element instanceof TableMetaData) {
            retImage = getTablemetaDataImage(element);

        } else if (element instanceof ViewMetaData) {
            retImage = IconUtility.getIconImage(IiconPath.ICO_VIEW, getClass());
        } else if (element instanceof Server) {
            retImage = IconUtility.getIconImage(IiconPath.ICO_SERVER, getClass());
        } else if (element instanceof Database) {
            retImage = getDatabaseImage(element);
        } else if (element instanceof UserNamespaceObjectGroup) {
            retImage = IconUtility.getIconImage(IiconPath.ICO_USER_NAMESPACE_GROUP, getClass());
        } else if (element instanceof SystemNamespaceObjectGroup) {
            retImage = IconUtility.getIconImage(IiconPath.ICO_SYSTEM_NAMESPACE_GROUP, getClass());
        } else if (element instanceof ShowMoreObject) {
            retImage = IconUtility.getIconImage(IiconPath.ICO_LOAD, getClass());
        } else if (element instanceof TableObjectGroup) {
            return IconUtility.getIconImage(IiconPath.ICO_TABLES, getClass());
        } else if (element instanceof ViewObjectGroup) {
            return IconUtility.getIconImage(IiconPath.ICO_VIEWS, getClass());
        } else if (element instanceof Tablespace) {
            return IconUtility.getIconImage(IiconPath.TABLESPACE, getClass());
        }

        return retImage;
    }

    /**
     * Gets the database image.
     *
     * @param element the element
     * @return the database image
     */
    private Image getDatabaseImage(Object element) {
        Database db = (Database) element;
        String icon = IiconPath.ICO_DISCONNECTED_DB;
        if (db.isConnected()) {
            icon = IiconPath.ICO_CONNECTED_DB;
        }

        return IconUtility.getIconImage(icon, getClass());
    }

    /**
     * Gets the tablemeta data image.
     *
     * @param element the element
     * @return the tablemeta data image
     */
    private Image getTablemetaDataImage(Object element) {
        TableMetaData tData = (TableMetaData) element;
        OBJECTTYPE tableType = tData.getType();
        String iconName = null;

        switch (tableType) {

            case TABLEMETADATA: {
                iconName = IiconPath.ICO_TABLE;
                break;
            }
            case FOREIGN_TABLE_GDS: {
                iconName = IiconPath.FOREIGN_TABLE_GDS;
                break;
            }
            case FOREIGN_TABLE_HDFS: {
                iconName = IiconPath.FOREIGN_TABLE_HDFS;
                break;
            }
            case PARTITION_TABLE: {
                iconName = IiconPath.PARTITION_TABLE;
                break;
            }
            case FOREIGN_PARTITION_TABLE: {
                iconName = IiconPath.ICO_FOREIGN_PARTITION_TABLE;
                break;
            }
            default: {
                iconName = IiconPath.ICO_TABLE;
            }

        }
        return IconUtility.getIconImage(iconName, this.getClass());
    }

    /**
     * Gets the image part 2.
     *
     * @param element the element
     * @return the image part 2
     */
    private Image getImagePart2(Object element) {
        if (element instanceof OLAPObjectList<?>) {
            OBJECTTYPE type = ((OLAPObjectList<?>) element).getType();
            switch (type) {
                case COLUMN_GROUP:
                case VIEW_COLUMN_GROUP: {
                    return IconUtility.getIconImage(IiconPath.ICO_COLUMNS, getClass());
                }
                case CONSTRAINT_GROUP: {
                    return IconUtility.getIconImage(IiconPath.ICO_CONSTRAINTS, getClass());
                }
                default: {
                    break;
                }
            }
        } else if (element instanceof ConstraintMetaData) {
            return IconUtility.getIconImage(IiconPath.ICO_CONSTRAINTS, getClass());
        } else if (element instanceof IndexMetaData) {
            return IconUtility.getIconImage(IiconPath.ICO_INDEX, getClass());
        } else if (element instanceof ColumnMetaData || element instanceof ViewColumnMetaData) {
            return IconUtility.getIconImage(IiconPath.ICO_COLUMN, getClass());
        } else if (element instanceof OLAPObjectGroup<?>) {
            return IconUtility.getIconImage(IiconPath.ICO_CLOUD_NODES, getClass());
        }

        return IconUtility.getIconImage(IiconPath.ICO_TABLESPACE, getClass());
    }
}