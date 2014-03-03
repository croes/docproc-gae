package be.gcroes.thesis.docproc.gae.entity;

import static be.gcroes.thesis.docproc.gae.entity.OfyService.ofy;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
public class Job {

	@Id
	Long id; // if saved when null, will be autogenerated

	@Index
	String user;

	String template;

	String csv;

	Date startTime;

	Date endTime;

	String result;
		
	int nTasks;

	/**
	 * Empty constructor. All fields will be null.
	 */
	public Job() {
		this(null, null, null, null, null);
	}

	/**
	 * 
	 * @param owner
	 * @param template
	 * @param csv
	 * @param startTime
	 * @param endTime
	 */
	public Job(String user, String template, String csv, Date startTime,
			Date endTime) {
		this.id = null;
		this.user = user;
		this.template = template;
		this.csv = csv;
		this.startTime = startTime;
		this.endTime = endTime;
		this.nTasks = 0;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List<Task> getTasks() {
		return ofy().load().type(Task.class).filter("job", this).list();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getCsv() {
		return csv;
	}

	public void setCsv(String csv) {
		this.csv = csv;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Long getId() {
		return id;
	}

	public Key<Job> getKey() {
		return Key.create(Job.class, id);
	}

	/**
	 * Persist the job first before adding tasks!
	 * Will create a new Task and persist it.
	 * @return
	 */
	public Task newTask() {
		if (id == null) {
			throw new IllegalStateException(
					"Cannot add new task for a job that has not been persisted yet. Persist the job first so it has an id.");
		}
		nTasks++;
		return new Task(this);
	}

	public Task newTask(HashMap<String, Object> params) {
		nTasks++;
		return new Task(this, params);
	}
	
	public long getDurationInMillis(){
		if(getStartTime() == null){
			return -1;
		}
		if(getEndTime() == null){
			return new Date().getTime() - getStartTime().getTime();
		}
		return getEndTime().getTime() - getStartTime().getTime();
	}
	
	public int getNbOfTasks(){
		return nTasks;
	}
}
