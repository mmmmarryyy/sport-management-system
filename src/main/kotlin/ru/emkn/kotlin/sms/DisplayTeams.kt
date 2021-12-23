package ru.emkn.kotlin.sms

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

val topRowHeight = 30.dp
val separatorLineWidth = 1.dp

fun listOfTabs(phase: Int): List<String> =
    when (phase) {
        1 -> listOf("Команды", "Дистанции", "Группы", "Старт. прот.")
        2 -> listOf("Старт. прот.", "Дистанции")
        3 -> listOf("Результаты", "Общие")
        else -> emptyList()
    }

@Composable
fun eventDataOnScreen(eventData: MutableState<Event>) {
    Column {
        Text(eventData.value.name)
        Text(eventData.value.date.toString())
    }
}

@Composable
fun teamsDataOnScreen(teamList: SnapshotStateList<Team>,configurationFolder:String,groupList: SnapshotStateList<Group>,participantList: SnapshotStateList<ParticipantStart>) {
    Column {
        LazyScrollable(teamList, false, listOf(groupList,participantList))
        Button(onClick = {
            File("$configurationFolder/save/application").mkdirs();
            File("$configurationFolder/save/application").createNewFile();
            teamList.forEach {
                val teams = it.createCSVHeader()
                teams.addAll(it.createCSVStrings())
                csvWriter().writeAll(teams,
                    File("$configurationFolder/save/application/application_${it.name}.csv"),
                    append = false
                )
            }
        }) {
            Text("Save")
        }
    }
}



@Composable
fun distancesDataOnScreen(distanceList: SnapshotStateList<Distance>, configurationFolder:String, groupList: SnapshotStateList<Group>,participantList: SnapshotStateList<ParticipantStart>) {
    Column {
        LazyScrollable(distanceList,true,listOf(groupList,participantList))
        Button(onClick = {

            //Создать функции для сохранения различных видов структур и обработать возможные ошибки (Например: удалили дистанцию D4000, значит с группой, которая отвечает за эту дистанцию надо что-то сделать)
            // Возможное решение: находить все конфликты после попытки сохранить новые данные и если удалённая структура где-то использовалась предложить пользователю поменять эту структуру в этом месте или отказаться от изменений
            // Создать для каждой фазы отдельный класс, хранящий все данные фазы и через него проверять все конфликты после удаления
            // (Функция будет принимать класс, реализующий интерфейс Phase, внутри которого будет функция checkConflicts, а Phase будет реализован в Phase1 Phase2 Phase3)
            // Например функция distancesDataOnScreen может принимать как Phase1, так и Phase2, но конфликты в них могут возникнуть разные (или одинаковые, хрен его знает, 4 часа ночи. Я не буду думать, что там разное, а что одинаковое)
            // Выпадающий список с доступными дистанциями
            // Создать папку saves внутри папки переданной пользователем и проверить корректность работы программы. Если что-то работает не так, попросить пользователя внести необходимые правки.
            File("$configurationFolder/save/").mkdirs();
            File("$configurationFolder/save/distances.csv").createNewFile();

            val maxNumberOfPoints = try{ distanceList.maxOf { it.getPointsList().size }} catch (e:NoSuchElementException){0}
            val distances = try{mutableListOf(distanceList[0].createCSVHeader(maxNumberOfPoints))} catch (e:IndexOutOfBoundsException){
               mutableListOf( Distance("Any", DistanceTypeData(DistanceType.ALL_POINTS,100,true)).createCSVHeader(maxNumberOfPoints))}
            distanceList.forEach {
                distances.add(it.createCSVString(maxNumberOfPoints))
            }
            csvWriter().writeAll(distances,
            File("$configurationFolder/save/distances.csv"),
                append = false
        )
        }) {
            Text("Save")
        }
    }
}

@Composable
fun groupsDataOnScreen(groupList: SnapshotStateList<Group>, participantList:SnapshotStateList<ParticipantStart>) {
    Column {
        LazyScrollable(groupList,true, listOf(participantList))
        Button(onClick = {
            File("test.csv").createNewFile(); csvWriter().writeAll(
            listOf(groupList),
            File("test.csv")
        )
        }) {
            Text("Save")
        }
    }
}

@Composable
fun startProtocolsDataOnScreen(
    participantList: SnapshotStateList<ParticipantStart>, isDeletable: Boolean = true,groupList: SnapshotStateList<Group>
) {
    Column {
        LazyScrollable(participantList,isDeletable, listOf(groupList))
        Button(onClick = {
            File("test.csv").createNewFile(); csvWriter().writeAll(
            listOf(participantList),
            File("test.csv")
        )
        }) {
            Text("Save")
        }
    }
}

