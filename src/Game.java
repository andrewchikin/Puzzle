import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game extends JFrame {
    private ArrayList<Point> solution;
    private ArrayList<PuzzleButton> buttons;
    private JPanel panel;
    private BufferedImage source;
    private BufferedImage resized;
    private int width, height;
    private Image image;
    private PuzzleButton lastButton;

    private final int DESIRED_WIDTH = 400;
    private final int NUMBER_OF_BUTTONS = 12;


    public Game() {
        initIU();
    }

    public void initIU() {
        solution = new ArrayList<>();
        solution.add(new Point(0, 0));
        solution.add(new Point(0, 1));
        solution.add(new Point(0, 2));
        solution.add(new Point(1, 0));
        solution.add(new Point(1, 1));
        solution.add(new Point(1, 2));
        solution.add(new Point(2, 0));
        solution.add(new Point(2, 1));
        solution.add(new Point(2, 2));
        solution.add(new Point(3, 0));
        solution.add(new Point(3, 1));
        solution.add(new Point(3, 2));

        buttons = new ArrayList<>();

        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 3));

        try {
            source = loadImage();
            int h = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizedImage(source, DESIRED_WIDTH, h, BufferedImage.TYPE_INT_ARGB);
        }
        catch (IOException e) {
            Logger.getLogger(Game.class.getName()).log(
                    Level.SEVERE, null, e);
        }

        width = resized.getWidth();
        height = resized.getHeight();

        add(panel, BorderLayout.CENTER);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                image = createImage(new FilteredImageSource(resized.getSource(),
                        new CropImageFilter(j * width / 3, i * height / 4, width / 3, height / 4)));
                PuzzleButton button = new PuzzleButton(image);
                button.putClientProperty("position", new Point(i, j));

                if (i == 3 && j == 2) {
                    lastButton = new PuzzleButton();
                    lastButton.setBorderPainted(false);
                    lastButton.setContentAreaFilled(false);
                    lastButton.setLastButton(true);
                } else
                    buttons.add(button);
            }
        }

        Collections.shuffle(buttons);
        buttons.add(lastButton);

        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
            PuzzleButton btn = buttons.get(i);
            panel.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            btn.addActionListener(new ClickAction());
        }
        //---
    }

    private BufferedImage resizedImage(BufferedImage originImage, int width, int height, int type) {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private BufferedImage loadImage() throws IOException {
        BufferedImage bimg = ImageIO.read(new File("1.jpg"));
        return bimg;
    }

    private int getNewHeight(int w, int h) {
        double ratio = DESIRED_WIDTH / (double) w;
        int newHeight = (int) (h * ratio);
        return newHeight;
    }

    private void checkSolution() {
        ArrayList<Point> current = new ArrayList<>();

        for (JComponent btn : buttons) {
            current.add((Point) btn.getClientProperty("position"));
        }

        if (compareList(solution, current)) {
            JOptionPane.showMessageDialog(panel, "Finished",
                    "Congratulation", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    class ClickAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            checkButton(e);
            checkSolution();
        }

        private void checkButton(ActionEvent e) {
            int lidx = 0;

            for (PuzzleButton button : buttons) {
                if (button.isLastButton()) {
                    lidx = buttons.indexOf(button);
                }
            }

            JButton button = (JButton) e.getSource();
            int bidx = buttons.indexOf(button);

            if ((bidx - 1 == lidx) || (bidx + 1 == lidx)
                    || (bidx - 3 == lidx) || (bidx + 3 == lidx)) {
                Collections.swap(buttons, bidx, lidx);
                updateButtons();
            }
        }

        private void updateButtons() {
            panel.removeAll();

            for (JComponent btn : buttons) {
                panel.add(btn);
            }

            panel.validate();
        }
    }


    public static boolean compareList(ArrayList ls1, ArrayList ls2) {
        return ls1.toString().contentEquals(ls2.toString());
    }

    //___________________________________________
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Game puzzle = new Game();
                puzzle.setVisible(true);
            }
        });
    }

}
