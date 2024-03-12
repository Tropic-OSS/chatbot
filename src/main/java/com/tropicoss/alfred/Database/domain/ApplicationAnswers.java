package com.tropicoss.alfred.Database.domain;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;

@DatabaseTable(tableName = "app_user_answers")
public class ApplicationAnswers extends BaseDaoEnabled {

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "appID")
    private Application application;


    @DatabaseField
    private int questionNumber;

    @DatabaseField
    private String answerText;

    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private Date lastUpdatedAt;

     ApplicationAnswers(){

    }

    @Override
    public int create() throws SQLException {
        this.lastUpdatedAt = Date.from(Instant.now());
        return super.create();
    }

    @Override
    public int update() throws SQLException {
        this.lastUpdatedAt = Date.from(Instant.now());
        return super.update();
    }


}
