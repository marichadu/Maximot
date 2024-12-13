//Importe les classes nécessaires
import java.io.File;
import java.util.Random;
import java.util.Scanner;

public class maximot {
 // Déclaration des variables statiques
 private static final Scanner scanner = new Scanner(System.in);  // Scanner pour lire l'entrée utilisateur
 private static final Random random = new Random();  // Générateur de nombres aléatoires
 private static final String[] WORDS = loadDictionary();  // Charge le dictionnaire de mots
 private static final int MAX_TRIES = 5;  // Nombre maximum d'essais
 private static final int INITIAL_POINTS = 50;  // Points initiaux pour le mode avancé
 private static int currentPoints = INITIAL_POINTS;  // Points actuels du joueur

 private static String[] loadDictionary() {
     try {
         // Ouvre le fichier dictionnaire
         Scanner fileScanner = new Scanner(new File("dictionnaire.txt"), "UTF-8");
         StringBuilder words = new StringBuilder();
         // Lit chaque ligne du fichier
         while (fileScanner.hasNextLine()) {
             String line = fileScanner.nextLine().trim();
             if (!line.isEmpty()) {
                 // Ajoute le mot en minuscules au StringBuilder
                 words.append(line.toLowerCase()).append(",");
             }
         }
         fileScanner.close();  // Ferme le scanner de fichier
         return words.toString().split(",");  // Retourne un tableau de mots
     } catch (Exception e) {
         // Gère les erreurs lors du chargement du dictionnaire
         System.err.println("Error loading dictionary: " + e.getMessage());
         System.exit(1);  // Quitte le programme en cas d'erreur
         return new String[0];  // Retourne un tableau vide (ne sera jamais atteint)
     }
 }

 private static char[] getRandomWord() {
     // Sélectionne un mot aléatoire du dictionnaire et le convertit en tableau de caractères
     return WORDS[random.nextInt(WORDS.length)].toCharArray();
 }

 public static void main(String[] args) {
     System.out.println("Bienvenue dans le jeu Maximot!");
     boolean continueGame = true;

     while (continueGame) {
         // Affiche le menu principal
         System.out.println("\nChoisissez votre niveau:");
         System.out.println("1. Essentiel (1 essai)");
         System.out.println("2. Attendu (5 essais)");
         System.out.println("3. Avancé (avec points)");
         System.out.println("4. Quitter");

         int choice = getIntInput("Votre choix : ");  // Obtient le choix de l'utilisateur

         // Exécute le mode de jeu choisi
         switch (choice) {
             case 1:
                 playEssentiel();
                 break;
             case 2:
                 playAttendu();
                 break;
             case 3:
                 playAvance();
                 break;
             case 4:
                 continueGame = false;  // Quitte le jeu
                 break;
             default:
                 System.out.println("Choix invalide! Veuillez choisir entre 1 et 4.");
         }

         if (continueGame) {
             // Demande si le joueur veut rejouer
             continueGame = getYesNoInput("\nVoulez-vous jouer à nouveau? (o/n) : ");
         }
     }

     System.out.println("Merci d'avoir joué à Maximot!");
     scanner.close();  // Ferme le scanner
 }

 private static void playEssentiel() {
     char[] originalWord = getRandomWord();  // Obtient un mot aléatoire
     char[] scrambledWord = scramble(originalWord);  // Mélange le mot

     System.out.println("\nVoici le tirage :");
     displayWord(scrambledWord);  // Affiche le mot mélangé

     System.out.print("Quel est le mot caché dans ce tirage ? ");
     char[] guess = scanner.nextLine().toLowerCase().toCharArray();  // Lit la proposition du joueur

     if (isCorrectLetters(scrambledWord, guess)) {  // Vérifie si les lettres utilisées sont correctes
         if (areIdentical(originalWord, guess)) {  // Vérifie si la proposition est correcte
             System.out.println("GAGNE - Vous avez trouvé le mot!");
         } else {
             System.out.println("PERDU - Ce n'est pas le bon mot.");
         }
     } else {
         System.out.println("PERDU - Vous avez utilisé des lettres non disponibles!");
     }

     System.out.println("Le mot original était: " + new String(originalWord));  // Révèle le mot original
 }

 private static void playAttendu() {
     char[] originalWord = getRandomWord();  // Obtient un mot aléatoire
     char[] scrambledWord = scramble(originalWord);  // Mélange le mot
     int remainingTries = MAX_TRIES;  // Initialise le nombre d'essais restants

     System.out.println("\nVoici le tirage :");
     displayWord(scrambledWord);  // Affiche le mot mélangé

     for (int tries = 1; tries <= MAX_TRIES; tries++) {
         System.out.print("Quel est le mot caché dans ce tirage ? ");
         char[] guess = scanner.nextLine().toLowerCase().toCharArray();  // Lit la proposition du joueur

         if (!isCorrectLetters(scrambledWord, guess)) {  // Vérifie si les lettres utilisées sont correctes
             System.out.println("Lettre incorrecte ! - Il vous reste " + (--remainingTries) + " essai(s).");
             continue;  // Passe à l'itération suivante
         }

         if (areIdentical(originalWord, guess)) {  // Vérifie si la proposition est correcte
             System.out.println("GAGNE - vous avez trouvé en " + tries + " essai(s), le mot " + new String(originalWord));
             return;  // Termine la méthode
         } else {
             System.out.println("Ce n'est pas le mot à trouver - Il vous reste " + (--remainingTries) + " essai(s).");
         }
     }

     System.out.println("PERDU - le mot à trouver était " + new String(originalWord));  // Révèle le mot si perdu
 }

