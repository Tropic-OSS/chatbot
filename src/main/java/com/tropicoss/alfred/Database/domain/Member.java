package com.tropicoss.alfred.Database.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "member")
public class Member {


    @DatabaseField
    private String minecraftUUID;

    @DatabaseField
    private String discordUUID;





    @DatabaseField
    private java.util.Date createdAt;
    @DatabaseField
    private java.util.Date lastUpdatedAt;



}
