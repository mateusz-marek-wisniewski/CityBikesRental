/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.mok;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
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
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import pl.lodz.p.edu.s195738.cbr.entities.*;
import pl.lodz.p.edu.s195738.cbr.entities.roles.AdminRole;
import pl.lodz.p.edu.s195738.cbr.entities.roles.CustomerRole;
import pl.lodz.p.edu.s195738.cbr.entities.roles.EmployeeRole;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mok.*;
import pl.lodz.p.edu.s195738.cbr.facades.AccountFacade;
import pl.lodz.p.edu.s195738.cbr.facades.AccountRoleFacade;
import pl.lodz.p.edu.s195738.cbr.facades.AdminRoleFacade;
import pl.lodz.p.edu.s195738.cbr.facades.CustomerRoleFacade;
import pl.lodz.p.edu.s195738.cbr.facades.EmployeeRoleFacade;
import pl.lodz.p.edu.s195738.cbr.facades.LoginAttemptFacade;
import pl.lodz.p.edu.s195738.cbr.mok.utils.EmailUtil;
import pl.lodz.p.edu.s195738.cbr.mok.utils.PasswordUtil;

/**
 *
 * @author Mateusz Wiśniewski
 */

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class MOKEndpoint implements SessionSynchronization{
    
    @EJB
    AccountFacade accountFacade;
    @EJB
    AdminRoleFacade adminRoleFacade;
    @EJB
    EmployeeRoleFacade employeeRoleFacade;
    @EJB
    CustomerRoleFacade customerRoleFacade;
    
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
        loginAttempt.setIpAddress(ipAddress);
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
     * MOK.5 Zmień dane własnego konta
     * Pozwala użytkownikowi zalogowanemu zmienić dane jego własnego konta
     * 
     * @param account obiekt konta użytkownika ze zmienionymi danymi
     * @throws BaseApplicationException
     */
    @RolesAllowed({"ADMIN", "EMPLOYEE", "CUSTOMER"})
    public void editMyAccount(Account account) throws BaseApplicationException {
        accountFacade.edit(account);
    }
    
    /**
     * MOK.6 Zmień hasło własnego konta
     * Pozwala użytkownikowi zalogowanemu zmienić jego własne hasło
     * 
     * @param oldPassword obecne hasło  postaci jawnej
     * @param newPassword nowe hasło w postaci jawnej
     * @param newPasswordRepeat nowe hasło w postaci jawnej powtórzone
     * @param account konto zalogowanego użytkownika
     * @throws BaseApplicationException
     */
    @RolesAllowed({"ADMIN", "EMPLOYEE", "CUSTOMER"})
    public void changeMyPassword(String oldPassword, String newPassword, String newPasswordRepeat, Account account) throws BaseApplicationException{
        if (!account.getPassword().equals(PasswordUtil.hash(oldPassword))) throw new CurrentPasswordIsInvalidException();
        if (!newPassword.equals(newPasswordRepeat)) throw new PasswordsDoNotMatchException();
        if (newPassword.equals(oldPassword)) throw new PasswordsCurrentAndNewCanNotBeTheSameException();
        if (account.isPasswordInAccountPasswordCollection(PasswordUtil.hash(newPassword))) throw new PasswordAlreadyUsedException();
        
        account.setPassword(PasswordUtil.hash(newPassword));
        AccountPassword accountPassword = new AccountPassword();
        accountPassword.setPassword(PasswordUtil.hash(oldPassword));
        accountPassword.setAccount(account);
        account.getAccountPasswordCollection().add(accountPassword);
        accountFacade.edit(account);
    }
    
    /**
     * MOK.7 Wyloguj się
     * Pozwala użytkownikowi zalogowanemu wylogować się
     */
    @RolesAllowed({"ADMIN", "EMPLOYEE", "CUSTOMER"})
    public void logOut() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }
    
    /**
     * MOK.8 Usuń własne konto
     * Pozwala użytkownikowi zalogowanemu usunąć swoje konto
     * 
     * @param account konto użytkownika do usunięcia
     * @throws BaseApplicationException
     */
    @RolesAllowed({"ADMIN", "EMPLOYEE", "CUSTOMER"})
    public void myAccountRemoval(Account account) throws BaseApplicationException {
        logOut();
        account.setActive(false);
        accountFacade.edit(account);
    }
    
    /**
     * MOK.9 Wyświetl listę kont
     * Administrator wyświetla listę wszystkich użytkowników
     * 
     * @return lista wszystkich użytkowników
     */
    @RolesAllowed("ADMIN")
    public List<Account> getAccountsList() {
        return accountFacade.findAll();
    }
    
    @RolesAllowed("ADMIN")
    public void createAccount(Account account, String password2, String phone) throws BaseApplicationException {
        
        // sprawdź poprawność hasła
        if (!account.getPassword().equals(password2)) throw new PasswordsDoNotMatchException();
        
        //zaszyfruj hasło
        account.setPassword(PasswordUtil.hash(account.getPassword()));
        
        // ustaw powiązanie dwustronne
        if (account.getAdminRole() != null) account.getAdminRole().setAccount(account);        
        if (account.getEmployeeRole() != null) {
            account.getEmployeeRole().setAccount(account);
            account.getEmployeeRole().setPhone(phone);
        }
        if (account.getCustomerRole() != null) account.getCustomerRole().setAccount(account);
        
        accountFacade.create(account);
    }
    
    @RolesAllowed("ADMIN")
    public Account getAccountCopyBeforeEdit(Account account) {
        return accountFacade.find(account.getId());
    }
    
    @RolesAllowed("ADMIN")
    public void editAccount(Account account, boolean a1, boolean a2, boolean e1, boolean e2, boolean c1, boolean c2, String phone) throws BaseApplicationException {

        if (a1 && account.getAdminRole() == null) {
            AdminRole aR = new AdminRole();
            aR.setActive(a2);
            aR.setAccount(account);
            account.setAdminRole(aR);
            adminRoleFacade.create(aR);
        } else if (a1 && account.getAdminRole() != null) {
            account.getAdminRole().setActive(a2);
        } else if (!a1 && account.getAdminRole() != null) {
            adminRoleFacade.remove(account.getAdminRole());
            account.setAdminRole(null);
        }

        if (e1 && account.getEmployeeRole() == null) {
            EmployeeRole eR = new EmployeeRole();
            eR.setActive(e2);
            eR.setAccount(account);
            eR.setPhone(phone);
            account.setEmployeeRole(eR);
            employeeRoleFacade.create(eR);
        } else if (e1 && account.getEmployeeRole() != null) {
            account.getEmployeeRole().setActive(e2);
            account.getEmployeeRole().setPhone(phone);
        } else if (!e1 && account.getEmployeeRole() != null) {
            employeeRoleFacade.remove(account.getEmployeeRole());
            account.setEmployeeRole(null);
        }
        
        if (c1 && account.getCustomerRole() == null) {
            CustomerRole cR = new CustomerRole();
            cR.setActive(c2);
            cR.setAccount(account);
            account.setCustomerRole(cR);
            customerRoleFacade.create(cR);
        } else if (c1 && account.getCustomerRole() != null) {
            account.getCustomerRole().setActive(c2);
        } else if (!c1 && account.getCustomerRole() != null) {
            customerRoleFacade.remove(account.getCustomerRole());
            account.setCustomerRole(null);
        }
        
        accountFacade.edit(account);
    }

    @RolesAllowed("ADMIN")
    public void removeAccount(Account account) {
        accountFacade.remove(account);
    }
    
    public void sendResetPasswordLink(String email) throws BaseApplicationException{
        
        Account account = accountFacade.findByEmail(email);
        
        String verkey = UUID.randomUUID().toString();
        account.setEmailVerificationHash(PasswordUtil.hash(verkey));
        accountFacade.edit(account);

        // przygotowanie linku rejestracyjnego
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest req = (HttpServletRequest)context.getRequest();
        String projectURI = String.format("http%s://%s:%d%s", (req.isSecure()?"s":""), req.getServerName(), req.getServerPort(), req.getContextPath());
        String verlink = MessageFormat.format(msg.getString("passwordResetLinkTemplate"), projectURI, verkey);
        
        try {
            emailUtil.sendEmail(email, msg.getString("passwordResetMsgTitle"), MessageFormat.format(msg.getString("passwordResetMsgBody"), verlink));
        } catch (MessagingException ex) {
            throw new EmailCanNotBeSentException(ex);
        }
    }
    
    public Account getAccountByVerificationHash(String verkey) throws BaseApplicationException {
         return accountFacade.findByVerificationHash(PasswordUtil.hash(verkey));
    }
    
    public void resetPassword(String newPassword, String newPasswordRepeat, Account account) throws BaseApplicationException{
        if (!newPassword.equals(newPasswordRepeat)) throw new PasswordsDoNotMatchException();
        if (account.getPassword().equals(PasswordUtil.hash(newPassword))) throw new PasswordsCurrentAndNewCanNotBeTheSameException();
        if (account.isPasswordInAccountPasswordCollection(PasswordUtil.hash(newPassword))) throw new PasswordAlreadyUsedException();
        
        AccountPassword accountPassword = new AccountPassword();
        accountPassword.setPassword(account.getPassword());
        account.setPassword(PasswordUtil.hash(newPassword));
        accountPassword.setAccount(account);
        account.getAccountPasswordCollection().add(accountPassword);
        account.setEmailVerificationHash(null);
        accountFacade.edit(account);
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
