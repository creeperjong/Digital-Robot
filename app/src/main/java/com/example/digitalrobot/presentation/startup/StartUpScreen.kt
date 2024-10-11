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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.digitalrobot.presentation.Dimens.MediumPadding
import com.example.digitalrobot.presentation.Dimens.SmallPadding
import com.example.digitalrobot.presentation.robot.component.DropdownMenu
import com.example.digitalrobot.ui.theme.DigitalRobotTheme

@Composable
fun StartUpScreen(
    state: StartUpState,
    onEvent: (StartUpEvent) -> Unit,
    navigateToScanner: () -> Unit,
    navigateToRobot: (StartUpState) -> Unit
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onEvent(StartUpEvent.InitSharedPreferences(context))
        onEvent(StartUpEvent.SetAssistantList(state.gptApiKey))
    }

    Column(
        modifier = Modifier
            .padding(MediumPadding)
            .fillMaxSize()
    ) {
        TextField(
            value = state.macAddress,
            onValueChange = { onEvent(StartUpEvent.SetMacAddress(it)) },
            placeholder = {
                Text(text = "Please enter MAC address or scan QR code ...")
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(SmallPadding)
        )

        DropdownMenu(
            label = "Project",
            text = state.projectName,
            options = state.projectOptions.keys.toList(),
            onSelected = {
                onEvent(StartUpEvent.SetProject(projectName = it))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SmallPadding)
        )

        DropdownMenu(
            label = "Assistant",
            text = state.assistantName,
            options = state.assistantOptions.keys.toList(),
            onSelected = {
                onEvent(StartUpEvent.SetAssistant(assistantName = it))
            },
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

        Button(onClick = { navigateToRobot(state) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.large,
            enabled = state.macAddress.isNotBlank(),
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
            state = StartUpState(),
            onEvent = {},
            navigateToScanner = {},
            navigateToRobot = {}
        )
    }
}