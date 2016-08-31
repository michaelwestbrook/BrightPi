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
