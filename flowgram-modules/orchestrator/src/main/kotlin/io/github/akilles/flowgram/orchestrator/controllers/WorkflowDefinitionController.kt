package io.github.akilles.flowgram.orchestrator.controllers

import io.github.akilles.flowgram.orchestrator.models.WorkflowDefinitionDTO
import io.github.akilles.flowgram.orchestrator.services.WorkflowDefinitionService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/workflow-definitions")
@Tag(name = "Workflow Definitions", description = "API for managing workflow definitions")
class WorkflowDefinitionController(
    private val workflowDefinitionService: WorkflowDefinitionService
) {

    @PostMapping
    fun create(@RequestBody workflowDefinition: WorkflowDefinitionDTO): ResponseEntity<WorkflowDefinitionDTO> {
        val created = workflowDefinitionService.create(workflowDefinition)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<WorkflowDefinitionDTO> {
        val workflowDefinition = workflowDefinitionService.getById(id)
        return if (workflowDefinition != null) {
            ResponseEntity.ok(workflowDefinition)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<WorkflowDefinitionDTO>> {
        val workflowDefinitions = workflowDefinitionService.getAll()
        return ResponseEntity.ok(workflowDefinitions)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody workflowDefinition: WorkflowDefinitionDTO
    ): ResponseEntity<WorkflowDefinitionDTO> {
        val updated = workflowDefinitionService.update(id, workflowDefinition)
        return if (updated != null) {
            ResponseEntity.ok(updated)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        val deleted = workflowDefinitionService.delete(id)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{id}/activate")
    fun activate(@PathVariable id: UUID): ResponseEntity<Void> {
        workflowDefinitionService.subscribe(id)
        return ResponseEntity.accepted().build()
    }
}
