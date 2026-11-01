package br.com.composesample.featureA.service

import br.com.composesample.featureA.dto.SomeDTO

interface FeatureAService {

    suspend fun getSomething(someParam: String): List<SomeDTO>

}
