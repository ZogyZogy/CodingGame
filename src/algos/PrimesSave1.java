package algos;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrimesSave1 {

	
	List<Integer> primeNumbersForWheel = Arrays.asList(2,3,5);

	Integer wheelMultiple = primeNumbersForWheel.stream().reduce(1, (i,j) -> i*j);
	TreeSet<Integer> spokesOk = new TreeSet<>(Arrays.asList(1,7,11,13,17,19,23,29));
	TreeMap<Long, Long> primes = getFirstPrimes();
	TreeMap<Long, Boolean> mostlyPrimes = new TreeMap<>();
	TreeSet<Integer> primesOnFirstRow = new TreeSet<>(Arrays.asList(2,3,5,7,11,13,17,19,23,29));
	Long currentNumber = 7L;
	long dur;
	long dur2;
	private final boolean init = false;

	public static void main(String...args) {
		LocalDateTime before = LocalDateTime.now();
		PrimesSave1 pg = new PrimesSave1();
		IntStream stream = pg.solve();
		stream.limit(200).forEach(i -> System.out.println(i + ","));
		int result = stream.limit(200_000).max().getAsInt();
		System.out.println(result);
		LocalDateTime after  = LocalDateTime.now();
		Duration duration = Duration.between(before, after);
		System.out.println("Duree du calcul : " + duration.getSeconds() + "s " + duration.getNano()/1_000_000 + "ms");
		System.out.println("Duree 1 : " + pg.dur/1_000_000 + "ms "); 
		System.out.println("Duree 2 : " + pg.dur2/1_000_000 + "ms "); 
	}

	private TreeMap<Long, Long> getFirstPrimes() {
		TreeMap<Long, Long> primes = new TreeMap<>();
		Arrays.asList(7,11,13,17,19,23,29).stream().forEach(i -> primes.put((long)i, (long) (i*i)));
		return primes;
	}

	public static IntStream stream() {
		return new PrimesSave1().solve();
	}

	public IntStream solve() {
		Set<Integer> spokesRemoved = IntStream.rangeClosed(1, wheelMultiple).boxed()
				.collect(Collectors.toCollection(()-> new TreeSet<Integer>()));
		spokesRemoved.removeAll(primesOnFirstRow);
		spokesRemoved.addAll(primeNumbersForWheel);
		spokesRemoved.add(1);

		spokesOk.stream().forEach(i -> mostlyPrimes.put(0L + i + wheelMultiple, false));
		generateSieveUntilNumber((long) (wheelMultiple*wheelMultiple));
		checkoutNonPrimeNumbersUntil((long) (wheelMultiple*wheelMultiple));

		IntStream beginStream = primesOnFirstRow.stream().mapToInt(i -> i);
		IntStream stream = IntStream.generate(() -> getNextPrime());
		return IntStream.concat(beginStream, stream);
	}

	private int getNextPrime() {
//		for (currentNumber = mostlyPrimes.firstKey()
//				; mostlyPrimes.get(currentNumber).equals(true) 
//				; currentNumber = mostlyPrimes.higherKey(currentNumber) ) {}
		currentNumber = mostlyPrimes.firstKey();
		mostlyPrimes.keySet().remove(currentNumber);
		long square = currentNumber*currentNumber;
		double multiplicateur = 1.02;
		generateSieveUntilNumber((long)(multiplicateur*currentNumber));
		checkoutNonPrimeNumbersUntil((long)(multiplicateur*currentNumber));
		primes.put(currentNumber, square);
		return currentNumber.intValue();
	}

	private void init() {
		// TODO Auto-generated method stub
		
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
}
