package LeadBit;

import com.codeborne.selenide.Configuration;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bson.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.mongodb.client.model.Filters.*;
import static java.lang.Thread.sleep;

public class OrderTestCase extends AbstractBaseTestCase {
    static {

        Configuration.holdBrowserOpen = true;
        Configuration.reopenBrowserOnFail = true;
        Configuration.timeout = 7000;
    }

    @Test(priority = 1)
    public void checkStatusCodeTest() throws IOException {
        System.out.println();
        int responseStatusCode = getResponseCode(projectUrl);

        if (!((responseStatusCode >= 200 && responseStatusCode < 300) || responseStatusCode == 304))
            System.out.println("Invalid response status code: " + responseStatusCode);
    }

    @Test(priority = 2)
    public void localeChangingTest() {
        open(projectUrl);
        $(byText("Leadbit.com - international cpa-network")).should(exist);

        $("i[class=icon-flag-arrow-down]").click();
        $("i[class=flag-ru]").click();
        $(byText("Leadbit.com - международная товарная cpa-сеть")).should(exist);

        $("i[class=icon-flag-arrow-down]").click();
        $("i[class=flag-us]").click();
        $(byText("Leadbit.com - international cpa-network")).should(exist);
    }

    @Test(priority = 10)
    public void webMasterRegistrationSentTest() {
        open(projectUrl + "/referral/8cb144f5e073193d21ae9483100367e0");

        $("#user_registration_display_name").setValue(newWebmaster);
        $("#user_registration_email").setValue(newWebmasterMail);
        $("#user_registration_plainPassword_first").setValue(newWebmasterPass);
        $("#user_registration_plainPassword_second").setValue(newWebmasterPass);
        $("#user_registration_skype").setValue(newWebmasterSkype);

        if ($("input[class=invalid-e").is(exist)) {
            System.out.println($("[style*=block]").$("label").getText()+": "+newWebmasterMail);
            registeredFlag = 1;
        } else {
            registeredFlag = 0;

            $("input[type=submit]").click();
            $(byText("Register")).should(exist);
        }
    }

    @Test(priority = 11)
    public void webMasterRegistrationAutorisationWOActivationTest() {

        open(projectUrl);
        $(byText("Leadbit.com - international cpa-network")).should(exist);

        $("input[name=_username]").setValue(newWebmasterMail);
        $("input[name=_password]").setValue(newWebmasterPass);
        $("button").click();

        if ($(byText("User account is disabled.")).exists()) {
            activatedFlag = 0;
        }

        if ($(byText("Register")).is(exist)) {
            activatedFlag = 0;
        } else {
            open(projectUrl+"/logout");
            activatedFlag = 1;
        }
    }

    @Test(priority = 12)
    public void webMasterActivationTest() {
        if (activatedFlag == 0) {

            open(projectUrl);
            $(byText("Leadbit.com - international cpa-network")).should(exist);

            $("input[name=_username]").setValue(userAdministratorLogin);
            $("input[name=_password]").setValue(userAdministratorPassword);
            $("button").click();
            $(byText("Dashboard - Leadbit.com")).should(exist);

            if ($("label.st-checkbox").is(visible)) {
                $("#modalBody > div.user-hello__remind > form > label").click();
                $("#Modal > div > div > div.modal-header > button > span").click();
            }

            open(projectUrl + "/admin/cpa/userprofile/user/list");

            int userTableStringNumber = 1;
            String userTable = "null";

            while (!userTable.equals(newWebmasterMail)) {
                userTableStringNumber++;
                userTable = $("tbody > tr:nth-child(" + userTableStringNumber + ") > td:nth-child(5)").getText();
            }

            String regLink = $("tbody > tr:nth-child(" + userTableStringNumber + ") > td:nth-child(10) > a").getAttribute("href");

            open(projectUrl + "/logout");
            $(byText("Leadbit.com - international cpa-network")).should(exist);

            open(regLink);
            $(byText("Your account is activated!")).should(exist);

        } else {
            System.out.println("User: "+newWebmasterMail+" is activated before.");
        }
    }

