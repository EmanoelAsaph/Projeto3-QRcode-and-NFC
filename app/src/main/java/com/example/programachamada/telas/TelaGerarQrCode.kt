package com.example.programachamada.telas

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun TelaGerarQrCode(controladorDeNavegacao: NavController, turma: String?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (turma != null) {
            Text(text = turma, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(turma, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            Image(bitmap = bitmap.asImageBitmap(), contentDescription = "QR Code")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { controladorDeNavegacao.popBackStack() }) {
                Text(text = "Fechar")
            }
        } else {
            Text(text = "Erro: Turma não especificada")
        }
    }
}