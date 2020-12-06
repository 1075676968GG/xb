package com.example.xb.Service;


import com.example.xb.dao.DeptDao;
import com.example.xb.entity.Dept;
import com.example.xb.mapper.DeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DeptService {

    @Autowired
    private DeptDao deptDao;

    @Autowired
    private DeptMapper deptMapper;

    /**
     * 查询全部部门
     *
     * @return
     */
    public List<Dept> findAll() {
        return deptDao.findAll();
    }

    public Dept findById(Long deptId) {
        return deptDao.findById(deptId).get();
    }

    public List<Map> findDeptAll() {
        return deptMapper.findDeptAll();
    }
}