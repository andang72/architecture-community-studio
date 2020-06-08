package architecture.community.announce.dao;

import java.util.Date;
import java.util.List;

import architecture.community.announce.Announce;
import architecture.community.announce.AnnounceNotFoundException;

public interface AnnounceDao {

    public abstract Announce load(long announceId) throws AnnounceNotFoundException;

    public abstract long getNextAnnounceId();

    public abstract void update(Announce annoucne);

    public abstract void insert(Announce annoucne);

    public abstract void delete(Announce annoucne);

    public abstract void move(Long fromId, Long toId);

    public abstract List<Long> getAnnounceIdsForUser(long userId);

    public abstract List<Long> getAnnounceIds(int objectType, long objectId);

    public abstract List<Long> getAnnounceIds();

    public abstract int getAnnounceCount(int objectType, long objectId);

    public abstract int getAnnounceCount(int objectType, long objectId, Date endDate);

    public abstract int getAnnounceCount(int objectType, long objectId, Date startDate, Date endDate);
}
