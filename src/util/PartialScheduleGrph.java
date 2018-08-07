package util;

public class PartialScheduleGrph extends ScheduleGrph {

	/**
	 * A graphing class for an intermediary partial schedule generated during
	 * algorithm runs. Used to represent a potential partial solution for the
	 * problem on the tree.
	 */
	private static final long serialVersionUID = 1L;
	int score;

	public PartialScheduleGrph(int score) {
		super();
		this.score = score;
	}

	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public PartialScheduleGrph cloneSelf() {
		return (PartialScheduleGrph) super.clone();
	}

}
