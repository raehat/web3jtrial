package com.example.web3jtrial

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.protocol.http.HttpService
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import java.io.File
import java.lang.Exception
import java.math.BigDecimal
import java.security.Security

class MainActivityViewModel(val context: Context): ViewModel() {

    val clientVersion: MutableLiveData<Web3ClientVersion> = MutableLiveData()
    val password = "raehatodi"
    val walletPath = context.filesDir.absolutePath
    var walletDir = File(walletPath)
    lateinit var web3: Web3j

    init {
        connectToEthereum()
        setupBouncyCastle()
    }

    private fun connectToEthereum() {
        web3 = Web3j.build(HttpService("https://mainnet.infura.io/v3/11782cb03f81433d86bb20d869cdd882"))
        try {
            GlobalScope.launch {
                clientVersion.postValue(web3.web3ClientVersion().sendAsync().get())
            }
        } catch (e: Exception) {
            Log.i("error", "" + e.message)
        }
    }

    fun createWallet() {

        try {
            var fileName: String? = WalletUtils.generateLightNewWalletFile(password, walletDir)
            walletDir = File(walletPath + "/" + fileName)
            var sharedPreferences: SharedPreferences.Editor? = context.getSharedPreferences("DATA", MODE_PRIVATE).edit()
            sharedPreferences?.putString("fileName", fileName)
            sharedPreferences?.apply()
        } catch (e: Exception) {
            Log.i("catch-create-wallet", "" + e.message)
        }

    }

    fun loadWallet() {
        try {
            val credentials = WalletUtils.loadCredentials(password, walletDir)
            Log.i("address", "" + credentials.address)
        } catch (e: Exception) {
            Log.i("catch-load-wallet", "" + e.message)
        }
    }

    fun sendTransactions() {
         try { var sharedPreferences: SharedPreferences = context.getSharedPreferences("DATA", MODE_PRIVATE)
            var fileName = sharedPreferences.getString("fileName", "")
            walletDir = File(walletPath + "/" + fileName)
            val credentials = WalletUtils.loadCredentials(password, walletDir)
            Log.i("address", "" + credentials.address)
            val receipt = Transfer.sendFunds(web3, credentials, "0x31B98D14007bDEe637298086988A0bBd31184523", BigDecimal.valueOf(0.01), Convert.Unit.ETHER).sendAsync().get()
            Log.i("transcation_id=","" + receipt.transactionHash)
        } catch (e: Exception) {
            Log.i("catch-send-transactions", "" + e.message)
        }
    }

    fun setupBouncyCastle() {
        val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
        if (provider==null)
            return
        if (provider.javaClass.equals(BouncyCastleProvider::class.java))
            return

        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)

    }

}

class MainActivityViewModelFactory(val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}