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
import pl.lodz.p.edu.s195738.cbr.entities.Bike;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mow.BikeAlreadyExistsException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mow.BikeConcurrentEditException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mow.BikeDoesNotExistException;

/**
 *
 * @author Mateusz Wiśniewski
 */
@Stateless
public class BikeFacade extends AbstractFacade<Bike> {

    @PersistenceContext(unitName = "cbradminPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BikeFacade() {
        super(Bike.class);
    }
    
    @Override
    public void create(Bike bike) throws BaseApplicationException {
        try {
            super.create(bike);
            getEntityManager().flush();
        } catch (PersistenceException pe) {
            if (pe.getCause().toString().contains("bike_identifier_key")) {
                throw new BikeAlreadyExistsException(pe);
            } else {
                throw new BaseApplicationException(pe);
            }
        }
    }

    @Override
    public void edit(Bike bike) throws BaseApplicationException {
        try {
            super.edit(bike);
            getEntityManager().flush();
        } catch (OptimisticLockException ex) {
            throw new BikeConcurrentEditException(ex);
        } catch (PersistenceException pe) {
            if (pe.getCause().toString().contains("bike_identifier_key")) {
                throw new BikeAlreadyExistsException(pe);
            } else {
                throw new BaseApplicationException(pe);
            }
        }
    }
    
    public Bike findByIdentifier(String identifier) throws BikeDoesNotExistException {
        try {
            return (Bike) em.createNamedQuery("Bike.findByIdentifier").setParameter("identifier", identifier).getResultList().get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new BikeDoesNotExistException(e);
        }
    }
    
}
