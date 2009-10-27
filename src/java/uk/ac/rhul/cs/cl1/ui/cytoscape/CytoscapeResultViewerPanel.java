package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.ui.NodeSetTableModel;
import uk.ac.rhul.cs.cl1.ui.ResultViewerPanel;

/**
 * Result viewer panel with some added functionality to ensure better integration
 * with Cytoscape
 * 
 * @author tamas
 */
public class CytoscapeResultViewerPanel extends ResultViewerPanel implements ListSelectionListener, ActionListener {
	/**
	 * Mapping from node IDs to real Cytoscape {@link Node} objects
	 */
	protected List<Node> nodeMapping;

	/** Reference to the original Cytoscape network from which the results were calculated */
	protected WeakReference<CyNetwork> networkRef;
	
	/** Reference to a Cytoscape network view that will be used to highlight nodes in the selected nodeset */
	protected WeakReference<CyNetworkView> networkViewRef;
	
	/**
	 * Creates a result viewer panel associated to the given {@link CyNetwork}
	 * and {@link CyNetworkView}
	 * 
	 * It will be assumed that the results shown in this panel were generated
	 * from the given network, and the given view will be used to update the
	 * selection based on the current nodeset in the table.
	 * 
	 * @param network       a network from which the results were generated
	 * @param networkView   a network view that will be used to show the clusters
	 */
	public CytoscapeResultViewerPanel(CyNetwork network, CyNetworkView networkView) {
		super();
		this.networkRef = new WeakReference<CyNetwork>(network);
		this.networkViewRef = new WeakReference<CyNetworkView>(networkView);
		
		/* Listen to table selection changes */
		this.table.getSelectionModel().addListSelectionListener(this);
		
		/* Add the bottom buttons */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		JButton closeButton = new JButton("Close");
		closeButton.setActionCommand("close");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);
		this.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * Retrieves the Cytoscape network associated to this panel
	 */
	public CyNetwork getNetwork() {
		if (networkRef == null)
			return null;
		return networkRef.get();
	}
	
	/**
	 * Retrieves the Cytoscape network view associated to this panel
	 */
	public CyNetworkView getNetworkView() {
		if (networkViewRef == null)
			return null;
		return networkViewRef.get();
	}

	/**
	 * Sets the mapping from integer node IDs to real Cytoscape {@link Node} objects
	 */
	public void setNodeMapping(List<Node> mapping) {
		this.nodeMapping = mapping;
	}
	
	/**
	 * Method called when the table selection changes
	 * @param event   event describing how the selection changed
	 */
	public void valueChanged(ListSelectionEvent event) {
		CyNetwork network = this.getNetwork();
		CyNetworkView networkView = this.getNetworkView();
		
		if (network == null)
			return;
		
		int selectedRow = this.table.getSelectedRow();
		if (selectedRow == -1)
			return;
		
		NodeSetTableModel model = (NodeSetTableModel)this.table.getModel();
		NodeSet nodeSet = model.getNodeSetByIndex(selectedRow);
		ArrayList<Node> nodes = new ArrayList<Node>();
		
		for (Integer idx: nodeSet) {
			Node node = nodeMapping.get(idx);
			if (node == null)
				continue;         // node deleted in the meanwhile
			nodes.add(node);
		}
		
		network.unselectAllNodes();
		network.unselectAllEdges();
		network.setSelectedNodeState(nodes, true);
		networkView.redrawGraph(false, true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		
		if (action == null)
			return;
		
		if (action.equals("close")) {
			CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
			cytoPanel.remove(this);
			if (cytoPanel.getCytoPanelComponentCount() == 0) {
				cytoPanel.setState(CytoPanelState.HIDE);
			}
		}
	}
}