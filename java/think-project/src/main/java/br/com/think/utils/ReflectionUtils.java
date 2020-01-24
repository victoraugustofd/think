package br.com.think.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public abstract class ReflectionUtils
{
	public static List< Field > getFields( Class cls )
	{
		List< Field > fieldsList;

		fieldsList = Arrays
						.asList( cls.getDeclaredFields() )
							.stream()
							.collect( Collectors.toList() );

		return fieldsList;
	}

	public static List< String > getFieldsNames( Class cls )
	{
		List< String > fieldsNamesList;
		
		fieldsNamesList = getFields( cls )
							.stream()
							.map( Field::getName )
							.collect( Collectors.toList() );
		
		return fieldsNamesList;
	}

	public static List< Method > getMethodsList( Class cls )
	{
		List< Method > methodsList;

		methodsList = Arrays
						.asList( cls.getDeclaredMethods() )
							.stream()
							.collect( Collectors.toList() );

		return methodsList;
	}

	public static Map< String, Method > getMethodsMap( Class cls )
	{
		Map< String, Method > methodsTreeMap = new TreeMap<>( String.CASE_INSENSITIVE_ORDER );
		Map< String, Method > methodsMap;

		methodsMap = getMethodsList( cls )
						.stream()
						.collect( Collectors.toMap( Method::getName, method -> method ) );

		methodsTreeMap.putAll( methodsMap );

		return methodsTreeMap;
	}

	public static Map< String, Method > getGetterMethods( Class cls )
	{
		Map< String, Method > gettersTreeMap = new TreeMap<>( String.CASE_INSENSITIVE_ORDER );
		Map< String, Method > getters;

		getters = getMethodsMap( cls )
					.values()
						.stream()
						.filter( method -> method.getName().startsWith( "get" ) )
						.collect( Collectors.toMap( Method::getName, method -> method ) );

		gettersTreeMap.putAll( getters );

		return gettersTreeMap;
	}
	
	public static Map< String, Method > getSetterMethods( Class cls )
	{
		Map< String, Method > settersTreeMap = new TreeMap<>( String.CASE_INSENSITIVE_ORDER );
		Map< String, Method > setters;

		setters = getMethodsList( cls )
					.stream()
					.filter( method -> method.getName().startsWith( "set" ) )
					.collect( Collectors.toMap( Method::getName, method -> method ) );

		settersTreeMap.putAll( setters );

		return settersTreeMap;
	}
}