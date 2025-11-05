package com.philabid.ui;

public abstract class TableViewController implements RefreshableViewController {
    private boolean refreshTransactionActive = false;

    protected abstract void refreshTable();

    @Override
    public void refresh() {
        if (refreshTransactionActive) {
            return;
        }
        refreshTable();
    }

    protected void transactionalRefresh(Runnable runnable) {
        refreshTransactionActive = true;
        runnable.run();
        refreshTransactionActive = false;
        refresh();
    }
}
