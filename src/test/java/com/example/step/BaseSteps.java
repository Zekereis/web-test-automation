package com.example.step;

import com.thoughtworks.gauge.Step;
import com.example.base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.openqa.selenium.TimeoutException;
import com.example.model.ElementInfo;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.HashMap;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class BaseSteps extends BaseTest { // BaseTest sınıfından miras alındı
    private static final Logger logger = LogManager.getLogger(BaseSteps.class);

    private Map<String, ElementInfo> elementMapList = new HashMap<>(); // Element bilgilerini tutan harita

    public BaseSteps() {
        super(); // Üst sınıfın constructor'ını çağır
        setUp();
        initMap(getFileList()); // BaseTest içindeki metotlara erişim sağlanıyor
        // Burada elementMapList'in uygun şekilde doldurulduğundan emin olun
    }

    @Step({"Click to element <key>", "<key> elementine tiklanir"})
    public void clickElement(String key) {
        if (key != null && !key.isEmpty()) {
            WebElement element = findElement(key);
            hoverElement(element);
            clickElement(element);
            logger.info(key + " elementine tiklandi.");
        } else {
            logger.error("Key parametresi null veya boş: clickElement");
        }
    }

    @Step({"Go to <url> address", "<url> adresine git"})
    public void goToUrl(String url) {
        if (url != null && !url.isEmpty()) {
            driver.get(url);
            logger.info(url + " adresine gidiliyor.");
        } else {
            logger.error("URL parametresi null veya boş: goToUrl");
        }
    }

    @Step({"Write value <text> to element <key>",
            "<text> textini <key> Alanina Yaz"})
    public void ssendKeys1(String text, String key) {
        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.visibilityOfElementLocated(infoParam));
        if (!key.equals("")) {
            findElement(key).sendKeys(text);
            logger.info(key + " elementine " + text + " texti yazıldı.");
        }
    }



    private void hoverElement(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
    }

    private WebElement findElement(String key) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        ElementInfo elementInfo = findElementInfoByKey(key); // Key ile elementInfo'yu alıyoruz
        if (elementInfo != null) {
            String locatorType = elementInfo.getType();
            String locatorValue = elementInfo.getValue();

            switch (locatorType.toLowerCase()) {
                case "xpath":
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(locatorValue)));
                case "id":
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.id(locatorValue)));
                case "css":
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(locatorValue)));
                default:
                    logger.error("Desteklenmeyen locator türü: " + locatorType);
                    throw new IllegalArgumentException("Desteklenmeyen locator türü: " + locatorType);
            }
        } else {
            logger.error("Element bulunamadı: " + key);
            throw new NoSuchElementException("Element bulunamadı: " + key);
        }
    }

    private void clickElement(WebElement element) {
        element.click();
    }

    public By getElementInfoToBy(ElementInfo elementInfo) {
        By by = null;
        if (elementInfo.getType().equals("css")) {
            by = By.cssSelector(elementInfo.getValue());
        } else if (elementInfo.getType().equals("name")) {
            by = By.name(elementInfo.getValue());
        } else if (elementInfo.getType().equals("id")) {
            by = By.id(elementInfo.getValue());
        } else if (elementInfo.getType().equals("xpath")) {
            by = By.xpath(elementInfo.getValue());
        } else if (elementInfo.getType().equals("linkText")) {
            by = By.linkText(elementInfo.getValue());
        } else if (elementInfo.getType().equals("partialLinkText")) {
            by = By.partialLinkText(elementInfo.getValue());
        }
        return by;
    }

    // 'key' ile ElementInfo döndüren metod
    private ElementInfo findElementInfoByKey(String key) {
        // elementMapList'te 'key' ile eşleşen bir ElementInfo arıyoruz
        ElementInfo elementInfo = elementMapList.get(key);

        if (elementInfo == null) {
            logger.error("ElementInfo bulunamadı: " + key);
            throw new NoSuchElementException("ElementInfo bulunamadı: " + key);
        }

        return elementInfo;
    }
}
