package com.tropicoss.alfred.config;

public class GenericConfig {
    public String name = "YourMinecraftServerName";

    public Mode mode = Mode.STANDALONE;

    public enum Mode {
        SERVER,
        CLIENT,
        STANDALONE
    }
}
