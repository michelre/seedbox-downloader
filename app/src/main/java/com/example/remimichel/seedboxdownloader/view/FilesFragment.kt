package com.example.remimichel.seedboxdownloader.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.remimichel.seedboxdownloader.R
import com.example.remimichel.seedboxdownloader.data.remote.File
import com.example.remimichel.seedboxdownloader.presenter.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FilesFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FilesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FilesFragment : Fragment(), FtpListView, BaseFragment {

  // TODO: Rename and change types of parameters
  private var mParam1: String? = null
  private var mParam2: String? = null
  private lateinit var adapter: FileFTPAdapter

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
    return inflater.inflate(R.layout.fragment_files, container, false)
  }

  // TODO: Rename method, update argument and hook method into UI event
  fun onButtonPressed(uri: Uri) {
    if (mListener != null) {
      mListener!!.onFragmentInteraction(uri)
    }
  }

  override fun onBackPressed(): Boolean {
    onBackButtonClick(this, getCredentials("server1"))
    return true
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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    setupFTPList()
    initFtpContent(this, this.getCredentials("server1"))
  }

  override fun displayList(files: List<File>) {
    activity!!.runOnUiThread {
      this.adapter.files = files
      this.adapter.notifyDataSetChanged()
    }
  }

  override fun drawError(error: Throwable) {
    Log.e("APPP", "", error)
  }

  fun getCredentials(server: String): Map<String, String> {
    val sharedPreferences = activity!!.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return mapOf(
        "host" to sharedPreferences.getString("${server}_host", ""),
        "login" to sharedPreferences.getString("${server}_login", ""),
        "password" to sharedPreferences.getString("${server}_password", "")
    )
  }

  fun setupFTPList() {
    val recyclerView = view!!.findViewById<RecyclerView>(R.id.file_view)
    recyclerView.setHasFixedSize(true)
    adapter = FileFTPAdapter(listOf(), { onFtpListItemClick(this, it) })
    recyclerView.layoutManager = LinearLayoutManager(activity)
    recyclerView.adapter = adapter
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
     * @return A new instance of fragment FilesFragment.
     */
    // TODO: Rename and change types and number of parameters
    fun newInstance(param1: String, param2: String): FilesFragment {
      val fragment = FilesFragment()
      val args = Bundle()
      args.putString(ARG_PARAM1, param1)
      args.putString(ARG_PARAM2, param2)
      fragment.arguments = args
      return fragment
    }
  }
}// Required empty public constructor
