package khan.sajad.example.happyplaces

import android.app.Application
import khan.sajad.example.happyplaces.database.HappyPlacesDatabase

class HappyPlacesApp: Application() {
    val database by lazy{
        HappyPlacesDatabase.getInstance(this)
    }
}