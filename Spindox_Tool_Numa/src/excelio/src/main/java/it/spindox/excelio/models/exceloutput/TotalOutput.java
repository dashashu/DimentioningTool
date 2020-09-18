package excelio.src.main.java.it.spindox.excelio.models.exceloutput;

import java.util.ArrayList;
import java.util.List;

import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.OutputTable;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.TotalOutputCluster;

public class TotalOutput {
	List<TotalOutputCluster> cpuTotalOutputCluster = new ArrayList<TotalOutputCluster>();
	List<TotalOutputCluster> cpuDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
	List<OutputTable> cpuTotalOutput = new ArrayList<OutputTable>();
	List<OutputTable> cpuDeltaOutput = new ArrayList<OutputTable>();
	
	List<TotalOutputCluster> ramTotalOutputCluster = new ArrayList<TotalOutputCluster>();
	List<TotalOutputCluster> ramDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
	List<OutputTable> ramTotalOutput = new ArrayList<OutputTable>();
	List<OutputTable> ramDeltaOutput = new ArrayList<OutputTable>();
	
	List<TotalOutputCluster> storageTotalOutputCluster = new ArrayList<TotalOutputCluster>();
	List<TotalOutputCluster> storageDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
	List<OutputTable> storageTotalOutput = new ArrayList<OutputTable>();
	List<OutputTable> storageDeltaOutput = new ArrayList<OutputTable>();
	
	List<TotalOutputCluster> iopsTotalOutputCluster = new ArrayList<TotalOutputCluster>();
	List<TotalOutputCluster> iopsDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
	List<OutputTable> iopsTotalOutput = new ArrayList<OutputTable>();
	List<OutputTable> iopsDeltaOutput = new ArrayList<OutputTable>();
	
	List<TotalOutputCluster> bwTotalOutputCluster = new ArrayList<TotalOutputCluster>();
	List<TotalOutputCluster> bwDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
	List<OutputTable> bwTotalOutput = new ArrayList<OutputTable>();
	List<OutputTable> bwDeltaOutput = new ArrayList<OutputTable>();
	
	List<TotalOutputCluster> nsTotalOutputCluster = new ArrayList<TotalOutputCluster>();
	List<TotalOutputCluster> nsDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
	List<OutputTable> nsTotalOutput = new ArrayList<OutputTable>();
	List<OutputTable> nsDeltaOutput = new ArrayList<OutputTable>();
	
	List<TotalOutputCluster> ewTotalOutputCluster = new ArrayList<TotalOutputCluster>();
	List<TotalOutputCluster> ewDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
	List<OutputTable> ewTotalOutput = new ArrayList<OutputTable>();
	List<OutputTable> ewDeltaOutput = new ArrayList<OutputTable>();
	
