package gal.udc.fic.prperez.pleste.service.dao.component;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SQLComponentDao extends JpaRepository<Component, Long> {}