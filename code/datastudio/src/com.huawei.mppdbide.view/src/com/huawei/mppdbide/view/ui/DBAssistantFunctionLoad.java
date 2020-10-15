/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBAssistantFunctionLoad.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DBAssistantFunctionLoad extends BrowserFunction {

    /**
     * Instantiates a new DB assistant function load.
     *
     * @param browser the browser
     * @param name the name
     */
    public DBAssistantFunctionLoad(Browser browser, String name) {
        super(browser, name);
    }

    /**
     * Function.
     *
     * @param arguments the arguments
     * @return the object
     */
    @Override
    public Object function(Object[] arguments) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                DBAssistantWindow.loadData();
            }
        });
        return super.function(arguments);
    }
}
