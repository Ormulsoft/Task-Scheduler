package util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import grph.Grph;
import grph.io.DotWriter;
import toools.collections.primitive.IntCursor;

/**
 * Overrides the Grph library dot writer in order to write the additional Grph
 * tags (ie. processor, vertex weight) to an output dotfile. It also removes all
 * the unnecessary styles.
 * 
 * @author Matt
 *
 */
public class ScheduleDotWriter extends DotWriter {

	final static Logger log = Logger.getLogger(ScheduleDotWriter.class);

	public ScheduleDotWriter() {
		super();
	}

	/**
	 * Overrides the createDotText method in order to output the correctly
	 * formatted dotfile IF a ScheduleGraph is inputted, otherwise use super
	 * method.
	 */
	@Override
	public String createDotText(Grph graph, boolean writeEdgeLabels) {
		ScheduleGrph sg = null;

		// downcast to ScheduleGrph
		if (graph.getClass().equals(ScheduleGrph.class)) {

			sg = (ScheduleGrph) graph;

			StringBuilder text = new StringBuilder();
			text.append("digraph {\n");

			for (IntCursor c : IntCursor.fromFastUtil(sg.getVertices())) {
				int v = c.value;
				text.append('\t');
				text.append(v);
				text.append(' ');
				Map<String, Object> map = new HashMap<String, Object>();
				try {
					map.put("Weight", sg.getVertexWeightProperty().getValueAsInt(v));
					map.put("Start", sg.getVertexStartProperty().getValueAsInt(v));
					map.put("Processor", sg.getVertexProcessorProperty().getValueAsInt(v));
				} catch (NullPointerException e) {
					log.error(e);
				}

				// remove all " chars
				text.append(to(map).replaceAll("\"", ""));
				text.append(';');
				text.append('\n');
			}

			text.append('\n');

			for (IntCursor c : IntCursor.fromFastUtil(sg.getEdges())) {
				int e = c.value;

				if (sg.isSimpleEdge(e)) {
					text.append('\t');
					int a = sg.getOneVertex(e);
					text.append(a);
					text.append(" -> ");
					text.append(sg.getTheOtherVertex(e, a));
				}

				text.append(' ');

				Map<String, Object> map = new HashMap<String, Object>();

				if (writeEdgeLabels) {
					map.put("Weight", sg.getEdgeWidthProperty().getValueAsInt(e));
				}
				// remove all " chars
				text.append(to(map).replaceAll("\"", ""));
				text.append(';');
				text.append('\n');
			}

			text.append('}');
			return text.toString();
		} else {
			// use super method if is not a ScheduleGrph
			return super.createDotText(graph, writeEdgeLabels);
		}
	}
}
