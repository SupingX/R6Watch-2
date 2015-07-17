package com.mycj.healthy.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/** 
 * 常用单位转换的辅助类 
 *  
 *  
 *  
 */  
public class DensityUtils  
{  
    private DensityUtils()  
    {  
        /* cannot be instantiated */  
        throw new UnsupportedOperationException("cannot be instantiated");  
    }  
  
    /** 
     * dp转px 
     *  
     * @param context 
     * @param val 
     * @return 
     */  
    public static int dp2px(Resources rs, float dpVal)  
    {  
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  
                dpVal, rs.getDisplayMetrics());  
    }  
  
    /** 
     * sp转px 
     *  
     * @param context 
     * @param val 
     * @return 
     */  
    public static int sp2px(Resources rs, float spVal)  
    {  
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,  
                spVal, rs.getDisplayMetrics());  
    }  
  
    /** 
     * px转dp 
     *  
     * @param context 
     * @param pxVal 
     * @return 
     */  
    public static float px2dp(Resources rs, float pxVal)  
    {  
        final float scale = rs.getDisplayMetrics().density;  
        return (pxVal / scale);  
    }  
  
    /** 
     * px转sp 
     *  
     * @param fontScale 
     * @param pxVal 
     * @return 
     */  
    public static float px2sp(Resources rs, float pxVal)  
    {  
        return (pxVal / rs.getDisplayMetrics().scaledDensity);  
    }  
  
}  