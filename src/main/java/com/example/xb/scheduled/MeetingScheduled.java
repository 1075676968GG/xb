package com.example.xb.scheduled;

import com.example.xb.Service.MeetingService;
import com.example.xb.entity.Meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author liao
 * @date 2020/12/3 19:52
 * @Description
 */
@Component
public class MeetingScheduled {
    @Autowired
    private MeetingService meetingService;

    @Scheduled(cron = "1 * * * * ?")
    public void meetingJob(){
        List<Meeting> meetingList=meetingService.findByStatusNot(2L);
        for (Meeting meeting:meetingList){
            Date startTime = meeting.getStartTime();
            Date endTime = meeting.getEndTime();
            if(new Date().getTime()>startTime.getTime()&&meeting.getStatus().longValue()==0L){
                meetingService.updateMeetingStatus(meeting.getId(),1L);
            }
            if(new Date().getTime()>endTime.getTime()&&meeting.getStatus().longValue()==1L){
                meetingService.updateMeetingStatus(meeting.getId(),2L);
            }
        }

    }
}
