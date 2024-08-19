package io.github.garykam.readit.ui.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList

@Composable
fun DropdownButton(
    items: ImmutableList<String>,
    selectedItem: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.textButtonColors()
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        TextButton(onClick = { isExpanded = true }) {
            Text(
                text = selectedItem,
                color = colors.contentColor
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = colors.contentColor
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            for (item in items) {
                DropdownMenuItem(
                    modifier = Modifier.background(
                        if (item == selectedItem) {
                            colors.disabledContentColor
                        } else {
                            colors.containerColor
                        }
                    ),
                    text = {
                        Text(
                            text = item,
                            color = colors.contentColor
                        )
                    },
                    onClick = {
                        onItemClick(item)
                        isExpanded = false
                    }
                )
            }
        }
    }
}
