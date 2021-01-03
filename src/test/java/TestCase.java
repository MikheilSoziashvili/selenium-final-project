import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Random;

public class TestCase {
    WebDriver driver;
    String totalPrice;

    @BeforeClass
    @Parameters("browser")
    public void setUp(String browser) {
        if (browser.equals("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if (browser.equals("edge")) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
        }
    }

    // Going to Women --> T-shirts
    @Test(priority = 1)
    public void testCase1() {
        driver.get("http://automationpractice.com/index.php");
        driver.manage().window().maximize();
        Actions action = new Actions(driver);
        WebDriverWait wait = new WebDriverWait(driver, 3);
        WebElement womenButton = driver.findElement(By.xpath("//div[@id='block_top_menu']//a[@title='Women']"));
        action.moveToElement(womenButton).perform();

        WebElement tShirtButton = driver.findElement(By.xpath("//div[@id='block_top_menu']//a[@title='T-shirts' and text()='T-shirts']"));
        wait.until(ExpectedConditions.visibilityOf(tShirtButton));
        try {
            tShirtButton.click();
        } catch (NoSuchElementException e) {
            System.out.println("no such element");
        }
    }

    // Opening Quick View Frame
    @Test(priority = 2)
    public void testCase2() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions action = new Actions(driver);
        WebDriverWait wait = new WebDriverWait(driver, 5);

        js.executeScript("arguments[0].scrollIntoView()", driver.findElement(By.xpath("//ul[starts-with(@class, 'product_list')]")));
        action.moveToElement(driver.findElement(By.cssSelector("div.product-container"))).perform();

        WebElement quickViewButton = driver.findElement(By.className("quick-view"));
        wait.until(ExpectedConditions.visibilityOf(quickViewButton));
        quickViewButton.click();

        try {
            WebElement frame1 = driver.findElement(By.cssSelector("iframe[id^='fancybox-frame']"));
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame1));
        } catch (NoSuchElementException e) {
            System.out.println("No such element");
        }
    }

    // Hovering On Every Available Photo in Quick View Frame
    @Test(priority = 3)
    public void testCase3() {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        Actions action = new Actions(driver);

        List<WebElement> imageThumbnails = driver.findElements(By.xpath("//ul[@id='thumbs_list_frame']/child::li"));

        for (int i = 1; i <= imageThumbnails.size(); i++) {
            String startOfXPATH = "//ul[@id='thumbs_list_frame']/li[@id='thumbnail_";
            String XPATH = startOfXPATH + i + "']";
            WebElement thumbnail = driver.findElement(By.xpath(XPATH));
            WebElement bigImage = driver.findElement(By.id("bigpic"));
            String temp = "p/" + i + "/" + i;

            action.moveToElement(thumbnail).perform();
            wait.until(ExpectedConditions.attributeContains(bigImage, "src", temp));

            if (!bigImage.getAttribute("src").contains(temp)) {
                System.out.println("not changed");
            }
        }

    }

    // Adding To Cart And Clicking Continue Shopping
    @Test(priority = 4)
    public void testCase4() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, 4);

        WebElement quantityValue = driver.findElement(By.xpath("//p[@id='quantity_wanted_p']/input[@id='quantity_wanted']"));
        Select sel = new Select(driver.findElement(By.id("group_1")));

        if (!sel.getFirstSelectedOption().getAttribute("title").equals("M")) {
            sel.selectByValue("2");
        }

        quantityValue.clear();
        quantityValue.sendKeys("2");
        driver.findElement(By.xpath("//button/child::span[contains(text(),'Add to cart')]")).click();

        driver.switchTo().defaultContent();
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("layer_cart"))));

        try {
            WebElement continueShopping = driver.findElement(By.xpath("//div[@id='layer_cart']//span[@title='Continue shopping']"));
            wait.until(ExpectedConditions.visibilityOf(continueShopping));
            js.executeScript("arguments[0].click()", continueShopping);
        } catch (ElementNotVisibleException e) {
            System.out.println("Not Visible");
        }
    }

    // Adding Returned Element to Cart and Clicking Continue Shopping
    @Test(priority = 5)
    public void testCase5() {
        WebDriverWait wait = new WebDriverWait(driver, 3);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions action = new Actions(driver);

        WebElement mainMenuBlock = driver.findElement(By.id("header_logo"));
        js.executeScript("window.scrollTo(document.body.scrollHeight,0)");
        wait.until(ExpectedConditions.visibilityOf(mainMenuBlock));
        mainMenuBlock.click();

        List<WebElement> mainButtonList = driver.findElements(By.xpath("//*[@id='block_top_menu']/ul/li"));

        for (WebElement button : mainButtonList) {
            if (button.getText().equals("DRESSES")) {
                action.moveToElement(button).perform();
                List<WebElement> options = driver.findElements(By.xpath("//*[@id='block_top_menu']/ul/li/following::ul/li"));
                for (WebElement option : options) {
                    if (option.getText().equals("CASUAL DRESSES")) {
                        option.click();
                        break;
                    }
                }
                break;
            }
        }

        js.executeScript("arguments[0].scrollIntoView()", driver.findElement(By.cssSelector("div[class$='product-image-container']")));
        action.moveToElement(driver.findElement(By.cssSelector("div[class$='product-image-container']")));
        action.click(driver.findElement(By.xpath("//a[@title='Add to cart']/span[contains(text(), 'Add to cart')]")));
        action.perform();
        WebElement continueShopping = driver.findElement(By.xpath("//div[@id='layer_cart']//span[@title='Continue shopping']"));
        wait.until(ExpectedConditions.visibilityOf(continueShopping));
        continueShopping.click();
    }

    // Moving Into Cart, Clicking Checkout and Checking If All Our Items Are in Place
    @Test(priority = 6)
    public void testcase6() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, 4);
        Actions action = new Actions(driver);

        js.executeScript("window.scrollTo(document.body.scrollHeight,0)");

        action.moveToElement(driver.findElement(By.partialLinkText("Cart"))).perform();

        WebElement checkoutButton = driver.findElement(By.cssSelector("a#button_order_cart"));
        wait.until(ExpectedConditions.visibilityOf(checkoutButton));
        checkoutButton.click();

        wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("order-detail-content"))));

        action.moveToElement(driver.findElement(By.xpath("//*[@id='order-detail-content']"))).perform();

        List<WebElement> summaryItems = driver.findElements(By.cssSelector("tr[id^='product']"));
        String[] expectedItems = {"Faded Short Sleeve T-shirts", "Printed Dress"};

        for (int i = 0; i < summaryItems.size(); i++) {
            List<WebElement> itemDesc = driver.findElements(By.xpath("//tr[starts-with(@id,'product')]/child::td[contains(@class, 'description')]/p"));

            if (itemDesc.get(i).getText().equals(expectedItems[i])) {
                System.out.println("Correct Item");
            } else {
                System.out.println("Not Correct Item");
            }
        }
        // Clicking 'Proceed To Checkout'
        js.executeScript("arguments[0].scrollIntoView()", driver.findElement(By.cssSelector("p[class^='cart_navigation']")));
        List<WebElement> proceedButtons = driver.findElements(By.xpath("//a[@title='Proceed to checkout']"));
        for (WebElement proceedButton : proceedButtons) {
            if (proceedButton.isDisplayed()) {
                proceedButton.click();
            }
        }
    }

    //Clicking on Proceed To Checkout and Signing up Email
    @Test(priority = 7)
    public void testCase7() {
        WebDriverWait wait = new WebDriverWait(driver, 7);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement formEl = driver.findElement(By.cssSelector("form[id^='create-account']"));
        wait.until(ExpectedConditions.visibilityOf(formEl));
        js.executeScript("arguments[0].scrollIntoView()", driver.findElement(By.cssSelector("form[id^='create-account']")));

        Random randomNum = new Random();

        String emailSample = "mikheilSoziashvili" + randomNum.nextInt() + "@gmail.com";
        driver.findElement(By.id("email_create")).sendKeys(emailSample);

        driver.findElement(By.id("SubmitCreate")).click();


    }

    // Filling Personal Information
    @Test(priority = 8)
    public void testCase8() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 7);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("uniform-id_gender1")));

        // Filling Out Information
        driver.findElement(By.cssSelector("input[id$='gender1']")).click();
        driver.findElement(By.cssSelector("input[id$='firstname']")).sendKeys("Mikheil");
        driver.findElement(By.cssSelector("input[id$='lastname']")).sendKeys("Soziashvili");
        driver.findElement(By.cssSelector("input#passwd")).sendKeys("12345");
        Select date = new Select(driver.findElement(By.id("days")));
        Select month = new Select(driver.findElement(By.id("months")));
        Select year = new Select(driver.findElement(By.id("years")));
        date.selectByValue("2");
        month.selectByValue("12");
        year.selectByValue("2001");
        driver.findElement(By.id("address1")).sendKeys("s");
        driver.findElement(By.id("city")).sendKeys("s");
        Select state = new Select(driver.findElement(By.id("id_state")));
        state.selectByValue("1");
        driver.findElement(By.id("postcode")).sendKeys("12345");
        Select country = new Select(driver.findElement(By.id("id_country")));
        country.selectByValue("21");
        driver.findElement(By.id("phone_mobile")).sendKeys("123456789");
        //Clicking Submit
        WebElement submitAccount = driver.findElement(By.id("submitAccount"));
        Thread.sleep(2000);
        submitAccount.click();
    }

    // Clicking Proceed To Checkout After Filling Out Form
    @Test(priority = 9)
    public void testcase9() {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String[] XPATH = {"//button[@type='submit' and @name='processAddress']", "//button[@type='submit' and @name='processCarrier']"};
        for (int i = 0; i < 2; i++) {
            WebElement checkoutButton = driver.findElement(By.xpath(XPATH[i]));
            wait.until(ExpectedConditions.visibilityOf(checkoutButton));
            js.executeScript("arguments[0].scrollIntoView()", checkoutButton);
            checkoutButton.click();
        }
    }

    // Handling Alert Window
    @Test(priority = 10)
    public void testcase10() {
        WebElement alertWindow = driver.findElement(By.xpath("//p[contains(text(),'must agree')]"));
        WebDriverWait wait = new WebDriverWait(driver, 2);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        if (alertWindow.isDisplayed()) {
            js.executeScript("arguments[0].click()", driver.findElement(By.cssSelector("div[class^='fancybox-overlay']")));
        }
        js.executeScript("arguments[0].scrollIntoView()", driver.findElement(By.id("cgv")));
        js.executeScript("arguments[0].click()", driver.findElement(By.id("cgv")));
        driver.findElement(By.xpath("//button[@type='submit' and @name='processCarrier']")).click();
    }

    //Checking Price, Paying w/ check and
    @Test(priority = 11)
    public void testCase11() {
        Actions action = new Actions(driver);
        WebDriverWait wait = new WebDriverWait(driver, 2);

        totalPrice = driver.findElement(By.id("total_price")).getText();
        action.moveToElement(driver.findElement(By.cssSelector("a[class='cheque']"))).click().perform();
        WebElement summaryPrice = driver.findElement(By.id("amount"));

        action.moveToElement(summaryPrice).perform();
        if (totalPrice.equals(summaryPrice.getText())) {
            System.out.println("correct price");
        }

        action.moveToElement(driver.findElement(By.xpath("//button/span[contains(text(), 'confirm')]"))).click().perform();

        WebElement contactButton = driver.findElement(By.partialLinkText("customer service"));
        wait.until(ExpectedConditions.visibilityOf(contactButton));
        action.moveToElement(contactButton).click().perform();

        Select heading = new Select(driver.findElement(By.id("id_contact")));
        Select reference = new Select(driver.findElement(By.id("id_contact")));
        heading.selectByValue("2");

        reference.selectByIndex(1);
        driver.findElement(By.id("message")).sendKeys("message text");

        File file = new File("src/test/test");
        driver.findElement(By.id("fileUpload")).sendKeys(file.getAbsolutePath());

        action.moveToElement(driver.findElement(By.id("submitMessage"))).click().perform();
    }

    @AfterClass
    public void afterTest() {
        driver.manage().deleteAllCookies();
        driver.quit();
    }
}