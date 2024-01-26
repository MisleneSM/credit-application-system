package me.dio.credit.application.system.enummeration

import jakarta.persistence.Embeddable

@Embeddable
enum class Status {
   IN_PROGRESS, APPROVED, REJECT
}
