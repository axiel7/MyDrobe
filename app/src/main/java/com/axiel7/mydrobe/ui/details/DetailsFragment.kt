package com.axiel7.mydrobe.ui.details

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import coil.load
import coil.size.Scale
import com.axiel7.mydrobe.R
import com.axiel7.mydrobe.databinding.FragmentDetailsBinding
import com.axiel7.mydrobe.models.Clothing
import com.axiel7.mydrobe.models.ClothingViewModel
import com.axiel7.mydrobe.models.Season
import com.axiel7.mydrobe.ui.camera.CameraFragment
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.transition.MaterialSharedAxis
import java.io.File
import java.net.URI

class DetailsFragment : BottomSheetDialogFragment() {

    private val clothingViewModel: ClothingViewModel by activityViewModels()
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private var isNewItem = false
    private lateinit var newItem: Clothing
    private val colorsToAdd = mutableListOf<String>()
    private val colorsToRemove = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isNewItem = clothingViewModel.selectedItem.value == null
        if (isNewItem) {
            newItem = Clothing(name = "")
            clothingViewModel.selectItem(newItem)
        }

        if (clothingViewModel.selectedItem.value?.photoUri == null) {
            binding.image.setOnClickListener { openCamera() }
        }
        else {
            binding.image.setOnClickListener { showImageMenu(it) }
        }

        binding.applyButton.setOnClickListener { saveItem() }
        binding.cancelButton.setOnClickListener { dismiss() }
        if (!isNewItem) {
            binding.deleteButton.isEnabled = true
            binding.deleteButton.setOnClickListener {
                clothingViewModel.deleteClothing(clothingViewModel.selectedItem.value!!)
            }
        }

        binding.colorChipGroup.removeViews(0, binding.colorChipGroup.childCount-1)
        val defaultColors = resources.getIntArray(R.array.clothingColors)
        binding.addColorChip.setOnClickListener {
            MaterialColorPickerDialog.Builder(requireContext())
                .setTitle("Choose color")
                .setColorShape(ColorShape.CIRCLE)
                .setColorRes(defaultColors)
                .setColorListener { _, hex ->
                    addColor(hex)
                }
                .show()
        }

        clothingViewModel.selectedItem.observe(viewLifecycleOwner, {
            binding.nameTextEdit.setText(it?.name)
            if (it != null) {
                if (it.photoUri != null) {
                    binding.image.load(it.photoUri) {
                        placeholder(R.drawable.ic_add_image_24)
                        error(R.drawable.ic_add_image_24)
                        scale(Scale.FILL)
                    }
                } else {
                    binding.image.load(R.drawable.ic_add_image_24) {
                        placeholder(R.drawable.ic_add_image_24)
                        error(R.drawable.ic_add_image_24)
                    }
                }
                for (season in it.seasons) {
                    when (season) {
                        Season.WINTER -> binding.seasonChipGroup.check(R.id.winterChip)
                        Season.SPRING -> binding.seasonChipGroup.check(R.id.springChip)
                        Season.SUMMER -> binding.seasonChipGroup.check(R.id.summerChip)
                        Season.FALL -> binding.seasonChipGroup.check(R.id.fallChip)
                        Season.NONE -> binding.seasonChipGroup.clearCheck()
                    }
                }
                binding.colorChipGroup.removeViews(0, binding.colorChipGroup.childCount-1)
                for (color in it.colors) {
                    addChipColor(color)
                }
            }
        })
    }

    private fun openCamera() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        val newFragment = CameraFragment()
        childFragmentManager.beginTransaction()
            .add(R.id.imageContainer, newFragment, newFragment.tag)
            .addToBackStack(newFragment.tag)
            .commit()
    }

    private fun saveItem() {
        val name = binding.nameTextEdit.text.toString()
        val seasons = mutableListOf<Season>()
        val checkedSeasons = binding.seasonChipGroup.checkedChipIds
        if (checkedSeasons.isEmpty()) {
            seasons.add(Season.NONE)
        }
        else {
            for (id in checkedSeasons) {
                when (id) {
                    R.id.winterChip -> seasons.add(Season.WINTER)
                    R.id.springChip -> seasons.add(Season.SPRING)
                    R.id.summerChip -> seasons.add(Season.SUMMER)
                    R.id.fallChip -> seasons.add(Season.FALL)
                }
            }
        }
        clothingViewModel.selectedItem.value?.name = name
        clothingViewModel.selectedItem.value?.seasons = seasons
        clothingViewModel.selectedItem.value?.colors?.removeAll(colorsToRemove)
        clothingViewModel.selectedItem.value?.colors?.addAll(colorsToAdd)
        clothingViewModel.selectItem(clothingViewModel.selectedItem.value)
        if (isNewItem) {
            clothingViewModel.insertClothing(clothingViewModel.selectedItem.value!!)
        }
        else {
            clothingViewModel.updateClothing(clothingViewModel.selectedItem.value!!)
        }
        dismiss()
    }

    private fun addColor(hex: String) {
        if (clothingViewModel.selectedItem.value?.colors?.contains(hex) == false
                && !colorsToAdd.contains(hex)) {
            colorsToAdd.add(hex)
            addChipColor(hex)
        }
    }

    private fun removeColor(hex: String) {
        colorsToAdd.remove(hex)
        if (clothingViewModel.selectedItem.value?.colors?.contains(hex) == true) {
            colorsToRemove.add(hex)
        }
    }

    private fun addChipColor(hex: String) {
        val chip = LayoutInflater.from(requireContext())
                .inflate(R.layout.chip_color, null) as Chip
        chip.chipIconTint = ColorStateList.valueOf(Color.parseColor(hex))
        chip.setOnCloseIconClickListener {
            removeColor(hex)
            binding.colorChipGroup.removeView(chip)
        }
        binding.colorChipGroup.addView(chip, 0)
    }

    @SuppressLint("RestrictedApi")
    private fun showImageMenu(v: View) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(R.menu.menu_image, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_view -> {
                    //TODO(Open image viewer)
                    true
                }
                R.id.action_replace -> {
                    openCamera()
                    true
                }
                R.id.action_delete -> {
                    val file = File(URI.create(clothingViewModel.selectedItem.value?.photoUri!!))
                    if (file.delete()) {
                        clothingViewModel.selectedItem.value?.photoUri = null
                        clothingViewModel.selectItem(clothingViewModel.selectedItem.value)
                    }
                    true
                }
                else -> true
            }
        }

        if (popup.menu is MenuBuilder) {
            val menuBuilder = popup.menu as MenuBuilder
            menuBuilder.setOptionalIconsVisible(true)
            for (item in menuBuilder.visibleItems) {
                val iconMarginPx =
                        TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 8F, resources.displayMetrics)
                                .toInt()
                if (item.icon != null) {
                    item.icon = InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0)
                }
            }
        }
        popup.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        clothingViewModel.selectItem(null)
    }

}