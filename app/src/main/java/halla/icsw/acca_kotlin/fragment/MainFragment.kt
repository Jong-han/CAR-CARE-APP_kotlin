package halla.icsw.acca_kotlin.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import halla.icsw.acca_kotlin.MyViewModel
import halla.icsw.acca_kotlin.R
import halla.icsw.acca_kotlin.databinding.FragmentMainBinding

class MainFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentMainBinding
    lateinit var navController: NavController
    val mMyViewModel: MyViewModel by viewModels()
//    val ids = arrayListOf<Int>(R.id.action_mainFragment_to_drivingRecordFragment, R.id.action_mainFragment_to_oilRecordFragment, R.id.action_mainFragment_to_maintenanceFragment)

    companion object {
        var mToast: Toast? = null
    }

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
                } else
                    makeToast("주행이 시작되지 않았습니다.")
            }

            binding.btnDrive -> {
                navController.navigate(R.id.action_mainFragment_to_drivingRecordFragment)
                mMyViewModel.setDriveInfo()
            }
            binding.btnMaintenance -> navController.navigate(R.id.action_mainFragment_to_maintenanceFragment)
            binding.btnOil -> navController.navigate(R.id.action_mainFragment_to_oilRecordFragment)
        }
    }

    fun makeToast(str: String) {
        if (mToast == null) {
            mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT)
        } else
            mToast?.setText(str)
        mToast?.show()
    }
}