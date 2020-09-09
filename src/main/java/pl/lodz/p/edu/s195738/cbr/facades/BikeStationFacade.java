/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import pl.lodz.p.edu.s195738.cbr.entities.BikeStation;

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
    
}
