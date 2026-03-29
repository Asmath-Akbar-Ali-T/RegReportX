package com.cts.regreportx.controller;

import com.cts.regreportx.model.RegTemplate;
import com.cts.regreportx.model.TemplateField;
import com.cts.regreportx.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/templates")
@PreAuthorize("hasRole('REGTECH_ADMIN')")
public class TemplateController {

    private final TemplateService templateService;

    @Autowired
    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public ResponseEntity<List<RegTemplate>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegTemplate> getTemplateById(@PathVariable Integer id) {
        return templateService.getTemplateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RegTemplate> createTemplate(@RequestBody RegTemplate template) {
        RegTemplate saved = templateService.createTemplate(template);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegTemplate> updateTemplate(@PathVariable Integer id, @RequestBody RegTemplate template) {
        try {
            RegTemplate updated = templateService.updateTemplate(id, template);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Integer id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok().build();
    }

    // --- TemplateField Endpoints ---

    @GetMapping("/{templateId}/fields")
    public ResponseEntity<List<TemplateField>> getFieldsByTemplateId(@PathVariable Integer templateId) {
        return ResponseEntity.ok(templateService.getFieldsByTemplateId(templateId));
    }

    @PostMapping("/{templateId}/fields")
    public ResponseEntity<TemplateField> addFieldToTemplate(
            @PathVariable Integer templateId,
            @RequestBody TemplateField field) {
        TemplateField saved = templateService.addFieldToTemplate(templateId, field);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/fields/{fieldId}")
    public ResponseEntity<TemplateField> updateField(@PathVariable Integer fieldId, @RequestBody TemplateField field) {
        try {
            TemplateField updated = templateService.updateField(fieldId, field);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/fields/{fieldId}")
    public ResponseEntity<Void> deleteField(@PathVariable Integer fieldId) {
        templateService.deleteField(fieldId);
        return ResponseEntity.ok().build();
    }
}
