package pl.lodz.p.edu.s195738.cbr.web.controllers;

import pl.lodz.p.edu.s195738.cbr.entities.ChargeRate;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil.PersistAction;
import pl.lodz.p.edu.s195738.cbr.facades.ChargeRateFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("chargeRateController")
@SessionScoped
public class ChargeRateController implements Serializable {

    @EJB
    private pl.lodz.p.edu.s195738.cbr.facades.ChargeRateFacade ejbFacade;
    private List<ChargeRate> items = null;
    private ChargeRate selected;

    public ChargeRateController() {
    }

    public ChargeRate getSelected() {
        return selected;
    }

    public void setSelected(ChargeRate selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ChargeRateFacade getFacade() {
        return ejbFacade;
    }

    public ChargeRate prepareCreate() {
        selected = new ChargeRate();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ChargeRateCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ChargeRateUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ChargeRateDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ChargeRate> getItems() {
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

    public ChargeRate getChargeRate(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<ChargeRate> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ChargeRate> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ChargeRate.class)
    public static class ChargeRateControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ChargeRateController controller = (ChargeRateController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "chargeRateController");
            return controller.getChargeRate(getKey(value));
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
            if (object instanceof ChargeRate) {
                ChargeRate o = (ChargeRate) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ChargeRate.class.getName()});
                return null;
            }
        }

    }

}