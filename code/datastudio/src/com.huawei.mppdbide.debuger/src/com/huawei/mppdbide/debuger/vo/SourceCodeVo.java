/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.vo;

import com.huawei.mppdbide.debuger.annotation.DumpFiled;

/**
 * Title: the SourceCodeVo class
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/19]
 * @since 2020/11/19
 */
public class SourceCodeVo {
    /**
     *  the source code
     */
    @DumpFiled
    private String pldbg_get_source;

    /**
     * get source code
     *
     * @return String source code
     */
    public String getSourceCode() {
        return pldbg_get_source;
    }
}
