package etf.ri.rma.newsfeedapp.data

import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.model.NewsItem

object NewsData {

    fun getAllNews(): List<NewsItem> {
        return listOf(
            NewsItem("1", "Fudbaler potpisao za novi klub", "Nakon visemjesecne povrede, reprezentativac se vratio na teren i potpisao za slavni evropski klub...", null, "Sport", false, "SportWorld", "2025-01-22"),
            NewsItem("2", "Političke promjene u državi", "Novoizabrani predsjenik najavljuje reforme koje ce promijeniti ekomoniju...", null, "Politika", true, "BalkanNews", "2025-02-23"),
            NewsItem("3", "Roboti u zdravstvu", "Bolnice sirom svijeta testiraju robote za pomoc pri operacijama...", null, "Nauka/tehnologija", false, "TechWorld", "2025-03-03"),
            NewsItem("4", "Usvojen novi zakon", "Novi zakon o obrazovanju izglasan je većinom glasova...", null, "Politika", false, "BalkanNews", "2025-04-08"),
            NewsItem("5", "Veliki derbi završen remijem", "Navijači su ostali bez pobjednika u najvažnijoj utakmici sezone...", null, "Sport", true, "SportNews", "2025-04-08"),
            NewsItem("6", "Politička kriza u regionu", "Pregovori propali, lideri najavili vanredne izbore...", null, "Politika", false, "PolitikaPlus", "2025-04-05"),
            NewsItem("7", "Bivši fudbaler pokrenuo školu fudbala", "Namjenjena djeci iz ruralnih područja....", null, "Sport", false, "SportsFuture", "2025-04-01"),
            NewsItem("8", "Bokserski spektakl u Beogradskoj Areni", "Veče borbi privuklo hiljade gledalaca i vrhunske borce...", null, "Sport", false, "FightNews", "2025-04-05"),
            NewsItem("9", "Ambasador SAD u posjeti Sarajevu", "Razgovori o bilateralnim odnosima i investicijama...", null, "Politika", false, "DiplomaticNews", "2025-04-07"),
            NewsItem("10", "Fudbalski savez najavio reformu lige", "Sistem takmičenja biće znatno promijenjen naredne sezone...", null, "Sport", true, "SportReform", "2025-04-06"),
            NewsItem("11", "Povećanje plata u javnom sektoru", "Predsjednik Vlade najavio povećanje od 12%.", null, "Politika", true, "EkonomskeVesti", "2025-04-01"),
            NewsItem("12", "Novi softver predviđa zemljotrese", "Model koristi seizmičke podatke i vještačku inteligenciju...", null, "Nauka/tehnologija", false, "SeismoAI", "2025-04-04"),
            NewsItem("13", "Biotehnološki startup otkrio novi lijek za rijetku bolest", "Terapija uspješno testirana na prvih 20 pacijenata.", null, "Nauka/tehnologija", false, "BioScience", "2025-04-07"),
            NewsItem("14", "Zmajevi u prijateljskom meču protiv Hrvatske", "Igrači su optimistični pred derbi susret...", null, "Sport", false, "FudbalBiH", "2025-04-08"),
            NewsItem("15", "Predsjednica održala sastanak sa međunarodnim ambasadorima", "Diskutovalo se o ekonomskim reformama i bezbjednosnim pitanjima...", null, "Politika", false, "DiplomacijaBiH", "2025-04-08"),
            NewsItem("16", "Studenti iz BiH osvajaju nagrade na međunarodnim takmičenjima u programiranju", "Tim iz Sarajeva osvojio prvo mesto na prestižnom takmičenju u Amsterdamu...", null, "Nauka/tehnologija", false, "TechStudentsBiH", "2025-04-03"),
            NewsItem("17", "Sarajevo domaćin međunarodne konferencije o vještačkoj inteligenciji", "Konsultacije sa ekspertima o etici i bezbjednosti AI...", null, "Nauka/tehnologija", true, "AIForumBiH", "2025-04-05"),
            NewsItem("18", "BiH najavila izgradnju novih puteva i mostova", "Cilj je povezanost svih krajeva zemlje...", null, "Politika", false, "InfrastrukturaBiH", "2025-04-05"),
            NewsItem("19", "Bosna i Hercegovina domaćin međunarodnog turnira u odbojci", "Najbolje odbojkašice iz regiona dolaze u Sarajevo...", null, "Sport", false, "OdbojkaBiH", "2025-04-06"),
            NewsItem("20", "Parlament BiH diskutuje o zakonima o zaštiti životne sredine", "Planiraju se novi standardi za reciklažu i smanjenje emisija CO2...", null, "Politika", true, "EkologijaBiH", "2025-04-05"),
            NewsItem("21", "Tesla razvija novu generaciju električnih automobila", "Novi model dolazi sa poboljšanjima u autonomnoj vožnji i energetskoj efikasnosti...", null, "Nauka/tehnologija", false, "Tesla", "2025-04-09"),
            NewsItem("22", "Chelsea prelazi u novu fazu izgradnje Stamford Bridgea", "Chelsea najavljuje završetak prve faze rekonstrukcije svog stadiona u Londonu, što će povećati kapacitet i poboljšati iskustvo za navijače...", null, "Sport", true, "ChelseaFC", "2025-04-05")
            )
    }
}