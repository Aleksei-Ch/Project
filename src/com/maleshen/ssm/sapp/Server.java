package com.maleshen.ssm.sapp;


import com.maleshen.ssm.sapp.model.ServerConfigurator;

public class Server {
    public static void main(String[] args) throws Exception {
        System.err.println("SSM Server starting...");
        ServerConfigurator.start();
    }
}
