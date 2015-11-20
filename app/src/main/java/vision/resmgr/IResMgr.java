package vision.resmgr;

/**
 * 资源管理接口
 * Created by verg on 15/11/18.
 */
public interface IResMgr {

    String RES_TYPE = "res_type";
    String RES_TITLE = "res_title";

    byte PAGE_IMAGE = 0x01;
    byte PAGE_AUDIO = 0x02;
    byte PAGE_VIDEO = 0x04;
    byte PAGE_TEXT = 0x08;
    byte PAGE_APP = 0x10;

}
