package com.stylefeng.guns.mytest;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.rest.GunsOrderApplication;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GunsOrderApplication.class)
public class SeatTest {

    @Autowired
    MoocOrderTMapper moocOrderTMapper;

    @Test
    public void test1(){
        String[] seatId = {"2","4","7"};
        int fieldId = 7;
        List<MoocOrderT> fields = moocOrderTMapper.selectList(new EntityWrapper<MoocOrderT>().eq("field_id", fieldId));
        StringBuilder builder = new StringBuilder();
        for (MoocOrderT field : fields) {
            builder.append(field.getSeatsIds()).append(",");
        }
        String soldSeats = builder.toString();
        for (String s : seatId) {
            if(soldSeats.contains(s)){
                System.out.println(1);
            }
        }
        System.out.println(0);
    }
}
