package de.dktk.dd.rpb.core.domain.admin;

import static org.junit.Assert.*;

import de.dktk.dd.rpb.core.util.ValueGenerator;
import org.junit.Test;

/**
 * Basic tests for Role
 */
@SuppressWarnings("unused")
public class RoleTest {

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
        assertTrue(model1.equals(model2));
        assertTrue(model2.equals(model1));
        assertTrue(model1.hashCode() == model2.hashCode());
    }

}