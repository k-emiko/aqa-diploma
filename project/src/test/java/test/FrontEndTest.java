package test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import page.CardInfoForm;
import page.GeneralPageElements;

import static com.codeborne.selenide.Selenide.open;

public class FrontEndTest {
    public static GeneralPageElements mainPage;
    public static CardInfoForm cardInfo;
    public static String appUrl = "localhost:8080";

    @DisplayName("Date")
    public static class DateFields {

        @BeforeAll
        static void headless() {
            Configuration.headless = true;
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
    public static class MonthField {

        @BeforeAll
        static void headless() {
            Configuration.headless = true;
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
        public void empty() {
            cardInfo.clickContinue();
            CardInfoForm.assertFieldEmptyError();
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/MonthInvalidError.csv", delimiter = '|', numLinesToSkip = 1)
        void negativeInvalidTests(String test, String input) {
            cardInfo.inputMonth(input).clickContinue();
            CardInfoForm.assertInvalidError();
        }

        @ParameterizedTest @CsvFileSource(resources = "/MonthFieldFormatError.csv", delimiter = '|', numLinesToSkip = 1)
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
    public static class YearField {

        @BeforeAll
        static void headless() {
            Configuration.headless = true;
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
        public void empty() {
            cardInfo.clickContinue();
            CardInfoForm.assertFieldEmptyError();
        }

        @ParameterizedTest @CsvFileSource(resources = "/YearExpiredError.csv", delimiter = '|', numLinesToSkip = 1)
        void negativeInvalidTests(String test, String input) {
            cardInfo.inputYear(input).clickContinue();
            CardInfoForm.assertExpiredError();
        }

        @ParameterizedTest @CsvFileSource(resources = "/YearFieldFormatError.csv", delimiter = '|', numLinesToSkip = 1)
        void negativeFormatErrorTests(String test, String input) {
            cardInfo.inputYear(input).clickContinue();
            CardInfoForm.assertFieldFormatError();
        }

    }

    @DisplayName("Card Number Field")
    public static class CardNumberField {

        @BeforeAll
        static void headless() {
            Configuration.headless = true;
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
        public void empty() {
            cardInfo.clickContinue();
            CardInfoForm.assertFieldEmptyError();
        }

        @ParameterizedTest @CsvFileSource(resources = "/CardFieldFormatError.csv", delimiter = '|', numLinesToSkip = 1)
        void negativeFormatErrorTests(String test, String input) {
            cardInfo.inputInvalidNumber(input).clickContinue();
            CardInfoForm.assertFieldFormatError();
        }

    }

    @DisplayName("Name Field")
    public static class NameField {

        @BeforeAll
        static void headless() {
            Configuration.headless = true;
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
        public void empty() {
            cardInfo.clickContinue();
            CardInfoForm.assertFieldEmptyError();
        }

        @ParameterizedTest @CsvFileSource(resources = "/NameFieldFormatError.csv", delimiter = '|', numLinesToSkip = 1)
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
    public static class CvcField {

        @BeforeAll
        static void headless() {
            Configuration.headless = true;
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
        public void empty() {
            cardInfo.clickContinue();
            CardInfoForm.assertFieldEmptyError();
        }

        @ParameterizedTest @CsvFileSource(resources = "/CvcFieldFormatError.csv", delimiter = '|', numLinesToSkip = 1)
        void negativeFormatErrorTests(String test, String input) {
            cardInfo.inputInvalidCvc(input).clickContinue();
            CardInfoForm.assertFieldFormatError();
        }

        @Test
        public void fourDigits() {
            cardInfo.inputInvalidCvc("1111").clickContinue();
            mainPage.assertSuccess();
        }

    }
}
