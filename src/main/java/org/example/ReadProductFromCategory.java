package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ReadProductFromCategory implements Runnable{

    private WebDriver globalDriver;
    private WebDriverWait wait;
    private MySQLService mySQL;
    private boolean horizontalArrangement;
    private String categoryURL;
    public ReadProductFromCategory(String categoryURL){
        this.categoryURL = categoryURL;
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless=new");
//        options.addArguments("--disable-gpu");
//        options.addArguments("--no-sandbox");
//        options.addArguments("--disable-dev-shm-usage");
//        options.addArguments("--window-size=2560,1440");
        globalDriver = new ChromeDriver(options);
        globalDriver.manage().window().maximize();
        globalDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        globalDriver.get(categoryURL);
        this.wait = new WebDriverWait(globalDriver, Duration.ofSeconds(20));
        mySQL = new MySQLService();
        checkProductPageArrangement();
    }

    @Override
    public void run() {
        setCookies();
        globalDriver.navigate().to(categoryURL); // need to load again so page div will be set properly
        navigateTruProductPage();
    }

    private void navigateTruProductPage(){
        int counter = 1;

        while(true){
            int productNumber = counter % 20;
            int numberOfShops;

            if(productNumber == 0) productNumber = 20;
            try {
                numberOfShops = openProductPage(productNumber);

                if(numberOfShops == 0) writeWhenPageRedirectedToShop(productNumber);
                else findStartingPointAndWrite(numberOfShops);

            } catch (NoSuchElementException | TimeoutException  e){
                closeChromeDriver();
                break;
            }

            if(productNumber == 20) globalDriver.navigate().to(categoryURL + "?page=" + (counter / 20 +1));
            else if(numberOfShops != 0){
                globalDriver.navigate().back();
                sleep(1000);
            }
            counter++;
        }

    }

    private int openProductPage(int pageNumber) throws NoSuchElementException, TimeoutException  {
      //  System.out.println(pageNumber);
        sleep(1000);

        WebElement productElement = null;
        WebElement productAmount = null;

       if(horizontalArrangement){
           productElement  = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div[2]/div[2]/div[2]/div[3]/div[" + pageNumber + "]/div/p/a")));
           productAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div[2]/div[2]/div[2]/div[3]/div["+ pageNumber +"]/div/div[2]/div/p/a")));

       }else{
           productElement  = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div[2]/div[2]/div[2]/div[4]/div[" + pageNumber + "]/div[2]/h2/a")));
           productAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div[2]/div[2]/div[2]/div[4]/div[" + pageNumber +"]/div[3]/p[2]/a")));
       }

        scrollToElement(productElement);

        int numberOfShops = getNumberFromString(productAmount.getText());
        if(numberOfShops != 0)  productElement.click();

        return numberOfShops;
    }

    private void writeWhenPageRedirectedToShop(int productNumber){
        try {
            Product product = new Product();
            WebElement webElement = globalDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[2]/div[3]/div[" + productNumber + "]/div/a[2]/img"));
            String imageURL = webElement.getDomAttribute("src");
            product.setImage_Url(imageURL);

            String name = globalDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[2]/div[3]/div[" + productNumber + "]/div/p/a")).getText();
            String price = globalDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[2]/div[3]/div[" + productNumber + "]/div/div[2]/div/div/p/a")).getText();
            String shopName = globalDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[2]/div[3]/div[" + productNumber + "]/div/div[2]/div/p/a/img")).getDomAttribute("alt");
            product.setName(name);
            product.setPrice(getDoubleFromString(price));
            product.setShop(shopName);
            mySQL.registerProduct(product);
        }catch (NoSuchElementException e){
            return;
        }
    }

    private void findStartingPointAndWrite(int numberOfShops){
        int startingNumber = findStartingPoint();
        writeProducts(startingNumber,numberOfShops);
        //System.out.println(globalDriver.getCurrentUrl());
    }

    private int findStartingPoint() {
        int startingNumber = 1;
        while (true) {
            try {
                String tempText = globalDriver.findElement(By.xpath("/html/body/div/div[2]/div[2]/div[1]/div[2]/div[2]/div/div/div[" + startingNumber+ "]")).getText();
                startingNumber++;
                if (tempText.equals("Pardavėjai pagal mažiausią kainą")) return startingNumber;

            } catch (NoSuchElementException e) {
                return 1;
            }
        }
    }

    private void writeProducts(int startingPoint, int numberOfShops){
        try {
            Product product = new Product();
            WebElement webElement = globalDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[1]/div[2]/div[1]/div[2]/a/img"));
            String imageURL = webElement.getDomAttribute("src");
            product.setImage_Url(imageURL);

            while (numberOfShops != 0) {
                String name = globalDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[2]/div[2]/div/div/div[" + startingPoint + "]/table/tbody/tr/td[3]/h3/a")).getText();
                String price = globalDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[2]/div[2]/div/div/div[" + startingPoint + "]/table/tbody/tr[1]/td[5]/a/div/span[1]")).getText();
                String shopName = globalDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div[1]/div[2]/div[2]/div/div/div[" + startingPoint + "]/table/tbody/tr[1]/td[6]/a[2]/span")).getText();
                product.setName(name);
                product.setPrice(Double.parseDouble(price));
                product.setShop(shopName);
                mySQL.registerProduct(product);
                numberOfShops--;
                startingPoint++;
            }
        } catch (NoSuchElementException e){
            return;
        }

    }


    private void checkProductPageArrangement(){
        WebElement webElement;
        try {
             webElement = wait.until(ExpectedConditions.elementToBeClickable((By.xpath("/html/body/div[2]/div[2]/div[2]/div[2]/div[3]"))));
        }catch (NoSuchElementException | TimeoutException e){
            return;
        }
        if(webElement.getDomAttribute("class").contains("horisontal")) horizontalArrangement = true;
        else horizontalArrangement = false;

    }

    private void closeChromeDriver(){
        if (globalDriver != null) {
            globalDriver.quit();
        }
    }

    private void setCookies(){

        WebElement cookieSettings = wait.until(ExpectedConditions.elementToBeClickable(By.id("CybotCookiebotDialogNavDetails")));
        cookieSettings.click();
        WebElement cookieSettingsSubmit = globalDriver.findElement(By.id("CybotCookiebotDialogBodyButtonDecline"));
        cookieSettingsSubmit.click();
        sleep(1000);

    }

    private void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) globalDriver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
    }

    private int getNumberFromString(String input) {
        String number = input.replaceAll("[^\\d]", "");
        return number.isEmpty() ? 0 : Integer.parseInt(number);
    }

    public double getDoubleFromString(String input) {
        String number = input.replaceAll("[^\\d.]", "");
        return number.isEmpty() ? 0.0 : Double.parseDouble(number);
    }

    private void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
