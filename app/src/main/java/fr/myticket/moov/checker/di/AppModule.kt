package fr.myticket.moov.checker.di

import android.content.Context
import com.ajicreative.dtc.utils.JsonResponses
import fr.myticket.moov.checker.utils.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.myticket.moov.checker.repository.ApiManager
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(Logging)
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }

    @Provides
    @Singleton
    fun provideJsonResponses(@ApplicationContext context: Context): JsonResponses {
        return JsonResponses(context)
    }

    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext appContext: Context): Preferences {
        return Preferences(appContext)
    }

    @Provides
    @Singleton
    fun providesClientApiManager(client: HttpClient, jsonResponses: JsonResponses, preference: Preferences, @ApplicationContext appContext: Context): ApiManager {
        return ApiManager(client, jsonResponses, preference, appContext)
    }





}