    @Test(priority = 13)
    public void webMasterActivatedLoginTest() {

        open(projectUrl);
        $(byText("Leadbit.com - international cpa-network")).should(exist);

        $("input[name=_username]").setValue(newWebmasterMail);
        $("input[name=_password]").setValue(newWebmasterPass);
        $("button").click();
        $(byText("Dashboard - Leadbit.com")).should(exist);

        if ($("label.st-checkbox").is(visible)) {
            $("#modalBody > div.user-hello__remind > form > label").click();
            $("#Modal > div > div > div.modal-header > button > span").click();
        }

        open(projectUrl+"/logout");
        $(byText("Leadbit.com - international cpa-network")).should(exist);
    }

    @Test(priority = 30)
    public void checkOfferKindCreationTest() throws InterruptedException {
        open(projectUrl);
        $(byText("Leadbit.com - international cpa-network")).should(exist);

        $("input[name=_username]").setValue(userAdministratorLogin);
        $("input[name=_password]").setValue(userAdministratorPassword);
        $(".submit").click();

        open(projectUrl + "/admin/cpa/webmaster/offerkind/create");
        $(".a2lix_translationsLocales li", 1).should().$(".active");

        idValue = $("div [id*=_translations_en]").getAttribute("id");
        idValue=idValue.substring(0, idValue.indexOf("_translations_en"));

        $("#"+idValue+"_translations_en_title").setValue(offerKindNameEn);
        $("#"+idValue+"_translations_en_description").setValue(offerKindNameEnDescr);

        $(".a2lix_translationsLocales li").click();
        $("#"+idValue+"_translations_ru_title").setValue(offerKindNameRu);
        $("#"+idValue+"_translations_ru_description").setValue(offerKindNameRuDescr);

        $(".a2lix_translationsLocales li", 2).click();
        $("#"+idValue+"_translations_es_title").setValue(offerKindNameEs);
        $("#"+idValue+"_translations_es_description").setValue(offerKindNameEsDescr);


        $("#field_actions_"+idValue+"_targets").hover().click();
        $("#s2id_"+idValue+"_targets_0_kind").click();
        $(byXpath("//div[contains(text(),'Confirmed request')]")).click();
        $("#"+idValue+"_targets_0_advertPrice").setValue("1");
        $("#"+idValue+"_targets_0_wmPrice").setValue("3");
        $("#s2id_"+idValue+"_targets_0_countries").click();
        $(byXpath("//div[contains(text(),'Russia')]")).click();

        $("#field_actions_"+idValue+"_targets").hover().click();
        $("#s2id_"+idValue+"_targets_1_kind").click();
        $(".select2-result-label", 3).click();
        $("#"+idValue+"_targets_1_advertPrice").setValue("2");
        $("#"+idValue+"_targets_1_wmPrice").setValue("5");
        $("#s2id_"+idValue+"_targets_1_countries").click();
        $(byXpath("//div[contains(text(),'Spain')]")).click();

        $("#field_actions_"+idValue+"_landingPagesCollection").hover().click();
        $("#"+idValue+"_landingPagesCollection_0_url").setValue(devLandingFito);
        $("#s2id_"+idValue+"_landingPagesCollection_0_minUserLevel").click();
        $(byXpath("//div[contains(text(),'Золотой')]")).click();
        $("#"+idValue+"_landingPagesCollection_0_comment").setValue("comment");

        $("#field_actions_"+idValue+"_landingPagesCollection").hover().click();
        $("#"+idValue+"_landingPagesCollection_1_url").setValue(devLandingMask);
        $("#s2id_"+idValue+"_landingPagesCollection_1_minUserLevel").click();
        $(byXpath("//div[contains(text(),'Золотой')]")).click();
        $("#s2id_"+idValue+"_landingPagesCollection_1_kind").click();
        $(byXpath("//div[contains(text(),'Мобильный')]")).click();
        $("#"+idValue+"_landingPagesCollection_1_comment").setValue("comment");

        //File f = new File("src/test/resources/Koala90x75.jpg");
        $("#"+idValue+"_logoFile_file").hover().setValue("C:\\Koala90x75.jpg"); //Потому что запуск на удаленной машине, на винде.
        //$("#"+idValue+"_logoFile_file").hover().setValue(f.getAbsolutePath());

        $("#s2id_"+idValue+"_currency").hover().click();
        $(byXpath("//div[contains(text(),'Рубль')]")).click();

        $("#s2id_"+idValue+"_language").hover().click();
        $(byXpath("//div[contains(text(),'Russian (ru)')]")).click();

        $("#s2id_"+idValue+"_category").hover().click();
        $(byXpath("//div[contains(text(),'Finance')]")).click();

        $("#s2id_"+idValue+"_minUserLevel").hover().click();
        $(byXpath("//div[contains(text(),'Золотой')]")).click();

        $("#s2id_"+idValue+"_sources").hover().click();
        $(byXpath("//div[contains(text(),'Doorway')]")).click();

        $("[name=btn_create_and_list]").hover().click();

        open(projectUrl + "/logout");
        $(byText("Leadbit.com - international cpa-network")).should(exist);
    }

