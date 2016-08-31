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
 * Currently only supports low, medium, or high. With slight modification any value from 0x01 to 0x0f can be used.
 * 
 * @author Mike Westbrook
 *
 */
public enum BrightPiGain
{
	LOW_GAIN((byte) 0x01), MEDIUM_GAIN((byte) 0x09), HIGH_GAIN((byte) 0x0f);
	
	public static final int GAIN_CHIP_ADDRESS = 0x09;
	
	/**
	 * This value is multiplied by by the decimal multiplier in each dimming control register.
	 */
	private final byte my_brightness;
	
	private BrightPiGain(final byte the_brightness)
	{
		my_brightness = the_brightness;
	}
	
	/**
	 * 
	 * @return The byte value for this gain setting.
	 */
	public byte getGain()
	{
		return my_brightness;
	}
}
