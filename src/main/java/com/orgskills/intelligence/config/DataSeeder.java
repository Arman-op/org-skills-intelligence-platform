package com.orgskills.intelligence.config;

import com.orgskills.intelligence.entity.RoleCompetency;
import com.orgskills.intelligence.entity.Skill;
import com.orgskills.intelligence.entity.User;
import com.orgskills.intelligence.entity.UserSkill;
import com.orgskills.intelligence.entity.enums.ProficiencyLevel;
import com.orgskills.intelligence.entity.enums.Role;
import com.orgskills.intelligence.repository.RoleCompetencyRepository;
import com.orgskills.intelligence.repository.SkillRepository;
import com.orgskills.intelligence.repository.UserRepository;
import com.orgskills.intelligence.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final RoleCompetencyRepository roleCompetencyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (skillRepository.count() > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }
        log.info("Seeding database with sample data...");
        seedSkills();
        seedUsers();
        seedRoleCompetencies();
        seedUserSkills();
        log.info("Database seeding completed.");
    }

    private void seedSkills() {
        List<Skill> skills = List.of(
                createSkill("Java", "Technical", "Core Java programming including OOP, collections, streams, and concurrency"),
                createSkill("Spring Boot", "Technical", "Spring Boot framework for building production-ready applications"),
                createSkill("React", "Technical", "React.js library for building user interfaces"),
                createSkill("Python", "Technical", "Python programming for scripting, data analysis, and backend development"),
                createSkill("SQL", "Technical", "Relational database querying and design"),
                createSkill("Docker", "DevOps", "Containerization and container orchestration"),
                createSkill("AWS", "Cloud", "Amazon Web Services cloud platform services"),
                createSkill("Communication", "Soft Skills", "Effective verbal and written communication"),
                createSkill("Leadership", "Management", "Team leadership, delegation, and strategic thinking"),
                createSkill("Agile", "Process", "Agile methodologies including Scrum and Kanban")
        );
        skillRepository.saveAll(skills);
    }

    private void seedUsers() {
        User employee = new User();
        employee.setEmail("employee@orgskills.com");
        employee.setPassword(passwordEncoder.encode("password123"));
        employee.setFullName("Alice Johnson");
        employee.setRole(Role.EMPLOYEE);
        employee.setDepartment("Engineering");
        employee.setJobTitle("Software Engineer");
        userRepository.save(employee);

        User manager = new User();
        manager.setEmail("manager@orgskills.com");
        manager.setPassword(passwordEncoder.encode("password123"));
        manager.setFullName("Bob Smith");
        manager.setRole(Role.MANAGER);
        manager.setDepartment("Engineering");
        manager.setJobTitle("Engineering Manager");
        userRepository.save(manager);

        User admin = new User();
        admin.setEmail("admin@orgskills.com");
        admin.setPassword(passwordEncoder.encode("password123"));
        admin.setFullName("Carol Admin");
        admin.setRole(Role.HR_ADMIN);
        admin.setDepartment("Human Resources");
        admin.setJobTitle("HR Director");
        userRepository.save(admin);
    }

    private void seedRoleCompetencies() {
        Map<String, ProficiencyLevel> seCompetencies = Map.of(
                "Java", ProficiencyLevel.ADVANCED,
                "Spring Boot", ProficiencyLevel.INTERMEDIATE,
                "SQL", ProficiencyLevel.INTERMEDIATE,
                "Docker", ProficiencyLevel.BEGINNER,
                "Communication", ProficiencyLevel.INTERMEDIATE
        );
        seCompetencies.forEach((skillName, level) ->
                skillRepository.findByNameIgnoreCase(skillName).ifPresent(skill -> {
                    RoleCompetency rc = new RoleCompetency();
                    rc.setJobTitle("Software Engineer");
                    rc.setDepartment("Engineering");
                    rc.setSkill(skill);
                    rc.setRequiredProficiencyLevel(level);
                    roleCompetencyRepository.save(rc);
                }));

        Map<String, ProficiencyLevel> emCompetencies = Map.of(
                "Leadership", ProficiencyLevel.ADVANCED,
                "Communication", ProficiencyLevel.ADVANCED,
                "Agile", ProficiencyLevel.ADVANCED,
                "Java", ProficiencyLevel.INTERMEDIATE
        );
        emCompetencies.forEach((skillName, level) ->
                skillRepository.findByNameIgnoreCase(skillName).ifPresent(skill -> {
                    RoleCompetency rc = new RoleCompetency();
                    rc.setJobTitle("Engineering Manager");
                    rc.setDepartment("Engineering");
                    rc.setSkill(skill);
                    rc.setRequiredProficiencyLevel(level);
                    roleCompetencyRepository.save(rc);
                }));
    }

    private void seedUserSkills() {
        User alice = userRepository.findByEmail("employee@orgskills.com").orElseThrow();
        assignSkill(alice, "Java", ProficiencyLevel.INTERMEDIATE, 3.0);
        assignSkill(alice, "Spring Boot", ProficiencyLevel.BEGINNER, 2.0);
        assignSkill(alice, "SQL", ProficiencyLevel.INTERMEDIATE, 3.5);
        assignSkill(alice, "Communication", ProficiencyLevel.BEGINNER, 1.5);

        User bob = userRepository.findByEmail("manager@orgskills.com").orElseThrow();
        assignSkill(bob, "Java", ProficiencyLevel.EXPERT, 5.0);
        assignSkill(bob, "Leadership", ProficiencyLevel.INTERMEDIATE, 3.0);
        assignSkill(bob, "Communication", ProficiencyLevel.INTERMEDIATE, 3.0);
        assignSkill(bob, "Agile", ProficiencyLevel.BEGINNER, 2.0);
    }

    private void assignSkill(User user, String skillName, ProficiencyLevel level, double rating) {
        skillRepository.findByNameIgnoreCase(skillName).ifPresent(skill -> {
            UserSkill us = new UserSkill();
            us.setUser(user);
            us.setSkill(skill);
            us.setProficiencyLevel(level);
            us.setRatingScore(rating);
            userSkillRepository.save(us);
        });
    }

    private Skill createSkill(String name, String category, String description) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(category);
        skill.setDescription(description);
        return skill;
    }
}
