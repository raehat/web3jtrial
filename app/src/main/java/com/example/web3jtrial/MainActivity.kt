package com.example.web3jtrial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.protocol.http.HttpService

class MainActivity : AppCompatActivity() {

    val viewModel: MainActivityViewModel by viewModels{MainActivityViewModelFactory(context = this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.clientVersion.observe(this, Observer {
            if (!it.hasError()) {
                Toast.makeText(this, "noicce", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "" + it.error, Toast.LENGTH_SHORT).show()
            }
            //viewModel.createWallet()
            //viewModel.loadWallet()
        })

        findViewById<Button>(R.id.button_send).setOnClickListener{
            viewModel.sendTransactions()
        }

    }
}