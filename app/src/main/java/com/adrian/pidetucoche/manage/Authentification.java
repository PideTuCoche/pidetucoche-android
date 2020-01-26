package com.adrian.pidetucoche.manage;

public class Authentification {

    static Authentification authentification;

    public static Authentification getInstance(){
        if (authentification == null) authentification = new Authentification();
        return authentification;
    }


}
