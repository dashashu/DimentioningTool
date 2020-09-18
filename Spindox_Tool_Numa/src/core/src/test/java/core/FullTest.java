package core.src.test.java.core;

public class FullTest {
	/*
    private static final Logger logger = (Logger) LogManager
            .getLogger(FullTest.class);

    @Test
    public void fullTest() throws InvalidVirtualMachineException, IllegalMergedRegionException, IllegalEmptyCellException,
            IOException, IllegalCellFormatException, IllegalVBomYearException, InvalidInputFileException {
    	URL url = null;
        URL urlThreeParChar = getClass().getResource("/3ParCharacterization.xlsx");
        String threeParCharPath = urlThreeParChar.getPath();
        List<VBom> vbvbvb = null;
        List<VBom> vbvbvb2;

        BladeFactory bladeFactory = new BladeFactory(12.0, 120, 2, 0, 0, 100000.0, 100000.0);
        Cluster foundationCluster = new Cluster();
        Cluster cluster = new Cluster();
        cluster.setBladeFactory(bladeFactory);
        ClusterConfiguration cc = new ClusterConfiguration("", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
        cluster.setClusterConfiguration(cc);

        logger.info("Starting Test!");
        try {
            //Test 1: based on an earlier Vijay placement
            url = getClass().getResource("/FullTest01.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            VbomManagement vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            List<Year> years = vbm.getYearList();
            Placement placement = new PlacementExtraSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            placement.addSpareBlades(7);
            TestCase.assertEquals(placement.getBladeList().size(), 13);

            //Test 2: check if the blades containing the vmb have the high throughput flag as true
            url = getClass().getResource("/FullTest02.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            placement = new PlacementExtraSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            placement.addSpareBlades(7);
            for (Blade tmpBlade : placement.getBladeList()) {
                for (VMGroup tmpGroup : tmpBlade.getVmGroupAssignedToThisBladeList()) {
                    for (VirtualMachine tmpVm : tmpGroup.getVmList())
                        if (tmpVm.getVmName().equals("vmb.type")) {
                            TestCase.assertTrue(tmpBlade.isHighThroughputCoreZero());
                        } else {
                            TestCase.assertFalse(tmpBlade.isHighThroughputCoreZero());
                        }
                }
            }

            //Test 3: based on an earlier Vijay placement
            url = getClass().getResource("/FullTest03.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            placement.addSpareBlades(7);
            TestCase.assertEquals(placement.getBladeList().size()<=44, true);

            Should respond with an exception because VM B is in affinity with VM C, but VM C is in
              anti-affinity with VM B.
            try {
                url = getClass().getResource("/FullTest04.xls");
                vbvbvb = Util.readSheet(url.getPath(), 0);
                vbm = new VbomManagement(vbvbvb);
                vbm.extractDataFromVbom();
                vbm.applyConstraints();
                years = vbm.getYearList();
                placement = new PlacementSingleSocket(years.get(0)
                        .getSiteList().get(0), cluster);
                placement.place();
                placement.bladeCompression();
                placement.addSpareBlades(7);
                TestCase.assertTrue(false);
            } catch (InconsistentConstraintsException e) {
                TestCase.assertTrue(true);
            }

            Test 6: should return exception because it can't put a VM with 100 cores into a blade that
              does not have that much space.
            try {
                url = getClass().getResource("/FullTest06.xls");
                vbvbvb = Util.readSheet(url.getPath(), 0);
                vbm = new VbomManagement(vbvbvb);
                vbm.extractDataFromVbom();
                vbm.applyConstraints();
                years = vbm.getYearList();
                placement = new PlacementSingleSocket(years.get(0)
                        .getSiteList().get(0), cluster);
                placement.place();
                placement.bladeCompression();
                placement.addSpareBlades(7);
                TestCase.assertTrue(false);
            } catch (NotEnoughResourceAvailableException e) {
                TestCase.assertTrue(true);
            }

            Test 7a: should put the VMs in 2 blades because with the extra socket placement it can
            spread a VM in two sockets.
            url = getClass().getResource("/FullTest07.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            placement = new PlacementExtraSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            placement.addSpareBlades(7);
            TestCase.assertEquals(placement.getBladeList().size(), 2);

            Test 7b: should put the VMs in 3 blades because with the single socket placement it cannot
              spread a VM in two sockets.
            url = getClass().getResource("/FullTest07.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            years = vbm.getYearList();
            vbm.applyConstraints();
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            placement.addSpareBlades(7);
            TestCase.assertEquals(placement.getBladeList().size(), 3);

            //Test 8: calculate the averages and check the calculation done.
            url = getClass().getResource("/FullTest08.xls");
            // Set<String> yearNames = Util.checkYearsConsistency(url.getPath(), 0, 1);
            Set<String> yearNames = Util.checkYearsConsistency(url.getPath(), false);
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbvbvb2 = Util.readSheet(url.getPath(), 1);

            List<Cluster> clusterList = new ArrayList<Cluster>();
            Cluster a = new Cluster();
            a.setBladeFactory(bladeFactory);
            a.setSheetLabel("Service Cluster");
            a.setVbom(vbvbvb);
            clusterList.add(a);
            Cluster b = new Cluster();
            b.setBladeFactory(bladeFactory);
            b.setSheetLabel("Management Cluster");
            b.setVbom(vbvbvb2);
            clusterList.add(b);

            Averages avg = new Averages("Turin", yearNames, clusterList);
            TestCase.assertEquals(avg.getAvgBlockSize().get("2017.0"), 8.0);
            TestCase.assertEquals(avg.getAvgIops().get("2017.0"), 813.0);
            TestCase.assertEquals(avg.getAvgReadWrite().get("2017.0"), 40.0);

            Test 10: should put the VMs in one blade because they should need 8 cores
             (4 for each blade and 0 for High Throughput).
            url = getClass().getResource("/FullTest09.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(4.0, 120, 2, 0, 0, 100000.0, 100000.0);
            cluster.setBladeFactory(bladeFactory);
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 1);

            Test 10: should put the VMs in two different blades because they should need 9 cores
              (4 for each blade and 1 for High Throughput) while the blade only has 8 cores.
            url = getClass().getResource("/FullTest10.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(4.0, 120, 2, 0, 0, 100000.0, 100000.0);
            cluster.setBladeFactory(bladeFactory);
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 2);

            //Test 11a: should put the VMs in two different blades because they're in AAF between them
            url = getClass().getResource("/FullTest11.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints(); //Passing no argument is the same as passing false
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(4.0, 120, 2, 0, 0, 100000.0, 100000.0);
            cluster.setBladeFactory(bladeFactory);
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 2);

            Test 11b: should put the VMs in one blade because they're not in AAF since they're different
              instances
            url = getClass().getResource("/FullTest11.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints(true);
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(4.0, 120, 2, 0, 0, 1.0, 1.0);
            cluster.setBladeFactory(bladeFactory);
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 1);

            Test 12a: should put the VMs in two different blades because they're in affinity only between
              clones and there are two instances with only a clone each.
            url = getClass().getResource("/FullTest12.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints(true); //affinity only between clones true
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(4.0, 120, 1, 0, 0, 100000.0, 100000.0);
            cluster.setBladeFactory(bladeFactory);
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 2);

            Test 12b: should respond with an exception because the VMs are in affinity
              between them, but they can't fit into a single blade
            try {
                url = getClass().getResource("/FullTest12.xls");
                vbvbvb = Util.readSheet(url.getPath(), 0);
                vbm = new VbomManagement(vbvbvb);
                vbm.extractDataFromVbom();
                vbm.applyConstraints(false); //affinity only between clones false
                years = vbm.getYearList();
                bladeFactory = new BladeFactory(4.0, 120, 1, 0, 0, 100000.0, 100000.0);
                cluster.setBladeFactory(bladeFactory);
                placement = new PlacementSingleSocket(years.get(0)
                        .getSiteList().get(0), cluster);
                placement.place();
                placement.bladeCompression();
                TestCase.assertTrue(false);
            } catch (NotEnoughResourceAvailableException e) {
                TestCase.assertTrue(true);
            }

            //Test 13: should respond with an exception because there is an odd number of VM in AMS
            try {
                url = getClass().getResource("/FullTest13.xls");
                vbvbvb = Util.readSheet(url.getPath(), 0);
                vbm = new VbomManagement(vbvbvb);
                vbm.extractDataFromVbom();
                vbm.applyConstraints(true); //true-false o niente
                years = vbm.getYearList();
                bladeFactory = new BladeFactory(4.0, 120, 2, 0, 0, 100000.0, 100000.0);
                cluster.setBladeFactory(bladeFactory);
                placement = new PlacementSingleSocket(years.get(0)
                        .getSiteList().get(0), cluster);
                placement.place();
                placement.bladeCompression();
                TestCase.assertEquals(placement.getBladeList().size(), 1);
                TestCase.assertTrue(false);
            } catch (InconsistentConstraintsException e) {
                TestCase.assertTrue(true);
            }

            //Test 14a: should use only one blade because the VMs are little and the numa flag is false
            url = getClass().getResource("/FullTest14.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(8.0, 120, 2, 0, 0, 100000.0, 100000.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(false);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 1);

            Test 14b: should use two different blades, because even if the VMs are little, it has
              the numa flag true.
            url = getClass().getResource("/FullTest14.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(8.0, 120, 2, 0, 0, 100000.0, 100000.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(true);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 2);

            Test 15: should respond with an exception because the numa flag is enabled and the VM A cannot
              be put in the same blade of his clone despite having a self affinity.
            try {
                url = getClass().getResource("/FullTest15.xls");
                vbvbvb = Util.readSheet(url.getPath(), 0);
                vbm = new VbomManagement(vbvbvb);
                vbm.extractDataFromVbom();
                vbm.applyConstraints();
                years = vbm.getYearList();
                bladeFactory = new BladeFactory(6.0, 120, 2, 0, 0, 100000.0, 100000.0);
                bladeFactory.setEsxiCores(2.0);
                bladeFactory.setTxrxCores(2.0);
                cluster.setBladeFactory(bladeFactory);

                cc.setNumaFlag(true);
                cluster.setClusterConfiguration(cc);

                placement = new PlacementSingleSocket(years.get(0)
                        .getSiteList().get(0), cluster);
                placement.place();
                placement.bladeCompression();
                TestCase.assertEquals(placement.getBladeList().size(), 2);
                TestCase.assertTrue(false);
            } catch (NotEnoughResourceAvailableException e) {
                TestCase.assertTrue(true);
            }

            Test 16a: should use only one blade with one socket with 6 cores, and the blade will have at the
              end 0 cores available (2 used by the VM, 2 by esxi and 2 by txrx).
            url = getClass().getResource("/FullTest16.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(6.0, 120, 1, 0, 0, 100000.0, 100000.0);
            bladeFactory.setEsxiCores(2.0);
            bladeFactory.setTxrxCores(2.0);
            cluster.setBladeFactory(bladeFactory);
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 1);
            TestCase.assertEquals(placement.getBladeList().get(0).getCoreAvailable(), new Double(0.0));

            Test 16b: should use only one blade with one socket with 6 cores, and the blade will have at the
              end 4 cores available (2 used by the VM).
            url = getClass().getResource("/FullTest16.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(6.0, 120, 1, 0, 0, 100000.0, 100000.0);
            bladeFactory.setEsxiCores(0.0);
            bladeFactory.setTxrxCores(0.0);
            cluster.setBladeFactory(bladeFactory);
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 1);
            TestCase.assertEquals(placement.getBladeList().get(0).getCoreAvailable(), new Double(4.0));

            Test 17: should respond with an exception because it can't put the two VMs in affinity in the same blade, because the blade have only one
              socket and the numa flag is true
            try {
                url = getClass().getResource("/FullTest17.xls");
                vbvbvb = Util.readSheet(url.getPath(), 0);
                vbm = new VbomManagement(vbvbvb);
                vbm.extractDataFromVbom();
                vbm.applyConstraints();
                years = vbm.getYearList();
                bladeFactory = new BladeFactory(6.0, 120, 1, 0, 0, 100000.0, 100000.0);
                bladeFactory.setEsxiCores(2.0);
                bladeFactory.setTxrxCores(2.0);
                cluster.setBladeFactory(bladeFactory);
                cc.setNumaFlag(true);
                cluster.setClusterConfiguration(cc);
                placement = new PlacementSingleSocket(years.get(0)
                        .getSiteList().get(0), cluster);
                placement.place();
                placement.bladeCompression();
                TestCase.assertTrue(false);
            } catch (NotEnoughResourceAvailableException e) {
                TestCase.assertTrue(true);
            }

            Test 18a: should use two blades even if the two VMs are in affinity because the numa flag is
            true and the blade has only one socket
            url = getClass().getResource("/FullTest18.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(6.0, 120, 1, 0, 0, 100000.0, 100000.0);
            bladeFactory.setEsxiCores(2.0);
            bladeFactory.setTxrxCores(2.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(true);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementExtraSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 2);

            Test 18b: should use two blades even if the two VMs are in affinity because the numa flag is
            true and the blade has only one socket
            url = getClass().getResource("/FullTest18.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(6.0, 120, 1, 0, 0, 100000.0, 100000.0);
            bladeFactory.setEsxiCores(2.0);
            bladeFactory.setTxrxCores(2.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(true);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 2);

            Test 19: should use two blades even if the two VMs are in affinity because the numa flag is
            true and the blade has both sockets occupied by the first VM
            url = getClass().getResource("/FullTest19.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(6.0, 120, 2, 0, 0, 100000.0, 100000.0);
            bladeFactory.setEsxiCores(2.0);
            bladeFactory.setTxrxCores(2.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(true);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 2);

            Test 20a: 
            url = getClass().getResource("/FullTest20.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(10.0, 250, 2, 0, 0, 100000.0, 100000.0);
            bladeFactory.setEsxiCores(0.0);
            bladeFactory.setTxrxCores(0.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(false);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementSingleSocket(years.get(0).getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 1);

            Test 20b: 
            url = getClass().getResource("/FullTest20.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(10.0, 250, 2, 0, 0, 100000.0, 100000.0);
            bladeFactory.setEsxiCores(0.0);
            bladeFactory.setTxrxCores(0.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(true);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementSingleSocket(years.get(0).getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 2);

            Test 20c: 
            url = getClass().getResource("/FullTest20.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(10.0, 250, 2, 1, 1, 100000.0, 100000.0);
            bladeFactory.setEsxiCores(1.0);
            bladeFactory.setTxrxCores(1.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(true);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementSingleSocket(years.get(0).getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 2);

            Test 20d: 
            try {
                url = getClass().getResource("/FullTest20.xls");
                vbvbvb = Util.readSheet(url.getPath(), 0);
                vbm = new VbomManagement(vbvbvb);
                vbm.extractDataFromVbom();
                vbm.applyConstraints();
                years = vbm.getYearList();
                bladeFactory = new BladeFactory(10.0, 250, 2, 6, 6, 100000.0, 100000.0);
                bladeFactory.setEsxiCores(2.0);
                bladeFactory.setTxrxCores(2.0);
                cluster.setBladeFactory(bladeFactory);
                cc.setNumaFlag(true);
                cluster.setClusterConfiguration(cc);
                placement = new PlacementSingleSocket(years.get(0).getSiteList().get(0), cluster);
                placement.place();
                placement.bladeCompression();
                TestCase.assertTrue(false);
            } catch (NotEnoughResourceAvailableException e) {
                TestCase.assertTrue(true);
            }

            Test 21a: 
            try {
                url = getClass().getResource("/FullTest21.xls");
                vbvbvb = Util.readSheet(url.getPath(), 0);
                vbm = new VbomManagement(vbvbvb);
                vbm.extractDataFromVbom();
                vbm.applyConstraints();
                years = vbm.getYearList();
                bladeFactory = new BladeFactory(10.0, 250, 2, 1, 0, 100000.0, 100000.0);
                bladeFactory.setEsxiCores(0.0);
                bladeFactory.setTxrxCores(1.0);
                cluster.setBladeFactory(bladeFactory);
                cc.setNumaFlag(true);
                cluster.setClusterConfiguration(cc);
                placement = new PlacementSingleSocket(years.get(0).getSiteList().get(0), cluster);
                placement.place();
                placement.bladeCompression();
                TestCase.assertTrue(false);
            } catch (NotEnoughResourceAvailableException e) {
                TestCase.assertTrue(true);
            }

            Test 21b: 
            try {
                url = getClass().getResource("/FullTest21.xls");
                vbvbvb = Util.readSheet(url.getPath(), 0);
                vbm = new VbomManagement(vbvbvb);
                vbm.extractDataFromVbom();
                vbm.applyConstraints();
                years = vbm.getYearList();
                bladeFactory = new BladeFactory(10.0, 250, 2, 0, 1, 100000.0, 100000.0);
                bladeFactory.setEsxiCores(1.0);
                bladeFactory.setTxrxCores(0.0);
                cluster.setBladeFactory(bladeFactory);
                cc.setNumaFlag(true);
                cluster.setClusterConfiguration(cc);
                placement = new PlacementSingleSocket(years.get(0).getSiteList().get(0), cluster);
                placement.place();
                placement.bladeCompression();
                TestCase.assertTrue(false);
            } catch (NotEnoughResourceAvailableException e) {
                TestCase.assertTrue(true);
            }

            Test 22: 
            url = getClass().getResource("/FullTest22.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(10.0, 250, 2, 0, 1, 100000.0, 100000.0);
            bladeFactory.setEsxiCores(0.0);
            bladeFactory.setTxrxCores(1.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(true);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementSingleSocket(years.get(0).getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 1);

            Test 23: 
            url = getClass().getResource("/FullTest23.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(10.0, 250, 2, 0, 0, 100000.0, 100000.0);
            bladeFactory.setEsxiCores(0.0);
            bladeFactory.setTxrxCores(0.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(true);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementSingleSocket(years.get(0).getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 2);

            Test 24: 
            try {
                url = getClass().getResource("/FullTest24.xls");
                vbvbvb = Util.readSheet(url.getPath(), 0);
                vbm = new VbomManagement(vbvbvb);
                vbm.extractDataFromVbom();
                vbm.applyConstraints();
                years = vbm.getYearList();
                bladeFactory = new BladeFactory(10.0, 250, 2, 0, 0, 100000.0, 100000.0);
                bladeFactory.setEsxiCores(0.0);
                bladeFactory.setTxrxCores(0.0);
                cluster.setBladeFactory(bladeFactory);
                cc.setNumaFlag(true);
                cluster.setClusterConfiguration(cc);
                placement = new PlacementSingleSocket(years.get(0).getSiteList().get(0), cluster);
                placement.place();
                placement.bladeCompression();
                TestCase.assertTrue(false);
            } catch (NotEnoughResourceAvailableException e) {
                TestCase.assertTrue(true);
            }

             Test 25: check for year names' consistency in input vBOM 
            try {
                url = getClass().getResource("/FullTest25.xls");
                Set<String> yearTitlesOfVbom = Util.checkYearsConsistency(url.getPath(), false);
            } catch (VfException e) {
                System.out.println(e.getMessage());
            }
            
            //Test 26a: will use two blades because with only one the throughput is more than the max
            url = getClass().getResource("/FullTest26.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(10.0, 250, 2, 0, 0, 1500.0, 600.0);
            bladeFactory.setEsxiCores(0.0);
            bladeFactory.setTxrxCores(0.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(true);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementSingleSocket(years.get(0).getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 2);
            
            //Test 26b: will use only one blade because the throughput is not more than the max
            url = getClass().getResource("/FullTest26.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(10.0, 250, 2, 0, 0, 3000.0, 600.0);
            bladeFactory.setEsxiCores(0.0);
            bladeFactory.setTxrxCores(0.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(true);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementSingleSocket(years.get(0).getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 1);
            
            Test 27a: will use two blades because with only one the iops running supported is more
              than the max
            url = getClass().getResource("/FullTest27.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(10.0, 250, 2, 0, 0, 3000.0, 300.0);
            bladeFactory.setEsxiCores(0.0);
            bladeFactory.setTxrxCores(0.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(true);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementSingleSocket(years.get(0).getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 2);
            
            //Test 27b: will use only one blades because the iops running supported is not more than the max
            url = getClass().getResource("/FullTest27.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            bladeFactory = new BladeFactory(10.0, 250, 2, 0, 0, 3000.0, 600.0);
            bladeFactory.setEsxiCores(0.0);
            bladeFactory.setTxrxCores(0.0);
            cluster.setBladeFactory(bladeFactory);
            cc.setNumaFlag(true);
            cluster.setClusterConfiguration(cc);
            placement = new PlacementSingleSocket(years.get(0).getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            TestCase.assertEquals(placement.getBladeList().size(), 1);
            
            Test 28: VBOM provided by Vijay that made us change the priority when choosing the blade
            for blade compression.
	        url = getClass().getResource("/FullTest28.xls");
	        vbvbvb = Util.readSheet(url.getPath(), 0);
	        vbm = new VbomManagement(vbvbvb);
	        vbm.extractDataFromVbom();
	        vbm.applyConstraints();
	        years = vbm.getYearList();
	        bladeFactory = new BladeFactory(18.0, 204, 2, 0, 0, 10000000.0, 10000000.0);
	        bladeFactory.setEsxiCores(0.0);
	        bladeFactory.setTxrxCores(0.0);
	        cluster.setBladeFactory(bladeFactory);
	        cc.setNumaFlag(false);
	        cluster.setClusterConfiguration(cc);
	        placement = new PlacementExtraSocket(years.get(0).getSiteList().get(0), cluster);
	        placement.place();
	        placement.bladeCompression();
	        TestCase.assertEquals(placement.getBladeList().size(), 13);

	        Test 29a: le VM in questione possono essere inserite in un 3PAR (considerato completamente libero perché il default foundation è a 0,
	         * e una drive enclosure, comprando solo un blocco di dischi.
	        url = getClass().getResource("/FullTest29.xls");
	        vbvbvb = Util.readSheet(url.getPath(), 0);
	        vbm = new VbomManagement(vbvbvb);
	        vbm.extractDataFromVbom();
	        vbm.applyConstraints();
	        years = vbm.getYearList();
	        bladeFactory = new BladeFactory(10.0, 408, 1, 0, 0, 10000000.0, 10000000.0);
	        bladeFactory.setEsxiCores(0.0);
	        bladeFactory.setTxrxCores(0.0);
	        cluster.setBladeFactory(bladeFactory);
	        cc.setNumaFlag(false);
	        cluster.setSheetLabel("Service Cluster");
	        cluster.setClusterConfiguration(cc);
	        cluster.setVbom(vbvbvb);
	        clusterList = new ArrayList<Cluster>();
	        placement = new PlacementExtraSocket(years.get(0).getSiteList().get(0), cluster);
	        
	        PlacementTable placementTable = new PlacementTable();
	        vbm.getYearList().forEach(year -> {
                year.getSiteList().forEach(site -> {
                    try {
                        placementTable.getPlacementResultList().add(placement(site, year, true, false, 7, cluster));
                        cluster.setPlacementTable(placementTable);
                    } catch (InconsistentConstraintsException | NotEnoughResourceAvailableException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
	        
	        clusterList = new ArrayList<Cluster>();
	        clusterList.add(cluster);
	        URL urlCatalog = getClass().getResource("/CatalogA.xlsx");
	        Util.readCatalogSheet(urlCatalog.getPath(), CatalogConstants.COMPUTE);
	        Util.readCatalogSheet(urlCatalog.getPath(), CatalogConstants.CONTAINER);
	        Util.readCatalogSheet(urlCatalog.getPath(), CatalogConstants.STORAGE);
	        Catalog catalog = new Catalog();
	        catalog.setBlade((Compute) (CatalogFromExcel.getCatalogEntry(CatalogConstants.COMPUTE, "1", CatalogConstants.BLADE)));
	        catalog.setBladeHighPerformance((Compute) (CatalogFromExcel.getCatalogEntry(CatalogConstants.COMPUTE, "2", CatalogConstants.HIGH_PERFORMANCE_BLADE)));

	        catalog.setEnclosure((Container) CatalogFromExcel.getCatalogEntry(CatalogConstants.CONTAINER, "1000", CatalogConstants.ENCLOSURE));
	        catalog.setEnclosureHighPerformance((Container) CatalogFromExcel.getCatalogEntry(CatalogConstants.CONTAINER, "1000", CatalogConstants.HIGH_PERFORMANCE_ENCLOSURE));
	        catalog.setThreePar((Storage) CatalogFromExcel.getCatalogEntry(CatalogConstants.STORAGE, "10001", CatalogConstants.THREEPAR));
	        catalog.setThreeParExpansion((Storage) CatalogFromExcel.getThreeParDependency(CatalogConstants.THREEPAR_EXPANSION, "10001", CatalogConstants.THREEPAR_EXPANSION));
	        catalog.setDisk((Storage) CatalogFromExcel.getThreeParDependency(CatalogConstants.DISK, "10001", CatalogConstants.DISK));
	        catalog.setDriveEnclosureDisk((Storage) CatalogFromExcel.getThreeParDependency(CatalogConstants.DRIVE_ENCLOSURE_DISK, "10001", CatalogConstants.DRIVE_ENCLOSURE_DISK));
	        
            avg = new Averages("Turin", yearNames, clusterList);
            ArrayList<Averages> avgList = new ArrayList<Averages>();
            avgList.add(avg);
	        EstimationManagement estimationManagement = new EstimationManagement(false, false, 0.0, 0.0, 0.0, 0.0, catalog, threeParCharPath, avgList, clusterList);
	        estimationManagement.estimate();
	        
	        List<EstimationLine> temp = estimationManagement.getEstimation().getEstimationTable().get("Turin").stream()
	        		.filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.THREEPAR))
	        		.filter(estimationLine -> estimationLine.getComponent().getComponentId().equals("10001")).collect(Collectors.toList());
	        
	        TestCase.assertEquals(0, temp.size());
	        
	        temp = estimationManagement.getEstimation().getEstimationTable().get("Turin").stream()
	        		.filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.DRIVE_ENCLOSURE_DISK))
	        		.filter(estimationLine -> estimationLine.getComponent().getComponentId().equals("10016")).collect(Collectors.toList());
	        
	        TestCase.assertEquals(1, temp.get(0).getEstimationLineDetail().get(0).getQuantity());
	        
	        temp = estimationManagement.getEstimation().getEstimationTable().get("Turin").stream()
	        		.filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.DISK))
	        		.filter(estimationLine -> estimationLine.getComponent().getComponentId().equals("10015")).collect(Collectors.toList());
	        
	        TestCase.assertEquals(1, temp.get(0).getEstimationLineDetail().get(0).getQuantity());
	        
	        Test 29b: si parte di default con il 3par pieno. 13000gb occuperanno un 3par e una parte di driveenclosure. si calcolerà
	          l'acquisto di due drive enclosure e zero blocchi di dischi.
	        url = getClass().getResource("/FullTest29.xls");
	        vbvbvb = Util.readSheet(url.getPath(), 0);
	        vbm = new VbomManagement(vbvbvb);
	        vbm.extractDataFromVbom();
	        vbm.applyConstraints();
	        years = vbm.getYearList();
	        bladeFactory = new BladeFactory(10.0, 408, 1, 0, 0, 10000000.0, 10000000.0);
	        bladeFactory.setEsxiCores(0.0);
	        bladeFactory.setTxrxCores(0.0);
	        cluster.setBladeFactory(bladeFactory);
	        cc.setNumaFlag(false);
	        cluster.setSheetLabel("Service Cluster");
	        cluster.setClusterConfiguration(cc);
	        cluster.setVbom(vbvbvb);
	        clusterList = new ArrayList<Cluster>();
	        placement = new PlacementExtraSocket(years.get(0).getSiteList().get(0), cluster);
	        
	        PlacementTable placementTable2 = new PlacementTable();
	        vbm.getYearList().forEach(year -> {
                year.getSiteList().forEach(site -> {
                    try {
                        placementTable2.getPlacementResultList().add(placement(site, year, true, false, 7, cluster));
                        cluster.setPlacementTable(placementTable2);
                    } catch (InconsistentConstraintsException | NotEnoughResourceAvailableException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
	        
	        clusterList = new ArrayList<Cluster>();
	        clusterList.add(cluster);
	        Util.readCatalogSheet(urlCatalog.getPath(), CatalogConstants.COMPUTE);
	        Util.readCatalogSheet(urlCatalog.getPath(), CatalogConstants.CONTAINER);
	        Util.readCatalogSheet(urlCatalog.getPath(), CatalogConstants.STORAGE);
	        
            avg = new Averages("Turin", yearNames, clusterList);
            avgList = new ArrayList<Averages>();
            avgList.add(avg);
	        estimationManagement = new EstimationManagement(false, false, 0.0, 0.0, 0.0, 13000.0, catalog, threeParCharPath, avgList, clusterList);
	        estimationManagement.estimate();
	        
	        temp = estimationManagement.getEstimation().getEstimationTable().get("Turin").stream()
	        		.filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.THREEPAR))
	        		.filter(estimationLine -> estimationLine.getComponent().getComponentId().equals("10001")).collect(Collectors.toList());
	        
	        TestCase.assertEquals(0, temp.size());
	        
	        temp = estimationManagement.getEstimation().getEstimationTable().get("Turin").stream()
	        		.filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.DRIVE_ENCLOSURE_DISK))
	        		.filter(estimationLine -> estimationLine.getComponent().getComponentId().equals("10016")).collect(Collectors.toList());
	        
	        TestCase.assertEquals(2, temp.get(0).getEstimationLineDetail().get(0).getQuantity());
	        
	        temp = estimationManagement.getEstimation().getEstimationTable().get("Turin").stream()
	        		.filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.DISK))
	        		.filter(estimationLine -> estimationLine.getComponent().getComponentId().equals("10015")).collect(Collectors.toList());
	        
	        TestCase.assertEquals(0, temp.get(0).getEstimationLineDetail().get(0).getQuantity());
	        
	        Test 30: uguale al 29b ma la foundation è presente nel suo sheet
	        url = getClass().getResource("/FullTest30.xls");
	        bladeFactory = new BladeFactory(40.0, 408, 1, 0, 0, 10000000.0, 10000000.0);
	        bladeFactory.setEsxiCores(0.0);
	        bladeFactory.setTxrxCores(0.0);
	        cc.setNumaFlag(false);
	        
	        vbvbvb = Util.readSheet(url.getPath(), 0);
	        vbm = new VbomManagement(vbvbvb);
	        vbm.extractDataFromVbom();
	        vbm.applyConstraints();
	        years = vbm.getYearList();
	        cluster.setBladeFactory(bladeFactory);
	        cluster.setSheetLabel("Service Cluster");
	        cluster.setClusterConfiguration(cc);
	        cluster.setVbom(vbvbvb);
	        
	        vbvbvb2 = Util.readSheet(url.getPath(), 1);
	        VbomManagement vbm2 = new VbomManagement(vbvbvb2);
	        vbm2.extractDataFromVbom();
	        vbm2.applyConstraints();
	        List<Year> foundationYears = vbm2.getYearList();
	        foundationCluster.setBladeFactory(bladeFactory);
	        foundationCluster.setSheetLabel("Foundation");
	        foundationCluster.setClusterConfiguration(cc);
	        foundationCluster.setVbom(vbvbvb2);
	        
	        clusterList = new ArrayList<Cluster>();
	        
	        PlacementTable placementTable4 = new PlacementTable();
	        vbm2.getYearList().forEach(year -> {
                year.getSiteList().forEach(site -> {
                    try {
                        placementTable4.getPlacementResultList().add(placement(site, year, true, false, 7, foundationCluster));
                        foundationCluster.setPlacementTable(placementTable4);
                    } catch (InconsistentConstraintsException | NotEnoughResourceAvailableException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
	        
	        clusterList.add(foundationCluster);
	        
	        PlacementTable placementTable3 = new PlacementTable();
	        vbm.getYearList().forEach(year -> {
                year.getSiteList().forEach(site -> {
                    try {
                        placementTable3.getPlacementResultList().add(placement(site, year, true, false, 7, cluster));
                        cluster.setPlacementTable(placementTable3);
                    } catch (InconsistentConstraintsException | NotEnoughResourceAvailableException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
	        clusterList.add(cluster);
	        
	        Util.readCatalogSheet(urlCatalog.getPath(), CatalogConstants.COMPUTE);
	        Util.readCatalogSheet(urlCatalog.getPath(), CatalogConstants.CONTAINER);
	        Util.readCatalogSheet(urlCatalog.getPath(), CatalogConstants.STORAGE);
	        
            avg = new Averages("Turin", yearNames, clusterList);
            avgList = new ArrayList<Averages>();
            avgList.add(avg);
	        estimationManagement = new EstimationManagement(true, true, 0.0, 0.0, 0.0, 0.0, catalog, threeParCharPath, avgList, clusterList);
	        estimationManagement.estimate();
	        
	        temp = estimationManagement.getEstimation().getEstimationTable().get("Turin").stream()
	        		.filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.THREEPAR))
	        		.filter(estimationLine -> estimationLine.getComponent().getComponentId().equals("10001")).collect(Collectors.toList());
	        
	        TestCase.assertEquals(1, temp.size());
	        
	        temp = estimationManagement.getEstimation().getEstimationTable().get("Turin").stream()
	        		.filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.DRIVE_ENCLOSURE_DISK))
	        		.filter(estimationLine -> estimationLine.getComponent().getComponentId().equals("10016")).collect(Collectors.toList());
	        
	        int quantity = 0;
	        for(EstimationLine el:temp) {
	        	for(EstimationLineDetail eld:el.getEstimationLineDetail()) {
	        		if(!eld.getType().equals("foundation"))
	        			quantity = eld.getQuantity();
	        	}
	        }
	        
	        TestCase.assertEquals(2, quantity);
	        
	        temp = estimationManagement.getEstimation().getEstimationTable().get("Turin").stream()
	        		.filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.DISK))
	        		.filter(estimationLine -> estimationLine.getComponent().getComponentId().equals("10015")).collect(Collectors.toList());
	        
	        TestCase.assertEquals(1, temp.get(0).getEstimationLineDetail().get(0).getQuantity());
	        
	        Test 31: la foundation occupa 70 blade, quindi un 3par intero e deve chiedere una 3parExpansion. L'initiative invece occupa poco
	        url = getClass().getResource("/FullTest31.xls");
	        bladeFactory = new BladeFactory(40.0, 408, 1, 0, 0, 10000000.0, 10000000.0);
	        bladeFactory.setEsxiCores(0.0);
	        bladeFactory.setTxrxCores(0.0);
	        cc.setNumaFlag(false);
	        
	        vbvbvb = Util.readSheet(url.getPath(), 0);
	        vbm = new VbomManagement(vbvbvb);
	        vbm.extractDataFromVbom();
	        vbm.applyConstraints();
	        years = vbm.getYearList();
	        cluster.setBladeFactory(bladeFactory);
	        cluster.setSheetLabel("Service Cluster");
	        cluster.setClusterConfiguration(cc);
	        cluster.setVbom(vbvbvb);
	        
	        vbvbvb2 = Util.readSheet(url.getPath(), 1);
	        vbm2 = new VbomManagement(vbvbvb2);
	        vbm2.extractDataFromVbom();
	        vbm2.applyConstraints();
	        foundationYears = vbm2.getYearList();
	        foundationCluster.setBladeFactory(bladeFactory);
	        foundationCluster.setSheetLabel("Foundation");
	        foundationCluster.setClusterConfiguration(cc);
	        foundationCluster.setVbom(vbvbvb2);
	        
	        clusterList = new ArrayList<Cluster>();
	        
	        PlacementTable placementTable5 = new PlacementTable();
	        vbm2.getYearList().forEach(year -> {
                year.getSiteList().forEach(site -> {
                    try {
                        placementTable5.getPlacementResultList().add(placement(site, year, true, false, 7, foundationCluster));
                        foundationCluster.setPlacementTable(placementTable5);
                    } catch (InconsistentConstraintsException | NotEnoughResourceAvailableException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
	        
	        clusterList.add(foundationCluster);
	        
	        PlacementTable placementTable6 = new PlacementTable();
	        vbm.getYearList().forEach(year -> {
                year.getSiteList().forEach(site -> {
                    try {
                        placementTable6.getPlacementResultList().add(placement(site, year, true, false, 7, cluster));
                        cluster.setPlacementTable(placementTable6);
                    } catch (InconsistentConstraintsException | NotEnoughResourceAvailableException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
	        clusterList.add(cluster);
	        
	        avg = new Averages("Turin", yearNames, clusterList);
            avgList = new ArrayList<Averages>();
            avgList.add(avg);
	        estimationManagement = new EstimationManagement(true, true, 0.0, 0.0, 0.0, 0.0, catalog, threeParCharPath, avgList, clusterList);
	        estimationManagement.estimate();
	        
	        temp = estimationManagement.getEstimation().getEstimationTable().get("Turin").stream()
	        		.filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.THREEPAR_EXPANSION))
	        		.filter(estimationLine -> estimationLine.getComponent().getComponentId().equals("10006")).collect(Collectors.toList());
	        
	        for(EstimationLine el:temp) {
	        	for(EstimationLineDetail eld:el.getEstimationLineDetail()) {
	        		if(eld.getType().equals("foundation"))
	        			quantity = eld.getQuantity();
	        	}
	        }
	        
	        TestCase.assertEquals(1, temp.size());
	        
	        //Test 32A: no VBom required - Pre Processing rules - Error because a parameter is empty
	        File rulesFile = new File(getClass().getResource("/rules-test32A.json").toURI());
	        Gson gson = new Gson();
	        VBomRules vBomRules;
	        final StringBuilder sb1 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb1.append(s));
	        vBomRules = gson.fromJson(sb1.toString().replace("\\", "\\\\"), VBomRules.class);
	        try {
	        	RulesUtil.validateRules(vBomRules, false);
	        } catch(RulesValidationException e) {
	        	TestCase.assertEquals(e.getMessage(), "One or more parameters are empty in the pre processing rule n. 2");
	        }
	        
	        //Test 32B: no VBom required - Pre Processing rules - Error because evaluated cell contains a wrong key
	        rulesFile = new File(getClass().getResource("/rules-test32B.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb2 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb2.append(s));
	        vBomRules = gson.fromJson(sb2.toString().replace("\\", "\\\\"), VBomRules.class);
	        try {
	        	RulesUtil.validateRules(vBomRules, false);
	        } catch(RulesValidationException e) {
	        	TestCase.assertEquals(e.getMessage(), "The key contained in the 'Evaluated Cell' parameter in the pre processing rule n. 2 is not valid.");
	        }
	        
	        //Test 32C: no VBom required - Pre Processing rules - Error because cell to modify contains a wrong key
	        rulesFile = new File(getClass().getResource("/rules-test32C.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb3 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb3.append(s));
	        vBomRules = gson.fromJson(sb3.toString().replace("\\", "\\\\"), VBomRules.class);
	        try {
	        	RulesUtil.validateRules(vBomRules, false);
	        } catch(RulesValidationException e) {
	        	TestCase.assertEquals(e.getMessage(), "The key contained in the 'Cell to Modify' parameter in the pre processing rule n. 2 is not valid.");
	        }
	        
	        //Test 32D: no VBom required - Pre Processing rules - Error because operator contains a wrong key
	        rulesFile = new File(getClass().getResource("/rules-test32D.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb4 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb4.append(s));
	        vBomRules = gson.fromJson(sb4.toString().replace("\\", "\\\\"), VBomRules.class);
	        try {
	        	RulesUtil.validateRules(vBomRules, false);
	        } catch(RulesValidationException e) {
	        	TestCase.assertEquals(e.getMessage(), "The value contained in the 'Operator' parameter in the pre processing rule n. 2 is not allowed. The allowed operators are 'greater', 'equals' and 'less'.");
	        }
	        
	        //Test 32E: no VBom required - Pre Processing rules - Error because action contains a wrong key
	        rulesFile = new File(getClass().getResource("/rules-test32E.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb5 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb5.append(s));
	        vBomRules = gson.fromJson(sb5.toString().replace("\\", "\\\\"), VBomRules.class);
	        try {
	        	RulesUtil.validateRules(vBomRules, false);
	        } catch(RulesValidationException e) {
	        	TestCase.assertEquals(e.getMessage(), "The key contained in the 'Action' parameter in the pre processing rule n. 2 is not valid. The allowed actions are 'overwrite' and 'multiplication'.");
	        }
	        
	        //Test 32F: no VBom required - Pre Processing rules - Error because action argument is a text and cell to modify a number
	        rulesFile = new File(getClass().getResource("/rules-test32F.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb6 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb6.append(s));
	        vBomRules = gson.fromJson(sb6.toString().replace("\\", "\\\\"), VBomRules.class);
	        try {
	        	RulesUtil.validateRules(vBomRules, false);
	        } catch(RulesValidationException e) {
	        	TestCase.assertEquals(e.getMessage(), "The rule n. 2 would multiply a number for a text value.");
	        }
	        
	        //Test 32G: no VBom required - Pre Processing rules - Error because evaluated key is a text but operator is not equals 
	        rulesFile = new File(getClass().getResource("/rules-test32G.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb20 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb20.append(s));
	        vBomRules = gson.fromJson(sb20.toString().replace("\\", "\\\\"), VBomRules.class);
	        try {
	        	RulesUtil.validateRules(vBomRules, true);
	        } catch(RulesValidationException e) {
	        	TestCase.assertEquals(e.getMessage(), "The value contained in the 'Evaluated Cell' parameter in the pre processing rule n. 2 is a text but the operator is not equals.");
	        }
	        
	        Test 32G: no VBom required - Pre Processing rules - Error because evaluated key is a VM_WORKLOAD_TYPE
	          but the VBom is not a customer one.
	        rulesFile = new File(getClass().getResource("/rules-test32H.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb23 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb23.append(s));
	        vBomRules = gson.fromJson(sb23.toString().replace("\\", "\\\\"), VBomRules.class);
	        try {
	        	RulesUtil.validateRules(vBomRules, false);
	        } catch(RulesValidationException e) {
	        	TestCase.assertEquals(e.getMessage(), "'Evaluated Cell' refers to the column 'VNF Workload Type' but the VBom is not a VBom customer.");
	        }
	        
	        //Test 33A: no VBom required - Cluster Selection rules - Error because a parameter is empty
	        rulesFile = new File(getClass().getResource("/rules-test33A.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb7 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb7.append(s));
	        vBomRules = gson.fromJson(sb7.toString().replace("\\", "\\\\"), VBomRules.class);
	        try {
	        	RulesUtil.validateRules(vBomRules, true);
	        } catch(RulesValidationException e) {
	        	TestCase.assertEquals(e.getMessage(), "One or more parameters are empty in the cluster selection rule n. 2");
	        }
	        
	        //Test 33B: no VBom required - Cluster Selection rules - Error because evaluated cell contains a wrong key
	        rulesFile = new File(getClass().getResource("/rules-test33B.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb8 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb8.append(s));
	        vBomRules = gson.fromJson(sb8.toString().replace("\\", "\\\\"), VBomRules.class);
	        try {
	        	RulesUtil.validateRules(vBomRules, true);
	        } catch(RulesValidationException e) {
	        	TestCase.assertEquals(e.getMessage(), "The key contained in the 'Evaluated Cell' parameter in the cluster selection rule n. 2 is not valid.");
	        }
	        
	        //Test 33C: no VBom required - Cluster Selection rules - Error because operator contains a wrong key
	        rulesFile = new File(getClass().getResource("/rules-test33C.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb9 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb9.append(s));
	        vBomRules = gson.fromJson(sb9.toString().replace("\\", "\\\\"), VBomRules.class);
	        try {
	        	RulesUtil.validateRules(vBomRules, true);
	        } catch(RulesValidationException e) {
	        	TestCase.assertEquals(e.getMessage(), "The value contained in the 'Operator' parameter in the cluster selection rule n. 2 is not allowed. The allowed operators are 'greater', 'equals' and 'less'.");
	        }
	        
	        //Test 33D: no VBom required - Cluster Selection rules - Error because the match value is a text but the operator is 'greater' or 'less'
	        rulesFile = new File(getClass().getResource("/rules-test33D.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb10 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb10.append(s));
	        vBomRules = gson.fromJson(sb10.toString().replace("\\", "\\\\"), VBomRules.class);
	        try {
	        	RulesUtil.validateRules(vBomRules, true);
	        } catch(RulesValidationException e) {
	        	TestCase.assertEquals(e.getMessage(), "The value contained in the 'Match Argument' parameter in the cluster selection rule n. 2 is a text but the operator is not equals.");
	        }
	        
	        //Test 33E: no VBom required - Cluster Selection rules - Error because evaluated key is a text but operator is not equals 
	        rulesFile = new File(getClass().getResource("/rules-test33E.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb21 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb21.append(s));
	        vBomRules = gson.fromJson(sb21.toString().replace("\\", "\\\\"), VBomRules.class);
	        try {
	        	RulesUtil.validateRules(vBomRules, true);
	        } catch(RulesValidationException e) {
	        	TestCase.assertEquals(e.getMessage(), "The value contained in the 'Evaluated Cell' parameter in the cluster selection rule n. 2 is a text but the operator is not equals.");
	        }
	        
	        Test 34A: Pre Processing rules - 3 VMs requiring 8 cores. A blade have 30 cores so only one (two with 
	         spare blades) would be needed. The rule would overwrite the core number with 20 so it would need three
	         (four with spare) blades. The operator of the rule is greater.
	        rulesFile = new File(getClass().getResource("/rules-test34A.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb11 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb11.append(s));
	        vBomRules = gson.fromJson(sb11.toString().replace("\\", "\\\\"), VBomRules.class);
	        RulesUtil.validateRules(vBomRules, false);
	        url = getClass().getResource("/FullTest34.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbvbvb = RulesUtil.applyPreProcessingRules(vbvbvb, vBomRules.getPreProcessRules());
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            placement.addSpareBlades(7);
            bladeFactory = new BladeFactory(30.0, 120, 1, 0, 0, 100000.0, 100000.0);
            cluster.setBladeFactory(bladeFactory);
            TestCase.assertEquals(placement.getBladeList().size() == 4, true);
            
            Test 34B: Pre Processing rules - 3 VMs requiring 21 ram in total. A blade have 120 RAM so only one (two with 
	         spare blades) would be needed. The rule would multiply the RAM value for 5 so it would need three
	         (four with spare) blades. The operator of the rule is greater.
            rulesFile = new File(getClass().getResource("/rules-test34B.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb12 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb12.append(s));
	        vBomRules = gson.fromJson(sb12.toString().replace("\\", "\\\\"), VBomRules.class);
	        RulesUtil.validateRules(vBomRules, false);
	        url = getClass().getResource("/FullTest34.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbvbvb = RulesUtil.applyPreProcessingRules(vbvbvb, vBomRules.getPreProcessRules());
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            placement = new PlacementSingleSocket(years.get(0)
                    .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            placement.addSpareBlades(7);
            bladeFactory = new BladeFactory(30.0, 120, 1, 0, 0, 100000.0, 100000.0);
            cluster.setBladeFactory(bladeFactory);
            TestCase.assertEquals(placement.getBladeList().size() == 4, true);
            
            Test 34C: Pre Processing rules - 3 VMs requiring 21 ram. A blade have 120 RAM so only one (two with 
	         spare blades) would be needed. The rule would multiply the RAM value for 10, but only for the third
	         VM, so it would need two (three with spare) blades. The operator of the rule is equals.
            rulesFile = new File(getClass().getResource("/rules-test34C.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb13 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb13.append(s));
	        vBomRules = gson.fromJson(sb13.toString().replace("\\", "\\\\"), VBomRules.class);
	        RulesUtil.validateRules(vBomRules, false);
	        url = getClass().getResource("/FullTest34.xls");
            vbvbvb = Util.readSheet(url.getPath(), 0);
            vbvbvb = RulesUtil.applyPreProcessingRules(vbvbvb, vBomRules.getPreProcessRules());
            vbm = new VbomManagement(vbvbvb);
            vbm.extractDataFromVbom();
            vbm.applyConstraints();
            years = vbm.getYearList();
            placement = new PlacementSingleSocket(years.get(0)
                   .getSiteList().get(0), cluster);
            placement.place();
            placement.bladeCompression();
            placement.addSpareBlades(7);
            bladeFactory = new BladeFactory(30.0, 120, 1, 0, 0, 100000.0, 100000.0);
            cluster.setBladeFactory(bladeFactory);
            TestCase.assertEquals(placement.getBladeList().size() == 3, true);
            
            Test 35: Cluster Selection rules - The VMs should be divided into two clusters, following a rule that would put two in
            the first cluster and two in the other.
            rulesFile = new File(getClass().getResource("/rules-test35.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb14 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb14.append(s));
	        vBomRules = gson.fromJson(sb14.toString().replace("\\", "\\\\"), VBomRules.class);
	        
	        InputConfiguration inputConfig = new InputConfiguration();
	        inputConfig.setFoundationFlag(false);
	        inputConfig.setManagementFlag(false);
	        inputConfig.setEsxiCores(0);
	        inputConfig.setTxrxCores(0);
	        ClusterConfiguration cc1 = new ClusterConfiguration("Cluster1", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
	        ClusterConfiguration cc2 = new ClusterConfiguration("Cluster2", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
	        List<ClusterConfiguration> clusterConfigurationList = new ArrayList<ClusterConfiguration>();
	        clusterConfigurationList.add(cc1);
	        clusterConfigurationList.add(cc2);
	        inputConfig.setClusterConfiguration(clusterConfigurationList);
	        inputConfig.setDefaultAvgBlockSize(0);
	        inputConfig.setDefaultHighThroughput(0);
	        url = getClass().getResource("/FullTest35.xlsx");
	        inputConfig.setvBomFilePath(url.getPath());
	        RulesUtil.validateRules(vBomRules, true);
	        vbvbvb = Util.readCustomerSheet(inputConfig);
	        vbvbvb = RulesUtil.applyPreProcessingRules(vbvbvb, vBomRules.getPreProcessRules());
	        clusterList = RulesUtil.dividePerCluster(vbvbvb, vBomRules, inputConfig, catalog);
	        
	        if(clusterList.size() == 2)
	        	TestCase.assertTrue(true);
	        
	        for(Cluster tempCluster:clusterList) {
	        	if(tempCluster.getSheetLabel().equals("Cluster1")) {
	        		TestCase.assertEquals(tempCluster.getVbom().size(), 2);
	        		
	        		if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFA.VMType1")).count() == 1)
	        			TestCase.assertTrue(true);
	        		
	        		if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFA.VMType2")).count() == 1)
	        			TestCase.assertTrue(true);
	        	}
	        	
	        	if(tempCluster.getSheetLabel().equals("Cluster2")) {
	        		TestCase.assertEquals(tempCluster.getVbom().size(), 2);
	        		
	        		if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFB.VMType1")).count() == 1)
	        			TestCase.assertTrue(true);
	        		
	        		if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFC.VMType1")).count() == 1)
	        			TestCase.assertTrue(true);
	        	}
	        }
	        
	        Test 36: Cluster Selection rules - The VMs should be divided into two clusters, following a rule that would put two in the first
	         cluster and two in the other, but due to the fact of one being in affinity with one of those that would go in a different group,
	         we expect to find three VMs in one group and only one in the other.
            rulesFile = new File(getClass().getResource("/rules-test36.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb15 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb15.append(s));
	        vBomRules = gson.fromJson(sb15.toString().replace("\\", "\\\\"), VBomRules.class);
	        
	        inputConfig = new InputConfiguration();
	        inputConfig.setFoundationFlag(false);
	        inputConfig.setManagementFlag(false);
	        inputConfig.setEsxiCores(0);
	        inputConfig.setTxrxCores(0);
	        cc1 = new ClusterConfiguration("Cluster1", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
	        cc2 = new ClusterConfiguration("Cluster2", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
	        clusterConfigurationList = new ArrayList<ClusterConfiguration>();
	        clusterConfigurationList.add(cc1);
	        clusterConfigurationList.add(cc2);
	        inputConfig.setClusterConfiguration(clusterConfigurationList);
	        inputConfig.setDefaultAvgBlockSize(0);
	        inputConfig.setDefaultHighThroughput(0);
	        url = getClass().getResource("/FullTest36.xlsx");
	        inputConfig.setvBomFilePath(url.getPath());
	        RulesUtil.validateRules(vBomRules, true);
	        vbvbvb = Util.readCustomerSheet(inputConfig);
	        vbvbvb = RulesUtil.applyPreProcessingRules(vbvbvb, vBomRules.getPreProcessRules());
	        clusterList = RulesUtil.dividePerCluster(vbvbvb, vBomRules, inputConfig, catalog);
	        
	        if(clusterList.size() == 2)
	        	TestCase.assertTrue(true);
	        
	        for(Cluster tempCluster:clusterList) {
	        	if(tempCluster.getSheetLabel().equals("Cluster1")) {
	        		TestCase.assertEquals(tempCluster.getVbom().size(), 3);
        			
	        		if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFA.VMType1")).count() == 1)
	        			TestCase.assertTrue(true);
	        		
	        		if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFA.VMType2")).count() == 1)
	        			TestCase.assertTrue(true);
	        		
	        		if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFB.VMType1")).count() == 1)
	        			TestCase.assertTrue(true);
	        	}
	        	
	        	if(tempCluster.getSheetLabel().equals("Cluster2")) {
        			TestCase.assertEquals(tempCluster.getVbom().size(), 1);

        			if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFC.VMType1")).count() == 1)
	        			TestCase.assertTrue(true);
	        	}
	        }
	        
	        Test 37: Cluster Selection rules - The first three VMs have the same VNF Name or are in affinity, for one, the rule return
	          success, and they should be put in the same cluster. For the last three, even if they share the same name, the rule should
			  return failure, and since they use different rules we expect them to be put in different clusters.
            rulesFile = new File(getClass().getResource("/rules-test37.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb16 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb16.append(s));
	        vBomRules = gson.fromJson(sb16.toString().replace("\\", "\\\\"), VBomRules.class);
	        
	        inputConfig = new InputConfiguration();
	        inputConfig.setFoundationFlag(false);
	        inputConfig.setManagementFlag(false);
	        inputConfig.setEsxiCores(0);
	        inputConfig.setTxrxCores(0);
	        cc1 = new ClusterConfiguration("Cluster1", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
	        cc2 = new ClusterConfiguration("Cluster2", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
	        ClusterConfiguration cc3 = new ClusterConfiguration("Cluster3", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
	        clusterConfigurationList = new ArrayList<ClusterConfiguration>();
	        clusterConfigurationList.add(cc1);
	        clusterConfigurationList.add(cc2);
	        clusterConfigurationList.add(cc3);
	        inputConfig.setClusterConfiguration(clusterConfigurationList);
	        inputConfig.setDefaultAvgBlockSize(0);
	        inputConfig.setDefaultHighThroughput(0);
	        url = getClass().getResource("/FullTest37.xlsx");
	        inputConfig.setvBomFilePath(url.getPath());
	        RulesUtil.validateRules(vBomRules, true);
	        vbvbvb = Util.readCustomerSheet(inputConfig);
	        vbvbvb = RulesUtil.applyPreProcessingRules(vbvbvb, vBomRules.getPreProcessRules());
	        clusterList = RulesUtil.dividePerCluster(vbvbvb, vBomRules, inputConfig, catalog);
	        
	        if(clusterList.size() == 2)
	        	TestCase.assertTrue(true);
	        
	        for(Cluster tempCluster:clusterList) {
	        	if(tempCluster.getSheetLabel().equals("Cluster1")) {
	        		TestCase.assertEquals(tempCluster.getVbom().size(), 3);
	        		
	        		if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFA.VMType1")).count() == 1)
	        			TestCase.assertTrue(true);
	        		
	        		if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFA.VMType2")).count() == 1)
	        			TestCase.assertTrue(true);
	        		
	        		if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFB.VMType1")).count() == 1)
	        			TestCase.assertTrue(true);
	        	}
	        	
	        	if(tempCluster.getSheetLabel().equals("Cluster2")) {
       			TestCase.assertEquals(tempCluster.getVbom().size(), 2);

	        		if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFC.VMType1")).count() == 1)
	        			TestCase.assertTrue(true);
	        		
	        		if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFC.VMType2")).count() == 1)
	        			TestCase.assertTrue(true);
	        	}
	        	
	        	if(tempCluster.getSheetLabel().equals("Cluster3")) {
	       			TestCase.assertEquals(tempCluster.getVbom().size(), 1);

		        		if(tempCluster.getVbom().stream().filter(vbom -> vbom.getCompleteName().equals("VNFC.VMType3")).count() == 1)
		        			TestCase.assertTrue(true);
		        	}
	        }
	        
	        Test 38: Cluster Selection rules - Following the rules two VMs with the same VMName would be put in different clusters.
	          The tool should then throw an exception.
            rulesFile = new File(getClass().getResource("/rules-test38.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb17 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb17.append(s));
	        vBomRules = gson.fromJson(sb17.toString().replace("\\", "\\\\"), VBomRules.class);
	        
	        inputConfig = new InputConfiguration();
	        inputConfig.setFoundationFlag(false);
	        inputConfig.setManagementFlag(false);
	        inputConfig.setEsxiCores(0);
	        inputConfig.setTxrxCores(0);
	        cc1 = new ClusterConfiguration("Cluster1", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
	        cc2 = new ClusterConfiguration("Cluster2", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
	        clusterConfigurationList = new ArrayList<ClusterConfiguration>();
	        clusterConfigurationList.add(cc1);
	        clusterConfigurationList.add(cc2);
	        inputConfig.setClusterConfiguration(clusterConfigurationList);
	        inputConfig.setDefaultAvgBlockSize(0);
	        inputConfig.setDefaultHighThroughput(0);
	        url = getClass().getResource("/FullTest38.xlsx");
	        inputConfig.setvBomFilePath(url.getPath());
	        RulesUtil.validateRules(vBomRules, true);
	        vbvbvb = Util.readCustomerSheet(inputConfig);
	        vbvbvb = RulesUtil.applyPreProcessingRules(vbvbvb, vBomRules.getPreProcessRules());
	        
	        try {
	        	clusterList = RulesUtil.dividePerCluster(vbvbvb, vBomRules, inputConfig, catalog);
	        } catch(UnexpectedSituationOccurredException e) {
	        	TestCase.assertTrue(true);
	        }
	        
	        Test 39: Cluster Selection rules - Following the rules two VMs in affinity would be put in different clusters. The tool
	          should then throw an exception.
            rulesFile = new File(getClass().getResource("/rules-test39.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb18 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb18.append(s));
	        vBomRules = gson.fromJson(sb18.toString().replace("\\", "\\\\"), VBomRules.class);
	        
	        inputConfig = new InputConfiguration();
	        inputConfig.setFoundationFlag(false);
	        inputConfig.setManagementFlag(false);
	        inputConfig.setEsxiCores(0);
	        inputConfig.setTxrxCores(0);
	        cc1 = new ClusterConfiguration("Cluster1", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
	        cc2 = new ClusterConfiguration("Cluster2", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
	        clusterConfigurationList = new ArrayList<ClusterConfiguration>();
	        clusterConfigurationList.add(cc1);
	        clusterConfigurationList.add(cc2);
	        inputConfig.setClusterConfiguration(clusterConfigurationList);
	        inputConfig.setDefaultAvgBlockSize(0);
	        inputConfig.setDefaultHighThroughput(0);
	        url = getClass().getResource("/FullTest39.xlsx");
	        inputConfig.setvBomFilePath(url.getPath());
	        RulesUtil.validateRules(vBomRules, true);
	        vbvbvb = Util.readCustomerSheet(inputConfig);
	        vbvbvb = RulesUtil.applyPreProcessingRules(vbvbvb, vBomRules.getPreProcessRules());
	        
	        try {
	        	clusterList = RulesUtil.dividePerCluster(vbvbvb, vBomRules, inputConfig, catalog);
	        } catch(UnexpectedSituationOccurredException e) {
	        	TestCase.assertTrue(true);
	        }
	        
	        Test 40: Cluster Selection rules - Test with foundation.
            rulesFile = new File(getClass().getResource("/rules-test40.json").toURI());
	        gson = new Gson();
	        final StringBuilder sb19 = new StringBuilder();
            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb19.append(s));
	        vBomRules = gson.fromJson(sb19.toString().replace("\\", "\\\\"), VBomRules.class);
	        
	        inputConfig = new InputConfiguration();
	        inputConfig.setFoundationFlag(false);
	        inputConfig.setManagementFlag(false);
	        inputConfig.setEsxiCores(0);
	        inputConfig.setTxrxCores(0);
	        cc1 = new ClusterConfiguration("Cluster1", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
	        ClusterConfiguration foundationConfiguration = new ClusterConfiguration("Foundation", 1.0, 1.0, 1.0, false, false, 1.0, 1.0);
	        clusterConfigurationList = new ArrayList<ClusterConfiguration>();
	        clusterConfigurationList.add(cc1);
	        clusterConfigurationList.add(foundationConfiguration);
	        inputConfig.setClusterConfiguration(clusterConfigurationList);
	        inputConfig.setDefaultAvgBlockSize(0);
	        inputConfig.setDefaultHighThroughput(0);
	        inputConfig.setManagementFlag(true);
	        inputConfig.setFoundationFlag(true);
	        url = getClass().getResource("/FullTest40.xlsx");
	        inputConfig.setvBomFilePath(url.getPath());
	        RulesUtil.validateRules(vBomRules, true);
	        vbvbvb = Util.readCustomerSheet(inputConfig);
	        vbvbvb = RulesUtil.applyPreProcessingRules(vbvbvb, vBomRules.getPreProcessRules());
	        
	        try {
	        	clusterList = RulesUtil.dividePerCluster(vbvbvb, vBomRules, inputConfig, catalog);
	        	
	        	for(Cluster clusterX:clusterList) {
	        		vbm = new VbomManagement(clusterX);
	                vbm.extractDataFromVbom();
	                vbm.applyConstraints();
	                years = vbm.getYearList();
	                
	                bladeFactory = new BladeFactory(40.0, 408, 1, 0, 0, 10000000.0, 10000000.0);
	    	        bladeFactory.setEsxiCores(0.0);
	    	        bladeFactory.setTxrxCores(0.0);
	    	        clusterX.setBladeFactory(bladeFactory);
	        		
	        		placement = new PlacementExtraSocket(years.get(0)
		                    .getSiteList().get(0), clusterX);
		            placement.place();
		            placement.bladeCompression();
		            placement.addSpareBlades(7);
		            
		            PlacementTable placementTable7 = new PlacementTable();
		            vbm.getYearList().forEach(year -> {
		                year.getSiteList().forEach(site -> {
		                    try {
		                    	placementTable7.getPlacementResultList().add(placement(site, year, true, false, 7, clusterX));
		                    	clusterX.setPlacementTable(placementTable7);
		                    } catch (InconsistentConstraintsException | NotEnoughResourceAvailableException e) {
		                        throw new RuntimeException(e);
		                    }
		                });
		            });
	        	}

	            yearNames = Util.checkYearsConsistency(url.getPath(), true);
	            avg = new Averages("Site 2", yearNames, clusterList);
	            avgList = new ArrayList<Averages>();
	            avgList.add(avg);
	            estimationManagement = new EstimationManagement(true, true, 0.0, 0.0, 0.0, 0.0, catalog, threeParCharPath, avgList, clusterList);
		        estimationManagement.estimate();
		        
		        temp = estimationManagement.getEstimation().getEstimationTable().get("Site 2").stream()
		        		.filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.BLADE))
		        		.filter(estimationLine -> estimationLine.getComponent().getComponentId().equals(catalog.getBlade().getComponentId())).collect(Collectors.toList());
		        
		        for(EstimationLine el:temp) {
		        	for(EstimationLineDetail eld:el.getEstimationLineDetail()) {
		        		if(eld.getLineReference().equals("End Of FY1")) {
		        			TestCase.assertEquals(eld.getQuantity(), 2);
		        		}
		        		
		        		if(eld.getLineReference().equals("foundation")) {
		        			TestCase.assertEquals(eld.getQuantity(), 3);
		        		}
		        	}
		        }
		        
		        Test 41: no VBom required - Pre Processing rules - Error because evaluated cell refers to
		          'VM_WORKLOAD_TYPE' but the input VBom is not a customer one.
		        rulesFile = new File(getClass().getResource("/rules-test41.json").toURI());
		        gson = new Gson();
		        final StringBuilder sb22 = new StringBuilder();
	            Files.lines(Paths.get(rulesFile.getAbsolutePath())).forEach(s -> sb22.append(s));
		        vBomRules = gson.fromJson(sb22.toString().replace("\\", "\\\\"), VBomRules.class);
		        try {
		        	RulesUtil.validateRules(vBomRules, true);
		        } catch(RulesValidationException e) {
		        	TestCase.assertEquals(e.getMessage(), "'Evaluated Cell' refers to the column 'VNF Workload Type' but the VBom is not a VBom customer.");
		        }
	        } catch(UnexpectedSituationOccurredException e) {
	        	TestCase.assertTrue(true);
	        }
        } catch (Exception e) {
            logger.error("errore: " + e.getMessage());
            e.printStackTrace();
            TestCase.assertTrue(false);
        }
    }
    
    public PlacementResult placement(Site site, Year year, boolean extraSocket, boolean numaFlag, int spareNumber, Cluster cluster) throws InconsistentConstraintsException, NotEnoughResourceAvailableException {

        Placement placement;
        PlacementResult placementResult = new PlacementResult(year.getYearName(), site.getSiteName(), new ArrayList<Blade>());

        if (extraSocket && !numaFlag) {
            placement = new PlacementExtraSocket(site, cluster);
        } else {
            placement = new PlacementSingleSocket(site, cluster);
        }

        logger.debug("\n\nDefined " + placement.getGroupList().size() + " groups for: " + site.getSiteName() + "/" + year.getYearName());

        logger.debug("Placement on " + site.getSiteName() + "/" + year.getYearName());
        placement.place();
        logger.debug("Blade Compression on " + site.getSiteName() + "/" + year.getYearName());
        placement.bladeCompression();
        if (spareNumber != 0) {
            logger.debug("Adding Spare Blade on " + site.getSiteName() + "/" + year.getYearName());
            placement.addSpareBlades(spareNumber);
        }

        logger.debug("Setting Placement Result on " + site.getSiteName() + "/" + year.getYearName());
        placementResult.setPlacement(placement.getBladeList());
        return placementResult;
    }
*/
}
