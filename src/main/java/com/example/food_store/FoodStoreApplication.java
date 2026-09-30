package com.example.food_store;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.food_store.domain.Role;
import com.example.food_store.domain.User;
import com.example.food_store.repository.RoleRepository;
import com.example.food_store.repository.UserRepository;

@SpringBootApplication
public class FoodStoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(FoodStoreApplication.class, args);
	}

	@Bean
    @Order(1)
    CommandLineRunner initDatabase(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role adminRole = new Role();
                adminRole.setName("ADMIN");
                adminRole.setDescription("");

                Role userRole = new Role();
                userRole.setName("USER");
                userRole.setDescription("");

                roleRepository.save(adminRole);
                roleRepository.save(userRole);

                System.out.println("Imported default roles: ADMIN, USER");
            }
        };
    }

    @Bean
	@Order(2)
    CommandLineRunner initDatabaseUser(RoleRepository roleRepository,UserRepository userRepository, PasswordEncoder passwordEncoder) {    
    return args -> {
        if (userRepository.count() == 0) {
            User adminUser = new User();
            adminUser.setEmail("letiendat@vti.com");
            adminUser.setPassword(passwordEncoder.encode("123456"));
            adminUser.setFullName("LeTienDat");
            adminUser.setRole(roleRepository.findByName("ADMIN"));
            adminUser.setProvider("LOCAL");
            adminUser.setAddress("HaNoi");
            adminUser.setAvatar("");
            adminUser.setPhone("0343866075");
            
            userRepository.save(adminUser);
            
            System.out.println("Imported default admin user: " + adminUser.getEmail());
        }
    };
}
}