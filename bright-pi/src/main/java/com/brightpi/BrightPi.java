package com.brightpi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class BrightPi implements BrightPiBrightness
{	
	private static BrightPi ME = null;
	
	public static final BrightPiGain DEFAULT_GAIN = BrightPiGain.LOW_GAIN;

	public static final BrightPiMode DEFAULT_MODE = BrightPiMode.OFF;

	public static final int BRIGHTPI_ADDRESS = 0x70;
	
	private static final int LED_MASK_ADDRESS = 0x00;
	
	private final Map<BrightPiMode, Byte> my_modes = new HashMap<BrightPiMode, Byte>();

	private final I2CDevice my_brightpi;
	
	private BrightPiMode my_mode = DEFAULT_MODE;

	private BrightPiGain my_gain;

	private boolean i_am_on;
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		my_brightpi = device;
		clear();
	}
	
	public void clear()
	{
		off();
		for (final BrightPiLED led : BrightPiLED.values())
		{
			write(led.getAddress(), LED_MIN);
		}
		
		setMode(DEFAULT_MODE);
		setGain(DEFAULT_GAIN);
	}

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		write(the_led.getAddress(), value);
	}

	public boolean isOn()
	{
		return i_am_on;
	}

	public BrightPiGain getGain()
	{
		return my_gain;
	}
	
	public BrightPiMode getMode()
	{
		return my_mode;
	}
	
	public byte getCurrentOnLEDs()
	{
		try
		{
			return (byte) my_brightpi.read(LED_MASK_ADDRESS);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public void off()
	{
		write(LED_MASK_ADDRESS, BrightPiMode.OFF.getLeds());
		i_am_on = false;
	}

	public void on()
	{
		write(LED_MASK_ADDRESS, my_modes.get(getMode()));
		i_am_on = true;
	}

	public void setGain(final BrightPiGain the_gain)
	{
		my_gain = the_gain;
		write(BrightPiGain.GAIN_CHIP_ADDRESS, getGain().getGain());
	}
	
	public void setLEDBrightness(final byte the_value)
	{
		for (final BrightPiLED led : BrightPiLED.values())
		{
			setLEDBrightness(led, the_value);
		}
	}
	
	public void setLEDBrightness(final BrightPiLED the_led, final byte the_value)
	{
		if (getMode() != BrightPiMode.INDIVIDUAL)
		{
			my_modes.put(BrightPiMode.INDIVIDUAL, getCurrentOnLEDs());
			setMode(BrightPiMode.INDIVIDUAL);
		}
		
		write(the_led.getAddress(), the_value);
	}

	public void setMode(final BrightPiMode the_mode)
	{
		my_mode = the_mode;
		if (isOn())
		{
			//Write the new mode to the LED mask if device is on.
			on();
		}
	}
	
	public static BrightPi instance()
	{
		if (ME == null)
		{
			ME = new BrightPi();
		}
		
		return ME;
	}

	private void populateModes()
	{
		for (final BrightPiMode mode : BrightPiMode.values())
		{
			my_modes.put(mode, mode.getLeds()); 
		}
	}

	private synchronized void write(final int the_address, final byte the_data)
	{
		try
		{
			my_brightpi.write(the_address, the_data);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
