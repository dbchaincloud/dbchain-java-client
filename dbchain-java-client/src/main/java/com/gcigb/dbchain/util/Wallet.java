package com.gcigb.dbchain.util;

import static com.gcigb.dbchain.DBChain.dbChainEncrypt;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcigb.dbchain.DbChainKey;
import com.gcigb.dbchain.util.coding.HexUtil;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.wallet.DeterministicSeed;
import org.jetbrains.annotations.NotNull;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.ObjectMapperFactory;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;


/**
 * 以太坊钱包创建工具类
 */

public class Wallet {

    private static ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();

    /**
     * 通用的以太坊基于bip44协议的助记词路径 （imtoken jaxx Metamask myetherwallet）
     */
    public static String GCIGB_BOX_TYPE = "m/44'/118'/0'/0/0";

    /**
     * 创建助记词，并通过助记词创建钱包
     */
    public static DbChainKey generateMnemonic(String walletName, String pwd) {
        String[] pathArray = GCIGB_BOX_TYPE.split("/");
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(walletName, ds, pathArray, pwd);
    }

    /**
     * 通过导入助记词，导入钱包
     *
     * @param list 助记词
     * @param pwd  密码
     */
    public static DbChainKey importMnemonic(List<String> list, String pwd) {
        String path = GCIGB_BOX_TYPE;
        if (!path.startsWith("m") && !path.startsWith("M")) {
            //参数非法
            return null;
        }
        String[] pathArray = path.split("/");
        if (pathArray.length <= 1) {
            //内容不对
            return null;
        }
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(list, null, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(generateNewWalletName(), ds, pathArray, pwd);
    }

    @NonNull
    private static String generateNewWalletName() {
        char letter1 = (char) (int) (Math.random() * 26 + 97);
        char letter2 = (char) (int) (Math.random() * 26 + 97);
        return letter1 + String.valueOf(letter2) + "-新钱包";
    }

    /**
     * @param walletName 钱包名称
     * @param ds         助记词加密种子
     * @param pathArray  助记词标准
     * @param pwd        密码
     */
    @NotNull
    private static DbChainKey generateWalletByMnemonic(String walletName, DeterministicSeed ds,
                                                      String[] pathArray, String pwd) {
        //种子
        byte[] seedBytes = ds.getSeedBytes();
        //助记词
        List<String> mnemonic = ds.getMnemonicCode();
        //生成masterKey
        DeterministicKey dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);
        //取出特定的key
        for (int i = 1; i < pathArray.length; i++) {
            ChildNumber childNumber;
            if (pathArray[i].endsWith("'")) {
                int number = Integer.parseInt(pathArray[i].substring(0,
                        pathArray[i].length() - 1));
                childNumber = new ChildNumber(number, true);
            } else {
                int number = Integer.parseInt(pathArray[i]);
                childNumber = new ChildNumber(number, false);
            }
            dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber);
        }
        //获取密钥对
        ECKeyPair keyPair = ECKeyPair.create(dkKey.getPrivKeyBytes());
        //生成钱包
        generateWallet(walletName, pwd, keyPair);

        String mnemonicValue = convertMnemonicList(mnemonic);
        String privateKey32Hex = dkKey.getPrivateKeyAsHex();
        byte[] publicKey33Bytes = dbChainEncrypt.generatePublicKey33ByPrivateKey(dkKey.getPrivKeyBytes(), dkKey);
        byte[] publicKey64Bytes = dbChainEncrypt.generatePublicKey64ByPrivateKey(dkKey.getPrivKeyBytes(), dkKey);
        String address = dbChainEncrypt.generateAddressByPublicKeyByteArray33(publicKey33Bytes);
        //创建我们需要的信息
        return new DbChainKey(
                mnemonicValue,
                privateKey32Hex,
                HexUtil.encodeHexString(publicKey33Bytes),
                HexUtil.encodeHexString(publicKey64Bytes),
                address);
    }

    private static String convertMnemonicList(List<String> mnemonics) {
        StringBuilder sb = new StringBuilder();
        int size = mnemonics.size();

        for (int i = 0; i < size; i++) {
            sb.append(mnemonics.get(i));
            if (i != size - 1) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    private static void generateWallet(String walletName, String pwd, ECKeyPair ecKeyPair) {
        WalletFile keyStoreFile;
        try {
            keyStoreFile = org.web3j.crypto.Wallet.create(pwd, ecKeyPair, 1024, 1); // WalletUtils. .generateNewWalletFile();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // TODO
        String wallet_dir = "";
        File destination = new File(wallet_dir, "keystore_" + walletName + ".json");
        //目录不存在则创建目录，创建不了则报错
        if (!createParentDir(destination)) {
            return;
        }
        try {
            objectMapper.writeValue(destination, keyStoreFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean createParentDir(File file) {
        //判断目标文件所在的目录是否存在
        if (file.getParentFile() == null) return true;
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            return file.getParentFile().mkdirs();
        }
        return true;
    }
}
