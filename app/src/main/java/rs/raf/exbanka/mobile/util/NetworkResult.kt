package rs.raf.exbanka.mobile.util

/**
 * Generic wrapper for API call results.
 * Used throughout the repository and ViewModel layers to represent
 * loading, success, and error states.
 */
sealed class NetworkResult<out T> {
    data object Loading : NetworkResult<Nothing>()
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
}
