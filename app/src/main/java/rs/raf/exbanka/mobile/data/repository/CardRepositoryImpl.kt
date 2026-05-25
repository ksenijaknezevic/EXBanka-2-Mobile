package rs.raf.exbanka.mobile.data.repository

import rs.raf.exbanka.mobile.data.remote.api.CardApi
import rs.raf.exbanka.mobile.domain.model.Card
import rs.raf.exbanka.mobile.domain.repository.CardRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardApi: CardApi
) : CardRepository {

    override suspend fun getMyCards(): NetworkResult<List<Card>> {
        return try {
            val response = cardApi.getMyCards()
            if (response.isSuccessful) {
                val cards = response.body()?.kartice?.map { it.toDomain() } ?: emptyList()
                NetworkResult.Success(cards)
            } else {
                NetworkResult.Error("Greška pri učitavanju kartica (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }

    override suspend fun blockCard(id: String): NetworkResult<Unit> {
        return try {
            val response = cardApi.blockCard(id)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                val msg = when (response.code()) {
                    403 -> "Kartica ne pripada vama"
                    404 -> "Kartica nije pronađena"
                    400 -> "Kartica nije aktivna — već je blokirana ili deaktivirana"
                    else -> "Greška pri blokiranju (${response.code()})"
                }
                NetworkResult.Error(msg, response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }
}
