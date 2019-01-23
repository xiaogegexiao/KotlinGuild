package au.com.foxtask.coroutineexperimental

import au.com.foxtask.coroutineexperimental.interactors.UseCase
import au.com.foxtask.coroutineexperimental.models.CatalogueEntity

class GetCatalogueCase(private val catalogueRepository: CatalogueRepository) : UseCase<CatalogueEntity, GetCatalogueCase.GetCatalogueParams>() {
    override suspend fun run(params: GetCatalogueParams): Either<Failure, CatalogueEntity> {
        return catalogueRepository.getCatalogue(params.storeId, params.saleId)
    }

    data class GetCatalogueParams(
            val storeId: String,
            val saleId: String)
}