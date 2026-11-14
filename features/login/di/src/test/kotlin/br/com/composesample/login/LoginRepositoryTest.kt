package br.com.composesample.login

import br.com.composesample.login.analytics.LoginAnalytics
import br.com.composesample.login.repository.LoginRepository
import br.com.composesample.structural.analytics.AnalyticsResult
import br.com.composesample.structural.core.di.CoreModule
import br.com.composesample.structural.di.Dependencies
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.di.startDependencyInjection
import br.com.composesample.structural.di.stopDependencyInjection
import br.com.composesample.structural.network.di.NetworkModule
import br.com.composesample.structural.network.http.HttpClientConfiguration
import br.com.composesample.structural.network.exception.HttpException
import br.com.composesample.structural.localization.AppRegion
import br.com.composesample.structural.localization.AppRegionProvider
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class LoginRepositoryTest {

    private val server = MockWebServer()

    @Before
    fun setUp() {
        server.start()
        startDependencyInjection {}
        CoreModule.init()
        NetworkModule.init()
        LoginModule.init()

        loadModules(
            dependencyModule {
                singleton { HttpClientConfiguration(baseUrl = server.url("/").toString()) }
                singleton<AppRegionProvider> {
                    object : AppRegionProvider {

                        override val region = MutableStateFlow(
                            AppRegion(languageTag = "en", countryCode = "US", timeZoneId = "UTC")
                        )

                        override fun start() = Unit

                    }
                }
                singleton<LoginAnalytics> {
                    object : LoginAnalytics {

                        override fun tokenRefreshStarted() = AnalyticsResult.UNAVAILABLE

                        override fun tokenRefreshFinished(succeeded: Boolean) = AnalyticsResult.UNAVAILABLE

                    }
                }
            }
        )
    }

    @After
    fun tearDown() {
        stopDependencyInjection()
        server.shutdown()
    }

    @Test
    fun refreshTokenUsesLoginServiceWithoutAuthorization() = runBlocking {
        server.enqueue(MockResponse().setBody(
            """{"AccessToken":"new-access-token","RefreshToken":"new-refresh-token"}"""
        ))

        val accessToken = Dependencies.get<LoginRepository>().refreshToken("refresh-token").first()

        assertEquals("new-access-token", accessToken)
        val request = server.takeRequest(1, TimeUnit.SECONDS)
        assertEquals("/auth/refresh-token/refresh-token?language=en", request?.path)
        assertNull(request?.getHeader("Authorization"))
        assertEquals(1, server.requestCount)
    }

    @Test
    fun rejectedRefreshDoesNotRetryAuthentication() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(401))

        val error = runCatching {
            Dependencies.get<LoginRepository>().refreshToken("expired-refresh-token").first()
        }.exceptionOrNull()

        assertEquals(401, (error as? HttpException)?.statusCode)
        assertEquals(1, server.requestCount)
    }

}
