package ru.pashocheck;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.pashocheck.metamask.Metamask;

import java.time.Duration;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;


// ВАЖНО:
//  - хром должен быть закрыт перед запуском траха
//  - все аккаунты метамаска должны быть connected к https://www.ethsign.xyz/(автотрахнуть не получилось), ну это хуйня - две кнопки в метамаске нажать
//  - в целом фулл автотрах, кроме клейма самого нфт на https://galaxy.eco/EthSign/campaign/GCV7dUtF8j/, так как там стоит какая-то защита от селениума,
//    наверно можно как-то трахнуть, но я не осилил пока
//  - есть вероятность что xpath будет разным(из-за разных систем), тогда напиши мне расскажу как пофиксить, заебись если сам знаешь
//  - есть вероятность что мои таймауты тебе не подойдут(интернет там типо разные или чето такое) и надо будет их фиксануть
//  - перед запуском ищем все комменты с %ФИКС% и правим как там написано(искать по всему проекту, они не только в main)
//  - в классе Metamask если что есть метод для создания нового кошелька, то есть можно нагенерить с его помощью
//  - pdf подписывается одна и та же, если надо будет разные, то могу рассказть как

public class Main {
    public static void main(String[] args) {
        ChromeOptions chromeOptions = new ChromeOptions();
        // ФИКС: тут вместо /Users/pakulkov/Library/Application Support/Google/Chrome/ указать путь до хрома
        chromeOptions.addArguments("--user-data-dir=/Users/pakulkov/Library/Application Support/Google/Chrome/");
        // ФИКС: тут вместо Default указать надо профиль хрома с метамаском, можно оставить default и тогда откроется твой основной
        // Вообще профили у меня это просто папки с названиями типо Profile 1, и вот как раз нейм такой папки тут надо прописать
        chromeOptions.addArguments("--profile-directory=Default");

        // ФИКС: надо положить по этому пути драйвер для своей версии хрома
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");


        ChromeDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().window().fullscreen();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        // ФИКС: вместо mymetamaskpass написать свой пароль от метамаска
        Metamask metamask = new Metamask(driver, "mymetamaskpass");
        // ФИКС: в цикле указываем номера акков метамаска, в данном случаи будет от 16 до 17(включительно, то есть 2 акка)
        for (int i = 16; i < 18; i++) {
            // ФИКС: эта хуйня чтобы токены раскидывать по аккам
            // Лучше делать так: Закоментить запуск траха, раскоментить раскидку токенов(не забыть поменять цикл)
            // После того как раскинеется - комментим обратно раскидку и разкоменчиваем запуск траха и СТАРТУЕМ
//            metamask.LocalAmountTransfer("matic", "0,06", "Account 1", "Account " + i);

            // ЗАПУСК САМОГО ТРАХА
            metamask.SwitchToAcc("Account " + i);
            System.out.println("Account " + i + " трах запущен");
            try {
                Fuck(driver);
            } catch (Exception e) {
                System.out.println("Account " + i + " трах не удался!");
                continue;
            }
            System.out.println("Account " + i + " трахнуто");
        }


    }

