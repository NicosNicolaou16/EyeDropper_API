package com.nicos.eye_dropper_api

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.nicos.eye_dropper_api.ui.theme.Eye_Dropper_APITheme

class MainActivity : ComponentActivity() {

    private lateinit var eyeDropperLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        eyeDropperLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val color = result.data?.getIntExtra(Intent.EXTRA_COLOR, Color.BLACK)
                    println("Selected color: $color")
                }
            }

        setContent {
            Eye_Dropper_APITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
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
    name: String,
    modifier: Modifier = Modifier,
    color: Int = Color.Black.value.toInt(),
    launchColorPicker: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clickable(
                    onClick = { launchColorPicker() }
                )
                .background(color = Color(color))
        ) {
            Text(
                text = "Hello $name!",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Eye_Dropper_APITheme {
        Greeting("Android", launchColorPicker = {})
    }
}