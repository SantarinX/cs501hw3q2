package cs501.hw3.q2

import android.content.res.XmlResourceParser
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cs501.hw3.q2.ui.theme.Q2Theme
import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

//Create a flashcard quiz app where questions and answers are loaded from an XML file.
//Load questions and answers from an XML file (res/xml/flashcards.xml).
//Use LazyRow to display flashcards that can be swiped horizontally.
//Clicking a flashcard should flip it to reveal the answer.
//Use coroutines to shuffle flashcards every 15 seconds.
//The flashcards.xml file should be in this format:
data class Flashcard(val question: String, val answer: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val flashcards = xMLParser(this)
            Q2Theme {
                Main(flashcards)
            }
        }
    }
}

@Composable
fun Main(flashcards:List<Flashcard> ) {
    var cards by remember { mutableStateOf(flashcards) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(15000L)
            cards = cards.shuffled()
        }
    }
    CardsLazyRow(cards)
}

@Composable
fun xMLParser(context: Context):List<Flashcard>{
    val flashcards = mutableListOf<Flashcard>()
    val xmlParser = context.resources.getXml(R.xml.flashcards)

    var question = ""
    var answer = ""
    var tag = ""

    while (xmlParser.eventType != XmlResourceParser.END_DOCUMENT) {
        when (xmlParser.eventType) {
            XmlResourceParser.START_TAG -> {
                tag = xmlParser.name
                if(xmlParser.name == "card"){
                    question = ""
                    answer = ""
                }
            }
            XmlResourceParser.TEXT -> {
                if(tag == "question"){
                    question = xmlParser.text
                }
                if(tag == "answer"){
                    answer = xmlParser.text
                }
            }
            XmlResourceParser.END_TAG -> {
                if(xmlParser.name == "card"){
                    flashcards.add(Flashcard(question, answer))
                }
                tag = ""
            }
        }
        xmlParser.next()
    }

    return flashcards
}

@Composable
fun CardsLazyRow(cards: List<Flashcard>) {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 25.dp)
    ) {
        items(
            items = cards,
        ) { card ->
            Box(
                modifier = Modifier.padding(9.dp),
                contentAlignment = Alignment.Center
            ) {
                FlashcardItem(card = card)
            }
        }
    }
}

@Composable
fun FlashcardItem(card: Flashcard) {
    var isFlipped by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .size(300.dp, 200.dp)
                .clickable { isFlipped = !isFlipped }
                .graphicsLayer {
                    alpha = if (!isFlipped) 1f else 0f
                }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = card.question,
                    modifier = Modifier.padding(19.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .size(300.dp, 200.dp)
                .clickable { isFlipped = !isFlipped }
                .graphicsLayer {
                    alpha = if (isFlipped) 1f else 0f
                }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = card.answer,
                    modifier = Modifier.padding(19.dp)
                )
            }
        }
    }
}