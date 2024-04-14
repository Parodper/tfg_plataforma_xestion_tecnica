package gal.udc.fic.prperez.pleste.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SQLTimerDao extends JpaRepository<Timer, Long> {}