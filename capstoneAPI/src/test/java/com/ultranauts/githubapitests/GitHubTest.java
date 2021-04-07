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

/**
 * Written class to demonstrate accessing a publically written API.
 * A personal account was used to interact with Github's API, and can
 * be reconfigured to use another personal via the .env file.  
 * @author Austin Bell
 * @version 1.0
 */
public class GitHubTest {

    /**
    * An instance of the {@link com.ultranauts.github.GitHub GitHub} class to
    * model a personal user account on GitHub. An apikey must be generated for
    * the user account in order to use the test class GitHubTest
    */
    private GitHub personalGithub;
    
    /**
    * The {@link io.github.cdimascio.dotenv.Dotenv Dotenv} class is used to store 
    * all the information of the personal Github account. The {@link com.ultranauts.github.GitHub#GitHub(Dotenv dotenv) GitHub(Dotenv dotenv)} 
    * constructor for {@link com.ultranauts.github.GitHub GitHub} will be configured
    * with the {@link io.github.cdimascio.dotenv.Dotenv Dotenv} class
    */
    private Dotenv dotenv;

    /**
    * The root URL which all the api calls make as documented in the API documentation 
    */
    private String apiURL;

    /**
    * A Selenium based webdriver used to view the webpage after API calls are made when
    * performing actions that change the state of the user account 
    */
    private WebDriver driver;

    /**
    * The webpage which will be used to grab a screenshot and confirm the API call was 
    * successfully able to change the front end experience
    */
    private String repositoryURL;

    /**
     * 
     */
    private String repositoryName;
    
    /**
    * A relative directory path that saves screenshots of the webpage after API calls
    * are made. This can be configured as needed
    */
    private String screenshotFolder;

    /**
    * Firefox Options set up for the driver to know which browser to use. This can
    * be changed to the appropriate browser if Firefox is not available.
    */
    private FirefoxOptions options;

    
    /** 
     * A method intentially designed to delay 15 seconds so after an API call is made,
     * the methods executed later on can grab the appropriate state of the webpage
     * and get the correct screenshot.
     * <p>
     * The test class navigated to webpages too quickly after the API call was made,
     * which resulted in incorrect screen grabs.
     * @throws InterruptedException
     */
    private void delay() throws InterruptedException{
        Thread.sleep(15000);
    }  

    
    /** 
     * Generates an Header used in the API call formated "Authorization: token APIKEY", where the
     * api key of the Github account is the token value
     * @return A {@link io.restassured.http.Header Header} that authenticates the API call being made in the tests
     */
    private Header authorizedHeader(){
        String apiKey = personalGithub.getUserAPIKey();

        Header authHeader = new Header("Authorization", "token " + apiKey);

        return authHeader;
    }

    
    /** 
     * Casts driver into an instance of {#link org.openqa.selenium.TakesScreenshot TakesScreenshot} amd
     * stores it in the designated directory for screenshots captured in the test class
     * @param driver that can capture a screenshot
     * @param fileName for the screenshot
     * @throws IOException
     */
    private void saveWebSnapShot(WebDriver driver, String fileName) throws IOException{

        TakesScreenshot scrShot =((TakesScreenshot)driver);
        
        File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);

        File DestFile=new File(screenshotFolder + "/" + fileName);  

        FileUtils.copyFile(SrcFile, DestFile);
    }
    
    /** 
     * Initializes all the basic member fields of the test class
     * <p>
     * dotenv - loads the .env file
     * <p>
     * personalGithub - loads username and api key from dotenv
     * <p>
     * apiurl - definied in Github's API documentation "https://api.github.com"
     * <p>
     * screenshotFolder - under root directory of project, "resources/screenshots"
     * <p>
     * repositoryURL - [Github URL]/{User Name}?tab=repositories
     * <p>
     * repositoryName - Can be modified as needed for testing purposes
     * @throws IOException
     */
    @BeforeTest
    public void init() throws IOException{

        dotenv = Dotenv.configure().load();
        personalGithub = new GitHub(dotenv);
        apiURL = "https://api.github.com";
        screenshotFolder = "resources/screenshots";
        repositoryURL = "https://github.com/" + personalGithub.getUserName() + "?tab=repositories";
        repositoryName = "APIGeneratedRepoPublic";
    }
    
    /** 
     * Initializes the basic configurations. The test methods that 
     * initialize the WebDriver will actually open the web browser
     * and not with this function call
    */
    @BeforeMethod
    public void initBrowser(){
        
        WebDriverManager.firefoxdriver().setup();

        options = new FirefoxOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
    }

    /**
     * Test mothod to confirm that the api key is correctly accessed
     * from the .env file (mostly to verify how Dotenv worked)
     */
    @Test (priority = 0)
    public void confirmPERSONALAPIKEY(){

        String key = dotenv.get("PERSONAL_API_KEY");

        System.out.println(key);

        Assert.assertEquals(personalGithub.getUserAPIKey(), key);   
    }

    /**
     * Test mothod to confirm that the username is correctly accessed
     * from the .env file (mostly to verify how Dotenv worked)
     */
    @Test (priority = 0)
    public void confirmPERSONALUSERNAME(){

        String name = dotenv.get("PERSONAL_USER_NAME");

        System.out.println(name);

        Assert.assertEquals(personalGithub.getUserName(), name);   
    }

    /** 
     * Authorized API call made to verify that the Github account's data is returned.
     * <p>
     * A basic example of GET that should generate status code 200
    */
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

    /** 
     * Authorized API call made to verify that a user is able to make a repository
     * <p>
     * A basic example of POST that should generate status code 201
     * @throws IOException
     * @throws InterruptedException
     */
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

    /** 
     * Authorized API call made to verify that a user's repository 
     * can have the topic fields updated
     * <p>
     * A basic example of PUT that should generate status code 200
     * @throws IOException
     * @throws InterruptedException
     */
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
            put(apiURL + "/repos/" + personalGithub.getUserName() + "/" + repositoryName + "/topics").
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

    /** 
     * Authorized API call made to verify that a user's repository
     * can have the description modified
     * <p>
     * A basic example of PATCH that should generate status code 200
     * @throws IOException
     * @throws InterruptedException
     */
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
            patch(apiURL + "/repos/" + personalGithub.getUserName() + "/" + repositoryName).
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

    /** 
     * Authorized API call made to verify that a user's repository
     * can be deleted
     * <p>
     * A basic example of DELETE that should generate status code 204
     * @throws IOException
     * @throws InterruptedException
     */
    @Test (priority = 4)
    public void deleteRepository() throws IOException, InterruptedException{
        driver = new FirefoxDriver(options);

        Header authHeader = authorizedHeader();

        given().
            header(authHeader).
        when().
            delete(apiURL + "/repos/" + personalGithub.getUserName() + "/" + repositoryName).
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
