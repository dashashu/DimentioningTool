package prototype.src.main.java.it.spindox.prototype;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import excelio.src.main.java.it.spindox.excelio.util.OutputUtils;
//import excelio.src.main.java.it.spindox.excelio.util.OutputUtils_Test;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import commons.src.main.java.it.spindox.vfexception.VfException;

import org.apache.log4j.Logger;


public class MainPrototype {
    final static Logger logger = Logger.getLogger(MainPrototype.class);
    
    public static void main(String[] args) {
    	long startTime = System.nanoTime();
        String path = args[0]; 
        //String path = "C://Users//DashA2//OneDrive - Vodafone Group//Desktop//Spindox_Tool_release_v3.1.0//Dimensioning Tool Release V3.1.1//Dimensioning Tool Release V3.1.4//inputConfig.json";
        String path2 = args[1];
        //String path2 = "C://Users//DashA2//OneDrive - Vodafone Group//Desktop//Spindox_Tool_release_v3.1.0//Dimensioning Tool Release V3.1.1//Dimensioning Tool Release V3.1.4//vfe-vbom-rules.json";
        File file = new File(path.trim());
        File file2 = new File(path2.trim());
        launchApplicationForPlacementAndEstimationWithClusterSupport(file, file2);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        double elapsedTimeInSecond = (double) duration / 1_000_000_000;
        System.out.println(elapsedTimeInSecond+" seconds");
        logger.info("Time taken by the tool to execute the VBOM is: "+elapsedTimeInSecond+" Seconds");
    }

    public static void launchApplicationForPlacementAndEstimationWithClusterSupport(File configFile, File rulesFile) {
        try {
            ServiceLayer serviceLayer = new ServiceLayer(configFile, rulesFile);

            serviceLayer.loadCatalog();
            serviceLayer.pickFromCatalog();
            logger.debug("Catalog: " + serviceLayer.getCatalog().toString());

            serviceLayer.fillClusterList();

            List<Cluster> clusterList = serviceLayer.placeAllCluster();

            try {
                serviceLayer.estimateWithCluster();
                OutputUtils.createOutputFile(clusterList, serviceLayer.getCatalog(), serviceLayer.getEstimation(), ServiceLayer.yearsOrder, serviceLayer.getInputConfig());
            } catch (Exception e) {
                logger.error("Error occurred during estimation of costs: " + e.getMessage());
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                String s = writer.toString();
                logger.debug("Error occurred during estimation of costs: " + s);

            }

            try {
            	OutputUtils.createOutputFile(clusterList, serviceLayer.getCatalog(), serviceLayer.getEstimation(), ServiceLayer.yearsOrder, serviceLayer.getInputConfig());
            } catch (IOException e) {
            	throw new VfException(e.getMessage());
            }

            serviceLayer.logPlacementDetailedForCluster();
        } catch (VfException e) {
            logger.error("\n\nExecution Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
