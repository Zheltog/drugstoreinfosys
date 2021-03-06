package ru.nsu.ccfit.beloglazov.drugstoreinfosys.frames;

import ru.nsu.ccfit.beloglazov.drugstoreinfosys.QueryExecutor;
import ru.nsu.ccfit.beloglazov.drugstoreinfosys.factories.DAOFactory;
import ru.nsu.ccfit.beloglazov.drugstoreinfosys.frames.mainframes.MainFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class QueryFrame extends JFrame implements ActionListener {
    private final Container container = getContentPane();
    private final JLabel titleLabel = new JLabel("DRUGSTORE INFORMATION SYSTEM");
    private final JLabel queryNameLabel = new JLabel();
    private final JList<String> resultsJList = new JList<>();
    private final JScrollPane scrollPane = new JScrollPane(resultsJList);
    private final JButton backButton = new JButton();
    private final MainFrame mf;
    private final QueryExecutor qe;

    public QueryFrame(MainFrame mf, DAOFactory daoFactory) {
        this.mf = mf;
        qe = new QueryExecutor(this, daoFactory);
        container.setLayout(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        setLocationAndSize();
        addActionEvent();
        addComponentsToContainer();
        setTitle("DIS :: Query Frame");
        setBounds(10,10,300,420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(false);
    }

    private void setLocationAndSize() {
        titleLabel.setBounds(10, 10, 260, 30);
        queryNameLabel.setBounds(10, 50, 260, 30);
        scrollPane.setBounds(10, 90, 260, 220);
        backButton.setBounds(10, 330, 50, 30);
        try {
            URL backResource = getClass().getClassLoader().getResource("images/back.gif");
            if (backResource != null) {
                ImageIcon backIcon = new ImageIcon(backResource);
                backButton.setIcon(backIcon);
            } else throw new Exception("Error!");
        } catch (Exception e) {
            e.printStackTrace();
            backButton.setText("<");
        }
    }

    private void addActionEvent() {
        backButton.addActionListener(this);
    }

    private void addComponentsToContainer() {
        container.add(titleLabel);
        container.add(queryNameLabel);
        container.add(scrollPane);
        container.add(backButton);
    }

    public void executeQuery(String queryName, String[] parameters) {
        setVisible(true);
        queryNameLabel.setText("QUERY: " + queryName);
        try {
            String[] results = qe.execute(queryName, parameters);
            resultsJList.setListData(results);
        } catch (Exception e) {
            resultsJList.setListData(new String[0]);
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Could not execute query!",
                    "Error!", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            setVisible(false);
            mf.setVisible(true);
        }
    }

    public MainFrame getMainFrame() { return mf; }
}