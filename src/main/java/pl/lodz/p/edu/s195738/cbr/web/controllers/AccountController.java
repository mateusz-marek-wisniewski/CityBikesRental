package pl.lodz.p.edu.s195738.cbr.web.controllers;

import pl.lodz.p.edu.s195738.cbr.entities.Account;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil;
import pl.lodz.p.edu.s195738.cbr.web.controllers.util.JsfUtil.PersistAction;
import pl.lodz.p.edu.s195738.cbr.mok.facades.AccountFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
import pl.lodz.p.edu.s195738.cbr.entities.AccountRole;
import pl.lodz.p.edu.s195738.cbr.entities.roles.AdminRole;
import pl.lodz.p.edu.s195738.cbr.entities.roles.CustomerRole;
import pl.lodz.p.edu.s195738.cbr.entities.roles.EmployeeRole;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.mok.MOKEndpoint;

@Named("accountController")
@SessionScoped
public class AccountController implements Serializable {

    @EJB
    private pl.lodz.p.edu.s195738.cbr.mok.facades.AccountFacade ejbFacade;
    @EJB
    private MOKEndpoint mok;
    private List<Account> items = null;
    private Account selected;
    private Account newAccount = new Account();
    private Account editAccount = new Account();
    private String password2;
    private boolean admin;
    private boolean adminActive;
    private boolean employee;
    private boolean employeeActive;
    private boolean customer;
    private boolean customerActive;
    private boolean adminEdit;
    private boolean adminActiveEdit;
    private boolean employeeEdit;
    private boolean employeeActiveEdit;
    private boolean customerEdit;
    private boolean customerActiveEdit;
    private String phone;
    private String phoneEdit;
    
    
    ResourceBundle msg = ResourceBundle.getBundle("i18n.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());

    public AccountController() {
    }

    public Account getSelected() {
        return selected;
    }

    public void setSelected(Account sel) {
        System.err.println("account dejavu");
        this.selected = sel;
    }
    
    

    public boolean isAdmin() {
        return newAccount.getAdminRole() != null;
    }
    public void setAdmin(boolean admin) {
        newAccount.setAdminRole(admin ? new AdminRole() : null);
        this.admin = admin;
    }
    public boolean isAdminActive() {
        return newAccount.getAdminRole() != null ? newAccount.getAdminRole().getActive() : adminActive;
    }
    public void setAdminActive(boolean adminActive) {
        if (newAccount.getAdminRole() != null) newAccount.getAdminRole().setActive(adminActive);
        this.adminActive = adminActive;
    }
    
    

    public boolean isEmployee() {
        return newAccount.getEmployeeRole() != null;
    }
    public void setEmployee(boolean employee) {
        if (employee)
            newAccount.setEmployeeRole(new EmployeeRole());
        else
            newAccount.setEmployeeRole(null);
        this.employee = employee;
    }
    public boolean isEmployeeActive() {
        return newAccount.getEmployeeRole() != null ? newAccount.getEmployeeRole().getActive() : employeeActive;
    }
    public void setEmployeeActive(boolean employeeActive) {
        if (newAccount.getEmployeeRole() != null) newAccount.getEmployeeRole().setActive(employeeActive);
        this.employeeActive = employeeActive;
    }
    
    

    public boolean isCustomer() {
        return newAccount.getCustomerRole() != null;
    }
    public void setCustomer(boolean customer) {
        newAccount.setCustomerRole(customer ? new CustomerRole() : null);
        this.customer = customer;
    }
    public boolean isCustomerActive() {
        return newAccount.getCustomerRole() != null ? newAccount.getCustomerRole().getActive() : customerActive;
    }
    public void setCustomerActive(boolean customerActive) {
        if (newAccount.getCustomerRole() != null) newAccount.getCustomerRole().setActive(customerActive);
        this.customerActive = customerActive;
    }

    public Account getNewAccount() {
        return newAccount;
    }

    public void setNewAccount(Account newAccount) {
        this.newAccount = newAccount;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public Account getEditAccount() {
        return editAccount;
    }

    public void setEditAccount(Account editAccount) {
        this.editAccount = editAccount;
    }

    public boolean isAdminEdit() {
        return adminEdit;
    }

    public void setAdminEdit(boolean adminEdit) {
        this.adminEdit = adminEdit;
    }

    public boolean isAdminActiveEdit() {
        return adminActiveEdit;
    }

    public void setAdminActiveEdit(boolean adminActiveEdit) {
        this.adminActiveEdit = adminActiveEdit;
    }

    public boolean isEmployeeEdit() {
        return employeeEdit;
    }

    public void setEmployeeEdit(boolean employeeEdit) {
        this.employeeEdit = employeeEdit;
    }

    public boolean isEmployeeActiveEdit() {
        return employeeActiveEdit;
    }

    public void setEmployeeActiveEdit(boolean employeeActiveEdit) {
        this.employeeActiveEdit = employeeActiveEdit;
    }

    public boolean isCustomerEdit() {
        return customerEdit;
    }

    public void setCustomerEdit(boolean customerEdit) {
        this.customerEdit = customerEdit;
    }

    public boolean isCustomerActiveEdit() {
        return customerActiveEdit;
    }

    public void setCustomerActiveEdit(boolean customerActiveEdit) {
        this.customerActiveEdit = customerActiveEdit;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneEdit() {
        return phoneEdit;
    }

    public void setPhoneEdit(String phoneEdit) {
        this.phoneEdit = phoneEdit;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private AccountFacade getFacade() {
        return ejbFacade;
    }

    public Account prepareCreate() {
        admin = false;
        adminActive = false;
        employee = false;
        employeeActive = false;
        customer = false;
        customerActive = false;
        newAccount = new Account();
        password2 = null;
        initializeEmbeddableKey();
        return newAccount;
    }

    public Account prepareEdit() {
        editAccount = mok.getAccountCopyBeforeEdit(selected);
        RolesToBooleans(editAccount, adminEdit, adminActiveEdit, employeeEdit, employeeActiveEdit, customerEdit, customerActiveEdit);
        phoneEdit = editAccount.getEmployeeRole() != null ? editAccount.getEmployeeRole().getPhone() : "";
        initializeEmbeddableKey();
        return newAccount;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("AccountCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    public void createAccount() {
        try {
            mok.createAccount(newAccount, password2, phone);
            items = null;
            prepareCreate();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), msg.getString("createAccount_success")));
        } catch (BaseApplicationException ex) {
            // resetuj hasło przy wyjątku
            newAccount.setPassword(null);
            password2 = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        }
    }
    
    public void editAccount() {
        try {
            mok.editAccount(editAccount, adminEdit, adminActiveEdit, employeeEdit, employeeActiveEdit, customerEdit, customerActiveEdit, phoneEdit);
            items = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("success"), msg.getString("editAccount_success")));
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
        }
    }
    
