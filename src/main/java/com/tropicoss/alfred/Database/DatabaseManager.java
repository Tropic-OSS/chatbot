package com.tropicoss.alfred.Database;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.tropicoss.alfred.Database.domain.Application;
import com.tropicoss.alfred.Database.domain.ApplicationAnswers;
import com.tropicoss.alfred.Database.domain.ApplicationQuestions;
import com.tropicoss.alfred.Database.domain.MinecraftServer;

import java.sql.SQLException;

public class DatabaseManager {

    private static final String databaseUrl = "jdbc:sqlite:test.db";
    public static ConnectionSource connectionSource;


    public static void Init() {

        try {
            connectionSource = new JdbcConnectionSource(databaseUrl);

            //For now, recreate tables until we are happy with the state
            TableUtils.dropTable(connectionSource, MinecraftServer.class, true);
            TableUtils.dropTable(connectionSource, Application.class, true);
            TableUtils.dropTable(connectionSource, ApplicationAnswers.class, true);
            TableUtils.dropTable(connectionSource, ApplicationQuestions.class, true);

            TableUtils.createTableIfNotExists(connectionSource, MinecraftServer.class);
            TableUtils.createTableIfNotExists(connectionSource, Application.class);
            TableUtils.createTableIfNotExists(connectionSource, ApplicationAnswers.class);
            TableUtils.createTableIfNotExists(connectionSource, ApplicationQuestions.class);

//            DaoManager.createDao(connectionSource, MinecraftServer.class);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseDaoEnabled> T createObjectInstance(Class T) {

        try {
            return (T) DaoManager.lookupDao(connectionSource, T).createObjectInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }




}
