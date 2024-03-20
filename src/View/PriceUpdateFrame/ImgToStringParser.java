package View.PriceUpdateFrame;

import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ImgToStringParser {
    private static final String DATA_PATH = "C:/Users/God/Documents/IDEA code/DofusTools/Libs/Tess4J/tessdata";

    private static final Color BG_COLOR = Color.WHITE;
    private static final Color TXT_COLOR = Color.BLACK;

    private Tesseract tesseract;
    private BufferedImage imgThree;
    private BufferedImage imgFive;

    public ImgToStringParser() throws Exception {
        tesseract = new Tesseract();
        tesseract.setDatapath(DATA_PATH);
        tesseract.setPageSegMode(8);

        imgThree = ImageIO.read(new File("resources/large_three.png"));
        imgFive = ImageIO.read(new File("resources/large_five.png"));
    }

    public BufferedImage takeScreenshot() throws Exception {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return new Robot().createScreenCapture(screenRect);
    }

    public BufferedImage processImage(BufferedImage img, int cutout) {
        BufferedImage imgOut = getRegionAsImage(img, new Rectangle(img.getWidth(), img.getHeight()));

        for(int y = 0; y < imgOut.getHeight(); ++y) {
            for(int x = 0; x < imgOut.getWidth(); ++x) {
                if(imgOut.getRGB(x, y) <= new Color(cutout, cutout, cutout).getRGB()) {
                    imgOut.setRGB(x, y, BG_COLOR.getRGB());
                }
                else {
                    imgOut.setRGB(x, y, TXT_COLOR.getRGB());
                }
            }
        }

        return imgOut;
    }

    public String getTextFromImage(BufferedImage img, String whitelist) throws Exception {
        tesseract.setTessVariable("tessedit_char_whitelist", whitelist);
        return tesseract.doOCR(img).trim();
    }

    public String processEdgeCases(BufferedImage img, String str) throws Exception {
        String strOut = "";

        ArrayList<Rectangle> regions = (ArrayList<Rectangle>) tesseract.getSegmentedRegions(img, ITessAPI.TessPageIteratorLevel.RIL_SYMBOL);
        for(int i = 0; i < str.length(); ++i) {
            if(str.charAt(i) == '3') {
                if(isFullyCovered(getRegionAsImage(img, regions.get(i)), imgThree)) {
                    strOut += '3';
                }
                else {
                    strOut += '8';
                }
            }
            else if(str.charAt(i) == '5') {
                if(isFullyCovered(getRegionAsImage(img, regions.get(i)), imgFive)) {
                    strOut += '5';
                }
                else {
                    strOut += '6';
                }
            }
            else {
                strOut += str.charAt(i);
            }
        }

        return strOut;
    }

    private static BufferedImage getRegionAsImage(BufferedImage img, Rectangle rect) {
        BufferedImage imgOut = new BufferedImage(rect.width, rect.height, img.getType());
        Graphics2D graphics = imgOut.createGraphics();
        graphics.drawImage(img.getSubimage(rect.x, rect.y, rect.width, rect.height), null, 0, 0);

        return imgOut;
    }

    private static boolean isFullyCovered(BufferedImage imgCovered, BufferedImage imgCovering) {
        for(int y = 0; y < imgCovered.getHeight(); ++y) {
            for(int x = 0; x < imgCovered.getWidth(); ++x) {
                if(imgCovering.getRGB(x, y) == BG_COLOR.getRGB() && imgCovered.getRGB(x, y) != BG_COLOR.getRGB()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void saveNthCharacterImage(BufferedImage img, int n, String fileName) throws Exception {
        Rectangle region = tesseract.getSegmentedRegions(img, ITessAPI.TessPageIteratorLevel.RIL_SYMBOL).get(n);
        ImageIO.write(getRegionAsImage(img, region), "png", new File("resources/"+fileName+".png"));
    }
}
