package com.arracso.ElfneinBot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arracso.ElfneinBot.model.DateSolution;

public interface DateSolutionRepository extends JpaRepository<DateSolution, String> {
	
	Optional<DateSolution> findByCardCode(String cardCode);
	
}
