package halla.icsw.acca_kotlin.fragment

import android.Manifest
import android.Manifest.*
import android.Manifest.permission.*
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ReportFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import halla.icsw.acca_kotlin.DB.Repository
import halla.icsw.acca_kotlin.MainActivity
import halla.icsw.acca_kotlin.Maintenance.PartData
import halla.icsw.acca_kotlin.MyViewModel
import halla.icsw.acca_kotlin.R
import halla.icsw.acca_kotlin.databinding.FragmentMainBinding

class MainFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentMainBinding
    lateinit var navController: NavController
    val mMyViewModel: MyViewModel by viewModels()

    var notificationId = 1000
    val CHANNEL_ID = "Maintenance_noti"
    val channel_name = "ACCA"
    var isFirst = true

    private lateinit var callback: OnBackPressedCallback
    var mBackWait: Long = 0

    var mToast: Toast? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //*********** PERMISSION CHECK ***********//
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() { // 권한 허가시 실행 할 내용
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(context, "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        TedPermission.with(context)
            .setPermissionListener(permissionlistener)
            .setRationaleMessage("앱을 이용하기 위해서는 접근 권한이 필요합니다")
            .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
            .setPermissions(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION,
                ACCESS_BACKGROUND_LOCATION,
                RECEIVE_SMS,
                READ_SMS
            ).check()
        //*********** PERMISSION CHECK ***********//

        navController = Navigation.findNavController(view)
        binding.btnDrive.setOnClickListener(this)
        binding.btnMaintenance.setOnClickListener(this)
        binding.btnOil.setOnClickListener(this)
        binding.btnStart.setOnClickListener(this)
        binding.btnEnd.setOnClickListener(this)

        binding.lifecycleOwner = this
        binding.viewModel = mMyViewModel

        observeStatus(mMyViewModel.autoOil)
        observeStatus(mMyViewModel.brakeOil)
        observeStatus(mMyViewModel.brakePad)
        observeStatus(mMyViewModel.engineOil)
        observeStatus(mMyViewModel.powerOil)
        observeStatus(mMyViewModel.timingBelt)

        //*********** CHECKBOX ***********//

        binding.checkboxEfficiency.isEnabled =
            !Repository.mMySharedPreferences.getIsChecked("Check", false)
        mMyViewModel.isStartCheck.observe(viewLifecycleOwner, {
            binding.checkboxEfficiency.isChecked = it
        })


        binding.checkboxEfficiency.setOnClickListener {
            if (binding.checkboxEfficiency.isChecked) {
                makeDialog(
                    "연비 측정 시작",
                    "\n연료가 적게 남은 상태일수록 정확도가 높아집니다.\n\n연비 측정이 시작되면 측정을 종료할 수 없습니다.",
                    binding.checkboxEfficiency
                )
            } else
                makeDialog(
                    "연비 측정 종료", "\n연비 측정 중!\n\n주의 : 연비에 대한 데이터가 모두 삭제됩니다",
                    binding.checkboxEfficiency
                )
        }

    }

    private fun observeStatus(partData: MutableLiveData<PartData>) {
        partData.observe(viewLifecycleOwner, {
            if (it.status == 0 && isFirst) {
                showNotification()
                isFirst = false
            }
        })
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnStart -> {
                makeToast("주행을 시작합니다.")
                mMyViewModel.startDrive()
            }
            binding.btnEnd -> {
                if (mMyViewModel.isStart) {
                    makeToast("주행을 종료합니다.")
                    mMyViewModel.endDrive()
                    isFirst = true
                } else
                    makeToast("주행이 시작되지 않았습니다.")
            }

            binding.btnDrive ->
                navController.navigate(R.id.action_mainFragment_to_drivingRecordFragment)
            binding.btnMaintenance -> {
                navController.navigate(R.id.action_mainFragment_to_maintenanceFragment)
                mMyViewModel.refreshParts()
            }
            binding.btnOil -> {
                navController.navigate(R.id.action_mainFragment_to_oilRecordFragment)
                mMyViewModel.removeLiter()
            }
        }
    }

    //*********** BACK PRESS EVENT ***********//
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - mBackWait >= 2000) {
                    mBackWait = System.currentTimeMillis()
                    makeToast("뒤로가기를 한 번 더 누르면 종료됩니다.")
                } else {
                    activity?.finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
    //*********** BACK PRESS EVENT ***********//

    private fun makeToast(str: String) {
        if (mToast == null) {
            mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT)
        } else
            mToast?.setText(str)
        mToast?.show()
    }


    //*********** NOTIFICATION ***********//
    private fun showNotification() {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = context?.let { NotificationCompat.Builder(it, CHANNEL_ID) }
            ?.setSmallIcon(R.mipmap.ic_launcher)
            ?.setContentTitle("차량 정비 알림")
            ?.setContentText("교체해야 하는 소모품이 있습니다!")
            ?.setAutoCancel(true)
            ?.setPriority(NotificationCompat.PRIORITY_HIGH)
            ?.setFullScreenIntent(pendingIntent, true)
        if (builder != null) {
            createNotificationChannel(builder, notificationId)
        }
    }

    private fun createNotificationChannel(
        builder: NotificationCompat.Builder,
        notificationId: Int,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "1번 채널입니다. "
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, channel_name, importance).apply {
                description = descriptionText
            }

            channel.lightColor = Color.BLUE
            channel.enableVibration(true)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                activity?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            notificationManager.notify(notificationId, builder.build())

        } else {
            val notificationManager: NotificationManager =
                activity?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, builder.build())
        }
    }
    //*********** NOTIFICATION ***********//

    fun makeDialog(title: String, message: String, view: CheckBox) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
            .setPositiveButton("입력") { dialogInterface, i ->
                if (view.isChecked == false) {
                } else {
                    mMyViewModel.saveIsChecked(view.isChecked)
                    binding.checkboxEfficiency.isEnabled = false
                }
            }
            .setNegativeButton("취소") { dialogInterface, i ->
                view.isChecked = !view.isChecked
            }
            .show()
    }
}