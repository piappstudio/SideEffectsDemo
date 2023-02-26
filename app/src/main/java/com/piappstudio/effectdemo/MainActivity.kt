package com.piappstudio.effectdemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.piappstudio.effectdemo.ui.theme.EffectDemoTheme
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext
import kotlin.random.Random
import kotlin.random.nextInt


/**
 *
 * Key Term: An effect is a composable function that doesn't emit UI and causes side effects to run when a composition completes.
 *
 *  SideEffect: publish Compose state to non-compose code
 *
 *  LaunchedEffect: run suspend functions in the scope of a composable
 *
 *  rememberCoroutineScope: obtain a composition-aware scope to
 *   launch a coroutine outside a composable
 *
 *  rememberUpdatedState: reference a value in an effect that shouldn't restart if the value changes
 *
 *  DisposableEffect: effects that require cleanup
 *
 *  derivedStateOf: convert one or multiple state objects into another state
 *
 *  produceState: convert non-Compose state into Compose state
 *
 *  snapshotFlow: convert Compose's State into Flows
 * */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EffectDemoTheme {

                val navController  = rememberNavController()
                NavHost(navController = navController, startDestination = "vote") {
                    composable("vote") {
                        DisplayScreen(navController = navController)
                    }
                    composable ("result") {
                        ResultScreen(navController)
                    }
                }

            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

const val TAG = "TAGEffect"
@Composable
fun DisplayScreen(navController: NavController) {


    SideEffect {
        Log.d(TAG, "--- Side Effect ----")
    }
    LaunchedEffect(Unit) {
        Log.d(TAG, "**** LaunchedEffect **** ")
    }

    var votes = remember {
        mutableStateOf(0)
    }
   /*val isExceeded by remember  {
        Log.d(TAG, "Calculate")
        derivedStateOf { votes >4 }
    }*/
    val isExceeded by remember {
        derivedStateOf {

            votes.value >4
        }
    }

    Column {
        VotingScreen(vote = votes.value, onClickVote = {
            votes.value = votes.value+1
        }) {
            navController.navigate("result")
        }

        Text(text = "Voting count is exceed $isExceeded")


    }

}

@Composable
fun EndResult(isVotingDone:Boolean) {
    Log.d(TAG, "EndResult: $isVotingDone")
}

@Composable
fun JustProduceState() {
    val timer by produceState(initialValue = 0) {
        delay(1000)
        value++
    }
    Log.d(TAG, "timer: $timer")
}

@Composable
fun ResultScreen(navController: NavController) {
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),  verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Thank you voting!")
        Button(onClick = { navController.navigateUp() }) {
            Text(text = "Back")
        }
    }
}

@Composable
fun VotingScreen(vote:Int, onClickVote:()->Unit, onClickResult: ()->Unit) {
    val rememberScope = rememberCoroutineScope()
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),  verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Total Voting So far!")
        Text(text = "$vote", style = MaterialTheme.typography.headlineMedium)

        Button(onClick = {
            onClickVote.invoke()
            rememberScope.launch {
                longTask()
            }
        }) {
            Text(text = "Vote")
        }

        Button(onClick = { onClickResult.invoke() }) {
            Text(text = "Result")
        }

    }
}

suspend fun longTask() {
    val random = Random.nextInt(0,10000)
    Log.d(TAG, "Next random should be: $random")
    withContext(Dispatchers.IO) {
        Log.d(TAG, "Start LongTask")
        Thread.sleep(5000)
        Log.d(TAG, "End LongTask $random" )
    }

}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EffectDemoTheme {
        Greeting("Android")
    }
}