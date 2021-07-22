package helper;

import org.junit.Rule;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Paths;

public class ContainerHelper {
    @Rule
    private static GenericContainer appContMYSQL =
            new GenericContainer(new ImageFromDockerfile("app-mysql")
                    .withDockerfile(Paths.get("artifacts/app-mysql/Dockerfile")));
    @Rule
    private static GenericContainer appContPSQL =
            new GenericContainer(new ImageFromDockerfile("app-psql")
                    .withDockerfile(Paths.get("artifacts/app-psql/Dockerfile")));
    @Rule
    private static GenericContainer paymentSimulator =
            new GenericContainer(new ImageFromDockerfile("payment-simulator")
                    .withDockerfile(Paths.get("artifacts/gate-simulator/Dockerfile")));

    private static String dbUrl;

    public static String getDbUrl() {
        return dbUrl;
    }

    public static String setUp(JdbcDatabaseContainer database) {
        dbUrl = database.getJdbcUrl();
        System.out.println("DEBUG: " + dbUrl);

        if (dbUrl.contains("?")) {
            dbUrl = database.getJdbcUrl().substring(0, database.getJdbcUrl().indexOf("?"));
        }
        paymentSimulator
                .withNetwork(database.getNetwork())
                .withNetworkAliases("gate-simulator")
                .withExposedPorts(9999)
                .start();
        System.out.println("DEBUG: " + paymentSimulator.getHost() + ":" + paymentSimulator.getMappedPort(9999));

        return containerSelector(database);
    }

    public static String containerSelector(JdbcDatabaseContainer database) {
        String appUrl = null;
        ToStringConsumer toStringConsumer = new ToStringConsumer();
        if (dbUrl.contains("mysql")) {
            appContMYSQL
                    .withEnv("TESTCONTAINERS_DB_URL", dbUrl)
                    .withEnv("TESTCONTAINERS_DB_USER", "app")
                    .withEnv("TESTCONTAINERS_DB_PASS", "pass")
                    .withCommand("./wait-for-it.sh --timeout=30 mysql:3306 -- java -jar aqa-shop.jar")
                    .withExposedPorts(8080)
                    .withNetwork(database.getNetwork())
                    .withNetworkAliases("app")
                    .start();
            appContMYSQL.followOutput(toStringConsumer, OutputFrame.OutputType.STDOUT);
            appUrl = appContMYSQL.getHost() + ":" + appContMYSQL.getMappedPort(8080);
        } else if (dbUrl.contains("postgresql")) {
            appContPSQL
                    .withEnv("TESTCONTAINERS_DB_URL", dbUrl)
                    .withEnv("TESTCONTAINERS_POSTGRES_USER", "app")
                    .withEnv("TESTCONTAINERS_POSTGRES_PASSWORD", "pass")
                    .withCommand("./wait-for-it.sh --timeout=30 psql:5432 -- java -jar aqa-shop.jar")
                    .withExposedPorts(8080)
                    .withNetwork(database.getNetwork())
                    .withNetworkAliases("app")
                    .start();
            appUrl = appContPSQL.getHost() + ":" + appContPSQL.getMappedPort(8080);
            appContPSQL.followOutput(toStringConsumer, OutputFrame.OutputType.STDOUT);
        }

        String utf8String = toStringConsumer.toUtf8String();
        System.out.println(appUrl);
        System.out.println(utf8String);
        return appUrl;
    }


}
