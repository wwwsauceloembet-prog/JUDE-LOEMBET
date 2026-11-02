package com.example.ai

import com.example.data.model.LessonPlan

object PedagogicalAiEngine {

    enum class AssistantType(val title: String, val subtitle: String) {
        LESSON_PREP("Préparation de leçon", "Fiche de préparation complète, déroulement minuté, matériel et synthèse"),
        EXERCISES("Générateur d'exercices", "Banque d'exercices progressifs (Niveaux 1, 2, 3) avec corrigé enseignant"),
        REMEDIATION("Atelier de remédiation", "Activités de soutien ciblées pour les élèves en difficulté"),
        REPORTS("Rédaction scolaire", "Bilans de fin de journée, appréciations de bulletins et mots aux parents")
    }

    val supportedCountries = listOf(
        "Gabon", "Sénégal", "Côte d'Ivoire", "Cameroun", "Bénin", "Togo", "Congo (Brazzaville)", "RDC", "Mali", "Burkina Faso"
    )

    val supportedLevels = listOf("CP1", "CP2", "CE1", "CE2", "CM1", "CM2")

    val supportedSubjects = listOf(
        "Français",
        "Mathématiques",
        "Éveil Scientifique",
        "Histoire-Géographie",
        "Éducation Civique & Morale",
        "Éducation Physique & Sportive",
        "Arts Plastiques"
    )

    data class AiGenerationResult(
        val title: String,
        val subject: String,
        val gradeLevel: String,
        val content: String,
        val lessonPlanTemplate: LessonPlan? = null
    )

    fun generateContent(
        type: AssistantType,
        country: String,
        level: String,
        subject: String,
        topic: String,
        classId: Long = 0,
        todayDate: String = "2026-11-02"
    ): AiGenerationResult {
        return when (type) {
            AssistantType.LESSON_PREP -> generateLessonPlan(country, level, subject, topic, classId, todayDate)
            AssistantType.EXERCISES -> generateExercises(country, level, subject, topic)
            AssistantType.REMEDIATION -> generateRemediation(country, level, subject, topic)
            AssistantType.REPORTS -> generateReportOrNote(country, level, subject, topic)
        }
    }

    private fun generateLessonPlan(
        country: String,
        level: String,
        subject: String,
        topic: String,
        classId: Long,
        todayDate: String
    ): AiGenerationResult {
        val cleanTopic = if (topic.isBlank()) "Leçon thématique" else topic

        val objectives = "À la fin de la séance de 45 minutes, l'élève de $level sera capable de comprendre la notion de '$cleanTopic', de l'appliquer dans des situations simples et de l'expliquer sur son ardoise."
        val prereq = "Connaissances préalables du niveau $level en $subject et utilisation usuelle du matériel scolaire."
        val materials = "Tableau noir avec quadrillage, craies blanche et couleur, ardoises individuelles, torchons, bâtonnets de comptage / matériel de récupération du milieu."
        
        val phase1 = "Mise en situation (5 min) : Le maître pose une devinette ou présente un exemple concret tiré de la vie quotidienne en milieu africain ($country). Éveil de la curiosité et recueil des représentations initiales des élèves."
        val phase2 = "Recherche & Manipulation (15 min) : Travail en petits groupes ou binômes. Les élèves manipulent ou analysent la situation au brouillon. Confrontation des réponses et argumentation guidée par l'enseignant."
        val phase3 = "Institutionnalisation & Pratique (15 min) : Formalisation au tableau par le maître. Trace écrite claire. Les élèves exécutent 3 applications directes sur leur ardoise avec correction immédiate."
        val phase4 = "Synthèse & Bilan (10 min) : Répétition de la règle essentielle par 3 élèves différents. Recopie soignée de la règle ou du schéma dans le cahier du jour."

        val eval = "2 questions ou exercices éclairs sur ardoise levée pour vérifier que 80% des élèves ont assimilé l'objectif."
        val remed = "Pour les élèves hésitants : travail guidé en atelier de remédiation avec ardoise quadrillée et tuteur élève."

        val formattedMarkdown = buildString {
            appendLine("### FICHE PÉDAGOGIQUE — CHIJU ASSISTANT")
            appendLine("**Discipline :** $subject | **Niveau :** $level | **Pays de référence :** $country")
            appendLine("**Titre de la leçon :** $cleanTopic")
            appendLine("**Durée :** 45 minutes | **Date :** $todayDate")
            appendLine()
            appendLine("#### 1. OBJECTIFS D'APPRENTISSAGE")
            appendLine(objectives)
            appendLine()
            appendLine("#### 2. PRÉREQUIS & MATÉRIEL")
            appendLine("- **Prérequis :** $prereq")
            appendLine("- **Matériel didactique :** $materials")
            appendLine()
            appendLine("#### 3. DÉROULEMENT CHRONOLOGIQUE")
            appendLine("1. **Découverte (5 min) :** $phase1")
            appendLine("2. **Recherche active (15 min) :** $phase2")
            appendLine("3. **Application sur ardoise (15 min) :** $phase3")
            appendLine("4. **Synthèse & Trace écrite (10 min) :** $phase4")
            appendLine()
            appendLine("#### 4. ÉVALUATION FORMATIVE & REMÉDIATION")
            appendLine("- **Évaluation rapide :** $eval")
            appendLine("- **Remédiation immédiate :** $remed")
        }

        val plan = LessonPlan(
            classId = classId,
            subject = subject,
            gradeLevel = level,
            title = "$subject : $cleanTopic",
            dateScheduled = todayDate,
            timeSlot = "08h30 - 09h15",
            durationMinutes = 45,
            objectives = objectives,
            prerequisites = prereq,
            materials = materials,
            phaseDiscovery = phase1,
            phaseActivity = phase2,
            phasePractice = phase3,
            phaseSynthesis = phase4,
            evaluation = eval,
            remediation = remed,
            actualSummary = "Fiche générée par l'Assistant IA CHIJU.",
            status = "PREVU",
            isAiGenerated = true
        )

        return AiGenerationResult(
            title = "$subject : $cleanTopic",
            subject = subject,
            gradeLevel = level,
            content = formattedMarkdown,
            lessonPlanTemplate = plan
        )
    }

