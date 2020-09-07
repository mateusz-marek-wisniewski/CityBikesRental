/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.mok;

import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import pl.lodz.p.edu.s195738.cbr.entities.*;
import pl.lodz.p.edu.s195738.cbr.entities.roles.CustomerRole;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mok.*;
import pl.lodz.p.edu.s195738.cbr.facades.AccountFacade;
import pl.lodz.p.edu.s195738.cbr.facades.LoginAttemptFacade;
import pl.lodz.p.edu.s195738.cbr.mok.utils.EmailUtil;
import pl.lodz.p.edu.s195738.cbr.mok.utils.PasswordUtil;

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
    
    @Inject
    EmailUtil emailUtil;
    
    /**
     * MOK.1 Zarejestruj się
     * Pozwala założyć nowe konto klienta użytkonikowi niezalogowanemu
     * 
     * @param username nazwa użytkownika
     * @param password hasło w postaci jawnej
     * @param password2 potórzenie hasła w postaci jawnej
     * @param email adres e-mail
     * @param name imię użytkownika
     * @param surname nazwisko użytkownika
     * @throws BaseApplicationException
     */
    public void createCustomerAccount(String username, String password, String password2, String email, String name, String surname) throws BaseApplicationException {
        
        // sprawdź poprawność hasła
        if (!password.equals(password2)) throw new PasswordsDoNotMatchException();
        
        String verkey = UUID.randomUUID().toString();
        
        Account account = new Account();
        account.setLogin(username);
        account.setPassword(PasswordUtil.hash(password));
        account.setEmail(email);
        account.setName(name);
        account.setSurname(surname);
        account.setEmailVerificationHash(PasswordUtil.hash(verkey));
        account.setActive(true);
        account.setConfirmed(false);
        account.setCustomerRole(new CustomerRole());
        account.getCustomerRole().setAccount(account);
        
        accountFacade.create(account);

        // przygotowanie linku rejestracyjnego
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest req = (HttpServletRequest)context.getRequest();
        String projectURI = String.format("http%s://%s:%d%s", (req.isSecure()?"s":""), req.getServerName(), req.getServerPort(), req.getContextPath());
        String verlink = MessageFormat.format(msg.getString("emailVerificationLinkTemplate"), projectURI, verkey);
        
        try {
            emailUtil.sendEmail(email, msg.getString("emailVerificationMsgTitle"), MessageFormat.format(msg.getString("emailVerificationMsgBody"), verlink));
        } catch (MessagingException ex) {
            throw new EmailCanNotBeSentException(ex);
        }
    }
    
    /**
     * MOK.2 Potwierdź konto e-mailem
     * Po znalezieniu konta z podanym kluczem w postaci niejawnej potwierdza konto
     * 
     * @param verkey klucz w postaci jawnej
     * @throws BaseApplicationException
     */
    public void confirmAccountEmail(String verkey) throws BaseApplicationException {
        Account account = accountFacade.findByVerificationHash(PasswordUtil.hash(verkey));
        account.setEmailVerificationHash(null);
        account.setConfirmed(true);
        accountFacade.edit(account);
    }
    
    /**
     * MOK.3 Zaloguj
     * Pozwala użytkownikowi zalogować się do systemu
     * 
     * @param username nazwa użytkownika
     * @param password hasło w postaci jawnej
     * @return obiekt konta zalogowanego użytkownika
     * @throws BaseApplicationException
     */
    public Account signIn(String username, String password) throws BaseApplicationException {

        Account account;
        
        // sprawdź konto o podanej nazwie użytkownika istnieje
        try {
            account = accountFacade.findByLogin(username);
        } catch (UsernameDoesNotExistException ex) {
            throw new SignInFailedException(ex);
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
            throw new SignInFailedException(ex);
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
