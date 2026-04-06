package com.chibiforge.elfnein.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chibiforge.elfnein.model.DateSolution;

public interface DateSolutionRepository extends JpaRepository<DateSolution, String> {
	
	Optional<DateSolution> findByCardCode(String cardCode);
	
}
