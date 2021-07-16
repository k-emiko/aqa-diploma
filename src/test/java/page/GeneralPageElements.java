package page;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class GeneralPageElements {
    //todo add difference with text inside: Купить/Купить в кредит //$x("//span[text()='']");
    SelenideElement buttonBuy = $$(".button").get(0);
    SelenideElement buttonCredit = $$(".button").get(1);
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
