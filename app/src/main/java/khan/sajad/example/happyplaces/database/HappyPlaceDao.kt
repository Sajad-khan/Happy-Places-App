package khan.sajad.example.happyplaces.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HappyPlaceDao {
    @Insert
    suspend fun insert(happyPlaceEntity: HappyPlaceEntity)

    @Query(/* value = */ "select * from `happy_places_table`")
    fun fetchAllPlaces(): Flow<List<HappyPlaceEntity>>

    @Delete
    suspend fun delete(happyPlaceEntity: HappyPlaceEntity)

    @Query("select * from `happy_places_table` where id=:id")
    fun fetchPlaceById(id: Int): Flow<HappyPlaceEntity>
}