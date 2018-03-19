/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.utils;

/**
 *
 * @author prem
 */
public class QuickMafs {

    public static double invSqRoot(double x) {
//        double xhalf = 0.5d * x;
////        long i = Double.doubleToLongBits(x);
////        i = 0x5fe6ec85e7de30daL - (i >> 1);
////        x = Double.longBitsToDouble(i);
//        x = Double.longBitsToDouble(0x5fe6ec85e7de30daL - (Double.doubleToLongBits(x) >> 1));
//        x *= (1.5d - xhalf * x * x);
//        return x;
        return 1 / Math.sqrt(x);
    }

    public static double square(double d) {
        return d * d;
    }
}
