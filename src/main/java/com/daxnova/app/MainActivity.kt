package com.daxnova.app

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.lazy.items

import android.os.Bundle
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.clickable

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.material.icons.filled.Delete

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.CardDefaults

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
private val Purple = Color(0xFF7B1FA2)
private val PurpleDark = Color(0xFF4A148C)
private val PurpleLight = Color(0xFFF3E5F5)

// --- Datos de alertas ---
data class Alerta(val icono: String, val mensaje: String)

val alertasEjemplo = listOf(
    Alerta("⚠️", "El insumo 'Tinte rubio' está por agotarse"),
    Alerta("⏰", "3 clientes llevan más de 60 días sin agendar cita")
)

data class ModuloItem(val icono: String, val titulo: String, val ruta: String)

val modulos = listOf(
    ModuloItem("📅", "Agenda", "agenda"),
    ModuloItem("👥", "Clientes", "clientes"),
    ModuloItem("📦", "Inventario", "inventario"),
    ModuloItem("💰", "Pagos e ingresos", "pagos"),
    ModuloItem("📊", "Reportes", "reportes"),
    ModuloItem("⚙️", "Configuración", "config")
)

@Composable
fun ModuleCard(icono: String, titulo: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.clickable { onClick() }) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icono, fontSize = 30.sp)
            Spacer(Modifier.height(8.dp))
            Text(titulo, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AlertsSection(alertas: List<Alerta>) {
    if (alertas.isEmpty()) return

    Card(
        Modifier.fillMaxWidth().padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleLight)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Alertas activas", fontWeight = FontWeight.Bold, color = PurpleDark)
            Spacer(Modifier.height(8.dp))

            alertas.forEach { alerta ->
                Row(
                    Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(alerta.icono, fontSize = 18.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(alerta.mensaje)
                }
            }
        }
    }
}

@Composable
fun DaxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Purple, secondary = Color(0xFF9C27B0),
            primaryContainer = PurpleLight, onPrimary = Color.White
        ),
        content = content
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent { DaxTheme { App() } }
    }
}

@Composable
fun App() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = "welcome"
    ) {

        composable("welcome") {
            Welcome(nav)
        }

        composable("register") {
            Register(nav)
        }

        composable("login") {
            Login(nav)
        }

        composable("dashboard") {
            Dashboard(nav)
        }

        composable("agenda") {
            Module(
                "Agenda",
                "Gestiona citas, tareas y próximos eventos.",
                nav
            )
        }

        composable("clientes") {
            Module(
                "Clientes",
                "Registra y consulta la información de clientes.",
                nav
            )
        }

        composable("inventario") {
            InventarioScreen(nav)
        }
        composable("pagos") {
            Module(
                "Pagos e ingresos",
                "Consulta ingresos y registra pagos.",
                nav
            )
        }

        composable("reportes") {
            Module(
                "Reportes",
                "Visualiza información resumida del negocio.",
                nav
            )
        }

        composable("config") {
            Module(
                "Configuración",
                "Personaliza preferencias de DAXNOVA.",
                nav
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(title: String, nav: NavHostController) {

    TopAppBar(
        title = {
            Text(
                title,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    nav.popBackStack()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás"
                )
            }
        }
    )
}

@Composable
fun Welcome(nav: NavHostController) {
    Column(
        Modifier.fillMaxSize().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("DAXNOVA", style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold, color = PurpleDark)
        Spacer(Modifier.height(12.dp))
        Text("Inteligencia organizacional para tu negocio",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(40.dp))
        Button({ nav.navigate("login") }, Modifier.fillMaxWidth()) { Text("Iniciar sesión") }
        Spacer(Modifier.height(12.dp))
        OutlinedButton({ nav.navigate("register") }, Modifier.fillMaxWidth()) { Text("Crear cuenta") }
    }
}

@Composable
fun Register(nav: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Header("Crear cuenta", nav)
        Field("Nombre", name) { name = it }
        Field("Correo", email) { email = it }
        Field("Contraseña", pass, isPassword = true) { pass = it }
        Spacer(Modifier.height(18.dp))
        Button({ nav.navigate("dashboard") }, Modifier.fillMaxWidth()) { Text("Registrarme") }
    }
}

@Composable
fun Login(nav: NavHostController) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Header("Iniciar sesión", nav)
        Field("Correo", email) { email = it }
        Field("Contraseña", pass, isPassword = true) { pass = it }
        Spacer(Modifier.height(18.dp))

        if (error != null) {
            Text(error!!, color = Color.Red)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                error = null
                cargando = true
                scope.launch {
                    try {
                        val respuesta = RetrofitClient.api.login(LoginRequest(email, pass))
                        Sesion.token = respuesta.accessToken
                        nav.navigate("dashboard")
                    } catch (e: HttpException) {
                        error = "Correo o contraseña incorrectos"
                    } catch (e: Exception) {
                        error = "No se pudo conectar al servidor: ${e.message}"
                    } finally {
                        cargando = false
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (cargando) "Ingresando..." else "Entrar")
        }
    }
}

