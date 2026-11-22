package com.example.bukukita.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import com.example.bukukita.R

@Composable
fun BukuKitaBrandMark(
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val drawableRes = if (isLightTheme) R.drawable.ic_bukukita_logo else R.drawable.ic_bukukita_logo_dark
    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = contentDescription,
        modifier = modifier
    )
}
