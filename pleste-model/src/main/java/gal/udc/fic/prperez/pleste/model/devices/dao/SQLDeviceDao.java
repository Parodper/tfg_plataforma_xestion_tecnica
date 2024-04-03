package gal.udc.fic.prperez.pleste.model.devices.dao;

import gal.udc.fic.prperez.pleste.model.devices.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SQLDeviceDao extends JpaRepository<Device, Long> {
}