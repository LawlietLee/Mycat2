/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package io.mycat.config.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.media.VideoTrack;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author mycat
 */
public class SchemaConfig implements Serializable{
	private static  long serialVersionUID = -6605226933829917213L;
	@JsonIgnore
	private  Random random = new Random();
	@NotNull
	private String name;
	private Map<String, TableConfig> tables=new HashMap<>();
	private  boolean noSharding;
	private  String dataNode;
	private  Set<String> metaDataNodes;
	private  Set<String> allDataNodes;
	/**
	 * when a select sql has no limit condition ,and default max limit to
	 * prevent memory problem when return a large result set
	 */
	private  int defaultMaxLimit=100;
	private  boolean checkSQLSchema;
	private  boolean needSupportMultiDBType=false;
	private  String defaultDataNodeDbType;
	/**
	 * key is join relation ,A.ID=B.PARENT_ID value is Root Table ,if a->b*->c*
	 * ,then A is root table
	 */
	private  Map<String, TableConfig> joinRel2TableMap = new HashMap<String, TableConfig>();
	private  String[] allDataNodeStrArr;

	private  Map<String,String> dataNodeDbTypeMap=new HashMap<>();

	/**
	 * Addtable.
	 *
	 * @param tableConfig the table config
	 */
	public void addtable(TableConfig tableConfig) {
		tables.put(tableConfig.getName().toUpperCase(), tableConfig);
		buildJoinMap(tables);
		this.noSharding = (tables == null || tables.isEmpty());

		if (noSharding && dataNode == null) {
			throw new RuntimeException(name
					+ " in noSharding mode schema must have default dataNode ");
		}
		this.metaDataNodes = buildMetaDataNodes();
		this.allDataNodes = buildAllDataNodes();
//		this.metaDataNodes = buildAllDataNodes();
		if (this.allDataNodes != null && !this.allDataNodes.isEmpty()) {
			String[] dnArr = new String[this.allDataNodes.size()];
			dnArr = this.allDataNodes.toArray(dnArr);
			this.allDataNodeStrArr = dnArr;
		} else {
			this.allDataNodeStrArr = null;
		}
	}
	public SchemaConfig() {

	}

	public SchemaConfig(String name, String dataNode,
						Map<String, TableConfig> tables, int defaultMaxLimit,
						boolean checkSQLschema) {
		this.name = name;
		this.dataNode = dataNode;
		this.checkSQLSchema = checkSQLschema;
		this.tables = tables;
		this.defaultMaxLimit = defaultMaxLimit;
		buildJoinMap(tables);
		this.noSharding = (tables == null || tables.isEmpty());
		if (noSharding && dataNode == null) {
			throw new RuntimeException(name
					+ " in noSharding mode schema must have default dataNode ");
		}
		this.metaDataNodes = buildMetaDataNodes();
		this.allDataNodes = buildAllDataNodes();
//		this.metaDataNodes = buildAllDataNodes();
		if (this.allDataNodes != null && !this.allDataNodes.isEmpty()) {
			String[] dnArr = new String[this.allDataNodes.size()];
			dnArr = this.allDataNodes.toArray(dnArr);
			this.allDataNodeStrArr = dnArr;
		} else {
			this.allDataNodeStrArr = null;
		}
	}

	public String getDefaultDataNodeDbType()
	{
		return defaultDataNodeDbType;
	}

	public void setDefaultDataNodeDbType(String defaultDataNodeDbType)
	{
		this.defaultDataNodeDbType = defaultDataNodeDbType;
	}

	public boolean isCheckSQLSchema() {
		return checkSQLSchema;
	}

	public int getDefaultMaxLimit() {
		return defaultMaxLimit;
	}

