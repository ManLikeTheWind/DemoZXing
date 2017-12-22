package com.dxiang.demozxing.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.util.Log;

/**
 * 作者：dongixang
 * 时间：2017/12/21 16:07
 * 功能：
 * 使用：
 */

public class StringUtils {


    /**
     * 字符串的单个字符是否是汉字
     *
     * @param c
     *            单个字符
     * @return 字符对应的ASCIIS 值， 负值 是汉字；
     */
    public static int ascii(String c) {
        byte x[] = new byte[2];// 这里是两个元素
        x = c.getBytes();// 按照原有的 编码格式生成字节数组；

        // x=c.getBytes("utf-8");// 按照什么编码格式生成 字节数组；
        // x=c.getBytes(srcBegin, srcEnd, dst, dstBegin);

        if (x == null || x.length > 2 || x.length <= 0) {// 没有字符，为空字符串（空格也是字符串）
            return -1;
        }
        if (x.length == 1) {// 英文字符
            return 1;
        }
        Pattern p=Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m=p.matcher(c);
        if(m.matches()){
//	      Toast.makeText(Main.this,"输入的是汉字", Toast.LENGTH_SHORT).show();
            return -1;
        }

        return 0;
    }

    /**
     * 去掉首位空格，做其它处理
     *
     * @param string
     * @return
     */
    public static String goodStr(String string) {

        string = string.trim();
        return string;
    }
    /**
     * 判断 有多少个 汉字  长度 取整数
     * @param string
     * @return
     */
    public static int letterSum(String string) {
        if (null != string) {
            string = goodStr(string);// 这个函数是干什么用处的？去掉 首位空格
            if (string.length() <= 0) {
                return 0;
            } else {
                String str;
                double len = 0;
                for (int i = 0; i < string.length(); i++) {
                    // 是否是汉字 ascii<0;
                    str = string.substring(i, i + 1);
                    if (ascii(str) < 0) {
                        len++;
                    } else {
                        len += 0.5;
                    }

                }
                Log.e("num", (int) Math.round(len)+";  len="+ len);
                return (int) Math.round(len);
            }
        }
        return 0;

    }

    /**
     * 判断有几个汉字，不是长度； 可以修改成： 英文字符有几个
     * @param string
     * @return
     */
    public static int chineseSum(String string) {
        if (!TextUtils.isEmpty(string)) {//字符串内容不不为空
            string = goodStr(string);// 这个函数是干什么用处的？去掉 首位空格
            if (string.length() <= 0) {
                return 0;
            } else {
                String str;
                double len = 0;
                for (int i = 0; i < string.length(); i++) {
                    // 是否是汉字 ascii<0;
                    str = string.substring(i, i + 1);
                    if (ascii(str) < 0) {//是汉字
                        len++;
                    }
//					else {//不是汉字
//						len += 0.5;
//					}

                }
                Log.e("num", (int) Math.round(len)+";  len="+ len);
                return (int) Math.round(len);
            }
        }
        return 0;
    }
    /**
     * 获取 多少的 字符串
     *
     * @param string
     *            字符串数据，
     * @param size
     *            要获取的长度 ( 是长度 不是字符个数，是长度)：中文为一个，英文为0.5个
     *            注意：假设有十个长度：全为中文，则为10个汉字；20个字母， 若某字符串字符数小于 10，则该字符串没有达到省略要求；
     * @return
     */
    public static String limitStr(String string, int size) {// 要多长的 字符串
        if (null != string) {
            string = goodStr(string);// 这个函数是干什么用处的？去掉 首位空格
            if (string.length() <= size) {
                return string;
            } else {
                StringBuffer buffer = new StringBuffer();
                String str;
                double len = 0;
                for (int i = 0; i < string.length(); i++) {
                    // 是否是汉字 ascii<0;
                    str = string.substring(i, i + 1);
                    if (ascii(str) < 0) {
                        buffer.append(str);
                        len++;
                    } else {
                        buffer.append(str);
                        len += 0.5;
                    }
                    if (len >= size)
                        break;
                }
                return buffer.toString();
            }
        }
        return "";

    }

    /**
     * 获取 以特定 字符串endStr 为结尾的字符串，
     *
     * @param strData 字符串数据
     * @param size
     * @param endStr
     * @return  返回以特定省略符号为结尾的字符串
     */
    public static String limitStr_Ending(String strData, int size, String endStr) {
        strData = goodStr(strData);//去掉首位空格
        if (size < endStr.length() || strData.length() < endStr.length()) {// 结尾的字符串过长，子以
            // 结尾的字符串为主
            Log.e("endStr is too long","endStr is too long! Please cut it.");
        }
        String  cutStr;
        cutStr = limitStr(strData, size);
        if (cutStr.length()!=strData.length()) {//如果字符串被裁减了 则执行下面操作
            cutStr = cutStr.substring(0, cutStr.length() - letterSum(endStr))+ endStr;
        }

        return cutStr;
    }

    public static <T extends CharSequence>boolean isNullorEmpty(T str){
        return str==null?true:(TextUtils.isEmpty((str+"").trim()));
    }

}
