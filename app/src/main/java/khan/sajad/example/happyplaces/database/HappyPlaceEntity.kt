package khan.sajad.example.happyplaces.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "happy_places_table")
data class HappyPlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val imageLocation: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: Double?,
    val longitude: Double?
) : java.io.Serializable