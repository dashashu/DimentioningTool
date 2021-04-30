package core.src.main.java.it.spindox.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Site;
import commons.src.main.java.it.spindox.model.placementAndEstimation.VirtualMachine;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Year;
import commons.src.main.java.it.spindox.model.vbom.VBom;
import commons.src.main.java.it.spindox.model.vbom.VBomYear;

public class Utils {
	static InputConfiguration inputconfig = new InputConfiguration();
	public static List<Year> setup(List<VBom> vBom)
	{
		List<Year> years = new ArrayList<Year>();
		
		//Scorro righe dell'excel
		for(VBom vbom:vBom)
		{
			//Scorro anni
			for(int i=1; i<=vbom.getvBomYearList().size(); i++)
			{
				Year tempYear = new Year();
				
				if(years.get(i) != null)
					tempYear = years.get(i);
				
				VBomYear vbomYear = vbom.getvBomYearList().get(i);
				
				tempYear.setNumOfYear(i);
				
				int numberOfInstances = vbomYear.getVnfPerSite();
				int numberOfClones = vbomYear.getNumberOfVnfPerTypeAndPerInstance();
				
				List<String> siteNameList = vbomYear.getSiteList();

//				List<Site> sites = new ArrayList<Site>();
				
//				if(tempYear.getSiteList() != null)
//					sites = tempYear.getSiteList();
				
				//Scorro siti
				for(int n=0; n<=siteNameList.size(); n++)
				{
					String nomesito = siteNameList.get(n);
//					Site tempSite = new Site();
					
//					if(tempYear.getSiteList().containsKey(nomesito))
					Site tempSite = tempYear.getSiteList().stream().filter(s->s.getSiteName().equalsIgnoreCase(nomesito)).findFirst().get();
//					else
//						tempSite.setSiteName(nomesito);
					
					if(tempSite==null)
					{
						tempSite = new Site();
						tempYear.getSiteList().add(tempSite);
					}

					List<VirtualMachine> vmPerSite = new ArrayList<VirtualMachine>();
					
					for(int k=1; k<=numberOfInstances; k++)
						for(int m=1; m<=numberOfClones; m++)
							vmPerSite.add(new VirtualMachine(k, m));
					
//					tempSite.placement();
					tempSite.addVmList(vmPerSite);
//					sites.add(tempSite);
				}
				
//				tempYear.addSites(sites);
//				years.set(i, tempYear);
			}
		}
		
		return years;
	}
	
	public static List<VirtualMachine> copyVmList(List<VirtualMachine> a) {
		List<VirtualMachine> b = new ArrayList<VirtualMachine>();
		
		for(VirtualMachine tmp:a) {
			VirtualMachine vm = new VirtualMachine(tmp.getInstance(), tmp.getClone(), tmp.getVmName());
			vm.setAaf(tmp.getAaf());
			vm.setAff(tmp.getAff());
			vm.setAms(tmp.isAms());
			vm.setCore(tmp.getCore());
			vm.setHardDisk(tmp.getHardDisk());
			vm.setHighThroughputCore(inputconfig.getDefaultHighThroughput());//(tmp.getHighThroughputCore());
			vm.setRam(tmp.getRam());
	
			b.add(vm);
		}
		
		return b;
	}
	
	public static int calcolateSpare(int bladeNumber, int spareNumber) {
		int spare = (int)(Math.ceil((float)bladeNumber/spareNumber));
		return spare;
	}

//	public List<Blade> sortBlade(List<Blade> list, List<String> sortingOrder) {
//		for(int i=0; i<sortingOrder.size(); i++)
//			for(int j=i; j<sortingOrder.size(); j++)
//				if(list.get(i).isLessThan(list.get(j)))
//					
//	}
}