@Composable
fun InventarioScreen(nav: NavHostController) {
    val insumos = remember { mutableStateListOf<InsumoApi>() }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val resultado = RetrofitClient.api.listarInsumos(Sesion.tokenConBearer())
            insumos.clear()
            insumos.addAll(resultado)
        } catch (e: Exception) {
            error = "No se pudieron cargar los insumos: ${e.message}"
        } finally {
            cargando = false
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Header("Inventario", nav)
        Spacer(Modifier.height(12.dp))

        FormularioInsumo(insumos = insumos)
        Spacer(Modifier.height(16.dp))

        when {
            cargando -> CircularProgressIndicator()
            error != null -> Text(error!!, color = Color.Red)
            insumos.isEmpty() -> Text("Todavía no tienes insumos registrados.")
            else -> LazyColumn {
                items(insumos, key = { it.id }) { insumo ->
                    InsumoRow(insumo = insumo, insumos = insumos)
                }
            }
        }
    }
}

@Composable
fun FormularioInsumo(insumos: SnapshotStateList<InsumoApi>) {
    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var minimo by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Agregar insumo", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Field("Nombre", nombre) { nombre = it }
            Field("Cantidad", cantidad) { cantidad = it }
            Field("Mínimo", minimo) { minimo = it }
            Field("Precio unitario", precio) { precio = it }
            Spacer(Modifier.height(8.dp))
            Button(
                enabled = !guardando && nombre.isNotBlank(),
                onClick = {
                    guardando = true
                    scope.launch {
                        try {
                            val nuevo = RetrofitClient.api.crearInsumo(
                                Sesion.tokenConBearer(),
                                InsumoCreateRequest(
                                    nombre = nombre,
                                    categoria = null,
                                    cantidad = cantidad.toIntOrNull() ?: 0,
                                    minimo = minimo.toIntOrNull() ?: 0,
                                    precioUnitario = precio.toDoubleOrNull() ?: 0.0
                                )
                            )
                            insumos.add(nuevo)
                            nombre = ""; cantidad = ""; minimo = ""; precio = ""
                        } catch (e: Exception) {
                            // En un paso futuro podemos mostrar este error en pantalla
                        } finally {
                            guardando = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (guardando) "Guardando..." else "Agregar")
            }
        }
    }
}
@Composable
fun InsumoRow(insumo: InsumoApi, insumos: SnapshotStateList<InsumoApi>) {
    val bajoStock = insumo.cantidad <= insumo.minimo
    val scope = rememberCoroutineScope()

    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(insumo.nombre)
                Text(
                    "${insumo.cantidad}/${insumo.minimo}",
                    color = if (bajoStock) Color.Red else PurpleDark,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = {
                scope.launch {
                    try {
                        RetrofitClient.api.borrarInsumo(Sesion.tokenConBearer(), insumo.id)
                        insumos.removeIf { it.id == insumo.id }
                    } catch (e: Exception) { }
                }
            }) {
                Icon(Icons.Filled.Delete, contentDescription = "Borrar")
            }
        }
    }
}
@Composable
fun Field(
    label: String,
    value: String,
    isPassword: Boolean = false,
    onChange: (String) -> Unit
) {
    var esVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
        singleLine = true,
        visualTransformation = if (isPassword && !esVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { esVisible = !esVisible }) {
                    Icon(
                        imageVector = if (esVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (esVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(nav: NavHostController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "DAXNOVA",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { pad ->

        LazyColumn(
            Modifier
                .padding(pad)
                .padding(16.dp)
        ) {

            item {
                AlertsSection(alertasEjemplo)
                Spacer(Modifier.height(12.dp))
            }

            item {
                Text(
                    "Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text("Resumen general de tu organización")

                Spacer(Modifier.height(18.dp))
            }

            item { Metric("👥", "Clientes", "128") }
            item { Metric("💰", "Ingresos del mes", "$ 8.450.000") }
            item { Metric("📅", "Citas pendientes", "12") }

            item {
                Spacer(Modifier.height(12.dp))

                Text(
                    "Módulos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            modulos.chunked(2).forEach { pareja ->
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pareja.forEach { m ->
                            ModuleCard(m.icono, m.titulo, { nav.navigate(m.ruta) }, Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Metric(icon: String, title: String, value: String) {
    Card(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 22.sp)
                Spacer(Modifier.width(12.dp))
                Text(title)
            }
            Text(value, fontWeight = FontWeight.Bold, color = PurpleDark)
        }
    }
}

@Composable
fun ModuleButton(title: String, route: String, nav: NavHostController) {
    OutlinedButton({ nav.navigate(route) },
        Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Text(title) }
}

@Composable
fun Module(title: String, description: String, nav: NavHostController) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Header(title, nav)
        Spacer(Modifier.height(18.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text(title, style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold, color = PurpleDark)
                Spacer(Modifier.height(8.dp))
                Text(description)
                Spacer(Modifier.height(20.dp))
                Button({ }, Modifier.fillMaxWidth()) { Text("Agregar registro") }
            }
        }
    }
}
