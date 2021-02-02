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
import halla.icsw.acca_kotlin.databinding.FragmentUserDistanceBinding

class UserDistanceFragment : Fragment() {
    lateinit var binding: FragmentUserDistanceBinding
    lateinit var navController: NavController
    val mMyViewModel: MyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_distance, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.btnNext.setOnClickListener {
            if (binding.userDataDistance.text.toString() != "") {
                navController.navigate(R.id.action_userDistanceFragment_to_userPartsFragment)
                mMyViewModel.saveUserDistance(binding.userDataDistance.text.toString().toDouble())
            } else
                Toast.makeText(context, "주행한 거리를 입력해주세요!", Toast.LENGTH_SHORT).show()
        }
    }
}