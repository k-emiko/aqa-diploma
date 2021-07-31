package helper;

import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {

    public static long countLinesInDB(String column, String table, String dbUrl) {
        QueryRunner runner = new QueryRunner();
        long result = 0;
        try (
                val conn = DriverManager.getConnection(
                        dbUrl, "app", "pass")
        ) {
            //result = runner.query(conn, "SELECT COUNT(?) FROM ?;", new ScalarHandler<>(), column, table);//this line cases SQL syntax error
            result = runner.query(conn, "SELECT COUNT(" + column + ") FROM " + table + ";", new ScalarHandler<>());//working line
        }//todo figure out why the ? thing doesn't work
        catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String seePaymentStatus(String dbUrl) {
        QueryRunner runner = new QueryRunner();
        String result = null;
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String seeCreditStatus(String dbUrl) {
        QueryRunner runner = new QueryRunner();
        String result = null;
        try (
                val conn = DriverManager.getConnection(
                        dbUrl, "app", "pass")
        ) {
            result = runner.query(conn,
                    "select status from credit_request_entity " +
                            "where created=" +
                            "(select max(created) from credit_request_entity);",
                    new ScalarHandler<>());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
