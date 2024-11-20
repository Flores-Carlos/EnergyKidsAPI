package com.gs.energykids.repository;

import com.gs.energykids.model.DicaEconomia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DicaEconomiaRepository extends JpaRepository<DicaEconomia, Long> {

}
