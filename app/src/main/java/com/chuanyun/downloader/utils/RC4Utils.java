package com.chuanyun.downloader.utils;

import java.nio.charset.Charset;

public class RC4Utils {
    // 仿PHP的getBytes方法
    public static byte[] getBytes(String str) {
        try {
            byte[] gbkBytes = str.getBytes("GBK");
            byte[] result = new byte[gbkBytes.length];

            for (int i = 0; i < gbkBytes.length; i++) {
                int temp = gbkBytes[i] & 0xFF; // 转无符号
                if (temp < 128) {
                    result[i] = (byte) temp;
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 1; j < 8; j++) {
                        sb.append(((temp >> j) & 1) ^ 1);
                    }
                    int value = Integer.parseInt(sb.toString(), 2) + 1;
                    result[i] = (byte) (-value);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("getBytes error: " + e.getMessage());
        }
    }

    // 初始化密钥
    public static int[] initKey(String key) {
        byte[] keyBytes = getBytes(key);
        int[] state = new int[256];
        int keyLen = keyBytes.length;

        for (int i = 0; i < 256; i++) {
            state[i] = i;
        }

        int j = 0;
        for (int i = 0; i < 256; i++) {
            j = (j + state[i] + (keyBytes[i % keyLen] & 0xFF)) & 0xFF;
            int temp = state[i];
            state[i] = state[j];
            state[j] = temp;
        }
        return state;
    }

    // RC4核心算法
    public static byte[] rc4Base(byte[] input, String key) {
        int[] state = initKey(key);
        int x = 0, y = 0;
        byte[] result = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            x = (x + 1) & 0xFF;
            y = (y + state[x]) & 0xFF;

            int temp = state[x];
            state[x] = state[y];
            state[y] = temp;

            int xorIndex = (state[x] + state[y]) & 0xFF;
            result[i] = (byte) (input[i] ^ state[xorIndex]);
        }
        return result;
    }

    // RC4加密
    public static String rc4Encrypt(String data, String key) {
        if (data == null || key == null) {
            return "";
        }
        byte[] base = rc4Base(getBytes(data), key);
        StringBuilder hexString = new StringBuilder();

        for (byte b : base) {
            hexString.append(String.format("%02x", b & 0xFF));
        }
        return hexString.toString();
    }

    // 将十六进制字符串转换为字节数组
    public static byte[] hexString2Bytes(String hex) {
        int len = hex.length();
        byte[] result = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int high = Character.digit(hex.charAt(i), 16);
            int low = Character.digit(hex.charAt(i + 1), 16);
            result[i / 2] = (byte) ((high << 4) + low);
        }
        return result;
    }

    // RC4解密
    public static String rc4Decrypt(String data, String key) {
        if (data == null || key == null) {
            return "";
        }
        byte[] decrypted = rc4Base(hexString2Bytes(data), key);
        try {
            return new String(decrypted, Charset.forName("GBK"));
        } catch (Exception e) {
            throw new RuntimeException("Decryption error: " + e.getMessage());
        }
    }

