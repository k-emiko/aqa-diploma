package test;

import com.codeborne.selenide.Configuration;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.ImageFromDockerfile;
import page.CardInfoForm;
import page.GeneralPageElements;

import java.nio.file.Paths;

import static com.codeborne.selenide.Selenide.open;

public class FrontEndTest {
    private static GeneralPageElements mainPage;
    private static CardInfoForm cardInfo;

    private static String appUrl;
    private static String dbUrl;
    private static Network network = Network.newNetwork();

    @Rule
    public static MySQLContainer database = new MySQLContainer("mysql:8.0.25");
    @Rule
    public static GenericContainer app =
            new GenericContainer(new ImageFromDockerfile("app-mysql")
                    .withDockerfile(Paths.get("artifacts/app-mysql/Dockerfile")));
    @Rule
    public static GenericContainer paymentSim =
            new GenericContainer(new ImageFromDockerfile("payment-simulator")
                    .withDockerfile(Paths.get("artifacts/gate-simulator/Dockerfile")));

    @DisplayName("Date")
    @Nested
    public static class DateFields {

        @BeforeAll
        static void headless() {
            Configuration.headless = true;
            setUpHelper();
        }

        @BeforeEach
        public void setUp() {
            open("http://" + appUrl);
            mainPage = new GeneralPageElements();
            cardInfo = mainPage.buyTour();
            cardInfo.inputNumber(true)
                    .inputValidName("en")
                    .inputValidCvc();
        }

        @Test
        public void currentMonthCurrentYear() {
            cardInfo.inputThisMonth().inputThisYear().clickContinue();
            mainPage.assertSuccess();
        }

        @Test
        public void futureMonthCurrentYear() {
            cardInfo.inputNextMonth().inputThisYear().clickContinue();
            mainPage.assertSuccess();
        }

        @Test
        public void lastMonthCurrentYear() {
            cardInfo.inputLastMonth().inputThisYear().clickContinue();
            CardInfoForm.assertExpiredError();
        }

        @Test
        public void currentMonthLastYear() {
            cardInfo.inputThisMonth().inputLastYear().clickContinue();
            CardInfoForm.assertExpiredError();
        }

        @Test
        public void futureMonthLastYear() {
            cardInfo.inputNextMonth().inputLastYear().clickContinue();
            CardInfoForm.assertExpiredError();
        }

        @Test
        public void lastMonthLastYear() {
            cardInfo.inputLastMonth().inputLastYear().clickContinue();
            CardInfoForm.assertExpiredError();
        }

        @Test
        public void currentMonthNextYear() {
            cardInfo.inputThisMonth().inputNextYear().clickContinue();
            mainPage.assertSuccess();
        }

        @Test
        public void futureMonthNextYear() {
            cardInfo.inputNextMonth().inputNextYear().clickContinue();
            mainPage.assertSuccess();
        }

        @Test
        public void lastMonthNextYear() {
            cardInfo.inputLastMonth().inputNextYear().clickContinue();
            mainPage.assertSuccess();
        }

    }

    @DisplayName("Month Field")
    @Nested
    public static class MonthField {

        @BeforeAll
        static void headless() {
            Configuration.headless = true;
            setUpHelper();
        }

        @BeforeEach
        public void setUp() {
            open("http://" + appUrl);
            mainPage = new GeneralPageElements();
            cardInfo = mainPage.buyTour();
            cardInfo.inputNumber(true)
                    .inputValidName("en")
                    .inputValidYear()
                    .inputValidCvc();
        }

        @Test
        @DisplayName("Month, empty")
        public void empty() {
            cardInfo.clickContinue();
            CardInfoForm.assertFieldEmptyError();
        }

        @ParameterizedTest(name = "{arguments}")
        @CsvFileSource(resources = "/MonthInvalidError.csv", delimiter = '|', numLinesToSkip = 1)
        void negativeInvalidTests(String test, String input) {
            cardInfo.inputMonth(input).clickContinue();
            CardInfoForm.assertInvalidError();
        }

        @ParameterizedTest(name = "{arguments}")
        @CsvFileSource(resources = "/MonthFieldFormatError.csv", delimiter = '|', numLinesToSkip = 1)
        void negativeFormatErrorTests(String test, String input) {
            cardInfo.inputMonth(input).clickContinue();
            CardInfoForm.assertFieldFormatError();
        }

        @Test
        public void oneDigit() {
            cardInfo.inputMonth("1").clickContinue();
            mainPage.assertSuccess();
        }

    }

    @DisplayName("Year Field")
    @Nested
    public static class YearField {

        @BeforeAll
        static void headless() {
            Configuration.headless = true;
            setUpHelper();
        }

        @BeforeEach
        public void setUp() {
            open("http://" + appUrl);
            mainPage = new GeneralPageElements();
            cardInfo = mainPage.buyTour();
            cardInfo.inputNumber(true)
                    .inputValidName("en")
                    .inputValidMonth()
                    .inputValidCvc();
        }

