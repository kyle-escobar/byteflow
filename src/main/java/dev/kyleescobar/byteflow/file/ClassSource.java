package dev.kyleescobar.byteflow.file;

public interface ClassSource {
	  Class loadClass(String name) throws ClassNotFoundException ;
}