//    // 测试代码
//    public static void main(String[] args) {
//        String data = "测试数据";
//        String key = "密钥";
//
//        String encrypted = rc4Encrypt(data, key);
//        System.out.println("加密结果: " + encrypted);
//
//        String decrypted = rc4Decrypt(encrypted, key);
//        System.out.println("解密结果: " + decrypted);
//    }



    public static String testRC4() {
        String data = "a3322b1e69560a9f7508be78852e09df104e4743f83de107091974918f81cdcee240be483671be9a9e4ca2be5729f088a11730476d495c650c60591dcf6d5e23e232ece5fdf20d897b9cac111e43f6d2b82f7604cee0870ee2883e473717154dc9942dedc43f92836d46ff54b0edd3c87fd6e729b21ad0626e1409ba4fb871a62a3c26b8abe3739af632e412414957c8162fdc59b3a287c82353a123203ba7fe750e3817268bf47b415230d4a3282917f58d50fbfa80106ea9ae8f4965b774c7d416a3bdd7dfd6dbb3d3fd8f72f23e1c49770c42707639c9b92f6d73c9b3781db06d340567f72af38e01328e69dcc02659de4317c8fd7f0bd0cd2eac3b504509575ba10c6dcb0b9df4108c621003fd05b466a657aceb3f59c59365216d203602c40a6e8f97ed10b4e7e9bdebe6ca40ace0a00a48c2b6deae00c2c47e388f7b94324816ce25cb9c575fa0b4325ea99e64346f35b7f0ebe30c6dfea9ef6412f2fca91fe0bc5153d54ce782699752f082c9cf4d6c77ccaa9576469b07a89107b9b1a288515a2736d795d9a1ce5e8b9ddb11c60d1d34619f963a1ab0d85ff2025ee1176b7d098b6ed721c420ebe828dfdb39b874eae3c0e076e9fdb07e16e0ac3ce3981034b08129012b6382f98fa7a8e5a88c55147887f220b47a8e201970b86f9ce427daab18dbb78224026fffc061b83459b7e9ee181a0445a1d5cb794abd92c6bd3b8563821868b937e155e2c83d67c1f4f3215f8c6b958fae5ab39440724e700a1d7b612a8577aed9cb8ac5335bc43a6d6f32006b5ed0b0c931533a76af5e012924c30e4dc3da355a6722ca2b54eb39a6f1b11af45dd42c3be4f09c3c0e431b1d585fd05ec0c2d38e09e6f5798ca5cfcf7f66ff73622b739330805ffc2e512ca3169c382f2cdce330e2bbed59e7e3a87a20f918a9cf4a79b0137d1f0190097a26ff53c6e7bc4b3dd87a14785589e1f7c790440db09a361ebb5ce8c37f6aae6e13d8186841f75c2659ba58d4cbbd14b5450dd5469476457846298cb92d323cacaef679cb6f1cb7cc3db047301448a5c345a991b9656fa129249c3ffd0cd16c2029734abb00709eeb67e29dfaef2a9ce5d0c105be37526619d3a4d6e7ed6bbfe231be0140cd476839aeacd8ff284f2f822dc46d3a129604cec547549aa272f6f8807ced7143e15936a20727c01abf4c2203af0c14f869a435872878a916908b944d42116d7c19deba260695843f38aec4e234150b4fe79e4db995f71be059902271fff9bfb80947e922887b4f7738226aa7cb68fa1e42b096206d4992e835e2702c14b0154f6a504aacbeb5cba6ed9d98b471446d51c6203d0cef5a66e1ea656acee2ee18c07cfecedc5c25288efbb7d999d431d0288db62161420c4708f8cee2b4ac8cc60c727a3032b9b69c6489680d14a8332f8693c0beaa03a25a98fb41b46b45f9b2e9e2cd5597c0a2983e88eaf3a00cf565b09d39e5dc5cfafea9aa2e1a5f6b55cf8f83d7c16b26704c7cee87e74a3af78697af2ae0600b31a6bb1a56abde866c0e24e66f3a44ee21ed1c4340da4539e82ebbf966917d823bf69054d636ac66f95ff48a654a2d004a6704a4815761019e298efb3d88ca37e7b51fd6d2dd4533b80a4ed965706632f10376f366c03a3e60011bf518559df73208bb450bc4e660239c75db53517f26892e5535a1826e89850c3f3272e71d5dbde3e6ac92e43b04d91ef890e0c17a8a52ded64880abb341506465e99e6eb02e361640c9f0c688cb4fff29ea1237533e9087be7ec2da486486a388ed039a47449274cd5d6d2f96de4f17b7d314b188956ddaa5339ad58b0c3a2585f3ec9a534b3df6f303f8d88fd7ce8c9bfcc655ad5ea348efdb16d6afd2c10a5e808d3283c3a1d1ca881f79e1338806012e591e9e13f77038b409c26f6604b1b9bb2f33cf2c6e62e80bf894c6dd5e3c5c9797856a541fc5f8eb7d752933812fc79dcb7c5409ad0545c684ffba7e8ad4ce06c67bbc4395ffcc9056f4d8f5bc548d61b9518eac65c8c4b3577739b7e0e52644640a584f04b4f7498c622af14b22fc966e6579cf6ced6c648691d2db97b3c9fa1e7a7ffaa46830ac20af4ce4704ceee72745ffda5250f25d435d86fd78955674ec41680d382077707ff9f67da9678bd875563cb81c988e1c852f93cf3d460d3f6451c9a84bdedcaf34ed3b098584b4b11cd242336258b548cdcf2dc6e4997428fda356a9fbafa981e2e28b8907bf53033dd356be45a8207e864b2112b6e4743d1d30d637d21ed06c907492c01e15b486081dd78491bc586a297de6cd144bdbaf3ac36e21618f278858ac07328a72ce40f0dbc04830261fe5e88ddd6f7904395a193f894776c989f83b533aa307a52bd290a147b2fb38a4433872fbb7627d16bc686350adac916ecbb6b3ffa2941475900051a8ce9e39dbf7002b44f9dff284c7a06d66f38290f5604c50773a9889380a61444a78f13a4a43187ed0d7edc1e8451c17362c51fed3b319345205b43be56638c5252ed894c633035cd7c42afde64f6963634275319305e6d0483ddf9de18dc89085d9403ed490eb469d5e0b58653719fb791f2b45821865c1f360c996e8a827d95f4264e6a6a3281ddc62d1499ef40b099bc8bb8b5c1944193ce7deb25a438d76dce0b5c17fee6b904fbebc81c2a08826798c33cbf9fa0ef53266dd98f7b20a4a469425ab8a24543df2dfc10d4d73157dca80c0d02ca2ea0655af3af00c6479e58bd8780dce669a04d1faeb5ebb04b4d14e9ac19bd57bf61562953ae9338ba14bf8d94a95f1472fe33f9c69f3d2858a689c7dfc408e09c09cd385f8669d817c700f092ad1f61b561bfa1792d8f7dc73926fe2921a9160533117ce0ded1b34c87f60464397facb5bbe5b82603e19cdcd4efd7ed8ca02476ad525189b99462619ab3bf2523fe0a9ffeef76de634db624e099fe41d4d6c3c2223c73c6cac91dc31e6602eb3743e486cf02176ff58";
        return rc4Decrypt(data,"0810");
    }
}
