package rationalpiano.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for Integer Fields describing a Minimum and Maximum value.
 * 
 * @author Fabian Ehrentraud
 * @date 2010-07-26
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldIntegerMinMax {

	/**
	 * The minimum value for the according Integer field.
	 */
	public int min();

	/**
	 * The maximum value for the according Integer field.
	 */
	public int max();
	
}
