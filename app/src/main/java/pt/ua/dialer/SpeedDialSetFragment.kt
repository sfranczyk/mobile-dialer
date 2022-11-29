package pt.ua.dialer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import pt.ua.dialer.databinding.FragmentSpeedDialSetBinding


class SpeedDialSetFragment : Fragment() {

    private var memoryNumber: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = DataBindingUtil.inflate<FragmentSpeedDialSetBinding>(
            inflater,
            R.layout.fragment_speed_dial_set, container, false
        )

        memoryNumber = arguments?.getString("memoryNumber")

        binding.buttonSetSpeedDial.setOnClickListener { view: View ->
            if (binding.editTextNameSpeedDial.text.toString() == "") {
                val text = "M" + (memoryNumber!!.toInt() + 1).toString()
                binding.editTextNameSpeedDial.setText(text)
            }

            val speedDialData = binding.editTextNameSpeedDial.text.toString() + ".comma." +
                    binding.editTextNumber.text.toString() + ".comma." +
                    memoryNumber

            val bundle = bundleOf("speedDialData" to speedDialData)
            view.findNavController().navigate(
                R.id.action_speedDialSetFragment_to_dialFragment,
                bundle
            )
        }

        return binding.root
    }

}