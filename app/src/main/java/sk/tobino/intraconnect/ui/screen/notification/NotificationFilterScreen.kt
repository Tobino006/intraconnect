package sk.tobino.intraconnect.ui.screen.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import sk.tobino.intraconnect.R
import sk.tobino.intraconnect.ui.screen.home.NotificationFilter

@Composable
fun NotificationFilterScreen (
    hasDepartment: Boolean,
    currentFilter: NotificationFilter,
    onFilterSelected: (NotificationFilter) -> Unit
) {
    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // global
        FilterOption (
            text = stringResource(R.string.filter_global),
            selected = currentFilter == NotificationFilter.GLOBAL_ONLY,
            onClick = { onFilterSelected(NotificationFilter.GLOBAL_ONLY) }
        )

        HorizontalDivider()

        // department only
        if (hasDepartment) {
            FilterOption (
                text = stringResource(R.string.filter_department),
                selected = currentFilter == NotificationFilter.DEPARTMENT_ONLY,
                onClick = { onFilterSelected(NotificationFilter.DEPARTMENT_ONLY) }
            )
        }

        HorizontalDivider()

        // all
        FilterOption (
            text = if (hasDepartment) stringResource(R.string.filter_global_department) else stringResource(R.string.filter_all),
            selected = currentFilter == NotificationFilter.ALL,
            onClick = { onFilterSelected(NotificationFilter.ALL) }
        )

        HorizontalDivider()
    }
}

@Composable
private fun FilterOption (
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row (
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton (
            selected = selected,
            onClick = onClick
        )
        Text (
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}