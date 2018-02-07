package algos;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Primes {

	TreeSet<Integer> primeNumbersForWheel = new TreeSet<Integer>(Arrays.asList(2,3,5,7,11));

	Integer wheelMultiple = primeNumbersForWheel.stream().reduce(1, (i,j) -> i*j);
	TreeSet<Integer> spokesOk = getSpokesOk();
	TreeMap<Long, Boolean> mostlyPrimes = new TreeMap<>();
	TreeSet<Integer> primesOnFirstRow = getPrimesOnFirstRow();
	TreeMap<Long, Long> primes = getFirstPrimes();
	long dur, dur2, dur3;
	int currentPrime;

	public TreeSet<Integer> getSpokesOk() {
		LocalDateTime before  = LocalDateTime.now();
		TreeSet<Integer> spokesOk = new TreeSet<>();
		spokesOk.add(1);
		IntStream.rangeClosed(primeNumbersForWheel.last()+1, wheelMultiple).filter(i -> i%2 != 0).boxed().collect(Collectors.toCollection(()-> spokesOk));
		for (Integer prime : primeNumbersForWheel) {
			if (prime == 2) {
				continue;
			}
			removeMultiplesOfPrimeInCollection(prime, spokesOk);
		}
		LocalDateTime after  = LocalDateTime.now();
		Duration duration = Duration.between(before, after);
		System.out.println("Duree du calcul : " + duration.getSeconds() + "s " + duration.getNano()/1_000_000 + "ms");

		return spokesOk;

	}

	public void removeMultiplesOfPrimeInCollection(Integer prime, TreeSet<Integer> treeSet) {
		for (int multiplePrime = prime*prime ; multiplePrime < wheelMultiple ; multiplePrime += 2*prime) {
			if (treeSet.contains(multiplePrime)) {
				treeSet.remove(multiplePrime);
			}
		}
	}

	public TreeSet<Integer> getPrimesOnFirstRow() {
		LocalDateTime before  = LocalDateTime.now();
		TreeSet<Integer> primesOnFirstRow = new TreeSet<>(spokesOk);
		primesOnFirstRow.remove(1);
		primesOnFirstRow.addAll(primeNumbersForWheel);
		for (Integer prime : spokesOk) {
			if (prime == 1) {
				continue;
			}
			removeMultiplesOfPrimeInCollection(prime, primesOnFirstRow);
		}
		LocalDateTime after  = LocalDateTime.now();
		Duration duration = Duration.between(before, after);
		System.out.println("Duree du calcul : " + duration.getSeconds() + "s " + duration.getNano()/1_000_000 + "ms");

		return primesOnFirstRow;
	}

	private TreeMap<Long, Long> getFirstPrimes() {
		LocalDateTime before  = LocalDateTime.now();
		TreeMap<Long, Long> primes = new TreeMap<>();
		TreeSet<Integer> primesNotInGenerator = new TreeSet<>(primesOnFirstRow);
		primesNotInGenerator.removeAll(primeNumbersForWheel);
		primesNotInGenerator.stream().forEach(i -> primes.put((long)i, (long) (i*i)));
		LocalDateTime after  = LocalDateTime.now();
		Duration duration = Duration.between(before, after);
		System.out.println("Duree du calcul : " + duration.getSeconds() + "s " + duration.getNano()/1_000_000 + "ms");

		return primes;
	}

	public static IntStream stream() {
		return new Primes().solve();
	}

	public IntStream solve() {
		//spokesOk.stream().forEach(i -> System.out.println(i));
		//primesOnFirstRow.stream().forEach(i -> System.out.println(i));
		// Initialization of sieve
		int limitNumber = wheelMultiple <= 2*3*5*7*11 ? wheelMultiple*wheelMultiple : 5336100;
		spokesOk.stream().forEach(i -> mostlyPrimes.put(0L + i + wheelMultiple, false));
		generateSieveUntilNumber(limitNumber);
		checkoutNonPrimeNumbersUntil(limitNumber);

		IntStream beginStream = primesOnFirstRow.stream().mapToInt(i -> i);
		IntStream stream = mostlyPrimes.keySet().stream().mapToInt(i -> i.intValue());
		//IntStream stream = IntStream.generate(() -> getNextPrime());
		return IntStream.concat(beginStream, stream);
	}

	private int getNextPrime() {
		//		for (currentNumber = mostlyPrimes.firstKey()
		//				; mostlyPrimes.get(currentNumber).equals(true) 
		//				; currentNumber = mostlyPrimes.higherKey(currentNumber) ) {}
		//		LocalDateTime beforeAll = LocalDateTime.now();
		Long currentNumber = mostlyPrimes.firstKey();
		mostlyPrimes.keySet().remove(currentNumber);
		long square = currentNumber < 5000 ? currentNumber*currentNumber : 0;
		//		LocalDateTime before = LocalDateTime.now();
		double multiplicateur = 1.2;
		generateSieveUntilNumber((long)(multiplicateur*currentNumber));
		//		LocalDateTime after  = LocalDateTime.now();
		checkoutNonPrimeNumbersUntil((long)(multiplicateur*currentNumber));
		//		LocalDateTime after2  = LocalDateTime.now();
		//		Duration duration = Duration.between(before, after);
		//		Duration duration2 = Duration.between(after, after2);
		//		dur += duration.getNano();
		//		dur2 += duration2.getNano();
		primes.put(currentNumber, square);
		//		LocalDateTime afterAll  = LocalDateTime.now();
		//		Duration duration3 = Duration.between(beforeAll, afterAll);
		//		dur3 += duration3.getNano();
		return currentNumber.intValue();
	}

	// Top non included
	private void checkoutNonPrimeNumbersUntil(long top) {
		for (Entry<Long, Long> primeEntry : primes.entrySet()) {
			long nextNumberToCheck = primeEntry.getValue();
			if(nextNumberToCheck > top) {
				break;
			}
			long lastNumberChecked = nextNumberToCheck;
			for ( 	; nextNumberToCheck < top 
					; nextNumberToCheck += 2*primeEntry.getKey()) {
				if(mostlyPrimes.containsKey(nextNumberToCheck)) {
					mostlyPrimes.remove(nextNumberToCheck);
				}
				lastNumberChecked = nextNumberToCheck;
			}
			primeEntry.setValue(lastNumberChecked);
		}

	}

	private void generateSieveUntilNumber(long n) {
		long targetMultiple = (n / wheelMultiple);
		long currentMultiple = getCurrentMultiple();
		while (currentMultiple++ < targetMultiple) {
			for(int i : spokesOk) {
				mostlyPrimes.put(i + currentMultiple*wheelMultiple, false);
			}
		}
	}

	private long getCurrentMultiple() {
		return mostlyPrimes.lastKey() / wheelMultiple;
	}

	public static void main(String...args) {
		LocalDateTime before = LocalDateTime.now();
		Primes pg = new Primes();
		//		LocalDateTime before2  = LocalDateTime.now();
		//		Duration duration2 = Duration.between(before, before2);
		//		System.out.println("Duree du calcul : " + duration2.getSeconds() + "s " + duration2.getNano()/1_000_000 + "ms");
		IntStream stream = pg.solve();
		//stream.limit(500).forEach(i -> System.out.println(i + ","));
		int result = stream.limit(2_000).max().getAsInt();
		System.out.println(result);
		LocalDateTime after  = LocalDateTime.now();
		Duration duration = Duration.between(before, after);
		System.out.println("Duree du calcul : " + duration.getSeconds() + "s " + duration.getNano()/1_000_000 + "ms");
		System.out.println("Duree 1 : " + pg.dur/1_000_000 + "ms "); 
		System.out.println("Duree 2 : " + pg.dur2/1_000_000 + "ms "); 
		System.out.println("Duree 3 : " + pg.dur3/1_000_000 + "ms "); 
	}


}
