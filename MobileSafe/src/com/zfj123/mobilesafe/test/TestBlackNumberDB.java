package com.zfj123.mobilesafe.test;

import java.util.List;
import java.util.Random;

import com.zfj123.mobilesafe.bean.BlackNumberInfo;
import com.zfj123.mobilesafe.db.BlackNumberDBOpenHelper;
import com.zfj123.mobilesafe.db.dao.BlackNumberDao;

import android.test.AndroidTestCase;

public class TestBlackNumberDB extends AndroidTestCase {

	
	public void testCreateDB() throws Exception {
		BlackNumberDBOpenHelper helper= new BlackNumberDBOpenHelper(
				getContext()); ;
		helper.getWritableDatabase();
	}

	public void testAdd() throws Exception {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		Random random=new Random();
		for (int i = 100; i < 200; i++) {
			dao.add(String.valueOf(100+i), String.valueOf(random.nextInt(3)+1));
		}
	}
	
	public void testFindAll(){
		BlackNumberDao dao=new BlackNumberDao(getContext());
		List<BlackNumberInfo> infos = dao.findAll();
		for (BlackNumberInfo info : infos) {
			System.out.println(info.toString());
		}
	}

	public void testDelete() throws Exception {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		dao.delete("110");
	}

	public void testUpdate() throws Exception {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		dao.update("110", "2");
	}

	public void testFind() throws Exception {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		boolean result = dao.find("110");
		assertEquals(true, result);
	}
}
