package util;

public class PartialScheduleGrph extends ScheduleGrph {

	/**
	 * 
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

}
