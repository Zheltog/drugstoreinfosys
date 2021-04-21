package ru.nsu.ccfit.beloglazov.drugstoreinfosys.frames.itemframes;

import ru.nsu.ccfit.beloglazov.drugstoreinfosys.dao.ComponentDAO;
import ru.nsu.ccfit.beloglazov.drugstoreinfosys.entities.Component;
import ru.nsu.ccfit.beloglazov.drugstoreinfosys.factories.DAOFactory;
import ru.nsu.ccfit.beloglazov.drugstoreinfosys.frames.TableFrame;
import ru.nsu.ccfit.beloglazov.drugstoreinfosys.interfaces.TableItem;
import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class ComponentFrame extends ItemFrame {
    private final JLabel nameLabel = new JLabel("NAME:");
    private final JTextField nameTextField = new JTextField();
    private final JLabel amountLabel = new JLabel("AMOUNT:");
    private final JTextField amountTextField = new JTextField();
    private final JLabel costLabel = new JLabel("COST/GRAM:");
    private final JTextField costTextField = new JTextField();

    public ComponentFrame(TableItem ti, TableFrame tf, Connection connection) {
        super(ti, tf, connection);
        initComponents();
        setBounds(10, 10, 300, 330);
    }

    @Override
    protected void setTextOnTextFields() {
        nameTextField.setText(((Component)ti).getName());
        amountTextField.setText(String.valueOf(((Component)ti).getAmount()));
        costTextField.setText(String.valueOf(((Component)ti).getCostPerGram()));
    }

    @Override
    protected void setLocationAndSizeForCustom() {
        nameLabel.setBounds(10, 170, 260, 30);
        nameTextField.setBounds(70, 170, 200, 30);
        amountLabel.setBounds(10, 210, 260, 30);
        amountTextField.setBounds(70, 210, 200, 30);
        costLabel.setBounds(10, 250, 260, 30);
        costTextField.setBounds(90, 250, 180, 30);
    }

    @Override
    protected void addCustomComponentsToContainer() {
        container.add(nameLabel);
        container.add(nameTextField);
        container.add(amountLabel);
        container.add(amountTextField);
        container.add(costLabel);
        container.add(costTextField);
    }

    @Override
    protected void create() {
        try {
            ComponentDAO dao = (ComponentDAO) DAOFactory.createDAO(tf.getTableName(), connection);
            String name = nameTextField.getText();
            int amount = Integer.parseInt(amountTextField.getText());
            float costPerGram = Float.parseFloat(costTextField.getText());
            Component c = new Component(name, amount, costPerGram);
            dao.add(c);
            tf.setVisible(true);
            dispose();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Could not create item!",
                    "Error!", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    protected void edit() {
        try {
            ComponentDAO dao = (ComponentDAO) DAOFactory.createDAO(tf.getTableName(), connection);
            String name = nameTextField.getText();
            int amount = Integer.parseInt(amountTextField.getText());
            float costPerGram = Float.parseFloat(costTextField.getText());
            Component newC = new Component(((Component)ti).getID(), name, amount, costPerGram);
            dao.update(newC);
            tf.setVisible(true);
            dispose();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Could not edit item!",
                    "Error!", JOptionPane.ERROR_MESSAGE
            );
        }
    }
}