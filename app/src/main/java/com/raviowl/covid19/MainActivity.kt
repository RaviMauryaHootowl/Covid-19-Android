package com.raviowl.covid19

import android.app.DownloadManager
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception
import android.support.v4.content.ContextCompat
import android.graphics.drawable.Drawable
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.Utils.getSDKInt


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val queue = Volley.newRequestQueue(this);
        val baseurl = "https://corona.lmao.ninja/countries/"
        val baseurlv2 = "https://corona.lmao.ninja/v2/historical/"

        val globaljsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, "https://corona.lmao.ninja/all", null,
            Response.Listener { response ->
                try{
                    val json_res = JSONObject(response.toString())
                    val activeCasesString = json_res.get("cases");
                    val recoveredString = json_res.get("recovered");
                    val deathString = json_res.get("deaths");

                    //text1.text = "Country : ${covid.country}\nCases: ${covid.cases}\nRecovered : ${covid.recovered}\nDeaths : ${covid.deaths}"
                    activeCases.text = "${activeCasesString}\nCases"
                    recoveredCases.text = "${recoveredString}\nRecovered"
                    deadCases.text = "${deathString}\nDeaths"
                }catch (e : Exception){
                    Toast.makeText(this, "Not Found", Toast.LENGTH_LONG).show()
                }

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            }
        );

        queue.add(globaljsonObjectRequest)
        btnSearch.setOnClickListener {
            val countryNameText = searchCountry.text
            val url = baseurl + countryNameText
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    try{
                        val json_res = JSONObject(response.toString())
                        val countryNameString = json_res.get("country");
                        val activeCasesString = json_res.get("cases");
                        val recoveredString = json_res.get("recovered");
                        val deathString = json_res.get("deaths");

                        //text1.text = "Country : ${covid.country}\nCases: ${covid.cases}\nRecovered : ${covid.recovered}\nDeaths : ${covid.deaths}"
                        countryNameTxt.text = "Country : ${countryNameString}"
                        activeCases.text = "${activeCasesString}\nCases"
                        recoveredCases.text = "${recoveredString}\nRecovered"
                        deadCases.text = "${deathString}\nDeaths"
                    }catch (e : Exception){
                        Toast.makeText(this, "Not Found", Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                }
            );


            val urlhist = baseurlv2 + countryNameText
            var str = ""
            val jsonObjectRequestV2 = JsonObjectRequest(
                Request.Method.GET, urlhist, null,
                Response.Listener { response ->
                    try {
                        val json_res : JSONObject = JSONObject(response.toString()).getJSONObject("timeline")
                        val cases_obj = json_res.getJSONObject("cases")
                        val values = ArrayList<Entry>();
                        val keys = cases_obj.keys()
                        var i = 1f
                        while (keys.hasNext())
                        {
                            val key = keys.next() as String
//                        if (cases_obj.get(key) is JSONObject)
//                        {
//                            val xx = JSONObject(resobj.get(key).toString())
//                            Log.d("res1", xx.getString("something"))
//                        }
                            values.add(Entry(i, cases_obj.getString(key).toFloat()))
                            i++
                        }

                        val vl = LineDataSet(values, "Cases")

                        //Part4
                        vl.setDrawValues(false)
                        vl.setDrawFilled(true)
                        vl.lineWidth = 3f
                        //vl.setGradientColor(R.color.colorOrange, R.color.colorWhite)
                        //vl.fillColor = R.color.colorOrange
                        vl.color = Color.rgb(255, 159, 69)

                        if (Utils.getSDKInt() >= 18) {
                            val drawable = ContextCompat.getDrawable(this, R.drawable.fadebg)
                            vl.setFillDrawable(drawable)
                        } else {
                            vl.setFillColor(Color.RED)
                        }

                        //Part5
                        lineChartCovid.xAxis.labelRotationAngle = 0f

                        // Part 6
                        lineChartCovid.data = LineData(vl)

                        //Part8
                        lineChartCovid.setTouchEnabled(true)
                        lineChartCovid.setPinchZoom(true)

                        //Part9
                        lineChartCovid.description.text = "Days"
                        lineChartCovid.setNoDataText("No forex yet!")

                        //Part10
                        lineChartCovid.animateX(1800, Easing.EaseInExpo)
                        //Toast.makeText(this, str, Toast.LENGTH_LONG).show()
                    }catch (e : Exception) {
                        Toast.makeText(this, "Not Found", Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                }
            )

            queue.add(jsonObjectRequest)
            queue.add(jsonObjectRequestV2)
        }
    }
}

class covidData(val country : String, val cases : Int, val recovered : Int, val deaths : Int)