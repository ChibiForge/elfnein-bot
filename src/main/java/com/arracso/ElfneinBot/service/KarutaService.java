package com.arracso.ElfneinBot.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.arracso.ElfneinBot.model.DateSolution;
import com.arracso.ElfneinBot.model.NodeInfo;
import com.arracso.ElfneinBot.model.ShogunInfo;

public interface KarutaService {
	
	public DateSolution getSavedDateSolution(String cardCode);
	public void saveDateSolution(DateSolution dateSolution);
	
	public NodeInfo getNodeInfo(String node);
	public void updateNodeInfo(NodeInfo nodeInfo);
	public void updateNodeHolders(Map<String, String> newHolders);
	public List<NodeInfo> getAllNodesOrderedByGrace();
	public ShogunInfo getShogunInfo(String id);
	public void setShogunInfo(ShogunInfo shogunInfo);
	public Instant getDateSolveUptime(String string);
	public List<ShogunInfo> getAllShoguns();
}