    @Test(priority = 41)
    public void offerCreationTest() {
        open(projectUrl);
        $(byText("Leadbit.com - international cpa-network")).should(exist);

        $("input[name=_username]").setValue(userAdministratorLogin);
        $("input[name=_password]").setValue(userAdministratorPassword);
        $(".submit").click();
        $(byText("Dashboard - Leadbit.com")).should(exist);

        open(projectUrl+"/?login_as_leadbit_user="+userAdvertiserLogin);
        $(byText("Dashboard - Leadbit.com")).should(exist);

        if ($("label.st-checkbox").is(visible)) {
            $("#modalBody > div.user-hello__remind > form > label").click();
            $("#Modal > div > div > div.modal-header > button > span").click();
        }

        open(projectUrl + "/offer/new");

        if ($("label.st-checkbox").is(visible)) {
            $("#modalBody > div.user-hello__remind > form > label").click();
            $("#Modal > div > div > div.modal-header > button > span").click();
        }

        $("#s2id_offer_version_offer_kind_type").click();
        $$(withText(offerKindNameEn)).find(visible).click();
        $("#offer_version_crm_group_id").setValue(crmGroupId);
        $("#offer_version_sending_conversion_url").setValue(offerUrlSendConversion);
        $("#offer_version_sending_conversion_status_url").setValue(offerUrlSendConversionStatus);

        $("[for=offer_version_countries_0]").click();
        $("[for=offer_version_target_groups_co_is_checked]").click();
        $("#target_group_offer_version_target_groups_co_wrapper > div:nth-child(2) > div:nth-child(2) > label:nth-child(2)").click();
        $("#target_group_offer_version_target_groups_co_wrapper > div:nth-child(6) > div:nth-child(2) > label:nth-child(2)").click();
        $("input[type=submit]").click();

        open(projectUrl + "/logout");
        $(byText("Leadbit.com - international cpa-network")).should(exist);

        $("input[name=_username]").setValue(userAdministratorLogin);
        $("input[name=_password]").setValue(userAdministratorPassword);
        $(".submit").click();

        open(projectUrl+"/?login_as_leadbit_user=admin@cpa.prod");
        $(byText("Dashboard - Leadbit.com")).should(exist);

        open(projectUrl+"/offer/");
        $("#OfferKindFilter_title").setValue(offerKindNameEn).pressEnter();
        $(byXpath("//div[contains(text(),"+offerKindNameEn+")]")).click();
        offerEditLink = $(byXpath("//a[contains(text(),'"+offerKindNameEn+"')]")).getAttribute("href");

        open(offerEditLink+"/edit");
        $("#s2id_offer_version_order_service").click();
        $(byXpath("//div[contains(text(),'API Type 8')]")).click();
        $("input[type=submit]").click();

        open(projectUrl + "/logout");
        $(byText("Leadbit.com - international cpa-network")).should(exist);
    }

    @Test(priority = 50)
    public void sourceCreationTest() {
        open(projectUrl);
        $(byText("Leadbit.com - international cpa-network")).should(exist);

        $("input[name=_username]").setValue(newWebmasterMail);
        $("input[name=_password]").setValue(newWebmasterPass);
        $(".submit").click();
        $(byText("Dashboard - Leadbit.com")).should(exist);

        if ($("label.st-checkbox").is(visible)) {
            $("#modalBody > div.user-hello__remind > form > label").click();
            $("#Modal > div > div > div.modal-header > button > span").click();
        }

        open(projectUrl+"/site/new");

        if ($("label.st-checkbox").is(visible)) {
            $("#modalBody > div.user-hello__remind > form > label").click();
            $("#Modal > div > div > div.modal-header > button > span").click();
        }

        $("#s2id_site_version_type").click();
        $(byXpath("//div[contains(text(),'Web-site')]")).click();
        $("#site_version_title").setValue(sourceName);

        $("input[type=submit]").click();
        $(byText("Sources - Leadbit.com")).should(exist);

        if ($("label.st-checkbox").is(visible)) {
            $("#modalBody > div.user-hello__remind > form > label").click();
            $("#Modal > div > div > div.modal-header > button > span").click();
        }

        open(projectUrl + "/logout");
        $(byText("Leadbit.com - international cpa-network")).should(exist);
    }

