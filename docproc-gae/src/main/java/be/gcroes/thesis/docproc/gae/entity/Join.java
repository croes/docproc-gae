package be.gcroes.thesis.docproc.gae.entity;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Serialize;

@Entity
@Cache
public class Join {

	@Id
	Long id;
	
	Key<Job> jobId;
	
	@Serialize
	List<Long> taskIds;
	
	public Join(){
		this.taskIds = new ArrayList<Long>();
	}
	
	public Join(Key<Job> jobId, List<Long> taskIds){
		this.jobId = jobId;
		this.taskIds = taskIds;
	}
	
	public void signal(Long taskId){
		this.taskIds.remove(taskId);
	}
	
	public boolean isComplete(){
		return taskIds.isEmpty();
	}
	
	public Long getId(){
		return id;
	}
	
	public Key<Join> getKey(){
		return Key.create(Join.class, id);
	}
	
	public int getCount(){
		return taskIds.size();
	}
}
