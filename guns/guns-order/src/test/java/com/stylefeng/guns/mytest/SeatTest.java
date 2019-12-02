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
<<<<<<< HEAD
        String[] seatId = {"2","4","7"};
        int fieldId = 7;
=======
        // 座位是否已经售出
        String[] seatId = {"2","4","7"};//放映场次为1，假设我要下单这个座位，如果判断出已经售出就成功
        int fieldId = 1;
>>>>>>> 59eedaa2f74abb91c0752fdf96909e610f52f605
        List<MoocOrderT> fields = moocOrderTMapper.selectList(new EntityWrapper<MoocOrderT>().eq("field_id", fieldId));
        StringBuilder builder = new StringBuilder();
        for (MoocOrderT field : fields) {
            builder.append(field.getSeatsIds()).append(",");
        }
        String soldSeats = builder.toString();
        for (String s : seatId) {
            if(soldSeats.contains(s)){
<<<<<<< HEAD
                System.out.println(1);
            }
        }
        System.out.println(0);
=======
                System.out.println("座位已经售出");
            }
        }
        System.out.println("座位还未售出");
>>>>>>> 59eedaa2f74abb91c0752fdf96909e610f52f605
    }
}
