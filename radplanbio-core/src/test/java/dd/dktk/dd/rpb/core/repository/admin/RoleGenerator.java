package de.dktk.dd.rpb.core.repository.admin;

import javax.inject.Named;
import javax.inject.Singleton;
import de.dktk.dd.rpb.core.domain.admin.Role;
import de.dktk.dd.rpb.core.util.ValueGenerator;

/**
 * Helper class to create transient entities instance for testing purposes.
 * Simple properties are pre-filled with random values.
 */
@SuppressWarnings("unused")
@Named
@Singleton
public class RoleGenerator {

    /**
     * Returns a new Role instance filled with random values.
     */
    public Role getRole() {
        Role role = new Role();

        // simple attributes follows
        role.setName(ValueGenerator.getUniqueString(100));

        return role;
    }

}