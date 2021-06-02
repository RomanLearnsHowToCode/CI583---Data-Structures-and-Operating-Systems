package ci583.htable.impl;
// Student number: 18838806
// Imports
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;


/**
 * A HashTable with no deletions allowed. Duplicates overwrite the existing value. Values are of
 * type V and keys are strings -- one extension is to adapt this class to use other types as keys.
 * 
 * The underlying data is stored in the array `arr', and the actual values stored are pairs of 
 * (key, value). This is so that we can detect collisions in the hash function and look for the next 
 * location when necessary.
 */

public class Hashtable<V> {

	private Object[] arr; //an array of Pair objects, where each pair contains the key and value stored in the hashtable
	private int max; //the size of arr. This should be a prime number
	private int itemCount; //the number of items stored in arr
	// If my load factor is set to default 0.6 my resize function wouldn't work, it works the best on 0.5
	private final double maxLoad = 0.5; //the maximum load factor (default value is 0.6)

	// ENUM
	public static enum PROBE_TYPE {
		LINEAR_PROBE, QUADRATIC_PROBE, DOUBLE_HASH;
	}

	PROBE_TYPE probeType; //the type of probe to use when dealing with collisions
	private final BigInteger DBL_HASH_K = BigInteger.valueOf(8);

	/**
	 * Create a new Hashtable with a given initial capacity and using a given probe type
	 * @param initialCapacity
	 * @param pt
	 */

	// OBJECT HASHTABLE - Constructor 1
	public Hashtable(int initialCapacity, PROBE_TYPE pt) {
	// Parsing prime number
		int correctedCapacity = nextPrime(initialCapacity);

		arr = new Object[correctedCapacity]; // Initialisation of array, Instantiation of Object array which size of initialCapacity
		probeType = pt; // Initialisation of probe type into constructor
		max = arr.length; // Maximum size of arr array

	}
	
	/**
	 * Create a new Hashtable with a given initial capacity and using the default probe type
	 * @param initialCapacity
	 */

	// OBJECT HASHTABLE - Constructor 2
	public Hashtable(int initialCapacity) {

		this(initialCapacity, PROBE_TYPE.LINEAR_PROBE); // Link between constructor 1 & 2 (constructor chaining)
	}

	/**
	 * Store the value against the given key. If the loadFactor exceeds maxLoad, call the resize 
	 * method to resize the array. the If key already exists then its value should be overwritten.
	 * Create a new Pair item containing the key and value, then use the findEmpty method to find an unoccupied 
	 * position in the array to store the pair. Call findEmmpty with the hashed value of the key as the starting
	 * position for the search, stepNum of zero and the original key.
	 * containing   
	 * @param key
	 * @param value
	 */
	public void put(String key, V value) { // VOID doesn't return a value

		// Key - from string to int
		int hashKey = hash(key);
		// Object
		Pair foundPair = findPair(hashKey, key, 0);

		if (foundPair == null) {
			int newEntryIndex = findEmpty(hashKey, 0, key);
			Pair pair = new Pair(key, value);
			arr[newEntryIndex] = pair;
			itemCount++;
		} else {
		// foundPair !=null, overwrite value
			foundPair.value = value;
		}
		// If maxLoad is less than getLoadFactor value
		if (maxLoad < getLoadFactor()) {
			resize();
		} else {
		}
	}

	/**
	 * Get the value associated with key, or return null if key does not exists. Use the find method to search the
	 * array, starting at the hashed value of the key, stepNum of zero and the original key.
	 * @param key
	 * @return
	 */
	public V get(String key) {

		int hashKey = hash(key);

		return find(hashKey,key,0);

	}

	/**
	 * Return true if the Hashtable contains this key, false otherwise 
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key) {

		int hashKey = hash(key);

		return findPair(hashKey, key, 0) != null;
	}

	/**
	 * getKeys
	 * Return all the keys in this Hashtable as a collection
	 * Search trough the array and check for keys.
	 * @return
	 */
	public Collection<String> getKeys() {

		ArrayList<String> keys = new ArrayList<String>(itemCount);

		for (int a = 0; a < arr.length; a++ ){

			if (arr[a] != null) {

				Pair keysPair = (Pair) (arr[a]); // casting
				keys.add(keysPair.key);

			}

		}

		return keys;
	}

	/**
	 * Return the load factor, which is the ratio of itemCount to max
	 * @return
	 */
	public double getLoadFactor() {

	// Without casting two integers (itemCount and max) to double type function will return not matching values
		double actualLoadFactor = (double)itemCount / (double)max;
	// actualLoadFactor is counted by itemCount and max division
		return actualLoadFactor;
	}

