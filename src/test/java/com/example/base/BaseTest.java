package com.example.base;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.model.ElementInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class BaseTest {

    protected static WebDriver driver;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private static final String DEFAULT_DIRECTORY_PATH = "elementvalues";

    // elementMapList değişkenini protected yapıyoruz ki alt sınıflar erişebilsin
    protected ConcurrentMap<String, Object> elementMapList = new ConcurrentHashMap<>();

    /**
     * WebDriver'ı başlatır ve Chrome tarayıcısını açar.
     */
    public void setUp() {
        logger.info("WebDriver başlatılıyor...");
        System.setProperty("webdriver.chrome.driver", "web_driver/chromedriver.exe"); // ChromeDriver yolunu belirtin
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Tarayıcıyı tam ekran başlat
        options.addArguments("--disable-notifications"); // Bildirimleri devre dışı bırak

        // Chrome tarayıcısını başlat
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // Bekleme süresi ayarla
        logger.info("WebDriver başlatıldı.");
    }

    public void initMap(File[] fileList) {
        Type elementType = new TypeToken<List<ElementInfo>>() {
        }.getType();
        Gson gson = new Gson();
        List<ElementInfo> elementInfoList = null;
        for (File file : fileList) {
            try {
                elementInfoList = gson
                        .fromJson(new FileReader(file), elementType);
                elementInfoList.parallelStream()
                        .forEach(elementInfo -> elementMapList.put(elementInfo.getKey(), elementInfo));
            } catch (FileNotFoundException e) {
                logger.warn("{} not found", e);
            }
        }
    }

    /**
     * WebDriver'ı kapatır ve tarayıcıyı sonlandırır.
     */
    public void tearDown() {
        if (driver != null) {
            logger.info("WebDriver kapatılıyor...");
            driver.quit();
            logger.info("WebDriver kapatıldı.");
        }
    }

    public File[] getFileList() {
        File[] fileList = new File(
                this.getClass().getClassLoader().getResource(DEFAULT_DIRECTORY_PATH).getFile())
                .listFiles(pathname -> !pathname.isDirectory() && pathname.getName().endsWith(".json"));
        if (fileList == null) {
            logger.warn(
                    "File Directory Is Not Found! Please Check Directory Location. Default Directory Path = {}",
                    DEFAULT_DIRECTORY_PATH);
            throw new NullPointerException();
        }
        return fileList;
    }

    /**
     * Belirtilen URL'ye gider.
     *
     * @param url Gidilecek URL
     */
    public void navigateTo(String url) {
        logger.info("{} adresine gidiliyor...", url);
        driver.get(url);
        logger.info("{} adresine gidildi.", url);
    }

    // Getter metodu ekliyoruz
    public ConcurrentMap<String, Object> getElementMapList() {
        return elementMapList;
    }
}
