/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.mok;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import pl.lodz.p.edu.s195738.cbr.entities.*;
import pl.lodz.p.edu.s195738.cbr.exceptions.mok.LoginDoesNotExistException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mok.SignInFailedException;
import pl.lodz.p.edu.s195738.cbr.facades.AccountFacade;
import pl.lodz.p.edu.s195738.cbr.facades.LoginAttemptFacade;

/**
 *
 * @author Siwy
 */

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class MOKEndpoint implements SessionSynchronization{
    
    @EJB
    AccountFacade accountFacade;
    @EJB
    LoginAttemptFacade loginAttemptFacade;
    
    @Resource
    private SessionContext sctx;
    private long txId;
    private static final Logger logger = Logger.getLogger(MOKEndpoint.class.getName());
    ResourceBundle msg = ResourceBundle.getBundle("i18n.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());


    /**
     * MOK.3 Zaloguj
     * Pozwala użytkownikowi zalogować się do systemu
     * 
     * @param username nazwa użytkownika
     * @param password hasło w postaci jawnej
     * @return obiekt konta zalogowanego użytkownika
     * @throws SignInFailedException
     */
    public Account signIn(String username, String password) throws SignInFailedException {

        Account account;
        
        // sprawdź konto o podanej nazwie użytkownika istnieje
        try {
            account = accountFacade.findByLogin(username);
        } catch (LoginDoesNotExistException ex) {
            throw new SignInFailedException();
        }
        
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        
        // pobierz adres IP zdalnego użytkownika
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) ipAddress = request.getRemoteAddr();
        
        // ustaw właściwości obiektu próby logowania
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setLoginDate(new java.sql.Date(new Date().getTime()));
        loginAttempt.setIpAddress(ipAddress.concat("/32"));
        loginAttempt.setAccount(account);
        
        try {
            // spróbuj zalogowac się do serwera aplikacji
            request.login(username, password);
            // jeśli sukces, zajerstruj pomyślne logowanie
            loginAttempt.setSucceded(true);
            loginAttemptFacade.create(loginAttempt);
        } catch (ServletException ex) {
            // jeśli nie, zarejestruj błędne logowanie
            loginAttempt.setSucceded(false);
            loginAttemptFacade.create(loginAttempt);
            throw new SignInFailedException();
        }
        return account;
    }

    /**
     *
     * @throws EJBException
     * @throws RemoteException
     */
    @Override
    public void afterBegin() throws EJBException, RemoteException {
        txId = System.currentTimeMillis();
        logger.log(Level.SEVERE, "Transakcja o ID: {0} zostala rozpoczeta", txId);
    }

    /**
     *
     * @throws EJBException
     * @throws RemoteException
     */
    @Override
    public void beforeCompletion() throws EJBException, RemoteException {
        logger.log(Level.SEVERE, "Transakcja o ID: {0} przed zakonczeniem", txId);
    }

    /**
     *
     * @param committed
     * @throws EJBException
     * @throws RemoteException
     */
    @Override
    public void afterCompletion(boolean committed) throws EJBException, RemoteException {
        if (committed) {
            logger.log(Level.INFO, "Transakcja o ID: {0} zostala zakonczona przez zatwierdzenie", txId);
        } else {
            logger.log(Level.SEVERE, "Transakcja o ID: {0} zostala zakonczona przez wycofanie", txId);
        }
    }
}
