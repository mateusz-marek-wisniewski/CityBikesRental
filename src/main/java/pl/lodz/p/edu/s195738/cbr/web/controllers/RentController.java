package pl.lodz.p.edu.s195738.cbr.web.controllers;

import pl.lodz.p.edu.s195738.cbr.entities.Rent;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil.PersistAction;
import pl.lodz.p.edu.s195738.cbr.facades.RentFacade;

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

@Named("rentController")
@SessionScoped
public class RentController implements Serializable {

    @EJB
    private pl.lodz.p.edu.s195738.cbr.facades.RentFacade ejbFacade;
    @EJB
    private MOWEndpoint mow;
    private List<Rent> items = null;
    private Rent selected;

    private String bikeStationIdentifier;
    private String bikeIdentifier;

    private List<Rent> customerToReturnList;
    private List<Rent> customerRentList;

    ResourceBundle msg = ResourceBundle.getBundle("i18n.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());

    public RentController() {
    }

    public Rent getSelected() {
        return selected;
    }

    public void setSelected(Rent selected) {
        this.selected = selected;
    }

    public String getBikeStationIdentifier() {
        return bikeStationIdentifier;
    }

    public void setBikeStationIdentifier(String bikeStationIdentifier) {
        this.bikeStationIdentifier = bikeStationIdentifier;
    }

    public String getBikeIdentifier() {
        return bikeIdentifier;
    }

    public void setBikeIdentifier(String bikeIdentifier) {
        this.bikeIdentifier = bikeIdentifier;
    }

    public List<Rent> getCustomerToReturnList() {
        return mow.getCustomerRentsToReturn();
    }
    
    public List<Rent> getCustomerRentList() {
        return mow.getCustomerRents();
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private RentFacade getFacade() {
        return ejbFacade;
    }

    public Rent prepareCreate() {
        selected = new Rent();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("RentCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("RentUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("RentDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    public void removeRent() {
        mow.removeRent(selected);
        items = null;
        selected = null;
    }

    public void rent() {
        try {
            mow.rent(bikeStationIdentifier, bikeIdentifier);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), MessageFormat.format(msg.getString("rent_success"), bikeIdentifier)));
            bikeIdentifier = "";
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        }
    }
    
    public void returnBike() {
        try {
            Rent returnRent = mow.returnBike(selected, bikeStationIdentifier);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), MessageFormat.format(msg.getString("return_success"), returnRent.getBike().getIdentifier())));
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        }
    }
    
    public void pay() {
        try {
            mow.payForRent(selected);
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        }
    }
    
    public double getMoneyEarned() {
        return getItems().stream().mapToDouble(r -> r.getCharge().doubleValue()).sum();
    }

    public List<Rent> getItems() {
        if (items == null) {
            items = mow.getRentsList();
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

    public Rent getRent(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Rent> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Rent> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Rent.class)
    public static class RentControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RentController controller = (RentController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rentController");
            return controller.getRent(getKey(value));
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
            if (object instanceof Rent) {
                Rent o = (Rent) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Rent.class.getName()});
                return null;
            }
        }

    }

}
