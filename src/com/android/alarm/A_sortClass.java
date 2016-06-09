package com.android.alarm;

import java.util.List;

public class A_sortClass {
	public A_sortClass() {
	}

	public List<A_DbInfoClass> Sort(List<A_DbInfoClass> DbInfoClassList) {
		A_DbInfoClass temp = new A_DbInfoClass();
		for (int i = 0; i < DbInfoClassList.size(); i++) {
			for (int j = 0; j < DbInfoClassList.size() - i - 1; j++) {
				if ((Integer.parseInt(DbInfoClassList.get(j).getTime()) - Integer
						.parseInt(DbInfoClassList.get(j + 1).getTime())) > 0) {
					temp.setID(DbInfoClassList.get(j).getID());
					temp.setHour(DbInfoClassList.get(j).getHour());
					temp.setMin(DbInfoClassList.get(j).getMin());
					temp.setCheck(DbInfoClassList.get(j).getCheck());
					temp.setTime(DbInfoClassList.get(j).getTime());

					DbInfoClassList.get(j).setID(
							DbInfoClassList.get(j + 1).getID());
					DbInfoClassList.get(j).setHour(
							DbInfoClassList.get(j + 1).getHour());
					DbInfoClassList.get(j).setMin(
							DbInfoClassList.get(j + 1).getMin());
					DbInfoClassList.get(j).setCheck(
							DbInfoClassList.get(j + 1).getCheck());
					DbInfoClassList.get(j).setTime(
							DbInfoClassList.get(j + 1).getTime());

					DbInfoClassList.get(j + 1).setID(temp.getID());
					DbInfoClassList.get(j + 1).setHour(temp.getHour());
					DbInfoClassList.get(j + 1).setMin(temp.getMin());
					DbInfoClassList.get(j + 1).setCheck(temp.getCheck());
					DbInfoClassList.get(j + 1).setTime(temp.getTime());
				}
			}
		}
		return DbInfoClassList;
	}

}