	public List<TotalOutputCluster> getCpuTotalOutputCluster() {
		return cpuTotalOutputCluster;
	}
	public void setCpuTotalOutputCluster(List<TotalOutputCluster> cpuTotalOutputCluster) {
		this.cpuTotalOutputCluster = cpuTotalOutputCluster;
	}
	public List<TotalOutputCluster> getCpuDeltaOutputCluster() {
		return cpuDeltaOutputCluster;
	}
	public void setCpuDeltaOutputCluster(List<TotalOutputCluster> cpuDeltaOutputCluster) {
		this.cpuDeltaOutputCluster = cpuDeltaOutputCluster;
	}
	public List<OutputTable> getCpuTotalOutput() {
		return cpuTotalOutput;
	}
	public void setCpuTotalOutput(List<OutputTable> cpuTotalOutput) {
		this.cpuTotalOutput = cpuTotalOutput;
	}
	public List<OutputTable> getCpuDeltaOutput() {
		return cpuDeltaOutput;
	}
	public void setCpuDeltaOutput(List<OutputTable> cpuDeltaOutput) {
		this.cpuDeltaOutput = cpuDeltaOutput;
	}
	public List<TotalOutputCluster> getRamTotalOutputCluster() {
		return ramTotalOutputCluster;
	}
	public void setRamTotalOutputCluster(List<TotalOutputCluster> ramTotalOutputCluster) {
		this.ramTotalOutputCluster = ramTotalOutputCluster;
	}
	public List<TotalOutputCluster> getRamDeltaOutputCluster() {
		return ramDeltaOutputCluster;
	}
	public void setRamDeltaOutputCluster(List<TotalOutputCluster> ramDeltaOutputCluster) {
		this.ramDeltaOutputCluster = ramDeltaOutputCluster;
	}
	public List<OutputTable> getRamTotalOutput() {
		return ramTotalOutput;
	}
	public void setRamTotalOutput(List<OutputTable> ramTotalOutput) {
		this.ramTotalOutput = ramTotalOutput;
	}
	public List<OutputTable> getRamDeltaOutput() {
		return ramDeltaOutput;
	}
	public void setRamDeltaOutput(List<OutputTable> ramDeltaOutput) {
		this.ramDeltaOutput = ramDeltaOutput;
	}
	public List<TotalOutputCluster> getStorageTotalOutputCluster() {
		return storageTotalOutputCluster;
	}
	public void setStorageTotalOutputCluster(List<TotalOutputCluster> storageTotalOutputCluster) {
		this.storageTotalOutputCluster = storageTotalOutputCluster;
	}
	public List<TotalOutputCluster> getStorageDeltaOutputCluster() {
		return storageDeltaOutputCluster;
	}
	public void setStorageDeltaOutputCluster(List<TotalOutputCluster> storageDeltaOutputCluster) {
		this.storageDeltaOutputCluster = storageDeltaOutputCluster;
	}
	public List<OutputTable> getStorageTotalOutput() {
		return storageTotalOutput;
	}
	public void setStorageTotalOutput(List<OutputTable> storageTotalOutput) {
		this.storageTotalOutput = storageTotalOutput;
	}
	public List<OutputTable> getStorageDeltaOutput() {
		return storageDeltaOutput;
	}
	public void setStorageDeltaOutput(List<OutputTable> storageDeltaOutput) {
		this.storageDeltaOutput = storageDeltaOutput;
	}
	public List<TotalOutputCluster> getIopsTotalOutputCluster() {
		return iopsTotalOutputCluster;
	}
	public void setIopsTotalOutputCluster(List<TotalOutputCluster> iopsTotalOutputCluster) {
		this.iopsTotalOutputCluster = iopsTotalOutputCluster;
	}
	public List<TotalOutputCluster> getIopsDeltaOutputCluster() {
		return iopsDeltaOutputCluster;
	}
	public void setIopsDeltaOutputCluster(List<TotalOutputCluster> iopsDeltaOutputCluster) {
		this.iopsDeltaOutputCluster = iopsDeltaOutputCluster;
	}
	public List<OutputTable> getIopsTotalOutput() {
		return iopsTotalOutput;
	}
	public void setIopsTotalOutput(List<OutputTable> iopsTotalOutput) {
		this.iopsTotalOutput = iopsTotalOutput;
	}
	public List<OutputTable> getIopsDeltaOutput() {
		return iopsDeltaOutput;
	}
	public void setIopsDeltaOutput(List<OutputTable> iopsDeltaOutput) {
		this.iopsDeltaOutput = iopsDeltaOutput;
	}
	public List<TotalOutputCluster> getBwTotalOutputCluster() {
		return bwTotalOutputCluster;
	}
	public void setBwTotalOutputCluster(List<TotalOutputCluster> bwTotalOutputCluster) {
		this.bwTotalOutputCluster = bwTotalOutputCluster;
	}
	public List<TotalOutputCluster> getBwDeltaOutputCluster() {
		return bwDeltaOutputCluster;
	}
	public void setBwDeltaOutputCluster(List<TotalOutputCluster> bwDeltaOutputCluster) {
		this.bwDeltaOutputCluster = bwDeltaOutputCluster;
	}
	public List<OutputTable> getBwTotalOutput() {
		return bwTotalOutput;
	}
	public void setBwTotalOutput(List<OutputTable> bwTotalOutput) {
		this.bwTotalOutput = bwTotalOutput;
	}
	public List<OutputTable> getBwDeltaOutput() {
		return bwDeltaOutput;
	}
	public void setBwDeltaOutput(List<OutputTable> bwDeltaOutput) {
		this.bwDeltaOutput = bwDeltaOutput;
	}
	public List<TotalOutputCluster> getNsTotalOutputCluster() {
		return nsTotalOutputCluster;
	}
	public void setNsTotalOutputCluster(List<TotalOutputCluster> nsTotalOutputCluster) {
		this.nsTotalOutputCluster = nsTotalOutputCluster;
	}
	public List<TotalOutputCluster> getNsDeltaOutputCluster() {
		return nsDeltaOutputCluster;
	}
	public void setNsDeltaOutputCluster(List<TotalOutputCluster> nsDeltaOutputCluster) {
		this.nsDeltaOutputCluster = nsDeltaOutputCluster;
	}
	public List<OutputTable> getNsTotalOutput() {
		return nsTotalOutput;
	}
	public void setNsTotalOutput(List<OutputTable> nsTotalOutput) {
		this.nsTotalOutput = nsTotalOutput;
	}
	public List<OutputTable> getNsDeltaOutput() {
		return nsDeltaOutput;
	}
	public void setNsDeltaOutput(List<OutputTable> nsDeltaOutput) {
		this.nsDeltaOutput = nsDeltaOutput;
	}
	public List<TotalOutputCluster> getEwTotalOutputCluster() {
		return ewTotalOutputCluster;
	}
	public void setEwTotalOutputCluster(List<TotalOutputCluster> ewTotalOutputCluster) {
		this.ewTotalOutputCluster = ewTotalOutputCluster;
	}
	public List<TotalOutputCluster> getEwDeltaOutputCluster() {
		return ewDeltaOutputCluster;
	}
	public void setEwDeltaOutputCluster(List<TotalOutputCluster> ewDeltaOutputCluster) {
		this.ewDeltaOutputCluster = ewDeltaOutputCluster;
	}
	public List<OutputTable> getEwTotalOutput() {
		return ewTotalOutput;
	}
	public void setEwTotalOutput(List<OutputTable> ewTotalOutput) {
		this.ewTotalOutput = ewTotalOutput;
	}
	public List<OutputTable> getEwDeltaOutput() {
		return ewDeltaOutput;
	}
	public void setEwDeltaOutput(List<OutputTable> ewDeltaOutput) {
		this.ewDeltaOutput = ewDeltaOutput;
	}
}
