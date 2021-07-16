package test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.JdbcDatabaseContainer;
import page.CardInfoForm;
import page.GeneralPageElements;

import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Happy Path")
@ExtendWith(DatabaseInvocationContextProvider.class)
public class TourPurchaseTest {
    private static GeneralPageElements mainPage;
    private static CardInfoForm cardInfo;
    private static String appUrl;
    private static String dbUrl;

    private static String paymentTable = "payment_entity";
    private static String creditTable = "credit_request_entity";
    private static String orderTable = "order_entity";
    private static final String approved = "APPROVED";
    private static final String declined = "DECLINED";

    private long initialCreditCount;
    private long initialPaymentCount;
    private long initialDebitCount;
    private long actualCreditCount;
    private long actualPaymentCount;
    private long actualDebitCount;

    //todo field warnings do not clear after input has been corrected

    @BeforeAll
    static void headless() {
        Configuration.headless = true;
    }

    public void setUp(JdbcDatabaseContainer database) {
        appUrl = ContainerHelper.setUp(database);
        dbUrl = ContainerHelper.getDbUrl();
        open("http://" + appUrl);
        mainPage = new GeneralPageElements();

    }

    @TestTemplate
    @DisplayName("Debit Approved")
    void debitApproved(JdbcDatabaseContainer database) throws SQLException {
        setUp(database);
        //get numbers of entries in relevant tables
        initialPaymentCount = DBHelper.countLinesInDB("status", paymentTable, dbUrl);
        initialDebitCount = DBHelper.countLinesInDB("payment_id", orderTable, dbUrl);

        cardInfo = mainPage.buyTour();
        mainPage = CardInfoForm.inputValidInfo(true, cardInfo);

        mainPage.assertSuccess();
        //get numbers of entries in relevant bases again to compare with the initial ones
        actualPaymentCount = DBHelper.countLinesInDB("status", paymentTable, dbUrl);
        actualDebitCount = DBHelper.countLinesInDB("payment_id", orderTable, dbUrl);
        assertAll(
                () -> assertEquals(initialDebitCount + 1, actualDebitCount),
                () -> assertEquals(initialPaymentCount + 1, actualPaymentCount),
                () -> assertEquals(approved, DBHelper.seePaymentStatus(dbUrl)));
    }

    @TestTemplate
    @DisplayName("Debit Declined")
    public void debitDeclined(JdbcDatabaseContainer database) throws SQLException {
        setUp(database);
        initialPaymentCount = DBHelper.countLinesInDB("status", paymentTable, dbUrl);
        initialDebitCount = DBHelper.countLinesInDB("payment_id", orderTable, dbUrl);

        cardInfo = mainPage.buyTour();
        mainPage = CardInfoForm.inputValidInfo(false, cardInfo);

        mainPage.assertError();//frontend error; the following asserts pass
        actualPaymentCount = DBHelper.countLinesInDB("status", paymentTable, dbUrl);
        actualDebitCount = DBHelper.countLinesInDB("payment_id", orderTable, dbUrl);
        assertAll(
                () -> assertEquals(initialDebitCount + 1, actualDebitCount),
                () -> assertEquals(initialPaymentCount + 1, actualPaymentCount),
                () -> assertEquals(declined, DBHelper.seePaymentStatus(dbUrl)));
    }

    @TestTemplate
    @DisplayName("Credit Approved")
    public void creditApproved(JdbcDatabaseContainer database) throws SQLException {
        setUp(database);
        initialCreditCount = DBHelper.countLinesInDB("id", creditTable, dbUrl);

        cardInfo = mainPage.creditTour();
        mainPage = CardInfoForm.inputValidInfo(true, cardInfo);

        mainPage.assertSuccess();
        actualCreditCount = DBHelper.countLinesInDB("id", creditTable, dbUrl);
        assertAll(
                () -> assertEquals(initialCreditCount + 1, actualCreditCount),
                () -> assertEquals(approved, DBHelper.seeCreditStatus(dbUrl)));
    }

    @TestTemplate
    @DisplayName("Credit Declined")
    public void creditDeclined(JdbcDatabaseContainer database) throws SQLException {
        setUp(database);
        initialCreditCount = DBHelper.countLinesInDB("id", creditTable, dbUrl);

        cardInfo = mainPage.creditTour();
        mainPage = CardInfoForm.inputValidInfo(false, cardInfo);
        mainPage.assertError(); //frontend error; the following asserts pass

        actualCreditCount = DBHelper.countLinesInDB("id", creditTable, dbUrl);
        assertAll(
                () -> assertEquals(initialCreditCount + 1, actualCreditCount),
                () -> assertEquals(declined, DBHelper.seeCreditStatus(dbUrl)));
    }

}
