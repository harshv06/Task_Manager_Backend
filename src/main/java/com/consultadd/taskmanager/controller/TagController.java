package com.consultadd.taskmanager.controller;

import com.consultadd.taskmanager.dto.TagDTO;
import com.consultadd.taskmanager.model.Tag;
import com.consultadd.taskmanager.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagDTO>> listTags() {
        return ResponseEntity.ok(tagService.getAllTags()
                .stream().map(t -> new TagDTO(t.getId(), t.getName())).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<TagDTO> create(@RequestBody TagDTO dto) {
        Tag tag = tagService.create(Tag.builder().name(dto.getName()).build());
        return ResponseEntity.ok(new TagDTO(tag.getId(), tag.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> update(@PathVariable Long id, @RequestBody TagDTO dto) {
        Tag tag = tagService.update(id, dto.getName());
        return ResponseEntity.ok(new TagDTO(tag.getId(), tag.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.ok().build();
    }
}
