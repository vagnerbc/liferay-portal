/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;

import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.impl.BaseModelImpl;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization;
import com.liferay.portal.tools.service.builder.test.model.LVEntryLocalizationModel;
import com.liferay.portal.tools.service.builder.test.model.LVEntryLocalizationVersion;

import java.io.Serializable;

import java.sql.Types;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The base model implementation for the LVEntryLocalization service. Represents a row in the &quot;LVEntryLocalization&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This implementation and its corresponding interface {@link LVEntryLocalizationModel} exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link LVEntryLocalizationImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LVEntryLocalizationImpl
 * @see LVEntryLocalization
 * @see LVEntryLocalizationModel
 * @generated
 */
@ProviderType
public class LVEntryLocalizationModelImpl extends BaseModelImpl<LVEntryLocalization>
	implements LVEntryLocalizationModel {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. All methods that expect a lv entry localization model instance should use the {@link LVEntryLocalization} interface instead.
	 */
	public static final String TABLE_NAME = "LVEntryLocalization";
	public static final Object[][] TABLE_COLUMNS = {
			{ "mvccVersion", Types.BIGINT },
			{ "headId", Types.BIGINT },
			{ "lvEntryLocalizationId", Types.BIGINT },
			{ "lvEntryId", Types.BIGINT },
			{ "languageId", Types.VARCHAR },
			{ "title", Types.VARCHAR },
			{ "content", Types.VARCHAR },
			{ "head", Types.BOOLEAN }
		};
	public static final Map<String, Integer> TABLE_COLUMNS_MAP = new HashMap<String, Integer>();

	static {
		TABLE_COLUMNS_MAP.put("mvccVersion", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("headId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("lvEntryLocalizationId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("lvEntryId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("languageId", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("title", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("content", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("head", Types.BOOLEAN);
	}

	public static final String TABLE_SQL_CREATE = "create table LVEntryLocalization (mvccVersion LONG default 0 not null,headId LONG,lvEntryLocalizationId LONG not null primary key,lvEntryId LONG,languageId VARCHAR(75) null,title VARCHAR(75) null,content VARCHAR(75) null,head BOOLEAN)";
	public static final String TABLE_SQL_DROP = "drop table LVEntryLocalization";
	public static final String ORDER_BY_JPQL = " ORDER BY lvEntryLocalization.lvEntryLocalizationId ASC";
	public static final String ORDER_BY_SQL = " ORDER BY LVEntryLocalization.lvEntryLocalizationId ASC";
	public static final String DATA_SOURCE = "liferayDataSource";
	public static final String SESSION_FACTORY = "liferaySessionFactory";
	public static final String TX_MANAGER = "liferayTransactionManager";
	public static final boolean ENTITY_CACHE_ENABLED = GetterUtil.getBoolean(com.liferay.portal.tools.service.builder.test.service.util.ServiceProps.get(
				"value.object.entity.cache.enabled.com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization"),
			true);
	public static final boolean FINDER_CACHE_ENABLED = GetterUtil.getBoolean(com.liferay.portal.tools.service.builder.test.service.util.ServiceProps.get(
				"value.object.finder.cache.enabled.com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization"),
			true);
	public static final boolean COLUMN_BITMASK_ENABLED = GetterUtil.getBoolean(com.liferay.portal.tools.service.builder.test.service.util.ServiceProps.get(
				"value.object.column.bitmask.enabled.com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization"),
			true);
	public static final long HEAD_COLUMN_BITMASK = 1L;
	public static final long HEADID_COLUMN_BITMASK = 2L;
	public static final long LANGUAGEID_COLUMN_BITMASK = 4L;
	public static final long LVENTRYID_COLUMN_BITMASK = 8L;
	public static final long LVENTRYLOCALIZATIONID_COLUMN_BITMASK = 16L;
	public static final long LOCK_EXPIRATION_TIME = GetterUtil.getLong(com.liferay.portal.tools.service.builder.test.service.util.ServiceProps.get(
				"lock.expiration.time.com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization"));

	public LVEntryLocalizationModelImpl() {
	}

	@Override
	public long getPrimaryKey() {
		return _lvEntryLocalizationId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setLvEntryLocalizationId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _lvEntryLocalizationId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Class<?> getModelClass() {
		return LVEntryLocalization.class;
	}

	@Override
	public String getModelClassName() {
		return LVEntryLocalization.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		Map<String, Function<LVEntryLocalization, Object>> attributeGetterFunctions =
			getAttributeGetterFunctions();

		for (Map.Entry<String, Function<LVEntryLocalization, Object>> entry : attributeGetterFunctions.entrySet()) {
			String attributeName = entry.getKey();
			Function<LVEntryLocalization, Object> attributeGetterFunction = entry.getValue();

			attributes.put(attributeName,
				attributeGetterFunction.apply((LVEntryLocalization)this));
		}

		attributes.put("entityCacheEnabled", isEntityCacheEnabled());
		attributes.put("finderCacheEnabled", isFinderCacheEnabled());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Map<String, BiConsumer<LVEntryLocalization, Object>> attributeSetterBiConsumers =
			getAttributeSetterBiConsumers();

		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attributeName = entry.getKey();

			BiConsumer<LVEntryLocalization, Object> attributeSetterBiConsumer = attributeSetterBiConsumers.get(attributeName);

			if (attributeSetterBiConsumer != null) {
				attributeSetterBiConsumer.accept((LVEntryLocalization)this,
					entry.getValue());
			}
		}
	}

	public Map<String, Function<LVEntryLocalization, Object>> getAttributeGetterFunctions() {
		return _attributeGetterFunctions;
	}

	public Map<String, BiConsumer<LVEntryLocalization, Object>> getAttributeSetterBiConsumers() {
		return _attributeSetterBiConsumers;
	}

	private static final Map<String, Function<LVEntryLocalization, Object>> _attributeGetterFunctions;
	private static final Map<String, BiConsumer<LVEntryLocalization, Object>> _attributeSetterBiConsumers;

	static {
		Map<String, Function<LVEntryLocalization, Object>> attributeGetterFunctions =
			new LinkedHashMap<String, Function<LVEntryLocalization, Object>>();
		Map<String, BiConsumer<LVEntryLocalization, ?>> attributeSetterBiConsumers =
			new LinkedHashMap<String, BiConsumer<LVEntryLocalization, ?>>();


		attributeGetterFunctions.put(
			"mvccVersion",
			new Function<LVEntryLocalization, Object>() {

				@Override
				public Object apply(LVEntryLocalization lvEntryLocalization) {
					return lvEntryLocalization.getMvccVersion();
				}

			});
		attributeSetterBiConsumers.put(
			"mvccVersion",
			new BiConsumer<LVEntryLocalization, Object>() {

				@Override
				public void accept(LVEntryLocalization lvEntryLocalization, Object mvccVersion) {
					lvEntryLocalization.setMvccVersion((Long)mvccVersion);
				}

			});
		attributeGetterFunctions.put(
			"headId",
			new Function<LVEntryLocalization, Object>() {

				@Override
				public Object apply(LVEntryLocalization lvEntryLocalization) {
					return lvEntryLocalization.getHeadId();
				}

			});
		attributeSetterBiConsumers.put(
			"headId",
			new BiConsumer<LVEntryLocalization, Object>() {

				@Override
				public void accept(LVEntryLocalization lvEntryLocalization, Object headId) {
					lvEntryLocalization.setHeadId((Long)headId);
				}

			});
		attributeGetterFunctions.put(
			"lvEntryLocalizationId",
			new Function<LVEntryLocalization, Object>() {

				@Override
				public Object apply(LVEntryLocalization lvEntryLocalization) {
					return lvEntryLocalization.getLvEntryLocalizationId();
				}

			});
		attributeSetterBiConsumers.put(
			"lvEntryLocalizationId",
			new BiConsumer<LVEntryLocalization, Object>() {

				@Override
				public void accept(LVEntryLocalization lvEntryLocalization, Object lvEntryLocalizationId) {
					lvEntryLocalization.setLvEntryLocalizationId((Long)lvEntryLocalizationId);
				}

			});
		attributeGetterFunctions.put(
			"lvEntryId",
			new Function<LVEntryLocalization, Object>() {

				@Override
				public Object apply(LVEntryLocalization lvEntryLocalization) {
					return lvEntryLocalization.getLvEntryId();
				}

			});
		attributeSetterBiConsumers.put(
			"lvEntryId",
			new BiConsumer<LVEntryLocalization, Object>() {

				@Override
				public void accept(LVEntryLocalization lvEntryLocalization, Object lvEntryId) {
					lvEntryLocalization.setLvEntryId((Long)lvEntryId);
				}

			});
		attributeGetterFunctions.put(
			"languageId",
			new Function<LVEntryLocalization, Object>() {

				@Override
				public Object apply(LVEntryLocalization lvEntryLocalization) {
					return lvEntryLocalization.getLanguageId();
				}

			});
		attributeSetterBiConsumers.put(
			"languageId",
			new BiConsumer<LVEntryLocalization, Object>() {

				@Override
				public void accept(LVEntryLocalization lvEntryLocalization, Object languageId) {
					lvEntryLocalization.setLanguageId((String)languageId);
				}

			});
		attributeGetterFunctions.put(
			"title",
			new Function<LVEntryLocalization, Object>() {

				@Override
				public Object apply(LVEntryLocalization lvEntryLocalization) {
					return lvEntryLocalization.getTitle();
				}

			});
		attributeSetterBiConsumers.put(
			"title",
			new BiConsumer<LVEntryLocalization, Object>() {

				@Override
				public void accept(LVEntryLocalization lvEntryLocalization, Object title) {
					lvEntryLocalization.setTitle((String)title);
				}

			});
		attributeGetterFunctions.put(
			"content",
			new Function<LVEntryLocalization, Object>() {

				@Override
				public Object apply(LVEntryLocalization lvEntryLocalization) {
					return lvEntryLocalization.getContent();
				}

			});
		attributeSetterBiConsumers.put(
			"content",
			new BiConsumer<LVEntryLocalization, Object>() {

				@Override
				public void accept(LVEntryLocalization lvEntryLocalization, Object content) {
					lvEntryLocalization.setContent((String)content);
				}

			});


		_attributeGetterFunctions = Collections.unmodifiableMap(attributeGetterFunctions);
		_attributeSetterBiConsumers = Collections.unmodifiableMap((Map)attributeSetterBiConsumers);
	}

	public boolean getHead() {
		return _head;
	}

	@Override
	public boolean isHead() {
		return _head;
	}

	public boolean getOriginalHead() {
		return _originalHead;
	}

	public void setHead(boolean head) {
		_columnBitmask |= HEAD_COLUMN_BITMASK;

		if (!_setOriginalHead) {
			_setOriginalHead = true;

			_originalHead = _head;
		}

		_head = head;
	}

	@Override
	public void populateVersionModel(
		LVEntryLocalizationVersion lvEntryLocalizationVersion) {
		lvEntryLocalizationVersion.setLvEntryId(getLvEntryId());
		lvEntryLocalizationVersion.setLanguageId(getLanguageId());
		lvEntryLocalizationVersion.setTitle(getTitle());
		lvEntryLocalizationVersion.setContent(getContent());
	}

	@Override
	public long getMvccVersion() {
		return _mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		_mvccVersion = mvccVersion;
	}

	@Override
	public long getHeadId() {
		return _headId;
	}

	@Override
	public void setHeadId(long headId) {
		_columnBitmask |= HEADID_COLUMN_BITMASK;

		if (!_setOriginalHeadId) {
			_setOriginalHeadId = true;

			_originalHeadId = _headId;
		}

		if (headId >= 0) {
			setHead(false);
		}
		else {
			setHead(true);
		}

		_headId = headId;
	}

	public long getOriginalHeadId() {
		return _originalHeadId;
	}

	@Override
	public long getLvEntryLocalizationId() {
		return _lvEntryLocalizationId;
	}

	@Override
	public void setLvEntryLocalizationId(long lvEntryLocalizationId) {
		_lvEntryLocalizationId = lvEntryLocalizationId;
	}

	@Override
	public long getLvEntryId() {
		return _lvEntryId;
	}

	@Override
	public void setLvEntryId(long lvEntryId) {
		_columnBitmask |= LVENTRYID_COLUMN_BITMASK;

		if (!_setOriginalLvEntryId) {
			_setOriginalLvEntryId = true;

			_originalLvEntryId = _lvEntryId;
		}

		_lvEntryId = lvEntryId;
	}

	public long getOriginalLvEntryId() {
		return _originalLvEntryId;
	}

	@Override
	public String getLanguageId() {
		if (_languageId == null) {
			return "";
		}
		else {
			return _languageId;
		}
	}

	@Override
	public void setLanguageId(String languageId) {
		_columnBitmask |= LANGUAGEID_COLUMN_BITMASK;

		if (_originalLanguageId == null) {
			_originalLanguageId = _languageId;
		}

		_languageId = languageId;
	}

	public String getOriginalLanguageId() {
		return GetterUtil.getString(_originalLanguageId);
	}

	@Override
	public String getTitle() {
		if (_title == null) {
			return "";
		}
		else {
			return _title;
		}
	}

	@Override
	public void setTitle(String title) {
		_title = title;
	}

	@Override
	public String getContent() {
		if (_content == null) {
			return "";
		}
		else {
			return _content;
		}
	}

	@Override
	public void setContent(String content) {
		_content = content;
	}

	public long getColumnBitmask() {
		return _columnBitmask;
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return ExpandoBridgeFactoryUtil.getExpandoBridge(0,
			LVEntryLocalization.class.getName(), getPrimaryKey());
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		ExpandoBridge expandoBridge = getExpandoBridge();

		expandoBridge.setAttributes(serviceContext);
	}

	@Override
	public LVEntryLocalization toEscapedModel() {
		if (_escapedModel == null) {
			_escapedModel = (LVEntryLocalization)ProxyUtil.newProxyInstance(_classLoader,
					_escapedModelInterfaces, new AutoEscapeBeanHandler(this));
		}

		return _escapedModel;
	}

	@Override
	public Object clone() {
		LVEntryLocalizationImpl lvEntryLocalizationImpl = new LVEntryLocalizationImpl();

		lvEntryLocalizationImpl.setMvccVersion(getMvccVersion());
		lvEntryLocalizationImpl.setHeadId(getHeadId());
		lvEntryLocalizationImpl.setLvEntryLocalizationId(getLvEntryLocalizationId());
		lvEntryLocalizationImpl.setLvEntryId(getLvEntryId());
		lvEntryLocalizationImpl.setLanguageId(getLanguageId());
		lvEntryLocalizationImpl.setTitle(getTitle());
		lvEntryLocalizationImpl.setContent(getContent());

		lvEntryLocalizationImpl.resetOriginalValues();

		return lvEntryLocalizationImpl;
	}

	@Override
	public int compareTo(LVEntryLocalization lvEntryLocalization) {
		long primaryKey = lvEntryLocalization.getPrimaryKey();

		if (getPrimaryKey() < primaryKey) {
			return -1;
		}
		else if (getPrimaryKey() > primaryKey) {
			return 1;
		}
		else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof LVEntryLocalization)) {
			return false;
		}

		LVEntryLocalization lvEntryLocalization = (LVEntryLocalization)obj;

		long primaryKey = lvEntryLocalization.getPrimaryKey();

		if (getPrimaryKey() == primaryKey) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (int)getPrimaryKey();
	}

	@Override
	public boolean isEntityCacheEnabled() {
		return ENTITY_CACHE_ENABLED;
	}

	@Override
	public boolean isFinderCacheEnabled() {
		return FINDER_CACHE_ENABLED;
	}

	@Override
	public void resetOriginalValues() {
		LVEntryLocalizationModelImpl lvEntryLocalizationModelImpl = this;

		lvEntryLocalizationModelImpl._originalHeadId = lvEntryLocalizationModelImpl._headId;

		lvEntryLocalizationModelImpl._setOriginalHeadId = false;

		lvEntryLocalizationModelImpl._originalLvEntryId = lvEntryLocalizationModelImpl._lvEntryId;

		lvEntryLocalizationModelImpl._setOriginalLvEntryId = false;

		lvEntryLocalizationModelImpl._originalLanguageId = lvEntryLocalizationModelImpl._languageId;

		lvEntryLocalizationModelImpl._originalHead = lvEntryLocalizationModelImpl._head;

		lvEntryLocalizationModelImpl._setOriginalHead = false;

		lvEntryLocalizationModelImpl._columnBitmask = 0;
	}

	@Override
	public CacheModel<LVEntryLocalization> toCacheModel() {
		LVEntryLocalizationCacheModel lvEntryLocalizationCacheModel = new LVEntryLocalizationCacheModel();

		lvEntryLocalizationCacheModel.mvccVersion = getMvccVersion();

		lvEntryLocalizationCacheModel.headId = getHeadId();

		lvEntryLocalizationCacheModel.lvEntryLocalizationId = getLvEntryLocalizationId();

		lvEntryLocalizationCacheModel.lvEntryId = getLvEntryId();

		lvEntryLocalizationCacheModel.languageId = getLanguageId();

		String languageId = lvEntryLocalizationCacheModel.languageId;

		if ((languageId != null) && (languageId.length() == 0)) {
			lvEntryLocalizationCacheModel.languageId = null;
		}

		lvEntryLocalizationCacheModel.title = getTitle();

		String title = lvEntryLocalizationCacheModel.title;

		if ((title != null) && (title.length() == 0)) {
			lvEntryLocalizationCacheModel.title = null;
		}

		lvEntryLocalizationCacheModel.content = getContent();

		String content = lvEntryLocalizationCacheModel.content;

		if ((content != null) && (content.length() == 0)) {
			lvEntryLocalizationCacheModel.content = null;
		}

		lvEntryLocalizationCacheModel.head = isHead();

		return lvEntryLocalizationCacheModel;
	}

	@Override
	public String toString() {
		Map<String, Function<LVEntryLocalization, Object>> attributeGetterFunctions =
			getAttributeGetterFunctions();

		StringBundler sb = new StringBundler((4 * attributeGetterFunctions.size()) +
				2);

		sb.append("{");

		for (Map.Entry<String, Function<LVEntryLocalization, Object>> entry : attributeGetterFunctions.entrySet()) {
			String attributeName = entry.getKey();
			Function<LVEntryLocalization, Object> attributeGetterFunction = entry.getValue();

			sb.append(attributeName);
			sb.append("=");
			sb.append(attributeGetterFunction.apply((LVEntryLocalization)this));
			sb.append(", ");
		}

		if (sb.index() > 1) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		Map<String, Function<LVEntryLocalization, Object>> attributeGetterFunctions =
			getAttributeGetterFunctions();

		StringBundler sb = new StringBundler((5 * attributeGetterFunctions.size()) +
				4);

		sb.append("<model><model-name>");
		sb.append(getModelClassName());
		sb.append("</model-name>");

		for (Map.Entry<String, Function<LVEntryLocalization, Object>> entry : attributeGetterFunctions.entrySet()) {
			String attributeName = entry.getKey();
			Function<LVEntryLocalization, Object> attributeGetterFunction = entry.getValue();

			sb.append("<column><column-name>");
			sb.append(attributeName);
			sb.append("</column-name><column-value><![CDATA[");
			sb.append(attributeGetterFunction.apply((LVEntryLocalization)this));
			sb.append("]]></column-value></column>");
		}

		sb.append("</model>");

		return sb.toString();
	}

	private static final ClassLoader _classLoader = LVEntryLocalization.class.getClassLoader();
	private static final Class<?>[] _escapedModelInterfaces = new Class[] {
			LVEntryLocalization.class, ModelWrapper.class
		};
	private long _mvccVersion;
	private long _headId;
	private long _originalHeadId;
	private boolean _setOriginalHeadId;
	private long _lvEntryLocalizationId;
	private long _lvEntryId;
	private long _originalLvEntryId;
	private boolean _setOriginalLvEntryId;
	private String _languageId;
	private String _originalLanguageId;
	private String _title;
	private String _content;
	private boolean _head;
	private boolean _originalHead;
	private boolean _setOriginalHead;
	private long _columnBitmask;
	private LVEntryLocalization _escapedModel;
}