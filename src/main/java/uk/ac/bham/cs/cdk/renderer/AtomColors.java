/**
 * @file   AtomColors.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Tue Feb 17 22:14:55 2015
 * 
 * @brief  Singleton class mapping atoms to traditional model colors. 
 * 
 * 
 */

//
package uk.ac.bham.cs.cdk.renderer;

import java.util.HashMap;
import java.util.Map;

import java.util.HashMap;
import java.util.Map;
import java.awt.Color;
import org.openscience.cdk.interfaces.IAtom;

/**
 *
 */

public class AtomColors {
    
    private static volatile AtomColors instance = null;
    private static final Map<String, Color> atomMap;
    static
    {
        atomMap = new HashMap<String, Color>();
        // H is not white but a light gray.
        atomMap.put("H", new Color(191, 191, 191));
        atomMap.put("He", new Color(217, 255, 255));
        atomMap.put("Li", new Color(204, 128, 255));
        atomMap.put("Be", new Color(194, 255, 0));
        atomMap.put("B", new Color(255, 181, 181));
        atomMap.put("C", new Color(144, 144, 144));
        atomMap.put("N", new Color(48, 80, 248));
        atomMap.put("O", new Color(255, 13, 13));
        atomMap.put("F", new Color(144, 224, 80));
        atomMap.put("Ne", new Color(179, 227, 245));
        atomMap.put("Na", new Color(171, 92, 242));
        atomMap.put("Mg", new Color(138, 255, 0));
        atomMap.put("Al", new Color(191, 166, 166));
        atomMap.put("Si", new Color(240, 200, 160));
        atomMap.put("P", new Color(255, 128, 0));
        atomMap.put("S", new Color(255, 255, 48));
        atomMap.put("Cl", new Color(31, 240, 31));
        atomMap.put("Ar", new Color(128, 209, 227));
        atomMap.put("K", new Color(143, 64, 212));
        atomMap.put("Ca", new Color(61, 255, 0));
        atomMap.put("Sc", new Color(230, 230, 230));
        atomMap.put("Ti", new Color(191, 194, 199));
        atomMap.put("V", new Color(166, 166, 171));
        atomMap.put("Cr", new Color(138, 153, 199));
        atomMap.put("Mn", new Color(156, 122, 199));
        atomMap.put("Fe", new Color(224, 102, 51));
        atomMap.put("Co", new Color(240, 144, 160));
        atomMap.put("Ni", new Color(80, 208, 80));
        atomMap.put("Cu", new Color(200, 128, 51));
        atomMap.put("Zn", new Color(125, 128, 176));
        atomMap.put("Ga", new Color(194, 143, 143));
        atomMap.put("Ge", new Color(102, 143, 143));
        atomMap.put("As", new Color(189, 128, 227));
        atomMap.put("Se", new Color(255, 161, 0));
        atomMap.put("Br", new Color(166, 41, 41));
        atomMap.put("Kr", new Color(92, 184, 209));
        atomMap.put("Rb", new Color(112, 46, 176));
        atomMap.put("Sr", new Color(0, 255, 0));
        atomMap.put("Y", new Color(148, 255, 255));
        atomMap.put("Zr", new Color(148, 224, 224));
        atomMap.put("Nb", new Color(115, 194, 201));
        atomMap.put("Mo", new Color(84, 181, 181));
        atomMap.put("Tc", new Color(59, 158, 158));
        atomMap.put("Ru", new Color(36, 143, 143));
        atomMap.put("Rh", new Color(10, 125, 140));
        atomMap.put("Pd", new Color(0, 105, 133));
        atomMap.put("Ag", new Color(192, 192, 192));
        atomMap.put("Cd", new Color(255, 217, 143));
        atomMap.put("In", new Color(166, 117, 115));
        atomMap.put("Sn", new Color(102, 128, 128));
        atomMap.put("Sb", new Color(158, 99, 181));
        atomMap.put("Te", new Color(212, 122, 0));
        atomMap.put("I", new Color(148, 0, 148));
        atomMap.put("Xe", new Color(66, 158, 176));
        atomMap.put("Cs", new Color(87, 23, 143));
        atomMap.put("Ba", new Color(0, 201, 0));
        atomMap.put("La", new Color(112, 212, 255));
        atomMap.put("Ce", new Color(255, 255, 199));
        atomMap.put("Pr", new Color(217, 255, 199));
        atomMap.put("Nd", new Color(199, 255, 199));
        atomMap.put("Pm", new Color(163, 255, 199));
        atomMap.put("Sm", new Color(143, 255, 199));
        atomMap.put("Eu", new Color(97, 255, 199));
        atomMap.put("Gd", new Color(69, 255, 199));
        atomMap.put("Tb", new Color(48, 255, 199));
        atomMap.put("Dy", new Color(31, 255, 199));
        atomMap.put("Ho", new Color(0, 255, 156));
        atomMap.put("Er", new Color(0, 230, 117));
        atomMap.put("Tm", new Color(0, 212, 82));
        atomMap.put("Yb", new Color(0, 191, 56));
        atomMap.put("Lu", new Color(0, 171, 36));
        atomMap.put("Hf", new Color(77, 194, 255));
        atomMap.put("Ta", new Color(77, 166, 255));
        atomMap.put("W", new Color(33, 148, 214));
        atomMap.put("Re", new Color(38, 125, 171));
        atomMap.put("Os", new Color(38, 102, 150));
        atomMap.put("Ir", new Color(23, 84, 135));
        atomMap.put("Pt", new Color(208, 208, 224));
        atomMap.put("Au", new Color(255, 209, 35));
        atomMap.put("Hg", new Color(184, 184, 208));
        atomMap.put("Tl", new Color(166, 84, 77));
        atomMap.put("Pb", new Color(87, 89, 97));
        atomMap.put("Bi", new Color(158, 79, 181));
        atomMap.put("Po", new Color(171, 92, 0));
        atomMap.put("At", new Color(117, 79, 69));
        atomMap.put("Rn", new Color(66, 130, 150));
        atomMap.put("Fr", new Color(66, 0, 102));
        atomMap.put("Ra", new Color(0, 125, 0));
        atomMap.put("Ac", new Color(112, 171, 250));
        atomMap.put("Th", new Color(0, 186, 255));
        atomMap.put("Pa", new Color(0, 161, 255));
        atomMap.put("U", new Color(0, 143, 255));
        atomMap.put("Np", new Color(0, 128, 255));
        atomMap.put("Pu", new Color(0, 107, 255));
        atomMap.put("Am", new Color(84, 92, 242));
        atomMap.put("Cm", new Color(120, 92, 227));
        atomMap.put("Bk", new Color(138, 79, 227));
        atomMap.put("Cf", new Color(161, 54, 212));
        atomMap.put("Es", new Color(179, 31, 212));
        atomMap.put("Fm", new Color(179, 31, 186));
        atomMap.put("Md", new Color(179, 13, 166));
        atomMap.put("No", new Color(189, 13, 135));
        atomMap.put("Lr", new Color(199, 0, 102));
        atomMap.put("Rf", new Color(204, 0, 89));
        atomMap.put("Db", new Color(209, 0, 79));
        atomMap.put("Sg", new Color(217, 0, 69));
        atomMap.put("Bh", new Color(224, 0, 56));
        atomMap.put("Hs", new Color(230, 0, 46));
        atomMap.put("Mt", new Color(235, 0, 38));
    }

    protected AtomColors() {
    }

    public static AtomColors getInstance() {
        if (instance == null) {
            instance = new AtomColors();
        }
        return instance;
    }

    public static Color color(String name) {
        Color result = atomMap.get(name);
        if (result == null) {
            return new Color(0, 0, 0);
        }
        return result;
    }

    public static Color color(IAtom atom) {
        return color(atom.getSymbol());
    }

}
