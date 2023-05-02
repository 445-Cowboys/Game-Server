package org.server;

//This class is copied and pasted from Nathan's for encryption

import com.google.crypto.tink.*;
import com.google.crypto.tink.aead.AeadConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

public final class AEAD {

    final static String keyType = "AES128_GCM";
    private Aead aead;
    private KeysetHandle keysetHandle;

    public AEAD() throws GeneralSecurityException {
        AeadConfig.register();
    }

    public void genKeySet() throws GeneralSecurityException, IOException {
        keysetHandle = KeysetHandle.generateNew(KeyTemplates.get(keyType));
        aead = keysetHandle.getPrimitive(Aead.class);
    }

    public byte[] encrypt(byte[] plaintext) throws GeneralSecurityException {
        return aead.encrypt(plaintext, null);
    }

    public byte[] decrypt(byte[] ciphertext) throws GeneralSecurityException {
        return aead.decrypt(ciphertext, null);
    }

    public void parseKey(byte[] keyData) throws GeneralSecurityException, IOException {
        keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withBytes(keyData));
        aead = keysetHandle.getPrimitive(Aead.class);
    }


    public byte[] getKeySetAsJSON() throws GeneralSecurityException, IOException {
        // output stream of some kind
        OutputStream outputStream = new ByteArrayOutputStream();
        CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withOutputStream(outputStream));
        return outputStream.toString().getBytes();
    }

    public void TestAEAD() throws GeneralSecurityException, IOException {
        AEAD client = new AEAD();
        AEAD server = new AEAD();
        server.genKeySet();
        byte[] keyData = server.getKeySetAsJSON();
        client.parseKey(keyData);


        // assert
        String plaintext = "Hello World!";
        byte[] ciphertext = client.encrypt(plaintext.getBytes());
        byte[] decrypted = server.decrypt(ciphertext);
        assert plaintext.equals(new String(decrypted)) : "Assertion Failed";
    }
}
