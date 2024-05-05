package gal.udc.fic.prperez.pleste.service.dao;

import gal.udc.fic.prperez.pleste.service.dao.component.SQLComponentDao;
import gal.udc.fic.prperez.pleste.service.dao.component.SQLFieldDao;
import gal.udc.fic.prperez.pleste.service.dao.template.SQLTemplateDao;
import gal.udc.fic.prperez.pleste.service.dao.template.SQLTemplateFieldDao;
import gal.udc.fic.prperez.pleste.service.dao.users.SQLTokenDao;
import gal.udc.fic.prperez.pleste.service.dao.users.SQLUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SQLDaoFactoryUtil {
	private final SQLTemplateDao sqlTemplateDao;
	private final SQLComponentDao sqlComponentDao;
	private final SQLTemplateFieldDao sqlTemplateFieldDao;
	private final SQLFieldDao sqlFieldDao;
	private final SQLUserDao sqlUserDao;
	private final SQLTokenDao sqlTokenDao;

	public @Autowired SQLDaoFactoryUtil(
			SQLTemplateDao sqlTemplateDao,
			SQLComponentDao sqlComponentDao,
			SQLTemplateFieldDao sqlTemplateFieldDao,
			SQLFieldDao sqlFieldDao,
			SQLUserDao sqlUserDao,
			SQLTokenDao sqlTokenDao) {
		this.sqlTemplateDao = sqlTemplateDao;
		this.sqlComponentDao = sqlComponentDao;
		this.sqlTemplateFieldDao = sqlTemplateFieldDao;
		this.sqlFieldDao = sqlFieldDao;
		this.sqlUserDao = sqlUserDao;
		this.sqlTokenDao = sqlTokenDao;
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

	public SQLUserDao getSqlUserDao() {
		return sqlUserDao;
	}

	public SQLTokenDao getSqlTokenDao() {
		return sqlTokenDao;
	}
}
