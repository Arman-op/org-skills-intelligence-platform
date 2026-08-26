package com.infosys.knowledgeplatform;

import com.infosys.knowledgeplatform.model.Article;
import com.infosys.knowledgeplatform.model.TrainingProgram;
import com.infosys.knowledgeplatform.model.Skill;
import com.infosys.knowledgeplatform.repository.SkillRepository;
import com.infosys.knowledgeplatform.model.User;
import com.infosys.knowledgeplatform.repository.ArticleRepository;
import com.infosys.knowledgeplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private com.infosys.knowledgeplatform.repository.TrainingProgramRepository trainingProgramRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.infosys.knowledgeplatform.repository.RoleRepository roleRepository;

    @Autowired
    private com.infosys.knowledgeplatform.repository.PermissionRepository permissionRepository;

    @Autowired
    private com.infosys.knowledgeplatform.service.RoleCatalogService roleCatalogService;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User u1 = new User();
            u1.setName("Admin User");
            u1.setEmail("admin@infosys.com");
            u1.setPassword(passwordEncoder.encode("Admin@123"));
            u1.setRole("System Administrator");
            u1.setDepartment("IT");

            User u2 = new User();
            u2.setName("Maya Lead");
            u2.setEmail("manager@infosys.com");
            u2.setPassword(passwordEncoder.encode("Manager@123"));
            u2.setRole("Team Lead / Manager");
            u2.setDepartment("Engineering");

            User u3 = new User();
            u3.setName("Rita HR");
            u3.setEmail("hr@infosys.com");
            u3.setPassword(passwordEncoder.encode("HR@123"));
            u3.setRole("HR Specialist");
            u3.setDepartment("Human Resources");

            User u4 = new User();
            u4.setName("Dinesh Dept");
            u4.setEmail("department@infosys.com");
            u4.setPassword(passwordEncoder.encode("Dept@123"));
            u4.setRole("Department Head");
            u4.setDepartment("Operations");

            User u5 = new User();
            u5.setName("Leena Mentor");
            u5.setEmail("mentor@infosys.com");
            u5.setPassword(passwordEncoder.encode("Mentor@123"));
            u5.setRole("Learning & Development Admin/mentor");
            u5.setDepartment("Learning & Development");

            User u6 = new User();
            u6.setName("John Employee");
            u6.setEmail("employee@infosys.com");
            u6.setPassword(passwordEncoder.encode("Employee@123"));
            u6.setRole("Employee");
            u6.setTargetRole("Backend Developer");
            u6.setDepartment("HR");

            userRepository.saveAll(List.of(u1, u2, u3, u4, u5, u6));
        }

        if (articleRepository.count() == 0) {
            Article a1 = new Article();
            a1.setTitle("Company Holiday Schedule 2026");
            a1.setAuthor("HR Dept");
            a1.setDateStr(LocalDate.now());
            a1.setStatus("Published");

            Article a2 = new Article();
            a2.setTitle("Q3 Engineering OKRs");
            a2.setAuthor("Jane Smith");
            a2.setDateStr(LocalDate.now().minusDays(2));
            a2.setStatus("Draft");

            Article a3 = new Article();
            a3.setTitle("Frontend Styling Guide");
            a3.setAuthor("Frontend Team");
            a3.setDateStr(LocalDate.now().minusDays(5));
            a3.setStatus("Published");

            articleRepository.saveAll(List.of(a1, a2, a3));
        }

        if (trainingProgramRepository.count() == 0) {
            TrainingProgram t1 = new TrainingProgram();
            t1.setTitle("Internal Security Basics");
            t1.setProvider("Internal HR");
            t1.setDurationHours(6);
            t1.setTargetSkillCategory("Security");
            t1.setUrl("https://internal.company/courses/security-basics");

            TrainingProgram t2 = new TrainingProgram();
            t2.setTitle("Advanced Cloud Architectures");
            t2.setProvider("Internal Engineering");
            t2.setDurationHours(12);
            t2.setTargetSkillCategory("Cloud");
            t2.setUrl("https://internal.company/courses/cloud-advanced");

            TrainingProgram t3 = new TrainingProgram();
            t3.setTitle("Generative AI for Professionals");
            t3.setProvider("Coursera Enterprise");
            t3.setDurationHours(4);
            t3.setTargetSkillCategory("AI");
            t3.setUrl("https://coursera.org/course/generative-ai");

            trainingProgramRepository.saveAll(List.of(t1, t2, t3));
        }

        // Seed basic skills
        if (skillRepository.count() == 0) {
            Skill s1 = new Skill();
            s1.setName("React.js");
            s1.setCategory("Frontend");
            s1.setDescription("Popular UI library");
            s1.setSkillType("technical");
            s1.setCriticality(4);

            Skill s2 = new Skill();
            s2.setName("Java Spring Boot");
            s2.setCategory("Backend");
            s2.setDescription("Enterprise Java framework");
            s2.setSkillType("technical");
            s2.setCriticality(5);

            Skill s3 = new Skill();
            s3.setName("AWS Cloud");
            s3.setCategory("DevOps");
            s3.setDescription("Cloud infra");
            s3.setSkillType("technical");
            s3.setCriticality(5);

            Skill s4 = new Skill();
            s4.setName("Generative AI");
            s4.setCategory("AI/ML");
            s4.setDescription("GenAI basics");
            s4.setSkillType("technical");
            s4.setCriticality(3);

            skillRepository.saveAll(List.of(s1, s2, s3, s4));
        }

        // Seed roles and permissions using RoleCatalogService
        if (permissionRepository.count() == 0 || roleRepository.count() == 0) {
            roleCatalogService.defaultRoles().forEach(rn -> {
                com.infosys.knowledgeplatform.service.RoleCatalogService.RoleProfile rp = roleCatalogService.getProfile(rn);
                // create permissions
                rp.permissions().forEach(pn -> {
                    permissionRepository.findByName(pn).orElseGet(() -> {
                        com.infosys.knowledgeplatform.model.Permission p = new com.infosys.knowledgeplatform.model.Permission();
                        p.setName(pn);
                        p.setDescription(pn);
                        return permissionRepository.save(p);
                    });
                });

                // create role
                roleRepository.findByName(rp.roleKey()).orElseGet(() -> {
                    com.infosys.knowledgeplatform.model.Role role = new com.infosys.knowledgeplatform.model.Role();
                    role.setName(rp.roleKey());
                    role.setDescription(rp.displayName());
                    java.util.Set<com.infosys.knowledgeplatform.model.Permission> perms = new java.util.HashSet<>();
                    rp.permissions().forEach(pn -> permissionRepository.findByName(pn).ifPresent(perms::add));
                    role.setPermissions(perms);
                    return roleRepository.save(role);
                });
            });
        }
    }
}
