package gal.udc.fic.prperez.pleste.service.dao;

import gal.udc.fic.prperez.pleste.service.dao.component.SQLComponentDao;
import gal.udc.fic.prperez.pleste.service.dao.component.SQLFieldDao;
import gal.udc.fic.prperez.pleste.service.dao.template.SQLTemplateDao;
import gal.udc.fic.prperez.pleste.service.dao.template.SQLTemplateFieldDao;
import gal.udc.fic.prperez.pleste.service.dao.timer.SQLTimerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SQLDaoFactoryUtil {
	private final SQLTemplateDao sqlTemplateDao;
	private final SQLComponentDao sqlComponentDao;
	private final SQLTimerDao sqlTimerDao;
	private final SQLTemplateFieldDao sqlTemplateFieldDao;
	private final SQLFieldDao sqlFieldDao;

	public @Autowired SQLDaoFactoryUtil(
			SQLTemplateDao sqlTemplateDao,
			SQLComponentDao sqlComponentDao,
			SQLTimerDao sqlTimerDao,
			SQLTemplateFieldDao sqlTemplateFieldDao,
			SQLFieldDao sqlFieldDao) {
		this.sqlTemplateDao = sqlTemplateDao;
		this.sqlComponentDao = sqlComponentDao;
		this.sqlTimerDao = sqlTimerDao;
		this.sqlTemplateFieldDao = sqlTemplateFieldDao;
		this.sqlFieldDao = sqlFieldDao;
	}

	public SQLTimerDao getSqlTimerDao() {
		return sqlTimerDao;
	}

	public SQLComponentDao getSqlComponentDao() {
		return sqlComponentDao;
	}

	public SQLTemplateDao getSqlTemplateDao() {
		return sqlTemplateDao;
	}

	public SQLTemplateFieldDao getSqlTemplateFieldDao() {
		return sqlTemplateFieldDao;
	}

	public SQLFieldDao getSqlFieldDao() {
		return sqlFieldDao;
	}
}
