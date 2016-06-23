//package de.dktk.dd.rpb.core.dao.admin;
//
//import java.util.Set;
//
//import javax.inject.Inject;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//
//import org.apache.log4j.Logger;
//
//import static com.google.common.collect.Sets.newHashSet;
//import static org.fest.assertions.Assertions.*;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import de.dkfz.portal.domain.admin.Role;
//import de.dkfz.portal.repository.RoleGenerator;
//import de.dkfz.portal.dao.admin.RoleDao;
//
///**
// * Integration test on RoleDaoImpl
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath*:spring/applicationContext-test.xml" })
//@Transactional
//public class RoleDaoImplTest {
//    @SuppressWarnings("unused")
//    private static final Logger log = Logger.getLogger(RoleDaoImplTest.class);
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Inject
//    private RoleDao roleDao;
//
//    @Inject
//    private RoleGenerator roleGenerator;
//
//    @Test
//    @Rollback
//    public void saveAndGet() {
//        Role role = roleGenerator.getRole();
//
//        // add it to a Set before saving (force equals/hashcode)
//        Set<Role> set = newHashSet(role, role);
//        assertThat(set).hasSize(1);
//
//        roleDao.save(role);
//        entityManager.flush();
//
//        // reload it from cache and check equality
//        Role model = new Role();
//        model.setId(role.getId());
//        assertThat(role).isEqualTo(roleDao.get(model));
//
//        // clear cache to force reload from db
//        entityManager.clear();
//
//        // since you use a business key, equality must be preserved.
//        assertThat(role).isEqualTo(roleDao.get(model));
//    }
//}