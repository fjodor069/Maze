package com.gameproject.graphicstest;

public class Vector2
{
	public int X;
	public int Y;
	
	public Vector2(int x, int y)
	{
		X = x;
		Y = y;
	}
	
	public Vector2 add(Vector2 a, Vector2 b)
	{
		return new Vector2(a.X + b.X, a.Y + b.Y);
	} 
	
	public void add(Vector2 a)
	{
		X += a.X;
		Y += a.Y;
	}
	
	public String toString()
	{
		return "(" + X + "," + Y + ")" ;
	}
	
	
}
