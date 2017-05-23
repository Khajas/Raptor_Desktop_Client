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
package Jigs_Desktop_Client.GUI;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Anwar
 */
public class UserStatus {
    private final String username;
    private Circle status_circle;
    private String status;
    public UserStatus(String username, String status){
        this.username=username;
        this.status=status;
        this.status_circle=new Circle();
        this.status_circle.setFill(Color.RED);
    }
    public String getUsername(){
        return this.username;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public String getStatus(){
        return this.status;
    }
    public Circle getStatusCircle(){
        return this.status_circle;
    }
    @Override
    public String toString(){
        return this.status_circle+this.username;
    }
}
