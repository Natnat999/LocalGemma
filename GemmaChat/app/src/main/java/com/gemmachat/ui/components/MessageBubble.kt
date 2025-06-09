package com.gemmachat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gemmachat.R
import com.gemmachat.data.model.ChatMessage
import com.gemmachat.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val isUserMessage = message.isFromUser
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showCopyConfirmation by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
        ) {
            if (!isUserMessage) {
                Icon(
                    imageVector = if (message.isError) Icons.Default.Error else Icons.Default.SmartToy,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp),
                    tint = if (message.isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
            
            Surface(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUserMessage) 16.dp else 4.dp,
                            bottomEnd = if (isUserMessage) 4.dp else 16.dp
                        )
                    )
                    .clickable {
                        clipboardManager.setText(AnnotatedString(message.content))
                        showCopyConfirmation = true
                    },
                color = when {
                    message.isError -> MaterialTheme.colorScheme.errorContainer
                    isUserMessage -> {
                        if (isSystemInDarkTheme()) 
                            UserMessageBackgroundDark 
                        else 
                            UserMessageBackground
                    }
                    else -> MaterialTheme.colorScheme.secondaryContainer
                },
                contentColor = when {
                    message.isError -> MaterialTheme.colorScheme.onErrorContainer
                    isUserMessage -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                }
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = formatTimestamp(message.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                }
            }
            
            if (isUserMessage) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(start = 8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Copy confirmation
        if (showCopyConfirmation) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showCopyConfirmation = false
            }
            
            Text(
                text = stringResource(R.string.copied),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(if (isUserMessage) Alignment.End else Alignment.Start)
            )
        }
    }
}

@Composable
private fun formatTimestamp(timestamp: Long): String {
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    return formatter.format(Date(timestamp))
}