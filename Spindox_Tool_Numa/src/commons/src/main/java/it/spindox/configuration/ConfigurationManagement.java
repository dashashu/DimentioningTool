package commons.src.main.java.it.spindox.configuration;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

/**
 * Created by Ashraf on 20/02/2017.
 */
public class ConfigurationManagement {

    public static Configuration getVbomConfiguration() {
        Configurations configs = new Configurations();

        Configuration config = null;
        try {
            //File vbomConfigFile = new File("C://Users//DashA2//Desktop//Spindox_Tool_release_v3.1.0//Dimensioning Tool Release V3.1.1//vBomConfig.properties");
            File vbomConfigFile = new File("vBomConfig.properties");
            config = configs.properties(vbomConfigFile);
        } catch (ConfigurationException e) {
            System.err.println("File vBomConfig.properties not found");
        }

        return config;
    }

    public static Configuration getVbomCustomerConfiguration() {
        Configurations configs = new Configurations();

        Configuration config = null;
        try {
            //File vbomCustomerConfigFile = new File("C://Users//DashA2//Desktop//Spindox_Tool_release_v3.1.0//Dimensioning Tool Release V3.1.1//vBomCustomerConfig.properties");
            File vbomCustomerConfigFile = new File("vBomCustomerConfig.properties");
            config = configs.properties(vbomCustomerConfigFile);
        } catch (ConfigurationException e) {
            System.err.println("File vbomCustomerConfigFile.properties not found");
        }

        return config;
    }

    public static Configuration getCatalogConfiguration() {
        Configurations configs = new Configurations();

        Configuration config = null;
        try {
        	//config = configs.properties(new File("C://Users//DashA2//Desktop//Spindox_Tool_release_v3.1.0//Dimensioning Tool Release V3.1.1//catalogConfig.properties"));
            config = configs.properties(new File("catalogConfig.properties"));
        } catch (ConfigurationException e) {
            System.err.println("File catalogConfig.properties not found");
        }

        return config;
    }

    public static Configuration getThreeParCharConfiguration() {
        Configurations configs = new Configurations();

        Configuration config = null;
        try {
            //config = configs.properties(new File("C://Users//DashA2//Desktop//Spindox_Tool_release_v3.1.0//Dimensioning Tool Release V3.1.1//threeParCharacterizationConfig.properties"));
            config = configs.properties(new File("threeParCharacterizationConfig.properties"));
        } catch (ConfigurationException e) {
            System.err.println("File threeParCharacterizationConfig.properties not found");
        }

        return config;
    }

}
