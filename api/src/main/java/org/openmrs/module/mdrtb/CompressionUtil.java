/**
 * 
 */
package org.openmrs.module.mdrtb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;

import com.thoughtworks.xstream.core.util.Base64Encoder;

public class CompressionUtil {
	
	public static String compressCode(String str) throws IOException {
		if (str == null || str.isEmpty()) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
	        gzip.write(str.getBytes(StandardCharsets.UTF_8));
	    }
		return (new Base64Encoder().encode(out.toByteArray()));
	}
	
	public static String decompressCode(String str) throws IOException {
		if (str == null || str.isEmpty()) {
			return str;
		}
		byte[] bytes = new Base64Encoder().decode(str);
		try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
	        InputStreamReader reader = new InputStreamReader(gis, StandardCharsets.UTF_8);
	        return IOUtils.toString(reader);
	    }
	}
}
