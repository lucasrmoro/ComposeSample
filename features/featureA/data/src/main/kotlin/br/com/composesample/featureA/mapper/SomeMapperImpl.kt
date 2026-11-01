package br.com.composesample.featureA.mapper

import br.com.composesample.featureA.dto.SomeDTO
import br.com.composesample.featureA.entity.SomeDbEntity

class SomeMapperImpl : SomeMapper {

    override fun toSomeDTOList(someDbEntityList: List<SomeDbEntity>) =
        someDbEntityList.map { SomeDTO(id = it.id, text = it.text) }

}