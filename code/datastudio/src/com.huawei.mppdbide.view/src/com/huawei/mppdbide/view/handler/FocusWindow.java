/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import com.huawei.mppdbide.view.ui.ObjectBrowser;

/**
 * 
 * Title: class
 * 
 * Description: The Class FocusWindow.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class FocusWindow {
    @Inject
    private EPartService partService;

    /**
     * Execute.
     *
     * @param windowName the window name
     */
    @Execute
    public void execute(
            @Optional @Named("com.huawei.mppdbide.view.commandparameter.focuswindow.partid") String windowName) {
        MPart currPart = null;
        switch (windowName) {
            case "ObjectBrowser": {
                currPart = partService.findPart("com.huawei.mppdbide.part.id.objectbrowser");
                if (currPart != null) {
                    Object objBrwserObj = currPart.getObject();
                    if (objBrwserObj instanceof ObjectBrowser) {
                        ((ObjectBrowser) objBrwserObj).onFocus();
                    }
                }
                break;
            }
            case "GlobalConsole": {
                currPart = partService.findPart("com.huawei.mppdbide.part.id.console");
                selectPart(currPart);
                break;
            }
            case "EditTable": {
                currPart = partService.findPart("com.huawei.mppdbide.part.id.editresultdata");
                // As per testability requirement, modified focus edit table
                // data to close edit table data.
                if (partService.savePart(currPart, true)) {
                    partService.hidePart(currPart);
                }
                break;
            }
            default: {
                break;
            }
        }

    }

    /**
     * Select part.
     *
     * @param currPart the curr part
     */
    private void selectPart(MPart currPart) {
        if (null != currPart) {
            MElementContainer<MUIElement> partstackcontainer = currPart.getParent();

            if (null != partstackcontainer) {
                partstackcontainer.setSelectedElement(currPart);
            }
        }

    }

}
