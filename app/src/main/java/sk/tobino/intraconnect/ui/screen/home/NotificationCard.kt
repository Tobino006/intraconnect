package sk.tobino.intraconnect.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sk.tobino.intraconnect.data.model.NotificationDto

@Composable
fun NotificationCard(
    notif: NotificationDto,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    Column(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            notif.title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(6.dp))

        Text(
            notif.message,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = if (isDark) Color.White else Color.Black
        )

        HorizontalDivider(Modifier.padding(top = 12.dp))
    }
}
