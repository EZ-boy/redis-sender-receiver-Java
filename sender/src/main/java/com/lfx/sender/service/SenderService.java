package com.lfx.sender.service;

import com.lfx.sender.util.QueueSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//开启定时器功能
@EnableScheduling
@Component
public class SenderService {
    private static final Logger log = LoggerFactory.getLogger(SenderService.class);

    //每次查询最大数据量
    private static final int rowNum = 20000;
    private static final String queueName = "zpQueue";

    @Autowired
    private QueueSender queueSender;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedDelay = 5000)
    public void sendMessageJlx() {
        //查询待处理数据
        String sql=" ";
        List<String> ids = jdbcTemplate.query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return String.valueOf(resultSet.getLong("ID"));
            }
        },rowNum);
        //将查询数据存到消息队列
        log.info("插入消息队列数据量："+ids.size());
        if (ids.size() > 0) {
            queueSender.sendMsg(queueName,ids);
            //消息队列插入完成后更新数据状态为待处理
            batchUpdateJlx1(ids);
        }
    }

    public void batchUpdateJlx1(List<String> ids){
        String sql="update tablename set status = ? where id = ? ";
        List<Object[]> list = new ArrayList<>();
        int [] types = new int[]{};
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            Object[] param = new Object[]{"3",id};
            list.add(param);
        }
        jdbcTemplate.batchUpdate(sql,list,types);
    }

}