	private void buildJoinMap(Map<String, TableConfig> tables2) {

		if (tables == null || tables.isEmpty()) {
			return;
		}
		for (TableConfig tc : tables.values()) {
			if (tc.isChildTable()) {
				TableConfig rootTc = tc.getRootParent();
				String joinRel1 = tc.getName() + '.' + tc.getJoinKey() + '='
						+ tc.getParentTC().getName() + '.' + tc.getParentKey();
				String joinRel2 = tc.getParentTC().getName() + '.'
						+ tc.getParentKey() + '=' + tc.getName() + '.'
						+ tc.getJoinKey();
				joinRel2TableMap.put(joinRel1, rootTc);
				joinRel2TableMap.put(joinRel2, rootTc);
			}

		}

	}

	public boolean isNeedSupportMultiDBType()
	{
		return needSupportMultiDBType;
	}

	public void setNeedSupportMultiDBType(boolean needSupportMultiDBType)
	{
		this.needSupportMultiDBType = needSupportMultiDBType;
	}

	public Map<String, TableConfig> getJoinRel2TableMap() {
		return joinRel2TableMap;
	}

	public String getName() {
		return name;
	}

	public String getDataNode() {
		return dataNode;
	}

	public Map<String, TableConfig> getTables() {
		return tables;
	}

	public boolean isNoSharding() {
		return noSharding;
	}

	public Set<String> getMetaDataNodes() {
		return metaDataNodes;
	}

	public Set<String> getAllDataNodes() {
		return allDataNodes;
	}

	public Map<String, String> getDataNodeDbTypeMap()
	{
		return dataNodeDbTypeMap;
	}

	public void setDataNodeDbTypeMap(Map<String, String> dataNodeDbTypeMap)
	{
		this.dataNodeDbTypeMap = dataNodeDbTypeMap;
	}

	public String getRandomDataNode() {
		if (this.allDataNodeStrArr == null) {
			return null;
		}
		int index = Math.abs(random.nextInt(Integer.MAX_VALUE)) % allDataNodeStrArr.length;
		return this.allDataNodeStrArr[index];
	}

	/**
	 * 取得含有不同Meta信息的数据节点,比如表和表结构。
	 */
	private Set<String> buildMetaDataNodes() {
		Set<String> set = new HashSet<String>();
		if (!isEmpty(dataNode)) {
			set.add(dataNode);
		}
		if (!noSharding) {
			for (TableConfig tc : tables.values()) {
				set.add(tc.getDataNodes().get(0));
			}
		}

		return set;
	}

	/**
	 * 取得该schema的所有数据节点
	 */
	private Set<String> buildAllDataNodes() {
		Set<String> set = new HashSet<String>();
		if (!isEmpty(dataNode)) {
			set.add(dataNode);
		}
		if (!noSharding) {
			for (TableConfig tc : tables.values()) {
				set.addAll(tc.getDataNodes());
			}
		}
		return set;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTables(Map<String, TableConfig> tables) {
		this.tables = tables;
	}

	public void setNoSharding(boolean noSharding) {
		this.noSharding = noSharding;
	}

	public void setDataNode(String dataNode) {
		this.dataNode = dataNode;
	}

	public void setMetaDataNodes(Set<String> metaDataNodes) {
		this.metaDataNodes = metaDataNodes;
	}

	public void setAllDataNodes(Set<String> allDataNodes) {
		this.allDataNodes = allDataNodes;
	}

	public void setDefaultMaxLimit(int defaultMaxLimit) {
		this.defaultMaxLimit = defaultMaxLimit;
	}

	public void setCheckSQLSchema(boolean checkSQLSchema) {
		this.checkSQLSchema = checkSQLSchema;
	}

	public void setJoinRel2TableMap(Map<String, TableConfig> joinRel2TableMap) {
		this.joinRel2TableMap = joinRel2TableMap;
	}

	public String[] getAllDataNodeStrArr() {
		return allDataNodeStrArr;
	}

	public void setAllDataNodeStrArr(String[] allDataNodeStrArr) {
		this.allDataNodeStrArr = allDataNodeStrArr;
	}

	private static boolean isEmpty(String str) {
		return ((str == null) || (str.length() == 0));
	}

}