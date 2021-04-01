package com.ultranauts.github;

import io.github.cdimascio.dotenv.Dotenv;

public class GitHubRun{

    public static void main(String[] args) {
    
    GitHub userAustin = new GitHub();

    Dotenv dotenv = Dotenv.load();

    String key = dotenv.get("PERSONAL_API_KEY");

    System.out.println(key);

    System.out.println(userAustin.getUserAPIKey());
    }
    
}