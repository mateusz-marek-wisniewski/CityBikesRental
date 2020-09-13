package pl.lodz.p.edu.s195738.cbr.web.controllers;

import pl.lodz.p.edu.s195738.cbr.entities.Bike;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil.PersistAction;
import pl.lodz.p.edu.s195738.cbr.facades.BikeFacade;

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

@Named("bikeController")
@SessionScoped
public class BikeController implements Serializable {

    @EJB
    private pl.lodz.p.edu.s195738.cbr.facades.BikeFacade ejbFacade;
    @EJB
    private MOWEndpoint mow;
    private List<Bike> items = null;
    private Bike selected;
    
    private String bikeIdentifier;
    private String damageDescription;

    ResourceBundle msg = ResourceBundle.getBundle("i18n.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());

    public BikeController() {
    }

    public Bike getSelected() {
        return selected;
    }

    public void setSelected(Bike selected) {
        this.selected = selected;
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

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private BikeFacade getFacade() {
        return ejbFacade;
    }

    public Bike prepareCreate() {
        selected = new Bike();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("BikeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
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
            mow.reportBike(Integer.parseInt(bikeIdentifier), damageDescription);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), MessageFormat.format(msg.getString("reportBike_success"), bikeIdentifier)));
            bikeIdentifier = "";
            damageDescription = "";
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
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
