package io.github.garykam.readit.ui.component.common

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ItemDrawer(
    items: List<String>,
    selectedItem: String,
    drawerState: DrawerState,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(IntrinsicSize.Max)) {
                for (item in items) {
                    Button(
                        onClick = { onItemClick(item) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (item == selectedItem) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.background
                            },
                            contentColor = if (item == selectedItem) {
                                MaterialTheme.colorScheme.background
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            }
                        )
                    ) {
                        Text(text = item)
                        Spacer(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        },
        modifier = modifier,
        drawerState = drawerState
    ) {
        content()
    }
}
