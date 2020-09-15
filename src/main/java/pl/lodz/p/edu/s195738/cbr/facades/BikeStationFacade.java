/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import pl.lodz.p.edu.s195738.cbr.entities.BikeStation;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mow.BikeAlreadyExistsException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mow.BikeStationConcurrentEditException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mow.BikeStationDoesNotExistException;

/**
 *
 * @author Mateusz Wiśniewski
 */
@Stateless
public class BikeStationFacade extends AbstractFacade<BikeStation> {

    @PersistenceContext(unitName = "cbradminPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BikeStationFacade() {
        super(BikeStation.class);
    }
    
    @Override
    public void create(BikeStation bikeStation) throws BaseApplicationException {
        try {
            super.create(bikeStation);
            getEntityManager().flush();
        } catch (PersistenceException pe) {
            if (pe.getCause().toString().contains("bike_station_identifier_key")) {
                throw new BikeAlreadyExistsException(pe);
            } else {
                throw new BaseApplicationException(pe);
            }
        }
    }

    @Override
    public void edit(BikeStation bikeStation) throws BaseApplicationException {
        try {
            super.edit(bikeStation);
            getEntityManager().flush();
        } catch (OptimisticLockException ex) {
            throw new BikeStationConcurrentEditException(ex);
        } catch (PersistenceException pe) {
            if (pe.getCause().toString().contains("bike_station_identifier_key")) {
                throw new BikeAlreadyExistsException(pe);
            } else {
                throw new BaseApplicationException(pe);
            }
        }
    }
    
    public BikeStation findByIdentifier(String identifier) throws BikeStationDoesNotExistException {
        try {
            return (BikeStation) em.createNamedQuery("BikeStation.findByIdentifier").setParameter("identifier", identifier).getResultList().get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new BikeStationDoesNotExistException(e);
        }
    }
    
}
