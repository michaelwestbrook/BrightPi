package com.brightpi;

public enum BrightPiMode
{	
	/**
	 * White LEDs.
	 */
	WHITE((byte) 0x5a), 
	
	/**
	 * Infrared LEDs.
	 */
	IR((byte) 0xa5), 
	
	/**
	 * All LEDs.
	 */
	MIXED((byte) 0xff), 
	
	/**
	 * No LEDs.
	 */
	OFF((byte) 0x00),
	
	/**
	 * Individual LEDs addressed.
	 */
	INDIVIDUAL((byte) 0x00);
	
	private final byte my_leds;
	
	private BrightPiMode(final byte the_leds)
	{
		my_leds = the_leds;
	}
	
	public byte getLeds()
	{
		return my_leds;
	}
}
