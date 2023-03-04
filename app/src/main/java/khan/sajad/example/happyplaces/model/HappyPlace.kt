package khan.sajad.example.happyplaces.model

data class HappyPlace(
    val id: Int,
    val title: String,
    var imageLocation: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
)