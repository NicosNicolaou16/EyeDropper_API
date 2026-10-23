package com.nicos.eyedropper_api

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nicos.eyedropper_api.ui.theme.EyeDropper_APITheme
import com.nicos.eyedropper_api.utils.getClosestColorName
import com.nicos.eyedropper_api.utils.toHexCode

class MainActivity : ComponentActivity() {

    private lateinit var eyeDropperLauncher: ActivityResultLauncher<Intent>
    var selectedColor by mutableIntStateOf(Color.Black.copy(alpha = 0.5f).toArgb())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        eyeDropperLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val resultColor = result.data?.getIntExtra(
                        Intent.EXTRA_COLOR,
                        Color.Black.copy(alpha = 0.5f).toArgb()
                    )
                    Log.d("EyeDropper", "Selected color: ${resultColor?.toHexCode()}")
                    selectedColor = resultColor ?: Color.Black.copy(alpha = 0.5f).toArgb()
                }
            }
        setContent {
            EyeDropper_APITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EyeDropper(
                        modifier = Modifier.padding(innerPadding),
                        selectedColor = selectedColor,
                        launchColorPicker = { launchColorPicker() }
                    )
                }
            }
        }
    }

    private fun launchColorPicker() {
        try {
            val intent = Intent(Intent.ACTION_OPEN_EYE_DROPPER)
            eyeDropperLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.eye_dropper_unavailable, Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun EyeDropper(
    modifier: Modifier = Modifier,
    selectedColor: Int = Color.Black.toArgb(),
    launchColorPicker: () -> Unit
) {
    val textColor = if (Color(selectedColor).luminance() > 0.5f) Color.Black else Color.White
    val colorName = remember(selectedColor) { getClosestColorName(selectedColor) }

    Box {
        Image(
            painter = painterResource(id = R.drawable.dragonball_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier
                    .size(width = 250.dp, height = 110.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(color = Color(selectedColor))
                    .clickable { launchColorPicker() }
            ) {
                Text(
                    text = stringResource(
                        R.string.selected_color_hex_color_name,
                        selectedColor.toHexCode(),
                        colorName
                    ),
                    color = textColor,
                    textAlign = TextAlign.Center,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EyeDropperPreview() {
    EyeDropper_APITheme {
        EyeDropper(
            selectedColor = Color.Black.toArgb(),
            launchColorPicker = {}
        )
    }
}