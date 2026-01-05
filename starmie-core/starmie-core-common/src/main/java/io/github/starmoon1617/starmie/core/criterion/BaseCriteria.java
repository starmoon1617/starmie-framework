/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.criterion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.starmoon1617.starmie.core.constant.CriterionConstants;
import io.github.starmoon1617.starmie.core.constant.InterpunctionConstants;
import io.github.starmoon1617.starmie.core.criterion.enums.CombinaType;
import io.github.starmoon1617.starmie.core.criterion.enums.LimitationType;
import io.github.starmoon1617.starmie.core.criterion.enums.OperatorType;
import io.github.starmoon1617.starmie.core.criterion.enums.SortType;
import io.github.starmoon1617.starmie.core.util.CommonUtils;

/**
 * Class to create query Criteria
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public final class BaseCriteria {
    /**
     * 查询条件是否有更新
     */
    private boolean condUpdated;

    /**
     * 排序条件是否有更新
     */
    private boolean sortUpdated;

    /**
     * 排序条件
     */
    private List<SortCriterion> sortCriteria;

    /**
     * 排序条件, 修改时使用
     */
    private List<SortCriterion> tempSortCriteria;

    /**
     * 组合条件map
     */
    private Map<String, CondCriteria> combinaCriteria;

    /**
     * 非组合的条件列表
     */
    private CondCriteria nonCombinaCriteria;

    /**
     * 所有的查询条件
     */
    private List<CondCriteria> criteria;

    /**
     * 限制数量, 单页数量
     */
    private Integer limit;

    /**
     * 开始位置
     */
    private Integer offset;

    /**
     * 结束位置
     */
    private Integer end;

    /**
     * return a new BaseCriteria instance
     * 
     * @return
     */
    public static BaseCriteria getInstance() {
        return new BaseCriteria();
    }

    private BaseCriteria() {
        combinaCriteria = new LinkedHashMap<>(3);
        offset = CriterionConstants.OFFSET;
    }

    /**
     * return Conditions list
     * 
     * @return
     */
    public List<CondCriteria> getCriteria() {
        if (!condUpdated) {
            return criteria;
        }
        List<CondCriteria> retVal = null;
        if (nonCombinaCriteria != null || !CommonUtils.isEmpty(combinaCriteria)) {
            retVal = new ArrayList<>();
            if (nonCombinaCriteria != null) {
                retVal.add(nonCombinaCriteria);
            }
            if (!CommonUtils.isEmpty(combinaCriteria)) {
                retVal.addAll(combinaCriteria.values());
            }
        }
        criteria = retVal;
        condUpdated = false;
        return criteria;
    }

    /**
     * @return the sortCriteria
     */
    public List<SortCriterion> getSortCriteria() {
        if (sortUpdated && !CommonUtils.isEmpty(tempSortCriteria)) {
            List<SortCriterion> temp = new ArrayList<>(tempSortCriteria.size());
            temp.addAll(tempSortCriteria);
            if (tempSortCriteria.size() > 1) {
                temp.sort(new SortCriterionComparator());
            }
            sortCriteria = temp;
            sortUpdated = false;
        }
        return sortCriteria;
    }

    /**
     * 根据类型和组合名获取条件对象,如果传入的组合名为空,则返回非组合的对象
     * 
     * @param combinaType
     * @param combinaName
     * @return
     */
    private CondCriteria createCriteria(CombinaType combinaType, String combinaName) {
        CondCriteria crta = null;
        // 非组合的条件,直接使用nonCombinaCriteria
        if (combinaType.combina() && CommonUtils.isNotBlank(combinaName)) {
            crta = combinaCriteria.computeIfAbsent(combinaName, v -> new CondCriteria());
            crta.setCombina(true);
            if (combinaType == CombinaType.COMBINA_OUTER_AND || combinaType == CombinaType.COMBINA_OUTER_OR) {
                // 当为外部组合类型时, 设置外部的连接字符
                crta.setJoinType(combinaType.joinType());
            }
        } else {
            if (nonCombinaCriteria == null) {
                nonCombinaCriteria = new CondCriteria();
                nonCombinaCriteria.setCombina(false);
            }
            crta = nonCombinaCriteria;
            crta.setJoinType(CombinaType.NON_COMBINA.joinType());
        }
        return crta;
    }

    /**
     * process new value for "like" Operator
     * 
     * @param type
     * @param value
     * @return
     */
    private String processLikeValue(OperatorType type, String value) {
        StringBuilder sb = new StringBuilder(value);
        switch (type) {
        case LK -> {
            sb.insert(0, InterpunctionConstants.PERCENT);
            sb.append(InterpunctionConstants.PERCENT);
        }
        case RLKM -> sb.append(InterpunctionConstants.PERCENT);
        case LLKM -> sb.insert(0, InterpunctionConstants.PERCENT);
        case SLKO -> {
            sb.insert(0, InterpunctionConstants.UNDER_LINE);
            sb.append(InterpunctionConstants.UNDER_LINE);
        }
        case RLKO -> sb.append(InterpunctionConstants.UNDER_LINE);
        case LLKO -> sb.insert(0, InterpunctionConstants.UNDER_LINE);
        default -> {
        }
        }
        return sb.toString();
    }
    
    /**
     * 添加LK条件, 默认添加一个NON_COMBINA_AND的条件
     * 
     * @param field
     *            - 字段
     * @param value
     *            - 值
     *
     */
    public BaseCriteria addLike(String field, Object value) {
        if (value == null || !CommonUtils.isNotBlank(field)) {
            return this;
        }
        return addCriterion(CombinaType.NON_COMBINA_AND, null, OperatorType.LK, field, value);
    }
    
    /**
     * 添加EQ/IN条件, 默认添加一个NON_COMBINA_AND的条件
     * 
     * @param field
     *            - 字段
     * @param values
     *            - 值 1个时为EQ, 多个时为 IN
     *
     */
    public BaseCriteria addEqual(String field, Object... values) {
        if (CommonUtils.isEmpty(values)) {
            return this;
        }
        return addCriterion(CombinaType.NON_COMBINA_AND, null, (values.length == 1 ? OperatorType.EQ : OperatorType.IN), field, values);
    }
    
    /**
     * 添加查询条件, 默认添加一个NON_COMBINA_AND的条件
     * 
     * @param operatorType
     *            - 操作类型
     * @param field
     *            - 字段, 可通过{别名}.{字段名}方式传入别名
     * @param values
     *            - 值, between操作类型的必须为2个值, (not)in类型的为至少一个值, is(not)null类型的不需要传值
     *
     */
    public BaseCriteria addCriterion(OperatorType operatorType, String field, Object... values) {
        return addCriterion(CombinaType.NON_COMBINA_AND, null, operatorType, field, values);
    }

    /**
     * 添加查询条件, 对于没有组合的所有条件,必须添加一个NON_COMBINA类型的条件
     * 对于组合条件,各个组合条件必须添加一个COMBINA_OUTER_AND/COMBINA_OUTER_OR的条件
     * 在生成查询条件时,这3种类型的条件将会放置在查询条件的最前端(组合为()的最前端)
     * 
     * @param combinaType
     *            - 一般传入非combina的类型,如传入combina的类型,则创建一个新的Criteria
     * @param combinaName
     *            - 组合名,组合条件时必须传入
     * @param operatorType
     *            - 操作类型
     * @param field
     *            - 字段, 可通过{别名}.{字段名}方式传入别名
     * @param values
     *            - 值, between操作类型的必须为2个值, (not)in类型的为至少一个值, is(not)null类型的不需要传值
     *
     * @return 
     */
    public BaseCriteria addCriterion(CombinaType combinaType, String combinaName, OperatorType operatorType, String field, Object... values) {
        // 获取或创建对象
        CondCriteria crta = createCriteria(combinaType, combinaName);
        // 创建条件
        CondCriterion criterion = new CondCriterion();
        criterion.setOperator(operatorType.opreator());
        criterion.setJoinType(combinaType.joinType());
        criterion.setTerm(toUnderScore(field));
        if (!CommonUtils.isEmpty(values)) {
            List<Object> list = new ArrayList<>(values.length);
            for (int i = 0; i < values.length; i++) {
                // like类型设置
                if (CriterionConstants.OPER_LIKE.equals(operatorType.opreator())) {
                    list.add(processLikeValue(operatorType, values[i].toString()));
                } else {
                    list.add(values[i]);
                }
            }
            criterion.setValues(list);
        }
        // 设置type
        criterion.setType(switch (operatorType) {
        case IN, NIN -> CriterionConstants.TYPE_MULTI;
        case BTW -> CriterionConstants.TYPE_DUAL;
        case ISN, ISNN -> CriterionConstants.TYPE_NONE;
        default -> CriterionConstants.TYPE_SINGLE;
        });
        // 填充条件
        if (CommonUtils.isEmpty(crta.getValues())) {
            crta.setValues(new ArrayList<Object>());
        }
        if (combinaType == CombinaType.COMBINA_OUTER_AND || combinaType == CombinaType.COMBINA_OUTER_OR || combinaType == CombinaType.NON_COMBINA) {
            crta.getValues().add(0, criterion);
            criterion.setJoinType(CombinaType.NON_COMBINA.joinType());
        } else {
            crta.getValues().add(criterion);
        }
        condUpdated = true;
        
        return this;
    }
    
    /**
     * 添加一个排序
     * 
     * @param field
     *            - 字段, 可通过{别名}.{字段名}方式传入别名
     * @param type
     *            - 排序类型
     */
    public BaseCriteria addSortCriterion(String field, SortType type) {
        return addSortCriterion(0, field, type);
    }

    /**
     * 添加一个排序
     * 
     * @param order
     *            - 排序的顺序,小的值排在前面
     * @param field
     *            - 字段 , 可通过{别名}.{字段名}方式传入别名
     * @param type
     *            - 排序类型
     * @return
     */
    public BaseCriteria addSortCriterion(int order, String field, SortType type) {
        if (tempSortCriteria == null) {
            tempSortCriteria = new ArrayList<>(3);
        }
        SortCriterion sortCriterion = new SortCriterion();
        sortCriterion.setTerm(toUnderScore(field));
        sortCriterion.setOrder(order);
        if (type == SortType.DESC) {
            sortCriterion.setType(type.value());
        }
        tempSortCriteria.add(sortCriterion);
        sortUpdated = true;
        
        return this;
    }

    /**
     * 添加数量限制
     * 
     * @param type
     * @param value
     * @return
     */
    public BaseCriteria addLimitation(LimitationType type, Integer value) {
        if (value == null) {
            return this;
        }
        switch (type) {
        case OFFSET -> offset = value;
        case LIMIT -> limit = value;
        case END -> end = value;
        }

        return this;
    }
    
    /**
     * clear all Criteria
     * 
     * @return
     */
    public BaseCriteria clear() {
        if (!CommonUtils.isEmpty(sortCriteria)) {
            sortCriteria.clear();
        }
        if (!CommonUtils.isEmpty(tempSortCriteria)) {
            tempSortCriteria.clear();
        }
        if (!CommonUtils.isEmpty(combinaCriteria)) {
            combinaCriteria.clear();
        }
        nonCombinaCriteria = null;
        if (!CommonUtils.isEmpty(criteria)) {
            criteria.clear();
        }
        limit = null;
        end = null;
        offset = 0;
        condUpdated = true;
        sortUpdated = true;
        return this;
    }

    /**
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @return the offset
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * @return the end
     */
    public Integer getEnd() {
        if (end == null && limit != null) {
            end = offset + limit;
        }
        return end;
    }
    
    /**
     * Check the term, and Camel to underscore
     * 
     * @param str
     * @return
     */
    private String toUnderScore(String term) {
        if (term == null) {
            return term;
        }
        int length = term.length();
        StringBuilder result = new StringBuilder(length * 2);
        int resultLength = 0;
        boolean wasPrevTranslated = false;
        for (int i = 0; i < length; i++) {
            char c = term.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (i > 0 || c != InterpunctionConstants.UNDER_LINE) {
                if (Character.isUpperCase(c)) {
                    if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != InterpunctionConstants.UNDER_LINE) {
                        result.append(InterpunctionConstants.UNDER_LINE);
                        resultLength++;
                    }
                    c = Character.toLowerCase(c);
                    wasPrevTranslated = true;
                } else {
                    wasPrevTranslated = false;
                }
                result.append(c);
                resultLength++;
            }
        }
        return (resultLength > 0 ? result.toString() : term);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(InterpunctionConstants.BRACE_LEFT);
        List<CondCriteria> criterias = getCriteria();
        if (!CommonUtils.isEmpty(criterias)) {
            sb.append(InterpunctionConstants.DOUBLE_QUOTATION);
            sb.append("criteria");
            sb.append(InterpunctionConstants.DOUBLE_QUOTATION);
            sb.append(InterpunctionConstants.COLON);
            sb.append(criterias.toString());
            sb.append(InterpunctionConstants.COMMA);
        }
        List<SortCriterion> sortCriterions = getSortCriteria();
        if (!CommonUtils.isEmpty(sortCriterions)) {
            sb.append(InterpunctionConstants.DOUBLE_QUOTATION);
            sb.append("sortCriteria");
            sb.append(InterpunctionConstants.DOUBLE_QUOTATION);
            sb.append(InterpunctionConstants.COLON);
            sb.append(sortCriterions.toString());
            sb.append(InterpunctionConstants.COMMA);
        }
        if (limit != null) {
            sb.append(InterpunctionConstants.DOUBLE_QUOTATION);
            sb.append("limit");
            sb.append(InterpunctionConstants.DOUBLE_QUOTATION);
            sb.append(InterpunctionConstants.COLON);
            sb.append(limit.intValue());
            sb.append(InterpunctionConstants.COMMA);
        }
        if (end != null) {
            sb.append(InterpunctionConstants.DOUBLE_QUOTATION);
            sb.append("end");
            sb.append(InterpunctionConstants.DOUBLE_QUOTATION);
            sb.append(InterpunctionConstants.COLON);
            sb.append(end.intValue());
            sb.append(InterpunctionConstants.COMMA);
        }
        sb.append(InterpunctionConstants.DOUBLE_QUOTATION);
        sb.append("offset");
        sb.append(InterpunctionConstants.DOUBLE_QUOTATION);
        sb.append(InterpunctionConstants.COLON);
        sb.append(offset.intValue());
        sb.append(InterpunctionConstants.BRACE_RIGHT);
        return sb.toString();
    }

}
