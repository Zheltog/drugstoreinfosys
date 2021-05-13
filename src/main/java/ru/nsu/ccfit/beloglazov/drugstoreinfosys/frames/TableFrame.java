package ru.nsu.ccfit.beloglazov.drugstoreinfosys.frames;

import ru.nsu.ccfit.beloglazov.drugstoreinfosys.TableAccessType;
import ru.nsu.ccfit.beloglazov.drugstoreinfosys.dao.DAO;
import ru.nsu.ccfit.beloglazov.drugstoreinfosys.entities.TableItem;
import ru.nsu.ccfit.beloglazov.drugstoreinfosys.frames.itemframes.ItemFrameType;
import ru.nsu.ccfit.beloglazov.drugstoreinfosys.frames.mainframes.MainFrame;
import ru.nsu.ccfit.beloglazov.drugstoreinfosys.factories.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;

public class TableFrame extends JFrame implements ActionListener {
    private final Container container = getContentPane();
    private final JLabel titleLabel = new JLabel("DRUGSTORE INFORMATION SYSTEM");
    private final JLabel tableNameLabel;
    private final JLabel tipLabel = new JLabel("(you can resize columns of this table)");
    private final JTable itemsJTable = new JTable();
    private final JScrollPane scrollPane;
    private final JButton createButton = new JButton("Create");
    private final JButton editButton = new JButton("Edit");
    private final JButton deleteButton = new JButton("Delete");
    private final JButton findButton = new JButton("Find");
    private final JButton backButton = new JButton("Back");
    private final MainFrame mf;
    private final String tableName;
    private final Connection connection;
    private final TableAccessType accessType;
    private final DAO dao;
    private List itemsList;

    public TableFrame(MainFrame mf, String tableName, Connection connection, TableAccessType accessType) throws SQLException {
        this.mf = mf;
        this.tableName = tableName;
        this.connection = connection;
        this.accessType = accessType;
        tableNameLabel = new JLabel("TABLE: '" + tableName + "'");
        dao = DAOFactory.createDAO(tableName, connection);
        updateItems(null);
        scrollPane = new JScrollPane(itemsJTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        container.setLayout(null);
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
        setTitle("DIS :: Table '" + tableName + "'");
        setBounds(10,10,680,460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    private void setLocationAndSize() {
        titleLabel.setBounds(10, 10, 260, 30);
        tableNameLabel.setBounds(10, 50, 260, 30);
        tipLabel.setBounds(10, 90, 260, 30);
        scrollPane.setBounds(10, 130, 640, 230);
        createButton.setBounds(10, 370, 120, 30);
        editButton.setBounds(140, 370, 120, 30);
        deleteButton.setBounds(270, 370, 120, 30);
        findButton.setBounds(400, 370, 120, 30);
        backButton.setBounds(530, 370, 120, 30);
    }

    private void addComponentsToContainer() {
        container.add(titleLabel);
        container.add(tableNameLabel);
        container.add(tipLabel);
        container.add(scrollPane);
        if (accessType == TableAccessType.READ_AND_WRITE) {
            container.add(createButton);
            container.add(editButton);
            container.add(deleteButton);
        }
        container.add(findButton);
        container.add(backButton);
    }

    private void addActionEvent() {
        createButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
        findButton.addActionListener(this);
        backButton.addActionListener(this);
    }

    public void updateItems(List<TableItem> items) {
        try {
            if (items != null) {
                itemsList = items;
            } else {
                itemsList = dao.getAll();
            }
            Object[] columnsArray;
            if (!itemsList.isEmpty()) {
                columnsArray = ((TableItem)itemsList.get(0)).getValues().keySet().toArray();
            } else {
                List<TableItem> allItems = dao.getAll();
                columnsArray = allItems.get(0).getValues().keySet().toArray();
            }
            DefaultTableModel tm = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tm.setColumnIdentifiers(columnsArray);
            for (Object item : itemsList) {
                Object[] values = ((TableItem)item).getValues().values().toArray(new Object[0]);
                tm.addRow(values);
            }
            itemsJTable.setModel(tm);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Could not update items!",
                    "Error!", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createButton) {
            create();
        } else if (e.getSource() == editButton) {
            edit();
        } else if (e.getSource() == deleteButton) {
            delete();
        } else if (e.getSource() == findButton) {
            find();
        } else if (e.getSource() == backButton) {
            back();
        }
    }

    public void create() {
        setVisible(false);
        JFrame cf = FrameFactory.getItemFrame(tableName, ItemFrameType.CREATE, null, this, connection);
    }

    private void edit() {
        int[] ids = itemsJTable.getSelectedRows();
        if (ids.length != 1) {
            return;
        }
        TableItem ti = (TableItem) itemsList.get(ids[0]);
        setVisible(false);
        JFrame ef = FrameFactory.getItemFrame(tableName, ItemFrameType.EDIT, ti, this, connection);
    }

    private void find() {
        setVisible(false);
        JFrame ff = FrameFactory.getItemFrame(tableName, ItemFrameType.FIND, null, this, connection);
    }

    private void delete() {
        int[] ids = itemsJTable.getSelectedRows();
        int i = ids.length - 1;
        while (i >= 0) {
            TableItem ti = (TableItem) itemsList.get(ids[i]);
            Integer id = (Integer) ti.getValues().get("id");
            try {
                dao.delete(id);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                JOptionPane.showMessageDialog(
                        this, "Could not delete item!",
                        "Error!", JOptionPane.ERROR_MESSAGE
                );
            }
            itemsList.remove(ids[i]);
            i--;
        }
        updateItems(null);
    }

    private void back() {
        mf.setVisible(true);
        setVisible(false);
        dispose();
    }
}