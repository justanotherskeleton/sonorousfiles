package src.sonorous.ui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import src.sonorous.build.Policy;
import src.sonorous.resource.FileUtil;

import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;
import javax.swing.JProgressBar;

public class SyncPanel extends JPanel {
	
	public JTable table;
	
	public SyncPanel() {
		setLayout(null);
		
		JLabel lblSonorousClientVversion = new JLabel("Sonorous client V:" + Policy.VERSION + " M:SYNC");
		lblSonorousClientVversion.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSonorousClientVversion.setBounds(177, 275, 263, 14);
		add(lblSonorousClientVversion);
		
		/*String[] columnNames = { "Name", "Absolute Path", "Valid" };
		/Object[][] tableData = FileUtil.getSyncTableData(new File("database.json")); */
		
		table = new JTable(/*tableData, columnNames*/);
		table.setBounds(10, 202, 225, -166);
		table.setShowHorizontalLines(true);
		table.setRowSelectionAllowed(true);
		table.setCellSelectionEnabled(false);
		table.setColumnSelectionAllowed(false);
		add(table);
		
		JLabel lblFilesEnabledFor = new JLabel("Files enabled for synchronization");
		lblFilesEnabledFor.setBounds(10, 11, 165, 14);
		add(lblFilesEnabledFor);
		
		JButton btnAddSource = new JButton("Add source");
		btnAddSource.setBounds(10, 225, 104, 23);
		add(btnAddSource);
		
		JButton btnDeleteSource = new JButton("Delete Source");
		btnDeleteSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnDeleteSource.setBounds(124, 225, 111, 23);
		add(btnDeleteSource);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 212, 225, 2);
		add(separator);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setBounds(10, 254, 104, 23);
		add(btnRefresh);
		
		JButton btnValidateSources = new JButton("Validate sources");
		btnValidateSources.setBounds(124, 254, 111, 23);
		add(btnValidateSources);
		
		JButton btnUpdateSettings = new JButton("Update settings");
		btnUpdateSettings.setBounds(284, 170, 111, 44);
		add(btnUpdateSettings);
		
		JButton btnSync = new JButton("Sync now");
		btnSync.setBounds(284, 115, 111, 44);
		add(btnSync);
		
		JLabel lblStatus = new JLabel("STATUS: (varible)");
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setBounds(284, 22, 111, 14);
		add(lblStatus);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(269, 67, 146, 23);
		add(progressBar);
		
		JLabel lblSyncProgress = new JLabel("Sync progress");
		lblSyncProgress.setHorizontalAlignment(SwingConstants.CENTER);
		lblSyncProgress.setBounds(305, 48, 74, 14);
		add(lblSyncProgress);

	}
}
