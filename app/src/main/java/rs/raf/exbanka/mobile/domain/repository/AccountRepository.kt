package rs.raf.exbanka.mobile.domain.repository

import rs.raf.exbanka.mobile.domain.model.Account
import rs.raf.exbanka.mobile.domain.model.AccountTransaction
import rs.raf.exbanka.mobile.util.NetworkResult

interface AccountRepository {
    /** Vraća sve račune ulogovanog klijenta. */
    suspend fun getMyAccounts(): NetworkResult<List<Account>>

    /** Vraća istoriju transakcija za zadati račun. Valuta se popunjava iz [currency]. */
    suspend fun getAccountTransactions(
        racunId: String,
        currency: String
    ): NetworkResult<List<AccountTransaction>>
}
