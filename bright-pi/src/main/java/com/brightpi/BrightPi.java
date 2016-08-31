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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/**
 * Provides an API to control the Bright Pi that can be purchased at @see <a href="https://www.pi-supply.com/product/bright-pi-bright-white-ir-camera-light-raspberry-pi/">Pi Supply</a>
 * 
 * @author Mike Westbrook
 *
 */
public class BrightPi implements BrightPiBrightness
{	
	private static final byte DEFAULT_LED_BRIGHTNESS = LED_MIN;

	/**
	 * Only one instance of BrightPi since there should only be one on a Raspberry Pi.
	 */
	private static BrightPi ME = null;
	
	/**
	 * Default gain for BrightPi.
	 */
	public static final BrightPiGain DEFAULT_GAIN = BrightPiGain.LOW_GAIN;

	/**
	 * Defaults mode for BrightPi.
	 */
	public static final BrightPiMode DEFAULT_MODE = BrightPiMode.OFF;

	/**
	 * I2C address for BrightPi.
	 */
	public static final int BRIGHTPI_ADDRESS = 0x70;
	
	/**
	 * Address used to turn individual LEDs on or off. Value written at this address controls which LEDs are activated.
	 */
	private static final int LED_MASK_ADDRESS = 0x00;
	
	/**
	 * Maps modes to their appropriate values.
	 */
	private final Map<BrightPiMode, Byte> my_modes = new HashMap<BrightPiMode, Byte>();

	/**
	 * Actual BrightPi I2C device.
	 */
	private final I2CDevice my_brightpi;
	
	/**
	 * Current mode for BrightPi.
	 */
	private BrightPiMode my_mode = DEFAULT_MODE;

	/**
	 * Current gain for BrightPi.
	 */
	private BrightPiGain my_gain;

	/**
	 * Indicates if BrightPi is current on.
	 */
	private boolean i_am_on;
	
	/**
	 * Gets instance of actual BrightPi device and resets it to default mode.
	 */
	private BrightPi()
	{
		populateModes();
		I2CDevice device = null;
		try
		{
			final I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_1);
			device = i2c.getDevice(BRIGHTPI_ADDRESS);
		} catch (UnsupportedBusNumberException e)
		{
			// TODO Exception handling
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Exception handling
			e.printStackTrace();
		}
		
		my_brightpi = device;
		clear();
	}
	
	/**
	 * Resets BrightPi.
	 */
	public void clear()
	{
		off();
		for (final BrightPiLED led : BrightPiLED.values())
		{
			write(led.getAddress(), DEFAULT_LED_BRIGHTNESS);
		}
		
		setMode(DEFAULT_MODE);
		setGain(DEFAULT_GAIN);
	}

	/**
	 * @param the_led The LED to fade.
	 * @param the_start Brightness value to start at.
	 * @param the_end Brightness value to end at.
	 * @param the_delay Delay between brightness changes.
	 */
	public void fade(final BrightPiLED the_led, final byte the_start, final byte the_end, final long the_delay)
	{
		byte value = the_start;
		while (value != the_end)
		{
			setLEDBrightness(the_led, value);
			if (the_start < the_end)
			{
				value = (byte) (value + 0x01);
			} else
			{
				value = (byte) (value - 0x01);
			}
			
			try
			{
				Thread.sleep(the_delay);
			} catch (InterruptedException e)
			{
				// TODO Exception handling
				e.printStackTrace();
			}
		}
		
		write(the_led.getAddress(), value);
	}

	/**

	 * @return True if BrightPi is on.
	 */
	public boolean isOn()
	{
		return i_am_on;
	}

	/**

	 * @return Current gain of BrightPi.
	 */
	public BrightPiGain getGain()
	{
		return my_gain;
	}
	
	/**

	 * @return Current mode of BrightPi.
	 */
	public BrightPiMode getMode()
	{
		return my_mode;
	}
	
	/**

	 * @return A bit-mask of LEDs that are currently on.
	 */
	public byte getCurrentOnLEDs()
	{
		try
		{
			return (byte) my_brightpi.read(LED_MASK_ADDRESS);
		} catch (IOException e)
		{
			// TODO Exception handling
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Turns all LEDs off.
	 */
	public void off()
	{
		write(LED_MASK_ADDRESS, BrightPiMode.OFF.getLeds());
		i_am_on = false;
	}

	/**
	 * Turns all LEDs for the current mode on.
	 */
	public void on()
	{
		write(LED_MASK_ADDRESS, my_modes.get(getMode()));
		i_am_on = true;
	}

	/**
	 * Sets the gain for the device.
	 * 
	 * @param the_gain Currently only low, medium, and high are available.
	 */
	public void setGain(final BrightPiGain the_gain)
	{
		my_gain = the_gain;
		write(BrightPiGain.GAIN_CHIP_ADDRESS, getGain().getGain());
	}
	
	/**
	 * Sets the brightness of all LEDs to the specified brightness.
	 * 
	 * @param the_value Brightness to set the LEDs.
	 */
	public void setLEDBrightness(final byte the_value)
	{
		for (final BrightPiLED led : BrightPiLED.values())
		{
			setLEDBrightness(led, the_value);
		}
	}
	
	/**
	 * Sets the brightness of an individual LED.
	 * 
	 * @param the_led The LED to adjust the brightness of.
	 * @param the_value The value to set the brightness to.
	 */
	public void setLEDBrightness(final BrightPiLED the_led, final byte the_value)
	{
		if (getMode() != BrightPiMode.INDIVIDUAL)
		{
			my_modes.put(BrightPiMode.INDIVIDUAL, getCurrentOnLEDs());
			setMode(BrightPiMode.INDIVIDUAL);
		}
		
		write(the_led.getAddress(), the_value);
	}

	/**
	 * Sets the mode of BrightPi.
	 * 
	 * @param the_mode The mode to set the device to.
	 */
	public void setMode(final BrightPiMode the_mode)
	{
		my_mode = the_mode;
		if (isOn())
		{
			//Write the new mode to the LED mask if device is on.
			on();
		}
	}
	
	/**
	 * @return The single instance of BrightPi.
	 */
	public static BrightPi instance()
	{
		if (ME == null)
		{
			ME = new BrightPi();
		}
		
		return ME;
	}

	/**
	 * Stores the BrightPi modes in a convenient location.
	 */
	private void populateModes()
	{
		for (final BrightPiMode mode : BrightPiMode.values())
		{
			my_modes.put(mode, mode.getLeds()); 
		}
	}

	/**
	 * Writes the value to the address.
	 * 
	 * @param the_address The address to write to.
	 * @param the_data The value to write to the address.
	 */
	private synchronized void write(final int the_address, final byte the_data)
	{
		try
		{
			my_brightpi.write(the_address, the_data);
		} catch (IOException e)
		{
			// TODO Exception handling
			e.printStackTrace();
		}
	}
}
