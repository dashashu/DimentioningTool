package commons.src.main.java.it.spindox.model.vbom;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VBomGroup {
	private List<VBom> vBomList = new ArrayList<VBom>();

	public List<VBom> getVBomList() {
		return vBomList;
	}

	public void setVBomList(List<VBom> VBomList) {
		this.vBomList = VBomList;
	}
	
	public void add(VBom VBom) {
		this.vBomList.add(VBom);
	}
	
	public void addAll(Set<VBom> VBomList) {
		for(VBom temp:VBomList)
			this.vBomList.add(temp);
	}
}
