package ltd.newbee.mall.service;

import ltd.newbee.mall.controller.vo.NewBeeMallIndexConfigGoodsVO;

import java.util.List;

public interface NewBeeMallIndexConfigService {


    List<NewBeeMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int type, int indexGoodsHotNumber);
}
