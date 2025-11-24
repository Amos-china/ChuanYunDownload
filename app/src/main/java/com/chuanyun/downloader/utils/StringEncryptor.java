package com.chuanyun.downloader.utils;

import java.math.BigInteger;

public class StringEncryptor {
    private static final int LENGTH = 10;
    // 用于加解密的密钥（Base62 编码字符串），用户可自行更换但需保持同样长度
    private static final String KEY_BASE62 = "Gc4d1A9Nb2";
    private static final BigInteger KEY = Base62.decode(KEY_BASE62);

    /**
     * 加密数字字符串
     * @param numericString 非空、仅包含数字的字符串，且数值范围 [0, 62^10)
     * @return 长度为 10 的 Base62 密文
     * @throws IllegalArgumentException 参数非法或超出可表示范围
     */
    public static String encrypt(String numericString) {
        if (numericString == null || !numericString.matches("\\d+")) {
            throw new IllegalArgumentException("输入必须为非空数字字符串");
        }
        BigInteger input = new BigInteger(numericString);
        BigInteger max = BigInteger.valueOf(62).pow(LENGTH);
        if (input.compareTo(BigInteger.ZERO) < 0 || input.compareTo(max) >= 0) {
            throw new IllegalArgumentException(
                    "数值超出可表示范围，需满足 0 <= value < " + max + "（即最多 17 位数字）"
            );
        }
        // 异或变换
        BigInteger cipher = input.xor(KEY);
        // Base62 编码并左侧补齐
        String base62 = Base62.encode(cipher);
        return padLeft(base62, LENGTH, '0');
    }

    /**
     * 解密获得原始数字字符串
     * @param cipherText 长度为 10 的 Base62 密文，包含 [0-9A-Za-z]
     * @return 恢复后的原始数字字符串
     * @throws IllegalArgumentException 参数格式不合法
     */
    public static String decrypt(String cipherText) {
        if (cipherText == null || !cipherText.matches("[0-9A-Za-z]{" + LENGTH + "}")) {
            throw new IllegalArgumentException("密文必须为 " + LENGTH + " 位，仅包含数字和字母");
        }
        BigInteger cipher = Base62.decode(cipherText);
        BigInteger original = cipher.xor(KEY);
        return original.toString();
    }

    // 左侧补齐函数
    private static String padLeft(String s, int length, char padChar) {
        if (s.length() >= length) return s;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length - s.length(); i++) {
            sb.append(padChar);
        }
        sb.append(s);
        return sb.toString();
    }

    /**
     * 简易 Base62 编解码工具
     */
    static class Base62 {
        private static final char[] CHARSET =
                "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        private static final int BASE = CHARSET.length;

        // 将非负整数编码为 Base62 字符串
        public static String encode(BigInteger number) {
            if (number.compareTo(BigInteger.ZERO) < 0) {
                throw new IllegalArgumentException("无法对负数进行 Base62 编码");
            }
            if (number.equals(BigInteger.ZERO)) {
                return "0";
            }
            StringBuilder sb = new StringBuilder();
            BigInteger base = BigInteger.valueOf(BASE);
            while (number.compareTo(BigInteger.ZERO) > 0) {
                BigInteger[] divmod = number.divideAndRemainder(base);
                number = divmod[0];
                int digit = divmod[1].intValue();
                sb.append(CHARSET[digit]);
            }
            return sb.reverse().toString();
        }

        // 将 Base62 字符串解码为整数
        public static BigInteger decode(String str) {
            BigInteger result = BigInteger.ZERO;
            BigInteger base = BigInteger.valueOf(BASE);
            for (char c : str.toCharArray()) {
                int val;
                if ('0' <= c && c <= '9')      val = c - '0';
                else if ('A' <= c && c <= 'Z') val = c - 'A' + 10;
                else if ('a' <= c && c <= 'z') val = c - 'a' + 36;
                else throw new IllegalArgumentException("无效的 Base62 字符: " + c);
                result = result.multiply(base).add(BigInteger.valueOf(val));
            }
            return result;
        }
    }

}
//