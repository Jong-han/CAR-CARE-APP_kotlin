package halla.icsw.acca_kotlin.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import halla.icsw.acca_kotlin.Maintenance.Cycle
import halla.icsw.acca_kotlin.Maintenance.PartData
import halla.icsw.acca_kotlin.MyViewModel
import halla.icsw.acca_kotlin.R
import halla.icsw.acca_kotlin.databinding.FragmentMaintenanceBinding

class MaintenanceFragment : Fragment(){

    lateinit var binding: FragmentMaintenanceBinding
    lateinit var navController: NavController
    val mMyViewModel: MyViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
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

        setBtnEvent()

    }

    private fun btnClickEvent(view: View, partName: String, partData: MutableLiveData<PartData>, partCycle: Cycle) {
        view.setOnClickListener {
            mMyViewModel.calculateCycle(partData, partCycle, partName)
        }
    }

    private fun setBtnEvent() {
        btnClickEvent(binding.btnChangeBattery, "Battery", mMyViewModel.battery, mMyViewModel.batteryCycle)
        btnClickEvent(binding.btnChangeAirConditioner, "AirConditioner", mMyViewModel.airConditioner, mMyViewModel.airConditionerCycle)
        btnClickEvent(binding.btnChangeEngineOil, "EngineOil", mMyViewModel.engineOil, mMyViewModel.engineOilCycle)
        btnClickEvent(binding.btnChangeBrakePad, "BrakePad", mMyViewModel.brakePad, mMyViewModel.brakePadCycle)
        btnClickEvent(binding.btnChangeBrakeOil, "BrakeOil", mMyViewModel.brakeOil, mMyViewModel.brakeOilCycle)
        btnClickEvent(binding.btnChangeTire, "Tire", mMyViewModel.tire, mMyViewModel.tireCycle)
    }


}