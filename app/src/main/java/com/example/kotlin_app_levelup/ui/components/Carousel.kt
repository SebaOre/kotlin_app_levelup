package com.example.kotlin_app_levelup.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.kotlin_app_levelup.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Carousel() {
    val promos = listOf(
        R.drawable.promo_ps5,
        R.drawable.promo_mouse,
        R.drawable.promo_hyper
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { promos.size }
    )

    // Auto-slide cada 3 segundos
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % promos.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.clip(RoundedCornerShape(16.dp))
        ) { page ->
            Image(
                painter = painterResource(id = promos[page]),
                contentDescription = "Promoción ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Indicadores
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(promos.size) { index ->
                val color =
                    if (pagerState.currentPage == index) Color(0xFF39FF14)
                    else Color.DarkGray
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}
