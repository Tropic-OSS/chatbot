package com.tropicoss.alfred.clii;

import com.tropicoss.alfred.Database.DatabaseManager;
import com.tropicoss.alfred.Database.domain.MinecraftServer;

import static com.tropicoss.alfred.Database.DatabaseManager.connectionSource;

public class AlfredCLIMain {



    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");


        // create an instance of Account
        DatabaseManager.Init();
        //        Dao<MinecraftServer, String> serverDao = DaoManager.lookupDao(connectionSource, MinecraftServer.class);
        MinecraftServer testServer = DatabaseManager.createObjectInstance(MinecraftServer.class);
        MinecraftServer testServer2 = DatabaseManager.createObjectInstance(MinecraftServer.class);


        testServer.setServerIP("testIP");
        testServer.setServerName("testName");
        testServer.create();
        testServer.setServerIP("ip part two");
        testServer.update();


        System.out.println(testServer2.getLastUpdatedAt());


        // close the connection source
        connectionSource.close();


    }

}
