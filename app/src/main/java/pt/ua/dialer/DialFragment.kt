package pt.ua.dialer

import android.Manifest
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import pt.ua.dialer.databinding.FragmentDialBinding


class DialFragment : Fragment() {

    private var speedDials: List<TextView>? = null
    private lateinit var speedDialsNumbers: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = DataBindingUtil.inflate<FragmentDialBinding>(
            inflater,
            R.layout.fragment_dial, container, false
        )

        checkPermission()

        setSpeedDialList(binding)
        updateSpeedDial()
        setDigitButtonsListeners(binding)
        setClearButtonListener(binding)
        setSpeedDialButtonsListeners(binding)

        setCallButton(binding);

        return binding.root
    }

    private fun setCallButton(binding: FragmentDialBinding) {
        val input = binding.textViewInput

        binding.buttonCall.setOnClickListener {
            if (input.text.toString() != "-") {
                val number = input.text.toString()
                startCallActivity(number)
            }
        }
    }

    private fun checkPermission() {
        Dexter.withContext(this.context)
            .withPermission(Manifest.permission.CALL_PHONE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {}

                override fun onPermissionDenied(response: PermissionDeniedResponse) {}
                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                }
            }).check()
    }

    private fun updateSpeedDial() {
        val memoriesData = arguments?.getString("speedDialData")

        if (memoriesData != null) {
            val memoriesList = memoriesData.split(".comma.")

            when (memoriesList[2].toInt()) {
                0 -> {
                    speedDials!![0].text = memoriesList[0]
                    speedDialsNumbers[0] = memoriesList[1]
                }
                1 -> {
                    speedDials!![1].text = memoriesList[0]
                    speedDialsNumbers[1] = memoriesList[1]
                }
                else -> {
                    speedDials!![2].text = memoriesList[0]
                    speedDialsNumbers[2] = memoriesList[1]
                }
            }
        }
    }

    private fun setSpeedDialList(binding: FragmentDialBinding) {
        speedDials = listOf(
            binding.buttonSpeedDials1,
            binding.buttonSpeedDials2,
            binding.buttonSpeedDials3,
        )

        val settings: SharedPreferences = requireActivity().getPreferences(MODE_PRIVATE)

        speedDials!![0].text = settings.getString("m1", "M1")
        speedDials!![1].text = settings.getString("m2", "M2")
        speedDials!![2].text = settings.getString("m3", "M3")

        speedDialsNumbers = mutableListOf(
            settings.getString("m1d", "")!!,
            settings.getString("m2d", "")!!,
            settings.getString("m3d", "")!!,
        )
    }

    override fun onPause() {

        super.onPause()

        val settings: SharedPreferences = requireActivity().getPreferences(MODE_PRIVATE)
        val editor = settings.edit()

        editor.putString("m1", speedDials!![0].text.toString())
        editor.putString("m2", speedDials!![1].text.toString())
        editor.putString("m3", speedDials!![2].text.toString())
        editor.putString("m1d", speedDialsNumbers[0])
        editor.putString("m2d", speedDialsNumbers[1])
        editor.putString("m3d", speedDialsNumbers[2])
        editor.apply()
    }


    private fun setDigitButtonsListeners(binding: FragmentDialBinding) {
        val input = binding.textViewInput

        val buttonsDigits = listOf(
            binding.button0,
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9,
            binding.buttonPlus,
            binding.buttonHash
        )

        val digitsValues = listOf(
            "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "+", "#"
        )

        buttonsDigits.zip(digitsValues).forEach { pair ->
            pair.component1().setOnClickListener {
                if (input.text.toString() == "-") {
                    input.text = ""
                }

                val newNumber = input.text.toString() + pair.component2()

                input.text = newNumber
            }
        }
    }

    private fun setClearButtonListener(binding: FragmentDialBinding) {
        val input = binding.textViewInput

        binding.imageButtonClear.setOnClickListener {
            val text = input.text.toString()
            input.text = text.substring(0, text.length - 1)

            if (input.text.toString() == "") {
                input.text = "-"
            }
        }
    }

    private fun setSpeedDialButtonsListeners(binding: FragmentDialBinding) {
        val input = binding.textViewInput

        speedDials?.forEachIndexed { index, element ->
            element.setOnLongClickListener { view: View ->
                val bundle = bundleOf("memoryNumber" to index.toString())
                view.findNavController().navigate(
                    R.id.action_dialFragment_to_speedDialSetFragment,
                    bundle
                )
                true
            }
            element.setOnClickListener {
                if (speedDialsNumbers[index] != "")
                    input.text = speedDialsNumbers[index]
            }
        }
    }

    private fun startCallActivity(number: String) {
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")))
    }

}

