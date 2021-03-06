package pl.lodz.p.edu.s195738.cbr.web.beans;

import pl.lodz.p.edu.s195738.cbr.entities.LoginAttempt;
import pl.lodz.p.edu.s195738.cbr.web.beans.util.JsfUtil;
import pl.lodz.p.edu.s195738.cbr.web.beans.util.JsfUtil.PersistAction;
import pl.lodz.p.edu.s195738.cbr.mok.facades.LoginAttemptFacade;

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

@Named("loginAttemptBean")
@SessionScoped
public class LoginAttemptBean implements Serializable {

    @EJB
    private pl.lodz.p.edu.s195738.cbr.mok.facades.LoginAttemptFacade ejbFacade;
    private List<LoginAttempt> items = null;
    private LoginAttempt selected;

    public LoginAttemptBean() {
    }

    public LoginAttempt getSelected() {
        return selected;
    }

    public void setSelected(LoginAttempt selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private LoginAttemptFacade getFacade() {
        return ejbFacade;
    }

    public LoginAttempt prepareCreate() {
        selected = new LoginAttempt();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("LoginAttemptCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("LoginAttemptUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("LoginAttemptDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<LoginAttempt> getItems() {
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

    public LoginAttempt getLoginAttempt(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<LoginAttempt> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<LoginAttempt> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = LoginAttempt.class)
    public static class LoginAttemptBeanConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LoginAttemptBean bean = (LoginAttemptBean) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "loginAttemptBean");
            return bean.getLoginAttempt(getKey(value));
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
            if (object instanceof LoginAttempt) {
                LoginAttempt o = (LoginAttempt) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), LoginAttempt.class.getName()});
                return null;
            }
        }

    }

}
