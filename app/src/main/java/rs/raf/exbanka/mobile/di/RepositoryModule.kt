package rs.raf.exbanka.mobile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rs.raf.exbanka.mobile.data.repository.AuthRepositoryImpl
import rs.raf.exbanka.mobile.data.repository.TransactionRepositoryImpl
import rs.raf.exbanka.mobile.domain.repository.AuthRepository
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
}
