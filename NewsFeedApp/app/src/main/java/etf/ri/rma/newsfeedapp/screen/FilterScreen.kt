package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private fun formatDate(dateMillis: Long?): String? {
    return dateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterScreen(navController: NavController) {
    val previousSavedStateHandle = navController.previousBackStackEntry?.savedStateHandle
    val categoryFromNewsFeed = previousSavedStateHandle?.get<String>("newsfeed_selected_category")
    previousSavedStateHandle?.remove<String>("newsfeed_selected_category")
    val categoryFromPreviousFilterRun = previousSavedStateHandle?.get<String>("selectedCategory")
    val initialCategory = categoryFromNewsFeed ?: categoryFromPreviousFilterRun ?: "Sve"
    val initialStartDateMillis = previousSavedStateHandle?.get<Long?>("startDateMillis")
    val initialEndDateMillis = previousSavedStateHandle?.get<Long?>("endDateMillis")
    val initialUnwantedWords = previousSavedStateHandle?.get<ArrayList<String>>("unwantedWords") ?: emptyList()
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    val datePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStartDateMillis,
        initialSelectedEndDateMillis = initialEndDateMillis
    )
    var showDatePicker by remember { mutableStateOf(false) }
    var unwantedInput by remember { mutableStateOf("") }
    val unwantedWordsList = remember { mutableStateListOf<String>(*initialUnwantedWords.toTypedArray()) }

    val dateRangeText = remember(datePickerState.selectedStartDateMillis, datePickerState.selectedEndDateMillis) {
        val start = formatDate(datePickerState.selectedStartDateMillis)
        val end = formatDate(datePickerState.selectedEndDateMillis)
        if (start != null && end != null) "$start; $end" else "Odaberite opseg datuma"
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Filteri") }) },
        bottomBar = {
            Button(
                onClick = {
                    val wordsToSave = ArrayList(unwantedWordsList)
                    previousSavedStateHandle?.set("selectedCategory", selectedCategory)
                    previousSavedStateHandle?.set("startDateMillis", datePickerState.selectedStartDateMillis)
                    previousSavedStateHandle?.set("endDateMillis", datePickerState.selectedEndDateMillis)
                    previousSavedStateHandle?.set("unwantedWords", wordsToSave)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("filter_apply_button")
            ) { Text("Primijeni filtere") }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Kategorija:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    val categories = listOf("Sve", "Politika", "Sport", "Nauka", "Tehnologija", "Ljepota i zdravlje")
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            modifier = Modifier.testTag(
                                when (category) {
                                    "Sve" -> "filter_chip_all"
                                    "Politika" -> "filter_chip_pol"
                                    "Sport" -> "filter_chip_spo"
                                    "Nauka" -> "filter_chip_sci"
                                    "Tehnologija" -> "filter_chip_tech"
                                    "Ljepota i zdravlje" -> "filter_chip_none"
                                    else -> ""
                                }
                            )
                        )
                    }
                }
            }
            item {
                Text("Datum objave:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = dateRangeText, modifier = Modifier.weight(1f).testTag("filter_daterange_display"), style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { showDatePicker = true }, modifier = Modifier.testTag("filter_daterange_button")) { Text("Odaberi") }
                }
            }
            item {
                Text("Nepoželjne riječi:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = unwantedInput, onValueChange = { unwantedInput = it }, label = { Text("Unesite riječ") }, modifier = Modifier.weight(1f).testTag("filter_unwanted_input"), singleLine = true)
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val trimmedInput = unwantedInput.trim()
                            if (trimmedInput.isNotBlank() && unwantedWordsList.none { it.equals(trimmedInput, ignoreCase = true) }) {
                                unwantedWordsList.add(trimmedInput)
                                unwantedInput = ""
                            }
                        },
                        modifier = Modifier.testTag("filter_unwanted_add_button"),
                        enabled = unwantedInput.isNotBlank()
                    ) { Text("Dodaj") }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .testTag("filter_unwanted_list")
                        .fillMaxWidth()
                        .heightIn(max = 100.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (unwantedWordsList.isEmpty()){
                        Text("Nema dodatih nepoželjnih riječi.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        unwantedWordsList.forEach { word ->
                            Text("$word", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("Potvrdi") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Otkaži") } }
        ) { DateRangePicker(state = datePickerState) }
    }
}