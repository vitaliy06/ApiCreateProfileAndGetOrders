package com.example.apicreateprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
const val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIzIiwianRpIjoiYTg1ZjlkZjRiY2ZlMzkyMzg0YzE2YzkzOTIxYTc5ZDU0ZTc4YTllMjZiMzFjNmFkNDlmN2VlMWI4NDc5MzE1YTM3YjRmMzE1NjkzZjAyNmUiLCJpYXQiOjE2ODA3NjgyMTEuMjkwNTE2LCJuYmYiOjE2ODA3NjgyMTEuMjkwNTIxLCJleHAiOjE3MTIzOTA2MTEuMjg3MjI0LCJzdWIiOiI0MzkiLCJzY29wZXMiOltdfQ.wnYYn4P4aqV8qNcuQk5YJJBXEn6Q4KcPjgivoUXgSTn2Khw0xsuzOme-SF95G0Z5IjoNENWWBOQbh_fbbEdqRcX2WlOhRjVlqTQyOKTVWQkM1ddee-4auWso1OUUpXNfWti-jwm9PRzUp3GnB30NT-NZFhXJlr2BLa7l_bVt0C-nW8JlbbqVx8-PGZqSt3dmE0mGztjj4Z6EoN79_RbIqA3LiMZmuzidFrMWn9fM92uFSELjoM20csGPLMXCq8pInVnQ02YV3WJsOZBVcMWtgk07G_QfBxnpyVBUPSp1rYOILxuxwFdG7dsLb61FFmD2teGTsS-hh-qfn4xBXiGx43HVGtlCH59SeMJHnVjoiQBHJa4YelNMKrwAif7ybF1cpF_nx-cgUnK75lTVwydbGCRH7dtQ9Rah11HmegyolE-Ts-bx_Us38wC9UtoDuq6u1VgqM1slml0ECfVO8bYNNnpv9YUPpijs9utEV35ieZuZK-IDLC99PAeV78UX6mJyLPH-Hx2Vas-WkWfrt7p_PSOBAIUXfHvoGAiJVXnJVJOxL2hfnonVNzIfullGDbPV5jdWUnsq5eTLQqb0_Qg_NxhgjH3Csys9VraXfm7s1x5INdFtq_gLU2O9BNpD2kKchvTlNkA2k1n6px_FJnR3xJIZEPr1hdw0GjLOsLc9dek"
class MainActivity : AppCompatActivity() {

    data class MyResponse(val errors:String)
    data class MyDataClass(
        val id: Int,
        val patient_id: Int,
        val created_at: String,
        val updated_at: String,
        val order_id: Int,
        val catalog_id: Int,
        val price: String,
        val patient: Patient,
        val order: Order,
        val item: Item
    )

    data class Patient(
        val id: Int,
        val name: String,
        val created_at: String,
        val updated_at: String
    )

    data class Order(
        val created_at: String,
        val updated_at: String,
        val id: Int,
        val address: String,
        val date_time: String,
        val phone: String,
        val comment: String,
        val audio_comment: String
    )

    data class Item(
        val name: String,
        val category: String,
        val price: String,
        val description: String,
        val created_at: String,
        val updated_at: String,
        val id: Int,
        val time_result: String,
        val preparation: String,
        val bio: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch(Dispatchers.IO){
            fun decode(input:String){
                val regex = "\\\\u([0-9a-fA-F]){4}".toRegex()
                val output = regex.replace(input){matchResult ->
                    val hex = matchResult.groupValues[1]
                    val codePoint = Integer.parseInt(hex,16)
                    codePoint.toChar().toString()
                }
                println("2")
                println(output.toString())
            }
            val client = OkHttpClient()
   //получения списка заказов
            val requet = Request.Builder().url("https://medic.madskill.ru/api/orders")
                .get()
                .addHeader("Authorization","Bearer $token")
                .build()
            client.newCall(requet).enqueue(object:Callback{
                override fun onFailure(call: Call, e: IOException) {
                    println(e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string().toString()
                    val gson = Gson()

                    val listType = object : TypeToken<List<MyDataClass>>(){}.type
                    val dataList = gson.fromJson<List<MyDataClass>>(body,listType)
                    for(data in dataList){
                        println(data)
                    }
                }

            })


//создание профиля
            val json = """
        {
            "id": 1,
            "firstname": "1",
            "lastname": "1",
            "middlename": "1",
            "bith": "1",
            "pol": "Мужской",
            "image": "1"
        }
    """.trimIndent()

            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("https://medic.madskill.ru/api/createProfile")
                .post(requestBody)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    println("1")
                    println(response.code.toString())
                    val body = response.body?.string().toString()
                    val gson = Gson()
                    val decoded = gson.fromJson(body,MyResponse::class.java)
                    println(decoded)
                }

                override fun onFailure(call: Call, e: IOException) {
                    println(e.message)
                    decode(e.message.toString())
                }
            })

        }


    }
}