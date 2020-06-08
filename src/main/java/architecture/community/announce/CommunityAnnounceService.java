package architecture.community.announce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import architecture.community.announce.dao.AnnounceDao;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.community.user.UserNotFoundException;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class CommunityAnnounceService implements AnnounceService {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Inject
	@Qualifier("announceDao")
	private AnnounceDao announceDao;
    
	@Inject
	@Qualifier("announceCache")
	private Cache announceCache;
    
	@Inject
	@Qualifier("userManager")
	private UserManager userManager;
    
	public CommunityAnnounceService() { 
		
	}
 

	public Announce createAnnounce(User user) {
		// TODO 자동 생성된 메소드 스텁
		Announce impl = new Announce(-1L, 0, -1L, user);
		return impl;
	}

	public Announce createAnnounce(User user, int objectType, long objectId) {
		Announce impl = new Announce(-1L, objectType, objectId, user);
		return impl;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addAnnounce(Announce announce) {
		Long announceId = announce.getAnnounceId();
		if (announceId < 0)
			announceId = announceDao.getNextAnnounceId();
		announce.setAnnounceId(announceId);
		announceDao.insert(announce);
		updateCache(announce);
		// fire event;
	}

	private void updateCache(Announce announce) {
		announceCache.put(new Element(announce.getAnnounceId(), announce));
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateAnnounce(Announce announce) {
		Date now = new Date();
		announce.setModifiedDate(now);

		announceDao.update(announce);
		updateCache(announce);
	}

	public Announce getAnnounce(long announceId) throws AnnounceNotFoundException {

		Announce announce = null;
		if (announceCache.get(announceId) != null) {
			announce = (Announce) announceCache.get(announceId).getValue();
		}
		if (announce == null) {
			announce = announceDao.load(announceId);
			User user;
			try {
				log.debug("find user by {}", announce.getUser().getUserId() );
				user = userManager.getUser(announce.getUser().getUserId());
				log.debug("found user : {}", user );
				announce.setUser(user);
			} catch (UserNotFoundException e) {}
			updateCache(announce);
		}
		return announce;
	}

	/**
	 * 공지 종료일이 없는 경우 공지 시작일이 현재일 이전이거나 같은 경우 ..
	 * 
	 */
	public List<Announce> getAnnounces(int objectType, long objectId) {

		List<Long> announceIds = announceDao.getAnnounceIds(objectType, objectId);

		List<Announce> list = new ArrayList<Announce>();
		Date now = new Date();
		long startDate = now.getTime();
		long endDate = now.getTime();

		for (Long announceId : announceIds) {
			try {
				Announce announce = getAnnounce(announceId);
				if (announce.getEndDate() == null) {
					if (announce.getStartDate().getTime() <= startDate) {
						list.add(announce);
					}
				} else if (announce.getEndDate().getTime() >= endDate
						&& announce.getStartDate().getTime() <= startDate) {
					list.add(announce);
				}
			} catch (AnnounceNotFoundException e) {
				log.warn(e.getMessage(), e);
			}
		}
		return list;
	}

	public List<Announce> getAnnounces(int objectType, long objectId, Date startDate, Date endDate) {
		List<Long> announceIds = announceDao.getAnnounceIds(objectType, objectId);
		if (announceIds.size() == 0)
			return Collections.EMPTY_LIST;
		if (startDate == null)
			startDate = new Date(0x8000000000000000L);
		if (endDate == null)
			endDate = new Date(0x7fffffffffffffffL);

		List<Announce> results = filterAnnounces(startDate, endDate, announceIds);
		return results;
	}

	private List<Announce> filterAnnounces(Date startDate, Date endDate, List<Long> announceIds) {
		List<Announce> list = new ArrayList<Announce>();
		for (Long announceId : announceIds) {
			try {
				Announce announce = getAnnounce(announceId);
				log.debug("diff start date : " + announce.getStartDate() + " / " + startDate);
				log.debug("diff end date : " + announce.getEndDate() + " / " + endDate);
				if (announce.getEndDate() == null) {
					if (announce.getStartDate().getTime() <= startDate.getTime())
						list.add(announce);

				} else if (announce.getEndDate().getTime() >= endDate.getTime()
						&& announce.getStartDate().getTime() <= startDate.getTime()) {
					list.add(announce);
				}
			} catch (AnnounceNotFoundException e) {
				log.warn(e.getMessage(), e);
			}
		}
		return list;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAnnounce(long announceId) {
		try {
			Announce announce = getAnnounce(announceId);
			announceDao.delete(announce);
			// fire event;
			announceCache.remove(announce.getAnnounceId());
		} catch (AnnounceNotFoundException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteUserAnnounces(User user) {

		List<Long> ids = announceDao.getAnnounceIdsForUser(user.getUserId());
		for (Long id : ids) {
			deleteAnnounce(id);
		}
	}

	public void moveAnnounces(int fromObjectType, int toObjectType) {
		// TODO 자동 생성된 메소드 스텁

	}

	public void moveAnnounces(int fromObjectType, long fromObjectId, int toObjectType, long toObjectId) {
		// TODO 자동 생성된 메소드 스텁
	}

	public int countAnnounce(int objectType, long objectId) {
		return announceDao.getAnnounceCount(objectType, objectId);
	}

	public int getAnnounceCount(int objectType, long objectId, Date endDate) {
		return announceDao.getAnnounceCount(objectType, objectId, endDate);
	}

	public int getAnnounceCount(int objectType, long objectId, Date startDate, Date endDate) {
		return announceDao.getAnnounceCount(objectType, objectId, startDate, endDate);
	}
}
