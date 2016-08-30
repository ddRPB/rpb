//package de.dkfz.portal.dao.support;
//
//import java.util.*;
//
//import javax.inject.Inject;
//
//import de.dkfz.portal.domain.admin.DefaultAccount;
//import org.apache.log4j.Logger;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath*:spring/applicationContext-test.xml" })
//@Transactional
//public class NamedQueryUtilIT {
//
//    private static final Logger log = Logger.getLogger(NamedQueryUtilIT.class);
//
//    @Inject
//    private NamedQueryUtil namedQueryUtil;
//
//    @Test
//    public void executeGetAllAccountsQueryName() {
//        String sqlQuery = "DefaultAccount.selectAll";
//        List<DefaultAccount> accounts = namedQueryUtil.findByNamedQuery(new SearchParameters().namedQuery(sqlQuery));
//
//        if (accounts != null) {
//            log.info(accounts.size());
//
//            for (DefaultAccount account : accounts) {
//                log.info(account.toString());
//            }
//        }
//    }
//
//    @Test
//    public void executeGetAllAccountsSqlQuery() {
//        String sqlQuery = "DefaultAccount.selectAll.native";
//        List<DefaultAccount> accounts = namedQueryUtil.findByNamedQuery(new SearchParameters().namedQuery(sqlQuery));
//
//        if (accounts != null) {
//            log.info(accounts.size());
//            for (DefaultAccount account : accounts) {
//                log.info(account.toString());
//            }
//        }
//    }
//
//}
