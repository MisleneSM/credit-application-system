package me.dio.credit.application.system.service

import me.dio.credit.application.system.entity.Credit
import java.util.UUID


interface ICreditService {
    fun save(credito: Credit): Credit
    fun findAllByCustomer(customerId: Long): List<Credit>
    fun findByCreditCode(customerId: Long, creditCode: UUID): Credit
}