package pl.lodz.p.edu.s195738.cbr.web.controllers;

import pl.lodz.p.edu.s195738.cbr.entities.Bike;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil.PersistAction;
import pl.lodz.p.edu.s195738.cbr.mow.facades.BikeFacade;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mow.BikeStationDoesNotExistException;
import pl.lodz.p.edu.s195738.cbr.mow.MOWEndpoint;

@Named("bikeController")
@SessionScoped
public class BikeController implements Serializable {

    @EJB
    private pl.lodz.p.edu.s195738.cbr.mow.facades.BikeFacade ejbFacade;
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

    public BikeController() {
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

    private BikeFacade getFacade() {
        return ejbFacade;
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

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("BikeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
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

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("BikeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("BikeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
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
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Bike getBike(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Bike> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Bike> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Bike.class)
    public static class BikeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BikeController controller = (BikeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "bikeController");
            return controller.getBike(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Bike) {
                Bike o = (Bike) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Bike.class.getName()});
                return null;
            }
        }

    }

}
