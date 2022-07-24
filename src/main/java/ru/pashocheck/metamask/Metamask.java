package ru.pashocheck.metamask;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Metamask {
    // ФИКС: у тебя url метамаска может быть другой в теории, точнее эта часть - nkbihfbeogaeaoehlefnkodbefgpgknn
    private final String metamaskHomeUrl = "chrome-extension://nkbihfbeogaeaoehlefnkodbefgpgknn/home.html";
    //
    private final String newAccountUrl = "chrome-extension://nkbihfbeogaeaoehlefnkodbefgpgknn/home.html#new-account";
    private final String newAccountInputNameXpath = "//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[2]/input";
    private final String newAccountCreateBtnXpath = "//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[2]/div/button[2]";
    private final String profileBtnXpath = "//*[@id=\"app-content\"]/div/div[1]/div/div[2]/button";
    private final String passwordInputXpath = "//*[@id=\"password\"]";
    private final String loginBtnXpath = "//*[@id=\"app-content\"]/div/div[3]/div/div/button";
    private final String sendBtnXpath = "//*[@id=\"app-content\"]/div/div[3]/div/div[2]/div[2]/button[2]/div";


    private final String password;
    private final WebDriver driver;
    private boolean isLogin = false;

    public Metamask(WebDriver driver, String password) {
        this.password = password;
        this.driver = driver;
    }

    private void Login() {
        this.driver.get(metamaskHomeUrl);
        WebElement passInput = this.driver.findElement(By.xpath(passwordInputXpath));
        passInput.click();
        passInput.sendKeys(this.password);
        WebElement loginBtn = this.driver.findElement(By.xpath(loginBtnXpath));
        loginBtn.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(profileBtnXpath)));
        this.isLogin = true;
    }

    public void SwitchToAcc(String name) {
        if (!this.isLogin) {
            this.Login();
        }
        this.driver.get(metamaskHomeUrl);
        WebElement profileBtn = this.driver.findElement(By.xpath(profileBtnXpath));
        profileBtn.click();
        List<WebElement> accounts = driver.findElements(By.className("account-menu__name"));
        for (WebElement account : accounts) {
            if (name.equals(account.getText())) {
                account.click();
                break;
            }
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(profileBtnXpath)));
    }

    private void SelectAsset(String name) {
        if (!this.isLogin) {
            this.Login();
        }
        List<WebElement> assets = this.driver.findElements(By.className("list-item--single-content-row"));
        for (WebElement asset : assets) {
            if (name.equals(asset.findElement(By.className("asset-list-item__token-symbol")).getText())) {
                asset.click();
                break;
            }
        }
    }

    public void LocalAmountTransfer(String tokenName, String amount, String accNamefrom, String accNameTo) {
        if (!this.isLogin) {
            this.Login();
        }
        this.SwitchToAcc(accNamefrom);
        WebElement assetsBtn = driver.findElement(By.xpath("//*[@id=\"app-content\"]/div/div[3]/div/div/div/div[3]/ul/li[1]/button"));
        assetsBtn.click();
        this.SelectAsset(tokenName.toUpperCase());
        WebElement sendBtn = driver.findElement(By.xpath("//*[@id=\"app-content\"]/div/div[3]/div/div[2]/div[2]/button[2]/div"));
        sendBtn.click();
        WebElement transferBetweenMyAccs = driver.findElement(By.xpath("//*[@id=\"app-content\"]/div/div[3]/div/div[3]/div/div/a"));
        transferBetweenMyAccs.click();
        List<WebElement> myAccs = driver.findElements(By.className("send__select-recipient-wrapper__group-item__title"));
        for (WebElement myAcc : myAccs) {
            if (accNameTo.equals(myAcc.getText())) {
                myAcc.click();
                break;
            }
        }
        WebElement amountInput = driver.findElement(By.xpath("//*[@id=\"app-content\"]/div/div[3]/div/div[3]/div/div[2]/div[2]/div[1]/div/div/div[1]/input"));
        amountInput.click();
        amountInput.sendKeys(amount);
        WebElement nextBtn = driver.findElement(By.xpath("//*[@id=\"app-content\"]/div/div[3]/div/div[4]/footer/button[2]"));
        nextBtn.click();
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"app-content\"]/div/div[3]/div/div[4]/div[3]/footer/button[2]")));
        WebElement confirmBtn = driver.findElement(By.xpath("//*[@id=\"app-content\"]/div/div[3]/div/div[4]/div[3]/footer/button[2]"));
        confirmBtn.click();
    }

    public List<String> GetAccounts() {
        if (!this.isLogin) {
            this.Login();
        }
        WebElement profileBtn = this.driver.findElement(By.xpath(profileBtnXpath));
        profileBtn.click();
        List<WebElement> elements = driver.findElements(By.className("account-menu__name"));
        return elements.stream().map(WebElement::getText).toList();
    }

    public void CreateAcc(String accName) {
        if (!this.isLogin) {
            this.Login();
        }
        this.driver.get(this.newAccountUrl);
        WebElement inputName = this.driver.findElement(By.xpath(newAccountInputNameXpath));
        inputName.click();
        inputName.sendKeys(accName);
        WebElement createBtn = this.driver.findElement(By.xpath(newAccountCreateBtnXpath));
        createBtn.click();
    }
}
