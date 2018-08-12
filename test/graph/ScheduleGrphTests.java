package graph;

import org.apache.log4j.Logger;
import org.junit.Test;

import util.PartialScheduleGrph;

public class ScheduleGrphTests {
	final static Logger log = Logger.getLogger(ScheduleGrphTests.class);

	@Test
	public void testGraph() {
		PartialScheduleGrph g = new PartialScheduleGrph(0);

		int a = g.addVertex();
		int b = g.addVertex();
		int c = g.addVertex();
		int d = g.addVertex();

		g.getVertexProcessorProperty().setValue(a, 1);
		g.getVertexProcessorProperty().setValue(b, 1);
		g.getVertexProcessorProperty().setValue(c, 2);
		g.getVertexProcessorProperty().setValue(d, 2);

		g.getVertexLabelProperty().setValue(a, "" + a);
		g.getVertexLabelProperty().setValue(b, "" + b);
		g.getVertexLabelProperty().setValue(c, "" + c);
		g.getVertexLabelProperty().setValue(d, "" + d);

		g.getVertexStartProperty().setValue(a, 0);
		g.getVertexStartProperty().setValue(b, 5);
		g.getVertexStartProperty().setValue(c, 6);
		g.getVertexStartProperty().setValue(d, 10);

		PartialScheduleGrph g2 = new PartialScheduleGrph(0);
		g2.addNVertices(4);

		g2.getVertexProcessorProperty().setValue(a, 2);
		g2.getVertexProcessorProperty().setValue(b, 2);
		g2.getVertexProcessorProperty().setValue(c, 1);
		g2.getVertexProcessorProperty().setValue(d, 1);

		g2.getVertexStartProperty().setValue(a, 0);
		g2.getVertexStartProperty().setValue(b, 5);
		g2.getVertexStartProperty().setValue(c, 6);
		g2.getVertexStartProperty().setValue(d, 10);

		g2.getVertexLabelProperty().setValue(a, "" + a);
		g2.getVertexLabelProperty().setValue(b, "" + b);
		g2.getVertexLabelProperty().setValue(c, "" + c);
		g2.getVertexLabelProperty().setValue(d, "" + d);

		// log.info(g.getNormalizedCopy().toDot());
		// log.info(g2.getNormalizedCopy().toDot());

		// assertEquals(g.getNormalizedCopy().toDot(),
		// g2.getNormalizedCopy().toDot());

	}

}
