package org.kbs.archiver.cache;

import org.kbs.archiver.persistence.FrontPageMapper;

/**
 * User: kcn
 * Date: 12-3-21
 * Time: 上午10:56
 */
public class GenerateFrontPage {
    public void setFrontPageMapper(FrontPageMapper frontPageMapper) {
        this.frontPageMapper = frontPageMapper;
    }

    private FrontPageMapper frontPageMapper;


}
