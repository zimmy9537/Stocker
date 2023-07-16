package com.zimmy.best.stocker.ui.main.home.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.zimmy.best.stocker.databinding.FragmentHomeBinding
import com.zimmy.best.stocker.model.User
import com.zimmy.best.stocker.ui.main.settings.SettingsActivity
import com.zimmy.best.stocker.utility.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userReference: DatabaseReference
    private val mAUth = FirebaseAuth.getInstance()
    private val TAG = HomeFragment::class.java.simpleName
    private lateinit var user: User

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialise()
    }

    private fun initialise() {
        with(binding) {
            if (mAUth.uid != null) {
                userReference =
                    FirebaseDatabase.getInstance().reference.child(Constants.USERS)
                        .child(mAUth.uid!!)
                userReference.child(Constants.BASIC_DETAILS)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            user = snapshot.getValue(User::class.java)!!
                            Picasso.get().load(user.imageUrl).into(profilePic)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d(TAG, "Some database error ${error.message}")
                        }
                    })
                userReference.child(Constants.WALLET_BALANCE)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val balance = snapshot.getValue(Int::class.java)
                            walletAmountTv.text = balance.toString()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d(TAG, "Some database error ${error.message}")
                        }
                    })
            }
            settingsIv.setOnClickListener {
                val intent = Intent(context, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
    }

}