package student_management;

import java.util.Date;

public class Student {
	private String studentID;
	private String name;
	private Date birthday;
	private float mathScore;
	private float physicScore;
	private float chemistryScore;
	private float average;
	
	public Student(String studentID, String name, Date birthday, float mathScore, float physicScore, float chemistryScore) {
		this.studentID = studentID;
		this.name = name;
		this.birthday = birthday;
		this.mathScore = mathScore;
		this.physicScore = physicScore;
		this.chemistryScore = chemistryScore;
	}
	
	public Student() {
		
	}

	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public float getMathScore() {
		return mathScore;
	}

	public void setMathScore(float mathScore) {
		this.mathScore = mathScore;
	}

	public float getPhysicScore() {
		return physicScore;
	}

	public void setPhysicScore(float physicScore) {
		this.physicScore = physicScore;
	}

	public float getChemistryScore() {
		return chemistryScore;
	}

	public void setChemistryScore(float chemistryScore) {
		this.chemistryScore = chemistryScore;
	}

	public float getAverage() {
		return (mathScore + physicScore + chemistryScore) / 3;
	}

	public void setAverage(float average) {
		this.average = average;
	}
	
	
}
