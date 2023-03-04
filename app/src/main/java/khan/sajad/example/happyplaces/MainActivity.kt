package khan.sajad.example.happyplaces

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import khan.sajad.example.happyplaces.adapter.ItemAdapter
import khan.sajad.example.happyplaces.database.HappyPlaceDao
import khan.sajad.example.happyplaces.database.HappyPlaceEntity
import khan.sajad.example.happyplaces.databinding.ActivityMainBinding
import khan.sajad.example.happyplaces.databinding.LongClickDialogBinding
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val dao = (application as HappyPlacesApp).database.happyPlaceDao()
        binding.fAddButton.setOnClickListener {
            val intent = Intent(this, AddHappyPlacesActivity::class.java)
            startActivity(intent)
        }
        getDataFromDatabase(dao)
    }

    private fun getDataFromDatabase(dao: HappyPlaceDao) {
        lifecycleScope.launch {
            dao.fetchAllPlaces().collect {
                val list = ArrayList(it)
                setUpDataInRecyclerview(list, dao)
            }
        }
    }

    private fun setUpDataInRecyclerview(list: ArrayList<HappyPlaceEntity>, dao: HappyPlaceDao) {
        val adapter = ItemAdapter(list)
        binding.rvItemsList.adapter = adapter
        adapter.setOnLongClickListener(object: ItemAdapter.OnLongClickListener{
            override fun onLongClick(position: Int, model: HappyPlaceEntity): Boolean {
                onLongClickDialog(dao, model)
                return true
            }
        })
    }

    private fun onLongClickDialog(dao: HappyPlaceDao, place: HappyPlaceEntity){
        val customDialog = Dialog(this)
        val dialogBinding = LongClickDialogBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnEdit.text = "Edit ${place.title}"
        dialogBinding.btnDelete.text = "Delete ${place.title}"
        dialogBinding.btnCancel.setOnClickListener {
            customDialog.dismiss()
        }
        dialogBinding.btnEdit.setOnClickListener {
            val intent = Intent(this@MainActivity, AddHappyPlacesActivity::class.java)
            intent.putExtra(HAPPY_PLACE, place)
            startActivity(intent)
            customDialog.dismiss()
        }
        dialogBinding.btnDelete.setOnClickListener {
            lifecycleScope.launch {
                dao.delete(place)
                val file = File(place.imageLocation)
                if(file.delete()){
                    Log.e("DELETED", "Image deleted")
                }
                else Log.e("NOT_DELETED", "Image not found")
            }
            customDialog.dismiss()
        }
        customDialog.show()
    }
    companion object{
        const val HAPPY_PLACE = "happy_place"
    }
}


