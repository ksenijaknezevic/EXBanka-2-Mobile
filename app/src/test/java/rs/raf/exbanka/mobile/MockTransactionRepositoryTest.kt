package rs.raf.exbanka.mobile

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import rs.raf.exbanka.mobile.data.repository.MockTransactionRepositoryImpl
import rs.raf.exbanka.mobile.domain.model.TransactionStatus
import rs.raf.exbanka.mobile.util.NetworkResult

class MockTransactionRepositoryTest {

    private lateinit var repository: MockTransactionRepositoryImpl

    @Before
    fun setUp() {
        repository = MockTransactionRepositoryImpl()
    }

    @Test
    fun `getPendingTransactions returns non-empty list`() = runTest {
        val result = repository.getPendingTransactions()
        assertTrue(result is NetworkResult.Success)
        val transactions = (result as NetworkResult.Success).data
        assertTrue(transactions.isNotEmpty())
    }

    @Test
    fun `all returned transactions have PENDING status`() = runTest {
        val result = repository.getPendingTransactions() as NetworkResult.Success
        result.data.forEach { txn ->
            assertEquals(TransactionStatus.PENDING, txn.status)
        }
    }

    @Test
    fun `getTransactionById returns correct transaction`() = runTest {
        val listResult = repository.getPendingTransactions() as NetworkResult.Success
        val firstId = listResult.data.first().id

        val detailResult = repository.getTransactionById(firstId)
        assertTrue(detailResult is NetworkResult.Success)
        assertEquals(firstId, (detailResult as NetworkResult.Success).data.id)
    }

    @Test
    fun `getTransactionById returns error for unknown id`() = runTest {
        val result = repository.getTransactionById("non-existent-id")
        assertTrue(result is NetworkResult.Error)
    }

    @Test
    fun `approveTransaction returns 6-digit verification code`() = runTest {
        val result = repository.approveTransaction("txn-001")
        assertTrue(result is NetworkResult.Success)
        val code = (result as NetworkResult.Success).data.code
        assertEquals(6, code.length)
        assertTrue(code.all { it.isDigit() })
    }

    @Test
    fun `approveTransaction returns 300 second expiry`() = runTest {
        val result = repository.approveTransaction("txn-001") as NetworkResult.Success
        assertEquals(300, result.data.expiresInSeconds)
    }

    @Test
    fun `transactions have required fields populated`() = runTest {
        val result = repository.getPendingTransactions() as NetworkResult.Success
        result.data.forEach { txn ->
            assertFalse(txn.id.isBlank())
            assertFalse(txn.recipientName.isBlank())
            assertFalse(txn.recipientAccount.isBlank())
            assertTrue(txn.amount > 0)
            assertFalse(txn.currency.isBlank())
            assertFalse(txn.purpose.isBlank())
        }
    }
}
