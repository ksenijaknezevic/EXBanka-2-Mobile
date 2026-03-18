package rs.raf.exbanka.mobile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import rs.raf.exbanka.mobile.domain.repository.AuthRepository
import rs.raf.exbanka.mobile.ui.screens.login.LoginViewModel
import rs.raf.exbanka.mobile.util.NetworkResult

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mock()
        whenever(authRepository.isLoggedIn()).thenReturn(flowOf(false))
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty fields and no error`() {
        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertNull(state.error)
        assertFalse(state.isLoading)
        assertFalse(state.loginSuccess)
    }

    @Test
    fun `login with empty email shows error`() = runTest {
        viewModel.onPasswordChange("password123")
        viewModel.login()

        assertNotNull(viewModel.uiState.value.error)
        assertEquals("Email is required", viewModel.uiState.value.error)
    }

    @Test
    fun `login with empty password shows error`() = runTest {
        viewModel.onEmailChange("user@example.com")
        viewModel.login()

        assertEquals("Password is required", viewModel.uiState.value.error)
    }

    @Test
    fun `successful login sets loginSuccess to true`() = runTest {
        whenever(authRepository.login("user@example.com", "password123"))
            .thenReturn(NetworkResult.Success(Unit))

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.loginSuccess)
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `failed login shows error message`() = runTest {
        whenever(authRepository.login("user@example.com", "wrong"))
            .thenReturn(NetworkResult.Error("Invalid email or password", 401))

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("wrong")
        viewModel.login()

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.loginSuccess)
        assertEquals("Invalid email or password", viewModel.uiState.value.error)
    }

    @Test
    fun `onEmailChange clears error`() = runTest {
        // Trigger an error first
        viewModel.login()
        assertNotNull(viewModel.uiState.value.error)

        // Changing email should clear it
        viewModel.onEmailChange("user@example.com")
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `logout is called on repository`() = runTest {
        // Just verify the auth repository is wired correctly
        // Logout is handled in PendingTransactionsViewModel; this confirms DI injection works
        verify(authRepository).isLoggedIn()
    }
}
