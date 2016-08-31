package test.brightpi;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.brightpi.BrightPi;
import com.brightpi.BrightPiBrightness;
import com.brightpi.BrightPiGain;
import com.brightpi.BrightPiLED;
import com.brightpi.BrightPiMode;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class TestBrightPi
{
	private static final long DELAY = 10l;

	private static final int LED_MASK_ADDRESS = 0x00;

	public static final int BRIGHTPI_ADDRESS = 0x70;

	private I2CDevice my_brightpi;

	@Before
	public void before() throws IOException
	{
		if (my_brightpi == null)
		{
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
		}
		
		BrightPi.instance().clear();
	}

	@Test
	public void testBrightness()
	{
		final BrightPi bp = BrightPi.instance();
		bp.setGain(BrightPiGain.HIGH_GAIN);
		bp.on();
		for (final BrightPiLED led : BrightPiLED.values())
		{
			bp.clear();
			for (byte value = BrightPiBrightness.LED_MIN; value <= BrightPiBrightness.LED_MAX; value = (byte) (value + 1))
			{
				bp.setLEDBrightness(led, value);
				try
				{
					assertTrue("LED " + led.name() + " did not set to the correct brightness of " + value, (byte) my_brightpi.read(led.getAddress()) == value);
				} catch (IOException e)
				{
					fail("IOException : " + e.getMessage());
				}
			}
		}
	}
	
	@Test
	public void testClear()
	{
		final BrightPi bp = BrightPi.instance();
		for (final BrightPiGain gain : BrightPiGain.values())	
		{
			for (byte brightness = BrightPiBrightness.LED_MIN; brightness <= BrightPiBrightness.LED_MAX; brightness = (byte) (brightness + 1))
			{
				for (final BrightPiMode mode : BrightPiMode.values())
				{
					//TODO something wrong top left and bottom right dimly lit when should be off after setting mode.
					bp.on();
					bp.setMode(mode);
					bp.setLEDBrightness(brightness);
					bp.setGain(gain);
					bp.clear();
					try
					{
						verifyDeviceCleared();
					} catch (IOException e)
					{
						fail("IOException : " + e.getMessage());
					}
				}
				
				bp.clear();
				for (final BrightPiLED led : BrightPiLED.values())
				{
					bp.on();
					bp.setLEDBrightness(led, brightness);
					bp.setGain(gain);
					bp.clear();
					try
					{
						verifyDeviceCleared();
					} catch (IOException e)
					{
						fail("IOException : " + e.getMessage());
					}
				}
			}
		}
	}

	@Test
	public void testFade()
	{
		final BrightPi bp = BrightPi.instance();
		for (final BrightPiLED led : BrightPiLED.values())
		{
			bp.on();
			bp.setGain(BrightPiGain.HIGH_GAIN);
			bp.setMode(BrightPiMode.MIXED);
			bp.fade(led, BrightPiBrightness.LED_MIN, BrightPiBrightness.LED_MAX, DELAY);
			try
			{
				assertTrue("LED " + led.name() + " not a full brightness.", (byte) my_brightpi.read() == BrightPiBrightness.LED_MAX);
			} catch (IOException e)
			{
				fail("IOException : " + e.getMessage());
			}

			bp.fade(led, BrightPiBrightness.LED_MAX, BrightPiBrightness.LED_MIN, DELAY);
			try
			{
				assertTrue("LED " + led.name() + " did not dim all the way.", (byte) my_brightpi.read() == BrightPiBrightness.LED_MIN);
			} catch (IOException e)
			{
				fail("IOException : " + e.getMessage());
			}

		}
	}

	@Test
	public void testGain()
	{
		final BrightPi bp = BrightPi.instance();
		for (final BrightPiGain gain : BrightPiGain.values())
		{
			bp.clear();
			bp.setGain(gain);
			try
			{
				final byte curr = (byte) my_brightpi.read(BrightPiGain.GAIN_CHIP_ADDRESS);
				assertTrue("Gain was not set correctly.", curr == gain.getGain());
			} catch (IOException e)
			{
				fail("IOException : " + e.getMessage());
			}
		}
	}

	@Test
	public void testLED()
	{
		final BrightPi bp = BrightPi.instance();
		for (final BrightPiMode mode : BrightPiMode.values())
		{
			bp.clear();
			bp.setMode(mode);
			try
			{
				//ensure all leds are off.
				assertTrue("All LEDs are not turned off.", (byte) my_brightpi.read(LED_MASK_ADDRESS) == BrightPiMode.OFF.getLeds());
				bp.on();

				//ensure all leds are on.
				final byte curr = (byte) my_brightpi.read(LED_MASK_ADDRESS);
				assertTrue(mode.name() + " LEDs did not turn on.", curr == mode.getLeds());
				bp.off();

				//ensure all leds are off.
				assertTrue(mode.name() + " LEDs did not turn off.", my_brightpi.read(LED_MASK_ADDRESS) == BrightPiMode.OFF.getLeds());

			} catch (IOException e)
			{
				fail("IOException : " + e.getMessage());
			}
		}

	}

	@AfterClass
	public static void afterClass()
	{
		BrightPi.instance().clear();
	}

	private Map<BrightPiLED, Integer> getDimValues() throws IOException
	{
		final Map<BrightPiLED, Integer> map = new HashMap<BrightPiLED, Integer>();
		for (final BrightPiLED led : BrightPiLED.values())
		{
			map.put(led, my_brightpi.read(led.getAddress()));
		}

		return map;
	}

	private void verifyDeviceCleared() throws IOException
	{
		final byte mode = (byte) my_brightpi.read(LED_MASK_ADDRESS);
		final Map<BrightPiLED, Integer> dim_values = getDimValues();
		final byte gain = (byte) my_brightpi.read(BrightPiGain.GAIN_CHIP_ADDRESS);
		assertTrue("Set mode did not reset.", BrightPi.instance().getMode() == BrightPi.DEFAULT_MODE);
		assertTrue("LEDs do not match default mode.", mode == BrightPi.DEFAULT_MODE.getLeds());
		assertTrue("LED dimmer values did not reset.", dimValuesCleared(dim_values));
		assertTrue("Gain not reset", gain == BrightPi.DEFAULT_GAIN.getGain());
	}

	private static boolean dimValuesCleared(final Map<BrightPiLED, Integer> dim_values)
	{
		boolean result = dim_values != null;
		for (final Entry<BrightPiLED, Integer>  entry : dim_values.entrySet())
		{
			result = entry.getValue() == BrightPiBrightness.LED_MIN;
			if (!result)
			{
				break;
			}
		}

		return result;
	}
}