@Composable
fun passingOfControlPointsDataOnScreen() {

}

@Composable
fun resultsOnScreen() {

}

@Composable
fun PhaseChoice(path: MutableState<String>, phase: MutableState<Int>) {
    val phaseFromDropDown = remember { mutableStateOf(-1) }
    Column(modifier = Modifier.padding(10.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(modifier = Modifier.weight(4f)) { PathField(path) }
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                DropDownMenu(
                    phaseFromDropDown,
                    listOf(1, 2, 3),
                    "Phase"
                )
            }
        }
        Button(
            onClick = {/*TODO(отправить куда-то для проверки)*/ phase.value = phaseFromDropDown.value },
            modifier = Modifier.fillMaxWidth()
        )
        { Text(text = "Результаты") }
    }
}

@Composable
fun PhaseOneWindow(
    distanceList: SnapshotStateList<Distance>,
    groupList: SnapshotStateList<Group>,
    teamList: SnapshotStateList<Team>,
    eventData: MutableState<Event>,
    configurationFolder: String,
    participantList:SnapshotStateList<ParticipantStart>
) {
    val currentPhase = 1
    val buttonStates = remember { mutableStateOf(MutableList(listOfTabs(currentPhase).size) { it == 0 }) }
    Column {
        AllTopButtons(buttonStates, listOfTabs(currentPhase))
        eventDataOnScreen(eventData)
        when (buttonStates.value.indexOf(true)) {
            0 -> teamsDataOnScreen(teamList,configurationFolder,groupList,participantList)
            1 -> distancesDataOnScreen(distanceList,configurationFolder,groupList,participantList)
            2 -> groupsDataOnScreen(groupList,participantList)
            3 -> startProtocolsDataOnScreen(participantList,false,groupList)
        }
    }
}

@Composable
fun PhaseTwoWindow(
    distanceList: SnapshotStateList<Distance>,
    eventData: MutableState<Event>,
    participantList: SnapshotStateList<ParticipantStart>,
    configurationFolder: String
) {
    val currentPhase = 2
    val buttonStates = remember { mutableStateOf(MutableList(listOfTabs(currentPhase).size) { it == 0 }) }
    Column {
        AllTopButtons(buttonStates, listOfTabs(currentPhase))
        eventDataOnScreen(eventData)
        when (buttonStates.value.indexOf(true)) {
            0 -> startProtocolsDataOnScreen(participantList,true,SnapshotStateList())
            1 -> distancesDataOnScreen(distanceList,configurationFolder, SnapshotStateList(),participantList)
        }
    }
}

@Composable
fun PhaseThreeWindow() {
    val currentPhase = 3
    val buttonStates = remember { mutableStateOf(MutableList(listOfTabs(currentPhase).size) { it == 0 }) }
    Column {
        AllTopButtons(buttonStates, listOfTabs(currentPhase))
    }
}

fun main() = application {
    val phase = remember { mutableStateOf(-1) }
    val path = remember { mutableStateOf("csvFiles/configuration") }

    when (phase.value) {
        -1 -> {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Application",
                state = rememberWindowState(width = 600.dp, height = 400.dp)
            ) {
                PhaseChoice(path, phase)
            }
        }
        0 -> {
            val configFolder = path.value
            val controlPoints = mutableListOf<ControlPoint>()

            val distances = DistanceReader(configFolder).getDistances(controlPoints)
            val groups = GroupReader(configFolder).getGroups(distances, Phase.FIRST)
            val teams = TeamReader(configFolder).getTeams()

            val (name, date) = getNameAndDate(readFile(configFolder).walk().toList(), configFolder)
            val event = Event(name, date, groups, distances, teams)
            event.getDistanceList().forEach {
                event.setNumbersAndTime(event.getGroupsByDistance(it.value))
            }
            event.makeStartProtocols()
            generateCP(controlPoints, groups)

            Window(
                onCloseRequest = ::exitApplication,
                title = "Phase ${phase.value + 1}",
                state = rememberWindowState(width = if (phase.value == -1) 600.dp else 800.dp, height = 400.dp)
            ) {
                val distanceList = remember { distances.values.toMutableStateList() }
                val groupList = remember { groups.toMutableStateList() }
                val teamList = remember { teams.toMutableStateList() }
                val participantList = remember { groupList.flatMap { it.listParticipants }.toMutableStateList()  }
                val eventData = remember { mutableStateOf(event) }

                PhaseOneWindow(distanceList, groupList, teamList, eventData,configFolder,participantList)

            }

        }
        1 -> {
            val configFolder = path.value

            val distances = DistanceReader(configFolder).getDistances()

            val groups = GroupReader(configFolder).getGroups(distances, Phase.SECOND)
            StartProtocolParse(configFolder).getStartProtocolFolder(groups)
            val (name, date) = getNameAndDate(readFile(configFolder).walk().toList(), configFolder)
            val event = Event(name, date, groups, distances)
            val participantDistanceWithTime: Map<Int, List<ControlPointWithTime>> =
                ControlPointReader(configFolder).getPoints()
            setStatusForAllParticipants(groups, distances, participantDistanceWithTime)

            makeResultProtocols(groups)
            Window(
                onCloseRequest = ::exitApplication,
                title = "Phase ${phase.value + 1}",
                state = rememberWindowState(width = if (phase.value == -1) 600.dp else 800.dp, height = 400.dp)
            ) {
                val distanceList = remember { distances.values.toMutableStateList() }
                val participantList = remember { groups.flatMap { it.listParticipants }.toMutableStateList() }
                val eventData = remember { mutableStateOf(event) }

                PhaseTwoWindow(distanceList, eventData, participantList,configFolder)

                println(participantList.size)
            }
        }
        2 -> PhaseThreeWindow()
    }
}


