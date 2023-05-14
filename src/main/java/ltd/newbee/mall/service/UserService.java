package ltd.newbee.mall.service;

import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

public interface UserService {
    PageResult getUserPage(PageQueryUtil pageQueryUtil);
}
