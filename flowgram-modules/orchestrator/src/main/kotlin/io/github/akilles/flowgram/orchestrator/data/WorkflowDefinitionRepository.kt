package io.github.akilles.flowgram.orchestrator.data

import io.github.akilles.flowgram.orchestrator.entities.WorkflowDefinition
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WorkflowDefinitionRepository : JpaRepository<WorkflowDefinition, UUID>

