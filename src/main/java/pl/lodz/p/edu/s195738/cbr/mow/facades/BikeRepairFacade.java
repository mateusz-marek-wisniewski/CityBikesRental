/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.mow.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import pl.lodz.p.edu.s195738.cbr.entities.BikeRepair;
import pl.lodz.p.edu.s195738.cbr.mok.facades.AbstractFacade;

/**
 *
 * @author Mateusz Wiśniewski
 */
@Stateless
public class BikeRepairFacade extends AbstractFacade<BikeRepair> {

    @PersistenceContext(unitName = "cbradminPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BikeRepairFacade() {
        super(BikeRepair.class);
    }
    
}
