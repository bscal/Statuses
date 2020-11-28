package me.bscal.statuses.utils;

public class Utils
{

	public static class MathUtils
	{
		public static int Clamp(int value, int min, int max)
		{
			return value > max ? max : value < min ? min : value;
		}

		public static long Clamp(long value, long min, long max)
		{
			return value > max ? max : value < min ? min : value;
		}

		public static float Clamp(float value, float min, float max)
		{
			return value > max ? max : value < min ? min : value;
		}

		public static double Clamp(double value, double min, double max)
		{
			return value > max ? max : value < min ? min : value;
		}
	}

}
