package halla.icsw.acca_kotlin.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import halla.icsw.acca_kotlin.MyViewModel
import halla.icsw.acca_kotlin.R
import halla.icsw.acca_kotlin.databinding.FragmentMaintenanceBinding

class MaintenanceFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentMaintenanceBinding
    lateinit var navController: NavController
    val mMyViewModel: MyViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maintenance, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.lifecycleOwner = this
        binding.viewModel = mMyViewModel
        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }
        binding.btnChangeAutomissionOil.setOnClickListener(this)
        binding.btnChangeBrakeOil.setOnClickListener(this)
        binding.btnChangeBrakePad.setOnClickListener(this)
        binding.btnChangeEngineOil.setOnClickListener(this)
        binding.btnChangePowerOil.setOnClickListener(this)
        binding.btnChangeTimingBelt.setOnClickListener(this)

//        mMyViewModel.engineOil.observe(viewLifecycleOwner, Observer {
//            var str = it.remainingDistance.toString()
//            binding.alertEngineOil.text = str
//        })
//
//        mMyViewModel.engineOilColor.observe(viewLifecycleOwner, Observer {
//            binding.alertEngineOil.
//        })

    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnChangeAutomissionOil -> {
                mMyViewModel.calculateCycle("AutoOil")
            }
            binding.btnChangeBrakeOil -> {
                mMyViewModel.calculateCycle("BrakeOil")
            }
            binding.btnChangeBrakePad -> {
                mMyViewModel.calculateCycle("BrakePad")
            }
            binding.btnChangeEngineOil -> {
                mMyViewModel.calculateCycle("EngineOil")
            }
            binding.btnChangePowerOil -> {
                mMyViewModel.calculateCycle("PowerOil")
            }
            binding.btnChangeTimingBelt -> {
                mMyViewModel.calculateCycle("TimingBelt")
            }
        }
    }

}