    @Test(priority = 51)
    public void offerConnectionTest(){
        open(projectUrl);
        $(byText("Leadbit.com - international cpa-network")).should(exist);

        $("input[name=_username]").setValue(newWebmasterMail);
        $("input[name=_password]").setValue(newWebmasterPass);
        $(".submit").click();
        $(byText("Dashboard - Leadbit.com")).should(exist);

        open(projectUrl+"/wm-offer");

        $("#OfferKindFilter_title").setValue(offerKindNameEn).pressEnter();
        //open(projectUrl+$$("div a").find(exactText("Create Tracking URL")).click());
        $("div a.js-show-modal").click();
        $("input[type=submit]").click();

        open(projectUrl + "/logout");
        $(byText("Leadbit.com - international cpa-network")).should(exist);
    }

    @Test(priority = 52)
    public void flowCreationTest(){
        open(projectUrl);
        $(byText("Leadbit.com - international cpa-network")).should(exist);

        $("input[name=_username]").setValue(newWebmasterMail);
        $("input[name=_password]").setValue(newWebmasterPass);
        $(".submit").click();
        $(byText("Dashboard - Leadbit.com")).should(exist);

        open(projectUrl+"/flow/new");

        $("#flow_title").setValue(flowTitle);
        $("#s2id_flow_offer_kind").click();
        $(byXpath("//div[contains(text(),'"+offerKindNameEn+"')]")).click();
        $("#s2id_flow_site").click();
        $(byXpath("//div[contains(text(),'"+sourceName+"')]")).click();
        $(".checkbox").click();
        $("[for=flow_landing_pages_0]").click();
        $("#flow_redirect_to_mobile_landing").click();
        $("[for=flow_mobileLandings_0]").click();

        $("input[type=submit]").click();

        open(projectUrl + "/logout");
        $(byText("Leadbit.com - international cpa-network")).should(exist);
    }

    @Test(priority = 60)
    public void leadGenerationTest() throws java.io.IOException {

        System.out.println(newWebmasterMail);
        System.out.println(newWebmasterPass);

        open(projectUrl);
        $(byText("Leadbit.com - international cpa-network")).should(exist);

        $("input[name=_username]").setValue(newWebmasterMail);
        $("input[name=_password]").setValue(newWebmasterPass);
        $(".submit").click();
        $(byText("Dashboard - Leadbit.com")).should(exist);

        open(projectUrl+"/flow/");
        $(byText("Tracking URL")).should(exist);

        flowLink = $(".table-result > table:nth-child(1) > tbody:nth-child(2) > tr:nth-child(2) > td:nth-child(4)").getText();
        flowHash = flowLink.substring(flowLink.indexOf("biz/")+4);

        System.out.println(flowLink);
        System.out.println(flowHash);
        open(flowLink);
        try {
            TID1 = url();
            timestamp = System.currentTimeMillis()/1000-2;
            TID1=TID1.substring(url().indexOf("TID=")+4);
            $("[name=name]").setValue(leadCustomerName);
            $("[name=phone]").setValue(leadCustomerPhone);
            $("[type=submit]").click();
        } catch (Exception e) {
            System.out.println(getResponseCode(flowLink));
        }
    }

    @Test(priority = 62)
    public void leadReceivingCheck() {
        open(projectUrl+"/api/check-order/"+apiKey+"?tid="+TID1+"&phone="+leadCustomerPhone+ "&name="+leadCustomerName+"&timestamp="+timestamp);
        $(byText("1")).shouldBe(visible);
        System.out.println("lead received");
    }

