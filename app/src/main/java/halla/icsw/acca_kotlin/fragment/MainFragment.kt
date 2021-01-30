package halla.icsw.acca_kotlin.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
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
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    }

    private fun observeStatus(partData: MutableLiveData<PartData>) {
        partData.observe(viewLifecycleOwner, Observer {
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

            binding.btnDrive -> {
                navController.navigate(R.id.action_mainFragment_to_drivingRecordFragment)
                mMyViewModel.setDriveInfo()
            }
            binding.btnMaintenance -> {
                navController.navigate(R.id.action_mainFragment_to_maintenanceFragment)
                mMyViewModel.refreshParts()
            }
            binding.btnOil -> navController.navigate(R.id.action_mainFragment_to_oilRecordFragment)
        }
    }

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

    private fun makeToast(str: String) {
        if (mToast == null) {
            mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT)
        } else
            mToast?.setText(str)
        mToast?.show()
    }

    private fun showNotification() {

        var builder = context?.let { NotificationCompat.Builder(it, CHANNEL_ID) }
                ?.setSmallIcon(R.mipmap.ic_launcher)
                ?.setContentTitle("차량 정비 알림")
                ?.setContentText("교체해야 하는 소모품이 있습니다!")
                ?.setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (builder != null) {
            createNotificationChannel(builder, notificationId)
        }
    }

    private fun createNotificationChannel(
            builder: NotificationCompat.Builder,
            notificationId: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "1번 채널입니다. "
            val importance = NotificationManager.IMPORTANCE_DEFAULT
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
}