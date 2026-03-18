package rs.raf.exbanka.mobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rs.raf.exbanka.mobile.BuildConfig
import rs.raf.exbanka.mobile.data.remote.api.AuthApi
import rs.raf.exbanka.mobile.data.remote.api.TransactionApi
import rs.raf.exbanka.mobile.data.remote.interceptor.AuthInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/** Qualifier za Retrofit koji pokazuje na user-service (port 8082, auth) */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserRetrofit

/** Qualifier za Retrofit koji pokazuje na bank-service (port 8083, transakcije/pending) */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BankRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // ── user-service Retrofit (login / auth) ──────────────────────────────────

    @Provides
    @Singleton
    @UserRetrofit
    fun provideUserRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // ── bank-service Retrofit (transakcije, pending akcije) ───────────────────

    @Provides
    @Singleton
    @BankRetrofit
    fun provideBankRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BANK_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideAuthApi(@UserRetrofit retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideTransactionApi(@BankRetrofit retrofit: Retrofit): TransactionApi =
        retrofit.create(TransactionApi::class.java)
}
