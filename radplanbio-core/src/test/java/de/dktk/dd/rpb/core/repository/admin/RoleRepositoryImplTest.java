package de.dktk.dd.rpb.core.repository.admin;

import de.dktk.dd.rpb.core.dao.admin.RoleDao;
import de.dktk.dd.rpb.core.dao.support.SearchParameters;
import de.dktk.dd.rpb.core.domain.admin.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Unit test on RoleRepositoryImpl
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RoleRepositoryImplTest.class, Logger.class, LoggerFactory.class})
public class RoleRepositoryImplTest {

    private RoleRepositoryImpl roleRepositoryImpl;
    private RoleDao roleDao;

    @Before
    public void setUp() {
        // called before each test.
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
        roleRepositoryImpl = new RoleRepositoryImpl();
        roleDao = mock(RoleDao.class);
        roleRepositoryImpl.setRoleDao(roleDao);
    }

    @Test
    public void testFindUniqueOrNoneCaseNone() {
        Role none = null;

        when(roleDao.findUniqueOrNone(any(Role.class), any(SearchParameters.class))).thenReturn(none);

        Role result = roleRepositoryImpl.findUniqueOrNone(new Role());

        assertThat(result).isNull();
        verify(roleDao, times(1)).findUniqueOrNone(any(Role.class), any(SearchParameters.class));
    }

    @Test
    public void testFindUniqueOrNoneCaseUnique() {
        Role unique = new Role();

        when(roleDao.findUniqueOrNone(any(Role.class), any(SearchParameters.class))).thenReturn(unique);

        Role result = roleRepositoryImpl.findUniqueOrNone(new Role());

        assertThat(result).isNotNull();
        verify(roleDao, times(1)).findUniqueOrNone(any(Role.class), any(SearchParameters.class));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NonUniqueResultException.class)
    public void testFindUniqueOrNoneCaseMultiple() {
        when(roleDao.findUniqueOrNone(any(Role.class), any(SearchParameters.class))).thenThrow(NonUniqueResultException.class);

        roleRepositoryImpl.findUniqueOrNone(new Role());
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NoResultException.class)
    public void testFindUniqueCaseNone() {
        when(roleDao.findUnique(any(Role.class), any(SearchParameters.class))).thenThrow(NoResultException.class);

        roleRepositoryImpl.findUnique(new Role());
    }

    @Test
    public void testFindUniqueCaseUnique() {
        Role unique = new Role();

        when(roleDao.findUnique(any(Role.class), any(SearchParameters.class))).thenReturn(unique);

        Role result = roleRepositoryImpl.findUnique(new Role());

        assertThat(result).isNotNull();
        verify(roleDao, times(1)).findUnique(any(Role.class), any(SearchParameters.class));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NonUniqueResultException.class)
    public void testFindUniqueCaseMultiple() {
        when(roleDao.findUnique(any(Role.class), any(SearchParameters.class))).thenThrow(NonUniqueResultException.class);

        roleRepositoryImpl.findUnique(new Role());
    }
}
