package com.moon.office.excel.annotations;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * @author benshaoye
 */
public @interface Style {

    String className();

    int width() default -1;

    short height() default -1;

    HorizontalAlignment align() default HorizontalAlignment.LEFT;

    VerticalAlignment verticalAlign() default VerticalAlignment.TOP;

    /**
     * top、right、bottom、left
     *
     * @return
     */
    BorderStyle[] borderStyle() default {};

    /**
     * top、right、bottom、left
     *
     * @return
     */
    short[] borderColor() default {};

    /**
     * 背景颜色
     *
     * @return
     */
    short backgroundColor() default Short.MAX_VALUE;

    FillPatternType patternType() default FillPatternType.NO_FILL;

    /**
     * 字体颜色
     *
     * @return
     * /
    short color() default 0;

    boolean italic() default false;*/
}