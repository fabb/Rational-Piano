package rationalpiano.consonance;

/**
 * This Class represents a rational number (a fraction).
 * 
 * @author Fabian Ehrentraud
 * @date 2010-07-26
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class Rational {
	
	private final int num;
	private final int denom;

	/**
	 * Numerator and denominator are not allowed to have a greatest common divisor which is not 1 (it must already be shortened).
	 * @param numerator The fraction's numerator
	 * @param denominator The fraction's denominator
	 */
	public Rational(int numerator, int denominator) {
		this.num = numerator;
		this.denom = denominator;
	}

	/**
	 * @return The numerator of this fraction.
	 */
	public int getNumerator() {
		return num;
	}

	/**
	 * @return The denominator of this fraction.
	 */
	public int getDenominator() {
		return denom;
	}
	
	/**
	 * @return The value of this fraction, meaning numerator/denominator.
	 */
	public double getValue(){
		return (double)num/denom;
	}
	
	/**
	 * @return The 'dissonance' of this fraction, meaning numerator*denominator (where the fraction is already shortened).
	 */
	public int getDiss(){
		return num*denom;
	}

	@Override
	public String toString() {
		return num + "/" + denom;
	}
}

