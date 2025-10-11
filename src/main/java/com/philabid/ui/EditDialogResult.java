package com.philabid.ui;

public record EditDialogResult(boolean saved, boolean editNext) {

    public static class Builder {
        private boolean saved = false;
        private boolean editNext = false;

        public Builder saved(boolean saved) {
            this.saved = saved;
            return this;
        }

        public Builder editNext(boolean editNext) {
            this.editNext = editNext;
            return this;
        }

        public EditDialogResult build() {
            return new EditDialogResult(saved, editNext);
        }
    }
}
