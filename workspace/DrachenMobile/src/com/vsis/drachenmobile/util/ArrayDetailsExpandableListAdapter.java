package com.vsis.drachenmobile.util;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.widget.BaseExpandableListAdapter;

/**
 * Adapter for expandable lists, which has detail information about the header
 * object so the children are kind of fields of the header object
 * 
 * @param <T>
 *            Type of the header items in List
 */
public abstract class ArrayDetailsExpandableListAdapter<T> extends
		BaseExpandableListAdapter {

	protected Context _context;
	protected List<T> _listDataHeader; // header titles

	public ArrayDetailsExpandableListAdapter(Context context,
			List<T> listDataHeader) {
		this._context = context;
		this._listDataHeader = listDataHeader;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return getChild(getGroup(groupPosition), childPosititon);
	}

	protected abstract Object getChild(T group, int childPosition);

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return getChildrenCount(getGroup(groupPosition));
	}

	protected abstract int getChildrenCount(T group);

	@Override
	public T getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void addGroup(T group) {
		_listDataHeader.add(group);
	}

	public void removeGroup(T group) {
		_listDataHeader.remove(group);
	}

	public List<T> getGroupList() {
		return _listDataHeader;
	}

	public void clear() {
		_listDataHeader.clear();
	}

	public void addAll(Collection<T> collection) {
		_listDataHeader.addAll(collection);
	}

}