@Composable
fun PathField(path: MutableState<String>) {
    var textState by remember { mutableStateOf("") }
    OutlinedTextField(
        value = textState,
        onValueChange = {
            textState = it
            path.value = it
        },
        modifier = Modifier.fillMaxWidth().padding(0.dp),
        singleLine = true,
        shape = RoundedCornerShape(5.dp)
    )
}

@Composable
fun DropDownMenu(indexOfChoice: MutableState<Int>, items: List<Any>, text: String) {
    var expanded by remember { mutableStateOf(false) }
    //Column
    Text(
        if (indexOfChoice.value >= 0) items[indexOfChoice.value].toString() else text,
        modifier = Modifier.border(3.dp, Color.Gray, RoundedCornerShape(6.dp))
            .clickable(onClick = { expanded/*.value*/ = true })
            .padding(top = 20.dp, start = 5.dp, end = 5.dp, bottom = 20.dp).fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = if (indexOfChoice.value >= 0) Color.Black else Color.LightGray
    )
    DropdownMenu(
        expanded = expanded/*.value*/,
        onDismissRequest = { expanded/*.value*/ = false },
        modifier = Modifier.padding(top = 5.dp).background(Color.White)
            .border(3.dp, Color.Gray, RoundedCornerShape(6.dp))
    ) {
        items.forEachIndexed { index, s ->
            DropdownMenuItem(onClick = {
                indexOfChoice.value = index
                expanded/*.value*/ = false
            }) {
                Text(s.toString())
            }
        }
    }
}

@Composable
fun AllTopButtons(buttonStates: MutableState<MutableList<Boolean>>, values: List<String>) {
    Row(
        modifier = Modifier.height(topRowHeight).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        values.forEachIndexed { index, s ->
            if (index != values.lastIndex) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    TopButton(
                        s,
                        index,
                        buttonStates
                    )
                }
                SeparatorLine()
            }
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            TopButton(
                values.last(),
                values.lastIndex,
                buttonStates
            )
        }
    }
}

@Composable
fun TopButton(text: String, index: Int, buttonStates: MutableState<MutableList<Boolean>>) {
    Box(
        modifier = Modifier.fillMaxSize()
            .clickable {
                buttonStates.value = MutableList(buttonStates.value.size) { it == index }
            }.background(color = if (buttonStates.value[index]) Color.White else Color.LightGray)
            .padding(start = 5.dp, end = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, textAlign = TextAlign.Center, color = Color.Black)
    }
}

@Composable
fun SeparatorLine() {
    Box(modifier = Modifier.fillMaxHeight().width(separatorLineWidth).background(Color.Black)) {}
}

@Composable
fun <T : Scrollable, E:Any> LazyScrollable(list: SnapshotStateList<T>, isDeletable:Boolean=true, toDelete: List<SnapshotStateList<out E>>) {

    Box(modifier = Modifier.fillMaxWidth().padding(10.dp).fillMaxHeight(.75f)) {
        val state = rememberLazyListState()

        LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state) {
            itemsIndexed(list) { index, it ->
                it.show(list, index, isDeletable,toDelete)
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}