    private fun generateExercises(
        country: String,
        level: String,
        subject: String,
        topic: String
    ): AiGenerationResult {
        val cleanTopic = if (topic.isBlank()) "Notion du programme" else topic

        val content = buildString {
            appendLine("### SÉRIE D'EXERCICES PROGRESSIFS")
            appendLine("**Matière :** $subject | **Classe :** $level | **Programme :** $country")
            appendLine("**Notion travaillée :** $cleanTopic")
            appendLine("---")
            appendLine()
            appendLine("#### NIVEAU 1 : DÉCOUVERTE & APPLICATION DIRECTE (Facile)")
            appendLine("1. Réponds par VRAI ou FAUX :")
            appendLine("   a) L'application de '$cleanTopic' dépend toujours du contexte de la consigne. [ ... ]")
            appendLine("   b) Dans la règle de base, il faut toujours vérifier les accords ou calculs. [ ... ]")
            appendLine("2. Relie chaque élément à sa définition ou son exemple correspondant.")
            appendLine()
            appendLine("#### NIVEAU 2 : CONSOLIDATION & MAÎTRISE (Intermédiaire)")
            appendLine("3. Complète les phrases ou équations suivantes :")
            appendLine("   a) Dans la classe de $level, lorsque nous étudions $cleanTopic, nous devons d'abord repérer [...]")
            appendLine("   b) Applique la formule ou la règle pour transformer les 3 cas donnés.")
            appendLine("4. Résous la situation problème suivante :")
            appendLine("   'À la coopérative scolaire, les élèves préparent un événement. En utilisant $cleanTopic, calcule ou justifie la solution attendue.'")
            appendLine()
            appendLine("#### NIVEAU 3 : DÉFI & PROLONGEMENT (Avancé)")
            appendLine("5. Trouve l'erreur glissée dans le texte ou le calcul ci-dessous et explique pourquoi elle fausse le résultat.")
            appendLine()
            appendLine("---")
            appendLine("#### CORRIGÉ POUR L'ENSEIGNANT (Confidentiel)")
            appendLine("- **Exercice 1 :** a) VRAI, b) VRAI.")
            appendLine("- **Exercice 2 :** Correspondance exacte selon le tableau de synthèse.")
            appendLine("- **Exercice 3 & 4 :** Vérifier que la démarche est justifiée avant d'attribuer la totalité des points.")
            appendLine("- **Exercice 5 :** L'erreur provient de la non-prise en compte de l'exception au pluriel ou de la retenue.")
        }

        return AiGenerationResult(
            title = "Exercices : $cleanTopic",
            subject = subject,
            gradeLevel = level,
            content = content
        )
    }

