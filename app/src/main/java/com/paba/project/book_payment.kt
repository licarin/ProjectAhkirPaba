package com.paba.project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class book_payment : AppCompatActivity() {
    data class TransactionRequest(
        val transaction_details: TransactionDetails,
        val customer_details: CustomerDetails,
        val item_details: List<ItemDetails>
    )

    data class TransactionDetails(
        val order_id: String,
        val gross_amount: Double
    )

    data class CustomerDetails(
        val first_name: String,
        val last_name: String,
        val email: String,
        val phone: String,
        val billing_address: BillingAddress,
        val shipping_address: ShippingAddress
    )

    data class BillingAddress(
        val address: String,
        val city: String,
        val postal_code: String
    )

    data class ShippingAddress(
        val address: String,
        val city: String,
        val postal_code: String
    )

    data class ItemDetails(
        val id: String,
        val price: Double,
        val quantity: Int,
        val name: String
    )
    data class SnapResponse(
        val token: String,
        val redirect_url: String
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_payment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val transactionResult = it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                    Toast.makeText(this@book_payment,"${transactionResult?.transactionId}", Toast.LENGTH_LONG).show()
                }
            }
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
        val gson = Gson()

        UiKitApi.Builder()
            .withMerchantClientKey("SB-Mid-client-HTLWiEqYeItTwAPm") // client_key is mandatory
            .withContext(applicationContext) // context is mandatory
            .withMerchantUrl("https://merchant-server-dummy.vercel.app/api/charge/") // set transaction finish callback (sdk callback)
            .enableLog(true) // enable sdk log (optional)
            .withFontFamily("fonts/OpenSans-Regular.ttf") // Replaced ASSET_FONT
            .withColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .build()
        setLocaleNew("id")

        val transactionDetails = TransactionDetails(
            order_id = "order-" + System.currentTimeMillis(),
            gross_amount = 100000.00
        )
        val billingAddress = BillingAddress(
            address = "Surabaya, Jawa Timur",
            city = "Surabaya",
            postal_code = "60111"
        )

        val shippingAddress = ShippingAddress(
            address = "Surabaya, Jawa Timur",
            city = "Surabaya",
            postal_code = "60111"
        )

        val customerDetails = CustomerDetails(
            first_name = "Budi",
            last_name = "Nugraha",
            email = "budi.nugraha@gmail.com",
            phone = "08123456789",
            billing_address = billingAddress,
            shipping_address = shippingAddress
        )

        val itemDetails = listOf(
            ItemDetails(
                id = "book01",
                price = 10000.00,
                quantity = 10,
                name = "Book Item"
            )
        )

        val transactionRequest = TransactionRequest(
            transaction_details = transactionDetails,
            customer_details = customerDetails,
            item_details = itemDetails
        )

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            gson.toJson(transactionRequest)
        )
        println("Request Body: ${gson.toJson(transactionRequest)}")

        val request = Request.Builder()
            .url("https://merchant-server-dummy.vercel.app/api/charge/")
            .post(requestBody)
            .build()
        println("Sending request to ${request.url}")

        var snapToken : String?

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    println("Raw Response: $responseBody")
                    val snapResponse = gson.fromJson(responseBody, SnapResponse::class.java)

                    snapToken = snapResponse.token
                    val redirectUrl = snapResponse.redirect_url

                    Log.d("TestToken", "Snap Token: $snapToken")
                    println("Redirect URL: $redirectUrl")
                    runOnUiThread {
                        if (!snapToken.isNullOrEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl))
                            startActivity(intent)
                        } else {
                            Log.d("TestToken", "Token tidak ditemukan")
                        }
                    }
                } else {
                    println("Error: ${response.code}")
                }
            }
        })



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

//    fun uiKitDetails(transactionRequest: TransactionRequest){
//        val customerDetails = CustomerDetails()
//        customerDetails.customerIdentifier = "Budi Identifier"
//        customerDetails.phone = "08123456789"
//        customerDetails.firstName = "Budi"
//        customerDetails.lastName = "Nugraha"
//        customerDetails.email = "budi.nugraha@gmail.com"
//        val shippingAddress = ShippingAddress()
//        shippingAddress.address = "Surabaya, Jawa Timur"
//        shippingAddress.city = "Surabaya"
//        shippingAddress.postalCode = "60111"
//        customerDetails.shippingAddress = shippingAddress
//        val billingAddress = BillingAddress()
//        billingAddress.address = "Surabaya, Jawa Timur"
//        billingAddress.city = "Surabaya"
//        billingAddress.postalCode = "60111"
//        customerDetails.billingAddress = billingAddress
//
//        transactionRequest.customerDetails = customerDetails
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val transactionResult = data?.getParcelableExtra<TransactionResult>(
                UiKitConstants.KEY_TRANSACTION_RESULT
            )
            if (transactionResult != null) {
                when (transactionResult.status) {
                    STATUS_SUCCESS -> {
                        Toast.makeText(this, "Transaction Finished. ID: " + transactionResult.transactionId, Toast.LENGTH_LONG).show()
                    }
                    STATUS_PENDING -> {
                        Toast.makeText(this, "Transaction Pending. ID: " + transactionResult.transactionId, Toast.LENGTH_LONG).show()
                    }
                    STATUS_FAILED -> {
                        Toast.makeText(this, "Transaction Failed. ID: " + transactionResult.transactionId, Toast.LENGTH_LONG).show()
                    }
                    STATUS_CANCELED -> {
                        Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_LONG).show()
                    }
                    STATUS_INVALID -> {
                        Toast.makeText(this, "Transaction Invalid. ID: " + transactionResult.transactionId, Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(this, "Transaction ID: " + transactionResult.transactionId + ". Message: " + transactionResult.status, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setLocaleNew(languageCode: String?) {
        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
    }
}