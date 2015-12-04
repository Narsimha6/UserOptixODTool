package com.useroptix.odtool.to;

public enum UserViewEnum {
	LOGIN("LOGIN"),
	ENDPOINT_SELECTION("ENDPOINT_SELECTION"),
	ENDPOINT_AUTHENTICATION("ENDPOINT_AUTHENTICATION"),
	TEAMPLATE_SELECTION("TEAMPLATE_SELECTION"),
	FINISH("FINISH"),
	CONFIGURATION_COMPLETE("CONFIGURATION_COMPLETE"),
	DO_ANOTHER("DO_ANOTHER"),
	SAVE_PTP_DATA("SAVE_PTP_DATA")
	;

    private final String value;

    private UserViewEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
