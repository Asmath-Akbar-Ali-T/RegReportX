package com.cts.regreportx.controller;

import com.cts.regreportx.dto.RegTemplateDto;
import com.cts.regreportx.dto.TemplateFieldDto;
import com.cts.regreportx.model.RegTemplate;
import com.cts.regreportx.model.TemplateField;
import com.cts.regreportx.service.TemplateService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/templates")
@PreAuthorize("hasRole('REGTECH_ADMIN')")
public class TemplateController {

    private final TemplateService templateService;
    private final ModelMapper modelMapper;

    @Autowired
    public TemplateController(TemplateService templateService, ModelMapper modelMapper) {
        this.templateService = templateService;
        this.modelMapper = modelMapper;
    }

    private RegTemplateDto mapToTemplateDto(RegTemplate entity) {
        return modelMapper.map(entity, RegTemplateDto.class);
    }

    private RegTemplate mapToTemplateEntity(RegTemplateDto dto) {
        return modelMapper.map(dto, RegTemplate.class);
    }

    private TemplateFieldDto mapToFieldDto(TemplateField entity) {
        return modelMapper.map(entity, TemplateFieldDto.class);
    }

    private TemplateField mapToFieldEntity(TemplateFieldDto dto) {
        return modelMapper.map(dto, TemplateField.class);
    }

    @GetMapping
    public ResponseEntity<List<RegTemplateDto>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates().stream()
                .map(this::mapToTemplateDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegTemplateDto> getTemplateById(@PathVariable Integer id) {
        return templateService.getTemplateById(id)
                .map(this::mapToTemplateDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RegTemplateDto> createTemplate(@RequestBody RegTemplateDto templateDto) {
        RegTemplate saved = templateService.createTemplate(mapToTemplateEntity(templateDto));
        return ResponseEntity.ok(mapToTemplateDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegTemplateDto> updateTemplate(@PathVariable Integer id, @RequestBody RegTemplateDto templateDto) {
        try {
            RegTemplate updated = templateService.updateTemplate(id, mapToTemplateEntity(templateDto));
            return ResponseEntity.ok(mapToTemplateDto(updated));
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
    public ResponseEntity<List<TemplateFieldDto>> getFieldsByTemplateId(@PathVariable Integer templateId) {
        return ResponseEntity.ok(templateService.getFieldsByTemplateId(templateId).stream()
                .map(this::mapToFieldDto)
                .collect(Collectors.toList()));
    }

    @PostMapping("/{templateId}/fields")
    public ResponseEntity<TemplateFieldDto> addFieldToTemplate(
            @PathVariable Integer templateId, 
            @RequestBody TemplateFieldDto fieldDto) {
        TemplateField saved = templateService.addFieldToTemplate(templateId, mapToFieldEntity(fieldDto));
        return ResponseEntity.ok(mapToFieldDto(saved));
    }

    @PutMapping("/fields/{fieldId}")
    public ResponseEntity<TemplateFieldDto> updateField(@PathVariable Integer fieldId, @RequestBody TemplateFieldDto fieldDto) {
        try {
            TemplateField updated = templateService.updateField(fieldId, mapToFieldEntity(fieldDto));
            return ResponseEntity.ok(mapToFieldDto(updated));
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
