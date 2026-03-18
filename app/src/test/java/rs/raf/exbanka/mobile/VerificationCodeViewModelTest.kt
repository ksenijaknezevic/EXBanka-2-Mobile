package rs.raf.exbanka.mobile

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import rs.raf.exbanka.mobile.ui.screens.verificationcode.VerificationCodeViewModel
import rs.raf.exbanka.mobile.ui.screens.verificationcode.toMinutesSeconds

@OptIn(ExperimentalCoroutinesApi::class)
class VerificationCodeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: VerificationCodeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = VerificationCodeViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialize sets code and expiresIn correctly`() = runTest {
        viewModel.initialize("847293", 300)
        assertEquals("847293", viewModel.uiState.value.code)
        assertEquals(300, viewModel.uiState.value.remainingSeconds)
        assertFalse(viewModel.uiState.value.isExpired)
    }

    @Test
    fun `countdown decrements remaining seconds`() = runTest {
        viewModel.initialize("123456", 300)
        advanceTimeBy(5_000L)
        val remaining = viewModel.uiState.value.remainingSeconds
        assertTrue(remaining < 300)
    }

    @Test
    fun `code is expired after countdown reaches zero`() = runTest {
        viewModel.initialize("123456", 2) // 2-second code for test speed
        advanceTimeBy(3_000L)
        assertTrue(viewModel.uiState.value.isExpired)
        assertEquals(0, viewModel.uiState.value.remainingSeconds)
    }

    @Test
    fun `toMinutesSeconds formats 300 as 05_00`() {
        assertEquals("05:00", 300.toMinutesSeconds())
    }

    @Test
    fun `toMinutesSeconds formats 65 as 01_05`() {
        assertEquals("01:05", 65.toMinutesSeconds())
    }

    @Test
    fun `toMinutesSeconds formats 0 as 00_00`() {
        assertEquals("00:00", 0.toMinutesSeconds())
    }
}