 private static void playAvance() {
     while (currentPoints > 0) {  // Continue tant que le joueur a des points
         char[] originalWord = getRandomWord();  // Obtient un mot aléatoire
         char[] scrambledWord = scramble(originalWord);  // Mélange le mot
         int remainingTries = MAX_TRIES;  // Initialise le nombre d'essais restants
         boolean wordFound = false;  // Indique si le mot a été trouvé

         System.out.println("\nVoici le tirage :");
         displayWord(scrambledWord);  // Affiche le mot mélangé

         for (int tries = 1; tries <= MAX_TRIES && !wordFound; tries++) {
             System.out.print("Quel est le mot caché dans ce tirage ? ");
             char[] guess = scanner.nextLine().toLowerCase().toCharArray();  // Lit la proposition du joueur

             if (!isCorrectLetters(scrambledWord, guess)) {  // Vérifie si les lettres utilisées sont correctes
                 System.out.println("Lettre incorrecte ! - Il vous reste " + (--remainingTries) + " essai(s).");
                 continue;  // Passe à l'itération suivante
             }

             if (areIdentical(originalWord, guess)) {  // Vérifie si la proposition est correcte
                 wordFound = true;
                 System.out.println("GAGNE - vous avez trouvé en " + tries + " essai(s), le mot " + new String(originalWord));
                 currentPoints += originalWord.length;  // Ajoute des points
             } else {
                 System.out.println("Ce n'est pas le mot à trouver - Il vous reste " + (--remainingTries) + " essai(s).");
             }
         }

         if (!wordFound) {
             System.out.println("PERDU - le mot à trouver était " + new String(originalWord));
             currentPoints -= originalWord.length;  // Soustrait des points
         }

         System.out.println("Votre solde est de " + currentPoints + " points");

         if (currentPoints <= 0) {
             System.out.println("Vous n'avez plus de points !");
             break;  // Termine la boucle si plus de points
         }

         if (!getYesNoInput("Voulez-vous rejouer (o/n) ? ")) {
             break;  // Termine la boucle si le joueur ne veut pas continuer
         }
     }

     // Affiche le résultat final
     int pointsDifference = currentPoints - INITIAL_POINTS;
     if (pointsDifference > 0) {
         System.out.println("Vous avez gagné " + pointsDifference + " points");
     } else if (pointsDifference < 0) {
         System.out.println("Vous avez perdu " + (-pointsDifference) + " points");
     } else {
         System.out.println("Vous n'avez ni gagné ni perdu de points");
     }
 }

 private static char[] scramble(char[] word) {
     char[] scrambled = word.clone();  // Crée une copie du mot original
     for (int i = scrambled.length - 1; i > 0; i--) {
         int j = random.nextInt(i + 1);  // Choisit un index aléatoire
         // Échange les caractères aux positions i et j
         char temp = scrambled[i];
         scrambled[i] = scrambled[j];
         scrambled[j] = temp;
     }
     return scrambled;  // Retourne le mot mélangé
 }

 private static void displayWord(char[] word) {
     for (char c : word) {
         System.out.print(c + " ");  // Affiche chaque caractère suivi d'un espace
     }
     System.out.println();  // Passe à la ligne suivante
 }

 private static boolean isCorrectLetters(char[] scrambledWord, char[] guess) {
     int[] count = new int[256];  // Tableau pour compter les occurrences de chaque caractère ASCII étendu
     
     // Compte les occurrences dans le mot mélangé
     for (char c : scrambledWord) {
         count[c]++;
     }
     
     // Vérifie la proposition contre les lettres disponibles
     for (char c : guess) {
         if (--count[c] < 0) {
             return false;  // Retourne faux si une lettre non disponible est utilisée
         }
     }
     return true;  // Toutes les lettres sont correctes
 }

 private static boolean areIdentical(char[] word1, char[] word2) {
     if (word1.length != word2.length) return false;  // Vérifie si les longueurs sont identiques
     for (int i = 0; i < word1.length; i++) {
         if (word1[i] != word2[i]) return false;  // Vérifie si chaque caractère est identique
     }
     return true;  // Les mots sont identiques
 }

 private static int getIntInput(String prompt) {
     while (true) {
         System.out.print(prompt);
         try {
             return Integer.parseInt(scanner.nextLine());  // Tente de convertir l'entrée en entier
         } catch (NumberFormatException e) {
             System.out.println("Entrée invalide! Veuillez entrer un nombre.");
         }
     }
 }

 private static boolean getYesNoInput(String prompt) {
     while (true) {
         System.out.print(prompt);
         String response = scanner.nextLine().toLowerCase();
         if (response.equals("o")) return true;  // Retourne vrai pour "oui"
         if (response.equals("n")) return false;  // Retourne faux pour "non"
         System.out.println("Réponse invalide! Veuillez répondre par 'o' ou 'n'.");
     }
 }
}