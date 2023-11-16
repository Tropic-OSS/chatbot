package com.tropicoss.minecraft.alfred.config;

public class WebSocketConfig {
    public boolean enabled = false;
    public Type type = Type.SERVER;
    public String host = "localhost";
    public int port = 9090;

    public enum Type {
        SERVER,
        CLIENT
    }
}
