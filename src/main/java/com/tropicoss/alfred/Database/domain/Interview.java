package com.tropicoss.alfred.Database.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "interview")
public class Interview {

    @DatabaseField
    // Reference ApplicationID
    private String applicationID;

    @DatabaseField
    private String status;

    @DatabaseField
    private java.util.Date createdAt;
    @DatabaseField
    private java.util.Date lastUpdatedAt;


    public Interview() {
    }
}
