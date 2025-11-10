package kr.co.micube.communication.ees.rms.support; 
import java.nio.charset.StandardCharsets; 
import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec; // AES CBC IV에 필요
import javax.crypto.spec.SecretKeySpec;
import kr.co.micube.component.ees.so.RmsTbProcessRecipeParameterSpecKey; 
import kr.co.micube.core.security.support.SHA256;
public class DataCryptography {
	
	public static byte[] hexToBytes(String hex) { 
		int len = hex.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = 
					(byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i+1), 16));
		} 
		return data;	
	} 
	
	public static SecretKeySpec createRecipeSpecCryptography(RmsTbProcessRecipeParameterSpecKey rmsTbProcessRecipeParameterSpecKey, Integer RecipeVersion)throws Exception{
		String stringKey =null; 
		SecretKeySpec CryptographyKey = null;
		SHA256 sha256 = new SHA256();
		try { 
			stringKey = rmsTbProcessRecipeParameterSpecKey.getPlantId()
					+ rmsTbProcessRecipeParameterSpecKey.getEquipmentId() 
					+ rmsTbProcessRecipeParameterSpecKey.getRecipeId()
					+ RecipeVersion; 
			//stringKey = new String(stringKey.getBytes(), "UTF-8");
			String sha256Hex = new SHA256().encode(stringKey.getBytes()); // HEX → 바이트 배열 (32바이트) 
			byte[] keyBytes = hexToBytes(sha256Hex); // AES-256 키 생성 
			CryptographyKey = new SecretKeySpec(keyBytes, "AES"); 
		} 
		catch (Exception e) {
		
		} 
		return CryptographyKey; 
	} 
		
	public static IvParameterSpec createInitializationVector(String dateTime) throws Exception { 
		String IV = null; 
		byte[] ivBytes = null; 
		byte[] fixedIV = new byte[16]; 
		IvParameterSpec ivSpec = null; 
		try {
			if(dateTime.equals("")) {
				//CASE 1 신규생성 ( Param :"") CASE 2 기존생성IV ( Param :yyyyMMddHHmmssSS ) 
				LocalDateTime now = LocalDateTime.now(); 
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS"); 
				String formatted = now.format(formatter); 
				IV = "MC"+ formatted; 
			}else {
				IV = "MC"+ dateTime; 
			} 
				ivBytes = IV.getBytes(StandardCharsets.UTF_8);
				System.arraycopy(ivBytes, 0, fixedIV, 0, Math.min(ivBytes.length, 16));				
				ivSpec = new IvParameterSpec(fixedIV); 
				
		} 
		catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return ivSpec; 
	} 
	
	public static GCMParameterSpec createInitializationVectorForGCM(String dateTime) throws Exception { 
	    String IV = null; 
	    byte[] ivBytes = null; 
	    byte[] fixedIV = new byte[12]; // GCM은 12바이트 IV 고정
	    GCMParameterSpec gcmSpec = null; 
	    try {
	        if (dateTime.equals("")) {
	            // CASE 1 신규생성 ( Param :"") CASE 2 기존생성IV ( Param :yyyyMMddHHmmssSS ) 
	            LocalDateTime now = LocalDateTime.now(); 
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS"); 
	            String formatted = now.format(formatter); 
	            IV = "MC" + formatted; 
	        } else {
	            IV = "MC" + dateTime; 
	        }
	        ivBytes = IV.getBytes(StandardCharsets.UTF_8);
	        // GCM 모드는 12바이트 IV 사용
	        System.arraycopy(ivBytes, 0, fixedIV, 0, Math.min(ivBytes.length, 12));
	        gcmSpec = new GCMParameterSpec(128, fixedIV); // 인증 태그 길이 128비트
	    } 
	    catch (Exception e) {
	        System.out.println(e.toString());
	    }
	    return gcmSpec; 
	}
	
	
	public static String dataEncrypt(SecretKeySpec secretKey ,IvParameterSpec IV,String Target) throws Exception { 
		byte[] encrpytionByte = null; 
		try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, secretKey, IV);
			encrpytionByte = c.doFinal(Target.getBytes("UTF-8"));
			} catch (Exception e) {
				System.out.println(e.toString());
				} 
		return Base64.getEncoder().encodeToString(encrpytionByte);
				
	}
	
	public static String dataEncryptForGCM(SecretKeySpec secretKey, GCMParameterSpec IV, String Target) throws Exception {
	    byte[] encrpytionByte = null;
	    try {
	        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
	        c.init(Cipher.ENCRYPT_MODE, secretKey, IV);
	        encrpytionByte = c.doFinal(Target.getBytes("UTF-8"));
	    } catch (Exception e) {
	        System.out.println(e.toString());
	    }
	    return Base64.getEncoder().encodeToString(encrpytionByte);
	}
	
	public static String dataDecrypt(SecretKeySpec secretKey ,IvParameterSpec IV,String Target) throws Exception {
		byte[] decryptedBytes = null;
		try { 
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, secretKey, IV);
			decryptedBytes = Base64.getDecoder().decode(Target);
			decryptedBytes = c.doFinal(decryptedBytes);
		} catch (Exception e) {
	
		} 
		return new String(decryptedBytes, StandardCharsets.UTF_8); 
	}
	
	public static String dataDecryptForGCM(SecretKeySpec secretKey ,IvParameterSpec IV,String Target) throws Exception {
		byte[] decryptedBytes = null;
		try { 
			Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
			c.init(Cipher.DECRYPT_MODE, secretKey, IV);
			decryptedBytes = Base64.getDecoder().decode(Target);
			decryptedBytes = c.doFinal(decryptedBytes);
		} catch (Exception e) {
	
		} 
		return new String(decryptedBytes, StandardCharsets.UTF_8); 
	}
	
	public static String createSelectKey (String dateTime)throws Exception{ 
		String stringKey =null; 
		SecretKeySpec CryptographyKey = null;
		String sha256Hex = null; 
		SHA256 sha256 = new SHA256(); 
		try { 
			sha256Hex = new SHA256().encode(dateTime.getBytes()); 
			
		} catch (Exception e) {
				
		} 
		return sha256Hex;
	} 
	
	

}
	