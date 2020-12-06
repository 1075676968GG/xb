package com.example.xb.controller;

import com.example.xb.Service.MeetingService;
import com.example.xb.entity.Meeting;
import com.example.xb.entity.PageResult;
import com.example.xb.entity.Result;
import com.example.xb.util.LoginUserUtil;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liao
 * @date 2020/12/3 12:08
 * @Description
 */
@RestController
@RequestMapping("/meeting")
public class MeetingController {

    @Autowired
    MeetingService meetingService;

    /**
     * 新增会议 * * @param meeting * @return * @throws Exception
     */
    @PostMapping
    public Result save(@RequestBody Meeting meeting) throws Exception {meetingService.save(meeting);
        return new Result(true, "新增成功");
    }

    /**
     * 搜索+分页 (按照会议发布时间倒序排序)
     *
     * @param searchMap
     * @return
     * @throws Exception
     */
    @PostMapping("/search/{page}")
    public Result search(@RequestBody Map searchMap, @PathVariable Integer page){
        Page<Meeting> pageData=meetingService.findPage(searchMap,page);
        PageResult<Meeting> pageResult=new PageResult<>(pageData.getTotalPages()==0?1:pageData.getTotalPages(),pageData.getContent());
        Map returnMap=new HashMap();
        returnMap.put("pageResult",pageResult);
        return new Result(true,"查询成功",returnMap);
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id){
        //会议详情
        Meeting meeting=meetingService.findById(id);
        //应到人数
        Integer makeUserCount=meeting.getMakeUser().split(",").length;
        //实到人数
        Integer joinCount=meetingService.countMeetingJoinByMeetingId(id);
        //未到人数
        Integer noJoinCount=makeUserCount-joinCount;
        //我是否参加过此会议
        Boolean isJoinMeeting=meetingService.isJoinMeeting(LoginUserUtil.getId(),id);
        Map returnMap=new HashMap();
        returnMap.put("meeting",meeting);
        returnMap.put("makeUserCount", makeUserCount);
        returnMap.put("joinCount", joinCount);
        returnMap.put("noJoinCount", noJoinCount);
        returnMap.put("isJoinMeeting", isJoinMeeting);

        return new Result(true, "查询成功", returnMap);
    }

    @PostMapping("/joinMeeting/{id}")
    public Result joinMeeting(@PathVariable Long id){
        Meeting meeting=meetingService.findById(id);
        if(meeting.getStatus().longValue()==1L){
            return new Result(false,"会议正在进行中");
        }if(meeting.getStatus().longValue()==2L){
            return new Result(false,"该会议已结束");
        }
        // 数组转集合
        List<String> makeUser= Arrays.asList(meeting.getMakeUser().split(","));
        // 如果当初发布会议的时候没有抄送给你,那么不能参加此次会议
        if(!makeUser.contains(LoginUserUtil.getId().toString())){
            return new Result(false, "您不能操作此次会议");
        }
        //查询我是否参加过此次会议
        Boolean joinMeeting=meetingService.isJoinMeeting(LoginUserUtil.getId(),id);
        if(joinMeeting){
            meetingService.unJoinMeeting(LoginUserUtil.getId(),id);
            return new Result(false,"取消会议成功",200);
        }
        meetingService.joinMeeting(LoginUserUtil.getId(),id);
        return new Result(true,"参加成功");
    }

}
