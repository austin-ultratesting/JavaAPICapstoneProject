package com.ultranauts.github;

import io.github.cdimascio.dotenv.Dotenv;

public class GitHub 
{
    private String userName;
    private String userAPIKey;

    public GitHub(){
        Dotenv dotenv = Dotenv.configure()
                              .load();
        
        userName = dotenv.get("PERSONAL_USER_NAME");
        userAPIKey = dotenv.get("PERSONAL_API_KEY");
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public GitHub(String key){
        userAPIKey = key;
    }

    public String getUserAPIKey() {
        return this.userAPIKey;
    }

    public void setUserAPIKey(String userAPIKey) {
        this.userAPIKey = userAPIKey;
    }

    
}
