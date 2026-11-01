package br.com.composesample.featureA.mapper

import br.com.composesample.featureA.dto.SomeDTO
import br.com.composesample.featureA.entity.SomeDbEntity

interface SomeMapper {

    fun toSomeDTOList(someDbEntityList: List<SomeDbEntity>): List<SomeDTO>

}