package com.esenla.vidframe.navdrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.esenla.vidframe.BoxTest
import com.esenla.vidframe.CardDemo
import com.esenla.vidframe.ListInternal
import com.esenla.vidframe.ListRootFoldersAndFiles
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object ListInternalRoute : NavKey

@Serializable
data object ListRootRoute : NavKey

@Serializable
data object BoxTestRoute: NavKey

@Serializable
data object CardDemoRoute : NavKey


@Composable
fun NavDrawerDemo(){

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentRoute: NavKey by remember { mutableStateOf(BoxTestRoute) }

//    val backStack = rememberNavBackStack(BoxTestRoute)
    val backStack = remember{ mutableStateListOf<NavKey>(BoxTestRoute) }

    ModalNavigationDrawer(

        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState()).padding(12.dp)
                ) {
                    // Header
                    Spacer(Modifier.height(12.dp))
                    Text("Nav Drawer Demo", style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()

                    //Section - 1
                    Text("Section 1", style = MaterialTheme.typography.titleMedium)

                    // Item 0
                    NavigationDrawerItem(
                        selected = currentRoute == CardDemoRoute,
                        onClick={
                            currentRoute = CardDemoRoute
                            backStack.add(CardDemoRoute)
                            scope.launch { drawerState.close() }
                        },
                        label = {Text("Card Demo")}
                    )

                    // Item 1
                    NavigationDrawerItem(
                        selected = currentRoute == ListInternalRoute,
                        onClick = {
                            currentRoute = ListInternalRoute
                            backStack.add(ListInternalRoute)
                            scope.launch { drawerState.close() }

                        },
                        label={Text("List Internal")},
                    )

                    // Item 2
                    NavigationDrawerItem(
                        selected = currentRoute == ListRootRoute,
                        onClick={
                            currentRoute = ListRootRoute
                            backStack.add(ListRootRoute)
                            scope.launch { drawerState.close() }
                        },
                        label = {Text("List Root -(File Manager)")}
                    )

                    // Item 3
                    NavigationDrawerItem(
                        selected = currentRoute == BoxTestRoute,
                        onClick={
                            currentRoute = BoxTestRoute
                            backStack.add(BoxTestRoute)
                            scope.launch { drawerState.close() }
                        },
                        label = {Text("Box Test")}
                    )

                    HorizontalDivider(Modifier.padding(vertical = 12.dp))

                    //Section - 2
                    Text("Section 2", style = MaterialTheme.typography.titleMedium)
                    // Item 4 - Setting
                    NavigationDrawerItem(
                        selected = false,
                        onClick={

                        },
                        label = {Text("Settings")},
                        icon = { Icon(Icons.Filled.Settings, "Settings") }
                    )

                    // Item 5 - User Profile
                    NavigationDrawerItem(
                        selected = false,
                        onClick={},
                        label = {Text("User Profile")},
                        icon = { Icon(Icons.Filled.Person, "User Profile") }
                    )

                } // Col
            } // end Sheet
        }, // end DrawContent
    ) {
        Box(Modifier.fillMaxSize().background(Color.DarkGray),
            Alignment.TopEnd){

            IconButton(modifier = Modifier.size(50.dp).offset(x=(-30).dp, 30.dp),
                onClick = {
                    scope.launch {
                        if(drawerState.isClosed) drawerState.open()
                        else drawerState.close()
                    }
                }) {
                Icon(Icons.Filled.Add, null)
            }
        }


        NavDisplay(
            backStack = backStack,
            onBack = {backStack.removeLastOrNull()},
            entryProvider = entryProvider {
                entry<ListRootRoute> {
                    ListRootFoldersAndFiles()
                }
                entry<ListInternalRoute>{
                    ListInternal()
                }
                entry<BoxTestRoute> {
                    BoxTest()
                }
                entry<CardDemoRoute> {
                    CardDemo()
                }
            }
        )
    } // end ModalNavigationDrawer

}