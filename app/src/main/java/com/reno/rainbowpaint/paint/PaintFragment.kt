package com.reno.rainbowpaint.paint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.reno.rainbowpaint.databinding.FragmentPaintBinding


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

// used to identify the request for using external storage, which
// the save image feature needs
private const val SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1


class PaintFragment : Fragment() {
    private var _binding: FragmentPaintBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaintBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}