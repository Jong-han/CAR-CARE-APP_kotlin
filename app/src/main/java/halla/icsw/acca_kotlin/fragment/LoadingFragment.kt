package halla.icsw.acca_kotlin.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import halla.icsw.acca_kotlin.MyViewModel
import halla.icsw.acca_kotlin.R
import kotlin.concurrent.timer

class LoadingFragment : Fragment() {
    lateinit var navController: NavController
    val mMyViewModel: MyViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        loading()

    }
    fun loading(){
        val handler = Handler(Looper.getMainLooper())
        mMyViewModel.isExistUserData.observe(viewLifecycleOwner, {
            if(it)
                handler.postDelayed({navController.navigate(R.id.action_loadingFragment_to_mainFragment)}, 1000)
            else
                handler.postDelayed({navController.navigate(R.id.action_loadingFragment_to_userOilKindFragment)}, 1000)
        })
    }
}