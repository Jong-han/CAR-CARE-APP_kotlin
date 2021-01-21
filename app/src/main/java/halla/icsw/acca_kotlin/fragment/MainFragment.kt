package halla.icsw.acca_kotlin.fragment

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import halla.icsw.acca_kotlin.R
import halla.icsw.acca_kotlin.databinding.FragmentDrivingRecordBinding
import halla.icsw.acca_kotlin.databinding.FragmentMainBinding

class MainFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentMainBinding
    lateinit var navController: NavController

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


        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager


    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnStart -> {
                makeToast("시작")
            }
            binding.btnEnd -> {
                makeToast("종료")
            }
        }
    }

    fun makeToast(str: String) {
        if (mToast == null) {
            mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT)
        }else
            mToast?.setText(str)
        mToast?.show()
    }
}