package rs.raf.exbanka.mobile.domain.repository

import rs.raf.exbanka.mobile.domain.model.Card
import rs.raf.exbanka.mobile.util.NetworkResult

interface CardRepository {
    /** Vraća sve kartice ulogovanog klijenta. */
    suspend fun getMyCards(): NetworkResult<List<Card>>

    /** Blokira aktivnu karticu ulogovanog klijenta. */
    suspend fun blockCard(id: String): NetworkResult<Unit>
}
