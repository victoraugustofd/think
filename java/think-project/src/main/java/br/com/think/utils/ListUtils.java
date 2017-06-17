package br.com.think.utils;

import java.util.List;

import com.google.common.collect.Lists;

public class ListUtils
{
	public static List< List< String > > divideList( List< String > list, int sizeOfEachList )
	{
		if ( sizeOfEachList <= 0 )
			sizeOfEachList = 1;
		
		return Lists.partition( list, sizeOfEachList );
	}
}