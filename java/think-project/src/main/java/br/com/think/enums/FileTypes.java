package br.com.think.enums;

public enum FileTypes
{
	Excel( "Excel" ), Text( "Text" );
	
	@SuppressWarnings( "unused" )
	private String fileType;
	
	private FileTypes( String fileType )
	{
		setFileType( fileType );
	}
	
	private void setFileType( String fileType )
	{
		this.fileType = fileType;
	}
}