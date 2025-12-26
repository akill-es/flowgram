package io.github.akilles.flowgram.orchestrator.services

import io.github.akilles.flowgram.models.Destination
import io.github.akilles.flowgram.models.DestinationType
import io.github.akilles.flowgram.models.FilterType
import io.github.akilles.flowgram.models.MessageFilter
import io.github.akilles.flowgram.models.Source
import io.github.akilles.flowgram.models.Task
import io.github.akilles.flowgram.orchestrator.data.WorkflowDefinitionRepository
import io.github.akilles.flowgram.orchestrator.entities.WorkflowDefinition as WorkflowDefinitionEntity
import io.github.akilles.flowgram.orchestrator.models.WorkflowDefinitionDTO
import jakarta.persistence.EntityNotFoundException
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class WorkflowDefinitionService(
    private val repository: WorkflowDefinitionRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    fun create(dto: WorkflowDefinitionDTO): WorkflowDefinitionDTO {
        val entity = toEntity(dto)
        val saved = repository.save(entity)
        return toDTO(saved)
    }

    fun getById(id: UUID): WorkflowDefinitionDTO? {
        return repository.findById(id).map { toDTO(it) }.orElse(null)
    }

    fun getAll(): List<WorkflowDefinitionDTO> {
        return repository.findAll().map { toDTO(it) }
    }

    fun update(id: UUID, dto: WorkflowDefinitionDTO): WorkflowDefinitionDTO? {
        val existingEntity = repository.findById(id).orElse(null) ?: return null
        val updatedEntity = existingEntity.copy(
            sourcesChatIds = dto.sourcesChatIds,
            destinationChatIds = dto.destinationChatIds,
            botKey = dto.botKey,
            filterStrings = dto.filterStrings
        )
        val saved = repository.save(updatedEntity)
        return toDTO(saved)
    }

    fun delete(id: UUID): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else {
            false
        }
    }

    fun subscribe(id: UUID) {
        val topic = "tasks.poll"
        val entity = repository.findById(id).orElseThrow { EntityNotFoundException("Workflow definition with ID $id not found") }
        val topicDto = Task.PollTask(
            workflowId = entity.longId,
            filter = MessageFilter(
                FilterType.KEYWORD,
                entity.filterStrings.joinToString(",")
            ),
            destination = entity.destinationChatIds.map {
                Destination(
                    type = DestinationType.SEND,
                    chatId = it,
                    botKey = entity.botKey
                )
            },
            sources = listOf(Source(
                botName = "shiny_workflow_telegram_bot",
                botKey = entity.botKey
            ))
        )
        kafkaTemplate.send(topic, topicDto)
    }

    private fun toEntity(dto: WorkflowDefinitionDTO): WorkflowDefinitionEntity {
        val uuid = UUID.randomUUID()
        return WorkflowDefinitionEntity(
            longId = uuid.mostSignificantBits,
            sourcesChatIds = dto.sourcesChatIds,
            destinationChatIds = dto.destinationChatIds,
            botKey = dto.botKey,
            filterStrings = dto.filterStrings
        )
    }

    private fun toDTO(entity: WorkflowDefinitionEntity): WorkflowDefinitionDTO {
        return WorkflowDefinitionDTO(
            sourcesChatIds = entity.sourcesChatIds,
            destinationChatIds = entity.destinationChatIds,
            botKey = entity.botKey,
            filterStrings = entity.filterStrings
        )
    }
}
