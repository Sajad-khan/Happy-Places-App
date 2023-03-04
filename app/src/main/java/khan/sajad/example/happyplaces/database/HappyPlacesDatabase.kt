package khan.sajad.example.happyplaces.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.InternalCoroutinesApi

@Database(entities = [HappyPlaceEntity::class], version = 1)
abstract class HappyPlacesDatabase: RoomDatabase() {
    abstract fun happyPlaceDao(): HappyPlaceDao

    companion object{
        @Volatile
        private var INSTANCE: HappyPlacesDatabase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(context: Context): HappyPlacesDatabase{
            kotlinx.coroutines.internal.synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HappyPlacesDatabase::class.java,
                        "happy_places_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}