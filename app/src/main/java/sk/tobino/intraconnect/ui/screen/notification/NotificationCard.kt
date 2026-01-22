package sk.tobino.intraconnect.ui.screen.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import sk.tobino.intraconnect.R
import sk.tobino.intraconnect.data.model.NotificationDto
import sk.tobino.intraconnect.data.model.UserDto

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
            color = if (isDark) Color.White else Color.Black,
            textAlign = TextAlign.Justify
        )

        HorizontalDivider(Modifier.padding(top = 12.dp))
    }
}

@Composable
fun NotificationDetailCard (
    modifier: Modifier = Modifier,
    notification: NotificationDto,
    author: UserDto?,
    departments: List<String>,
    publishedText: String?,
    updatedText: String?
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        // author
        if (author != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (author.avatarUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(author.avatarUrl),
                        contentDescription = "Author avatar",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Image (
                        painter = painterResource(R.drawable.ic_avatar_default),
                        contentDescription = "Default avatar",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                    )
                }

                Column (
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text(
                        text = author.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!author.position.isNullOrBlank()) {
                        Text(
                            text = author.position,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = notification.title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // departments
        Text(
            text = departments.joinToString(", "),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // scrollable message
        Column(
            modifier = Modifier
                .heightIn(max = 425.dp) // 👈 limit výšky
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )
        }

        Spacer(modifier = Modifier.padding(top = 24.dp))

        // dates
        Column {
            if (publishedText != null) {
                Text(
                    text = stringResource(R.string.notification_published) + " " + publishedText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            if (updatedText != null) {
                Text(
                    text = stringResource(R.string.notification_updated) + " " + updatedText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}