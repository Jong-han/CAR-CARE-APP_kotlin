package halla.icsw.acca_kotlin.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import halla.icsw.acca_kotlin.DB.Repository
import halla.icsw.acca_kotlin.MyViewModel
import halla.icsw.acca_kotlin.R
import halla.icsw.acca_kotlin.databinding.FragmentOilRecordBinding
import java.text.DecimalFormat

class OilRecordFragment : Fragment() {

    lateinit var binding: FragmentOilRecordBinding
    lateinit var navController: NavController
    val mMyViewModel: MyViewModel by viewModels()

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
            var temp_L_Double = 0.0
            var cnt = 1
            for (i in it) {
                if (cnt != it.size) {
                    temp_date += i.OilStation + "\n"
                    temp_totalPrice += dec.format(i.totalPrice) + " 원\n"
                    temp_price += dec.format(i.Price) + " 원\n"
                    temp_L_Double = i.totalPrice.toDouble() / i.Price.toDouble()
                    if (i.isChecked == 1)
                        mMyViewModel.saveLiter(temp_L_Double)
                    temp_L += String.format(
                        "%.2f",
                        temp_L_Double
                    ) + " L\n"
                    cnt++
                } else {
                    temp_date += i.OilStation
                    temp_totalPrice += dec.format(i.totalPrice) + " 원"
                    temp_price += dec.format(i.Price) + " 원"
                    temp_L_Double = i.totalPrice.toDouble() / i.Price.toDouble()
                    if (i.isChecked == 1)
                        mMyViewModel.saveLiter(temp_L_Double)
                    temp_L += String.format(
                        "%.2f",
                        temp_L_Double
                    ) + " L"
                    cnt = 0
                }
            }
            binding.oilDate.text = temp_date
            binding.totalOilPrice.text = temp_totalPrice
            binding.oilPrice.text = temp_price
            binding.oilL.text = temp_L
        })
        Repository.db.oilDAO().getTotalPrice().observe(viewLifecycleOwner, Observer {
            if (it == null) {
                binding.totalPrice.text = "총 주유가격 : 0 원"
            } else
                binding.totalPrice.text = "총 주유가격 : ${dec.format(it)} 원"
        })

        binding.btnSelf.setOnClickListener {
            var dialogBuilder = AlertDialog.Builder(context)
            var dialogView = layoutInflater.inflate(R.layout.dialog_layout, null)
            var dialog_totalOilPrice = dialogView.findViewById<EditText>(R.id.dialog_totalOilPrice)
            var dialog_OilPrice = dialogView.findViewById<EditText>(R.id.dialog_OilPrice)
            dialogBuilder.setView(dialogView)
                .setPositiveButton("입력") { dialogInterface, i ->
                    if (dialog_OilPrice.text.toString() != "" && dialog_totalOilPrice.text.toString() != "")
                        mMyViewModel.insertOilSelf(
                            dialog_OilPrice.text.toString().toInt(),
                            dialog_totalOilPrice.text.toString().toInt()
                        )
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
                .show()
        }

        binding.btnEfficiency.setOnClickListener {
            mMyViewModel.getEfficiency()
            var distance = 0.0
            var oil = 0.0
            var efficiency = ""
            var message = ""
            mMyViewModel.efficiency.observe(viewLifecycleOwner, Observer {
                distance = it.distance
                oil = it.oil
                efficiency = it.efficiency
//                message = "연비 체크 중이 아니거나, 체크 후 주유를 하지 않으셨습니다. \n\n주유 후 다시 확인해주세요!"
            })
//            if(oil != 0.0)
            message =
                "\n주행거리 : ${String.format("%.3f", distance)} km\n주유량 : ${
                    String.format(
                        "%.2f",
                        oil
                    )
                } L\n연비 : $efficiency km/L\n\n연비가 정확하지 않은가요?\n연비는 연료를 사용할 수록 정확해집니다."
            var dialogBuilder = AlertDialog.Builder(context)
                .setTitle("연비")
                .setMessage(message)
                .setPositiveButton("확인") { dialogInterface, i ->
                }
                .show()
        }
    }
}