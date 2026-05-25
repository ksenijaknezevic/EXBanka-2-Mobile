package rs.raf.exbanka.mobile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rs.raf.exbanka.mobile.data.repository.AccountRepositoryImpl
import rs.raf.exbanka.mobile.data.repository.AuthRepositoryImpl
import rs.raf.exbanka.mobile.data.repository.CardRepositoryImpl
import rs.raf.exbanka.mobile.data.repository.CreditRepositoryImpl
import rs.raf.exbanka.mobile.data.repository.ExchangeRepositoryImpl
import rs.raf.exbanka.mobile.data.repository.FundRepositoryImpl
import rs.raf.exbanka.mobile.data.repository.OtcRepositoryImpl
import rs.raf.exbanka.mobile.data.repository.TransactionRepositoryImpl
import rs.raf.exbanka.mobile.domain.repository.AccountRepository
import rs.raf.exbanka.mobile.domain.repository.AuthRepository
import rs.raf.exbanka.mobile.domain.repository.CardRepository
import rs.raf.exbanka.mobile.domain.repository.CreditRepository
import rs.raf.exbanka.mobile.domain.repository.ExchangeRepository
import rs.raf.exbanka.mobile.domain.repository.FundRepository
import rs.raf.exbanka.mobile.domain.repository.OtcRepository
import rs.raf.exbanka.mobile.domain.repository.TransactionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindCardRepository(impl: CardRepositoryImpl): CardRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    abstract fun bindExchangeRepository(impl: ExchangeRepositoryImpl): ExchangeRepository

    @Binds
    @Singleton
    abstract fun bindCreditRepository(impl: CreditRepositoryImpl): CreditRepository

    @Binds
    @Singleton
    abstract fun bindOtcRepository(impl: OtcRepositoryImpl): OtcRepository

    @Binds
    @Singleton
    abstract fun bindFundRepository(impl: FundRepositoryImpl): FundRepository
}
