package com.philabid.ui;

public abstract class TableViewController implements RefreshableViewController {
    protected abstract void refreshTable();

    @Override
    public void refresh() {
        refreshTable();
    }
}
