/*
 * Copyright (C) 2017 Anwar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Jigs_Desktop_Client.resources;

/**
 *
 * @author Anwar
 */
public class Message{
    private final User to;
    private final User from;
    private final String message;
    public Message(User to, User from, String message){
        this.to=to;
        this.from=from;
        this.message=message;
    }
    public String getMessage(){
        return this.message;
    }
    public String getTo(){
        return this.to.getUsername();
    }
    public String getFrom(){
        return this.from.getUsername();
    }
}
/////////////   END OF SOURCE FILE  //////////////////