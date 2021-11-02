/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.SourceCode;
import com.huawei.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.createfunction.CreateFunctionMainDlg;
import com.huawei.mppdbide.view.createfunction.DsCreateFunctionRelyInfo;
import com.huawei.mppdbide.view.search.SearchWindow;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 *
 * Title: class
 *
 * Description: The Class CreateFunction.
 *
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CreateFunctionBase {
    @Inject
    private ECommandService commandService;

    @Inject
    private EHandlerService handlerService;

    /**
     * Execute
     *
     * @param String the language
     */
    public void baseExecute(String language) {
        /*
         * The object will never be null. In case of null, the Menu item will be
         * disabled automatically.
         */

        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();

        ObjectGroup debugObjectGroup = (ObjectGroup) obj;
        INamespace namespace = null;

        if (debugObjectGroup != null) {
            namespace = debugObjectGroup.getNamespace();
            if (namespace == null) {
                return;
            }
            CreateFunctionMainDlg dlg = new CreateFunctionMainDlg(new Shell(), SWT.NONE);
            DsCreateFunctionRelyInfo relyInfo = new DsCreateFunctionRelyInfo(debugObjectGroup);
            relyInfo.setSchameName(namespace.getName());
            dlg.setRelyInfo(relyInfo);
            dlg.setInitLanguage(language);
            if (dlg.open() != 0) {
                return;
            }
            IDebugObject object = null;
            SourceCode srcCode = new SourceCode();
            if (debugObjectGroup instanceof OLAPObjectGroup) {
                final Database db = namespace.getDatabase();
                object = getDebugObject(db);
                srcCode.setCode(relyInfo.getSourceCode());
            }
            if (object == null) {
                return;
            }

            object.setNamespace(namespace);

            object.setSourceCode(srcCode);

            PLSourceEditor plSourceEditor = UIElement.getInstance().createEditor(object);
            if (plSourceEditor != null) {
                plSourceEditor.displaySourceForDebugObject(object);
                plSourceEditor.registerModifyListener();
                if (relyInfo.getAutoCompile() && !"".equals(srcCode.getCode())) {
                    Command command = commandService.getCommand(
                            "com.huawei.mppdbide.command.id.executeobjectbrowseritemfromtoolbar"
                            );
                    ParameterizedCommand pCommand = new ParameterizedCommand(command, null);
                    handlerService.executeHandler(pCommand);
                }
            }
        }
    }

    /**
     * Gets the debug object.
     *
     * @param db the db
     * @return the debug object
     */
    protected DebugObjects getDebugObject(final Database db) {
        return new DebugObjects(0, "NewObject", OBJECTTYPE.PLSQLFUNCTION, db);
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    protected boolean baseCanExecute() {
        Object object = UIElement.getInstance().getActivePartObject();
        if (object instanceof SearchWindow) {
            return false;
        }
        ObjectGroup<?> obj = (ObjectGroup<?>) IHandlerUtilities.getObjectBrowserSelectedObject();
        return (obj instanceof DebugObjectGroup) && (OBJECTTYPE.FUNCTION_GROUP == obj.getObjectGroupType());
    }

}