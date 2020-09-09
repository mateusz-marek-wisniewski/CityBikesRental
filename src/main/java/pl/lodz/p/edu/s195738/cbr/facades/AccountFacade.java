/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.p.edu.s195738.cbr.facades;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import pl.lodz.p.edu.s195738.cbr.entities.Account;
import pl.lodz.p.edu.s195738.cbr.exceptions.BaseApplicationException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mok.AccountConcurrentEditException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mok.EmailAddressAlreadyInUseException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mok.EmailVerificationHashDoesNotExistException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mok.UsernameAlreadyExistsException;
import pl.lodz.p.edu.s195738.cbr.exceptions.mok.UsernameDoesNotExistException;

/**
 *
 * @author Mateusz Wiśniewski
 */

@Stateless
public class AccountFacade extends AbstractFacade<Account> {

    @PersistenceContext(unitName = "cbradminPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AccountFacade() {
        super(Account.class);
    }

    @Override
    public void create(Account account) throws BaseApplicationException {
        try {
            super.create(account);
            getEntityManager().flush();
        } catch (PersistenceException pe) {
            if (pe.getCause().toString().contains("account_login_key")) {
                throw new UsernameAlreadyExistsException(pe);
            } else if (pe.getCause().toString().contains("account_email_key")) {
                throw new EmailAddressAlreadyInUseException(pe);
            } else {
                throw new BaseApplicationException(pe);
            }
        }
    }

    @Override
    public void edit(Account account) throws BaseApplicationException {
        try {
            super.edit(account);
            getEntityManager().flush();
        } catch (OptimisticLockException ex) {
            throw new AccountConcurrentEditException(ex);
        } catch (PersistenceException pe) {
            if (pe.getCause().toString().contains("account_login_key")) {
                throw new UsernameAlreadyExistsException(pe);
            } else if (pe.getCause().toString().contains("account_email_key")) {
                throw new EmailAddressAlreadyInUseException(pe);
            } else {
                throw new BaseApplicationException(pe);
            }
        }
    }
    
    public Account findByLogin(String login) throws UsernameDoesNotExistException {
        try {
            return (Account) em.createNamedQuery("Account.findByLogin").setParameter("login", login).getResultList().get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new UsernameDoesNotExistException(e);
        }
    }

    public Account findByVerificationHash(String hash) throws EmailVerificationHashDoesNotExistException {
        try {
            return (Account) em.createNamedQuery("Account.findByEmailVerificationHash").setParameter("emailVerificationHash", hash).getResultList().get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new EmailVerificationHashDoesNotExistException(e);
        }
    }
}
