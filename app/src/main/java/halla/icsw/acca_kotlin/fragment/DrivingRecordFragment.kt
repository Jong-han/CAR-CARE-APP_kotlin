package halla.icsw.acca_kotlin.fragment

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
import androidx.room.Room
import halla.icsw.acca_kotlin.DB.MyDataBase
import halla.icsw.acca_kotlin.MyViewModel
import halla.icsw.acca_kotlin.R
import halla.icsw.acca_kotlin.databinding.FragmentDrivingRecordBinding

class DrivingRecordFragment : Fragment() {

    lateinit var binding: FragmentDrivingRecordBinding
    lateinit var navController: NavController
    val mMyViewModel: MyViewModel by viewModels()

//    var db = activity?.let {
//        Room.databaseBuilder(
//            it.applicationContext,
//            MyDataBase::class.java,
//            "DataBase.db"
//        ).allowMainThreadQueries().build()
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_driving_record, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewModel = mMyViewModel

//        db?.driveDAO()?.getdate()?.observe(viewLifecycleOwner, Observer {
//            binding.driveDate.text = it.toString()
//        })

        navController = Navigation.findNavController(view)

        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }
    }
}