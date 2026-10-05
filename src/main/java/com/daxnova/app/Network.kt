package com.daxnova.app

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.annotations.SerializedName

// ---------- Modelos: deben coincidir con lo que devuelve el backend ----------

data class LoginRequest(val correo: String, val password: String)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
)

data class InsumoApi(
    val id: Int,
    val nombre: String,
    val categoria: String?,
    val cantidad: Int,
    val minimo: Int,
    @SerializedName("precio_unitario") val precioUnitario: Double,
    val estado: String
)

data class InsumoCreateRequest(
    val nombre: String,
    val categoria: String?,
    val cantidad: Int,
    val minimo: Int,
    @SerializedName("precio_unitario") val precioUnitario: Double
)

// ---------- Qué endpoints existen y qué forma tienen ----------

interface DaxNovaApi {
    @POST("auth/login")
    suspend fun login(@Body datos: LoginRequest): TokenResponse

    @GET("insumos/")
    suspend fun listarInsumos(@Header("Authorization") token: String): List<InsumoApi>

    @POST("insumos/")
    suspend fun crearInsumo(
        @Header("Authorization") token: String,
        @Body datos: InsumoCreateRequest
    ): InsumoApi

    @DELETE("insumos/{id}")
    suspend fun borrarInsumo(@Header("Authorization") token: String, @Path("id") id: Int)
}

// ---------- Una sola instancia de Retrofit para toda la app ----------

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder().addInterceptor(logging).build()

    val api: DaxNovaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DaxNovaApi::class.java)
    }
}

// ---------- Guarda el token mientras la app está abierta ----------

object Sesion {
    var token: String? = null
    fun tokenConBearer(): String = "Bearer $token"
}