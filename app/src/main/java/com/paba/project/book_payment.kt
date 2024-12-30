package com.paba.project

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_FAILED
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_INVALID
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_PENDING
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_SUCCESS
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import com.midtrans.sdk.uikit.internal.util.UiKitConstants.STATUS_CANCELED

class book_payment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_payment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        UiKitApi.Builder()
//            .withMerchantClientKey("SB-Mid-client-HTLWiEqYeItTwAPm") // client_key is mandatory
//            .withContext(applicationContext) // context is mandatory
//            .withMerchantUrl("https://snap-merchant-server.herokuapp.com/api/") // set transaction finish callback (sdk callback)
//            .enableLog(true) // enable sdk log (optional)
//            .withFontFamily("fonts/OpenSans-Regular.ttf") // Replaced ASSET_FONT
//            .withColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
//            .build()
//        setLocaleNew("id")

        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-HTLWiEqYeItTwAPm")
            .setContext(applicationContext)
            .setTransactionFinishedCallback {
                result ->

            }
            .setMerchantBaseUrl("https://merchant-server-dummy.vercel.app/api/charge/")
            .enableLog(true)
            .setColorTheme(com.midtrans.sdk.corekit.core.themes.CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("id")
            .buildSDK()

        val transactionRequest = TransactionRequest("Kita-Store-"+System.currentTimeMillis().toShort() + "", 100000.00)
        val detail = ItemDetails("NamaItem", 100000.00, 10, "Book Item")
        val itemDetails = ArrayList<ItemDetails>()
        itemDetails.add(detail)
        uiKitDetails(transactionRequest)
        transactionRequest.itemDetails = itemDetails
        MidtransSDK.getInstance().transactionRequest = transactionRequest
        MidtransSDK.getInstance().startPaymentUiFlow(this, "fb646652-9e81-40c4-a29f-b0a2856b0cf7")


//        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result?.resultCode == RESULT_OK) {
//                result.data?.let {
//                    val transactionResult = it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
//                    Toast.makeText(this,"${transactionResult?.transactionId}", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//
//        UiKitApi.getDefaultInstance().startPaymentUiFlow(
//            this@book_payment, // Activity
//            launcher, // ActivityResultLauncher
//            "6ea7be8b-ff57-47bc-9499-ff620649c323" // Snap Token
//        )
    }

    fun uiKitDetails(transactionRequest: TransactionRequest){
        val customerDetails = CustomerDetails()
        customerDetails.customerIdentifier = "Budi Identifier"
        customerDetails.phone = "08123456789"
        customerDetails.firstName = "Budi"
        customerDetails.lastName = "Nugraha"
        customerDetails.email = "budi.nugraha@gmail.com"
        val shippingAddress = ShippingAddress()
        shippingAddress.address = "Surabaya, Jawa Timur"
        shippingAddress.city = "Surabaya"
        shippingAddress.postalCode = "60111"
        customerDetails.shippingAddress = shippingAddress
        val billingAddress = BillingAddress()
        billingAddress.address = "Surabaya, Jawa Timur"
        billingAddress.city = "Surabaya"
        billingAddress.postalCode = "60111"
        customerDetails.billingAddress = billingAddress

        transactionRequest.customerDetails = customerDetails
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == RESULT_OK) {
//            val transactionResult = data?.getParcelableExtra<TransactionResult>(
//                UiKitConstants.KEY_TRANSACTION_RESULT
//            )
//            if (transactionResult != null) {
//                when (transactionResult.status) {
//                    STATUS_SUCCESS -> {
//                        Toast.makeText(this, "Transaction Finished. ID: " + transactionResult.transactionId, Toast.LENGTH_LONG).show()
//                    }
//                    STATUS_PENDING -> {
//                        Toast.makeText(this, "Transaction Pending. ID: " + transactionResult.transactionId, Toast.LENGTH_LONG).show()
//                    }
//                    STATUS_FAILED -> {
//                        Toast.makeText(this, "Transaction Failed. ID: " + transactionResult.transactionId, Toast.LENGTH_LONG).show()
//                    }
//                    STATUS_CANCELED -> {
//                        Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_LONG).show()
//                    }
//                    STATUS_INVALID -> {
//                        Toast.makeText(this, "Transaction Invalid. ID: " + transactionResult.transactionId, Toast.LENGTH_LONG).show()
//                    }
//                    else -> {
//                        Toast.makeText(this, "Transaction ID: " + transactionResult.transactionId + ". Message: " + transactionResult.status, Toast.LENGTH_LONG).show()
//                    }
//                }
//            } else {
//                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    private fun setLocaleNew(languageCode: String?) {
        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
    }
}