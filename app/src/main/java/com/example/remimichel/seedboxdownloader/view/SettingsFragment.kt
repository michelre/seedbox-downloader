package com.example.remimichel.seedboxdownloader.view

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

import com.example.remimichel.seedboxdownloader.R
import com.example.remimichel.seedboxdownloader.presenter.saveSetting

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {

  // TODO: Rename and change types of parameters
  private var mParam1: String? = null
  private var mParam2: String? = null

  private var mListener: OnFragmentInteractionListener? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (arguments != null) {
      mParam1 = arguments!!.getString(ARG_PARAM1)
      mParam2 = arguments!!.getString(ARG_PARAM2)
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_settings, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initSettings()
  }

  // TODO: Rename method, update argument and hook method into UI event
  fun onButtonPressed(uri: Uri) {
    if (mListener != null) {
      mListener!!.onFragmentInteraction(uri)
    }
  }

  fun initSettings() {
    val sharedPreferences = activity!!.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val fields = listOf(
        R.id.server1_host to "server1_host",
        R.id.server1_login to "server1_login",
        R.id.server1_password to "server1_password",
        R.id.server2_host to "server2_host",
        R.id.server2_login to "server2_login",
        R.id.server2_password to "server2_password"
    )
    initSettingsListeners(fields, sharedPreferences)
    initSettingValues(fields, sharedPreferences)
  }

  fun initSettingsListeners(fields: List<Pair<Int, String>>, sharedPreferences: SharedPreferences) {
    fields.map { field ->
      view!!.findViewById<EditText>(field.first).addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
          saveSetting(sharedPreferences, field.second to s.toString())
        }
      })
    }
  }

  fun initSettingValues(fields: List<Pair<Int, String>>, sharedPreferences: SharedPreferences) {
    fields.map { field -> view!!.findViewById<EditText>(field.first).setText(sharedPreferences.getString(field.second, "")) }
  }

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    if (context is OnFragmentInteractionListener) {
      mListener = context
    } else {
      throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
    }
  }

  override fun onDetach() {
    super.onDetach()
    mListener = null
  }

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   *
   *
   * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
   */
  interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    fun onFragmentInteraction(uri: Uri)
  }

  companion object {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    fun newInstance(param1: String, param2: String): SettingsFragment {
      val fragment = SettingsFragment()
      val args = Bundle()
      args.putString(ARG_PARAM1, param1)
      args.putString(ARG_PARAM2, param2)
      fragment.arguments = args
      return fragment
    }
  }
}// Required empty public constructor
