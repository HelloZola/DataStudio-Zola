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

package org.opengauss.mppdbide.adapter.gauss;

import java.sql.SQLWarning;
import java.util.List;

import org.postgresql.core.NoticeListener;
import org.apache.commons.lang.StringUtils;
import org.opengauss.mppdbide.utils.messaging.IMessageQueue;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.MessageQueue;
import org.opengauss.mppdbide.utils.messaging.MessageType;

/**
 * 
 * Title: class
 * 
 * Description: The Class GaussMppDbNoticeListner.
 *
 * @since 3.0.0
 */
public class GaussMppDbNoticeListner implements NoticeListener {
	
    private IMessageQueue msgQ;
    private MsgPlusHandler msgPlusHandler;

    /**
     * Instantiates a new gauss mpp db notice listner.
     *
     * @param messageQueue the message queue
     */
    public GaussMppDbNoticeListner(MessageQueue messageQueue) {
        msgQ = messageQueue;
    }
    
    public GaussMppDbNoticeListner(MessageQueue messageQueue, MsgPlusHandler msgPlusHandler) {
        this.msgQ = messageQueue;
        this.msgPlusHandler = msgPlusHandler;
    }

    @Override
    public void noticeReceived(SQLWarning notice) {
        if (null == notice || null == notice.getMessage() || null == msgQ) {
            return;
        }

        String msgString = notice.getMessage();
        Message msg = new Message(MessageType.NOTICE, msgString);
		if (msgPlusHandler != null) {
			List<String> msgList = msgPlusHandler.handlerMsg(msgString);
			for (String mm : msgList) {
				if(StringUtils.isNotBlank(mm)) {
					Message mmsg = new Message(MessageType.NOTICE, mm);
					msgQ.push(mmsg);
				}
			}
		} else {
			msgQ.push(msg);
		}
    }

}