    @Test(priority = 63)
    public void mobileLeadGenerationTest() throws IOException {
        WebDriver firefoxDriverMobile = getFirefoxMobileWebDriver();

        firefoxDriverMobile.get(flowLink);

        TID2 = firefoxDriverMobile.getCurrentUrl();
        timestamp = System.currentTimeMillis()/1000-2;
        TID2=TID2.substring(firefoxDriverMobile.getCurrentUrl().indexOf("TID=")+4);

        firefoxDriverMobile.findElement(By.cssSelector("[name=name]")).sendKeys(leadCustomerName+"mobile");
        firefoxDriverMobile.findElement(By.cssSelector("[name=phone]")).sendKeys(leadCustomerPhone);
        firefoxDriverMobile.findElement(By.cssSelector("[type=submit]")).click();
        firefoxDriverMobile.close();
    }

    @Test(priority = 64)
    public void mobileLeadReceivingCheck() {
        open(projectUrl+"/api/check-order/"+apiKey+"?tid="+TID2+"&phone="+leadCustomerPhone+ "&name="+leadCustomerName+"mobile"+"&timestamp="+timestamp);
        $(byText("1")).shouldBe(visible);
        System.out.println("mobile lead received");
    }

    @Test(priority = 80)
    public void mongoDBLeadReceivingTest() {

    }

    @Test(priority = 65)
    public void apiLeadGenerationTest() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        int responseStatusCode = getResponseCode(projectUrl+"/api/new-order/"+apiKey);

        if (!((responseStatusCode >= 200 && responseStatusCode < 300) || responseStatusCode == 304))
            System.out.println("Invalid response status code: " + responseStatusCode);

