/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.bl.serverdatacache;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class TypeMetaDataUtil.
 * 
 */

public class TypeMetaDataUtil {

    /**
     * Gets the data type from namespace.
     *
     * @param datatypeOid the datatype oid
     * @param nsList the ns list
     * @return the data type from namespace
     */
    public static String getDataTypeFromNamespace(int datatypeOid, ArrayList<Namespace> nsList) {
        String convertedDataType = null;

        if (nsList != null) {
            for (Namespace ns : nsList) {
                convertedDataType = getDataTypeFromNamespace(datatypeOid, ns);
                if (convertedDataType != null) {
                    break;
                }
            }
        }

        if (convertedDataType == null) {
            convertedDataType = MPPDBIDEConstants.UNKNOWN_DATATYPE_STR;
        }

        return convertedDataType;
    }

    /**
     * Gets the data type from namespace.
     *
     * @param rettype the rettype
     * @param ns the ns
     * @return the data type from namespace
     */
    private static String getDataTypeFromNamespace(int rettype, Namespace ns) {
        if (ns != null) {
            TypeMetaData tmd = ns.getTypeByOid(rettype);
            if (tmd != null) {
                return tmd.getName();
            }
        }

        return null;
    }
    
    public static final String TYPE_STATENMENT_BY_OID = "select pt.oid as typeoid, pt.typname as typename from pg_type pt where pt.oid = ?";

	public static TypeMetaData fetchTypeByOid(int oid, DBConnection dbconnection) throws DatabaseOperationException {

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			preparedStatement = dbconnection.getPrepareStmt(TYPE_STATENMENT_BY_OID);
			preparedStatement.setInt(1, oid);
			rs = preparedStatement.executeQuery();
			boolean hasNext = rs.next();
			while (hasNext) {
				TypeMetaData typeMetaData = new TypeMetaData(0, rs.getString("typename"), null);
				typeMetaData.setName(rs.getString("typename"));
				typeMetaData.setOid(rs.getLong("typeoid"));
				return typeMetaData;
			}
			return null;
		} catch (Exception e) {
			// TODO: handle exception
			throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
		} finally {
			if (rs != null) {
				dbconnection.closeResultSet(rs);
			}
		}
	}
}
