package com.gcigb.dbchain;

import com.gcigb.dbchain.util.coding.HexUtil;

/**
 * @author: Xiao Bo
 * @date: 20/10/2020
 */
public class DbChainKey {
    private String mnemonic;
    private String privateKey32;
    private String publicKey33;
    private String publicKey64;
    private String address;
    private byte[] privateKeyBytes;
    private byte[] publicKeyBytes33;

    // 私钥拼接方式：privateKeyDer = privateKeyLeft + privateKey32 + privateKeyMid + publicKey64
    // 公钥拼接方式：publicKeyDer = publicKeyLeft + publicKey64
    private static final String PRIVATEKEY_LEFT = "30818d020100301006072a8648ce3d020106052b8104000a047630740201010420";
    private static final String PRIVATEKEY_MID = "a00706052b8104000aa14403420004";
    private static final String PUBLICKEY_LEFT = "3056301006072a8648ce3d020106052b8104000a03420004";

    public DbChainKey() {
    }

    public DbChainKey(String mnemonic, String privateKey32, String publicKey33, String publicKey64, String address) {
        this.mnemonic = mnemonic;
        this.privateKey32 = privateKey32;
        this.publicKey33 = publicKey33;
        this.publicKey64 = publicKey64;
        this.address = address;
        privateKeyBytes = HexUtil.decode(this.privateKey32);
        publicKeyBytes33 = HexUtil.decode(this.publicKey33);
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getPrivateKey32() {
        return privateKey32;
    }

    public String getPublicKey33() {
        return publicKey33;
    }

    public String getPublicKey64() {
        return publicKey64;
    }

    public String getAddress() {
        return address;
    }

    public byte[] getPrivateKeyBytes() {
        return privateKeyBytes;
    }

    public byte[] getPublicKeyBytes33() {
        return publicKeyBytes33;
    }

    @Override
    public String toString() {
        return "DbChainKey{" +
                "mnemonic='" + mnemonic + '\'' +
                ", privateKey32='" + privateKey32 + '\'' +
                ", publicKey33='" + publicKey33 + '\'' +
                ", publicKey64='" + publicKey64 + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
