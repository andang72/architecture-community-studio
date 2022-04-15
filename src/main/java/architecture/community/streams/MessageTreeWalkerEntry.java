package architecture.community.streams;

public class MessageTreeWalkerEntry {
    
    private long threadId;
    private long messageId;

    protected MessageTreeWalkerEntry(long threadId, long messageId) {
        this.threadId = threadId;
        this.messageId = messageId;
    }

    public long getThreadId() {
        return threadId;
    }

    public long getMessageId() {
        return messageId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (messageId ^ (messageId >>> 32));
        result = prime * result + (int) (threadId ^ (threadId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if( obj instanceof MessageTreeWalkerEntry ){
            MessageTreeWalkerEntry other = (MessageTreeWalkerEntry) obj;
            if (messageId == other.messageId && threadId == other.threadId ){
                return true;
            }
        }    
        return false;
    } 
 
}
