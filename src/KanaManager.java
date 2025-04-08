import java.util.Random;

public class KanaManager {
    private boolean chooseHiragana;
    private boolean chooseKatakana;
    private Random random;

    public KanaManager(String mode) {
        this.random = new Random();
        setKanaMode(mode);
    }

    public void setKanaMode(String mode) {
        this.chooseHiragana = mode.equals("Hiragana") || mode.equals("Both");
        this.chooseKatakana = mode.equals("Katakana") || mode.equals("Both");
    }

    public Kana randomKana() {
        if (chooseHiragana && chooseKatakana) { // hiragana and katakana
            int choice = random.nextInt(2); // 0 = hiragana, 1 = katakana
            if (choice == 0)  return Kana.hiragana.get(random.nextInt(Kana.hiragana.size())); // hiragana
            else return Kana.katakana.get(random.nextInt(Kana.katakana.size())); // katakana
        } 
        else if (chooseKatakana) { // only katakana
            return Kana.katakana.get(random.nextInt(Kana.katakana.size()));
        }
        else { // only hiragana
            return Kana.hiragana.get(random.nextInt(Kana.hiragana.size()));
        }
    }

    public int totalPossibleKana() {
        if (chooseHiragana && chooseKatakana) return Kana.hiragana.size() + Kana.katakana.size();
        else if (chooseHiragana) return Kana.hiragana.size();
        else return Kana.katakana.size();
    }
}
