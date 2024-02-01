package me.dio.credit.application.system.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.enummeration.Status
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.service.impl.CreditService
import me.dio.credit.application.system.service.impl.CustomerService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

//@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK
    lateinit var creditRepository: CreditRepository

    @MockK
    lateinit var customerService: CustomerService

    @InjectMockKs
    lateinit var creditService: CreditService


    @Test
    fun `should save credit successfully`(){
        // given
        val fakeCredit: Credit = buildCredit()
        every { customerService.findById(fakeCredit.customer?.id!!) } returns fakeCredit.customer!!
        every { creditRepository.save(any()) } returns fakeCredit
        //then
        val actual: Credit = creditService.save(fakeCredit)

        //when
        assertThat(actual).isNotNull
        assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) {customerService.findById(fakeCredit.customer?.id!!)}
        verify(exactly = 1) {creditRepository.save(fakeCredit)}
    }

    @Test
    fun `should find all credits by customer id`() {
        // given
        val fakeCustomerId: Long = 1
        every { creditRepository.findAllByCustomerId(fakeCustomerId) } returns listOf(buildCredit())

        // when
        val actual: List<Credit> = creditService.findAllByCustomer(fakeCustomerId)

        // then
        assertThat(actual).isNotNull
        assertThat(actual).isNotEmpty
        verify(exactly = 1) { creditRepository.findAllByCustomerId(fakeCustomerId) }
    }

    @Test
    fun `should find credit by credit code and customer id`() {
        // given
        val fakeCustomerId: Long = 1
        val fakeCreditCode: UUID = UUID.randomUUID()
        val fakeCredit: Credit = buildCredit(id = fakeCustomerId, creditCode = fakeCreditCode)
        every { creditRepository.findByCreditCode(fakeCreditCode) } returns fakeCredit

        // when
        val actual: Credit = creditService.findByCreditCode(fakeCustomerId, fakeCreditCode)

        // then
        assertThat(actual).isNotNull
        assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) { creditRepository.findByCreditCode(fakeCreditCode) }
    }

    @Test
    fun `should throw exception when credit code not found`() {
        // given
        val fakeCustomerId: Long = 1
        val fakeCreditCode: UUID = UUID.randomUUID()
        every { creditRepository.findByCreditCode(fakeCreditCode) } returns null

        // when, then
        assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.findByCreditCode(fakeCustomerId, fakeCreditCode) }
            .withMessage("Creditcode $fakeCreditCode not found")

        verify(exactly = 1) { creditRepository.findByCreditCode(fakeCreditCode) }
    }

    @Test
    fun `should validate day of first installment successfully`() {
        // given
        val validDate = LocalDate.now().minusMonths(2)
        // when
        val actual = creditService.validDayFirstInstallment(validDate)
        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `should throw exception for invalid day of first installment`() {
        // given
        val invalidDate = LocalDate.now().plusMonths(3)
        // when, then
        assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.validDayFirstInstallment(invalidDate) }
            .withMessage("Invalid Date")
    }

    private fun buildCredit(
        creditCode: UUID = UUID.randomUUID(),
        creditValue: BigDecimal = BigDecimal.ZERO,
        dayFirstInstallment: LocalDate = LocalDate.now(),
        numberOfInstallments: Int = 0,
        status: Status = Status.IN_PROGRESS,
        customer: Customer? = buildCustomer(),
        id: Long? = null
    ) = Credit (
        creditCode = creditCode,
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        status = status,
        customer = customer,
        id = id
    )

    private fun buildCustomer(
        id: Long = 1
    ) = Customer(id = id)
}