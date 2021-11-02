/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.createfunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: CreateFunctionUiData for use
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2021-04-26]
 * @since 2021-04-26
 */
public class CreateFunctionUiData {
    /**
     * Title: ErrType for use
     * Description: this use to show err msg of ui param
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author z00588921
     * @version [DataStudio for openGauss 2021-04-26]
     * @since 2021-04-26
     */
    public enum ErrType {
        ERR_SUCCESS("success"),
        ERR_FUNCNAME("function name can't be empty"),
        ERR_FUNCTYPE("function return type can't be empty"),
        ERR_FUNCBODY("function body can't be empty"),
        ERR_FUNCPARAM("function param name not valid!");

        /**
         * The error message string
         */
        public final String errMsg;
        ErrType(String errMsg) {
            this.errMsg = errMsg;
        }
    }

    private CreateFunctionRelyInfo relyInfo;
    private String functionName;
    private String language;
    private String functionReturnType;
    private List<List<String>> paramList;
    private String functionBody;

    public CreateFunctionUiData(
            CreateFunctionRelyInfo relyInfo,
            String functionName,
            String language,
            String functionReturnType,
            List<CreateFunctionParam> paramList,
            String functionBody) {
        this.relyInfo = relyInfo;
        this.functionName = functionName;
        this.language = language;
        this.functionReturnType = functionReturnType;
        this.paramList = new ArrayList<>();
        for (CreateFunctionParam param: paramList) {
            this.paramList.add(new ArrayList<String>(param.getDatas()));
        }
        this.functionBody = functionBody;
    }

    /**
     * Check if is valid
     *
     * @return ErrType the error type
     */
    public ErrType valid() {
        if ("".equals(this.functionName)) {
            return ErrType.ERR_FUNCNAME;
        }

        if (!isProcedure()
                && !isTrigger()
                && "".equals(functionReturnType)) {
            return ErrType.ERR_FUNCTYPE;
        }

        if ("".equals(this.functionBody)) {
            return ErrType.ERR_FUNCBODY;
        }
        for (List<String> param: this.paramList) {
            String paramName = param.get(0);
            if ("".equals(paramName)
                    || CreateFunctionParam.INVALID_PARAM_NAME.equals(paramName)) {
                return ErrType.ERR_FUNCPARAM;
            }
        }
        return ErrType.ERR_SUCCESS;
    }

    private boolean isProcedure() {
        return CreateFunctionRelyInfo.PROCEDURE.equals(language);
    }

    private boolean isTrigger() {
        return CreateFunctionRelyInfo.LANGUAGE_TRIGGER.equals(language);
    }

    private String functionType() {
        return isProcedure() ? "PROCEDURE" : "FUNCTION";
    }

    /**
     * Get function define
     *
     * @return String the function define
     */
    public String getFunctionDefine() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("CREATE OR REPLACE " + functionType() + " ");
        sb.append(relyInfo.getSchameName());
        sb.append(".");
        sb.append(functionName);
        sb.append(" (");
        sb.append(formatParam());
        sb.append(")");

        sb.append(relyInfo.getLineSeparator());

        if (!isProcedure()) {
            sb.append("\tRETURNS ");
            sb.append(isTrigger() ? CreateFunctionRelyInfo.LANGUAGE_TRIGGER : functionReturnType);
            sb.append(relyInfo.getLineSeparator());
            sb.append("\tLANGUAGE "
                + (isTrigger() ? CreateFunctionRelyInfo.LANGUAGE_PLP : language));
            sb.append(relyInfo.getLineSeparator());
        }
        sb.append(relyInfo.getLineSeparator());
        sb.append(functionBody);
        return sb.toString();
    }

    /**
     * Format params
     *
     * @return String the formatted param string
     */
    public String formatParam() {
        if (isTrigger()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (this.paramList.size() != 0) {
            sb.append(relyInfo.getLineSeparator());
        }

        int index = 0;
        for (List<String> param: this.paramList) {
            String name = param.get(0).trim();
            String mode = param.get(1).trim();
            String type = param.get(2).trim();
            sb.append(name);
            sb.append(" ");
            sb.append(mode);
            sb.append(" ");
            sb.append(type);
            String defaultValue = param.get(3).trim();
            if (!"".equals(defaultValue)) {
                sb.append(" DEFAULT ");
                sb.append(defaultValue);
            }
            if (index != this.paramList.size() - 1) {
                sb.append(",");
                sb.append(relyInfo.getLineSeparator());
            }
            index += 1;
        }
        if (this.paramList.size() != 0) {
            sb.append(relyInfo.getLineSeparator());
        }

        return sb.toString();
    }
}