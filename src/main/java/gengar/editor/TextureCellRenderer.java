package gengar.editor;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


public class TextureCellRenderer implements ListCellRenderer<TextureListItem> {

	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override
	public Component getListCellRendererComponent(JList<? extends TextureListItem> list, TextureListItem value, int index, boolean isSelected, boolean cellHasFocus) {

		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		renderer.setFont(new Font("monospaced", Font.BOLD, 12));

		renderer.setText(value.getName());
		
		renderer.setIcon(value.getIcon());

		return renderer;
	}

}