    public void removeAccount() {
        mok.removeAccount(selected);
        selected = null;
        items = null;
    }
    
    public void RolesToBooleans (Account account, boolean a1, boolean a2, boolean e1, boolean e2, boolean c1, boolean c2) {
        adminEdit = (account.getAdminRole()!= null);
        adminActiveEdit = (account.getAdminRole()!= null ? account.getAdminRole().getActive() : false);
        employeeEdit = (account.getEmployeeRole()!= null);
        employeeActiveEdit = (account.getEmployeeRole()!= null ? account.getEmployeeRole().getActive() : false);
        customerEdit = (account.getCustomerRole()!= null);
        customerActiveEdit = (account.getCustomerRole()!= null ? account.getCustomerRole().getActive() : false);
    }    
 

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("AccountUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("AccountDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Account> getItems() {
        if (items == null) {
            items = mok.getAccountsList();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    if (selected.getEmailVerificationHash() == "") selected.setEmailVerificationHash(null);
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
            } finally {
                items = null;
            }
        }
    }

    public Account getAccount(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Account> getItemsAvailableSelectMany() {
        return mok.getAccountsList();
    }

    public List<Account> getItemsAvailableSelectOne() {
        return mok.getAccountsList();
    }

    @FacesConverter(forClass = Account.class)
    public static class AccountControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AccountController controller = (AccountController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "accountController");
            return controller.getAccount(getKey(value));
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
            if (object instanceof Account) {
                Account o = (Account) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Account.class.getName()});
                return null;
            }
        }

    }

}
