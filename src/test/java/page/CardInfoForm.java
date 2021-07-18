package page;

import com.codeborne.selenide.SelenideElement;
import data.CardDataGenerator;

import java.util.Calendar;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class CardInfoForm {
    SelenideElement cardNumberField = $(".input__control[placeholder='0000 0000 0000 0000']");
    SelenideElement monthField = $(".input__control[placeholder='08']");
    SelenideElement yearField = $(".input__control[placeholder='22']");
    SelenideElement nameField = $x("//span[text()='Владелец']/../../span[@class='input__inner']/span[@class='input__box']/input");
    SelenideElement cvcField = $(".input__control[placeholder='999']");
    SelenideElement continueButton = $x("//button/span/span[text()='Продолжить']");
    SelenideElement heading = $(".heading");

    static SelenideElement fieldError = $(".input__sub");
    CardDataGenerator dataGen = new CardDataGenerator();
    String approvedCardNumber = "4444444444444441";
    String declinedCardNumber = "4444444444444442";
    Calendar cal = Calendar.getInstance();
    int currentMonth = cal.get(Calendar.MONTH) + 1;
    int currentYear = cal.get(Calendar.YEAR) - 2000;

    public CardInfoForm() {
        heading.shouldBe(visible);
    }

    /*error messages .input__sub or .input_invalid
    Неверный формат
    Поле обязательно для заполнения
     */
    public static void assertFieldFormatError() {
        fieldError.shouldHave(text("Неверный формат"));
    }

    public static void assertExpiredError() {
        fieldError.shouldHave(text("Истёк срок действия карты"));
    }

    public static void assertInvalidError() {
        fieldError.shouldHave(text("Неверно указан срок действия карты"));
    }

    public static void assertFieldEmptyError() {
        fieldError.shouldHave(text("Поле обязательно для заполнения"));
    }

    public static void assertNoErrors() {
        fieldError.shouldNotBe(visible);
    }

    public static GeneralPageElements inputValidInfo(boolean approved, CardInfoForm cardInfo) {
        return cardInfo.inputNumber(approved)
                .inputValidDate()
                .inputValidName("en")
                .inputValidCvc()
                .clickContinue();
    }

    public CardInfoForm inputNumber(boolean approved) {
        if (approved) {
            cardNumberField.setValue(approvedCardNumber);
        } else {
            cardNumberField.setValue(declinedCardNumber);
        }
        return this;
    }

    public CardInfoForm inputInvalidNumber(String input) {
        cardNumberField.setValue(input);
        return this;
    }

    public CardInfoForm inputValidDate() {
        int year = CardDataGenerator.DateGenerator.RandomValidYear(currentYear + 5, currentYear);
        String month;
        if (year == currentYear) {
            month = CardDataGenerator.DateGenerator.RandomValidMonth(currentMonth);
        } else {
            month = CardDataGenerator.DateGenerator.RandomValidMonth(1);
        }
        inputMonth(month);
        inputYear(String.valueOf(year));
        return this;
    }

    public CardInfoForm inputValidMonth() {
        String month = CardDataGenerator.DateGenerator.RandomValidMonth(1);
        inputMonth(month);
        return this;
    }

    public CardInfoForm inputValidYear() {
        int year = CardDataGenerator.DateGenerator.RandomValidYear(currentYear + 5, currentYear);
        inputYear(String.valueOf(year));
        return this;
    }

    public CardInfoForm inputYear(String year) {
        yearField.setValue(year);
        return this;
    }

    public CardInfoForm inputMonth(String month) {
        monthField.setValue(month);
        return this;
    }

    public CardInfoForm inputValidName(String locale) {
        nameField.setValue(dataGen.generateRandomValidName(locale));
        return this;
    }

    public CardInfoForm inputInvalidName(String input) {
        nameField.setValue(input);
        return this;
    }

    public CardInfoForm inputValidCvc() {
        cvcField.setValue(dataGen.generateRandomCvcCode());
        return this;
    }

    public CardInfoForm inputInvalidCvc(String input) {
        cvcField.setValue(input);
        return this;
    }

    public GeneralPageElements clickContinue() {
        continueButton.click();
        return new GeneralPageElements();
    }

    public CardInfoForm inputLastYear() {
        yearField.setValue(String.valueOf(currentYear - 1));
        return this;
    }

    public CardInfoForm inputThisYear() {
        yearField.setValue(String.valueOf(currentYear));
        return this;
    }

    public CardInfoForm inputNextYear() {
        yearField.setValue(String.valueOf(currentYear + 1));
        return this;
    }

    public String monthHelper(int month) {
        if (month < 10) {
            return "0" + month;
        } else {
            return String.valueOf(month);
        }
    }

    public CardInfoForm inputLastMonth() {
        String month = monthHelper(currentMonth - 1);
        monthField.setValue(month);
        return this;
    }

    public CardInfoForm inputThisMonth() {
        String month = monthHelper(currentMonth);
        monthField.setValue(month);
        return this;
    }

    public CardInfoForm inputNextMonth() {
        String month = monthHelper(currentMonth + 1);
        monthField.setValue(month);
        return this;
    }

}
