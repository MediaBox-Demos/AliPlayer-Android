package com.aliyun.player.alivcplayerexpand.util;

/**
 * 正则表达式
 */
/****
 * Regular expressions
 */
public class RegularPatternUtil {

    /**
     * 判断输入的字符串是否是数字
     * @param inputText    要判断的字符串
     * @return  true:是数字,false:不是数字
     */
    /****
     * Determine whether the input string is a number
     * @param inputText    The string to be judged
     * @return  true: is a number, false: is not a number
     */
    public static boolean isNumber(String inputText){
        return inputText.matches("^(\\-|\\+)?\\d+(\\.\\d+)?$");
    }
}
