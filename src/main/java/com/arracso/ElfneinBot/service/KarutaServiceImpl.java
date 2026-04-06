package com.arracso.ElfneinBot.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arracso.ElfneinBot.model.DateSolution;
import com.arracso.ElfneinBot.model.NodeInfo;
import com.arracso.ElfneinBot.model.ShogunInfo;
import com.arracso.ElfneinBot.repository.DateSolutionRepository;
import com.arracso.ElfneinBot.repository.NodeInfoRepository;
import com.arracso.ElfneinBot.repository.ShogunInfoRepository;

@Service
public class KarutaServiceImpl implements KarutaService {
	
	@Autowired
	private DateSolutionRepository dateSolutionRepository;
	
	@Autowired
	private NodeInfoRepository nodeInfoRepository;
	
	@Autowired
	private ShogunInfoRepository shogunInfoRepository;

	@Override
	public DateSolution getSavedDateSolution(String cardCode) {
		return dateSolutionRepository.findByCardCode(cardCode).orElse(null);
	}

	@Override
	public void saveDateSolution(DateSolution dateSolution) {
		dateSolutionRepository.save(dateSolution);
		
	}
	
	@Override
	public List<NodeInfo> getAllNodesOrderedByGrace() {
		return nodeInfoRepository.findAllByOrderByGraceAsc();
	}
	
	@Override
	public NodeInfo getNodeInfo(String node) {
		return nodeInfoRepository.findById(node).orElse(null);
	}

	@Override
	public void updateNodeInfo(NodeInfo nodeInfo) {
		nodeInfoRepository.save(nodeInfo);
	}
	
	@Transactional
	public void updateNodeHolders(Map<String, String> newHolders) {
		List<NodeInfo> nodes = nodeInfoRepository.findAllById(newHolders.keySet());
		
		for (NodeInfo node : nodes) {
			String newHolder = newHolders.get(node.getNode());
			if (!node.getHolder().equals(newHolder)) {
				node.setHolder(newHolder);
				node.setGrace(100);
				node.setDecay(null);
				node.setUpdated(Timestamp.from(Instant.now()));
			}
		}

		nodeInfoRepository.saveAll(nodes);
	}
	
	@Override
	public ShogunInfo getShogunInfo(String id) {
		return shogunInfoRepository.findById(id).orElse(null);
	}
	
	@Override
	public void setShogunInfo(ShogunInfo shogunInfo) {
		shogunInfoRepository.save(shogunInfo);
	}

	@Override
	public Instant getDateSolveUptime(String cardCode) {
		DateSolution dateSolution = dateSolutionRepository.findByCardCode(cardCode).orElse(null);
		Instant uptime = null;
		if(dateSolution == null) { 
			dateSolution = new DateSolution();
			dateSolution.setCardCode(cardCode);
			dateSolution.setUpdated(null);
			dateSolution.setCalled(new Timestamp(System.currentTimeMillis()));
		} else {
			Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
			if(dateSolution.getCalled().toInstant().isAfter(fiveMinutesAgo)) {
				uptime = dateSolution.getCalled().toInstant().plus(5, ChronoUnit.MINUTES);
			} else {
				dateSolution.setCalled(new Timestamp(System.currentTimeMillis()));
			}
		}
		
		dateSolutionRepository.save(dateSolution);
		
		return uptime;
	}

	@Override
	public List<ShogunInfo> getAllShoguns() {
		return shogunInfoRepository.findAll(Sort.by(Sort.Direction.ASC, "side"));
	}

}
