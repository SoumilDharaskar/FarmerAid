import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.market.sell_produce.SellProduceViewModel
import com.example.farmeraid.ui.theme.BlackColour
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.ButtonView
import com.example.farmeraid.uicomponents.IncrementListItemView
import com.example.farmeraid.uicomponents.models.UiComponentModel
import java.text.NumberFormat
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellProduceView() {

    val viewModel = hiltViewModel<SellProduceViewModel>()
    val state by viewModel.state.collectAsState()

    val numberFormat = NumberFormat.getCurrencyInstance(Locale.CANADA)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Sell - ${state.marketName}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }){
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = WhiteContentColour,
                        )
                    }
                },
                actions = {
                    if (viewModel.userIsAdmin()) {
                        IconButton(onClick = { viewModel.navigateToEditMarket(state.marketId) }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit ${state.marketName ?: "Unknown"} Market",
                                tint = WhiteContentColour,
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.navigateToTransactions() }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Navigate to Transactions",
                            tint = WhiteContentColour,
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = PrimaryColour,
                    titleContentColor = WhiteContentColour,
                    actionIconContentColor = WhiteContentColour,
                )
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp, 0.dp, 20.dp, 20.dp)
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
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp, 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    items(state.produceSellList) { produceSell ->

                        IncrementListItemView(
                            produceItem = UiComponentModel.IncrementListItemUiState(
                                title = produceSell.produceName,
                                price = produceSell.produceTotalPrice,
                                showPrice = true,
                                quantityPickerState = UiComponentModel.QuantityPickerUiState(
                                    count = produceSell.produceCount,
                                    limit = produceSell.produceInventory
                                ),
                                setQuantity = { count ->
                                    viewModel.setProduceCount(
                                        produceSell.produceName,
                                        count
                                    )
                                },
                                onIncrement = {
                                    viewModel.incrementProduceCount(produceSell.produceName)
                                },
                                onDecrement = { viewModel.decrementProduceCount(produceSell.produceName) },
                                progressBarUiState = UiComponentModel.ProgressBarUiState(
                                    text = if (produceSell.produceQuotaTotalGoal != -1) "Quota Progress: ${produceSell.produceQuotaCurrentProgress + produceSell.produceCount}/${produceSell.produceQuotaTotalGoal}" else "No Quota Available",
                                    progress = if (produceSell.produceQuotaTotalGoal != -1) produceSell.produceQuotaCurrentProgress.toFloat() / produceSell.produceQuotaTotalGoal else 0f,
                                    expectedProgress = if (produceSell.produceQuotaTotalGoal != -1) (produceSell.produceQuotaCurrentProgress.toFloat() + produceSell.produceCount) / produceSell.produceQuotaTotalGoal else 0f,
                                    fontSize = 14.sp,
                                    containerColor = PrimaryColour.copy(alpha = 0.2f),
                                    progressColor = PrimaryColour,
                                ),
                                showProgressBar = true,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Divider(modifier = Modifier.height(1.dp), color = LightGrayColour)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        text = "Total Earnings:",
                        fontWeight = FontWeight.Bold,
                        color = BlackColour,
                        fontSize = 16.sp
                    )

                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        text = numberFormat.format(viewModel.getTotalEarnings()),
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(modifier = Modifier.height(1.dp), color = LightGrayColour)
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ButtonView(
                        buttonUiState = state.submitButtonUiState,
                        buttonUiEvent = UiComponentModel.ButtonUiEvent(
                            onClick = { viewModel.submitSell() }),
                        modifier = Modifier
                            .weight(1f).height(50.dp)
                    )

                    IconButton(
                        modifier = Modifier.height(50.dp).width(50.dp).clip(CircleShape).background(
                            PrimaryColour),
                        onClick = { state.micFabUiEvent.onClick()  }) {
                        Icon(
                            imageVector = state.micFabUiState.icon,
                            contentDescription = state.micFabUiState.contentDescription,
                            tint = WhiteContentColour,
                        )
                    }
                }
            }
        }
    }
}