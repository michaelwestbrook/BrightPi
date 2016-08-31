package com.brightpi;

/**
 * Used to address LEDs.
 * 
 * @author Mike
 *
 */
public enum BrightPiLED
{	
	/**
	 * White LED #1
	 */
	W1(0x02),

	/**
	 * White LED #2
	 */
	W2(0x04),
	/**
	 * White LED #3
	 */
	W3(0x05),

	/**
	 * White LED #4
	 */
	W4(0x07), 

	/**
	 * IR LED bank #1
	 */
	IR1(0x01), 

	/**
	 * IR LED bank #2
	 */
	IR2(0x03), 

	/**
	 * IR LED bank #3
	 */IR3(0x06), 

	 /**
	  * IR LED bank #4
	  */
	 IR4(0x08);

	private final int my_led_address;

	private BrightPiLED(final int the_led_address)
	{
		my_led_address = the_led_address;
	}

	/**
	 * 
	 * @return The chip address of this LED.
	 */
	public int getAddress()
	{
		return my_led_address;
	}
}
