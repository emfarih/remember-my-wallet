package limited.m.remembermywallet.di

import limited.m.remembermywallet.data.QuizRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QuizModule {

    @Provides
    @Singleton
    fun provideQuizRepository(): QuizRepository {
        return QuizRepository()
    }
}
