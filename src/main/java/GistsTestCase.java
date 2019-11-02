import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.Alert;

public class GistsTestCase {
    private WebDriver driver;
    public static final Dotenv dotenv = Dotenv.load();

    @BeforeTest
    public void login() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver","chromedriver.exe");
        driver = new ChromeDriver();

        String baseUrl = "https://www.github.com/login";
        driver.get(baseUrl);

        driver.findElement(By.id("login_field")).sendKeys(dotenv.get("USERNAME"));
        driver.findElement(By.id("password")).sendKeys(dotenv.get("PASSWORD"));
        driver.findElement(By.name("commit")).click();

        //assert if Pull request menu is displayed after login
        Thread.sleep(10);
        Assert.assertTrue(driver.findElement(By.xpath("//a[@href='/pulls']")).isDisplayed());
    }

    @Test
    public void createPublicGist() {
        //go to gists page
        driver.get("https://gist.github.com");

        //fill Gists description
        driver.findElement(By.name("gist[description]")).sendKeys("Test create gists");
        //fill filename
        driver.findElement(By.name("gist[contents][][name]")).sendKeys(dotenv.get("GIST_NAME"));
        //fill code on file
        driver.findElement(By.className("CodeMirror-code")).sendKeys("Create existing gists");
        driver.findElement(By.name("gist[public]")).click();

        String gistName = driver.findElement(By.xpath("//*[@class='user-select-contain gist-blob-name css-truncate-target']")).getText();
        Assert.assertEquals(gistName,dotenv.get("GIST_NAME"));
    }

    @Test
    public void editExistingGist() {
        //go to gists page
        driver.get("https://gist.github.com");

        //on this case, we will always edit 1st gist on the list
        driver.findElement(By.xpath("//*[@class='css-truncate d-block'][1]")).click();
        //click Edit button
        driver.findElement(By.xpath("//*[@class='octicon octicon-pencil']")).click();

        driver.findElement(By.className("CodeMirror-code")).sendKeys("Edit existing gists");
        driver.findElement(By.xpath("//*[@class='btn btn-primary']")).click();

        String newText = driver.findElement(By.xpath("//*[@class='blob-code blob-code-inner js-file-line']")).getText();
        //Assert if changes is included in gists
        Assert.assertTrue(newText.contains("Edit existing gists"));
    }

    @Test
    public void deleteExistingGist() {
        //go to gists page
        driver.get("https://gist.github.com");

        //on this case, we will always delete 1st gist on the list
        driver.findElement(By.xpath("//*[@class='css-truncate d-block'][1]")).click();
        //click Delete button
        driver.findElement(By.xpath("//*[@class='octicon octicon-trashcan']")).click();

        Alert javascriptAlert = driver.switchTo().alert();
        System.out.println(javascriptAlert.getText()); // Get text on alert box
        javascriptAlert.accept();
    }

    @Test
    public void seeListOfGist() {
        //go to gists page
        driver.get("https://gist.github.com");

        driver.findElement(By.xpath("//*[text()='See all of your gists']\t")).click();

        //get how many gists on the list
        int n = Integer.parseInt(driver.findElement(By.xpath("//*[@class='Counter']")).getText());

        //do loop to check all list in gists
        for(int i=1;i<=n;i++){
            Assert.assertTrue(driver.findElement(By.xpath("//*[@class='gist-snippet']["+n+"]")).isDisplayed());
        }
    }

    @AfterTest
    public void tearDown() throws Exception {
        this.driver.quit();
    }
}
