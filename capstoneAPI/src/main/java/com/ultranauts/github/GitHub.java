package com.ultranauts.github;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Models a basic Github account which at the minimum requires
 * a {@link #userName} and {@link #userAPIKey}
 * @author Austin Bell
 * @version 1.0
 */
public class GitHub 
{
    /**
    * A username for a {@link com.ultranauts.github.GitHub GitHub} account 
    */
    protected String userName;

    /**
    * An apikey that is authenticated for a {@link com.ultranauts.github.GitHub GitHub} account. 
    * This should be matched for the appripriate username, and configured
    * for approriate permissions when testing Github's API
    */
    protected String userAPIKey;

    /** 
     * Constructor will pull user name and api key from 
     * the .env file located in the project directory and assign them to
     * {@link com.ultranauts.github.GitHub GitHub} instance's {@link #userName} and {@link #userAPIKey}
     * @param dotenv to get username and api key from the .env file
     */
    public GitHub(Dotenv dotenv){
        userName = dotenv.get("PERSONAL_USER_NAME");
        userAPIKey = dotenv.get("PERSONAL_API_KEY");
    }

    /** 
     * Constructor that can allow a tester to enter custom 
     * username and password if .env becomes unavailable for
     * the {@link com.ultranauts.github.GitHub GitHub} Object
     * @param name to set {@link #userName}
     * @param key to set {@link #userAPIKey}
     */
    public GitHub(String name, String key){
        userName = name;
        userAPIKey = key;
    }
    
    /** 
     * Returns the username asociated with the {@link com.ultranauts.github.GitHub GitHub} object 
     * @return the {@link #userName} associated with the {@link #GitHub} Object
     */
    public String getUserName() {
        return this.userName;
    }

    /** 
     * Sets a new username for the {@link com.ultranauts.github.GitHub GitHub} object
     * @param name to set {@link #userName}
     */
    public void setUserName(String name) {
        this.userName = name;
    }

    /** 
     * Returns the apikey asociated with the {@link com.ultranauts.github.GitHub GitHub}
     * object (ideally paired with the proper username)
     * @return the {@link #userAPIKey} associated with the {@link com.ultranauts.github.GitHub GitHub} Object
     */
    public String getUserAPIKey() {
        return this.userAPIKey;
    }  

    /** 
     * Sets a apikey string for the {@link com.ultranauts.github.GitHub GitHub} object 
     * (ideaiily paired with the proper username)
     * @param key to set {@link #userAPIKey}
     */
    public void setUserAPIKey(String key) {
        this.userAPIKey = key;
    }
}