package com.useroptix.odtool.to;


public enum EPIconsEnum {
	MYSQL("mysql","mysql.jpg"),
	NETSUITE("netsuite","netsuite.jpg"),
	ORACELERP("oracelerp","oracle.jpg"),
	ORACLECRM("oraclecrm","oracle.jpg"),
	ORACLEDB("oracledb","oracle.jpg"),
	SALESFORCE("salesforce","salesforce.jpg"),
	SAP("sap","sap.jpg"),
	TBE("tbe","salesforce.jpg"),
	TEE("tee","salesforce.jpg"),
	WORKDAY("workday","workday.jpg")
	;

    private final String value;
    private final String imagePath;

    private EPIconsEnum(String value, String imagePath) {
        this.value = value;
        this.imagePath = imagePath;
    }

    public String getValue() {
        return value;
    }
    
    public String getImagePath() {
    	return imagePath;
    }
    
}
