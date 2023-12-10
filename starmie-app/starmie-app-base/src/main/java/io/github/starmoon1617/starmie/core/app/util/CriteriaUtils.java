/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.app.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.github.starmoon1617.starmie.core.app.enums.ParamType;
import io.github.starmoon1617.starmie.core.constant.InterpunctionConstants;
import io.github.starmoon1617.starmie.core.criterion.BaseCriteria;
import io.github.starmoon1617.starmie.core.criterion.enums.CombinaType;
import io.github.starmoon1617.starmie.core.criterion.enums.LimitationType;
import io.github.starmoon1617.starmie.core.criterion.enums.OperatorType;
import io.github.starmoon1617.starmie.core.criterion.enums.SortType;
import io.github.starmoon1617.starmie.core.util.CommonUtils;
import io.github.starmoon1617.starmie.core.util.DateUtils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Help to create BaseCriteria from HttpRequest.<br>
 * 查询条件工具类,用于从request中获取提交参数,组装出BaseCriteria对象 <br>
 * 条件过滤类型参数格式:_FC{组合}{组合名}_{数据类型}{操作符类型}_{字段名}_{表别名} <br>
 * 排序过滤类型参数格式:_FS{顺序}_{字段名}_{表别名} <br>
 * 数量限制类型参数格式:_FL{类型} <br>
 * 字段名和表别名使用驼峰方式编码,转换为_连接的小写字符,如:userName转换为user_name <br>
 * 组合 :0-无AND/OR,1-AND,2-OR,3-(AND),4-(OR),5-AND(),6-OR(),7-() <br>
 * 数据类型:I-Integer,L-Long,S-String,H-Short,D-Date,T-DateTime,B-BigDecimal <br>
 * 操作符:0-=,1-!=,2->,3->=,4-&lt;,5-&lt;=,6-in,7-not in,8-is null,9-is not
 * null,10-between,11-like '%x%',12-like 'x_',13-like '_x',14-like '_x_',
 * 15-like 'x%', 16-like '%x' <br>
 * 排序:0-ASC,1-DESC <br>
 * 显示类型:0-限制数量, 1-开始位置, 2-结束位置(需要限制数量时,0是必须的)
 * 
 * 
 * @date 2023-10-23
 * @author Nathan Liao
 */
public class CriteriaUtils {

    /**
     * 过滤条件的前缀._FC,
     */
    private static final String FILTER_PREFIX_CRITERION = "_FC";

    /**
     * 排序过滤类型前缀, _FS,
     */
    private static final String FILTER_PREFIX_SORT = "_FS";

    /**
     * 数量限制类型前缀, _FL
     */
    private static final String FILTER_PREFIX_LIMITATION = "_FL";

    /**
     * 所有组合类型的列表(7个), 组合类型只需传递组合对应的index, 前3个非组合(NON_COMBINA放在最前),(COMBINA_AND,
     * COMBINA_OR为组合内),(COMBINA_OUTER_AND, COMBINA_OUTER_OR未组合外),
     */
    private static final CombinaType[] COMBINAS = { CombinaType.NON_COMBINA, CombinaType.NON_COMBINA_AND, CombinaType.NON_COMBINA_OR, CombinaType.COMBINA_AND,
            CombinaType.COMBINA_OR, CombinaType.COMBINA_OUTER_AND, CombinaType.COMBINA_OUTER_OR, CombinaType.COMBINA };

    /**
     * 所有排序类型的列表(2个),排序类型的值只需传入对应的index
     */
    private static final SortType[] SORTCRITERIONS = { SortType.ASC, SortType.DESC };

    /**
     * 所有查询条件的类型列表(17个), 传入操作对应的index
     */
    private static final OperatorType[] OPERATORS = { OperatorType.EQ, OperatorType.NEQ, OperatorType.GT, OperatorType.GTE, OperatorType.LT, OperatorType.LTE,
            OperatorType.IN, OperatorType.NIN, OperatorType.ISN, OperatorType.ISNN, OperatorType.BTW, OperatorType.LK, OperatorType.RLKO, OperatorType.LLKO,
            OperatorType.SLKO, OperatorType.RLKM, OperatorType.LLKM };

    /**
     * 所有可用的数量限制类型, 传入对应的index
     */
    private static final LimitationType[] LIMITATIONS = { LimitationType.LIMIT, LimitationType.OFFSET, LimitationType.END };

    private CriteriaUtils() {

    }

    /**
     * 获取参数并生成BaseCriteria对象
     * 
     * @return
     */
    public static BaseCriteria getCriteria() {
        return getCriteria(ServletRequestAttributes.class.cast(RequestContextHolder.getRequestAttributes()).getRequest());
    }

