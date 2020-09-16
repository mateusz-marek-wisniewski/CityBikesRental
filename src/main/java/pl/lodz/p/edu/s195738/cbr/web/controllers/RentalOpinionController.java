package pl.lodz.p.edu.s195738.cbr.web.controllers;

import pl.lodz.p.edu.s195738.cbr.entities.RentalOpinion;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil.PersistAction;
import pl.lodz.p.edu.s195738.cbr.facades.RentalOpinionFacade;

import java.io.Serializable;
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

@Named("rentalOpinionController")
@SessionScoped
public class RentalOpinionController implements Serializable {

    @EJB
    private pl.lodz.p.edu.s195738.cbr.facades.RentalOpinionFacade ejbFacade;
    @EJB
    private MOWEndpoint mow;
    private List<RentalOpinion> items = null;
    private RentalOpinion selected;
    private RentalOpinion customerOpinion;
    

    ResourceBundle msg = ResourceBundle.getBundle("i18n.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());

    public RentalOpinionController() {
    }

    public RentalOpinion getSelected() {
        return selected;
    }

    public void setSelected(RentalOpinion selected) {
        this.selected = selected;
    }

    public RentalOpinion getCustomerOpinion() {
        if (customerOpinion == null) customerOpinion = mow.getCustomerOpinion();
        if (customerOpinion == null) customerOpinion = new RentalOpinion();
        return customerOpinion;
    }

    public void setCustomerOpinion(RentalOpinion customerOpinion) {
        this.customerOpinion = customerOpinion;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private RentalOpinionFacade getFacade() {
        return ejbFacade;
    }

    public RentalOpinion prepareCreate() {
        selected = new RentalOpinion();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("RentalOpinionCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("RentalOpinionUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("RentalOpinionDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    public void removeOpinion() {
        mow.removeRentalOpinion(selected);
        items = null;
        selected = null;
    }
    
    public void updateOpinion() {
        try {
            mow.updateOpinion(customerOpinion);
            if (customerOpinion.getAddedDate() == null)
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), msg.getString("addOpinion_success")));
            else
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), msg.getString("editOpinion_success")));
            customerOpinion = null;
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        }
    }
    
    public double getAverageRating() {
        return getItems().stream().mapToInt(r -> r.getRating()).average().getAsDouble();
    }

    public List<RentalOpinion> getItems() {
        if (items == null) {
            items = mow.getRentalOpinionsList();
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

    public RentalOpinion getRentalOpinion(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<RentalOpinion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<RentalOpinion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = RentalOpinion.class)
    public static class RentalOpinionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RentalOpinionController controller = (RentalOpinionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rentalOpinionController");
            return controller.getRentalOpinion(getKey(value));
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
            if (object instanceof RentalOpinion) {
                RentalOpinion o = (RentalOpinion) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), RentalOpinion.class.getName()});
                return null;
            }
        }

    }

}
