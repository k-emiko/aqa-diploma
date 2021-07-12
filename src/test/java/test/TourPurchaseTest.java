package test;

import com.codeborne.selenide.Configuration;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import page.CardInfoForm;
import page.GeneralPageElements;

import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Happy Path")
@ExtendWith(DatabaseInvocationContextProvider.class)
public class TourPurchaseTest {
    public static GeneralPageElements mainPage;
    public static CardInfoForm cardInfo;
    public static String appUrl;
    public static String dbUrl;

    public static String paymentTable = "payment_entity";
    public static String creditTable = "credit_request_entity";
    public static String orderTable = "order_entity";
    public static final String approved = "APPROVED";
    public static final String declined = "DECLINED";

    private long initialCreditCount;
    private long initialPaymentCount;
    private long initialDebitCount;
    private long actualCreditCount;
    private long actualPaymentCount;
    private long actualDebitCount;

    @Rule
    public static GenericContainer appContMYSQL =
            new GenericContainer(new ImageFromDockerfile("app-mysql")
                    .withDockerfile(Paths.get("artifacts/app-mysql/Dockerfile")));
    @Rule
    public static GenericContainer appContPSQL =
            new GenericContainer(new ImageFromDockerfile("app-psql")
                    .withDockerfile(Paths.get("artifacts/app-psql/Dockerfile")));
    @Rule
    public static GenericContainer paymentSimulator =
            new GenericContainer(new ImageFromDockerfile("payment-simulator")
                    .withDockerfile(Paths.get("artifacts/gate-simulator/Dockerfile")));

    //todo field warnings do not clear after input has been corrected

    @BeforeAll
    static void headless() {
        Configuration.headless = true;
    }


    @TestTemplate
    @DisplayName("Debit Approved")
    void debitApproved(JdbcDatabaseContainer database) throws SQLException {
        setUp(database);
        //get numbers of entries in relevant tables
        initialPaymentCount = countLinesInDB("status", paymentTable);
        initialDebitCount = countLinesInDB("payment_id", orderTable);

        cardInfo = mainPage.buyTour();
        inputValidInfo(true);

        mainPage.assertSuccess();
        //get numbers of entries in relevant bases again to compare with the initial ones
        actualPaymentCount = countLinesInDB("status", paymentTable);
        actualDebitCount = countLinesInDB("payment_id", orderTable);
        assertAll(
                () -> assertEquals(initialDebitCount + 1, actualDebitCount),
                () -> assertEquals(initialPaymentCount + 1, actualPaymentCount),
                () -> assertEquals(approved, seePaymentStatus()));
    }

    @TestTemplate
    @DisplayName("Debit Declined")
    public void debitDeclined(JdbcDatabaseContainer database) throws SQLException {
        setUp(database);
        initialPaymentCount = countLinesInDB("status", paymentTable);
        initialDebitCount = countLinesInDB("payment_id", orderTable);

        cardInfo = mainPage.buyTour();
        inputValidInfo(false);

        mainPage.assertError();//frontend error; the following asserts pass
        actualPaymentCount = countLinesInDB("status", paymentTable);
        actualDebitCount = countLinesInDB("payment_id", orderTable);
        assertAll(
                () -> assertEquals(initialDebitCount + 1, actualDebitCount),
                () -> assertEquals(initialPaymentCount + 1, actualPaymentCount),
                () -> assertEquals(declined, seePaymentStatus()));
    }

    @TestTemplate
    @DisplayName("Credit Approved")
    public void creditApproved(JdbcDatabaseContainer database) throws SQLException {
        setUp(database);
        initialCreditCount = countLinesInDB("id", creditTable);

        cardInfo = mainPage.creditTour();
        inputValidInfo(true);

        mainPage.assertSuccess();
        actualCreditCount = countLinesInDB("id", creditTable);
        assertAll(
                () -> assertEquals(initialCreditCount + 1, actualCreditCount),
                () -> assertEquals(approved, seeCreditStatus()));
    }

    @TestTemplate
    @DisplayName("Credit Declined")
    public void creditDeclined(JdbcDatabaseContainer database) throws SQLException {
        setUp(database);
        initialCreditCount = countLinesInDB("id", creditTable);

        cardInfo = mainPage.creditTour();
        inputValidInfo(false);
        mainPage.assertError(); //frontend error; the following asserts pass

        actualCreditCount = countLinesInDB("id", creditTable);
        assertAll(
                () -> assertEquals(initialCreditCount + 1, actualCreditCount),
                () -> assertEquals(declined, seeCreditStatus()));
    }

    public void containerSelector(JdbcDatabaseContainer database) {
        if (dbUrl.contains("mysql")) {
            appContMYSQL
                    .withEnv("TESTCONTAINERS_DB_URL", dbUrl)
                    .withEnv("TESTCONTAINERS_DB_USER", "app")
                    .withEnv("TESTCONTAINERS_DB_PASS", "pass")
                    .withCommand("./wait-for-it.sh --timeout=10 mysql:3306 -- java -jar aqa-shop.jar")
                    .withExposedPorts(8080)
                    .withNetwork(database.getNetwork())
                    .withNetworkAliases("app")
                    .start();
            appUrl = appContMYSQL.getHost() + ":" + appContMYSQL.getMappedPort(8080);
        } else if (dbUrl.contains("postgresql")) {
            appContPSQL
                    .withEnv("TESTCONTAINERS_DB_URL", dbUrl)
                    .withEnv("TESTCONTAINERS_POSTGRES_USER", "app")
                    .withEnv("TESTCONTAINERS_POSTGRES_PASSWORD", "pass")
                    .withCommand("./wait-for-it.sh --timeout=10 psql:5432 -- java -jar aqa-shop.jar")
                    .withExposedPorts(8080)
                    .withNetwork(database.getNetwork())
                    .withNetworkAliases("app")
                    .start();
            appUrl = appContPSQL.getHost() + ":" + appContPSQL.getMappedPort(8080);
        }
    }

    public void setUp(JdbcDatabaseContainer database) {
        dbUrl = database.getJdbcUrl();
        if (dbUrl.contains("?")) {
            dbUrl = database.getJdbcUrl().substring(0, database.getJdbcUrl().indexOf("?"));
        }
        paymentSimulator
                .withNetwork(database.getNetwork())
                .withNetworkAliases("gate-simulator")
                .withExposedPorts(9999)
                .start();
        containerSelector(database);

        open("http://" + appUrl);
        mainPage = new GeneralPageElements();
    }

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
                        dbUrl, "app", "pass")
        ) {
            //result = runner.query(conn, "SELECT COUNT(?) FROM ?;", new ScalarHandler<>(), column, table);//this line cases SQL syntax error
            result = runner.query(conn, "SELECT COUNT(" + column + ") FROM " + table + ";", new ScalarHandler<>());//working line
        }//todo figure out why the ? thing doesn't work
        return result;
    }

    private static String seePaymentStatus() throws SQLException {
        QueryRunner runner = new QueryRunner();
        String result;
        try (
                val conn = DriverManager.getConnection(
                        dbUrl, "app", "pass")
        ) {
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
                        dbUrl, "app", "pass")
        ) {
            result = runner.query(conn,
                    "select status from credit_request_entity " +
                            "where created=" +
                            "(select max(created) from credit_request_entity);",
                    new ScalarHandler<>());
        }
        return result;
    }

}
