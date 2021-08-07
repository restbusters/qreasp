package com.restbusters.util.common;

/**
 * @author restbusters on 10/15/18
 * @project qreasp
 */

public enum Constant {

    GENERIC_RECORD_NAME_PATTERN("yyyy-MM-dd-HHmm"),
    V10_DATE_FORMAT_PATTERN("yyyy-MM-dd"),
    FOLDER_NAME_TIME_PATTERN("yyyyMMdd"),
    GENERIC_RECORD_FILE_NAME_PATTERN("-yyyy-MM-dd-HHmm.log"),
    DEPLOYMENT_FAILED("Version of deployed build has not been changed"),
    TEMPLATE_METADATA_REGEX("<#--.*\\$(.*)\\$.*-->"),
    SPLIT_TO_MAP_VALIDATOR_REGEX(".*=.*;.*=.*");

    private String constant;

    private Constant(String constant) {
        this.constant = constant;
    }
    @Override
    public String toString() {
        return this.constant;
    }
}
