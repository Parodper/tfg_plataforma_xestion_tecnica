package gal.udc.fic.prperez.pleste.service.dao;

import gal.udc.fic.prperez.pleste.service.dao.component.SQLComponentDao;
import gal.udc.fic.prperez.pleste.service.dao.component.SQLFieldDao;
import gal.udc.fic.prperez.pleste.service.dao.template.SQLTemplateDao;
import gal.udc.fic.prperez.pleste.service.dao.template.SQLTemplateFieldDao;
import gal.udc.fic.prperez.pleste.service.dao.users.SQLPasswordDao;
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
	private final SQLPasswordDao sqlPasswordDao;
	private final SQLTokenDao sqlTokenDao;

	public @Autowired SQLDaoFactoryUtil(
			SQLTemplateDao sqlTemplateDao,
			SQLComponentDao sqlComponentDao,
			SQLTemplateFieldDao sqlTemplateFieldDao,
			SQLFieldDao sqlFieldDao,
			SQLUserDao sqlUserDao,
			SQLPasswordDao sqlPasswordDao,
			SQLTokenDao sqlTokenDao) {
		this.sqlTemplateDao = sqlTemplateDao;
		this.sqlComponentDao = sqlComponentDao;
		this.sqlTemplateFieldDao = sqlTemplateFieldDao;
		this.sqlFieldDao = sqlFieldDao;
		this.sqlUserDao = sqlUserDao;
		this.sqlPasswordDao = sqlPasswordDao;
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

	public SQLPasswordDao getSqlPasswordDao() {
		return sqlPasswordDao;
	}

	public SQLTokenDao getSqlTokenDao() {
		return sqlTokenDao;
	}
}
