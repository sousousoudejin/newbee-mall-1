package ltd.newbee.mall.controller;

import ltd.newbee.mall.entity.JdbcTem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jdbc")
public class JdbcTemplateController {

    //注入jdbcTemplate
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //新增一行
    @PostMapping("/addJdbc")
    public String createJdbcTem(JdbcTem jdbcTem) {
        if (!StringUtils.hasText(jdbcTem.getDs_type()) || !StringUtils.hasText(jdbcTem.getDs_name())) {
            return "类型或名称为空";
        }
        jdbcTemplate.execute(" insert into jdbc_test(ds_type,ds_name) values(\""+ jdbcTem.getDs_type() +"\", \""+jdbcTem.getDs_name()+"\")");
        return "新增完成";
    }

    //删除一行
    @GetMapping("/delJdbc")
    public String deleteJdbcTem(Integer dsId) {
        if (dsId < 0) {
            return "输入的数值有误";
        }
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(" select * from jdbc_test where ds_id =  " + dsId);
        if (CollectionUtils.isEmpty(maps)) {
            return "数据不存在";
        }
        jdbcTemplate.execute(" delete from jdbc_test where ds_id = "+dsId+" ");
        return "删除成功";
    }

    //修改一行
    @PostMapping("/updJdbc")
    public String updateJdbcTem(JdbcTem jdbcTem) {
        if (jdbcTem.getDs_id() < 0  || !StringUtils.hasText(jdbcTem.getDs_name())) {
            return "请输入修改内容";
        }
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(" select * from jdbc_test where ds_id =  " + jdbcTem.getDs_id());
        if (CollectionUtils.isEmpty(maps)) {
            return "要修改的内容不存在";
        }
        int update = jdbcTemplate.update(" update jdbc_test set ds_name = \""+jdbcTem.getDs_name()+"\" where ds_id = "+jdbcTem.getDs_id()+" ");
        return update > 0 ? "修改成功" : "修改失败";
    }

    //查询所有
    @GetMapping("/selJdbc")
    public List<JdbcTem> selectJdbcTem() {
        List<JdbcTem> jts = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(" select * from jdbc_test ");
        while (sqlRowSet.next()) {
            JdbcTem jt = new JdbcTem();
            jt.setDs_id(sqlRowSet.getInt(1));
            jt.setDs_type(sqlRowSet.getString(2));
            jt.setDs_name(sqlRowSet.getString(3));
            jts.add(jt);
        }
        return jts;
    }

    /**
     * 复现问题 ArrayList每次存入的都是一个对象的内存地址
     */
    /*@GetMapping("/selJdbc")
    public List<JdbcTem> selectJdbcTem() {
        List<JdbcTem> jts = new ArrayList<>();
        JdbcTem jt = new JdbcTem();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(" select * from jdbc_test ");
        while (sqlRowSet.next()) {
            jt.setDs_id(sqlRowSet.getInt(1));
            jt.setDs_type(sqlRowSet.getString(2));
            jt.setDs_name(sqlRowSet.getString(3));
            jts.add(jt);
        }
        return jts;
    }*/
}
