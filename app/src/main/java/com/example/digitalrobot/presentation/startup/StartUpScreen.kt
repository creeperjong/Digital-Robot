package com.example.digitalrobot.presentation.startup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.digitalrobot.presentation.Dimens.MediumPadding
import com.example.digitalrobot.presentation.Dimens.SmallPadding
import com.example.digitalrobot.ui.theme.DigitalRobotTheme

@Composable
fun StartUpScreen(
    macAddress: String,
    onEvent: (StartUpEvent) -> Unit,
    navigateToScanner: () -> Unit,
    navigateToRobot: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .padding(MediumPadding)
            .fillMaxSize()
    ) {
        TextField(
            value = macAddress,
            onValueChange = { onEvent(StartUpEvent.ChangeMacAddress(it)) },
            placeholder = {
                Text(text = "Please enter MAC address or scan QR code ...")
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(SmallPadding)
        )
        Button(
            onClick = { navigateToScanner() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .padding(SmallPadding)
        ) {
            Text(text = "Scan QR Code")
        }
        Button(onClick = { navigateToRobot(macAddress.trim()) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.large,
            enabled = macAddress.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(SmallPadding)
        ) {
            Text(text = "Done")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StartUpScreenPreview() {
    DigitalRobotTheme {
        StartUpScreen(
            macAddress = "",
            onEvent = {},
            navigateToScanner = {},
            navigateToRobot = {}
        )
    }
}