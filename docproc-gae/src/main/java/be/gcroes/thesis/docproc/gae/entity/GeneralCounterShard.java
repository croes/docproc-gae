package be.gcroes.thesis.docproc.gae.entity;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@Cache
public class GeneralCounterShard {
	
	@Id
	String id;
		
	int count;
	
	public GeneralCounterShard(){
		
	}
	
	public GeneralCounterShard(Key<ShardedCounter> shardedCounter, String counterName, int shardNum){
		this.id = counterName + "-" + shardNum;
		this.count = 0;
	}
	
	public int getCount(){
		return count;
	}
	
	public void increment(){
		count++;
	}

}
