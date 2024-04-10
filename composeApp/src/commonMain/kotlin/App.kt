import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deflatedpickle.degen.FourChan
import io.exoquery.kmp.pprint
import io.ktor.client.call.body
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview

import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    var data: FourChan.BoardList? = null
    runBlocking {
        data = FourChan.boards()
        println(pprint(data))
    }


    MaterialTheme {
        /*var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }*/

        DegenDrawer(data)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DegenDrawer(boardList: FourChan.BoardList?) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedBoard by remember {
        mutableStateOf(boardList?.boards?.firstOrNull { it.board == "lgbt" })
    }
    val gradientColors = listOf(Cyan, Blue, Magenta)

    // todo: make it reload
    var catalog: List<FourChan.Catalog> = listOf()
    runBlocking {
        catalog = FourChan.catalog(selectedBoard!!.board)
        println(pprint(catalog))
    }

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            Divider()

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                boardList?.boards?.let { boards ->
                    boards.groupBy { it.board[0] }.forEach { (c, b) ->
                        stickyHeader {
                            CharacterHeader(c)
                        }

                        items(b) {
                            DegenDrawerCard(it)
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar {
                    Text(
                        text = selectedBoard?.title ?: "",
                        /*style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        )*/
                    )
                }
            },
            bottomBar = {
                BottomNavigation {

                }
            },
            floatingActionButton = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Scroll to Top",
                )
            },
        ) { innerPadding ->
            LazyColumn {
                items(catalog[0].threads) {
                    DegenCard(it)
                }
            }
        }
    }
}

@Composable
fun CharacterHeader(char: Char) =
    Text(text = char.toString(), fontWeight = FontWeight.Bold)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DegenDrawerCard(board: FourChan.Board) =
    ListItem(
        modifier = Modifier.clickable {

        }
    ) {
        Text(text = board.board)
    }

// todo: betterer
@Composable
fun DegenCard(thread: FourChan.Post) =
    Card(
        modifier = Modifier
            .padding(8.dp, 4.dp)
            .clickable {

            }
            .fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = thread.now)
                Text(text = thread.no.toString())
            }

            thread.com?.let {
                Text(text = it, style = TextStyle())
            }
        }
    }