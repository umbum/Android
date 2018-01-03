package com.example.umbum.sqliteankoexp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.umbum.sqliteankoexp.DB.DBHandler
import com.example.umbum.sqliteankoexp.DB.UserInfo
import kotlinx.android.synthetic.main.activity_add_user.*



class AddUserActivity: AppCompatActivity() {
    companion object {
        const val PICK_IMAGE = 1010
        const val REQ_PERMISSION = 1011
    }
    val mDBHandler = DBHandler(this)
    var mSelectedImgId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickImage(view: View?) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        }
        else {
            // 암시적 인텐트
            startActivityForResult(
                    Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                    PICK_IMAGE)
        }
    }

    private fun requestPermissions() {
        /** 사용자에게 권한에 대한 설명이 필요한 경우인지를 체크.
         * 한 번 거절하게 되면 권한에 대한 설명이 필요한 경우가 되어 Snackbar 쪽으로 들어가는데,
         * 거기서 또 다시 보지 않기를 체크하고 거절한 경우 이후에는 알아서 거절한 것으로 간주됨. */
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            /* Toast는 ActivityComapt.requestPermissions이 뜨면서 사라져버려서, Snackbar로 처리.
            * MediaStore Activity에 Snackbar를 띄우면 좋을 것 같지만, View를 얻을 수 없어 불가능한 듯. */
            Snackbar.make(findViewById(android.R.id.content), "거절하면 external storage에는 접근할 수 없음. ㅇㅋ?", Snackbar.LENGTH_LONG)
                    .setAction("SET", { _ ->
                        ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQ_PERMISSION)
                    })
                    .show()
        }
        else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQ_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 거절일 때. 제한된 권한으로 그냥 진행.
                    Toast.makeText(this, "external storage에는 접근할 수 없음.", Toast.LENGTH_LONG).show()
                    startActivityForResult(
                            Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                            PICK_IMAGE)
                }
            }
        }
    }

    /* 직접 SettingDialog를 띄워 유도하는 방식인데, 반드시 필요한 권한이 아니라면 굳이 안써도 될 것 같음.
    private fun showSettingDialog() {
        AlertDialog.Builder(this)
                .setTitle("권한 설정")
                .setMessage("거절하면 external storage에 접근 못함 ㅅㄱ\n설정? 그냥 진행?")
                .setPositiveButton("SETTING", { _, _ ->
                    startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
                                    .apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    })
                })
                .setNegativeButton("IGNORE", { _, _->
                    startActivityForResult(
                            Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                            PICK_IMAGE)
                })
                .setCancelable(true)
                .create().show()
    }
    */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE -> {
                val uri = data?.getData() ?: return

                mSelectedImgId = getImageID(uri)
                if (mSelectedImgId == -1L) return
                val thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                        contentResolver, mSelectedImgId, MediaStore.Images.Thumbnails.MICRO_KIND, null)
                val sel_image = findViewById(R.id.sel_image) as ImageView
                sel_image.setImageBitmap(thumbnail)
            }
        }
    }

    fun getImageID(uri: Uri): Long {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        val column_index = cursor.getColumnIndex(MediaStore.Images.Media._ID)

        if (column_index == -1) return -1

        cursor.moveToFirst()
        val id = cursor.getLong(column_index)
        cursor.close()
        return id
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickSaveBtn(v: View) {
        // kotlinx
        mDBHandler.addUser(
                UserInfo(
                        edit_name.text.toString(),
                        edit_age.text.toString(),
                        edit_tel.text.toString(),
                        mSelectedImgId.toString())
        )
        mDBHandler.close()
        finish()
    }
}
