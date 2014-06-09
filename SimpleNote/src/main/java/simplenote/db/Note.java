package simplenote.db;

/**
 * Created by Costi on 6/1/2014.
 */
public class Note {

    public Note(Integer aId, String aNote)
    {
        mId = aId;
        mNote = aNote;
    }

    @Override
    public String toString()
    {
        return mNote;
    }

    public Integer getId()
    {
        return  mId;
    }

    private Integer mId;
    private String mNote;
}
