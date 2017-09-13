package br.com.think.excel;

import java.awt.*;
import java.util.List;

public class ExcelWriterParameters extends ExcelParameters
{
	private Color	backgroundColor;
	private Color	fontColor;
	private Font	font;
	private boolean	isBordered;
	
	public ExcelWriterParameters( String excelFileAbsolutePath, Class cls, List< String > fields, Color backgroundColor, Color fontColor, Font font,
	        boolean isBordered )
	{
		super( excelFileAbsolutePath, cls, fields );
		setBackgroundColor( backgroundColor );
		setFontColor( fontColor );
		setFont( font );
		setBordered( isBordered );
	}

	public Color getBackgroundColor()
	{
		return backgroundColor;
	}
	
	public Color getFontColor()
	{
		return fontColor;
	}
	
	public Font getFont()
	{
		return font;
	}
	
	public boolean isBordered()
	{
		return isBordered;
	}
	
	public void setBackgroundColor( Color backgroundColor )
	{
		if( backgroundColor != null )
			this.backgroundColor = backgroundColor;
		else
			this.backgroundColor = Color.WHITE;
	}
	
	public void setFontColor( Color fontColor )
	{
		if( fontColor != null )
			this.fontColor = fontColor;
		else
			this.fontColor = Color.BLACK;
	}
	
	public void setFont( Font font )
	{
		if( font != null )
			this.font = font;
		else
			this.font = new Font( "Calibri", Font.PLAIN, 11 );
	}
	
	public void setBordered( boolean isBordered )
	{
		this.isBordered = isBordered;
	}
	
	public boolean isValid()
	{
		return super.isValid() 		   &&
			   backgroundColor != null &&
			   fontColor 	   != null &&
			   font 		   != null;
	}
}