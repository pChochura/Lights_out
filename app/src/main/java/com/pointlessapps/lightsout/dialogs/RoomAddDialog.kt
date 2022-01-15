package com.pointlessapps.lightsout.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pointlessapps.lightsout.R
import com.pointlessapps.lightsout.databinding.DialogRoomAddBinding
import com.pointlessapps.lightsout.model.DeviceRoom
import com.pointlessapps.lightsout.viewModels.ViewModelMain
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class RoomAddDialog : BottomSheetDialogFragment() {

    private enum class Icon(val icon: Int) {
        Bedroom(R.drawable.icon_bedroom),
        LivingRoom(R.drawable.icon_living_room),
        Kitchen(R.drawable.icon_kitchen)
    }

    private lateinit var binding: DialogRoomAddBinding
    private val viewModel: ViewModelMain by viewModel()
    private var selectedIconIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogRoomAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonCancel.setOnClickListener { dismiss() }
        binding.buttonDone.setOnClickListener {
            if (binding.editName.text.toString().isNotEmpty()) {
                val room = DeviceRoom(
                    UUID.randomUUID(),
                    binding.editName.text.toString(),
                    Icon.values()[selectedIconIndex].icon
                )
                viewModel.addRoom(room)
                dismiss()
            }
        }

        binding.containerIcon.setOnClickListener {
            selectedIconIndex = (selectedIconIndex + 1) % Icon.values().size
            binding.imageIcon.setImageResource(Icon.values()[selectedIconIndex].icon)
        }
    }
}