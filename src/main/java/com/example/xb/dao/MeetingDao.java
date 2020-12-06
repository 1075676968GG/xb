package com.example.xb.dao;

import com.example.xb.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface MeetingDao extends JpaRepository<Meeting,Long>, JpaSpecificationExecutor<Meeting> {
    @Query("select count(1) from  MeetingJoin mj where mj.mId=?1")
    Integer countMeetingJoinByMeetingId(Long id);

    @Query("select count(1) from MeetingJoin mj where mj.uId=?1 and mj.mId=?2")
    Integer countMeetingJoinByUserIdAndMeetingId(Long userId, Long meetingId);

    @Query("delete from MeetingJoin mj where mj.uId=?1 and mj.mId=?2")
    @Modifying
    void deleteMeeting(Long userId, Long meetingId);


    @Query(value = "insert into meeting_join values(?1,?2)",nativeQuery = true)
    @Modifying
    void insertMeeting(Long userId, Long meetingId);

    @Query("update Meeting  m set m.status=?2 where m.id=?1")
    @Modifying
    void updateMeetingStatus(Long meetingId, Long status);


    List<Meeting> findByStatusNot(long l);
}
