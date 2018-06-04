package com.stf.oauth.admin.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "schedule")
public class Schedule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "periodicity")
	private String periodicity;

	@Column(name = "days")
	private String days;

	@Column(name = "hours")
	private Integer hours;

	@Column(name = "minutes")
	private Integer minutes;

	@Column(name = "expression")
	private String expression;

	@Column(name = "user_creator")
	private String userCreator;

	@Column(name = "user_modifier")
	private String userModififier;

	@Temporal(TemporalType.DATE)
	@Column(name = "created")
	private Date created;

	@Temporal(TemporalType.DATE)
	@Column(name = "modified")
	private Date modified;

	@Column(name = "enabled")
	private Boolean enabled;

	/* GETTER AND SETTER */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	/**
	 * @return the hours
	 */
	public Integer getHours() {
		return hours;
	}

	/**
	 * @param hours
	 *            the hours to set
	 */
	public void setHours(Integer hours) {
		this.hours = hours;
	}

	/**
	 * @return the minutes
	 */
	public Integer getMinutes() {
		return minutes;
	}

	/**
	 * @param minutes
	 *            the minutes to set
	 */
	public void setMinutes(Integer minutes) {
		this.minutes = minutes;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getUserCreator() {
		return userCreator;
	}

	public void setUserCreator(String userCreator) {
		this.userCreator = userCreator;
	}

	public String getUserModififier() {
		return userModififier;
	}

	public void setUserModififier(String userModififier) {
		this.userModififier = userModififier;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String constructExpression(Schedule schedule) {
		String expression = "0 ";

		expression += schedule.getMinutes() + " " + schedule.getHours() + " ? * ";

		String[] listDays = schedule.getDays().split(" - ");

		for (String day : listDays) {
			if (day.equals("Lun")) {
				expression += "1,";
			}
			if (day.equals("Mar")) {
				expression += "2,";
			}
			if (day.equals("Mie")) {
				expression += "3,";
			}
			if (day.equals("Jue")) {
				expression += "4,";
			}
			if (day.equals("Vie")) {
				expression += "5,";
			}
			if (day.equals("Sab")) {
				expression += "6,";
			}
			if (day.equals("Dom")) {
				expression += "0,";
			}
		}
		expression = expression.substring(0, expression.length() - 1);
		return expression;
	}

	@Override
	public String toString() {
		return "Schedule{" + "id=" + id + ", name ='" + name + '\'' + ", periodicity='" 
				+ periodicity + '\''+ ", days='" + days + '\'' + ", hours='" 
				+ hours + '\'' + ", minutes='" + minutes + '\''	+ ", expression='" 
				+ expression + '\'' + ", userCreator='" + userCreator + '\'' 
				+ ", userModififier='"+ userModififier + '\'' + ", created=" 
				+ created + ", modified=" + modified + ", enabled=" + enabled
				+ '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Schedule schedule = (Schedule) o;

		if (id != null ? !id.equals(schedule.id) : schedule.id != null)
			return false;
		if (name != null ? !name.equals(schedule.name) : schedule.name != null)
			return false;
		if (periodicity != null ? !periodicity.equals(schedule.periodicity) : schedule.periodicity != null)
			return false;
		if (days != null ? !days.equals(schedule.days) : schedule.days != null)
			return false;
		if (hours != null ? !hours.equals(schedule.hours) : schedule.hours != null)
			return false;
		if (minutes != null ? !minutes.equals(schedule.minutes) : schedule.minutes != null)
			return false;
		if (expression != null ? !expression.equals(schedule.expression) : schedule.expression != null)
			return false;
		if (userCreator != null ? !userCreator.equals(schedule.userCreator) : schedule.userCreator != null)
			return false;
		if (userModififier != null ? !userModififier.equals(schedule.userModififier) : schedule.userModififier != null)
			return false;
		if (created != null ? !created.equals(schedule.created) : schedule.created != null)
			return false;
		if (modified != null ? !modified.equals(schedule.modified) : schedule.modified != null)
			return false;
		return enabled != null ? enabled.equals(schedule.enabled) : schedule.enabled == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (periodicity != null ? periodicity.hashCode() : 0);
		result = 31 * result + (days != null ? days.hashCode() : 0);
		result = 31 * result + (hours != null ? hours.hashCode() : 0);
		result = 31 * result + (minutes != null ? minutes.hashCode() : 0);
		result = 31 * result + (expression != null ? expression.hashCode() : 0);
		result = 31 * result + (userCreator != null ? userCreator.hashCode() : 0);
		result = 31 * result + (userModififier != null ? userModififier.hashCode() : 0);
		result = 31 * result + (created != null ? created.hashCode() : 0);
		result = 31 * result + (modified != null ? modified.hashCode() : 0);
		result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
		return result;
	}
}
