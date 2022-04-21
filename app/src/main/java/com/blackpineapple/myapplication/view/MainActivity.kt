package com.blackpineapple.myapplication.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blackpineapple.myapplication.R
import com.blackpineapple.myapplication.viewModel.MyViewModel
import com.google.accompanist.appcompattheme.AppCompatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme {
                AnyName()
            }
        }
    }

    @Composable
    fun AnyName(viewModel: MyViewModel = MyViewModel()) {
        val padding = 16.dp
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(stringResource(R.string.host_ip))
            var host by remember { mutableStateOf("192.168.0.16") }
            TextField(
                value = host,
                onValueChange = { host = it }
            )
            Text(stringResource(R.string.host_port))
            var port by remember { mutableStateOf("50007") }
            TextField(
                value = port,
                onValueChange = { port = it }
            )
            Spacer(Modifier.size(padding))
            val context = LocalContext.current
            val intent = Intent(context, TrashActivity::class.java)
                .putExtra("host", host)
                .putExtra("port", port.toInt())

            Button(onClick = { context.startActivity(intent) }) {
                Text(stringResource(R.string.connect))
            }
        }
    }

    @Preview
    @Composable
    fun PreviewAnyName() {
        AnyName()
    }
}