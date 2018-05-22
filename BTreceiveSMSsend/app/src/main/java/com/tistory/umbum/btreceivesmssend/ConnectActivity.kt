package com.tistory.umbum.btreceivesmssend

import android.app.Activity
import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.telephony.SmsManager
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class ConnectActivity : AppCompatActivity() {
    lateinit var mThread : ConnectThread
    lateinit var mTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)


        val device = intent.getParcelableExtra<BluetoothDevice>("device")

        mTextView = findViewById<View>(R.id.textView) as TextView
        mTextView.movementMethod = ScrollingMovementMethod()

        mThread = ConnectThread(device)
        mTextView.text = "try to connect " + device.name + "/" + device.address + "\n"
        mThread.run()

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { _ ->
            mThread.cancel()
            finish()
        }
    }

    override fun onStop() {
        // 이 Activity가 다른 Activity로 가려지든, 뒤로 가든 안보이면 호출됨.
        super.onStop()
        mThread.cancel()
        finish()
    }

    inner class ConnectThread(var mmDevice: BluetoothDevice) : Thread() {
        val mmSocket : BluetoothSocket
        init {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            var tmp : BluetoothSocket? = null

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            // MY_UUID is the app's UUID string, also used by the server code
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(APP_UUID)
            } catch (e : IOException) {
                finishDialog(this@ConnectActivity, "createRfcommSocket failure", e.message + "\n아마 잘못된 장치를 선택한 것 같습니다.")
            }
            mmSocket = tmp!!
        }

        override fun run() {
            super.run()
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect()
            } catch (eConnect : IOException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close()
                } catch (eClose: IOException) {
                    // 닫는 것도 에러.
                } finally {
                    finishDialog(this@ConnectActivity, "Connect failure", eConnect.message)
                    return;
                }
            }

            mTextView.append("Connected!!\n")
            // Do work to manage the connection (in a separate thread)
            val connectedThread = ConnectedThread(mmSocket)
            connectedThread.run()
        }

        /** Will cancel an in-progress connection, and close the socket */
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e : IOException) { }
        }

    }


    inner class ConnectedThread(val mmSocket: BluetoothSocket) : Thread() {
        val mmInStream: InputStream
        val mmOutStream: OutputStream
        var count = 0

        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (e: IOException) {
                finishDialog(this@ConnectActivity, "get stream failure", e.message)
            }
            mmInStream = tmpIn!!
            mmOutStream = tmpOut!!
        }

        override fun run() {
            /* 멈춘다면 여기가 문제. */
            super.run()
            val buffer = ByteArray(1024)
            var bytes: Int     // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer)
                    mTextView.append(buffer.toString() + "\n")

                    // if (조건) sendSMS()

                    // Send the obtained bytes to the UI activity
                    // mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget()

                } catch (e: IOException) {
                    finishDialog(this@ConnectActivity, "Connected Thread run failure", e.message)
                }
            }
        }

        /** Call this from the main activity to send data to the remote device **/
        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes);
            } catch (e: IOException) {
            }
        }

        /** Call this from the main activity to shutdown the connection **/
        fun cancel() {
            try {
                mmSocket.close();
            } catch (e: IOException) {
            }
        }
    }
        fun sendSMS() {
            val SENT = "SMS_SENT"
            val DELIVERED = "SMS_DELIVERED"

            val sentPI = PendingIntent.getBroadcast(this@ConnectActivity, 0, Intent(SENT), 0)
            val deliveredPI = PendingIntent.getBroadcast(this@ConnectActivity, 0, Intent(DELIVERED), 0)

            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (resultCode) {
                        Activity.RESULT_OK ->
                            Toast.makeText(this@ConnectActivity, "송신 완료", Toast.LENGTH_LONG).show()
                        SmsManager.RESULT_ERROR_GENERIC_FAILURE ->
                            Toast.makeText(this@ConnectActivity, "Generic failure", Toast.LENGTH_LONG).show()
                        SmsManager.RESULT_ERROR_NO_SERVICE ->
                            Toast.makeText(this@ConnectActivity, "서비스 지역이 아닙니다.", Toast.LENGTH_LONG).show()
                        SmsManager.RESULT_ERROR_RADIO_OFF ->
                            Toast.makeText(this@ConnectActivity, "무선 통신이 꺼져있습니다.", Toast.LENGTH_LONG).show()
                        else -> { // 그 외 에러
                            Toast.makeText(this@ConnectActivity, "기타 에러", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }, IntentFilter(SENT))

            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (resultCode) {
                        Activity.RESULT_OK ->
                            Toast.makeText(this@ConnectActivity, "SMS가 수신측에 잘 도착했습니다.", Toast.LENGTH_LONG).show()
                        Activity.RESULT_CANCELED ->
                            Toast.makeText(this@ConnectActivity, "SMS가 수신측에 도착하지 못했습니다.", Toast.LENGTH_LONG).show()
                    }
                }
            }, IntentFilter(DELIVERED))

            val smsManager : SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(resources.getString(R.string.dst_phone), null, resources.getString(R.string.sms_text), sentPI, deliveredPI)
        }
}
