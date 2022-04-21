package com.blackpineapple.myapplication.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blackpineapple.myapplication.R
import com.blackpineapple.myapplication.viewModel.MyViewModel
import com.google.accompanist.appcompattheme.AppCompatTheme

class TrashActivity : ComponentActivity() {
    private val viewModel: MyViewModel = MyViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        var host = ""
        var port = 0
        if(extras != null) {
            host = extras.getString("host").toString()
            port = extras.getInt("port")
        }
        setContent {
            AppCompatTheme {
                TopLevel(viewModel = viewModel, host = host, port = port)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnect()
    }

    private fun hideKeyboard() {
        try {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        } catch (e: Exception) {
            Log.e("TrashActivity", "hideKeyboard: ", e)
        }
    }

    @Composable
    fun TopLevel(viewModel: MyViewModel = viewModel(), host: String, port: Int) {
        viewModel.connect(host, port)
        val isConnected by remember { viewModel.isConnectionEstablished }
        if (!isConnected) {
            Toast.makeText(this, stringResource(R.string.connection_error), Toast.LENGTH_SHORT).show()
            finish()
        }

        AnyName(viewModel)
    }

    @Composable
    fun AnyName(viewModel: MyViewModel = viewModel()) {
        val padding = 16.dp
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val capacity by remember { viewModel.trashCapacity }
            val quantity by remember { viewModel.trashFilled }
            val isButtonEnabled by remember { viewModel.isButtonEnabled }
            var cp by remember { mutableStateOf("") }
            val percentage: Float = if(capacity != 0) quantity.toFloat()/capacity else 0.0f

            TextField(
                value = cp,
                onValueChange = { cp = it }
            )
            Button(onClick = {
                viewModel.changeCapacity(cp.toInt())
                hideKeyboard()
            }) {
                Text(text = stringResource(R.string.change_capacity))
            }
            Spacer(Modifier.size(padding))
            Row {
                Text(text = stringResource(R.string.capacity))
                Text(text = capacity.toString())
            }
            Row {
                Text(text = stringResource(R.string.actual_trash))
                Text(text = quantity.toString())
            }
            Spacer(Modifier.size(padding))
            LinearProgressIndicator(progress = percentage)
            Spacer(Modifier.size(padding))
            Button(
                onClick = { viewModel.putTrash() },
                enabled = isButtonEnabled
            ) {
                Text(text = stringResource(R.string.put_trash))
            }
            Button(onClick = { viewModel.emptyTrash() }) {
                Text(text = stringResource(R.string.empty_can_bin))
            }
            Button(
                onClick = { viewModel.lockTrash() },
                enabled = isButtonEnabled
            ) {
                Text(text = stringResource(R.string.lock_can_bin))
            }
        }
    }

    @Preview
    @Composable
    fun PreviewAnyName() {
        TopLevel(host = "", port = 0)
    }
}