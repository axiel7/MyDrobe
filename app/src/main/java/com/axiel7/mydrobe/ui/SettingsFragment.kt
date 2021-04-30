package com.axiel7.mydrobe.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import com.axiel7.mydrobe.R
import com.axiel7.mydrobe.databinding.FragmentSettingsBinding
import com.google.android.material.transition.MaterialFadeThrough

class SettingsFragment : Fragment() {

    private lateinit var safeContext: Context
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        //returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.beginTransaction()
            .add(R.id.settings_container, PreferenceFragment(), "preference")
            .commit()

        binding.settingsToolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }


    class PreferenceFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

        }

    }
}
