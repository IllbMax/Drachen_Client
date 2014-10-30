package com.vsis.drachenmobile.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.widget.BaseExpandableListAdapter;

/**
 * 
 * @author
 * 
 * @param <T>
 *            Type of the header items in List
 * @param <U>
 *            Type of the child items expanded under the headers
 */
public abstract class ArrayMapExpandableListAdapter<T, U> extends
		BaseExpandableListAdapter {

	protected Context _context;
	protected List<T> _listDataHeader; // header titles
	// child data in format of header title, child title
	protected HashMap<T, List<U>> _listDataChild;

	public ArrayMapExpandableListAdapter(Context context,
			List<T> listDataHeader, HashMap<T, List<U>> listChildData) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
	}

	@Override
	public U getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.size();
	}

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
		addGroup(group, new ArrayList<U>());
	}

	public void addGroup(T group, List<U> children) {
		_listDataHeader.add(group);
		_listDataChild.put(group, children);
	}

	public void removeGroup(T group) {
		_listDataHeader.remove(group);
		_listDataChild.remove(group);
	}

	public void removeChild(T group, U child) {
		_listDataChild.get(group).remove(child);
	}

	public List<T> getGroupList() {
		return _listDataHeader;
	}

	public HashMap<T, List<U>> getChildMap() {
		return _listDataChild;

	}
}