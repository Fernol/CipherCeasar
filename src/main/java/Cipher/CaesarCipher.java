package Cipher;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

public class CaesarCipher {
    private static final char[] ALPHABET = {'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й',
            'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т',
            'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы',
            'ь', 'э', 'ю', 'я',
            '.', ',', '«', '»', ':', '!', '?', '-', ' '};
    private static final double[] russianLettersProbabilities =
            {0.0801, 0.0159, 0.0454, 0.0170, 0.0298, 0.0845, 0.0094, 0.0165, 0.0735, 0.0121,
            0.0349, 0.0440, 0.0321, 0.0670, 0.1097, 0.0281, 0.0473, 0.0547, 0.0626,
            0.0262, 0.0026, 0.0097, 0.0048, 0.0144, 0.0073, 0.036, 0.0004, 0.0190,
            0.0174, 0.0032, 0.0064, 0.0201,
            0.0653, 0.0613, 0.0267, 0.0267, 0.0034, 0.0033, 0.0056, 0.0153, 0.2};
    public static final int ALPHABET_SIZE = ALPHABET.length;

    public static void encrypt(File fileInput, File fileOutput, int offset) {
        try(FileReader reader = new FileReader(fileInput);
            FileWriter writer = new FileWriter(fileOutput)) {
            while(reader.ready()) {
                char currentCharacter = (char) Character.toLowerCase(reader.read());
                int indexCurrentCharacter = contains(currentCharacter);
                if (indexCurrentCharacter > -1) {
                    int indexNewCharacter = (indexCurrentCharacter + offset) % ALPHABET_SIZE;
                    writer.append(ALPHABET[indexNewCharacter]);
                } else {
                    writer.append(currentCharacter);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            throw new RuntimeException();
        } catch (IOException e) {
            System.out.println("I/O problems!");
            throw new RuntimeException();
        }
    }

    public static List<Character> encrypt(File fileInput, int offset) {
        List<Character> encryptedList = new ArrayList<>();
        try(FileReader reader = new FileReader(fileInput)) {
            while(reader.ready()) {
                char currentCharacter = (char) Character.toLowerCase(reader.read());
                int indexCurrentCharacter = contains(currentCharacter);
                if (indexCurrentCharacter > -1) {
                    int indexNewCharacter = (indexCurrentCharacter + offset) % ALPHABET_SIZE;
                    encryptedList.add(ALPHABET[indexNewCharacter]);
                } else {
                    encryptedList.add(currentCharacter);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            throw new RuntimeException();
        } catch (IOException e) {
            System.out.println("I/O problems!");
            throw new RuntimeException();
        }
        return encryptedList;
    }

    public static void decrypt(File fileInput, File fileOutput, int offset) {
        encrypt(fileInput, fileOutput, (ALPHABET_SIZE - (offset % ALPHABET_SIZE)));
    }

    public static List<Character> decrypt(File fileInput, int offset) {
        return encrypt(fileInput, (ALPHABET_SIZE - (offset % ALPHABET_SIZE)));
    }

    public static void breakCipherAnalysis(File fileInput, File directoryOutput) {
        List<Double> chiSquares = new ArrayList<>(ALPHABET_SIZE);
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            List<Character> listDecrypted = decrypt(fileInput, i);
            double[] expectedLetterFrequencies = expectedLetterFrequencies(listDecrypted);
            long[] realLetterFrequencies = realLetterFrequencies(listDecrypted);
            chiSquares.add(new ChiSquareTest().chiSquare(expectedLetterFrequencies, realLetterFrequencies));
        }

        int minIndex1 = minSquares(chiSquares); chiSquares.remove(minIndex1);
        //int minIndex2 = minSquares(chiSquares); chiSquares.remove(minIndex2);
        //int minIndex3 = minSquares(chiSquares); chiSquares.remove(minIndex3);
        int[] minIndex = new int[] {minIndex1/*, //minIndex2, //minIndex3 */};
        for (int i = 0; i < 1; i++) {
            directoryOutput = new File(directoryOutput.getAbsolutePath() + "\\hackedAnalysis" + /*(i+1) + */ ".txt");
            try (FileWriter writer = new FileWriter(directoryOutput)) {
                List<Character> listDecrypted = decrypt(fileInput, minIndex[i]);
                for (Character character : listDecrypted) {
                    writer.append(character);
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
                throw new RuntimeException();
            } catch (IOException e) {
                System.out.println("I/O problems!");
                throw new RuntimeException();
            }
        }
    }

    public static void breakCipher(File fileInput, File fileOutput) {
        int maxSpaces = 0;
        int indexMaxSpaces = 0;
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            List<Character> listDecrypted = decrypt(fileInput, i);
            int countSpaces = 0;
            for (Character character : listDecrypted) {
                if (character.equals(' ')) {
                    countSpaces++;
                }
            }
            if(maxSpaces < countSpaces) {
                maxSpaces = countSpaces;
                indexMaxSpaces = i;
            }
        }

        try(FileWriter writer = new FileWriter(fileOutput)) {
            List<Character> listDecrypted = decrypt(fileInput, indexMaxSpaces);
            for (Character character : listDecrypted) {
                writer.append(character);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            throw new RuntimeException();
        } catch (IOException e) {
            System.out.println("I/O problems!");
            throw new RuntimeException();
        }
    }

    private static int minSquares(List<Double> chiSquares) {
        double min = chiSquares.get(0);
        int m_min = 0;
        for (int i = 1; i < chiSquares.size(); i++) {
            if (chiSquares.get(i) < min) { min = chiSquares.get(i); m_min = i;}
        }
        return m_min;
    }

    private static double[] expectedLetterFrequencies(List<Character> list) {
        double[] expectedLetterFrequencies = new double[ALPHABET_SIZE];
        for (int i = 0; i < expectedLetterFrequencies.length; i++) {
            expectedLetterFrequencies[i] = russianLettersProbabilities[i] * list.size();
        }
        return expectedLetterFrequencies;
    }

    private static long[] realLetterFrequencies(List<Character> list) {
        long[] realLetterFrequencies = new long[ALPHABET_SIZE];
        for (int i = 0; i < realLetterFrequencies.length; i++) {
            int countCharFrequencies = 0;
            for (Character character : list) {
                if (character.equals(ALPHABET[i])) countCharFrequencies++;
            }
            realLetterFrequencies[i] = countCharFrequencies;
        }
        return realLetterFrequencies;
    }

    private static int contains(char currentCharacter) {
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            if (currentCharacter == ALPHABET[i]) return i;
        }
        return -1;
    }
}


