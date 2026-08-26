package com.infosys.knowledgeplatform.controller;

import com.infosys.knowledgeplatform.model.Skill;
import com.infosys.knowledgeplatform.repository.SkillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class SkillController {

    private final SkillRepository skillRepository;

    public SkillController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @GetMapping
    public List<Skill> list() {
        return skillRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Skill> getById(@PathVariable Long id) {
        return skillRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Skill skill) {
        if (skill.getName() == null || skill.getName().isBlank()) return ResponseEntity.badRequest().body("name required");
        if (skillRepository.findByName(skill.getName()).isPresent()) return ResponseEntity.status(409).body("already exists");
        return ResponseEntity.ok(skillRepository.save(skill));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Skill s) {
        return skillRepository.findById(id).map(existing -> {
            existing.setName(s.getName());
            existing.setCategory(s.getCategory());
            existing.setDescription(s.getDescription());
            existing.setCriticality(s.getCriticality());
            existing.setSkillType(s.getSkillType());
            return ResponseEntity.ok(skillRepository.save(existing));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!skillRepository.existsById(id)) return ResponseEntity.notFound().build();
        skillRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
