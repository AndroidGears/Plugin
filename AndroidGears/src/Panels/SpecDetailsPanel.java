package Panels;

import Forms.ManageAndroidGearsForm;
import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecAuthor;
import Models.GearSpec.GearSpecDependency;
import Utilities.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by matthewyork on 4/2/14.
 */
public class SpecDetailsPanel extends JPanel{
    private GearSpec selectedSpec;

    public SpecDetailsPanel(GearSpec selectedSpec) {
        this.selectedSpec = selectedSpec;

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setSize(200, -1);

        //Add repo name
        JLabel nameLabel = new JLabel(selectedSpec.getName(), JLabel.LEFT);
        nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.BOLD, 14));
        this.add(nameLabel);

        //Add version and type
        JLabel versionLabel = new JLabel(selectedSpec.getVersion() + " - " + selectedSpec.getType(), JLabel.LEFT);
        versionLabel.setFont(new Font(versionLabel.getFont().getName(), Font.BOLD, 12));
        this.add(versionLabel);

        //Add repo name
        if (selectedSpec.getSummary() != null) {
            JLabel summaryLabel = new JLabel(Utils.wrappedStringForString(selectedSpec.getSummary(), ManageAndroidGearsForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            summaryLabel.setFont(new Font(summaryLabel.getFont().getName(), Font.PLAIN, 12));
            this.add(summaryLabel);
        }

        //Add authors
        if (selectedSpec.getAuthors() != null) {
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>Authors", ManageAndroidGearsForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            this.add(header);

            //Add authors
            for (GearSpecAuthor author : selectedSpec.getAuthors()) {
                JLabel authorLabel = new JLabel(Utils.wrappedStringForString(author.getName() + " - " + author.getEmail(), ManageAndroidGearsForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
                authorLabel.setFont(new Font(authorLabel.getFont().getName(), Font.PLAIN, 12));
                this.add(authorLabel);
            }
        }

        //Add Dependencies
        if (selectedSpec.getDependencies() != null) {
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>Dependencies", ManageAndroidGearsForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            this.add(header);

            //Add authors
            for (GearSpecDependency dependency : selectedSpec.getDependencies()) {
                JLabel authorLabel = new JLabel(Utils.wrappedStringForString(dependency.getName() + " - " + dependency.getVersion(), ManageAndroidGearsForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
                authorLabel.setFont(new Font(authorLabel.getFont().getName(), Font.PLAIN, 12));
                this.add(authorLabel);
            }
        }

        //Add License
        if (selectedSpec.getLicense() != null) {
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>License", ManageAndroidGearsForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            this.add(header);

            //Add authors header
            JLabel licenseLabel = new JLabel(selectedSpec.getLicense(), JLabel.LEFT);
            licenseLabel.setFont(new Font(licenseLabel.getFont().getName(), Font.PLAIN, 12));
            this.add(licenseLabel);
        }

        //Add homepage
        if (selectedSpec.getHomepage() != null) {
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>Homepage", ManageAndroidGearsForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            this.add(header);

            //Add homepage
            JLabel homepageLabel = new JLabel(selectedSpec.getHomepage(), JLabel.LEFT);
            homepageLabel.setFont(new Font(homepageLabel.getFont().getName(), Font.PLAIN, 12));
            this.add(homepageLabel);


        }

    }
}
