package RationalPiano.ConsonanceCalculation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;


/**
 * Calculates the consonances for a range of notes (on the 12TET scale) given a set of active notes with different volumes.
 * At initialization time calculates the dissonance of all intervals in the parametrized range with the given maximum dissonance and bell width.
 * It is based on rational numbers and the assumption that the perceived dissonance between the rational number 1/1 (unison) and another n/d is n*d.
 * Dissonance = 1 / Consonance.
 * Each rational number denotes a point (x=numerator/denominator, y=numerator*denominator) on the relative frequency / dissonance plane.
 * Each rational number gets "fuzzified" by a bell curve as human can't distinguish close frequencies and all real numbers on the x-axis have to get filled which is impossible with rational numbers.
 * Also note that the human auditory system works log scaled, so the difference between two halftones is a FACTOR (multiplication wise) of 2^(1/12) in the 12TET scale (logarithmic equal steps).
 * So the bell curve is logarithmized in a way that it's looking normal on a log plot.
 * It is not based on the specific timbre played like in the works of Sethares.
 * 
 * @author Fabian Ehrentraud
 * @date 2010-07-30
 * @version 1.01
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class Consonance implements IConsonance {
	
	private int notestart;
	private int notecount;
	private int maxfrac;
	private double bellWidth;
	
	private ArrayList<Double> noteDiss;
	
	private HashMap<Integer, Double> voiceConsonances = new HashMap<Integer, Double>();
	
	private static final Logger logger = Logger.getLogger(Consonance.class.getName());
	
	/**
	 * @param notestart Midi note number of the first note in range.
	 * @param notecount Count of halftones above (and inclusively) notestart to include.
	 * @param maxfrac The maximum dissonance value for rational numbers (numerator * denominator) to take account for. 157 is a good value. High values will cause longer initialization times!
	 * @param bellWidth The width of the bell shaped curve with which each fraction's point get's "fuzzified". bellWidth==1 means it's inflection point is at +- 1/Sqrt(e) semitones. bellWidth>0
	 */
	public Consonance(int notestart, int notecount, int maxfrac, double bellWidth) {
		this.notestart = notestart;
		this.notecount = notecount;
		this.maxfrac = maxfrac;
		this.bellWidth = bellWidth;
		
		//initialize hashmap so it's faster as there will always be the same keys in it
		for(int key = notestart; key < notecount; key++){
			voiceConsonances.put(key, 1.);
		}
		
		initialize(notecount, maxfrac, bellWidth);
	}
	
	@Override
	public HashMap<Integer, Double> calculate(HashMap<Integer,Double> voicesValues) {
		//add up single dissonances and then invert 

		/**/
		for(int keyout = notestart; keyout < notestart+notecount; keyout++){
			double comboDiss = 1; //make sure the calculated dissonance value is always greater or equal to 1
			for(int keywith = notestart; keywith < notestart+notecount; keywith++){
				if(keywith == keyout){
					continue; //as every key can only be played once at a time, the dissonance to itself doesn't have to be considered
				}
				if(voicesValues.containsKey(keywith)){
					//FIXME change the forumula so that the fade out curve is nicer
					comboDiss += (noteDiss.get(Math.abs(keywith - keyout))) * voicesValues.get(keywith);
				}
			}
			voiceConsonances.put(keyout, 1 / (comboDiss));
		}

		/*/
		//TODO TEST just output the volume values back again
		for(int key = notestart; key < notestart+notecount; key++){
			if(voicesValues.containsKey(key)){
				voiceConsonances.put(key, voicesValues.get(key));
			}else{
				voiceConsonances.put(key, 0.);
			}
		}
		/**/
		
		return voiceConsonances;
	}
	
	/**
	 * Calculates the dissonance of all intervals in the parametrized range with the given maximum dissonance and bell width.
	 * At first calculates the factorization of all integers i=1...maxfrac where for each single factorization, same primes get multiplied back together (eg. from factorization of 300 = {2,2,3,5,5} result is {4,3,25}).
	 * For each factorization list generates a set of rational numbers where all possibilities of distributing those factors on each side of the fraction mark ({}->{1}). (eg. {4,3,25} -> {(1)/(4*3*25),(1*4)/(3*25),(1*3)/(4*25),(1*25)/(4*3),(4*3)/(25),(4*25)/(3),(3*25)/(4),(4*3*25)/(1}}})
	 * For each note in the noterange, calculate the minimum dissonance value which results of bellcurves centered about all the fraction values respectively multiplied with the individual dissonances (numerator * denominator).
	 * @param notecount Count of halftones above prime (1/1) to include.
	 * @param maxfrac The maximum dissonance value for rational numbers (numerator * denominator) to take account for. 157 is a good value. High values will cause longer initialization times!
	 * @param bellWidth The width of the bell shaped curve with which each fraction's point get's "fuzzified". bellWidth==1 means it's inflection point is at +- 1/Sqrt(e) semitones. bellWidth>0
	 */
	private void initialize(int notecount, int maxfrac, double bellWidth){
		logger.info("Calculating consonances for a range of " + notecount + " notes with a maximum fraction dissonance of " + maxfrac + " and an approximating bell curve with width " + bellWidth);

		noteDiss = calcNoteDiss(calcAllSublists(calcAllCombinedFactorizations(maxfrac)), bellWidth, notecount);
	}

	/**
	 * For each note in the noterange, calculate the minimum dissonance value which results of bellcurves centered about all the fraction values respectively multiplied with the individual dissonances (numerator * denominator).
	 * @param rationals A list of rational numbers to consider for calculating the individual dissonances. Each rational number denotes a point (x=numerator/denominator, y=numerator*denominator) on the relative frequency / dissonance plane.
	 * @param bellWidth The width of a bell curve that each rational number gets "fuzzified" by (on a log scale). bellWidth > 0.
	 * @param notecount All single dissonances for the halftone distances from 0 to notecount - 1 get calculated. As the result is symmetrical, only one side gets calculated. notecount > 0.
	 * @return A list of dissonances for the different note tuples. Index 0 is unison, index 1 is the dissonance of a base note to one halfstep above, index 2 is the dissonance of a base note to two halfsteps above etc. Yet there is no taking account for volumes.
	 */
	private ArrayList<Double> calcNoteDiss(ArrayList<Rational> rationals, double bellWidth, int notecount){
		ArrayList<Double> noteDiss = new ArrayList<Double>();
		
		for(int notedistance = 0; notedistance<notecount; notedistance++){
			double minDiss = Double.MAX_VALUE;
			
			Rational rmin = null;
			
			for(Rational r : rationals){
				//TODO maybe other curve or better parameters for curve
				double diss = r.getDiss() * Math.exp(( (-(Math.log(r.getValue())/Math.log(2)*12) + notedistance)*(-(Math.log(r.getValue())/Math.log(2)*12) + notedistance) / (2*bellWidth*bellWidth)));
				
				if(diss < minDiss){
					minDiss = diss;
					rmin = r;
				}
			}
			
			logger.config(notedistance + " - " + rmin + " - " + minDiss);
			noteDiss.add(minDiss);
		}
		
		return noteDiss;
	}
	
	
	/**
	 * Calculates all sublists for all factor lists respectively.
	 * @param factorlist A list of lists with factors which are individually put in the nominator or denominator.
	 * @return A list of fractions which represent all the sublists.
	 */
	private ArrayList<Rational> calcAllSublists(ArrayList<ArrayList<Integer>> factorlist){
		ArrayList<Rational> sublists = new ArrayList<Rational>();
		
		for(ArrayList<Integer> factors : factorlist){
			ArrayList<Rational> rationals = calcSublists(factors);
			sublists.addAll(rationals);
		}
		
		return sublists;
	}
	
	/**
	 * Calculates from a given list of integers all possibilities of putting them on the left or right side of a fraction bar.
	 * If intlist is empty it will still return 1/1.
	 * @param intlist A list of integers which can all be put on either the numerator or the denominator. Size can be of a maximum of 31 elements (Integer.SIZE-1).
	 * @return A list of all possible rational numbers.
	 */
	private ArrayList<Rational> calcSublists(ArrayList<Integer> intlist){
		ArrayList<Rational> sublists = new ArrayList<Rational>();
		
		assert(Integer.SIZE-1 < intlist.size());
		//can not happen with the prime factorization of an integer number as the first whole number that's got more combined prime factors is the multiplication of the first 32 primes and that's way bigger than the maximum integer: 525896479052627740771371797072411912900610967452630

		for(int walkthrough = 0; walkthrough < 1<<intlist.size(); walkthrough++){
			int num = 1;
			int denom = 1;
			
			for(int place = 0; place<intlist.size(); place++){
				if((walkthrough & 1<<place) > 0){
					num *= intlist.get(place);
				}else{
					denom *= intlist.get(place);
				}
			}

			sublists.add(new Rational(num,denom));
		}
		
		return sublists;
	}
	
	/**
	 * Calculates all factorizations from 1 to upto.
	 * Same primes get multiplied back together, so not {2,2,3,3,5} but {4,9,5}.
	 * Needs to calculate all prime numbers in the range of [2,upto], does this only once for all integer's factorizations to save unnecessary computation time.
	 * @param upto The maximum integer to calculate the factorization for. upto numbers smaller than 1 get replaced by 1.
	 * @return A list containing upto lists with the factorizations of the single integers.
	 */
	private ArrayList<ArrayList<Integer>> calcAllCombinedFactorizations(int upto){
		ArrayList<ArrayList<Integer>> allFactors = new ArrayList<ArrayList<Integer>>();
		
		if(upto < 1){
			upto = 1;
		}
		
		ArrayList<Integer> primes = calcPrimes(upto);
		
		for(int num = 1; num<=upto; num++){
			ArrayList<Integer> factors;
			factors = calcCombinedFactorization(num, primes);
			
			allFactors.add(factors);
		}
		
		return allFactors;
	}
	
	/**
	 * Calculates the factorization for the given integer number.
	 * Same primes get multiplied back together, so not {2,2,3,3,5} but {4,9,5}.
	 * @param num The number to calculate the prime factorization for.
	 * @param primes The list of primes in the range of [2,num].
	 * @return A list with all (same primes back multipled) prime factors of the given number. Returns an empty list if num < 1.
	 */
	private ArrayList<Integer> calcCombinedFactorization(int num, ArrayList<Integer> primes){
		ArrayList<Integer> factors = new ArrayList<Integer>();
		
		if(num <= 1){
			return factors;
		}
		
		for(int p : primes){
			int mulfact = 1;
			while(num%p == 0){
				num /= p;
				mulfact *= p;
			}
			if(mulfact != 1){
				factors.add(mulfact);
			}
			if(num == 1){
				break;
			}
		}
		
		return factors;
	}
	
	/**
	 * Calculates all primes from 2 to upto.
	 * @param upto Maximum prime to detect.
	 * @return A list of prime numbers. Returns an empty list if upto < 1.
	 */
	private ArrayList<Integer> calcPrimes(int upto){
		ArrayList<Integer> primes = new ArrayList<Integer>();
		
		if(upto <= 1){
			return primes;
		}
		
		primes.add(2);
		
		for(int p = 3; p<=upto; p+=2){
			boolean isDivisible = false;
			for(int i : primes){
				//TODO do not walk through ALL primes here.. up to 1/3 would be enough
				if(p%i == 0){
					isDivisible = true;
					break; //not a prime
				}
			}
			if(!isDivisible){
				//is a new prime
				primes.add(p);
			}
		}
		
		return primes;
	}
}
