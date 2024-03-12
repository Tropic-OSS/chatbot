package com.tropicoss.alfred.Database.domain;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;


@DatabaseTable(tableName = "minecraft_server")
public class MinecraftServer extends BaseDaoEnabled {


    @DatabaseField(generatedId = true)
    private int minecraftServerID;


    @DatabaseField(unique = true, canBeNull = false)
    private String serverIP;


    @DatabaseField(unique = true)
    private String serverName;
    @DatabaseField
    private String authKey;


    @DatabaseField(canBeNull = false, defaultValue = "true" )
    private Boolean enabled;

    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private Date createdAt;


    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private Date lastUpdatedAt;


    public MinecraftServer() {

    }


    @Override
    public int create() throws SQLException {
        this.createdAt = Date.from(Instant.now());
        this.lastUpdatedAt = Date.from(Instant.now());
        ;
        return super.create();
    }

    @Override
    public int update() throws SQLException {
        this.lastUpdatedAt = Date.from(Instant.now());
        ;
        return super.update();
    }

    public int getMinecraftServerID() {
        return minecraftServerID;
    }

    public void setMinecraftServerID(int minecraftServerID) {
        this.minecraftServerID = minecraftServerID;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getLastUpdatedAt() {
        return lastUpdatedAt;
    }


}
