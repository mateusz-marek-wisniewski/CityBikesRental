package pl.lodz.p.edu.s195738.cbr.web.beans;

import pl.lodz.p.edu.s195738.cbr.entities.Bike;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mow.BikeStationDoesNotExistException;
import pl.lodz.p.edu.s195738.cbr.mow.MOWEndpoint;

@Named("bikeBean")
@SessionScoped
public class BikeBean implements Serializable {

    @EJB
    private MOWEndpoint mow;
    private List<Bike> items = null;
    private Bike selected;
    private Bike newBike = new Bike();
    private Bike editBike = new Bike();
    private List<Bike> bikesToAttach = null;
    private List<Bike> bikesToDetach = null;
    private List<Bike> bikesReported = null;
    private List<Bike> bikesSelected = new ArrayList<>();

    ResourceBundle msg = ResourceBundle.getBundle("i18n.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
    
    private String bikeIdentifier = "";
    private String damageDescription;

    public BikeBean() {
    }

    public Bike getSelected() {
        return selected;
    }

    public void setSelected(Bike selected) {
        this.selected = selected;
    }

    public Bike getNewBike() {
        return newBike;
    }

    public void setNewBike(Bike newBike) {
        this.newBike = newBike;
    }

    public Bike getEditBike() {
        return editBike;
    }

    public void setEditBike(Bike editBike) {
        this.editBike = editBike;
    }

    public String getBikeIdentifier() {
        return bikeIdentifier;
    }

    public void setBikeIdentifier(String bikeIdentifier) {
        this.bikeIdentifier = bikeIdentifier;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public List<Bike> getBikesToAttach() {
        if (bikesToAttach == null) bikesToAttach = mow.getBikesToAttach();
        return bikesToAttach;
    }

    public void setBikesToAttach(List<Bike> bikesToAttach) {
        this.bikesToAttach = bikesToAttach;
    }

    public List<Bike> getBikesToDetach() {
        return bikesToDetach;
    }

    public void getBikesToDetachOnChange() {
        try {
            if (bikeIdentifier.length() == 5) bikesToDetach = mow.getBikesToDetach(bikeIdentifier);
            else bikesToDetach = null;
        } catch (BikeStationDoesNotExistException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
            bikesToDetach = null;
        }
        bikesSelected.clear();
    }

    public void setBikesToDetach(List<Bike> bikesToDetach) {
        this.bikesToDetach = bikesToDetach;
    }

    public List<Bike> getBikesSelected() {
        return bikesSelected;
    }

    public void setBikesSelected(List<Bike> bikesSelected) {
        this.bikesSelected = bikesSelected;
    }

    public List<Bike> getBikesReported() {
        return mow.getReportedBikes();
    }

    public void setBikesReported(List<Bike> bikesReported) {
        this.bikesReported = bikesReported;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }
    
    public void onSelect(Bike bike) {
        if (bike == null) {bikesSelected.addAll(bikesToAttach); return;}
        bikesSelected.add(bike);
        bikesSelected = bikesSelected.stream().distinct().collect(Collectors.toList());
    }
    
    public void onDeselect(Bike bike) {
        if (bike == null) {bikesSelected.removeAll(bikesToAttach); return;}
        bikesSelected.remove(bike);
    }
    
    public void onSelectDetach(Bike bike) {
        if (bike == null) {bikesSelected.addAll(bikesToDetach); return;}
        bikesSelected.add(bike);
        bikesSelected = bikesSelected.stream().distinct().collect(Collectors.toList());
    }
    
    public void onDeselectDetach(Bike bike) {
        if (bike == null) {bikesSelected.removeAll(bikesToDetach); return;}
        bikesSelected.remove(bike);
    }

    public Bike prepareCreate() {
        selected = new Bike();
        newBike = new Bike();
        initializeEmbeddableKey();
        return newBike;
    }

    public Bike prepareEdit() {
        editBike = mow.getBikeCopyBeforeEdit(selected);
        initializeEmbeddableKey();
        return editBike;
    }
    
    public void createBike() {
        try {
            mow.createBike(newBike);
            items = null;
            prepareCreate();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), msg.getString("createBike_success")));
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        }
    }
    
    public void editBike() {
        try {
            mow.editBike(editBike);
            items = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), msg.getString("editBike_success")));
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        }
    }
    
    public void removeBike() {
        try {
            mow.removeBike(selected);
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        }
        selected = null;
        items = null;
    }
    
    public void report() {
        try {
            mow.reportBike(bikeIdentifier, damageDescription);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), MessageFormat.format(msg.getString("reportBike_success"), bikeIdentifier)));
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        } finally {
            bikeIdentifier = "";
            damageDescription = "";
        }
    }
    
    public void saveBikeDamage() {
        try {
            mow.saveBikeDamage(bikeIdentifier, damageDescription);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), MessageFormat.format(msg.getString("saveBikeDamage_success"), bikeIdentifier)));
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        } finally {
            bikeIdentifier = "";
            damageDescription = "";
        }
    }
    
    public void attachBikes(){
        try {
            for (Bike bike : bikesSelected) {
                mow.attachBike(bikeIdentifier, bike);
            }
            bikesSelected.clear();
            bikesToAttach = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), MessageFormat.format(msg.getString("attachBikes_success"), bikeIdentifier)));
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
            bikesToAttach = null;
        }
    }
    
    public void detachBikes(){
        try {
            for (Bike bike : bikesSelected) {
                mow.detachBike(bike);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), MessageFormat.format(msg.getString("detachBikes_success"), bikeIdentifier)));
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        } finally {
            bikesSelected.clear();
            getBikesToDetachOnChange();
        }
    }

    public List<Bike> getItems() {
        if (items == null) {
            items = mow.getBikesList();
        }
        return items;
    }

    public List<Bike> getItemsAvailableSelectMany() {
        return mow.getBikesList();
    }

    public List<Bike> getItemsAvailableSelectOne() {
        return mow.getBikesList();
    }

}