    private fun generateRemediation(
        country: String,
        level: String,
        subject: String,
        topic: String
    ): AiGenerationResult {
        val cleanTopic = if (topic.isBlank()) "Difficulté d'apprentissage" else topic

        val content = buildString {
            appendLine("### ATELIER DE REMÉDIATION PÉDAGOGIQUE CHIJU")
            appendLine("**Classe :** $level | **Matière :** $subject")
            appendLine("**Difficulté ciblée :** $cleanTopic")
            appendLine("---")
            appendLine()
            appendLine("#### 1. DIAGNOSTIC DE L'OBSTACLE")
            appendLine("L'élève hésite ou commet des erreurs sur '$cleanTopic' généralement en raison de :")
            appendLine("- Une surcharge cognitive au moment de passer de l'oral à l'écrit.")
            appendLine("- Une confusion entre la règle générale et les cas particuliers.")
            appendLine()
            appendLine("#### 2. ÉTAPE 1 : RETOUR À LA MANIPULATION (10 min)")
            appendLine("- Utiliser du matériel visuel et palpable : ardoise, jetons, couleurs différentes pour isoler l'élément clé.")
            appendLine("- Verbalisation : Faire expliquer à l'élève ce qu'il fait avec ses propres mots avant de poser la craie.")
            appendLine()
            appendLine("#### 3. ÉTAPE 2 : EXERCICES ÉCHAFAUDÉS (15 min)")
            appendLine("- Exercice à trous où seuls 2 choix simples sont proposés.")
            appendLine("- Valoriser chaque réussite immédiate par une validation positive du maître.")
            appendLine()
            appendLine("#### 4. CRITÈRE D'ACQUISITION")
            appendLine("L'élève a surmonté la difficulté s'il réussit 3 fois d'affilée sans aide extérieure sur son ardoise.")
        }

        return AiGenerationResult(
            title = "Remédiation : $cleanTopic",
            subject = subject,
            gradeLevel = level,
            content = content
        )
    }

    private fun generateReportOrNote(
        country: String,
        level: String,
        subject: String,
        topic: String
    ): AiGenerationResult {
        val prompt = if (topic.isBlank()) "Bilan de la journée" else topic

        val content = buildString {
            appendLine("### PROPOSITIONS DE RÉDACTION SCOLAIRE")
            appendLine("**Contexte :** $level — $country | **Sujet :** $prompt")
            appendLine("---")
            appendLine()
            appendLine("#### OPTION A : BILAN JOURNALIER DU MAÎTRE (Pour le Cahier Journal)")
            appendLine("'Journée studieuse. Les séances de Français et Mathématiques se sont déroulées conformément aux prévisions. " +
                    "Bonne participation globale des élèves lors de la manipulation. Quelques difficultés constatées sur $prompt " +
                    "qui feront l'objet d'un retour ciblé en début de matinée demain.'")
            appendLine()
            appendLine("#### OPTION B : MOT DANS LE CAHIER DE LIAISON (Aux Parents d'élèves)")
            appendLine("'Chers parents,\n" +
                    "Nous vous informons que les élèves de la classe de $level travaillent actuellement sur $prompt. " +
                    "Afin de consolider leurs acquis, merci de veiller à ce que votre enfant relise attentivement sa leçon " +
                    "et s'exerce 10 minutes chaque soir sur son ardoise. Cordialement, Le Maître de classe.'")
            appendLine()
            appendLine("#### OPTION C : APPRÉCIATION POUR BULLETIN SCOLAIRE")
            appendLine("'Élève attentif et régulier dans son travail personnel. Les notions abordées en $subject ($prompt) " +
                    "sont acquises avec satisfaction. Poursuivez ces efforts au prochain trimestre !'")
        }

        return AiGenerationResult(
            title = "Rédaction : $prompt",
            subject = subject,
            gradeLevel = level,
            content = content
        )
    }
}
