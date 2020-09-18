package prototype.src.test;

import prototype.src.main.java.it.spindox.prototype.ServiceLayer;
import commons.src.main.java.it.spindox.vfexception.VfException;
import commons.src.main.java.it.spindox.vfexception.core.NoFoundationClusterDefinedException;
import commons.src.main.java.it.spindox.vfexception.excelio.IllegalCellFormatException;
import commons.src.main.java.it.spindox.vfexception.excelio.IllegalEmptyCellException;
import commons.src.main.java.it.spindox.vfexception.excelio.InvalidInputFileException;
import commons.src.main.java.it.spindox.vfexception.excelio.UnexpectedSituationOccurredException;
import junit.framework.TestCase;

import org.junit.Test;

import java.io.File;

public class TestServiceLayer {
	/*

    @Test
    public void loadInputConfigJSON() throws VfException {

        String path = getClass().getResource("/inputConfigWithoutFoundation.json").getFile();
        ServiceLayer serviceLayer = new ServiceLayer();
        serviceLayer.fillInputConfigFile(new File(path));
        TestCase.assertTrue(!serviceLayer.getInputConfig().toString().isEmpty());
    }


    @Test
    public void loadCatalog() throws VfException {
        String path = getClass().getResource("/inputConfigWithoutFoundation.json").getFile();
        ServiceLayer serviceLayer = new ServiceLayer();
        serviceLayer.fillInputConfigFile(new File(path));
        serviceLayer.getInputConfig().setCatalogFilePath(getClass().getResource(serviceLayer.getInputConfig().getCatalogFilePath()).getFile());

        serviceLayer.loadCatalog();
        serviceLayer.pickFromCatalog();

        TestCase.assertTrue(serviceLayer.getCatalog() != null);
        TestCase.assertTrue(serviceLayer.getCatalog().getBlade() != null);
        TestCase.assertTrue(serviceLayer.getCatalog().getBladeHighPerformance() != null);
    }

    @Test
    public void loadVbomFile() throws VfException {
        String path = getClass().getResource("/inputConfigWithoutFoundation.json").getFile();
        ServiceLayer serviceLayer = new ServiceLayer();
        serviceLayer.fillInputConfigFile(new File(path));
        serviceLayer.getInputConfig().setvBomFilePath(getClass().getResource(serviceLayer.getInputConfig().getvBomFilePath()).getFile());
        serviceLayer.getInputConfig().setCatalogFilePath(getClass().getResource(serviceLayer.getInputConfig().getCatalogFilePath()).getFile());

        serviceLayer.loadCatalog();

        serviceLayer.pickFromCatalog();
        serviceLayer.fillClusterList();


        TestCase.assertTrue(serviceLayer.getClusterList().size() == 1);

        //IN QUESTO CASO L'UNICO FILE CHE E' VUOTO E' QUELLO DI FOUNDATION MA NON DOBBIAMO CONSIDERARLO PER QUESTA
        //INTERAZIONE PERCHE' L'INPUT CONFIG E' SENZA FOUNDATION
        TestCase.assertTrue(serviceLayer.getClusterList().get(0).getVbom().size() > 0);
    }


    @Test
    public void placeAllCluster() throws VfException {

        String path = getClass().getResource("/inputConfigWithoutFoundation.json").getFile();
        ServiceLayer serviceLayer = new ServiceLayer();
        serviceLayer.fillInputConfigFile(new File(path));
        serviceLayer.getInputConfig().setvBomFilePath(getClass().getResource(serviceLayer.getInputConfig().getvBomFilePath()).getFile());
        serviceLayer.getInputConfig().setCatalogFilePath(getClass().getResource(serviceLayer.getInputConfig().getCatalogFilePath()).getFile());

        serviceLayer.loadCatalog();

        serviceLayer.pickFromCatalog();
        serviceLayer.fillClusterList();

        serviceLayer.placeAllCluster();



    }

    @Test
    public void placeAllClusterOnlyFoundationWithoutManagement() throws VfException {
        String path = getClass().getResource("/inputConfigWithoutFoundation.json").getFile();
        ServiceLayer serviceLayer = new ServiceLayer();
        serviceLayer.fillInputConfigFile(new File(path));
        serviceLayer.getInputConfig().setvBomFilePath(getClass().getResource(serviceLayer.getInputConfig().getvBomFilePath()).getFile());
        serviceLayer.getInputConfig().setCatalogFilePath(getClass().getResource(serviceLayer.getInputConfig().getCatalogFilePath()).getFile());
        serviceLayer.getInputConfig().setFoundationFlag(true);
        serviceLayer.loadCatalog();
        serviceLayer.pickFromCatalog();
        serviceLayer.fillClusterList();

        serviceLayer.placeAllCluster();


    }


    @Test
    public void placeAllClusterOnlyFoundationWithManagementWithFoundation() throws VfException {
        String path = getClass().getResource("/inputConfigWithFoundation.json").getFile();
        ServiceLayer serviceLayer = new ServiceLayer();
        serviceLayer.fillInputConfigFile(new File(path));
        serviceLayer.getInputConfig().setvBomFilePath(getClass().getResource(serviceLayer.getInputConfig().getvBomFilePath()).getFile());
        serviceLayer.getInputConfig().setCatalogFilePath(getClass().getResource(serviceLayer.getInputConfig().getCatalogFilePath()).getFile());
        serviceLayer.getInputConfig().setFoundationFlag(true);
        serviceLayer.getInputConfig().setManagementFlag(true);
        serviceLayer.loadCatalog();
        serviceLayer.pickFromCatalog();
        serviceLayer.fillClusterList();

        serviceLayer.placeAllCluster();

//        serviceLayer.logPlacementDetailed();


    }


    @Test
    public void placeAllClusterOnlyFoundationWithManagement()  {

        String path = getClass().getResource("/inputConfigWithoutFoundation.json").getFile();
        ServiceLayer serviceLayer = new ServiceLayer();
        boolean error =true;
        try {
            serviceLayer.fillInputConfigFile(new File(path));
            TestCase.assertEquals(true, error);
        } catch (VfException e) {
            error=false;
            TestCase.assertEquals(true, error);
        }

        serviceLayer.getInputConfig().setvBomFilePath(getClass().getResource(serviceLayer.getInputConfig().getvBomFilePath()).getFile());
        serviceLayer.getInputConfig().setCatalogFilePath(getClass().getResource(serviceLayer.getInputConfig().getCatalogFilePath()).getFile());
        serviceLayer.getInputConfig().setFoundationFlag(true);
        serviceLayer.getInputConfig().setManagementFlag(true);
        try {
            serviceLayer.loadCatalog();
        } catch (UnexpectedSituationOccurredException e) {
            error=false;
            TestCase.assertEquals(true, error);
        } catch (InvalidInputFileException e) {
            error=false;
            TestCase.assertEquals(true, error);
        } catch (IllegalEmptyCellException e) {
            error=false;
            TestCase.assertEquals(true, error);
        } catch (IllegalCellFormatException e) {
            error=false;
            TestCase.assertEquals(true, error);
        }

        try {
        	serviceLayer.pickFromCatalog();
            serviceLayer.fillClusterList();
        } catch (VfException e) {
            error=false;
            TestCase.assertEquals(true, error);
        }

        try {
            serviceLayer.placeAllCluster();
        } catch (NoFoundationClusterDefinedException e) {
            error=true;
            TestCase.assertEquals(true, error);
        } catch (VfException e) {
            error=false;
            TestCase.assertEquals(true, error);
        }
    }

*/}
