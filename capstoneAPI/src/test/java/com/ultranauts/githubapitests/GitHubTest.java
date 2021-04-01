package com.ultranauts.githubapitests;

import com.ultranauts.github.GitHub;

import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.http.Header;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

import java.util.HashMap;
import java.util.Map;


public class GitHubTest {

    GitHub userAustin;
    Dotenv dotenv;
    String apiURL;

    private Header authorizedHeader(){
        String apiKey = userAustin.getUserAPIKey();

        Header authHeader = new Header("Authorization", "token " + apiKey);

        return authHeader;
    }
    
    @BeforeTest
    public void init(){

        userAustin = new GitHub();
        dotenv = Dotenv.load();
        apiURL = "https://api.github.com";
    }

    @Test
    public void confirmPERSONALAPIKEY(){

        String key = dotenv.get("PERSONAL_API_KEY");

        System.out.println(key);

        Assert.assertEquals(userAustin.getUserAPIKey(), key);   
    }

    @Test
    public void confirmPERSONALUSERNAME(){

        String name = dotenv.get("PERSONAL_USER_NAME");

        System.out.println(name);

        Assert.assertEquals(userAustin.getUserName(), name);   
    }

    @Test
    public void getPersonalGithubAccount(){

        Header authHeader = authorizedHeader();

        Response austinAccount = given().
                                    header(authHeader).
                                 when().
                                    get(apiURL + "/user");

        System.out.println(austinAccount.asPrettyString());

        austinAccount.then().
                        statusCode(200);
    }

    @Test
    public void createRepository(){

        Header authHeader = authorizedHeader();

        Map<String,Object> bodyParameters = new HashMap<String,Object>();

        bodyParameters.put("name", "APIGeneratedRepoPublic");

        JSONObject newRepo = new JSONObject(bodyParameters);


        Response createdRepoForUser = given().
                                        header(authHeader).
                                        body(newRepo.toJSONString()).
                                      when().
                                        post(apiURL + "/user/repos");
            
        createdRepoForUser.then().
                                statusCode(201);            
    }

    @Test
    public void deleteRepository(){

        Header authHeader = authorizedHeader();

        Response createdRepoForUser = given().
                                        header(authHeader).
                                      when().
                                        delete(apiURL + "/repos/" + userAustin.getUserName() + "/APIGeneratedRepoPublic");
            
        createdRepoForUser.then().
                                statusCode(204);            
    }
}
