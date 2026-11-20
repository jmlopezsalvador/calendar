package com.calendar.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.calendar.res.Res
import com.calendar.res.ic_alt_acalendar
import com.calendar.res.ic_alt_apple
import com.calendar.res.ic_alt_business
import com.calendar.res.ic_alt_google
import com.calendar.res.ic_alt_huawei
import com.calendar.res.ic_alt_moca
import com.calendar.res.ic_alt_oppo_google
import com.calendar.res.ic_alt_samsung
import com.calendar.res.ic_alt_xiaomi
import com.calendar.res.ic_alt_xiaomi_google
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/** Recurso de imagen del icono con el id de [com.calendar.domain.AppIdentity]. */
private fun recurso(id: String): DrawableResource = when (id) {
    "samsung" -> Res.drawable.ic_alt_samsung
    "apple" -> Res.drawable.ic_alt_apple
    "xiaomi" -> Res.drawable.ic_alt_xiaomi
    "xiaomi_google" -> Res.drawable.ic_alt_xiaomi_google
    "oppo_google" -> Res.drawable.ic_alt_oppo_google
    "huawei" -> Res.drawable.ic_alt_huawei
    "business" -> Res.drawable.ic_alt_business
    "moca" -> Res.drawable.ic_alt_moca
    "acalendar" -> Res.drawable.ic_alt_acalendar
    else -> Res.drawable.ic_alt_google
}

/** Imagen de un icono de la app con las esquinas redondeadas. */
@Composable
fun IconoApp(id: String, tamano: Dp, modifier: Modifier = Modifier) {
    Image(
        painterResource(recurso(id)), contentDescription = null,
        modifier = modifier.size(tamano).clip(RoundedCornerShape(tamano * 0.22f)),
    )
}
