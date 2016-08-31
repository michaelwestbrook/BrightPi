package com.brightpi;

public enum BrightPiGain
{
	//Values for specifying the LED gain. Possible values range from 0x01 to 0x0f.
	//This value is multiplied by by the decimal multiplier in each dimming control register.
	LOW_GAIN((byte) 0x01), MEDIUM_GAIN((byte) 0x09), HIGH_GAIN((byte) 0x0f);
	
	public static final int GAIN_CHIP_ADDRESS = 0x09;
	
	private final byte my_brightness;
	
	private BrightPiGain(final byte the_brightness)
	{
		my_brightness = the_brightness;
	}
	
	public byte getGain()
	{
		return my_brightness;
	}
}
