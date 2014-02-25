package be.gcroes.thesis.docproc.gae.entity;

import java.util.HashMap;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Serialize;

@Entity
@Cache
public class Task {

	@Id
	Long id;
	
	@Parent
	Ref<Job> job;
	
	@Serialize //cannot be queried, cannot exceed 1 MB
	HashMap<String, Object> params;
	
	String result;
	
	public Task(){
		this.job = null;
		this.params = new HashMap<String, Object>();
		this.result = null;
	}
	
	public Task(Job parent){
		this.job = Ref.create(parent);
		this.params = new HashMap<String, Object>();
		this.result = null;
	}
	
	public Task(Job parent, HashMap<String, Object> params){
		this.job = Ref.create(parent);
		this.params = params;
		this.result = null;
	}
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public HashMap<String, Object> getParams() {
		return params;
	}

	public Job getJob(){
		return job.get();
	}
	
	public void removeParam(String key){
		params.remove(key);
	}
	
	public void putParam(String key, Object value){
		params.put(key, value);
	}
	
	public Key<Task> getKey(){
		return Key.create(Task.class, id);
	}

	public Long getId() {
		return id;
	}
	
}
