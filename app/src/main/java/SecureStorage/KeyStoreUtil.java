package SecureStorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

public class KeyStoreUtil {
    private static final String KEY_ALIAS = "YourKeyAlias"; // Aquí debes usar un alias de clave seguro

    public static void saveEncryptedPassword(Context context, String password) {
        try {
            // Generar una clave secreta para cifrar la contraseña
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build();
            keyGenerator.init(keyGenParameterSpec);
            SecretKey secretKey = keyGenerator.generateKey();

            // Cifrar la contraseña utilizando el Cipher
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedPassword = cipher.doFinal(password.getBytes());

            // Almacenar la contraseña cifrada en SharedPreferences o cualquier otro lugar
            String encryptedPasswordString = Base64.encodeToString(encryptedPassword, Base64.DEFAULT);
            SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("encrypted_password", encryptedPasswordString);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