    public static void Fuck(WebDriver driver) throws InterruptedException {
        driver.get("https://www.ethsign.xyz/");

        WebElement betaBtn = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/section[1]/div/div[5]/button"));
        betaBtn.click();

        WebElement metamaskBtn = driver.findElement(By.xpath("/html/body/div[2]/div/div/button[1]"));
        metamaskBtn.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
//        wait.until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(By.className("load-spinner-container"))));
        Thread.sleep(5000);

        // upload button
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/div/div[2]/div[1]")).click();

        // load pdf
        By fileInput = By.cssSelector("input[type=file]");
        String filePath = "/Users/pakulkov/IdeaProjects/StarkNetEbka/src/main/resources/39144096.a4.pdf";
        driver.findElement(fileInput).sendKeys(filePath);


        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[3]/div/button")));
        // next button click
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[3]/div/button")).click();


        // Add  myself
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[1]/button[1]")).click();
        // Add
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/button")));
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/button")).click();
        // next button click
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[4]/button[2]")).click();


        WebElement iframe = driver.findElement(By.xpath("//*[@id=\"webviewer-1\"]"));

        driver.switchTo().frame(iframe);
        // click Signature Fields
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[1]/div/div[2]/div/nav/div[1]/div/button/span")).click();

        Actions clickAt = new Actions(driver);
        Random random = new Random();

        // click to random place on 1 page
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), 200, 400).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), 200, 400).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), 200, 400).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), 200, 400).click();
        clickAt.build().perform();

        // click to Date Signed
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[1]/div/div[2]/div/nav/div[2]/button")).click();
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[1]/div/div[2]/div/nav/div[2]/div/button/span")).click();

        // click to random place on 1 page
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), 100, 300).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), 100, 300).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), 100, 300).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), 100, 300).click();
        clickAt.build().perform();


        // click to Wallet address
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[1]/div/div[2]/div/nav/div[3]/button")).click();
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[1]/div/div[2]/div/nav/div[3]/div/button")).click();

        // click to random place on 1 page
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), random.nextInt(40, 60),
                random.nextInt(20, 30)).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), random.nextInt(40, 60),
                random.nextInt(20, 30)).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), random.nextInt(80, 90),
                random.nextInt(20, 30)).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), random.nextInt(40, 60),
                random.nextInt(20, 30)).click();
        clickAt.build().perform();


        // click toCheck box
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[1]/div/div[2]/div/nav/div[4]/button")).click();
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[1]/div/div[2]/div/nav/div[4]/div/button")).click();

        // click to random place on 1 page
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), random.nextInt(40, 60),
                random.nextInt(20, 30)).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), random.nextInt(40, 60),
                random.nextInt(20, 30)).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), random.nextInt(80, 90),
                random.nextInt(20, 30)).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), random.nextInt(40, 60),
                random.nextInt(20, 30)).click();
        clickAt.build().perform();

        // click to Text Field
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[1]/div/div[2]/div/nav/div[5]/button")).click();
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[1]/div/div[2]/div/nav/div[5]/div/button[2]")).click();

        // click to random place on 1 page
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), random.nextInt(40, 60),
                random.nextInt(20, 30)).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), random.nextInt(40, 60),
                random.nextInt(20, 30)).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), random.nextInt(80, 90),
                random.nextInt(20, 30)).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"pageContainer1\"]")), random.nextInt(40, 60),
                random.nextInt(20, 30)).click();
        clickAt.build().perform();


        driver.switchTo().parentFrame();
        // click Next
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/button[2]")).click();

        //password input
        WebElement passwordInput = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[3]/div[2]/div[2]/div[2]/div/div/div/input"));
        passwordInput.click();
        passwordInput.sendKeys("123");


        // click send
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[4]/button[2]")).click();


        String original = driver.getWindowHandle();
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        Set<String> windowHandles = driver.getWindowHandles();
        windowHandles.remove(original);
        WebDriver signWindow = driver.switchTo().window(windowHandles.iterator().next());
        // sigh btn
        signWindow.findElement(By.xpath("//*[@id=\"app-content\"]/div/div[2]/div/div[3]/button[2]")).click();
        signWindow.close();
        driver.switchTo().window(original);


        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        windowHandles = driver.getWindowHandles();
        windowHandles.remove(original);
        WebDriver confirmWindow = driver.switchTo().window(windowHandles.iterator().next());
        confirmWindow.findElement(By.xpath("//*[@id=\"app-content\"]/div/div[2]/div/div[4]/div[3]/footer/button[2]")).click();
        confirmWindow.close();
        driver.switchTo().window(original);


        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        windowHandles = driver.getWindowHandles();
        windowHandles.remove(original);
        signWindow = driver.switchTo().window(windowHandles.iterator().next());
        signWindow.findElement(By.xpath("//*[@id=\"app-content\"]/div/div[2]/div/div[3]/button[2]")).click();
        signWindow.close();
        driver.switchTo().window(original);

        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        windowHandles = driver.getWindowHandles();
        windowHandles.remove(original);
        signWindow = driver.switchTo().window(windowHandles.iterator().next());
        signWindow.findElement(By.xpath("//*[@id=\"app-content\"]/div/div[2]/div/div[3]/button[2]")).click();
        signWindow.close();
        driver.switchTo().window(original);

        // Back to dashboard
        driver.findElement(By.xpath("/html/body/div[2]/div/div/div[2]/div/button")).click();


        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div[2]/div[3]/div[1]/div/div[3]/button")));
        // Sign now
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[3]/div[1]/div/div[3]/button")).click();

        WebElement passInput = driver.findElement(By.xpath("/html/body/div[2]/div/div/div[2]/div/div/div/div/div/input"));
        passInput.click();
        passInput.sendKeys("123");

        //Decrypt
        driver.findElement(By.xpath("/html/body/div[2]/div/div/div[2]/div/button")).click();

        iframe = driver.findElement(By.xpath("//*[@id=\"webviewer-1\"]"));
        driver.switchTo().frame(iframe);
        driver.findElement(By.className("signature")).click();


        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[10]/div/div/div[2]/div[1]/div[2]/div[1]/canvas")), random.nextInt(40, 60),
                random.nextInt(20, 30)).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[10]/div/div/div[2]/div[1]/div[2]/div[1]/canvas")), random.nextInt(40, 60),
                random.nextInt(20, 30)).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[10]/div/div/div[2]/div[1]/div[2]/div[1]/canvas")), random.nextInt(80, 90),
                random.nextInt(20, 30)).click();
        clickAt.moveToElement(driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[10]/div/div/div[2]/div[1]/div[2]/div[1]/canvas")), random.nextInt(40, 60),
                random.nextInt(20, 30)).click();
        clickAt.build().perform();

        // click insert
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[10]/div/div/div[2]/div[2]/button")).click();

        // click finish
        driver.switchTo().window(original);
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/button")).click();


        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        windowHandles = driver.getWindowHandles();
        windowHandles.remove(original);
        signWindow = driver.switchTo().window(windowHandles.iterator().next());
        signWindow.findElement(By.xpath("//*[@id=\"app-content\"]/div/div[2]/div/div[3]/button[2]")).click();
        signWindow.close();
        driver.switchTo().window(original);


        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        windowHandles = driver.getWindowHandles();
        windowHandles.remove(original);
        signWindow = driver.switchTo().window(windowHandles.iterator().next());
        try {
            WebElement element = signWindow.findElement(By.xpath("//*[@id=\"app-content\"]/div/div[2]/div/div[4]/button[2]"));
            if (!element.isEnabled()) {
                WebElement element1 = signWindow.findElement(By.className("signature-request-message__scroll-button"));
                element1.click();
            }
            element.click();
        } catch (NoSuchElementException ex) {
            WebElement element = signWindow.findElement(By.className("request-signature__footer__sign-button"));
            element.click();
        }
        signWindow.close();
        driver.switchTo().window(original);


        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        windowHandles = driver.getWindowHandles();
        windowHandles.remove(original);
        signWindow = driver.switchTo().window(windowHandles.iterator().next());
        try {
            WebElement element = signWindow.findElement(By.className("request-signature__footer__sign-button"));
            element.click();
        } catch (NoSuchElementException ex) {
            WebElement element = signWindow.findElement(By.xpath("//*[@id=\"app-content\"]/div/div[2]/div/div[4]/button[2]"));
            if (!element.isEnabled()) {
                WebElement element1 = signWindow.findElement(By.className("signature-request-message__scroll-button"));
                element1.click();
            }
            element.click();
        }
        signWindow.close();
        driver.switchTo().window(original);


//        // Back to Dashboard
        driver.findElement(By.xpath("/html/body/div[2]/div/div/div[2]/div/button")).click();

//        driver.get("https://galaxy.eco/EthSign/campaign/GCV7dUtF8j/");
//        //Claim nft
//        driver.findElement(By.xpath("//*[@id=\"app\"]/div/main/div/div/div/div/div[1]/div[1]/div[2]/div[2]/div/div[1]/div/div/button")).click();
    }
}
