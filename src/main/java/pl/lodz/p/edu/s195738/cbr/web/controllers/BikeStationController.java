package pl.lodz.p.edu.s195738.cbr.web.controllers;

import pl.lodz.p.edu.s195738.cbr.entities.BikeStation;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil.PersistAction;
import pl.lodz.p.edu.s195738.cbr.facades.BikeStationFacade;

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
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.mow.MOWEndpoint;

@Named("bikeStationController")
@SessionScoped
public class BikeStationController implements Serializable {

    @EJB
    private pl.lodz.p.edu.s195738.cbr.facades.BikeStationFacade ejbFacade;
    @EJB
    private MOWEndpoint mow;
    private List<BikeStation> items = null;
    private BikeStation selected;

    ResourceBundle msg = ResourceBundle.getBundle("i18n.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
    
    private String bikeStationIdentifier;
    private String damageDescription;

    public BikeStationController() {
    }

    public BikeStation getSelected() {
        return selected;
    }

    public void setSelected(BikeStation selected) {
        this.selected = selected;
    }

    public String getBikeStationIdentifier() {
        return bikeStationIdentifier;
    }

    public void setBikeStationIdentifier(String bikeStationIdentifier) {
        this.bikeStationIdentifier = bikeStationIdentifier;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private BikeStationFacade getFacade() {
        return ejbFacade;
    }

    public BikeStation prepareCreate() {
        selected = new BikeStation();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("BikeStationCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("BikeStationUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("BikeStationDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    public void report() {
        try {
            mow.reportBikeStation(bikeStationIdentifier, damageDescription);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), MessageFormat.format(msg.getString("reportBikeStation_success"), bikeStationIdentifier)));
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        } finally {
            bikeStationIdentifier = "";
            damageDescription = "";
        }
    }

    public List<BikeStation> getItems() {
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

    public BikeStation getBikeStation(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<BikeStation> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<BikeStation> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = BikeStation.class)
    public static class BikeStationControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BikeStationController controller = (BikeStationController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "bikeStationController");
            return controller.getBikeStation(getKey(value));
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
            if (object instanceof BikeStation) {
                BikeStation o = (BikeStation) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), BikeStation.class.getName()});
                return null;
            }
        }

    }

}
