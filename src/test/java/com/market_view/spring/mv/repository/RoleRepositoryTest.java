package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void setUp() {
        roleRepository.deleteAll();
    }

    @Test
    public void testSaveAndFindByName() {
        Role role = new Role();
        role.setName(Role.ROLE_USER);
        role.setDescription("Standard user role");

        roleRepository.save(role);

        Optional<Role> found = roleRepository.findByName(Role.ROLE_USER);
        assertTrue(found.isPresent());
        assertEquals(Role.ROLE_USER, found.get().getName());
        assertEquals("Standard user role", found.get().getDescription());
    }

    @Test
    public void testFindByName_NotFound() {
        Optional<Role> found = roleRepository.findByName("NON_EXISTENT_ROLE");
        assertFalse(found.isPresent());
    }

    @Test
    public void testDuplicateRoleName_Overwrites() {
        Role role1 = new Role("ROLE_ADMIN");
        role1.setDescription("Admin Description");
        roleRepository.save(role1);

        // Overwrite with same name, but different description
        Role role2 = new Role("ROLE_ADMIN");
        role2.setDescription("Overwritten Description");

        // To force update by name (since Mongo uses _id by default), we must:
        // 1. find role1, set its ID into role2, then save it
        role2.setId(role1.getId());
        roleRepository.save(role2);  // This will now truly overwrite

        Optional<Role> result = roleRepository.findByName("ROLE_ADMIN");
        assertTrue(result.isPresent());
        assertEquals("Overwritten Description", result.get().getDescription());
    }


}
