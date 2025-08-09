import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class NetworkHealthWindow extends JFrame {
    private final JTable table;
    private final DefaultTableModel tableModel;

    public NetworkHealthWindow() {
        setTitle("Network Health Monitor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Matrix-style background ---
        getContentPane().setBackground(Color.BLACK);

        // --- Server metrics table ---
        String[] columns = {"Server ID", "Connections", "Load (ms)", "Load %", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table is read-only
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.BLACK);
        table.setForeground(Color.GREEN);
        table.setFont(new Font("Consolas", Font.BOLD, 14));
        table.setGridColor(new Color(0, 100, 0)); // Dark green grid

        // --- Header styling ---
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.GREEN);
        header.setFont(new Font("Consolas", Font.BOLD, 14));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        // --- Center alignment for all columns ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setBackground(Color.BLACK);
        centerRenderer.setForeground(Color.GREEN);
        centerRenderer.setFont(new Font("Consolas", Font.BOLD, 14));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // --- Special coloring for Status column ---
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Consolas", Font.BOLD, 14));
                setBackground(Color.BLACK);

                if ("Good".equals(value)) {
                    setForeground(Color.GREEN);
                } else if ("Neutral".equals(value)) {
                    setForeground(Color.CYAN);
                } else if ("Bad".equals(value)) {
                    setForeground(Color.RED);
                } else {
                    setForeground(Color.WHITE);
                }

                return c;
            }
        });

        // --- Scroll pane ---
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.BLACK);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * Updates the health monitor table with the latest server metrics.
     */
    public void updateData(List<Server> servers, int serverId) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0); // Clear table

            for (Server s : servers) {
                double loadPercent = s.getLoadPercent();
                long simulatedLoadMs = (long) ((loadPercent / 100.0) * 5000);

                String status;
                if (loadPercent < 50) {
                    status = "Good";
                } else if (loadPercent < 80) {
                    status = "Neutral";
                } else {
                    status = "Bad";
                }

                tableModel.addRow(new Object[]{
                        s.getServerId(),
                        s.getCurrentConnections(),
                        simulatedLoadMs,
                        String.format("%.1f", loadPercent) + "%",
                        status
                });
            }
        });
    }
}