        @Test
        @DisplayName("Year, empty")
        public void empty() {
            cardInfo.clickContinue();
            CardInfoForm.assertFieldEmptyError();
        }

        @ParameterizedTest(name = "{arguments}")
        @CsvFileSource(resources = "/YearExpiredError.csv", delimiter = '|', numLinesToSkip = 1)
        void negativeInvalidTests(String test, String input) {
            cardInfo.inputYear(input).clickContinue();
            CardInfoForm.assertExpiredError();
        }

        @ParameterizedTest(name = "{arguments}")
        @CsvFileSource(resources = "/YearFieldFormatError.csv", delimiter = '|', numLinesToSkip = 1)
        void negativeFormatErrorTests(String test, String input) {
            cardInfo.inputYear(input).clickContinue();
            CardInfoForm.assertFieldFormatError();
        }

    }

    @DisplayName("Card Number Field")
    @Nested
    public static class CardNumberField {

        @BeforeAll
        static void headless() {
            Configuration.headless = true;
            setUpHelper();
        }

        @BeforeEach
        public void setUp() {
            open("http://" + appUrl);
            mainPage = new GeneralPageElements();
            cardInfo = mainPage.buyTour();
            cardInfo.inputValidDate()
                    .inputValidName("en")
                    .inputValidCvc();
        }

        @Test
        @DisplayName("Card, empty")
        public void empty() {
            cardInfo.clickContinue();
            CardInfoForm.assertFieldEmptyError();
        }

        @ParameterizedTest(name = "{arguments}")
        @CsvFileSource(resources = "/CardFieldFormatError.csv", delimiter = '|', numLinesToSkip = 1)
        void negativeFormatErrorTests(String test, String input) {
            cardInfo.inputInvalidNumber(input).clickContinue();
            CardInfoForm.assertFieldFormatError();
        }

    }

    @DisplayName("Name Field")
    @Nested
    public static class NameField {

        @BeforeAll
        static void headless() {
            Configuration.headless = true;
            setUpHelper();
        }

        @BeforeEach
        public void setUp() {
            open("http://" + appUrl);
            mainPage = new GeneralPageElements();
            cardInfo = mainPage.buyTour();
            cardInfo.inputNumber(true)
                    .inputValidDate()
                    .inputValidCvc();
        }

        @Test
        @DisplayName("Name, empty")
        public void empty() {
            cardInfo.clickContinue();
            CardInfoForm.assertFieldEmptyError();
        }

        @ParameterizedTest(name = "{arguments}")
        @CsvFileSource(resources = "/NameFieldFormatError.csv", delimiter = '|', numLinesToSkip = 1)
        void negativeFormatErrorTests(String test, String input) {
            cardInfo.inputInvalidName(input).clickContinue();
            CardInfoForm.assertFieldFormatError();
        }

        @Test
        public void max() {
            cardInfo.inputInvalidName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").clickContinue();
            CardInfoForm.assertFieldFormatError();
        }

    }

    @DisplayName("CVC Field")
    @Nested
    public static class CvcField {

        @BeforeAll
        static void headless() {
            Configuration.headless = true;
            setUpHelper();
        }

        @BeforeEach
        public void setUp() {
            open("http://" + appUrl);
            mainPage = new GeneralPageElements();
            cardInfo = mainPage.buyTour();
            cardInfo.inputNumber(true)
                    .inputValidName("en")
                    .inputValidDate();
        }

        @Test
        @DisplayName("CVC (empty)")
        public void empty() {
            cardInfo.clickContinue();
            CardInfoForm.assertFieldEmptyError();
        }

        @ParameterizedTest(name = "{arguments}")
        @CsvFileSource(resources = "/CvcFieldFormatError.csv", delimiter = '|', numLinesToSkip = 1)
        void negativeFormatErrorTests(String test, String input) {
            cardInfo.inputInvalidCvc(input).clickContinue();
            CardInfoForm.assertFieldFormatError();
        }

        @Test
        @DisplayName("CVC (four digits), 1111")
        public void fourDigits() {
            cardInfo.inputInvalidCvc("1111").clickContinue();
            mainPage.assertSuccess();
        }

    }

    public static void setUpHelper() {
        database
                .withDatabaseName("app")
                .withUsername("app")
                .withPassword("pass")
                .withNetwork(network)
                .withNetworkAliases("mysql")
                .withExposedPorts(3306)
                .start();

        dbUrl = database.getJdbcUrl();
        paymentSim
                .withNetwork(database.getNetwork())
                .withNetworkAliases("gate-simulator")
                .withExposedPorts(9999)
                .start();
        app
                .withEnv("TESTCONTAINERS_DB_USER", "app")
                .withEnv("TESTCONTAINERS_DB_PASS", "pass")
                .withEnv("TESTCONTAINERS_DB_URL", dbUrl)
                .withCommand("./wait-for-it.sh --timeout=10 mysql:3306 -- java -jar aqa-shop.jar")
                .withNetwork(database.getNetwork())
                .withExposedPorts(8080)
                .start();
        appUrl = app.getHost() + ":" + app.getMappedPort(8080);
    }

}
