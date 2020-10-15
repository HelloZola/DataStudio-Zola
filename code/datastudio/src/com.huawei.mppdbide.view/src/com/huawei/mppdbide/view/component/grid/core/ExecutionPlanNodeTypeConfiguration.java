/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;
import org.eclipse.swt.graphics.Color;

import com.huawei.mppdbide.explainplan.ui.model.TreeGridColumnHeader;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecutionPlanNodeTypeConfiguration.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExecutionPlanNodeTypeConfiguration extends AbstractRegistryConfiguration {
    private IDSGridDataProvider dataProvider;
    private ICellPainter cellPaintr;

    /**
     * Pre destroy.
     */
    public void preDestroy() {
        this.dataProvider = null;
        this.cellPaintr = null;
    }

    /**
     * Configure registry.
     *
     * @param configRegistry the config registry
     */
    @Override
    public void configureRegistry(IConfigRegistry configRegistry) {
        if (null == getDataProvider()) {
            return;
        }

        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
                new BackgroundPainter(this.cellPaintr), DisplayMode.NORMAL, TreeLayer.TREE_COLUMN_CELL);

        IStyle costlyCellStyle = new Style();
        costlyCellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, new Color(null, 255, 200, 100));
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, costlyCellStyle, DisplayMode.NORMAL,
                TreeGridColumnHeader.COLUMN_LABEL_HEAVIEST);
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, costlyCellStyle, DisplayMode.NORMAL,
                TreeGridColumnHeader.COLUMN_LABEL_SLOWEST);
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, costlyCellStyle, DisplayMode.NORMAL,
                TreeGridColumnHeader.COLUMN_LABEL_COSTLIEST);
    }

    /**
     * Instantiates a new execution plan node type configuration.
     *
     * @param dataProvider the data provider
     */
    public ExecutionPlanNodeTypeConfiguration(IDSGridDataProvider dataProvider) {
        this.dataProvider = dataProvider;
        this.cellPaintr = new ExplainPlanTreeTablePainterWrapper();
    }

    /**
     * Gets the data provider.
     *
     * @return the data provider
     */
    public IDSGridDataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * Sets the data provider.
     *
     * @param dataProvider the new data provider
     */
    public void setDataProvider(IDSGridDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

}
