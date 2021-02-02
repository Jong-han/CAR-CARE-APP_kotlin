package halla.icsw.acca_kotlin.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import halla.icsw.acca_kotlin.MyViewModel
import halla.icsw.acca_kotlin.R
import halla.icsw.acca_kotlin.databinding.FragmentUserDistanceBinding
import halla.icsw.acca_kotlin.databinding.FragmentUserOilKindBinding

class UserOilKindFragment : Fragment() {

    lateinit var binding: FragmentUserOilKindBinding
    lateinit var navController: NavController
    val mMyViewModel: MyViewModel by viewModels()

    private lateinit var callback: OnBackPressedCallback
    var mBackWait: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_oil_kind, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.btnNext.setOnClickListener {
            if (binding.gasoline.isChecked || binding.diesel.isChecked) {
                navController.navigate(R.id.action_userOilKindFragment_to_userDistanceFragment)
                if(binding.gasoline.isChecked)
                    mMyViewModel.saveUserOilKind("B027")
                else
                    mMyViewModel.saveUserOilKind("D047")
            }
            else
                Toast.makeText(context,"유종을 체크해 주세요!",Toast.LENGTH_SHORT).show()
        }
    }

    //*********** BACK PRESS EVENT ***********//
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - mBackWait >= 2000) {
                    mBackWait = System.currentTimeMillis()
                    Toast.makeText(context,"뒤로가기를 한 번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show()
                } else {
                    activity?.finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
    //*********** BACK PRESS EVENT ***********//
}