package com.restbusters.util.common;

/**
 * @author restbusters on 10/15/18
 * @project qreasp
 */

public enum Constants {

    GENERIC_RECORD_NAME_PATTERN("yyyy-MM-dd-HHmm"),
    V10_DATE_FORMAT_PATTERN("yyyy-MM-dd"),
    FOLDER_NAME_TIME_PATTERN("yyyyMMdd"),
    GENERIC_RECORD_FILE_NAME_PATTERN("-yyyy-MM-dd-HHmm.log"),
    DEPLOYMENT_FAILED("Version of deployed build has not been changed");

    private String constants;

    private Constants(String cons) {
        this.constants = cons;
    }
    @Override
    public String toString() {
        return constants;
    }
}
