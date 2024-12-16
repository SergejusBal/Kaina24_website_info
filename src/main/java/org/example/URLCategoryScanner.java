package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class URLCategoryScanner {

    WebDriver globalDriver;
    MySQLService mySQL;
    WebDriverWait wait;
    public URLCategoryScanner(){
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless=new");
//        options.addArguments("--disable-gpu");
//        options.addArguments("--no-sandbox");
//        options.addArguments("--disable-dev-shm-usage");
//        options.addArguments("--window-size=2560,1440");
        globalDriver = new ChromeDriver(options);
        globalDriver.manage().window().maximize();
        globalDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        globalDriver.get("https://www.kaina24.lt/");
        mySQL = new MySQLService();
        this.wait = new WebDriverWait(globalDriver, Duration.ofSeconds(20));
    }

    public void scanForCategories(){
        setCookies();
        globalDriver.navigate().to("https://www.kaina24.lt/");
        for(int i = 1; i <= 16; i++){
            navigateToMainCategory(i);
            navigateToMeniu();
        }
    }


    private void setCookies(){

        WebElement cookieSettings = wait.until(ExpectedConditions.elementToBeClickable(By.id("CybotCookiebotDialogNavDetails")));
        cookieSettings.click();
        WebElement cookieSettingsSubmit = globalDriver.findElement(By.id("CybotCookiebotDialogBodyButtonDecline"));
        cookieSettingsSubmit.click();
        sleep(1000);

    }

    private void navigateToMainCategory(int number) {

        int line = number/4 + 1;
        int column = number%4;
        if(column == 0) {
            column = 4;
            line--;
        }


        int cycle = 1;
        while(true) {
            try {
                WebElement categoryElement = globalDriver.findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div[2]/div[" + line + "]/div[" + column + "]/div/ul/li[" + cycle + "]/a"));
                if (categoryElement.getText().contains("Daugiau ...")){
                    scrollToElement(categoryElement);
                    categoryElement.click();
                }
                cycle++;
            }catch (NoSuchElementException e){
                break;
            }
        }
    }

    private void navigateToMeniu() {
        int count = 1;
        while (true) {
            try {
                globalDriver.findElement(By.xpath("/html/body/div/div[2]/div[2]/div/div[2]/div/ul/li[" + count + "]")).click();
                navigateToMeniu();
            } catch (NoSuchElementException e) {
                mySQL.registerCategoryURL(globalDriver.getCurrentUrl());
                globalDriver.navigate().back();
                break;
            }
            count++;
        }
    }

    private void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) globalDriver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
    }

    private void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



}
