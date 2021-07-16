package page;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class GeneralPageElements {
    SelenideElement buttonBuy = $x("//button/span/span[text()='Купить']");
    SelenideElement buttonCredit = $x("//button/span/span[text()='Купить в кредит']");
    SelenideElement successNotification = $(".notification_status_ok");
    SelenideElement declinedNotification = $(".notification_status_error");

    public CardInfoForm buyTour() {
        buttonBuy.click();
        return new CardInfoForm();
    }

    public CardInfoForm creditTour() {
        buttonCredit.click();
        return new CardInfoForm();
    }

    public void assertSuccess() {
        successNotification.shouldBe(visible, Duration.ofSeconds(20));
    }

    public void assertError() {
        declinedNotification.shouldBe(visible, Duration.ofSeconds(20));
    }
}
