package com.example.umbum.sqliteankoexp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
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
    fun onClickImage(v: View?) {
        // 6.0 이상이면 권한이 있는지 체크.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {
            // 사용자에게 권한에 대한 설명이 필요한 경우인지를 체크. ( 이전에 거절했거나... )
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "퍼미션 거절했으므로 external storage에 접근 불가!", Toast.LENGTH_SHORT).show()
                /** 여기서 그냥 제한된 기능으로 계속 진행할거면 startActivity.(MediaStore에는 접근 가능하지만 external storage 리소스에는 접근할 수 없음.)
                다시 사용자한테 권한 요청할거면 requestPermissions.*/
                startActivityForResult(
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                        PICK_IMAGE)
//                ActivityCompat.requestPermissions(this,
//                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQ_PERMISSION)
            }
            else {
                /** 권한 요청 팝업을 띄운다. 이후 팝업 종료 시 onReqeustPermissionsResult가 호출.
                단, 다시 보지 않기를 체크하고 거절한 경우 팝업이 뜨지 않고 자동으로 거절로 처리된다.*/
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQ_PERMISSION)
            }
        }
        else {
            // 암시적 인텐트.
            startActivityForResult(
                    Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                    PICK_IMAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 거절일 때.
                    AlertDialog.Builder(this).setTitle("권한 설정")
                            .setMessage("거절하면 external storage 접근 못함 ㅅㄱ\n 설정하는걸 추천")
                            .setCancelable(true)
                            .setPositiveButton("설정") {
                                _, _ -> showSetting()
                            }
                            .create().show()
                }
            }
        }
    }

    fun showSetting() {
        startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
                        .apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
    }

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
