/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.web.controllers;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.mok.MOKEndpoint;

/**
 *
 * @author Mateusz Wiśniewski
 */

@Named
@RequestScoped
public class RegistrationController {
    private String username;
    private String name;
    private String surname;
    private String password;
    private String password2;
    private String email;
    private String key;
    
    ResourceBundle msg = ResourceBundle.getBundle("i18n.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
    
    @EJB
    MOKEndpoint mok;
    
    public String create() {
        try {
            mok.createCustomerAccount(username, password, password2, email, name, surname);
        } catch (BaseApplicationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("exceptionMessageTitle"), msg.getString(ex.getClass().getName())));
            return null;
        } finally {
            password = "";
            password2 = "";
        }
        return "registrationSuccess";
    }
    
    public String verify() {
        try {
            mok.confirmAccountEmail(key);
        } catch (BaseApplicationException ex) {
            return "verificationError";
        }
        return "verificationSuccess";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
