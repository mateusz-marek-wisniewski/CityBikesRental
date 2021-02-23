package pl.lodz.p.edu.s195738.cbr.web.controllers;

import pl.lodz.p.edu.s195738.cbr.entities.BikeStationRepair;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil.PersistAction;
import pl.lodz.p.edu.s195738.cbr.mow.facades.BikeStationRepairFacade;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import pl.lodz.p.edu.s195738.cbr.entities.BikeStation;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.mow.MOWEndpoint;

@Named("bikeStationRepairController")
@SessionScoped
public class BikeStationRepairController implements Serializable {

    @EJB
    private pl.lodz.p.edu.s195738.cbr.mow.facades.BikeStationRepairFacade ejbFacade;
    @EJB
    MOWEndpoint mow;
    private List<BikeStationRepair> items = null;
    private BikeStationRepair selected;

    ResourceBundle msg = ResourceBundle.getBundle("i18n.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());

    private List<BikeStation> bikeStationsToRepair = null;
    private BikeStation bikeStationSelected;

    public BikeStationRepairController() {
    }

    public BikeStationRepair getSelected() {
        return selected;
    }

    public void setSelected(BikeStationRepair selected) {
        this.selected = selected;
    }

    public List<BikeStation> getBikeStationsToRepair() {
        bikeStationsToRepair = mow.getBikeStationsToRepair();
        return bikeStationsToRepair;
    }

    public void setBikeStationsToRepair(List<BikeStation> bikeStationsToRepair) {
        this.bikeStationsToRepair = bikeStationsToRepair;
    }

    public BikeStation getBikeStationSelected() {
        return bikeStationSelected;
    }

    public void setBikeStationSelected(BikeStation bikeStationSelected) {
        this.bikeStationSelected = bikeStationSelected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private BikeStationRepairFacade getFacade() {
        return ejbFacade;
    }

    public BikeStationRepair prepareCreate() {
        selected = new BikeStationRepair();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("RepairCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("RepairUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("RepairDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    public void createRepair() {
        try {
            mow.repairBikeStation(bikeStationSelected, selected);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), MessageFormat.format(msg.getString("describeBikeStationRepair_success"), bikeStationSelected.getIdentifier())));
            prepareCreate();
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        } finally {
            bikeStationSelected = null;
            bikeStationsToRepair = null;
        }
    }
    
    public void removeBikeStationRepair() {
        mow.removeBikeStationRepair(selected);
        items = null;
        selected = null;
    }

    public double getRepairCost() {
        return getItems().stream().mapToDouble(b -> b.getRepairCost().doubleValue()).sum();
    }

    public List<BikeStationRepair> getItems() {
        if (items == null) {
            items = mow.getBikeStationRepairsList();
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

    public BikeStationRepair getRepair(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<BikeStationRepair> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<BikeStationRepair> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = BikeStationRepair.class)
    public static class BikeStationRepairControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BikeStationRepairController controller = (BikeStationRepairController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "bikeStationRepairController");
            return controller.getRepair(getKey(value));
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
            if (object instanceof BikeStationRepair) {
                BikeStationRepair o = (BikeStationRepair) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), BikeStationRepair.class.getName()});
                return null;
            }
        }

    }

}
