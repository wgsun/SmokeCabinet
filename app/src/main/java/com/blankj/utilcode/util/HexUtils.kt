package com.blankj.utilcode.util

object HexUtils{

    /**
     * 十六进制String转换成Byte[]
     * @param hexString the hex string
     * *
     * @return byte[]
     */
    fun hexStringToBytes(hexStringTemp: String?): ByteArray? {
        if (hexStringTemp == null || hexStringTemp == "") {
            return null
        }
        var hexString = hexStringTemp
        hexString = hexString.toUpperCase()
        val length = hexString.length / 2
        val hexChars = hexString.toCharArray()
        val d = ByteArray(length)
        for (i in 0 until length) {
            val pos = i * 2
            d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
        }
        return d
    }

    /**
     * Convert char to byte
     * @param c char
     * *
     * @return byte
     */
    private fun charToByte(c: Char): Byte {

        return "0123456789ABCDEF".indexOf(c).toByte()
    }

    /* 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
        * @param src byte[] data
        * @return hex string
        */
    fun bytesToHexString(src: ByteArray?): String? {
        val stringBuilder = StringBuilder("")
        if (src == null || src.size <= 0) {
            return null
        }
        for (i in 0..src.size-1) {
            val v = src[i].toInt() and 0xFF
            val hv = Integer.toHexString(v)
            if (hv.length < 2) {
                stringBuilder.append(0)
            }
            stringBuilder.append(hv)
        }
        return stringBuilder.toString()
    }


    fun convertStringToHex(str: String): String {

        val chars = str.toCharArray()

        val hex = StringBuffer()
        for (i in chars.indices) {
            hex.append(Integer.toHexString(chars[i].toInt()))
        }

        return hex.toString()
    }

    fun convertHexToString(hex: String): String {

        val sb = StringBuilder()
        val temp = StringBuilder()

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        var i = 0
        while (i < hex.length - 1) {

            //grab the hex in pairs
            val output = hex.substring(i, i + 2)
            //convert hex to decimal
            val decimal = Integer.parseInt(output, 16)
            //convert the decimal to character
            sb.append(decimal.toChar())

            temp.append(decimal)
            i += 2
        }

        return sb.toString()
    }
}