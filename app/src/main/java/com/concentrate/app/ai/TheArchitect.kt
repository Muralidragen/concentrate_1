package com.concentrate.app.ai

import com.concentrate.app.data.model.AppRiskCategory
import com.concentrate.app.data.model.ArchitectAnalysis
import com.concentrate.app.data.model.InstalledApp
import com.concentrate.app.data.model.Wallet

object TheArchitect {

    private val knownDistractionKeywords = listOf(
        "instagram", "youtube", "tiktok", "snapchat", "reddit", "twitter", "x.com",
        "facebook", "netflix", "primevideo", "hotstar", "twitch", "discord",
        "game", "pubg", "freefire", "candycrush", "chess", "reels", "shorts"
    )

    private val knownProductivityKeywords = listOf(
        "calculator", "desmos", "formula", "ncert", "notes", "keep", "pdf",
        "camscanner", "adobe", "drive", "clock", "dictionary", "allen", "pw", "unacademy"
    )

    /**
     * Automatic heuristic classification of installed applications
     */
    fun classifyApp(packageName: String, appName: String): AppRiskCategory {
        val lowerPkg = packageName.lowercase()
        val lowerName = appName.lowercase()

        for (kw in knownDistractionKeywords) {
            if (lowerPkg.contains(kw) || lowerName.contains(kw)) {
                return AppRiskCategory.HIGH_RISK_DISTRACTION
            }
        }

        for (kw in knownProductivityKeywords) {
            if (lowerPkg.contains(kw) || lowerName.contains(kw)) {
                return AppRiskCategory.PRODUCTIVITY_TOOL
            }
        }

        return AppRiskCategory.NEUTRAL_SYSTEM
    }

    /**
     * Algorithmic Dynamic Inflation Pricing Engine
     * Scales cost of passes when study-to-break ratio deteriorates
     */
    fun calculateInflation(passesBoughtThisWeek: Int, problemsSolvedThisWeek: Int): Float {
        val ratio = passesBoughtThisWeek.toFloat() / (problemsSolvedThisWeek.coerceAtLeast(1) / 10f).coerceAtLeast(1f)
        return (1.0f + (ratio * 0.4f)).coerceIn(1.0f, 4.0f)
    }

    /**
     * Generates the Architect's Daily Accountability Briefing
     */
    fun generateDailyBriefing(wallet: Wallet, passesBought: Int): ArchitectAnalysis {
        val problems = wallet.totalProblemsSolved
        val streak = wallet.streakDays
        val minutes = wallet.totalFocusMinutes

        val inflation = calculateInflation(passesBought, problems)

        val briefing = when {
            streak >= 7 && problems >= 80 -> {
                "🔥 OUTSTANDING VELOCITY. You maintained a 7-day streak with $problems JEE problems logged. The Vault has forged peak discipline. Inflation remains low (1.0x). Keep the throttle down."
            }
            problems < 20 && passesBought >= 3 -> {
                "⚠️ CRITICAL RELAPSE WARNING. You solved only $problems problems while consuming $passesBought social media passes. You are trading your IIT rank for cheap dopamine. Pass prices have inflated by ${(inflation * 100 - 100).toInt()}%."
            }
            wallet.totalSlashedPoints > 0 -> {
                "⚡ DISCIPLINE BREACH DETECTED. You suffered a penalty slash of ${wallet.totalSlashedPoints} points. The Warden does not negotiate. Reset your mindset and reclaim your streak."
            }
            else -> {
                "🎯 CONSISTENCY REQUISITE. Focus: ${minutes / 60}h ${minutes % 60}m. Streak: $streak days. Output: $problems problems. Target the 180-min NTA Exam slot today to solidify your exam stamina."
            }
        }

        return ArchitectAnalysis(
            dailyBriefing = briefing,
            currentInflationMultiplier = inflation,
            passesBoughtThisWeek = passesBought,
            problemsSolvedThisWeek = problems,
            highRiskRelapseWindow = "21:30 - 23:00 (Post-Study Fatigue)",
            mostFrequentTriggerApp = "Shorts / Reels",
            studyToBreakRatio = if (passesBought > 0) (minutes / (passesBought * 15f)) else 10f
        )
    }
}