	/**
	 * return the maximum capacity of the Hashtable
	 * @return
	 */
	public int getCapacity() {
		// arr is the size of hash table (value and keys are stored in this array)
		return arr.length;
	}
	
	/**
	 * Find the value stored for this key, starting the search at position startPos in the array. If
	 * the item at position startPos is null, the Hashtable does not contain the value, so return null. 
	 * If the key stored in the pair at position startPos matches the key we're looking for, return the associated 
	 * value. If the key stored in the pair at position startPos does not match the key we're looking for, this
	 * is a hash collision so use the getNextLocation method with an incremented value of stepNum to find 
	 * the next location to search (the way that this is calculated will differ depending on the probe type 
	 * being used). Then use the value of the next location in a recursive call to find.
	 * @param startPos
	 * @param key
	 * @param stepNum
	 * @return
	 */
	private V find(int startPos, String key, int stepNum) {

		Object entry = arr [startPos];


		//test if startPos equals to null, if yes return null
		if (entry == null) {
			return null;
		}

		// Casting
		if (((Pair)entry).key.equals(key)) {
			return ((Pair)entry).value; //
		}

		// recursive call if hash collision occurs
		int nextLoc = getNextLocation(startPos,stepNum,key);
		V value = find(nextLoc,key,++stepNum) ;
		return value;


	}

	/**
	 * Information about findPair method
	 * @param startPos
	 * @param key
	 * @param stepNum
	 * @return
	 */


	private Pair findPair(int startPos, String key, int stepNum) {

		Object entry = arr [startPos];


		//test if startPos equals to null, if yes return null
		if (entry == null) {
			return null;
		}

		//casting
		if (((Pair)entry).key.equals(key)) {
			return ((Pair)entry);
		}




		// recursive call hash collision
		int nextLoc = getNextLocation(startPos,stepNum,key);
		Pair pair = findPair(nextLoc,key,stepNum+1); //stepNum+1
		return pair;

		/**
		 * Information about while loop avoid recursive call
		 *
		 * I have tried to avoid an recursive call, cause of wrong hash function has been causing stackoverflow error
		 * With proper hash function problem no occurs any more
		 */

// This is how I have sorted problem without recursive call

		/*int nextLoc = startPos;

		while (entry != null && !(((Pair)entry).key.equals(key))) {

			nextLoc = getNextLocation(nextLoc,stepNum,key);
			entry = arr [nextLoc];
			++stepNum;

			//test if startPos equals to null, if yes return null
			if (entry == null) {
				return null;
			}
			//casting
			if (((Pair)entry).key.equals(key)) {
				return ((Pair)entry); //
			}
		}
		return null;*/
	}


	/**
	 * Find the first unoccupied location where a value associated with key can be stored, starting the
	 * search at position startPos. If startPos is unoccupied, return startPos. Otherwise use the getNextLocation
	 * method with an incremented value of stepNum to find the appropriate next position to check 
	 * (which will differ depending on the probe type being used) and use this in a recursive call to findEmpty.
	 * @param startPos
	 * @param stepNum
	 * @param key
	 * @return
	 */
	private int findEmpty(int startPos, int stepNum, String key) {
		//
		Object entry = arr[startPos];
		if (entry == null) {

			return startPos;

		}
		// StepNum++ and StepNum+1 collision might be stepNum++
		// StepNum++ will parse values nextLoc, StepNum++ and Key
		int nextLoc = getNextLocation (startPos,stepNum,key); // new startPos
		//System.out.println("found empty, stepNum: " + stepNum + "KEY" + key);

		/*
		* ++stepNum & stepNum++ difference
		*
		* */
		return findEmpty (nextLoc, ++stepNum, key); // return findEmpty (value)
		//return findEmpty (nextLoc, stepNum++, key ); // won't increase stepNum and cause error

	}

	/**
	 * Finds the next position in the Hashtable array starting at position startPos. If the linear
	 * probe is being used, we just increment startPos. If the double hash probe type is being used, 
	 * add the double hashed value of the key to startPos. If the quadratic probe is being used, add
	 * the square of the step number to startPos.
	 * @param startPos
	 * @param stepNum
	 * @param key
	 * @return
	 */
	private int getNextLocation(int startPos, int stepNum, String key) {
		int step = startPos;
		switch (probeType) {
		case LINEAR_PROBE:
			step++;
			break;
		case DOUBLE_HASH:
			step += doubleHash(key);
			break;
		case QUADRATIC_PROBE:
			step += stepNum * stepNum;
			break;
		default:
			break;
		}
		return step % max;
	}

