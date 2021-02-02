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
import halla.icsw.acca_kotlin.databinding.FragmentUserDistanceBinding
import halla.icsw.acca_kotlin.databinding.FragmentUserPartsBinding


class UserPartsFragment : Fragment() {
    lateinit var binding: FragmentUserPartsBinding
    lateinit var navController: NavController
    val mMyViewModel: MyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_parts, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.btnFinish.setOnClickListener {
            if (binding.userDataAutoOil.text.toString() != "" &&
                binding.userDataBrakeOil.text.toString() != "" &&
                binding.userDataBrakePad.text.toString() != "" &&
                binding.userDataEngineOil.text.toString() != "" &&
                binding.userDataPowerOil.text.toString() != "" &&
                binding.userDataTimingBelt.text.toString() != ""
            ) {
                mMyViewModel.saveUserPartsData("AutoOil",binding.userDataAutoOil.text.toString().toDouble())
                mMyViewModel.saveUserPartsData("BrakeOil",binding.userDataBrakeOil.text.toString().toDouble())
                mMyViewModel.saveUserPartsData("BrakePad",binding.userDataBrakePad.text.toString().toDouble())
                mMyViewModel.saveUserPartsData("EngineOil",binding.userDataEngineOil.text.toString().toDouble())
                mMyViewModel.saveUserPartsData("PowerOil",binding.userDataPowerOil.text.toString().toDouble())
                mMyViewModel.saveUserPartsData("TimingBelt",binding.userDataTimingBelt.text.toString().toDouble())
                mMyViewModel.saveUserData()
                navController.navigate(R.id.action_userPartsFragment_to_mainFragment)
            } else
                Toast.makeText(context, "교체 시기를 모두 입력해주세요!", Toast.LENGTH_SHORT).show()
        }
    }
}