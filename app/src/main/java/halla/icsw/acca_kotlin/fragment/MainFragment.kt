package halla.icsw.acca_kotlin.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import halla.icsw.acca_kotlin.R
import halla.icsw.acca_kotlin.databinding.FragmentDrivingRecordBinding
import halla.icsw.acca_kotlin.databinding.FragmentMainBinding

class MainFragment : Fragment(), View.OnClickListener{
    lateinit var binding: FragmentMainBinding
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.btnDrive.setOnClickListener(this)
        binding.btnMaintenance.setOnClickListener(this)
        binding.btnOil.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view){
            binding.btnDrive -> navController.navigate(R.id.action_mainFragment_to_drivingRecordFragment)
            binding.btnOil -> navController.navigate(R.id.action_mainFragment_to_oilRecordFragment)
            binding.btnMaintenance -> navController.navigate(R.id.action_mainFragment_to_maintenanceFragment)
        }
    }
}