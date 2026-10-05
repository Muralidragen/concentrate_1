package com.concentrate.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concentrate.app.data.model.JeeSubject
import com.concentrate.app.ui.components.SubjectBadgeSelector
import com.concentrate.app.ui.theme.BorderSubtle
import com.concentrate.app.ui.theme.OledBlack
import com.concentrate.app.ui.theme.SurfaceDark
import com.concentrate.app.ui.theme.SurfaceElevated
import com.concentrate.app.ui.theme.TextMuted
import com.concentrate.app.ui.theme.TextPrimary
import com.concentrate.app.ui.theme.TextSecondary

data class FormulaItem(
    val title: String,
    val formula: String,
    val note: String
)

@Composable
fun FormulaVaultScreen() {
    var selectedSubject by remember { mutableStateOf(JeeSubject.PHYSICS) }

    val formulas = when (selectedSubject) {
        JeeSubject.PHYSICS -> listOf(
            FormulaItem("Center of Mass (Continuous)", "R_cm = (1/M) ∫ r dm", "Integral form for non-uniform rods/plates"),
            FormulaItem("Rotational Kinetic Energy", "K_rot = 1/2 I ω²", "I is moment of inertia about axis of rotation"),
            FormulaItem("Simple Harmonic Motion", "ω = √(k/m),  T = 2π √(m/k)", "Differential equation: d²x/dt² + ω²x = 0"),
            FormulaItem("Gauss's Law", "∮ E · dA = Q_enclosed / ε₀", "Valid for any closed Gaussian surface"),
            FormulaItem("LC Oscillations", "f = 1 / (2π √(LC))", "Total energy U = 1/2 LI² + 1/2 Q²/C = constant")
        )
        JeeSubject.CHEMISTRY -> listOf(
            FormulaItem("Gibbs Free Energy", "ΔG = ΔH - TΔS", "Spontaneous if ΔG < 0 at constant T, P"),
            FormulaItem("Arrhenius Equation", "k = A · e^(-Ea / RT)", "ln(k2/k1) = (Ea/R) [1/T1 - 1/T2]"),
            FormulaItem("Nernst Equation", "E_cell = E°_cell - (0.0591/n) log Q", "Calculates EMF at non-standard 298K conditions"),
            FormulaItem("Bragg's Law (Solid State)", "n λ = 2 d sin θ", "Constructive interference of X-rays in crystals"),
            FormulaItem("Raoult's Law", "P_total = P°_A · X_A + P°_B · X_B", "Ideal solution vapor pressure")
        )
        JeeSubject.MATHEMATICS -> listOf(
            FormulaItem("Leibnitz Rule", "d/dx ∫[u(x) to v(x)] f(t) dt = f(v) v' - f(u) u'", "Differentiating under the integral sign"),
            FormulaItem("Euler's Identity & De Moivre", "(cos θ + i sin θ)^n = cos(nθ) + i sin(nθ)", "e^(i θ) = cos θ + i sin θ"),
            FormulaItem("Bayes' Theorem", "P(A|B) = [P(B|A) P(A)] / P(B)", "Conditional probability updating"),
            FormulaItem("Conic Eccentricity", "Ellipse: e = √(1 - b²/a²),  Hyperbola: e = √(1 + b²/a²)", "Standard forms with a > b"),
            FormulaItem("Integration by Parts", "∫ u v dx = u ∫ v dx - ∫ [u' (∫ v dx)] dx", "ILATE rule prioritization")
        )
        JeeSubject.MOCK_TEST -> listOf(
            FormulaItem("JEE Marking Scheme", "+4 Correct, -1 Incorrect, 0 Unattempted", "Negative marking penalty requires high accuracy"),
            FormulaItem("Optimal Time Split", "Chem: 40-45m, Phys: 60m, Maths: 75m", "Standard NTA exam 180m allocation strategy")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack)
            .padding(top = 16.dp, bottom = 80.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "FORMULA VAULT",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Distraction-free offline revision inside The Vault",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        item {
            SubjectBadgeSelector(
                selectedSubject = selectedSubject,
                onSubjectSelected = { selectedSubject = it }
            )
        }

        item {
            Text(
                text = "${selectedSubject.displayName.uppercase()} FORMULAS & HANDBOOK",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(formulas) { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = item.title,
                        color = Color(selectedSubject.hexColor),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.formula,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.note,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
