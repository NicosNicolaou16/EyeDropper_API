package com.nicos.eye_dropper_api

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nicos.eye_dropper_api.ui.theme.Eye_Dropper_APITheme

class MainActivity : ComponentActivity() {

    private lateinit var eyeDropperLauncher: ActivityResultLauncher<Intent>
    var selectedColor by mutableIntStateOf(Color.Black.toArgb())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        eyeDropperLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val resultColor = result.data?.getIntExtra(
                        Intent.EXTRA_COLOR,
                        Color.Black.toArgb()
                    )
                    println("Selected color: $resultColor")
                    selectedColor = resultColor ?: Color.Black.toArgb()
                }
            }
        setContent {

            Eye_Dropper_APITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        selectedColor = selectedColor,
                        launchColorPicker = { launchColorPicker() }
                    )
                }
            }
        }
    }

    private fun launchColorPicker() {
        val intent = Intent(Intent.ACTION_OPEN_EYE_DROPPER)
        eyeDropperLauncher.launch(intent)
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    selectedColor: Int = Color.Black.value.toInt(),
    launchColorPicker: () -> Unit
) {

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.sample), // Replace with your jpg name
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // This makes the image cover the whole screen
        )
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(color = Color(selectedColor))
                    .clickable(
                        onClick = { launchColorPicker() }
                    )
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Select Color: ${selectedColor.toHexCode()}",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Eye Dropper",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Eye_Dropper_APITheme {
        Greeting(
            selectedColor = Color.Black.toArgb(),
            launchColorPicker = {}
        )
    }
}

// Helper to get Hex String
fun Int.toHexCode(): String = String.format("#%08X", this)

// Basic name matcher
fun getColorName(colorInt: Int): String {return when (colorInt) {
    Color.Black.toArgb() -> "Black"
    Color.White.toArgb() -> "White"
    Color.Red.toArgb() -> "Red"
    Color.Green.toArgb() -> "Green"
    Color.Blue.toArgb() -> "Blue"
    Color.Yellow.toArgb() -> "Yellow"
    Color.Cyan.toArgb() -> "Cyan"
    Color.Magenta.toArgb() -> "Magenta"
    Color.Gray.toArgb() -> "Gray"
    else -> "Picked Color"
}
}