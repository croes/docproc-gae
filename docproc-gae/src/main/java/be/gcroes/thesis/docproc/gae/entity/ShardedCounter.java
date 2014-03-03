package be.gcroes.thesis.docproc.gae.entity;

import static be.gcroes.thesis.docproc.gae.entity.OfyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * A counter which can be incremented rapidly.
 * 
 * Capable of incrementing the counter and increasing the number of shards. When
 * incrementing, a random shard is selected to prevent a single shard from being
 * written to too frequently. If increments are being made too quickly, increase
 * the number of shards to divide the load. Performs datastore operations using
 * JDO.
 */
@Entity
@Cache
public class ShardedCounter {

	@Id
	private String counterName;

	int numShards;
	
	public ShardedCounter(){
		
	}

	public ShardedCounter(String counterName, int numShards) {
		this.counterName = counterName;
		addShards(numShards);
	}

	public String getCounterName() {
		return counterName;
	}

	/**
	 * Retrieve the value of this sharded counter.
	 * 
	 * @return Summed total of all shards' counts
	 */
	public int getCount() {
		int sum = 0;
		List<Key<GeneralCounterShard>> shardKeys = new ArrayList<Key<GeneralCounterShard>>();
		for(int i = 0; i < numShards; i++){
			shardKeys.add(Key.create(GeneralCounterShard.class, counterName + "-" + i));
		}
		Map<Key<GeneralCounterShard>, GeneralCounterShard> shards = ofy().load().keys(shardKeys);

		if (shards != null && !shards.isEmpty()) {
			for (GeneralCounterShard current : shards.values()) {
				sum += current.getCount();
			}
		}
		return sum;
	}

	/**
	 * Increment the value of this sharded counter.
	 */
	public void increment() {

		// Find how many shards are in this counter.
		int shardCount = getShardCount();

		// Choose the shard randomly from the available shards.
		Random generator = new Random();
		int shardNum = generator.nextInt(shardCount);

		final Key<GeneralCounterShard> shardKey = Key.create(
				GeneralCounterShard.class, counterName + "-" + shardNum);
//		List<GeneralCounterShard> allShards = ofy().load().type(GeneralCounterShard.class).list();
//		for(GeneralCounterShard shard : allShards){
//			System.out.println("shard: " + shard.id + " = " + shard.count);
//		}
//		System.out.println(shardKey);
		ofy().transact(
				new Work<GeneralCounterShard>() {

					@Override
					public GeneralCounterShard run() {
						GeneralCounterShard shard = ofy().load().key(shardKey)
								.now();
						shard.increment();
						ofy().save().entity(shard); // async op is completed
													// upon
													// transact end
						return shard;
					}

				});
	}

	private int getShardCount() {
		return numShards;
	}

	/**
	 * Increase the number of shards for a given sharded counter. Will never
	 * decrease the number of shards.
	 * 
	 * @param count
	 *            Number of new shards to build and store
	 * @return Total number of shards
	 */
	public int addShards(int count) {
		for (int i = 0; i < count; i++) {
			GeneralCounterShard newShard = new GeneralCounterShard(
					this.getKey(), counterName, numShards);
			ofy().save().entity(newShard).now();
			numShards++;
		}

		return numShards;
	}

	public Key<ShardedCounter> getKey() {
		return Key.create(ShardedCounter.class, counterName);
	}
}
