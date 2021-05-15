package com.axiel7.mydrobe.ui.details

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import coil.load
import coil.size.Scale
import com.axiel7.mydrobe.BuildConfig
import com.axiel7.mydrobe.MyApplication
import com.axiel7.mydrobe.R
import com.axiel7.mydrobe.databinding.FragmentDetailsBinding
import com.axiel7.mydrobe.models.Clothing
import com.axiel7.mydrobe.models.ClothingViewModel
import com.axiel7.mydrobe.models.Season
import com.axiel7.mydrobe.ui.camera.CameraFragment
import com.axiel7.mydrobe.utils.SharedPrefsHelpers
import com.axiel7.mydrobe.utils.UseCases
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.transition.MaterialSharedAxis
import java.io.File
import java.net.URI

class DetailsFragment : BottomSheetDialogFragment() {

    private val clothingViewModel: ClothingViewModel by activityViewModels {
        ClothingViewModel.provideFactory(MyApplication.clothesRepository)
    }
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private var isNewItem = false
    private var useLegacyCamera = false
    private lateinit var newItem: Clothing
    private lateinit var safeContext: Context
    private var currentPhotoPath: String = ""
    private val colorsToAdd = mutableListOf<String>()
    private val colorsToRemove = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        useLegacyCamera = SharedPrefsHelpers.instance?.getBoolean("camera_legacy", false) ?: false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
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
            binding.image.setOnClickListener {
                if (useLegacyCamera) { openCameraIntent() }
                else { openCamera() }
            }
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
                dismiss()
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
                        Season.WINTER -> binding.seasonChipGroup.check(R.id.winter_chip)
                        Season.SPRING -> binding.seasonChipGroup.check(R.id.spring_chip)
                        Season.SUMMER -> binding.seasonChipGroup.check(R.id.summer_chip)
                        Season.FALL -> binding.seasonChipGroup.check(R.id.fall_chip)
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
        BottomSheetBehavior.from(
                dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        ).state = BottomSheetBehavior.STATE_EXPANDED

        val params = binding.imageContainer.layoutParams as ViewGroup.MarginLayoutParams
        params.height = binding.bottomContainer.height
        binding.imageContainer.layoutParams = params

        val newFragment = CameraFragment()
        childFragmentManager.beginTransaction()
            .add(R.id.image_container, newFragment, newFragment.tag)
            .addToBackStack(newFragment.tag)
            .commit()
    }


    private fun openCameraIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(safeContext.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File = UseCases.createImageFile(safeContext)
                photoFile.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        safeContext,
                        "${BuildConfig.APPLICATION_ID}.provider",
                        it
                    )
                    currentPhotoPath = Uri.fromFile(it).toString()
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, 1)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (clothingViewModel.selectedItem.value?.photoUri != null) {
                val file = File(URI.create(clothingViewModel.selectedItem.value?.photoUri!!))
                file.delete()
            }
            clothingViewModel.selectedItem.value?.photoUri = currentPhotoPath
            clothingViewModel.selectItem(clothingViewModel.selectedItem.value)
        }
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
                    R.id.winter_chip -> seasons.add(Season.WINTER)
                    R.id.spring_chip -> seasons.add(Season.SPRING)
                    R.id.summer_chip -> seasons.add(Season.SUMMER)
                    R.id.fall_chip -> seasons.add(Season.FALL)
                }
            }
        }
        clothingViewModel.selectedItem.value?.name = name
        clothingViewModel.selectedItem.value?.seasons = seasons
        clothingViewModel.selectedItem.value?.colors?.removeAll(colorsToRemove)
        clothingViewModel.selectedItem.value?.colors?.addAll(colorsToAdd)
        clothingViewModel.selectItem(clothingViewModel.selectedItem.value)
        if (isNewItem) {
            clothingViewModel.addClothing(clothingViewModel.selectedItem.value!!)
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
        val chip = LayoutInflater.from(safeContext)
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
        val popup = PopupMenu(safeContext, v)
        popup.menuInflater.inflate(R.menu.menu_image, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_view -> {
                    //TODO(Open image viewer)
                    true
                }
                R.id.action_replace -> {
                    if (useLegacyCamera) { openCameraIntent() }
                    else { openCamera() }
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

    fun restoreImageParams() {
        val params = binding.imageContainer.layoutParams as ViewGroup.MarginLayoutParams
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        binding.imageContainer.layoutParams = params
    }

    override fun onDestroy() {
        super.onDestroy()
        clothingViewModel.selectItem(null)
    }

}