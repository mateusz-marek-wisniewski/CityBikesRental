package pl.lodz.p.edu.s195738.cbr.web.beans;

import pl.lodz.p.edu.s195738.cbr.entities.BikeRepair;
import pl.lodz.p.edu.s195738.cbr.web.beans.util.JsfUtil;
import pl.lodz.p.edu.s195738.cbr.web.beans.util.JsfUtil.PersistAction;
import pl.lodz.p.edu.s195738.cbr.mow.facades.BikeRepairFacade;

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
import pl.lodz.p.edu.s195738.cbr.entities.Bike;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.mow.MOWEndpoint;

@Named("bikeRepairBean")
@SessionScoped
public class BikeRepairBean implements Serializable {

    @EJB
    private pl.lodz.p.edu.s195738.cbr.mow.facades.BikeRepairFacade ejbFacade;
    @EJB
    MOWEndpoint mow;
    private List<BikeRepair> items = null;
    private BikeRepair selected;

    ResourceBundle msg = ResourceBundle.getBundle("i18n.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());

    private List<Bike> bikesToRepair = null;
    private Bike bikeSelected;

    public BikeRepairBean() {
    }

    public BikeRepair getSelected() {
        return selected;
    }

    public void setSelected(BikeRepair selected) {
        this.selected = selected;
    }

    public List<Bike> getBikesToRepair() {
        bikesToRepair = mow.getBikesToRepair();
        return bikesToRepair;
    }

    public void setBikesToRepair(List<Bike> bikesToRepair) {
        this.bikesToRepair = bikesToRepair;
    }

    public Bike getBikeSelected() {
        return bikeSelected;
    }

    public void setBikeSelected(Bike bikeSelected) {
        this.bikeSelected = bikeSelected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private BikeRepairFacade getFacade() {
        return ejbFacade;
    }

    public BikeRepair prepareCreate() {
        selected = new BikeRepair();
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
            mow.repairBike(bikeSelected, selected);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), MessageFormat.format(msg.getString("describeBikeRepair_success"), bikeSelected.getIdentifier())));
            prepareCreate();
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        } finally {
            bikeSelected = null;
            bikesToRepair = null;
        }
    }
    
    public void removeBikeRepair() {
        mow.removeBikeRepair(selected);
        items = null;
        selected = null;
    }
    
    public double getRepairCost() {
        return getItems().stream().mapToDouble(b -> b.getRepairCost().doubleValue()).sum();
    }

    public List<BikeRepair> getItems() {
        if (items == null) {
            items = mow.getBikeRepairsList();
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

    public BikeRepair getRepair(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<BikeRepair> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<BikeRepair> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = BikeRepair.class)
    public static class BikeRepairBeanConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BikeRepairBean bean = (BikeRepairBean) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "bikeRepairBean");
            return bean.getRepair(getKey(value));
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
            if (object instanceof BikeRepair) {
                BikeRepair o = (BikeRepair) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), BikeRepair.class.getName()});
                return null;
            }
        }

    }

}
