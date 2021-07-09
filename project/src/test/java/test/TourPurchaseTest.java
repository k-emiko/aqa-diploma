package test;

import com.codeborne.selenide.Configuration;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.*;
import page.CardInfoForm;
import page.GeneralPageElements;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TourPurchaseTest {
    public static GeneralPageElements mainPage;
    public static CardInfoForm cardInfo;
    public static String appUrl = "localhost:8080";
    public static String dbUrl = "localhost:3306";
    public static String linesInDB = "SELECT COUNT(?) FROM ?;";
    public static String paymentStatus = "SELECT status FROM payment_entity " +
            "WHERE transaction_id=" +
            "(SELECT payment_id FROM order_entity " +
            "WHERE created=" +
            "(select max(created) FROM order_entity));";

    public static String creditStatus = "SELECT status FROM credit_request_entity " +
            "WHERE created=" +
            "(SELECT max(created) FROM credit_request_entity);";

    public static String paymentTable = "payment_entity";
    public static String creditTable = "credit_request_entity";
    public static String orderTable = "order_entity";
    public static final String approved = "APPROVED";
    public static final String declined = "DECLINED";

    //todo field warnings do not clear after input has been corrected
    private static void inputValidInfo(boolean approved) {
        mainPage = cardInfo.inputNumber(approved)
                .inputValidDate()
                .inputValidName("en")
                .inputValidCvc()
                .clickContinue();
    }

    private static long countLinesInDB(String column, String table) throws SQLException {
        QueryRunner runner = new QueryRunner();
        long result;
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://" + dbUrl, "app", "pass")
        ) {
            runner.execute(conn, "use app;");
            result = runner.query(conn, "SELECT COUNT(?) FROM ?;", new ScalarHandler<>(), column, table);//this line cases SQL syntax error
            //result = runner.query(conn, "SELECT COUNT(" + column + ") FROM " + table + ";", new ScalarHandler<>());//working line
        }//todo figure out why the ? thing doesn't work
        return result;
    }

    private static String seePaymentStatus() throws SQLException {
        QueryRunner runner = new QueryRunner();
        String result;
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://" + dbUrl, "app", "pass")
        ) {
            runner.execute(conn, "use app;");
            result = runner.query(conn,
                    "select status from payment_entity " +
                            "where transaction_id=" +
                                "(select payment_id from order_entity " +
                                    "where created=" +
                                        "(select max(created) from order_entity));",
                    new ScalarHandler<>());
        }
        return result;
    }

    private static String seeCreditStatus() throws SQLException {
        QueryRunner runner = new QueryRunner();
        String result;
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://" + dbUrl, "app", "pass")
        ) {
            runner.execute(conn, "use app;");
            result = runner.query(conn,
                    "select status from credit_request_entity " +
                            "where created=" +
                                "(select max(created) from credit_request_entity);",
                    new ScalarHandler<>());
        }
        return result;
    }

    @DisplayName("Happy Path")
    public static class Happy {
        @BeforeAll
        static void headless() {
            Configuration.headless = true;
        }

        @BeforeEach
        public void setUp(){
            open("http://" + appUrl);
            mainPage = new GeneralPageElements();
        }

        @Test
        void debitApproved() throws SQLException {
            //get numbers of entries in relevant tables
            long initialPaymentCount = countLinesInDB("status", paymentTable);
            long initialDebitCount = countLinesInDB("payment_id", orderTable);

            cardInfo = mainPage.buyTour();
            inputValidInfo(true);

            mainPage.assertSuccess();
            //get numbers of entries in relevant bases again to compare with the initial ones
            long actualPaymentCount = countLinesInDB("status", paymentTable);
            long actualDebitCount = countLinesInDB("payment_id", orderTable);
            assertEquals(initialDebitCount + 1, actualDebitCount);
            assertEquals(initialPaymentCount + 1, actualPaymentCount);
            assertEquals(approved, seePaymentStatus());
        }

        @Test
        public void debitDeclined()throws SQLException {
            long initialPaymentCount = countLinesInDB("status", paymentTable);
            long initialDebitCount = countLinesInDB("payment_id", orderTable);

            cardInfo = mainPage.buyTour();
            inputValidInfo(false);

            mainPage.assertError();//frontend error; the following asserts pass
            long actualPaymentCount = countLinesInDB("status", paymentTable);
            long actualDebitCount = countLinesInDB("payment_id", orderTable);
            assertEquals(initialDebitCount + 1, actualDebitCount);
            assertEquals(initialPaymentCount + 1, actualPaymentCount);
            assertEquals(declined, seePaymentStatus());
        }

        @Test
        public void creditApproved() throws SQLException {
            long initialCreditCount = countLinesInDB("id", creditTable);

            cardInfo = mainPage.creditTour();
            inputValidInfo(true);

            mainPage.assertSuccess();
            long actualCreditCount = countLinesInDB("id", creditTable);
            assertEquals(initialCreditCount + 1, actualCreditCount);
            assertEquals(approved, seeCreditStatus());
        }

        @Test
        public void creditDeclined() throws SQLException {
            long initialCreditCount = countLinesInDB("id", creditTable);
            cardInfo = mainPage.creditTour();
            inputValidInfo(false);

            mainPage.assertError(); //frontend error; the following asserts pass
            long actualCreditCount = countLinesInDB("id", creditTable);
            assertEquals(initialCreditCount + 1, actualCreditCount);
            assertEquals(declined, seeCreditStatus());
        }

    }

}