    /**
     * 从HttpServletRequest中获取参数,并组装成BaseCriteria
     * 
     * @param request
     * @return
     */
    public static final BaseCriteria getCriteria(HttpServletRequest request) {
        BaseCriteria baseCriteria = BaseCriteria.getInstance();
        Map<String, String[]> params = request.getParameterMap();
        if (CommonUtils.isEmpty(params)) {
            // 没有参数,直接返回结果
            return baseCriteria;
        }
        constructCriteria(baseCriteria, params);
        return baseCriteria;
    }

    /**
     * 构建baseCriteria
     * 
     * @param baseCriteria
     * @param params
     */
    private static void constructCriteria(BaseCriteria baseCriteria, Map<String, String[]> params) {
        Set<Entry<String, String[]>> paramEntries = params.entrySet();

        for (Entry<String, String[]> param : paramEntries) {
            String name = param.getKey();
            if (name.startsWith(FILTER_PREFIX_CRITERION)) {
                String[] retVal = parseConditionName(name);
                if (!CommonUtils.isEmpty(retVal)) {
                    Object[] values = parseValues(retVal[1].substring(0, 1), param.getValue());
                    // 组合类型
                    int index = Integer.parseInt(retVal[0].substring(0, 1));
                    String combinaName = retVal[0].length() > 1 ? retVal[0].substring(1) : "";
                    // 非组合类型
                    baseCriteria.addCriterion(COMBINAS[index], combinaName, OPERATORS[Integer.valueOf(retVal[1].substring(1))],
                            ((retVal.length > 3 ? (retVal[3] + InterpunctionConstants.DOT_STR) : "") + retVal[2]), values);
                }
            } else if (name.startsWith(FILTER_PREFIX_SORT)) {
                // 转换为排序
                String[] retVal = parseSortName(name);
                if (!CommonUtils.isEmpty(retVal)) {
                    baseCriteria.addSortCriterion(Integer.parseInt(retVal[0]),
                            ((retVal.length > 2 ? (retVal[2] + InterpunctionConstants.DOT_STR) : "") + retVal[1]),
                            SORTCRITERIONS[Integer.valueOf(param.getValue()[0])]);
                }
            } else if (name.startsWith(FILTER_PREFIX_LIMITATION)) {
                baseCriteria.addLimitation(LIMITATIONS[Integer.parseInt(name.substring(3))], Integer.valueOf(param.getValue()[0]));
            }
        }
    }

    /**
     * 解析查询条件, _FC{数据类型}{操作符类型}_{字段名}_{表别名}解析成数组[{数据类型}{操作符类型},{字段名},{表别名}]
     * 
     * @param name
     * @return
     */
    private static String[] parseConditionName(String name) {
        return CommonUtils.split(name.substring(3), InterpunctionConstants.UNDER_LINE_STR);
    }

    /**
     * 解析排序条件, _FS_{字段名}_{表别名}解析成数组[{字段名},{表别名}]
     * 
     * @param name
     * @return
     */
    private static String[] parseSortName(String name) {
        return CommonUtils.split(name.substring(3), InterpunctionConstants.UNDER_LINE_STR);
    }

    /**
     * 解析出对应类型的值
     * 
     * @param type
     * @param values
     * @return
     */
    private static Object[] parseValues(String type, String[] values) {
        if (CommonUtils.isEmpty(values)) {
            return null;
        }
        Object[] retVal = null;
        if (ParamType.STRING.type().equals(type)) {
            retVal = values;
        } else if (ParamType.INT.type().equals(type)) {
            retVal = new Integer[values.length];
            for (int i = 0; i < values.length; i++) {
                retVal[i] = Integer.valueOf(values[i]);
            }
        } else if (ParamType.LONG.type().equals(type)) {
            retVal = new Long[values.length];
            for (int i = 0; i < values.length; i++) {
                retVal[i] = Long.valueOf(values[i]);
            }
        } else if (ParamType.DATE.type().equals(type)) {
            retVal = new Date[values.length];
            for (int i = 0; i < values.length; i++) {
                retVal[i] = DateUtils.parseDate(values[i]);
            }
        } else if (ParamType.DATETIME.type().equals(type)) {
            retVal = new Date[values.length];
            for (int i = 0; i < values.length; i++) {
                retVal[i] = DateUtils.parseDateTime(values[i]);
            }
        } else if (ParamType.BIGDECIMAL.type().equals(type)) {
            retVal = new BigDecimal[values.length];
            for (int i = 0; i < values.length; i++) {
                retVal[i] = new BigDecimal(values[i]);
            }
        } else if (ParamType.SHORT.type().equals(type)) {
            retVal = new Short[values.length];
            for (int i = 0; i < values.length; i++) {
                retVal[i] = Short.valueOf(values[i]);
            }
        }
        return retVal;
    }

}
