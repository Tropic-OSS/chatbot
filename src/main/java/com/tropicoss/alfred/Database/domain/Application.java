package com.tropicoss.alfred.Database.domain;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

@DatabaseTable(tableName = "applications")
public class Application extends BaseDaoEnabled {


    @DatabaseField(generatedId = true)
    private int appID;

    @DatabaseField
    private String discordUUID;

    @DatabaseField
    private String applicantID;

    @DatabaseField
    private String applicationStatus;


    @DatabaseField
    private String adminUUID;
    @DatabaseField
    private String adminResponse;


    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private java.util.Date createdAt;


    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private java.util.Date lastUpdatedAt;


    @ForeignCollectionField(eager = true)
    private ForeignCollection<ApplicationAnswers> applicationAnswers;


    public Application() {
        //Empty for ORMLite
    }


    @Override
    public int create() throws SQLException {
        this.createdAt = Date.from(Instant.now());
        this.lastUpdatedAt = Date.from(Instant.now());
        return super.create();
    }

    @Override
    public int update() throws SQLException {
        this.lastUpdatedAt = Date.from(Instant.now());
        return super.update();
    }


}