        HttpPost httpPost = new HttpPost(projectUrl+"/api/new-order/"+apiKey);
        System.out.println(projectUrl+"/api/new-order/"+apiKey);

        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("flow_hash", flowHash));
        params.add(new BasicNameValuePair("landing", devLandingFito));
        params.add(new BasicNameValuePair("referrer", leadReferer));
        params.add(new BasicNameValuePair("phone", leadCustomerPhone));
        params.add(new BasicNameValuePair("name", leadCustomerName));
        params.add(new BasicNameValuePair("sub1", "subac1"));
        params.add(new BasicNameValuePair("sub2", "subac2"));
        params.add(new BasicNameValuePair("sub3", "subac3"));
        params.add(new BasicNameValuePair("sub4", "subac4"));
        params.add(new BasicNameValuePair("sub5", "subac5"));

        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        HttpResponse Response = httpclient.execute(httpPost);

        System.out.println(Response.getStatusLine());

        httpclient.close();
    }

    @Test(priority = 70)
    public void leadReceivingCheckTest() {
        WebDriver firefoxDriverMobile = getFirefoxMobileWebDriver();

        open(projectUrl+"/api/check-order/"+apiKey+"?tid="+TID2+"&phone="+leadCustomerPhone+ "&name="+leadCustomerName+"mobile"+"&timestamp="+timestamp);

        MongoClient mongoClient = new MongoClient("dev.leadbit.com", 27017);

        MongoDatabase database = mongoClient.getDatabase("cpa_stats");

        MongoCollection<Document> collection = database.getCollection("tracker_lead");

        Document myDoc = collection.find(and(eq("check_id", TID1), eq("order.order_data.data.name", leadCustomerName), gte("order.created_at", timestamp))).first();

        TID2 = firefoxDriverMobile.getCurrentUrl();
        timestamp = System.currentTimeMillis()/1000-2;
        TID2=TID2.substring(firefoxDriverMobile.getCurrentUrl().indexOf("TID=")+4);

        String mongoDbRequest = myDoc.toString();
        String check_id;

        System.out.println(mongoDbRequest.substring(mongoDbRequest.indexOf("check_id=")+9, mongoDbRequest.indexOf(",")));

        System.out.println(myDoc);

        mongoClient.close();

    }

    @Test(priority = 1500)   //Включается только когда надо следить за статистикой.
    public void wmStatisticViewTest() throws IOException, InterruptedException {
        int subsIterations = 1;
        final int timer = 150;
        final int iterations = 1000000;
        int i = 1;
        String totalConversion;
        final String loginAsUserName = "email@grr.la";

        open(projectUrl);
        $(byText("Leadbit.com - international cpa-network")).should(exist);

        $("input[name=_username]").setValue(userAdministratorLogin);
        $("input[name=_password]").setValue(userAdministratorPassword);
        $(".submit").click();
        $(byText("Dashboard - Leadbit.com")).should(exist);

        open(projectUrl+"/?login_as_leadbit_user="+loginAsUserName);
        System.out.println();
        System.out.println("Testing statistics of user: "+loginAsUserName);
        
        open(projectUrl+"/finance/currency/1/default");

        open(projectUrl+"/stats/?track_webmaster_filter%5Baggregation_type%5D=created_at");
        try {
            $("tr:nth-child(3) > td:nth-child(6)").getText();
        } catch (Exception e) {
            System.out.println(title());
        }
        totalConversion = $("tr:nth-child(3) > td:nth-child(6)").getText();
        System.out.print(
                $("tr:nth-child(3) > td:nth-child(1)").getText()+"  |  "+
                $("tr:nth-child(3) > td:nth-child(2)").getText()+"  "+
                $("tr:nth-child(3) > td:nth-child(3)").getText()+"  "+
                $("tr:nth-child(3) > td:nth-child(4)").getText()+"  |  "+
                $("tr:nth-child(3) > td:nth-child(5)").getText()+"  "+
                $("tr:nth-child(3) > td:nth-child(6)").getText()+"  "+
                $("tr:nth-child(3) > td:nth-child(7)").getText()+"  "+
                $("tr:nth-child(3) > td:nth-child(8)").getText()+"  "+
                $("tr:nth-child(3) > td:nth-child(9) > a:nth-child(1)").getText()+
                $("tr:nth-child(3) > td:nth-child(9) > a:nth-child(2) > small").getText()+"  |  "+
                $("tr:nth-child(3) > td:nth-child(10)").getText()+"  "+
                $("tr:nth-child(3) > td:nth-child(11)").getText()+"  "+
                $("tr:nth-child(3) > td:nth-child(12)").getText()+"  |  "
        );
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
        System.out.println(ft.format(dNow));
        System.out.println();

        while (i<iterations) {

            sleep(timer*1000);

            open(projectUrl+"/stats/?track_webmaster_filter%5Baggregation_type%5D=created_at");

            try {
                if (!$("tr:nth-child(3) > td:nth-child(6)").getText().equals(totalConversion)) {
                    totalConversion = $("tr:nth-child(3) > td:nth-child(6)").getText();

                    System.out.print(
                            $("tr:nth-child(3) > td:nth-child(1)").getText()+"  |  "+
                            $("tr:nth-child(3) > td:nth-child(2)").getText()+"  "+
                            $("tr:nth-child(3) > td:nth-child(3)").getText()+"  "+
                            $("tr:nth-child(3) > td:nth-child(4)").getText()+"  |  "+
                            $("tr:nth-child(3) > td:nth-child(5)").getText()+"  "+
                            totalConversion+"  "+
                            $("tr:nth-child(3) > td:nth-child(7)").getText()+"  "+
                            $("tr:nth-child(3) > td:nth-child(8)").getText()+"  "+
                            $("tr:nth-child(3) > td:nth-child(9) > a:nth-child(1)").getText()+
                            $("tr:nth-child(3) > td:nth-child(9) > a:nth-child(2) > small").getText()+"  |  "+
                            $("tr:nth-child(3) > td:nth-child(10)").getText()+"  "+
                            $("tr:nth-child(3) > td:nth-child(11)").getText()+"  "+
                            $("tr:nth-child(3) > td:nth-child(12)").getText()+"  |  "
                    );

                    dNow = new Date();
                    System.out.println(ft.format(dNow)+"  Statistics by Date for: "+loginAsUserName);
                }
            } catch (Exception e) {
                int responseStatusCode = getResponseCode(projectUrl);
                System.out.println(responseStatusCode);
            }
            startTime = System.currentTimeMillis();
            open(projectUrl+"/stats/?track_webmaster_filter%5Baggregation_type%5D=sub1%2Bsub2%2Bsub3%2Bsub4%2Bsub5");
            endTime = System.currentTimeMillis();
            duration = (endTime - startTime);
            System.out.println(duration/1000 + " seconds subs");
            i++;
        }
    }
}