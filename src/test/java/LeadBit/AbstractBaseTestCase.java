package LeadBit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

class AbstractBaseTestCase {

    private final String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    private StringBuilder salt = new StringBuilder();
    private Random rnd = new Random();
    {

        while (salt.length() < 8) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
    }

    private static final int PAGE_LOAD_TIMEOUT = 10;
    private static final int WAIT_SECONDS = 3;

    String projectUrl = System.getProperty("project_url");
    String mongoAddress = "http://dev.leadbit.com";
    private String projectBuildNumber = System.getProperty("project_build_number");

    String userAdministratorLogin = System.getProperty("project_admin_username");
    String userAdministratorPassword = System.getProperty("project_admin_password");

    String userAdvertiserLogin = System.getProperty("project_advertiser_username");
    String userAdvertiserPassword = System.getProperty("project_advertiser_password");

    String userWebmasterLogin = System.getProperty("project_webmaster_username");
    String userWebmasterPassword = System.getProperty("project_webmaster_password");

    String newWebmaster = "newWeb"+projectBuildNumber;
    String newWebmasterPass = "newWebPass";
    String newWebmasterMail = "newWebMail"+projectBuildNumber+"@grr.la";
    String newWebmasterSkype = "newWebSkype"+projectBuildNumber;
    int registeredFlag = 1;
    int activatedFlag = 0;

    String devLandingFito = "http://staging.fitospray-pro.com";
    String devLandingMask = "http://staging.colmask.com";

    String idValue;
    private String offerKindName = "OfferKind_" + projectBuildNumber;
    String offerKindNameRu = offerKindName + "_Ru";
    String offerKindNameEn = offerKindName + "_En";
    String offerKindNameEs = offerKindName + "_Es";
    String offerKindNameRuDescr = offerKindName + "_Ru_description";
    String offerKindNameEnDescr = offerKindName + "_En_description";
    String offerKindNameEsDescr = offerKindName + "_Es_description";
    String offerKindId;
    String offerKindCreationHashId;

    String sourceName = offerKindNameEn + "_Source";

    String flowTitle = offerKindNameEn + "_Flow";
    String flowId;
    String flowLink;
    String flowHash;
    String TID1;
    String TID2;
    String apiKey = System.getProperty("api_key");
    long timestamp;

    String leadCountry = "ru";
    String leadCustomerName = salt.toString()+"_"+projectBuildNumber;
    String leadCustomerPhone = "79251234567";
    String leadCustomerAddress = "Some Address";
    String leadLanding = "http://staging.fitospray-pro.com";
    String leadUserAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0";
    String leadAccept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    String leadAcceptLanguage = "en-US,en;q=0.8,ru-RU;q=0.5,ru;q=0.3";
    String leadAcceptEncoding = "gzip, deflate";
    String leadReferer = "http://leadbit.com/?utm=autotests";
    String leadConnection = "keep-alive";
    String crmGroupId = "40";
    String offerUrlSendConversion = "https://8requests.ru:1443/api/v1/order";
    String offerUrlSendConversionStatus = "https://8requests.ru:1443/api/v1/order_status";

    HttpClient httpclient = HttpClients.createDefault();
    HttpPost getTid;
    HttpResponse firstResponse;
    HttpResponse secondResponse;
    HttpPost firstOrder;
    HttpPost secondOrder;

    String offerEditLink;
    String offerId;
    String offerIdConnectionRequest;

    long startTime;
    long endTime;
    long duration;

    static int getResponseCode(String urlString) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.connect();

        return httpURLConnection.getResponseCode();
    }

    WebDriver getFirefoxMobileWebDriver() {
        try {
            FirefoxProfile fp = new FirefoxProfile();
            DesiredCapabilities desiredCapability = DesiredCapabilities.firefox();
            fp.setPreference("general.useragent.override", "Mozilla/5.0 (Linux; Android 4.4.2; Nexus 4 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.114 Mobile Safari/537.36");
            desiredCapability.setCapability(FirefoxDriver.PROFILE, fp);

            WebDriver webDriver;
            webDriver = new RemoteWebDriver(new URL(System.getProperty("remote")), desiredCapability);

            webDriver.manage().window().maximize();

            webDriver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
            return webDriver;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected WebDriverWait getWebDriverWait(WebDriver webDriver) {
        return new WebDriverWait(webDriver, WAIT_SECONDS);
    }

    /*void mongoRequest() {
        MongoClient mongoClient = new MongoClient(mongoAddress, 27017);
    }
    private MongoClient mongoClient = new MongoClient("dev.leadbit.com", 27017);

    MongoDatabase database = mongoClient.getDatabase("cpa_stats");

    MongoCollection<Document> collection = database.getCollection("tracker_lead");

    Document myDoc = collection.find(and(eq("check_id", TID), eq("order.order_data.data.name", orderName), gte("order.created_at", timestamp))).first();

    JSONObject objFromDB = new JSONObject(myDoc);*/
}
