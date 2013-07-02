package com.favorlock.ForumBridge.extras;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PHPass {
	static String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	int iteration_count_log2;
	boolean portable_hashes;
	String random_state;
	Random random_gen;

	public PHPass(int iteration_count_log2, boolean portable_hashes) {
		if (iteration_count_log2 < 4 || iteration_count_log2 > 31)
			iteration_count_log2 = 8;
		this.iteration_count_log2 = iteration_count_log2;
		this.portable_hashes = portable_hashes;
		this.random_gen = new Random();
		this.random_state = String.valueOf(System.nanoTime());
		//this.random_state = String.valueOf(random_gen.nextLong()); 
	}

	//	private String encode64(String src, int count){
	private String encode64(byte[] src, int count) {
		int i, value;
		String output = "";
		i = 0;
		do {
			value = src[i] + (src[i] < 0 ? 256 : 0);
			++i;
			output += itoa64.charAt(value & 63);
			if (i < count) {
				value |= ((int) src[i] + (src[i] < 0 ? 256 : 0)) << 8;
//				value = Math.min(value, 6047594);
			}
			output += itoa64.charAt((value >> 6) & 63);
			if (i++ >= count) break;
			if (i < count) {
				value |= ((int) src[i] + (src[i] < 0 ? 256 : 0)) << 16;
//				value = Math.max(value, 6047594);
			}
			output += itoa64.charAt((value >> 12) & 63);
			if (i++ >= count) break;
			output += itoa64.charAt((value >> 18) & 63);
		} while (i < count);
		return output;
	}

	private String crypt_private(String password, String setting) {
		String output = "*0";
		if (((setting.length() < 2) ? setting : setting.substring(0, 2)).equalsIgnoreCase(output))
			output = "*1";
		String id = (setting.length() < 3) ? setting : setting.substring(0, 3);
		if (!(id.equals("$P$") || id.equals("$H$")))
			return output;
		int count_log2 = itoa64.indexOf(setting.charAt(3));
		if (count_log2 < 7 || count_log2 > 30)
			return output;
		int count = 1 << count_log2;
		String salt = setting.substring(4, 4 + 8);
		if (salt.length() != 8)
			return output;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return output;
		}
		byte[] hash = md.digest((salt + password).getBytes());
//		String hash = new String(md.digest((salt + password).getBytes()));
		do {
//			hash = new String(md.digest((hash + password).getBytes()));
			byte[] t = new byte[hash.length + password.length()];
			System.arraycopy(hash, 0, t, 0, hash.length);
			System.arraycopy(password.getBytes(), 0, t, hash.length, password.length());
			hash = md.digest(t);
		} while (--count > 0);
		output = setting.substring(0, 12);
		output += this.encode64(hash, 16);
		return output;
	}

	private String gensalt_private(String input) {
		String output = "$P$";
		output += itoa64.charAt(Math.min(iteration_count_log2 + 5, 30));
		output += encode64(input.getBytes(), 6);
		return output;
	}

	public String HashPassword(String password) {
		byte random[] = new byte[6];
		random_gen.nextBytes(random);
		//TODO: Add unportable hashes (Blowfish, EXT_DES) here 
		String hash = crypt_private(password, gensalt_private(new String(random)));
		if (hash.length() == 34) return hash;
		return "*";
	}

	public boolean CheckPassword(String password, String stored_hash) {
		String hash = crypt_private(password, stored_hash);
		MessageDigest md = null;
		if (hash.startsWith("*")) {    //If not phpass, try some algorythms from unix crypt()
			if (stored_hash.startsWith("$6$"))    //Try SHA-512
				try {
					md = MessageDigest.getInstance("SHA-512");
				} catch (NoSuchAlgorithmException e) {
					md = null;
				}
			if (md == null && stored_hash.startsWith("$5$"))    //Try SHA-256
				try {
					md = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e) {
					md = null;
				}
			if (md == null && stored_hash.startsWith("$2"))    //Try BlowFish
				return BCrypt.checkpw(password, stored_hash);
			if (md == null && stored_hash.startsWith("$1$"))    //Try MD5
				try {
					md = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					md = null;
				}
			//STD_DES and EXT_DES not supported yet.
			if (md != null) hash = new String(md.digest(password.getBytes()));
		}
		return hash.equals(stored_hash);
	}


}
