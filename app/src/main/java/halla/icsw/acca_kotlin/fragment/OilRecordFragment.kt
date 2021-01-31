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
import halla.icsw.acca_kotlin.DB.Repository
import halla.icsw.acca_kotlin.MyViewModel
import halla.icsw.acca_kotlin.R
import halla.icsw.acca_kotlin.databinding.FragmentDrivingRecordBinding
import halla.icsw.acca_kotlin.databinding.FragmentOilRecordBinding
import java.text.DecimalFormat

class OilRecordFragment : Fragment() {

    lateinit var binding: FragmentOilRecordBinding
    lateinit var navController: NavController

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_oil_record, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.lifecycleOwner = this
        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }
        var dec = DecimalFormat("#,###")
        Repository.db.oilDAO().getAll().observe(viewLifecycleOwner, Observer {
            var temp_date = ""
            var temp_totalPrice = ""
            var temp_price = ""
            var temp_L = ""
            var cnt = 1
            for (i in it) {
                if (cnt != it.size) {
                    temp_date += i.OilStation + "\n"
                    temp_totalPrice += dec.format(i.totalPrice) + "원\n"
                    temp_price += dec.format(i.Price) + "원\n"
                    temp_L = String.format("%.2f",(i.totalPrice.toDouble()/i.Price.toDouble())) + "L\n"
                    cnt++
                } else {
                    temp_date += i.OilStation
                    temp_totalPrice += dec.format(i.totalPrice) + "원"
                    temp_price +=  dec.format(i.Price) + "원"
                    temp_L = String.format("%.2f",(i.totalPrice.toDouble()/i.Price.toDouble())) + "L"
                    cnt = 0
                }
            }
            binding.oilDate.text = temp_date
            binding.totalOilPrice.text = temp_totalPrice
            binding.oilPrice.text = temp_price
            binding.oilL.text = temp_L
        })
        Repository.db.oilDAO().getTotalPrice().observe(viewLifecycleOwner, Observer {
            binding.totalPrice.text = "총 주유가격 : ${dec.format(it)} 원"
        })
    }
}