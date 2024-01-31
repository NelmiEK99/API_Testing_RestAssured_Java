import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final String CONFIG_FILE = "config.properties";

    public static String BASE_URL;
    public static String API_ENDPOINT;
    public static String ADMIN_USERNAME;
    public static String ADMIN_PASSWORD;
    public static String USER_USERNAME;
    public static String USER_PASSWORD;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        Properties prop = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.out.println("Sorry, unable to find " + CONFIG_FILE);
                return;
            }

            // load a properties file from class path
            prop.load(input);

            // get the property values
            BASE_URL = prop.getProperty("base.url");
            API_ENDPOINT = prop.getProperty("api.endpoint");
            ADMIN_USERNAME = prop.getProperty("admin.username");
            ADMIN_PASSWORD = prop.getProperty("admin.password");
            USER_USERNAME = prop.getProperty("user.username");
            USER_PASSWORD = prop.getProperty("user.password");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
