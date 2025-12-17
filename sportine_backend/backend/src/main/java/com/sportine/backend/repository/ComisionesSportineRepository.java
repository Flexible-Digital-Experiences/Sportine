package com.sportine.backend.repository;

import com.sportine.backend.model.ComisionesSportine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComisionesSportineRepository extends JpaRepository<ComisionesSportine, Integer> {
}