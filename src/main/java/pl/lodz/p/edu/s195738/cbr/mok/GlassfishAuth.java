/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.mok;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import pl.lodz.p.edu.s195738.cbr.entities.Account;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mok.UsernameDoesNotExistException;
import pl.lodz.p.edu.s195738.cbr.facades.AccountFacade;

/**
 *
 * @author Mateusz Wiśniewski
 */

@Named
@SessionScoped
public class GlassfishAuth implements Serializable {

    private String username;
    private String password;
    private boolean isAdmin;
    private boolean isEmloyee;
    private boolean isCustomer;
    private String oldPassword;
    private String newPassword;
    private String newPasswordRepeat;
    private Account account;
    
    ResourceBundle rb = ResourceBundle.getBundle("i18n.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
    
    @EJB
    MOKEndpoint mokEndpoint;
    @EJB
    AccountFacade accountFacade;

    public String login() throws IOException {
        try {
            if (isUserLoggedIn()) return redirectToPanel();
            account = mokEndpoint.signIn(username, password);
            password = "";
            resolveRoles();
            return redirectToPanel();
        } catch (BaseApplicationException ex) {
            password = "";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, rb.getString("exceptionMessageTitle"), rb.getString(ex.getClass().getName())));
        }
        return null;
    }
    
    public String logout(){
        mokEndpoint.logOut();
        return "logoutSuccess";

    }
    
    public String edit() {
        try {
            mokEndpoint.editMyAccount(account);
        } catch (BaseApplicationException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, rb.getString("exceptionMessageTitle"), rb.getString(e.getClass().getName())));
            account = accountFacade.find(account.getId());
            return null;
        }
        account = accountFacade.find(account.getId());
        return "View";
    }
    
    public void change() {
        try {
            mokEndpoint.changeMyPassword(oldPassword, newPassword, newPasswordRepeat, account);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, rb.getString("changePasswordSuccess")));
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, rb.getString("exceptionMessageTitle"), rb.getString(ex.getClass().getName())));
        } finally {
            oldPassword = "";
            newPassword = "";
            newPasswordRepeat = "";
        }
    }
    
    public String remove() {
        try {
            mokEndpoint.myAccountRemoval(account);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, e.getMessage()));
        }
        return "accountRemovalSuccess";
    }
    
    public String resetPassword() {
        try {
            mokEndpoint.resetPassword(newPassword, newPasswordRepeat, account);
            return "resetPasswordSuccess";
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, rb.getString("exceptionMessageTitle"), rb.getString(ex.getClass().getName())));
            return null;
        } finally {
            newPassword = "";
            newPasswordRepeat = "";
        }
    }

    public boolean isUserLoggedIn() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser() != null;
    }
    public boolean isUserInRole(String rolename) {
        return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(rolename);
    }

    public void resolveRoles() {
        List<String> rolesList = Collections.list(ResourceBundle.getBundle("i18n.roles", FacesContext.getCurrentInstance().getViewRoot().getLocale()).getKeys());
        rolesList.sort(String.CASE_INSENSITIVE_ORDER);
        isAdmin = isUserInRole(rolesList.get(0));
        isCustomer = isUserInRole(rolesList.get(1));
        isEmloyee = isUserInRole(rolesList.get(2));
    }
    
    public String redirectToPanel() throws IOException {
        if (isAdmin) return "adminPanel";
        if (isEmloyee) return "employeePanel";
        if (isCustomer) return "customerPanel";
        return null;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isIsEmloyee() {
        return isEmloyee;
    }

    public void setIsEmloyee(boolean isEmloyee) {
        this.isEmloyee = isEmloyee;
    }

    public boolean isIsCustomer() {
        return isCustomer;
    }

    public void setIsCustomer(boolean isCustomer) {
        this.isCustomer = isCustomer;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordRepeat() {
        return newPasswordRepeat;
    }

    public void setNewPasswordRepeat(String newPasswordRepeat) {
        this.newPasswordRepeat = newPasswordRepeat;
    }
    
}
