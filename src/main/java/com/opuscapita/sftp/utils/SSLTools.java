package com.opuscapita.sftp.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.interfaces.RSAPublicKey;

public class SSLTools {

	private SSLTools() {

	}

	public static byte[] encode(RSAPublicKey key) {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			byte[] name = "ssh-rsa".getBytes();
			write(name, buf);
			write(key.getPublicExponent().toByteArray(), buf);
			write(key.getModulus().toByteArray(), buf);
			return buf.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void write(byte[] str, OutputStream os) throws IOException {
		for (int shift = 24; shift >= 0; shift -= 8) {
			os.write((str.length >>> shift) & 0xFF);
		}
		os.write(str);
	}

}
