package com.zimmy.best.stocker.ui.main.onboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zimmy.best.stocker.BuildConfig
import com.zimmy.best.stocker.R
import com.zimmy.best.stocker.databinding.ActivityLoginBinding
import com.zimmy.best.stocker.model.User
import com.zimmy.best.stocker.ui.main.home.HomeActivity
import com.zimmy.best.stocker.utility.Constants
import com.zimmy.best.stocker.utility.PreferenceManager
import com.zimmy.best.stocker.utility.toast

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val GOOGLE_SIGN_IN = 64
    private val TAG = LoginActivity::class.simpleName
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private var isMain = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (isMain) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            initialise()
            updateUI()
        }
    }

    private fun initialise() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.WEB_CLIENT_ID)
            .requestEmail().build()
        gsc = GoogleSignIn.getClient(this@LoginActivity, gso)
    }

    private fun signIn() {
        val intent = gsc.signInIntent
        startActivityForResult(intent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                toast(resources.getString(R.string.something_went_wrong))
                Log.v(TAG, "error stack ${e.status}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
                if (account != null) {
                    databaseOperation(account)
                }
            } else {
                toast(resources.getString(R.string.something_went_wrong))
                Log.v(TAG, "error stack ${task.exception!!.message}")
            }
        }
    }

    private fun updateUI() {
        binding.signInBt.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        isMain = isUserSignIn()
    }

    private fun isUserSignIn(): Boolean {
        val currentUser = mAuth.currentUser
        return currentUser != null
    }

    private fun databaseOperation(account: GoogleSignInAccount) {
        val accountReference = FirebaseDatabase.getInstance().reference.child(Constants.USERS)
        var firstTime: Boolean

        val imageUrl = if (account.photoUrl == null) {
            Constants.defaultUserImage
        } else {
            account.photoUrl.toString()
        }
        Log.d("displayNamedisplayName","displayNamedisplayName $imageUrl")
        Log.d("displayNamedisplayName","displayNamedisplayName uid ${mAuth.uid.toString()}")
        accountReference.child(mAuth.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    firstTime = !snapshot.exists()
                    Log.d("displayNamedisplayName","displayNamedisplayName $firstTime")
                    if (firstTime) {
                        val user = User(account.displayName!!, account.email!!, imageUrl)
                        Log.d("displayNamedisplayName","displayNamedisplayName $user")
                        accountReference
                            .child(mAuth.uid.toString()).child(Constants.BASIC_DETAILS)
                            .setValue(user)
                        toast("Hello, " + account.displayName)
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        accountReference.child(mAuth.uid.toString())
                            .child(Constants.BASIC_DETAILS)
                            .addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val user = snapshot.getValue(User::class.java) as User
                                    val preferenceManager =
                                        PreferenceManager(this@LoginActivity)
                                    preferenceManager.saveString(
                                        PreferenceManager.NAME,
                                        user.name
                                    )
                                    preferenceManager.saveString(
                                        PreferenceManager.EMAIL,
                                        user.email
                                    )
                                    toast("welcome back ${user.name}")
                                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                                    finish()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.v(TAG, "database error " + error.message)
                                }

                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.v(TAG, "database error ${error.message}")
                }

            })
    }
}