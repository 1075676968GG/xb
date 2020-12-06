package com.example.xb.Service;

import com.example.xb.dao.DeptDao;
import com.example.xb.dao.MeetingDao;
import com.example.xb.entity.Dept;
import com.example.xb.entity.Meeting;
import com.example.xb.entity.PageResult;
import com.example.xb.mapper.MeetingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liao
 * @date 2020/12/3 12:32
 * @Description
 */
@Service
public class MeetingService {
    @Autowired
    MeetingDao meetingDao;

    @Autowired
    MeetingMapper meetingMapper;

    @Autowired
    DeptDao deptDao;


    public void save(Meeting meeting) {
        Long deptId = meeting.getDeptId();
        Dept dept = deptDao.findById(deptId).get();
        meeting.setDeptName(dept.getName());
        meeting.setStatus(0L);//默认状态0，未开始
        meeting.setPublishDate(new Date());//发布日期为当前时间
        meetingDao.save(meeting);

    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Specification<Meeting> createSpec(Map searchMap) {

        return new Specification<Meeting>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery cq, CriteriaBuilder cb) {

                List<Predicate> predList = new ArrayList<>();

                if (searchMap.get("title") != null && !"".equals(searchMap.get("title"))) {
                    // 标题模糊查找
                    predList.add(cb.like(root.get("title").as(String.class), "%" + searchMap.get("title").toString() + "%"));
                }

                if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                    // 状态精确查找
                    predList.add(cb.equal(root.get("status").as(Long.class), Long.parseLong(searchMap.get("status").toString())));
                }

                Predicate[] predicates = new Predicate[predList.size()];

                return cb.and(predList.toArray(predicates));
            }
        };

    }

    /**
     * 条件搜索+分页 (按照会议发布时间倒序排序)
     *
     * @param searchMap
     * @param page
     * @return
     */

    public Page<Meeting> findPage(Map searchMap, Integer page) {
        Specification<Meeting> spec = createSpec(searchMap);
        return meetingDao.findAll(spec, PageRequest.of(page - 1, PageResult.PAGE_SIZE, Sort.by("publishDate").descending()));
    }


    public Meeting findById(Long id) {
        return meetingDao.findById(id).get();
    }

    public Integer countMeetingJoinByMeetingId(Long id) {
        return meetingDao.countMeetingJoinByMeetingId(id);
    }

    public Boolean isJoinMeeting(Long userId, Long meetingId) {
        Integer join = meetingDao.countMeetingJoinByUserIdAndMeetingId(userId, meetingId);
        if (join > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 取消会议
     *
     * @param userId
     * @param meetingId
     */
    @Transactional
    public void unJoinMeeting(Long userId, Long meetingId) {
        meetingDao.deleteMeeting(userId, meetingId);
    }

    /**
     * 参加会议
     *
     * @param userId
     * @param meetingId
     */
    @Transactional
    public void joinMeeting(Long userId, Long meetingId) {
        meetingDao.insertMeeting(userId, meetingId);
    }

    public List<Meeting> findByStatusNot(long l) {
        return meetingDao.findByStatusNot(l);
    }

    @Transactional
    public void updateMeetingStatus(Long meetingId, Long status) {
        meetingDao.updateMeetingStatus(meetingId, status);
    }
}
