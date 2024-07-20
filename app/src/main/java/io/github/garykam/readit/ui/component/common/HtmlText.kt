package io.github.garykam.readit.ui.component.common

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun HtmlText(
    text: String,
    modifier: Modifier = Modifier,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = AnnotatedString.fromHtml(
            text,
            linkStyles = TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
        ),
        modifier = modifier,
        overflow = overflow,
        maxLines = maxLines,
        style = style
    )
}
