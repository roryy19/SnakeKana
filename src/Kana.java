import java.awt.Image;
import java.util.List;

import javax.swing.ImageIcon;

public class Kana {
    public final String romaji;
    public final Image image;

    public Kana(String romaji, Image image) {
        this.romaji = romaji;
        this.image = image;
    }

    public static final List<Kana> hiragana = List.of(
        new Kana("a", loadImage("a_hira.png")),
        new Kana("i", loadImage("i_hira.png")),
        new Kana("u", loadImage("u_hira.png")),
        new Kana("e", loadImage("e_hira.png")),
        new Kana("o", loadImage("o_hira.png")),
        new Kana("ka", loadImage("ka_hira.png")),
        new Kana("ki", loadImage("ki_hira.png")),
        new Kana("ku", loadImage("ku_hira.png")),
        new Kana("ke", loadImage("ke_hira.png")),
        new Kana("ko", loadImage("ko_hira.png")),
        new Kana("sa", loadImage("sa_hira.png")),
        new Kana("shi", loadImage("shi_hira.png")),
        new Kana("su", loadImage("su_hira.png")),
        new Kana("se", loadImage("se_hira.png")),
        new Kana("so", loadImage("so_hira.png")),
        new Kana("ta", loadImage("ta_hira.png")),
        new Kana("chi", loadImage("chi_hira.png")),
        new Kana("tsu", loadImage("tsu_hira.png")),
        new Kana("te", loadImage("te_hira.png")),
        new Kana("to", loadImage("to_hira.png")),
        new Kana("na", loadImage("na_hira.png")),
        new Kana("ni", loadImage("ni_hira.png")),
        new Kana("nu", loadImage("nu_hira.png")),
        new Kana("ne", loadImage("ne_hira.png")),
        new Kana("no", loadImage("no_hira.png")),
        new Kana("ha", loadImage("ha_hira.png")),
        new Kana("hi", loadImage("hi_hira.png")),
        new Kana("fu", loadImage("fu_hira.png")),
        new Kana("he", loadImage("he_hira.png")),
        new Kana("ho", loadImage("ho_hira.png")),
        new Kana("ma", loadImage("ma_hira.png")),
        new Kana("mi", loadImage("mi_hira.png")),
        new Kana("mu", loadImage("mu_hira.png")),
        new Kana("me", loadImage("me_hira.png")),
        new Kana("mo", loadImage("mo_hira.png")),
        new Kana("ya", loadImage("ya_hira.png")),
        new Kana("yu", loadImage("yu_hira.png")),
        new Kana("yo", loadImage("yo_hira.png")),
        new Kana("ra", loadImage("ra_hira.png")),
        new Kana("ri", loadImage("ri_hira.png")),
        new Kana("ru", loadImage("ru_hira.png")),
        new Kana("re", loadImage("re_hira.png")),
        new Kana("ro", loadImage("ro_hira.png")),
        new Kana("wa", loadImage("wa_hira.png")),
        new Kana("wo", loadImage("wo_hira.png")),
        new Kana("n", loadImage("n_hira.png"))
    );

    public static final List<Kana> katakana = List.of(
        new Kana("a", loadImage("a_kata.png")),
        new Kana("i", loadImage("i_kata.png")),
        new Kana("u", loadImage("u_kata.png")),
        new Kana("e", loadImage("e_kata.png")),
        new Kana("o", loadImage("o_kata.png")),
        new Kana("ka", loadImage("ka_kata.png")),
        new Kana("ki", loadImage("ki_kata.png")),
        new Kana("ku", loadImage("ku_kata.png")),
        new Kana("ke", loadImage("ke_kata.png")),
        new Kana("ko", loadImage("ko_kata.png")),
        new Kana("sa", loadImage("sa_kata.png")),
        new Kana("shi", loadImage("shi_kata.png")),
        new Kana("su", loadImage("su_kata.png")),
        new Kana("se", loadImage("se_kata.png")),
        new Kana("so", loadImage("so_kata.png")),
        new Kana("ta", loadImage("ta_kata.png")),
        new Kana("chi", loadImage("chi_kata.png")),
        new Kana("tsu", loadImage("tsu_kata.png")),
        new Kana("te", loadImage("te_kata.png")),
        new Kana("to", loadImage("to_kata.png")),
        new Kana("na", loadImage("na_kata.png")),
        new Kana("ni", loadImage("ni_kata.png")),
        new Kana("nu", loadImage("nu_kata.png")),
        new Kana("ne", loadImage("ne_kata.png")),
        new Kana("no", loadImage("no_kata.png")),
        new Kana("ha", loadImage("ha_kata.png")),
        new Kana("hi", loadImage("hi_kata.png")),
        new Kana("fu", loadImage("fu_kata.png")),
        new Kana("he", loadImage("he_kata.png")),
        new Kana("ho", loadImage("ho_kata.png")),
        new Kana("ma", loadImage("ma_kata.png")),
        new Kana("mi", loadImage("mi_kata.png")),
        new Kana("mu", loadImage("mu_kata.png")),
        new Kana("me", loadImage("me_kata.png")),
        new Kana("mo", loadImage("mo_kata.png")),
        new Kana("ya", loadImage("ya_kata.png")),
        new Kana("yu", loadImage("yu_kata.png")),
        new Kana("yo", loadImage("yo_kata.png")),
        new Kana("ra", loadImage("ra_kata.png")),
        new Kana("ri", loadImage("ri_kata.png")),
        new Kana("ru", loadImage("ru_kata.png")),
        new Kana("re", loadImage("re_kata.png")),
        new Kana("ro", loadImage("ro_kata.png")),
        new Kana("wa", loadImage("wa_kata.png")),
        new Kana("wo", loadImage("wo_kata.png")),
        new Kana("n", loadImage("n_kata.png"))
    );

    private static Image loadImage(String imageFileName) {
        return new ImageIcon(Kana.class.getResource("/res/kana/" + imageFileName)).getImage();
    }
}
