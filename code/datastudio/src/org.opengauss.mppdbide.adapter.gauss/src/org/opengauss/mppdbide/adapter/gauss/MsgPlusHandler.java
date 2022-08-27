package org.opengauss.mppdbide.adapter.gauss;

import java.util.List;

import org.opengauss.mppdbide.utils.messaging.MessageQueue;

public interface MsgPlusHandler {

	List<String> handlerMsg(String msg);

}
