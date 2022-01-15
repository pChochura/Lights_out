package com.pointlessapps.lightsout.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pointlessapps.lightsout.R
import com.pointlessapps.lightsout.adapters.AdapterDevicesFound
import com.pointlessapps.lightsout.databinding.DialogDeviceAddBinding
import com.pointlessapps.lightsout.decorations.SpacingItemDecoration
import com.pointlessapps.lightsout.model.RoomWithDevices
import com.pointlessapps.lightsout.providers.ProviderWiz
import com.pointlessapps.lightsout.px
import com.pointlessapps.lightsout.viewModels.ViewModelMain
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeviceAddDialog(private val room: RoomWithDevices) : BottomSheetDialogFragment() {

    private enum class Icon(val icon: Int) {
        Light1(R.drawable.icon_light1),
        Light2(R.drawable.icon_light2),
        Light3(R.drawable.icon_light3),
        Light4(R.drawable.icon_light4)
    }

    private lateinit var binding: DialogDeviceAddBinding
    private val viewModel: ViewModelMain by viewModel()
    private val providerWiz = ProviderWiz()
    private val adapterDevices = AdapterDevicesFound()
    private var selectedIconIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeviceAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.listDevices.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = adapterDevices
            addItemDecoration(SpacingItemDecoration(16.px))
        }

        binding.buttonConnectWiz.setOnClickListener {
            binding.containerDevices.isVisible = true
            binding.containerProviders.isVisible = false
            TransitionManager.beginDelayedTransition(
                dialog?.window?.decorView!! as ViewGroup,
                AutoTransition()
            )
            lifecycleScope.launch {
                providerWiz.findLights { devices ->
                    adapterDevices.updateList(devices)
                    TransitionManager.beginDelayedTransition(
                        dialog?.window?.decorView!! as ViewGroup,
                        AutoTransition()
                    )
                }
            }
        }

        binding.buttonCancel.setOnClickListener { dismiss() }
        binding.buttonDone.setOnClickListener {
            if (binding.editName.text.toString().isNotEmpty()) {
                adapterDevices.getSelectedDevice()?.also { device ->
                    device.name = binding.editName.text.toString()
                    device.roomId = room.room.id
                    device.icon = Icon.values()[selectedIconIndex].icon
                    viewModel.addDevice(device)
                    dismiss()
                }
            }
        }

        binding.containerIcon.setOnClickListener {
            selectedIconIndex = (selectedIconIndex + 1) % Icon.values().size
            binding.imageIcon.setImageResource(Icon.values()[selectedIconIndex].icon)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        providerWiz.turnOff()
        super.onDismiss(dialog)
    }
}