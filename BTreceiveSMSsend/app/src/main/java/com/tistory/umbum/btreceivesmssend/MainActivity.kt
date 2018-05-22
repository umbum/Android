package com.tistory.umbum.btreceivesmssend

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import java.util.*
import java.util.UUID.fromString

const val REQUEST_ENABLE_BT = 5
lateinit var APP_UUID : UUID

fun finishDialog(activity : AppCompatActivity, title : String?, message : String?) {
    AlertDialog.Builder(activity).setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("종료", { _, _ ->
                activity.finish()
            }).show()
}

class MainActivity : AppCompatActivity() {
    val mBluetoothAdapter : BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    lateinit var mArrayAdapter : ArrayAdapter<String>
    val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.getAction() as String
            // 수신한 intent action이 ACTION_FOUND이면,
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // intent로부터 BluetoothDevice 객체 가져오기.
                val device : BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                mArrayAdapter.add(device.name + "\n" + device.address)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        mArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayList<String>())
        assignUUID()
        btClientMain()
    }

    fun assignUUID() {
        try {
            // Arduino용 UUID. Server측과 Client측이 같은 UUID를 가져야 Connect된다.
            APP_UUID = fromString(resources.getString(R.string.app_uuid))
        } catch (e : IllegalArgumentException) {
            finishDialog(this, "UUID 에러", "개발자가 설정한 UUID가 잘못되었습니다. 개발자에게 알려주세요.")
        }
    }

    fun btClientMain() {
        checkBtIsRunning()
        getPairedDevices()
//        findBtDevices()     /* 1. 반드시 잊지 말고 onDestroy에서 unregister해줄 것 2. connect 호출 전 cancelDiscovery()해줄 것.*/

        val listView = findViewById(R.id.list_view) as ListView
        listView.adapter = mArrayAdapter
        listView.setOnItemClickListener { parent, v, position, id ->
            val strText = parent.getItemAtPosition(position) as String
            val selectedDevice = mBluetoothAdapter!!.bondedDevices.find { it.address == strText.substringAfterLast("\n") }
            if (selectedDevice == null) {
                Snackbar.make(v, "선택된 장치가 현재 페어링 목록에 없습니다. 방금 전에 페어링이 해제된 경우 이런 현상이 발생할 수 있습니다"
                        , Snackbar.LENGTH_LONG).setAction("Action", null).show()
            }
            else {
                // Cancel discovery because it will slow down the connection
                /* cancelDiscovery()는 반드시 BLUETOOTH_ADMIN 권한이 있어야한다. 없는 상태에서 호출하면 강제 종료된다. */
                mBluetoothAdapter.cancelDiscovery()
                val intent = Intent(this, ConnectActivity::class.java)
                intent.putExtra("device", selectedDevice)
                startActivity(intent)
            }
        }
    }


    fun checkBtIsRunning() {
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            finish()
        } else {
            mBluetoothAdapter.takeIf { !it.isEnabled() }?.run {
                startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT)
            }
        }
    }

    fun getPairedDevices() {
        val pairedDevices : Set<BluetoothDevice> = mBluetoothAdapter!!.bondedDevices
        if (pairedDevices.size > 0) {
            for (device in pairedDevices) {
                mArrayAdapter.add(device.name + "\n" + device.address)
                // mArrayAdapter에 추가하고, 추후 ListView의 Adapter를 mArrayAdapter로 지정해 이를 띄워준다.
            }
        }
    }

    fun findBtDevices() {
        // ACTION_FOUND Broadcast를 수신하는 BroadcastReceiver를 생성.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 블루투스 기능 활성화 요청
        when (resultCode) {
            RESULT_OK -> {
                btClientMain() // 다시 첨부터 실행.
            }
            RESULT_CANCELED -> {
                // 사용자가 No를 선택했거나 오류때문에 블루투스 활성화하지 못한 경우.
                finish()
            }
        }
    }
}

