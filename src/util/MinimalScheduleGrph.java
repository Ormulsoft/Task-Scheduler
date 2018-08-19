package util;

import grph.properties.NumericalProperty;

/**
 * A compact serialized form of the original PartialScheduleGrph. The original
 * objects use too much memory, and are instead queued in this format.
 * 
 * @author Matt Frost
 *
 */
public class MinimalScheduleGrph implements Comparable<MinimalScheduleGrph> {

	private String _serialized;
	private int _score;

	/**
	 * The constructor for generating a MinimalScheduleGrph based on a PartialScheduleGrph
	 * @param in The Partial Schedule to serialize
	 */
	public MinimalScheduleGrph(PartialScheduleGrph in) {
		String serialized = "";
		// NumericalProperty weights = this.getVertexWeightProperty();
		NumericalProperty procs = in.getVertexProcessorProperty();
		NumericalProperty starts = in.getVertexStartProperty();
		for (int i : in.getVertices()) {
			serialized += i;
			serialized += '|';
			// serialized += weights.getValueAsString(i);
			serialized += procs.getValueAsString(i);
			serialized += '|';
			serialized += starts.getValueAsString(i);
			serialized += '|';
		}
		this._serialized = serialized;
		this._score = in.getScore();
	}

	/**
	 * Get the score of this schedule, used for ranking/comparing.
	 * @return
	 */
	public int getScore() {
		return _score;
	}

	/**
	 * Get the serialization string of this schedule
	 * @return
	 */
	public String getSerialString() {
		return _serialized;
	}

	/**
	 * Implementation of the compareTo method, uses score to rank.
	 */
	public int compareTo(MinimalScheduleGrph g) {

		if (this.getScore() < g.getScore())
			return -1;
		else
			return 1;
	}

	/**
	 * Generate a full PartialScheduleGrph from this minimal version
	 * @return
	 */
	public PartialScheduleGrph toGraph() {

		PartialScheduleGrph out = new PartialScheduleGrph(_score);
		// NumericalProperty weights = this.getVertexWeightProperty();
		if (_serialized.length() != 0) {
			NumericalProperty procs = out.getVertexProcessorProperty();
			NumericalProperty starts = out.getVertexStartProperty();
			String[] ints = _serialized.split("\\|");

			for (int i = 0; i < ints.length; i += 3) {
				out.addVertex(Integer.parseInt(ints[i]));
				procs.setValue(Integer.parseInt(ints[i]), Integer.parseInt(ints[i + 1]));
				starts.setValue(Integer.parseInt(ints[i]), Integer.parseInt(ints[i + 2]));
			}

		}

		return out;

	}

}
