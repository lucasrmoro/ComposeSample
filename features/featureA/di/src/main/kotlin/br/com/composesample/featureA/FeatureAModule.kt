package br.com.composesample.featureA

import br.com.composesample.featureA.analytics.FeatureAAnalytics
import br.com.composesample.featureA.analytics.createFeatureAAnalytics
import br.com.composesample.featureA.dataSource.FeatureALocalDataSource
import br.com.composesample.featureA.dataSource.FeatureALocalDataSourceImpl
import br.com.composesample.featureA.dataSource.FeatureARemoteDataSource
import br.com.composesample.featureA.dataSource.FeatureARemoteDataSourceImpl
import br.com.composesample.featureA.mapper.SomeMapper
import br.com.composesample.featureA.mapper.SomeMapperImpl
import br.com.composesample.featureA.navigation.createFeatureAEntryPoint
import br.com.composesample.featureA.repository.FeatureARepository
import br.com.composesample.featureA.repository.FeatureARepositoryImpl
import br.com.composesample.featureA.service.FeatureAService
import br.com.composesample.featureA.service.FeatureAServiceImpl
import br.com.composesample.featureA.useCase.GetSomethingUseCase
import br.com.composesample.featureA.useCase.GetSomethingUseCaseImpl
import br.com.composesample.featureA.form.viewModel.FormViewModel
import br.com.composesample.featureA.list.viewModel.ListViewModel
import br.com.composesample.structural.core.ext.dispatcherIO
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.factory
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.di.singleton
import br.com.composesample.structural.navigation.core.NavigationRegistry
import br.com.composesample.structural.network.ext.httpClient

object FeatureAModule {

    private val dataModule = dependencyModule {
        singleton<FeatureAAnalytics> { createFeatureAAnalytics(provider = get()) }
        // Remote
        singleton<FeatureAService> { FeatureAServiceImpl(httpClient = httpClient) }
        singleton<FeatureARemoteDataSource> { FeatureARemoteDataSourceImpl(featureAService = get()) }

        // Local
        singleton<FeatureALocalDataSource> {
            FeatureALocalDataSourceImpl(someDao = get(), someMapper = get())
        }

        // Mapper
        factory<SomeMapper> { SomeMapperImpl() }

        // Repository
        singleton<FeatureARepository> {
            FeatureARepositoryImpl(
                dispatcher = dispatcherIO,
                featureARemoteDataSource = get(),
                featureALocalDataSource = get()
            )
        }
    }

    private val domainModule = dependencyModule {
        factory<GetSomethingUseCase> { GetSomethingUseCaseImpl(featureARepository = get()) }
    }

    private val presentationModule = dependencyModule {
        factory { FormViewModel() }
        factory { ListViewModel(analytics = get()) }
    }

    fun init(navigationRegistry: NavigationRegistry) {
        loadModules(
            dataModule,
            domainModule,
            presentationModule
        )
        navigationRegistry.register(
            entryPoint = createFeatureAEntryPoint(),
            isStartDestination = true
        )
    }

}
