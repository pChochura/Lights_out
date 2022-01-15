package com.pointlessapps.lightsout.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pointlessapps.lightsout.R
import com.pointlessapps.lightsout.databinding.DialogDeviceDetailsBinding
import com.pointlessapps.lightsout.model.Device
import com.pointlessapps.lightsout.viewModels.ViewModelMain
import org.koin.android.ext.android.inject
import kotlin.math.roundToInt

class DeviceDetailsDialog(private val device: Device) : BottomSheetDialogFragment() {

    private enum class Icon(val icon: Int) {
        Light1(R.drawable.icon_light1),
        Light2(R.drawable.icon_light2),
        Light3(R.drawable.icon_light3),
        Light4(R.drawable.icon_light4)
    }

    private lateinit var binding: DialogDeviceDetailsBinding
    private val viewModel: ViewModelMain by inject()
    private var selectedIconIndex = Icon.values().find { it.icon == device.icon }?.ordinal ?: 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeviceDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.sliderBrightness.setOnValueChangedListener {
            val brightness = (100 * it).roundToInt()
            binding.sliderBrightness.value = "$brightness%"
            device.brightness = brightness
            device.update()
            viewModel.updateDevice(device)
        }
        binding.sliderHue.setOnValueChangedListener {
            binding.sliderHue.value = "${(360 * it).roundToInt()}"
            binding.sliderHue.color = Color.HSVToColor(floatArrayOf(it * 360f, 0.5f, 1f))
            device.hue = (it * 360).roundToInt()
            device.update()
            viewModel.updateDevice(device)
        }

        binding.sliderBrightness.progress = device.brightness / 100f
        binding.sliderHue.progress = device.hue / 360f

        binding.editName.setText(device.name)
        binding.imageIcon.setImageResource(device.icon)
        binding.imageIcon.clipToOutline = true

        binding.buttonRemove.setOnClickListener {
            Dialog(requireActivity()).also { dialog ->
                dialog.setContentView(R.layout.dialog_remove)
                dialog.findViewById<TextView>(R.id.textTitle).setText(R.string.do_you_want_to_remove_this_device)
                dialog.findViewById<View>(R.id.buttonCancel).setOnClickListener { dialog.dismiss() }
                dialog.findViewById<View>(R.id.buttonRemove).setOnClickListener {
                    viewModel.removeDevice(device)
                    dialog.dismiss()
                    dismiss()
                }
            }.show()
        }
        binding.buttonSave.setOnClickListener {
            device.name = binding.editName.text.toString()
            device.icon = Icon.values()[selectedIconIndex].icon
            viewModel.updateDevice(device)
            dismiss()
        }

        binding.containerIcon.setOnClickListener {
            selectedIconIndex = (selectedIconIndex + 1) % Icon.values().size
            binding.imageIcon.setImageResource(Icon.values()[selectedIconIndex].icon)
        }
    }
}