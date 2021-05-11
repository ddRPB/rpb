package de.dktk.dd.rpb.core.domain.admin;

import de.dktk.dd.rpb.core.util.ValueGenerator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Basic tests for Role
 */
@SuppressWarnings("unused")
@RunWith(PowerMockRunner.class)
@PrepareForTest({RoleTest.class, Logger.class})
public class RoleTest {

    @Before
    public void executedBeforeEach() {
        mockStatic(Logger.class);
        Logger logger = mock(Logger.class);
        when(Logger.getLogger(any(Class.class))).thenReturn(logger);
    }

    // test unique primary key
    @Test
    public void newInstanceHasNoPrimaryKey() {
        Role model = new Role();
        assertFalse(model.isIdSet());
    }

    @Test
    public void isIdSetReturnsTrue() {
        Role model = new Role();
        model.setId(ValueGenerator.getUniqueInteger());
        assertNotNull(model.getId());
        assertTrue(model.isIdSet());
    }

    // test columns methods

    @Test
    public void toStringNotNull() {
        Role model = new Role();
        assertNotNull(model.toString());
    }

    @Test
    public void equalsUsingBusinessKey() {
        Role model1 = new Role();
        Role model2 = new Role();
        String roleName = ValueGenerator.getUniqueString(100);
        model1.setName(roleName);
        model2.setName(roleName);
        assertEquals(model1, model2);
        assertEquals(model2, model1);
        assertEquals(model1.hashCode(), model2.hashCode());
    }

}