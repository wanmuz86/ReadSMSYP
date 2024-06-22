package com.muzaffar.readsmsyp

import android.app.ListActivity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.CursorAdapter

import com.muzaffar.readsmsyp.databinding.ActivityMainBinding
import org.w3c.dom.Text

class MainActivity : ListActivity() {

    private lateinit var binding : ActivityMainBinding;

    // content here refers to content provider to have access to SMS
    val SMS = Uri.parse("content://sms")

    val PERMISSIONS_REQUEST_READ_SMS = 1 // code permission declared by us for sms

    // SMS will be stored inside a database in Android system
    // We will retrieve the SMSs and shows it inside our List (ListActivity)
    // The db stucture of the SMS is defined as follows

    object SmsColumns {
        val ID = "_id"
        val ADDRESS = "address"
        val DATE = "date"
        val BODY = "body"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)

        // Jikalau permission sudah diberi
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            // Paangil function readSMS
            readSMS()

        } else {
            // Jika permission belum diberi, tanya user adakah anda setuju memberi READ_SMS permission
            ActivityCompat.requestPermissions(this,

                arrayOf(android.Manifest.permission.READ_SMS),

                PERMISSIONS_REQUEST_READ_SMS)

        }

    }

    private fun readSMS(){
        // How to call Content Provider
        val cursor = contentResolver.query(SMS, arrayOf(
            SmsColumns.ID,
            SmsColumns.ADDRESS,
            SmsColumns.DATE,
            SmsColumns.BODY),
            null,
            null,
            SmsColumns.DATE +"  DESC"
        )
        val adapter = SmsCursorAdapter(this, cursor!!, true)
        listAdapter = adapter
    }

    // In X01 we go through about Adapter
    // An adapter is the link between a UI element and the datasource
    // Because some UI element relies on data for it to work:
    // RecyclerView, Spinner, AutoCompleteTextView, ListView
    // In this example, the SMS, retrieved from Content Provider content://sms
    // Will be shown inside a ListView.
    // We will link the data retrieved to the ListView with an Adapter

    private inner class SmsCursorAdapter(context: Context, c: Cursor, autoRequery:Boolean) :
            CursorAdapter(context,c, autoRequery){
                // Nak tunjuk apa // onCreateViewHolder
        override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
            return View.inflate(context, R.layout.custom_row,null)
        }
        // Link kan ui element ke data // onBindViewHolder
        override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
            // Untuk sms_origin TextView, tunjukkan data dari database dalam column Address
            view!!.findViewById<TextView>(R.id.sms_origin).text =
                cursor!!.getString(cursor!!.getColumnIndexOrThrow(SmsColumns.ADDRESS))
            view.findViewById<TextView>(R.id.sms_body).text =
                cursor!!.getString(cursor!!.getColumnIndexOrThrow(SmsColumns.BODY))
            view.findViewById<TextView>(R.id.sms_date).text =
                cursor!!.getString(cursor!!.getColumnIndexOrThrow(SmsColumns.DATE))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){

            // For the case of readSMS
            PERMISSIONS_REQUEST_READ_SMS -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readSMS()

                } else {
                    Toast.makeText(this, "Permission not granted",Toast.LENGTH_LONG ).show()

                }

                return

            }

        }
    }


}