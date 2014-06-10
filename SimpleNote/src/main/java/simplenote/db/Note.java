/**
 * (C) Copyright 2014 Costi Rada.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License
 * (GPL) version 3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/copyleft/gpl.html
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * Contributors:
 */

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
