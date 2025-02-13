package limited.m.remembermywallet.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import limited.m.remembermywallet.data.QuizRepository
import limited.m.remembermywallet.data.SeedPhraseRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSecureStorage(@ApplicationContext context: Context): SeedPhraseRepository {
        return SeedPhraseRepository(context)
    }


    @Provides
    fun provideQuizRepository(@ApplicationContext context: Context): QuizRepository {
        return QuizRepository(context)
    }
}
