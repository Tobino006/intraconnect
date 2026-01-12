package sk.tobino.intraconnect.ui.screen.home

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import sk.tobino.intraconnect.R
import sk.tobino.intraconnect.data.model.HomeBottomNavItem

@Composable
fun HomeBottomNav (
    selectedIndex : Int,
    onItemSelected: (Int) -> Unit,
    profileAvatarUrl: String?,
    modifier: Modifier = Modifier
) {

    val navItemList = listOf (
        HomeBottomNavItem(stringResource(R.string.home), painterResource(R.drawable.ic_home)),
        HomeBottomNavItem("Filter", painterResource(R.drawable.ic_filter)),
        HomeBottomNavItem(stringResource(R.string.settings), painterResource(R.drawable.ic_settings)),
        HomeBottomNavItem(stringResource(R.string.profile), painterResource(R.drawable.ic_avatar_default))
    )

    NavigationBar(modifier = modifier, containerColor = colorScheme.background) {
        navItemList.forEachIndexed { index, navItem ->
            NavigationBarItem (
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = {

                    if (index == 3 && profileAvatarUrl != null) {
                        // profile image from supabase
                        AsyncImage (
                            model = profileAvatarUrl,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        // other icons
                        Icon (
                            modifier = Modifier.size(30.dp),
                            painter = navItem.icon,
                            contentDescription = navItem.label,
                            tint = Color.Unspecified
                        )
                    }
                },
                label = {
                    Text (
                        text = navItem.label,
                        color = colorScheme.onBackground,
                        style = MaterialTheme.typography.labelSmall)
                }
            )
        }
    }
}