package com.example.farmeraid.market.add_edit_market.views

import SearchableExpandedDropDownMenuView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.market.add_edit_market.AddEditMarketViewModel
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.ButtonView
import com.example.farmeraid.uicomponents.models.UiComponentModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddEditMarketScreenView() {
    val viewModel = hiltViewModel<AddEditMarketViewModel>()
    val state by viewModel.state.collectAsState()

    val dropdownSearchMinSize = 4
    val numberFormat = NumberFormat.getNumberInstance(Locale.CANADA)
    numberFormat.maximumFractionDigits = 2
    numberFormat.minimumFractionDigits = 2

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (state.selectedMarket == null) "Add Market" else "Edit - ${state.selectedMarket!!.name}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }){
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "",
                            tint = WhiteContentColour,
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = PrimaryColour,
                    titleContentColor = WhiteContentColour,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = PrimaryColour,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(25.0.dp),
                    strokeWidth = 3.0.dp,
                )
            }
            else {
                TextField(
                    label = { Text(text = "Market Name") },
                    value = state.marketName,
                    onValueChange = { viewModel.setMarketName(it) },
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = PrimaryColour,
                        focusedIndicatorColor = PrimaryColour,
                        focusedLabelColor = PrimaryColour,
                        focusedSupportingTextColor = PrimaryColour,
                    ),
                    readOnly = state.selectedMarket != null,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(20.dp))
                Divider(modifier = Modifier.fillMaxWidth())

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(0.dp, 20.dp),
                ) {
                    items(state.produceRows, key = { it.id }) { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SearchableExpandedDropDownMenuView(
                                modifier = Modifier.weight(1.1f),
                                listOfItems = state.produce.toList().map { it.first },
                                selectedOption = row.produce,
                                dropdownItem = { produceName ->
                                    Text(produceName)
                                },
                                placeholder = {
                                    Text(
                                        fontSize = 13.5.sp,
                                        text = "Select Produce"
                                    )
                                },
                                enable = state.selectedMarket != null,
                                enableSearch = state.produce.size >= dropdownSearchMinSize,
                                color = PrimaryColour,
                                onDropDownItemSelected = { produceName ->
                                    viewModel.selectProduce(
                                        row.id,
                                        produceName
                                    )
                                },
                                fontSize = 13.5.sp,
                            )

                            Spacer(modifier = Modifier.width(20.dp))

                            TextField(
                                label = { Text(text = "Price of Produce") },
                                value = numberFormat.format(row.producePrice),
                                onValueChange = { viewModel.setProducePrice(row.id, it.toDoubleOrNull())},
                                colors = TextFieldDefaults.textFieldColors(
                                    cursorColor = PrimaryColour,
                                    focusedIndicatorColor = PrimaryColour,
                                    focusedLabelColor = PrimaryColour,
                                    focusedSupportingTextColor = PrimaryColour,
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(0.9f),
                            )

                            if (state.produceRows.size > 1) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { viewModel.removeProduceRow(row.id) },
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Remove Produce Row",
                                    tint = LightGrayColour,
                                )
                            }
                        }
                    }
                    item {
                        OutlinedIconButton(
                            onClick = { viewModel.addProduceRow() },
                            colors = IconButtonDefaults.outlinedIconButtonColors(
                                contentColor = PrimaryColour,
                            ),
                            border = BorderStroke(1.dp, PrimaryColour),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Produce Row in Market"
                            )
                        }
                    }
                }
                ButtonView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    buttonUiState = state.submitButtonUiState,
                    buttonUiEvent = UiComponentModel.ButtonUiEvent(
                        onClick = { viewModel.submitMarket() }
                    ),
                )
            }
        }
    }
}