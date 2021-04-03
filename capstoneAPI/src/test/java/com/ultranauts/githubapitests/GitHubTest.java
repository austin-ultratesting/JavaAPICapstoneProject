package com.ultranauts.githubapitests;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.ultranauts.github.GitHub;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.http.Header;

public class GitHubTest {

    private GitHub userAustin;
    private Dotenv dotenv;
    private String apiURL;

    private WebDriver driver;

    private String repositoryURL;
    private String screenshotFolder;

    private FirefoxOptions options;

    private void delay() throws InterruptedException{
        Thread.sleep(15000);
    }  

    private Header authorizedHeader(){
        String apiKey = userAustin.getUserAPIKey();

        Header authHeader = new Header("Authorization", "token " + apiKey);

        return authHeader;
    }

    private void saveWebSnapShot(WebDriver driver, String fileName) throws IOException{

        TakesScreenshot scrShot =((TakesScreenshot)driver);
        
        File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);

        File DestFile=new File(screenshotFolder + "/" + fileName);  

        FileUtils.copyFile(SrcFile, DestFile);
    }
    
    @BeforeTest
    public void init() throws IOException{

        userAustin = new GitHub();
        dotenv = Dotenv.load();
        apiURL = "https://api.github.com";
        screenshotFolder = "target/screenshots";
        repositoryURL = "https://github.com/" + userAustin.getUserName() + "?tab=repositories";
        FileUtils.cleanDirectory(new File(screenshotFolder));
    }

    @BeforeMethod
    public void initBrowser(){
        
        WebDriverManager.firefoxdriver().setup();

        options = new FirefoxOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
    }

    @Test (priority = 0)
    public void confirmPERSONALAPIKEY(){

        String key = dotenv.get("PERSONAL_API_KEY");

        System.out.println(key);

        Assert.assertEquals(userAustin.getUserAPIKey(), key);   
    }

    @Test (priority = 0)
    public void confirmPERSONALUSERNAME(){

        String name = dotenv.get("PERSONAL_USER_NAME");

        System.out.println(name);

        Assert.assertEquals(userAustin.getUserName(), name);   
    }

    @Test (priority = 0)
    public void getPersonalGithubAccount(){

        Header authHeader = authorizedHeader();

        given().
            header(authHeader).
        when().
            get(apiURL + "/user").
        then().
            statusCode(200).
            log().all(true);
    }

    @Test (priority = 1)
    public void createRepository() throws IOException, InterruptedException{
        driver = new FirefoxDriver(options);

        Header authHeader = authorizedHeader();

        Map<String,Object> bodyParameters = new HashMap<String,Object>();

        bodyParameters.put("name", "APIGeneratedRepoPublic");

        JSONObject newRepo = new JSONObject(bodyParameters);

        given().
            header(authHeader).
            body(newRepo.toJSONString()).
        when().
            post(apiURL + "/user/repos").
        then().
            statusCode(201).
            log().all(true);

        delay();

        //Maximize Window
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        driver.get(repositoryURL); 

        saveWebSnapShot(driver,"SCRN 001 - Created Repository.png");

        driver.quit();
    }

    @Test (priority = 2)
    public void replaceRepositoryTopics() throws IOException, InterruptedException{
        driver = new FirefoxDriver(options);

        Header authHeader = authorizedHeader();

        Header mediaType = new Header("Accept", "application/vnd.github.mercy-preview+json");

        Map<String,Object> bodyParameters = new HashMap<String,Object>();

        bodyParameters.put("names", Arrays.asList("sample","api","java","automation"));

        JSONObject newRepo = new JSONObject(bodyParameters);

        given().
            header(authHeader).
            header(mediaType).
            body(newRepo.toJSONString()).
        when().
            put(apiURL + "/repos/" + userAustin.getUserName() + "/APIGeneratedRepoPublic" + "/topics").
        then().
            statusCode(200).
            log().all(true);
        
        delay();

        //Maximize Window
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        
        driver.get(repositoryURL);

        saveWebSnapShot(driver,"SCRN 002 - Repository topics updated.png");

        driver.quit();
    }

    @Test (priority = 3)
    public void updateRepository() throws IOException, InterruptedException{
        driver = new FirefoxDriver(options);

        Header authHeader = authorizedHeader();

        Map<String,Object> bodyParameters = new HashMap<String,Object>();

        bodyParameters.put("description", "Patched description on my newly created repository");
        //bodyParameters.put("private", "true");

        JSONObject newRepo = new JSONObject(bodyParameters);

        given().
            header(authHeader).
            body(newRepo.toJSONString()).
        when().
            patch(apiURL + "/repos/" + userAustin.getUserName() + "/APIGeneratedRepoPublic").
        then().
            statusCode(200).
            log().all(true);

        delay();

        //Maximize Window
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        
        driver.get(repositoryURL);
    
        saveWebSnapShot(driver,"SCRN 003 - Repository set to private and updated description.png");

        driver.quit();
    }

    @Test (priority = 4)
    public void deleteRepository() throws IOException, InterruptedException{
        driver = new FirefoxDriver(options);

        Header authHeader = authorizedHeader();

        given().
            header(authHeader).
        when().
            delete(apiURL + "/repos/" + userAustin.getUserName() + "/APIGeneratedRepoPublic").
        then().
            statusCode(204).
            log().all(true);

        delay();

        //Maximize Window
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        
        driver.get(repositoryURL);
    
        saveWebSnapShot(driver,"SCRN 004 - Repository Deleted.png");

        driver.quit();
    }
}
