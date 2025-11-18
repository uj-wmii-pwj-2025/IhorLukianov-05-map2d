package uj.wmii.pwj.map2d;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;

public class Map2Dimpl<R, C, V> implements Map2D<R, C, V> {
	private HashMap<R, HashMap<C, V>> data;

	public Map2Dimpl() {
		data = new HashMap<R, HashMap<C, V>>();
	}

	@Override
    public V put(R rowKey, C columnKey, V value) {
		if (rowKey == null || columnKey == null) {
			throw new NullPointerException();
		}

		V old = get(rowKey, columnKey);

		if (!data.containsKey(rowKey)) {
			data.put(rowKey, new HashMap<C, V>());
		}

		data.get(rowKey).put(columnKey, value);
		return old;
	}

	@Override
    public V get(R rowKey, C columnKey) {
		if (!data.containsKey(rowKey)) {
			return null;
		}

		return data.get(rowKey).get(columnKey);
	}

	@Override
    public V getOrDefault(R rowKey, C columnKey, V defaultValue) {
		if (!containsKey(rowKey, columnKey)) {
			return defaultValue;
		}

		return get(rowKey, columnKey);
	}

	@Override
    public V remove(R rowKey, C columnKey) {
		if (!containsKey(rowKey, columnKey)) {
			return null;
		}

		return data.get(rowKey).remove(columnKey);
	}

	@Override
    public boolean isEmpty() {
		return size() == 0;
	}

	@Override
    public boolean nonEmpty() {
		return size() != 0;
	}

	@Override
    public int size() {
		int res = 0;

		for (HashMap<C, V> col: data.values()) {
			res += col.size();
		}

		return res;
	}

	@Override
    public void clear() {
		data = new HashMap<R, HashMap<C, V>>();
	}

	@Override
    public Map<C, V> rowView(R rowKey) {
		HashMap<C, V> res = new HashMap<C, V>();

		if (!data.containsKey(rowKey)) {
			return res;
		}

		for (C columnKey: data.get(rowKey).keySet()) {
			res.put(columnKey, data.get(rowKey).get(columnKey));
		}

		return res;
	}

	@Override
    public Map<R, V> columnView(C columnKey) {
		HashMap<R, V> res = new HashMap<R, V>();

		for (R key: data.keySet()) {
			if (data.get(key).containsKey(columnKey)) {
				res.put(key, data.get(key).get(columnKey));
			}
		}

		return res;
	}

	@Override
    public boolean containsValue(V value) {
		for (HashMap<C, V> col: data.values()) {
			if (col.containsValue(value)) {
				return true;
			}
		}

		return false;
	}

	@Override
    public boolean containsKey(R rowKey, C columnKey) {
		if (!data.containsKey(rowKey)) {
			return false;
		}

		return data.get(rowKey).containsKey(columnKey);
	}

	@Override
    public boolean containsRow(R rowKey) {
		return data.containsKey(rowKey);
	}

	@Override
    public boolean containsColumn(C columnKey) {
		return !columnView(columnKey).isEmpty();
	}

	@Override
    public Map<R, Map<C, V>> rowMapView() {
		Map<R, Map<C, V>> res = new HashMap<R, Map<C, V>>();

		for (R rowKey: data.keySet()) {
			HashMap<C, V> col = new HashMap<C, V>();

			for (C columnKey: data.get(rowKey).keySet()) {
				col.put(columnKey, data.get(rowKey).get(columnKey));
			}

			res.put(rowKey, col);
		}

		return res;
	}

	@Override
    public Map<C, Map<R, V>> columnMapView() {
		Map<C, Map<R, V>> res = new HashMap<C, Map<R, V>>();

		for (R rowKey: data.keySet()) {
			for (C columnKey: data.get(rowKey).keySet()) {
				if (!res.containsKey(columnKey)) {
					res.put(columnKey, new HashMap<R, V>());
				}

				res.get(columnKey).put(rowKey, data.get(rowKey).get(columnKey));
			}
		}

		return res;
	}

	@Override
    public Map2D<R, C, V> fillMapFromRow(Map<? super C, ? super V> target, R rowKey) {
		Map<C, V> view = rowView(rowKey);

		for (C columnKey: view.keySet()) {
			target.put(columnKey, data.get(rowKey).get(columnKey));
		}

		return this;
	}

	@Override
    public Map2D<R, C, V> fillMapFromColumn(Map<? super R, ? super V> target, C columnKey) {
		for (R key: data.keySet()) {
			if (data.get(key).containsKey(columnKey)) {
				target.put(key, data.get(key).get(columnKey));
			}
		}

		return this;
	}

	@Override
    public Map2D<R, C, V>  putAll(Map2D<? extends R, ? extends C, ? extends V> source) {
		Map<? extends R, ? extends Map<? extends C, ? extends V>> view = source.rowMapView();

		for (R rowKey: view.keySet()) {
			for (C columnKey: view.get(rowKey).keySet()) {
				put(rowKey, columnKey, view.get(rowKey).get(columnKey));
			}
		}

		return this;
	}

	@Override
    public Map2D<R, C, V>  putAllToRow(Map<? extends C, ? extends V> source, R rowKey) {
		for (C columnKey: source.keySet()) {
			put(rowKey, columnKey, source.get(columnKey));
		}

		return this;
	}

	@Override
    public Map2D<R, C, V>  putAllToColumn(Map<? extends R, ? extends V> source, C columnKey) {
		for (R rowKey: source.keySet()) {
			put(rowKey, columnKey, source.get(rowKey));
		}

		return this;
	}

	@Override
    public <R2, C2, V2> Map2D<R2, C2, V2> copyWithConversion(
        Function<? super R, ? extends R2> rowFunction,
        Function<? super C, ? extends C2> columnFunction,
        Function<? super V, ? extends V2> valueFunction) {
		Map2D<R2, C2, V2> res = new Map2Dimpl<R2, C2, V2>();

		for (R rowKey: data.keySet()) {
			for (C columnKey: data.get(rowKey).keySet()) {
				res.put(rowFunction.apply(rowKey), columnFunction.apply(columnKey), valueFunction.apply(data.get(rowKey).get(columnKey)));
			}
		}

		return res;
	}
}