	/**
	 * A secondary hash function which returns a small value (less than or equal to DBL_HASH_K)
	 * to probe the next location if the double hash probe type is being used
	 * @param key
	 * @return
	 */
	private int doubleHash(String key) {
		BigInteger hashVal = BigInteger.valueOf(key.charAt(0) - 96);
		for (int i = 0; i < key.length(); i++) {
			BigInteger c = BigInteger.valueOf(key.charAt(i) - 96);
			hashVal = hashVal.multiply(BigInteger.valueOf(27)).add(c);
		}
		return DBL_HASH_K.subtract(hashVal.mod(DBL_HASH_K)).intValue();
	}

	/**
	 * Return an int value calculated by hashing the key. See the lecture slides for information
	 * on creating hash functions. The return value should be less than max, the maximum capacity 
	 * of the array
	 * @param key
	 * @return
	 */
	private int hash(String key) {

// Better quality hash function from slides
		int hashVal = key. charAt (0) - 96;
		for (int i =0; i<key. length (); i++) {
			 int c = key. charAt (i) - 96;
			 hashVal = ( hashVal * 27 + c) % max ;
			 }
		return Math.abs(hashVal);

/*
 	Hash function will take a key and use default implementation of hashcode of string object that can return integer.
	because we want to work with numbers within the array index boundaries which we need to make sure that are positive
*/


// Low quality hash function was 1)slow 2)while recursive call caused stack OverFlow error
		/*return Math.abs(key.hashCode()) % max;*/

	}


	/**
	 * Information about The Sieve of Eratosthenes method
	 *
	 */

	// The Sieve of Eratosthenes implementation * from Slides
	boolean [] primes = new boolean[0];

	// prime numbers sieve
	public void Sieve(int size){

		primes = new boolean[size];

		for (int i = 0; i < size; i++)
			primes[i] = true;
		// 2 is the smallest prime number
		primes[0] = primes[1] = false; // SYNTATIC SUGAR: https://en.wikipedia.org/wiki/Syntactic_sugar
		/* Same as
		*	primes[0] = false;
		*	primes[1] = false;
		*/

		//
		for (int i = 2; i < primes.length; i++){
			// If i number is prime, it's multiples are not
			if(primes[i]){
				// Multiplies x j are false
				for (int j = 2; i*j < primes.length; j++){
					primes[i*j] = false;
				}

			}

		}

	}



	/**
	 * Return true if n is prime
	 * @param n
	 * @return
	 */

	/*
	* Prime number:
	* A prime number is a NATURAL number greater than 1 that cannot be formed by multiplying two smaller natural numbers
	* */

	private boolean isPrime(int n) {

			return primes[n];

	}

	/**
	 * Get the smallest prime number which is larger than n
	 * @param n
	 * @return
	 */
	private int nextPrime(int n) {

		// K is the number we will receive and we have to compare this number with N value (prime number = true)

		if ( n > primes.length){
			Sieve(Math.max(n*2,10000));
		}

		for (int k = n; k < primes.length; k++) {

			// K will find closest prime number
			if (isPrime(k)) {

				return k;

			}

		}

		// IF we didn't find K valued primes, then we will increase SIEVE by multiple of 2 and call function again
		Sieve(primes.length*2);
		// Recursive call
		return nextPrime(n);

	}

	/**
	 * Resize the hashtable, to be used when the load factor exceeds maxLoad. The new size of
	 * the underlying array should be the smallest prime number which is at least twice the size
	 * of the old array.
	 */
// This method will resize the array arr (Hash table)
	private void resize() {



		// If maxload = maxLoad new maxload x 2

		if (getLoadFactor() >= maxLoad ){
			int newArrSize = nextPrime(max*2);
			Object[] oldArr = arr;
			arr = new Object[newArrSize];
			// copy the current size of arr
			max = newArrSize;
			// reset the itemCount
			itemCount = 0;
			for (int a = 0; a < oldArr.length; a++ ){

				if (oldArr[a] != null) {

					Pair oldPair = (Pair) (oldArr[a]); // casting
					put(oldPair.key , oldPair.value); // can access thanks to recasting

				}
			}
		}
	}

	/**
	 * Instances of Pair are stored in the underlying array. We can't just store
	 * the value because we need to check the original key in the case of collisions.
	 * @author jb259
	 *
	 */
	private class Pair {
		private String key;
		private V value;

		public Pair(String key, V value) {
			this.key = key;
			this.value = value;
		}
	}
}