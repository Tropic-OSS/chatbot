package com.tropicoss.alfred.Database.domain;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "application_questions")
public class ApplicationQuestions extends BaseDaoEnabled {


    @DatabaseField(generatedId = true)
    private int questionID;

    @DatabaseField
    private int questionNumber;

    @DatabaseField
    private String questionText;
    @DatabaseField(defaultValue = "false")
    private boolean enabled;


    @DatabaseField
    private java.util.Date lastUpdatedAt;

    ApplicationQuestions(){}


}
