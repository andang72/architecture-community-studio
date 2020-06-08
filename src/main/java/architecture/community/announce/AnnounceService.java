package architecture.community.announce;

import java.util.Date;
import java.util.List;

import architecture.community.user.User;

public interface AnnounceService {
	
	public abstract Announce createAnnounce(User user);

    public abstract Announce createAnnounce(User user, int objectType, long objectId);

    public abstract void addAnnounce(Announce announce);

    public abstract void updateAnnounce(Announce announce);

    public abstract Announce getAnnounce(long announceId) throws AnnounceNotFoundException;

    public abstract List<Announce> getAnnounces(int objectType, long objectId);

    public abstract List<Announce> getAnnounces(int objectType, long objectId, Date startDate, Date endDate);

    public abstract void deleteAnnounce(long announceId);

    public abstract void deleteUserAnnounces(User user);

    public abstract void moveAnnounces(int fromObjectType, int toObjectType);

    public abstract void moveAnnounces(int fromObjectType, long fromObjectId, int toObjectType, long toObjectId);

    public abstract int countAnnounce(int objectType, long objectId);

    public abstract int getAnnounceCount(int objectType, long objectId, Date endDate);

    public abstract int getAnnounceCount(int objectType, long objectId, Date startDate, Date endDate);
}
