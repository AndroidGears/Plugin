package Renderers;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecAuthor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by matthewyork on 4/1/14.
 */

public class GearSpecCellRenderer extends JPanel implements ListCellRenderer {
    private static final Color HIGHLIGHT_COLOR = Color.decode("0x2B2B2B");
    private Color cellBackgroundColor = null;

    JPanel specInfoPanel;
    JLabel nameLabel;
    JLabel authorLabel;
    JLabel imageLabel;
    //JLabel jarLabel;
    ImageIcon declaredIcon = new ImageIcon(getClass().getResource("GearStateDeclared.png"));
    ImageIcon installedIcon = new ImageIcon(getClass().getResource("GearStateInstalled.png"));
    //ImageIcon jarfile = new ImageIcon(getClass().getResource("jarfile.png"));

    public GearSpecCellRenderer() {
        setOpaque(true);
        cellBackgroundColor = getBackground();
    }

    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
    {
        GearSpec spec = (GearSpec)value;

        //Check for first runthrough
        if(nameLabel == null){
            //Initialize name panel
            this.setLayout(new BorderLayout());
            this.setBorder(new EmptyBorder(10,10,10,10));
            specInfoPanel = new JPanel();
            specInfoPanel.setLayout(new BoxLayout(specInfoPanel, BoxLayout.Y_AXIS));
            specInfoPanel.setOpaque(false);

            //Set name label
            nameLabel = new JLabel(spec.getName(), JLabel.LEFT);
            nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.PLAIN, 18));

            //Set author label
            authorLabel = new JLabel("", JLabel.LEFT);
            authorLabel.setFont(new Font(authorLabel.getFont().getName(), Font.PLAIN, 12));

            //Iterate over all authors for matches
            if (spec.getAuthors() != null){
                String authors = "";
                for (GearSpecAuthor author : spec.getAuthors()){
                    authors = authors+author.getName()+", ";
                }
                //Remove last comma/space
                authors = authors.substring(0, authors.length()-2);

                //Set label text
                authorLabel.setText(authors);
            }

            //Set image
            imageLabel = new JLabel();
            //Set image
            switch (spec.getGearState().ordinal()){
                case 0: imageLabel.setIcon(new ImageIcon());
                    break;
                case 1: imageLabel.setIcon(declaredIcon);
                    break;
                case 2: imageLabel.setIcon(installedIcon);
                    break;
            }


            //set jar image
            /*
            jarLabel = new JLabel();
            if(spec.getType().equals("jar")){
                jarLabel.setIcon(jarfile);
            }else{
                jarLabel.setIcon(new ImageIcon());
            }*/


            //Add components
            this.add(specInfoPanel, BorderLayout.WEST);
            specInfoPanel.add(nameLabel);
            specInfoPanel.add(authorLabel);
            this.add(imageLabel, BorderLayout.EAST);
            //this.add(jarLabel,BorderLayout.CENTER);
        }
        else {
            //Set name label
            nameLabel.setText(spec.getName());

            //Set author label
            if (spec.getAuthors() != null){
                String authors = "";
                for (GearSpecAuthor author : spec.getAuthors()){
                    authors = authors+author.getName()+", ";
                }
                //Remove last comma/space
                authors = authors.substring(0, authors.length()-2);

                //Set label text
                authorLabel.setText(authors);
            }

            //Set image
            switch (spec.getGearState().ordinal()){
                case 0: imageLabel.setIcon(new ImageIcon());
                    break;
                case 1: imageLabel.setIcon(declaredIcon);
                    break;
                case 2: imageLabel.setIcon(installedIcon);
                    break;
            }
            //set if jar
            /*
            if(spec.getType().equals("jar")){
                jarLabel.setIcon(jarfile);
            }else{
                jarLabel.setIcon(new ImageIcon());
            }*/
        }

        if(isSelected) {
            setBackground(cellBackgroundColor.darker());
            setOpaque(true);
            //setForeground(Color.white);
        } else {
            setOpaque(false);
            //setForeground(Color.black);
        }
        return this;
    }
}
