/*
 * MIT License
 * 
 * Copyright (c) 2016 Michael Westbrook
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.brightpi;

/**
 * Sets the mode of the BrightPi device. Current modes are off, only white LEDs, only infrared LEDs,
 * both white and infrared LEDs, or individually addressed.
 * 
 * @author Mike Westbrook
 *
 */
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

	/**
	 * @return A bit-mask representing the LEDs that should be on in this mode.
	 */
	public byte getLeds()
	{
		return my_leds;
	}
}
