package company.vk.myapplication.ui.main

import androidx.paging.PagingSource
import androidx.paging.PagingState
import company.vk.myapplication.R
import company.vk.myapplication.datalayer.IAccessor
import company.vk.myapplication.objects.Cat
import okio.IOException
import retrofit2.HttpException

class CatPagingSource(
    private val service:IAccessor,
    private val query:String
) : PagingSource<Int, Cat>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cat> {
        //for first case it will be null, then we can pass some default value, in our case it's 1
        val page = params.key ?: R.integer.Base_Page
        return try {
            val response = service.getCats(page,params.loadSize)
            LoadResult.Page(
                response, prevKey = if (page == R.integer.Base_Page) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Cat>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}