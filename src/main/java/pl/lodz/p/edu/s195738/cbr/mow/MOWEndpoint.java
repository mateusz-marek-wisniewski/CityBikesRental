/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.mow;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import pl.lodz.p.edu.s195738.cbr.entities.Bike;
import pl.lodz.p.edu.s195738.cbr.entities.BikeStation;
import pl.lodz.p.edu.s195738.cbr.entities.ChargeRate;
import pl.lodz.p.edu.s195738.cbr.entities.Rent;
import pl.lodz.p.edu.s195738.cbr.entities.RentalOpinion;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mow.*;
import pl.lodz.p.edu.s195738.cbr.facades.BikeFacade;
import pl.lodz.p.edu.s195738.cbr.facades.BikeStationFacade;
import pl.lodz.p.edu.s195738.cbr.facades.ChargeRateFacade;
import pl.lodz.p.edu.s195738.cbr.facades.CustomerRoleFacade;
import pl.lodz.p.edu.s195738.cbr.facades.RentFacade;
import pl.lodz.p.edu.s195738.cbr.facades.RentalOpinionFacade;
import pl.lodz.p.edu.s195738.cbr.mok.GlassfishAuth;

/**
 *
 * @author Siwy
 */

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class MOWEndpoint implements SessionSynchronization {
    
    private static final Logger logger = Logger.getLogger(MOWEndpoint.class.getName());
    private long transactionId;
    
    @Inject
    GlassfishAuth userSession;
    
    @Inject
    RentFacade rentFacade;
    @EJB
    BikeFacade bikeFacade;
    @EJB
    BikeStationFacade bikeStationFacade;
    @EJB
    ChargeRateFacade chargeRateFacade;
    @EJB
    CustomerRoleFacade customerRoleFacade;
    @EJB
    RentalOpinionFacade rentalOpinionFacade;



    @RolesAllowed("EMPLOYEE")
    public List<Bike> getBikesToAttach() {
        return bikeFacade.findAll().stream()
                .filter(b -> b.getBikeStation() == null)
                .filter(b -> !b.isRented())
                .collect(Collectors.toList());
    }
    
    /**
     * MOW.20 Dołącz rowery do stacji
     * Pozwala pracownikowi dołączyć nieprzydzielony rower do wybranej stacji
     * 
     * @param bikeStationIdentifier identyfikator wybranej stacji
     * @param selectedBike wybrany rower
     * @throws BaseApplicationException
     */
    @RolesAllowed("EMPLOYEE")
    public void attachBike(String bikeStationIdentifier, Bike selectedBike) throws BaseApplicationException {
        
        BikeStation bikeStation = bikeStationFacade.findByIdentifier(bikeStationIdentifier);
        
        selectedBike.setBikeStation(bikeStation);
        bikeFacade.edit(selectedBike);
    }
    
    @RolesAllowed("EMPLOYEE")
    public List<Bike> getBikesToDetach(String bikeStationIdentifier) throws BikeStationDoesNotExistException {
        BikeStation bikeStation = bikeStationFacade.findByIdentifier(bikeStationIdentifier);
        return bikeFacade.findAll().stream()
                .filter(b -> b.getBikeStation() != null)
                .filter(b -> b.getBikeStation().getId().equals(bikeStation.getId()))
                .collect(Collectors.toList());
    }
    
    /**
     * MOW.21 Odłącz rowery od stacji
     * Pozwala pracownikowi na odłączenie wybranego roweru od dowolnej stacji
     * 
     * @param selectedBike wybrany rower
     * @throws BaseApplicationException
     */
    @RolesAllowed("EMPLOYEE")
    public void detachBike(Bike selectedBike) throws BaseApplicationException {
        
        selectedBike.setBikeStation(null);
        bikeFacade.edit(selectedBike);
    }
    
    
    /**
     * MOW.27 Wypożycz rower
     * Pozwala klientowi stacji wypożyczyć rower
     * 
     * @param bikeStationIdentifier identyfikator stacji
     * @param bikeIdentifier identyfikator roweru
     * @throws BaseApplicationException
     */
    @RolesAllowed("CUSTOMER")
    public void rent(String bikeStationIdentifier, int bikeIdentifier) throws BaseApplicationException {
        
        BikeStation bikeStation = bikeStationFacade.findByIdentifier(bikeStationIdentifier);
        Bike bike = bikeFacade.findByIdentifier(bikeIdentifier);
        
        if (bike.getBikeStation() == null) throw new BikeNotInStationException(new Throwable());
        if (!bike.getBikeStation().getIdentifier().equals(bikeStationIdentifier)) throw new BikeNotInStationException(new Throwable());
        
        Rent rent = new Rent();
        rent.setBike(bike);
        rent.setCustomerRole(userSession.getAccount().getCustomerRole());
        rent.setRentStation(bikeStation);
        rent.setStartDate(new Date());
        bike.setBikeStation(null);
        
        rentFacade.create(rent);
        bikeFacade.edit(bike);
    }
    
    @RolesAllowed("CUSTOMER")
    public List<Rent> getCustomerRentsToReturn() {
        return customerRoleFacade.find(userSession.getAccount().getCustomerRole().getId()).getRentCollection().stream()
                .filter(r -> r.getEndDate() == null)
                .collect(Collectors.toList());
    }
    
    @RolesAllowed("CUSTOMER")
    private BigDecimal calculateCharge(Date startDate, Date endDate) {

        long rentDurationMinutes = ( endDate.getTime() - startDate.getTime() ) / 60000;
        
        List<ChargeRate> chargeRates = chargeRateFacade.findAll();

        ChargeRate chargeRate = chargeRates.stream()
                .sorted(Comparator.comparingInt(ChargeRate::getTimeLimit))
                .filter(cR -> cR.getTimeLimit() > rentDurationMinutes)
                .findFirst()
                .orElseGet(() -> chargeRates.get(chargeRates.size() - 1));
        
        BigDecimal hours = BigDecimal.valueOf(Math.ceil(rentDurationMinutes / 60.0));
        return chargeRate.getChargeValue().multiply(hours);

    }
    
    /**
     * MOW.28 Zwróć rower
     * Pozwala klientowi zwrócić wypożyczony rower
     * 
     * @param rentToReturn wypożyczenie do zwrotu
     * @param bikeStationIdentifier identyfikator stacji zwrotu
     * @return zwrócone wypożyczenie po zmianach
     * @throws BaseApplicationException
     */
    @RolesAllowed("CUSTOMER")
    public Rent returnBike(Rent rentToReturn, String bikeStationIdentifier) throws BaseApplicationException {
        
        BikeStation bikeStation = bikeStationFacade.findByIdentifier(bikeStationIdentifier);
        Bike bike = rentToReturn.getBike();
        
        rentToReturn.setEndDate(new Date());
        rentToReturn.setReturnStation(bikeStation);
        rentToReturn.setCharge(calculateCharge(rentToReturn.getStartDate(), rentToReturn.getEndDate()));
        if (rentToReturn.getCharge().compareTo(BigDecimal.ZERO) == 1) rentToReturn.setIsPaid(false);

        bike.setBikeStation(bikeStation);
        
        rentFacade.edit(rentToReturn);
        bikeFacade.edit(bike);
        
        return rentToReturn;
    }
    
    /**
     * MOW.29 Zgłoś usterkę roweru
     * Pozwala klientowi zgłosić usterkę roweru
     * 
     * @param identifier identyfikator roweru
     * @param damageDescription opis usterki
     * @throws BaseApplicationException
     */
    @RolesAllowed("CUSTOMER")
    public void reportBike(int identifier, String damageDescription) throws BaseApplicationException {
        Bike bike = bikeFacade.findByIdentifier(identifier);
        if (bike.getDamageDesc() != null) throw new DamageAlreadyReportedException();
        bike.setDamageDesc(damageDescription);
        bikeFacade.edit(bike);
    }
    
    /**
     * MOW.30 Zgłoś usterkę stacji
     * Pozwala klientowi zgłosić usterkę stacji
     * 
     * @param identifier identyfikator stacji
     * @param damageDescription opis usterki
     * @throws BaseApplicationException
     */
    @RolesAllowed("CUSTOMER")
    public void reportBikeStation(String identifier, String damageDescription) throws BaseApplicationException {
        BikeStation bikeStation = bikeStationFacade.findByIdentifier(identifier);
        if (bikeStation.getDamageDesc() != null) throw new DamageAlreadyReportedException();
        bikeStation.setDamageDesc(damageDescription);
        bikeStationFacade.edit(bikeStation);
    }

    /**
     * MOW.31 Wyświetl listę wypożyczeń
     * Umożlwia klientowi wyświetlenie jego listy wypożyczeń
     * 
     * @return lista wypożyczeń klienta
     */
    @RolesAllowed("CUSTOMER")
    public List<Rent> getCustomerRents() {
        List<Rent> rentList =  customerRoleFacade.find(userSession.getAccount().getCustomerRole().getId()).getRentCollection().stream()
                .collect(Collectors.toList());
        rentList.sort((r1, r2) -> r2.getStartDate().compareTo(r1.getStartDate()));
        return rentList;
    }

    @RolesAllowed("CUSTOMER")
    public RentalOpinion getCustomerOpinion() {
        return rentalOpinionFacade.find(userSession.getAccount().getCustomerRole().getId());
    }

    /**
     * MOW.32 Opłać wypożyczenie
     * Pozwala klientowi opłacić wypożyczenie
     * 
     * @param rent wypożyczenie do opłacenia
     * @throws BaseApplicationException
     */
    @RolesAllowed("CUSTOMER")
    public void payForRent(Rent rent) throws BaseApplicationException {
        rent.setIsPaid(true);
        rentFacade.edit(rent);
    }
    
    /**
     * MOK.33 Dodaj/edytuj opinię o wypożyczalni
     * Pozwala klientowi dodać opinię o stacji jeśli jej jeszcze nie dodał, lub edytować ją
     * 
     * @param opinion opinia do dodania/edycji
     * @throws BaseApplicationException
     */
    @RolesAllowed("CUSTOMER")
    public void updateOpinion(RentalOpinion opinion) throws BaseApplicationException {
        opinion.setAddedDate(new Date());
        opinion.setCustomerRole(userSession.getAccount().getCustomerRole());
        opinion.setId(userSession.getAccount().getCustomerRole().getId());
        
        rentalOpinionFacade.edit(opinion);
    }  
    


    @Override
    public void afterBegin() throws EJBException, RemoteException {
        transactionId = System.currentTimeMillis();
        logger.log(Level.SEVERE, "Transakcja o ID: {0} zostala rozpoczeta", transactionId);
    }

    @Override
    public void beforeCompletion() throws EJBException, RemoteException {
        logger.log(Level.SEVERE, "Transakcja o ID: {0} przed zakonczeniem", transactionId);
    }

    @Override
    public void afterCompletion(boolean committed) throws EJBException, RemoteException {
        logger.log(Level.SEVERE, "Transakcja o ID: {0} zostala zakonczona przez: {1}", new Object[]{transactionId, committed ? "zatwierdzenie" : "wycofanie"});
    }
    
}
