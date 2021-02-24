/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.mow.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import pl.lodz.p.edu.s195738.cbr.entities.Bike;
import pl.lodz.p.edu.s195738.cbr.entities.ChargeRate;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mow.BikeAlreadyExistsException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mow.BikeConcurrentEditException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mow.ChargeRateAlreadyExistsException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mow.ChargeRateConcurrentEditException;
import pl.lodz.p.edu.s195738.cbr.mok.facades.AbstractFacade;

/**
 *
 * @author Mateusz Wiśniewski
 */
@Stateless
public class ChargeRateFacade extends AbstractFacade<ChargeRate> {

    @PersistenceContext(unitName = "cbradminPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ChargeRateFacade() {
        super(ChargeRate.class);
    }
    
    @Override
    public void create(ChargeRate chargeRate) throws BaseApplicationException {
        try {
            super.create(chargeRate);
            getEntityManager().flush();
        } catch (PersistenceException pe) {
            if (pe.getCause().toString().contains("charge_rate_time_limit_key")) {
                throw new ChargeRateAlreadyExistsException(pe);
            } else {
                throw new BaseApplicationException(pe);
            }
        }
    }

    @Override
    public void edit(ChargeRate chargeRate) throws BaseApplicationException {
        try {
            super.edit(chargeRate);
            getEntityManager().flush();
        } catch (OptimisticLockException ex) {
            throw new ChargeRateConcurrentEditException(ex);
        } catch (PersistenceException pe) {
            if (pe.getCause().toString().contains("charge_rate_time_limit_key")) {
                throw new ChargeRateAlreadyExistsException(pe);
            } else {
                throw new BaseApplicationException(pe);
            }
        }
    }
    
}
