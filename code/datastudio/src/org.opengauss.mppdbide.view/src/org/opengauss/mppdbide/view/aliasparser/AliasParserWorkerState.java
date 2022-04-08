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

package org.opengauss.mppdbide.view.aliasparser;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum AliasParserWorkerState. State machine for Alias parser
 * worker IDLE : Serving no job BUSY : Processing a parsing request
 *
 * @since 3.0.0
 */
public enum AliasParserWorkerState {

    /**
     * The idle.
     */
    IDLE,
    /**
     * The busy.
     */
    BUSY
}
