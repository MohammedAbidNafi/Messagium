package com.margsapp.messenger.Model;

public class AppVersion {

    private String appversion;
    public AppVersion(String appversion){
        this.appversion = appversion;
    }

    public AppVersion(){}

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }
}
